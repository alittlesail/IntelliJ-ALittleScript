package plugin.link;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.filetype.ALittleFileType;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ALittleMysqlDataManager {
    private static Map<String, ALittleMysqlData> mDataMap = new HashMap<>();
    private static Map<String, ALittleMysqlData> mCheckMap = new HashMap<>();
    private static List<String> mRemoveList = new ArrayList<>();
    private static Timer mTimer;

    private static Map<String, Connection> mConnMap = new HashMap<>();

    public static void Setup() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ignored) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        ALittleMysqlDataManager.checkRun();
                    }
                });
            }
        }, 60*1000, 10*1000);
    }

    public static void Shutdown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        for (Connection conn : mConnMap.values()) {
            try {
                conn.close();
            } catch (SQLException ignored) {

            }
        }
        mConnMap.clear();
    }

    public static Connection getConn(String url) throws SQLException {
        Connection conn = mConnMap.get(url);
        if (conn != null) return conn;
        conn = DriverManager.getConnection(url);
        if (conn == null) return null;
        mConnMap.put(url, conn);
        return conn;
    }

    public static void removeConn(String url) {
        Connection conn = mConnMap.get(url);
        if (conn == null) return;
        mConnMap.remove(url);
        try {
            conn.close();
        } catch (SQLException e) {
        }
    }

    private static void checkRun() {
        // 把检查列表拷贝一份
        if (mCheckMap.size() == 0) {
            for (Map.Entry<String, ALittleMysqlData> entry : mDataMap.entrySet()) {
                mCheckMap.put(entry.getKey(), entry.getValue());
            }
        }

        mRemoveList.clear();

        int count = 1;
        for (Map.Entry<String, ALittleMysqlData> entry : mCheckMap.entrySet()) {
            if (count <= 0) break;
            -- count;
            mRemoveList.add(entry.getKey());

            ALittleMysqlData mysqlData = entry.getValue();
            ALittleMysqlData.ChangeType changeType = mysqlData.isChanged();
            ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(mysqlData.getProject());
            if (listener == null) continue;

            if (changeType == ALittleMysqlData.ChangeType.CT_DELETED) {
                HashSet<ALittleStructDec> set = listener.getMysqlData(entry.getKey());
                if (set != null) {
                    for (ALittleStructDec dec : set) {
                        WriteCommandAction.writeCommandAction(mysqlData.getProject()).run(() -> {
                            changeMysql(dec, new ArrayList<>());
                        });
                    }
                }
                mDataMap.remove(entry.getKey());
            } else if (changeType == ALittleMysqlData.ChangeType.CT_CHANGED) {
                List<String> varList = mysqlData.generateVarList();
                HashSet<ALittleStructDec> set = listener.getMysqlData(entry.getKey());
                if (set != null) {
                    for (ALittleStructDec dec : set) {
                        WriteCommandAction.writeCommandAction(mysqlData.getProject()).run(() -> {
                            changeMysql(dec, varList);
                        });
                    }
                }
            }
        }

        for (String path : mRemoveList) {
            mCheckMap.remove(path);
        }
        mRemoveList.clear();
    }

    // 返回是否需要变化
    public static ALittleMysqlData checkMysql(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleMysqlModifier mysqlModifier = structDec.getMysqlModifier();
        if (mysqlModifier == null) return null;
        PsiElement pathElement = mysqlModifier.getStringContent();
        if (pathElement == null)
            throw new ALittleGuessException(mysqlModifier, "Mysql注解的格式错误, @Mysql \"IP:端口/数据库?user=账号名&password=密码\"");
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);
        boolean result = path.matches("^(localhost|\\d+\\.\\d+\\.\\d+\\.\\d+)(:\\d+)?/\\w+\\.\\w+\\?user=\\w+&password=\\w+$");
        if (!result) {
            throw new ALittleGuessException(pathElement, "Mysql注解的格式错误, @Mysql \"IP:端口/数据库?user=账号名&password=密码\"");
        }

        ALittleMysqlData mysqlData = mDataMap.get(path);
        if (mysqlData == null) {
            mysqlData = new ALittleMysqlData(structDec.getProject(), path);
            String error = mysqlData.load();
            if (error != null) {
                throw new ALittleGuessException(pathElement, error);
            }
            mDataMap.put(path, mysqlData);
        }
        // 检查是否和mysqlData一致
        if (mysqlData.check(structDec.getStructVarDecList())) return mysqlData;
        return null;
    }

    // 处理变化
    public static void changeMysql(@NotNull ALittleStructDec dec, List<String> varList) {
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

        parentNode.replaceChild(node, structDecList.get(0).getNode());

        if (listener != null) {
            listener.addNamespaceName(namespaceNameDec);
        }
    }

    public static void checkAndChange(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleMysqlData mysqlData = ALittleMysqlDataManager.checkMysql(structDec);
        if (mysqlData == null) return;

        List<String> varList = mysqlData.generateVarList();
        Project project = structDec.getProject();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    ALittleMysqlDataManager.changeMysql(structDec, varList);
                });
            }
        });
    }
}
