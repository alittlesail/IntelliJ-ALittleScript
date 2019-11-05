package plugin.link;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.filetype.ALittleFileType;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.nio.file.WatchKey;
import java.sql.*;
import java.util.*;

public class ALittleMysqlDataManager {
    private static Map<String, ALittleMysqlData> mDataMap = new HashMap<>();
    // 主线程收集模块数据
    private static Map<String, Set<Module>> mUrlMap = new HashMap<>();
    private static Timer mTimer;

    // 支线程使用的集合
    private static class TableInfo {
        Connection conn;
        Map<String, Timestamp> timeMap;
        long initTime;
    }
    private static Map<String, TableInfo> mConnMap = new HashMap<>();
    private static Set<String> mUrlSet = new HashSet<>();

    public static void setWatch(Module module, String url) {
        Setup();

        for (Map.Entry<String, Set<Module>> entry : mUrlMap.entrySet()) {
            if (entry.getValue().contains(module)) {
                entry.getValue().remove(module);
                if (entry.getValue().isEmpty()) {
                    mUrlMap.remove(entry.getKey());
                }
                break;
            }
        }
        if (!url.isEmpty()) {
            Set<Module> set = mUrlMap.get(url);
            if (set == null) {
                set = new HashSet<>();
                mUrlMap.put(url, set);
            }
            set.add(module);
        }

        Set<String> urlSet = new HashSet<>(mUrlMap.keySet());

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mUrlSet = urlSet;
                Map<String, TableInfo> newConnMap = new HashMap<>();
                for (Map.Entry<String, TableInfo> entry : mConnMap.entrySet()) {
                    if (mUrlSet.contains(entry.getKey())) {
                        newConnMap.put(entry.getKey(), entry.getValue());
                    } else {
                        try {
                            entry.getValue().conn.close();
                        } catch (Exception ignored) {

                        }
                    }
                }
                mConnMap = newConnMap;

                timerThreadCheckRun();
                this.cancel();
            }
        }, 1, 1);
    }

    private static void timerThreadCheckRun() {
        // long time1 = System.currentTimeMillis();
        for (String url : mUrlSet) {
            try {
                TableInfo info = mConnMap.get(url);
                if (info == null) {
                    info = new TableInfo();
                    info.conn = DriverManager.getConnection(url);
                    info.timeMap = new HashMap<>();
                    info.initTime = 0;
                    mConnMap.put(url, info);
                }

                // 读取表结构
                Statement stmt = info.conn.createStatement();
                Set<String> nameSet = new HashSet<>(info.timeMap.keySet());
                // 字段名，数据类型，注释，key类型
                ResultSet rs = stmt.executeQuery("SELECT `table_schema`, `table_name`, `update_time`,`create_time` FROM `tables`;");
                while (rs.next()) {
                    String dbName = rs.getString(1);
                    String tableName = rs.getString(2);
                    Timestamp lastModified = rs.getTimestamp(3);
                    if (lastModified == null)
                        lastModified = rs.getTimestamp(4);
                    String name = dbName + "." + tableName;
                    nameSet.remove(name);
                    if (info.initTime == 0) {
                        info.timeMap.put(name, lastModified);
                        info.initTime = System.currentTimeMillis();
                    } else {
                        Timestamp oldTime = info.timeMap.get(name);
                        if (oldTime == null) {
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                public void run() {
                                    handleChangeForMysql(url, name, ALittleLinkData.ChangeType.CT_CREATED);
                                }
                            });
                        } else if (!oldTime.equals(lastModified)) {
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                public void run() {
                                    handleChangeForMysql(url, name, ALittleLinkData.ChangeType.CT_CHANGED);
                                }
                            });
                        }
                        info.timeMap.put(name, lastModified);
                    }
                }
                for (String name : nameSet) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            handleChangeForMysql(url, name, ALittleLinkData.ChangeType.CT_DELETED);
                        }
                    });
                }
                rs.close();
                stmt.close();
            } catch (Exception ignored) {
            }
        }
        // System.out.println(System.currentTimeMillis() - time1);
    }

    public static void Setup() {
        if (mTimer != null) return;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ignored) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerThreadCheckRun();
            }
        }, 5*1000, 5*1000);
    }

    public static void Shutdown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        for (TableInfo info : mConnMap.values()) {
            try {
                info.conn.close();
            } catch (SQLException ignored) {

            }
        }
        mConnMap.clear();
    }

    private static void handleChangeForMysql(String url, String name, ALittleLinkData.ChangeType changeType) {
        Set<Module> moduleSet = mUrlMap.get(url);
        if (moduleSet == null || moduleSet.isEmpty()) return;
        Project project = null;
        Module module = null;
        for (Module m : moduleSet) {
            module = m;
            project = m.getProject();
            break;
        }
        String path = url + name;

        if (changeType == ALittleLinkData.ChangeType.CT_CREATED) {
            ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(project);
            if (listener == null) return;
            HashSet<ALittleStructDec> set = listener.getMysqlData(name);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!moduleSet.contains(m)) continue;
                checkAndChangeForStruct(dec);
            }
            return;
        }

        ALittleMysqlData mysqlData = mDataMap.get(path);
        if (mysqlData == null) return;
        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(mysqlData.getProject());
        if (listener == null) return;

        if (changeType == ALittleLinkData.ChangeType.CT_DELETED) {
            mDataMap.remove(path);
            HashSet<ALittleStructDec> set = listener.getMysqlData(name);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                if (dec.getContainingFile() == null) continue;
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!moduleSet.equals(m)) continue;

                WriteCommandAction.writeCommandAction(mysqlData.getProject()).run(() -> {
                    handleChangeForStruct(dec, new ArrayList<>());
                });
            }
            return;
        }

        if (changeType == ALittleLinkData.ChangeType.CT_CHANGED) {
            mysqlData.load();
            List<String> varList = mysqlData.generateVarList();
            HashSet<ALittleStructDec> set = listener.getMysqlData(name);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                if (dec.getContainingFile() == null) continue;
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!module.equals(m)) continue;

                WriteCommandAction.writeCommandAction(mysqlData.getProject()).run(() -> {
                    handleChangeForStruct(dec, varList);
                });
            }
            return;
        }
    }

    // 返回是否需要变化
    public static ALittleMysqlData checkForStruct(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleMysqlModifier mysqlModifier = structDec.getMysqlModifier();
        if (mysqlModifier == null) return null;
        VirtualFile virtualFile = structDec.getContainingFile().getOriginalFile().getVirtualFile();
        if (virtualFile == null) return null;
        FileIndexFacade facade = FileIndexFacade.getInstance(structDec.getProject());
        Module module = facade.getModuleForFile(virtualFile);
        if (module == null) return null;
        String mysqlUrl = ALittleLinkConfig.getConfig(module).getMysqlUrl();
        if (mysqlUrl.isEmpty()) {
            throw new ALittleGuessException(mysqlModifier, "所在模块没有设置Mysql连接信息,无法解析");
        }
        PsiElement pathElement = mysqlModifier.getStringContent();
        if (pathElement == null)
            throw new ALittleGuessException(mysqlModifier, "Mysql注解的格式错误, @Mysql \"数据库名.表名\"");
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);
        boolean result = path.matches("^\\w+\\.\\w+$");
        if (!result) {
            throw new ALittleGuessException(pathElement, "Mysql注解的格式错误, @Mysql \"数据库名.表名\"");
        }

        ALittleMysqlData mysqlData = mDataMap.get(mysqlUrl + path);
        if (mysqlData == null) {
            mysqlData = new ALittleMysqlData(structDec.getProject(), mysqlUrl, path);
            String error = mysqlData.load();
            if (error != null) {
                throw new ALittleGuessException(pathElement, error);
            }
            mDataMap.put(mysqlUrl + path, mysqlData);
        }
        // 检查是否和mysqlData一致
        if (mysqlData.check(structDec.getStructVarDecList())) return mysqlData;
        return null;
    }

    // 处理变化
    public static void handleChangeForStruct(@NotNull ALittleStructDec dec, List<String> varList) {
        ALittleStructNameDec nameDec = dec.getStructNameDec();
        if (nameDec == null) return;
        ASTNode node = dec.getNode();
        if (node == null) return;
        PsiElement parent = dec.getParent();
        if (parent == null) return;
        ASTNode parentNode = parent.getNode();
        if (parentNode == null) return;

        String name = nameDec.getText();
        String mysql = "";
        ALittleMysqlModifier mysqlModifier = dec.getMysqlModifier();
        if (mysqlModifier != null) {
            mysql = mysqlModifier.getText() + "\n";
        }
        // 构造节点
        String content = "namespace ALittle;\n" + mysql + "struct " + name + "\n{\n" + String.join("\n", varList) + "\n}";
        ALittleFile alittleFile = (ALittleFile)PsiFileFactory.getInstance(dec.getProject()).createFileFromText("dummy.alittle", ALittleFileType.INSTANCE, content);
        ALittleNamespaceDec namespaceDec = PsiHelper.getNamespaceDec(alittleFile);
        if (namespaceDec == null) return;
        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return;
        List<ALittleStructDec> structDecList = namespaceDec.getStructDecList();
        if (structDecList.isEmpty()) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(dec.getProject());
        if (listener != null) {
            listener.removeNamespaceName(namespaceNameDec);
        }

        dec.getParent().getNode().replaceChild(dec.getNode(), structDecList.get(0).getNode());

        if (listener != null) {
            listener.addNamespaceName(namespaceNameDec);
        }
    }

    public static void checkAndChangeForStruct(@NotNull ALittleStructDec structDec) {
        WriteCommandAction.writeCommandAction(structDec.getProject()).run(() -> {
            try {
                ALittleMysqlData mysqlData = ALittleMysqlDataManager.checkForStruct(structDec);
                if (mysqlData == null) return;

                List<String> varList = mysqlData.generateVarList();
                ALittleMysqlDataManager.handleChangeForStruct(structDec, varList);
            } catch (ALittleGuessException ignored) {

            }
        });
    }
}
