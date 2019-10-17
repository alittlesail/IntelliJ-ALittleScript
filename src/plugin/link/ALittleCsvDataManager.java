package plugin.link;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.filetype.ALittleFileType;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ALittleCsvDataManager {
    private static Map<String, ALittleCsvData> mDataMap = new HashMap<>();
    private static Map<WatchKey, KeyInfo> mKeyMap = new HashMap<>();
    private static WatchService mWatchService;
    private static Thread mThread;

    public static class KeyInfo
    {
        String moduleName;
        Project project;
    }

    public enum ChangeType
    {
        CT_CREATED,
        CT_DELETED,
        CT_CHANGED,
    }

    public static void Setup() {
        try {
            mWatchService = FileSystems.getDefault().newWatchService();

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            WatchKey watchKey = mWatchService.take();
                            for (WatchEvent event : watchKey.pollEvents()) {
                                ApplicationManager.getApplication().invokeLater(new Runnable() {
                                    public void run() {
                                        if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                            handleChangeForCsv(watchKey, event.context().toString(), ChangeType.CT_CHANGED);
                                        } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                            handleChangeForCsv(watchKey, event.context().toString(), ChangeType.CT_DELETED);
                                        } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                            handleChangeForCsv(watchKey, event.context().toString(), ChangeType.CT_CREATED);
                                        }
                                    }
                                });
                            }
                            watchKey.reset();
                        } catch (InterruptedException | ClosedWatchServiceException ignored) {
                            break;
                        }
                    }
                }
            });
            mThread.start();
        } catch (UnsupportedOperationException | IOException ignored) {

        }
    }

    public static void Shutdown() {
        if (mWatchService != null) {
            try {
                mWatchService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mWatchService = null;
        }
        if (mThread != null) {
            try {
                mThread.join();
            } catch (InterruptedException ignored) {
            }
            mThread = null;
        }
    }

    private static void handleChangeForCsv(WatchKey key, String relPath, ChangeType changeType) {
        KeyInfo keyInfo = mKeyMap.get(key);
        if (keyInfo == null) return;
        String path = keyInfo.moduleName + ":/" + relPath;

        if (changeType == ChangeType.CT_CREATED) {
            ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(keyInfo.project);
            if (listener == null) return;
            HashSet<ALittleStructDec> set = listener.getCsvData(path);
            if (set == null) return;
            for (ALittleStructDec dec : set) {
                try {
                    checkAndChangeForStruct(dec);
                } catch (ALittleGuessException ignored) {

                }
            }
            return;
        }

        ALittleCsvData csvData = mDataMap.get(path);
        if (csvData == null) return;
        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(csvData.getProject());
        if (listener == null) return;

        if (changeType == ChangeType.CT_DELETED) {
            mDataMap.remove(path);
            HashSet<ALittleStructDec> set = listener.getCsvData(path);
            if (set == null) return;
            for (ALittleStructDec dec : set) {
                WriteCommandAction.writeCommandAction(csvData.getProject()).run(() -> {
                    handleChangeForStruct(dec, new ArrayList<>());
                });
            }
            return;
        }

        if (changeType == ChangeType.CT_CHANGED) {
            csvData.load();
            List<String> varList = csvData.generateVarList();
            HashSet<ALittleStructDec> set = listener.getCsvData(path);
            if (set == null) return;
            for (ALittleStructDec dec : set) {
                WriteCommandAction.writeCommandAction(csvData.getProject()).run(() -> {
                    handleChangeForStruct(dec, varList);
                });
            }
            return;
        }
    }

    // 对struct只执行检查
    public static ALittleCsvData checkForStruct(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleCsvModifier csvModifier = structDec.getCsvModifier();
        if (csvModifier == null) return null;
        PsiElement pathElement = csvModifier.getStringContent();
        if (pathElement == null)
            throw new ALittleGuessException(csvModifier, "Csv注解的格式错误,比如A模块的src目录下有B.csv文件，那么写成 @Csv \"A:/B.csv\"");
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);

        ALittleCsvData csvData = mDataMap.get(path);
        if (csvData == null) {
            String[] split = path.split(":");
            if (split.length != 2) {
                throw new ALittleGuessException(csvModifier, "Csv注解的格式错误,比如A模块的src目录下有B.csv文件，那么写成 @Csv \"A:/B.csv\"");
            }
            Module module = ModuleManager.getInstance(structDec.getProject()).findModuleByName(split[0]);
            if (module == null) {
                throw new ALittleGuessException(pathElement, "模块名:" + split[0] + "不存在");
            }

            String error = null;
            String rootPath = null;
            VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
            for (VirtualFile root : roots) {
                ALittleCsvData tmp = new ALittleCsvData(structDec.getProject(), root.getPath() + split[1]);
                error = tmp.load();
                if (error == null) {
                    csvData = tmp;
                    rootPath = root.getPath();
                    break;
                }
            }
            if (error != null) throw new ALittleGuessException(pathElement, error);
            if (csvData == null) return null;

            mDataMap.put(path, csvData);

            Path pathObject = Paths.get(rootPath);
            try {
                WatchKey key = pathObject.register(mWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                KeyInfo keyInfo = new KeyInfo();
                keyInfo.moduleName = split[0];
                keyInfo.project = structDec.getProject();
                mKeyMap.put(key, keyInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 检查是否和csvData一致
        if (csvData.check(structDec.getStructVarDecList())) return csvData;
        return null;
    }

    // 对struct执行变化
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
        String csv = "";
        ALittleCsvModifier csvModifier = dec.getCsvModifier();
        if (csvModifier != null) {
            csv = csvModifier.getText() + "\n";
        }
        // 构造节点
        String content = "namespace ALittle;\n" + csv + "struct " + name + "\n{\n" + String.join("\n", varList) + "\n}";
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

    // 对struct检查，如果有变化就直接执行变化
    public static void checkAndChangeForStruct(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleCsvData csvData = ALittleCsvDataManager.checkForStruct(structDec);
        if (csvData == null) return;

        List<String> varList = csvData.generateVarList();
        Project project = structDec.getProject();
        WriteCommandAction.writeCommandAction(project).run(() -> {
            ALittleCsvDataManager.handleChangeForStruct(structDec, varList);
        });
    }
}
