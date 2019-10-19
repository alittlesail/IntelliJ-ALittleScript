package plugin.link;

import aapt.pb.repackage.com.google.protobuf.MapEntry;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
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
    private static Map<WatchKey, Set<Module>> mKeyMap = new HashMap<>();
    private static WatchService mWatchService;
    private static Thread mThread;

    public static void setWatch(Module module, String csvPath) {
        Setup();

        for (Map.Entry<WatchKey, Set<Module>> entry : mKeyMap.entrySet()) {
            if (entry.getValue().contains(module)) {
                entry.getValue().remove(module);
                if (entry.getValue().isEmpty()) {
                    entry.getKey().cancel();
                    mKeyMap.remove(entry.getKey());
                }
                break;
            }
        }
        if (csvPath.isEmpty()) return;
        Path path = Paths.get(csvPath);
        try {
            WatchKey key = path.register(mWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            Set<Module> set = mKeyMap.get(key);
            if (set == null) {
                set = new HashSet<>();
                mKeyMap.put(key, set);
            }
            set.add(module);
        } catch (IOException e) {
        }
    }

    public static void Setup() {
        if (mWatchService != null) return;
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
                                            handleChangeForCsv(watchKey, event.context().toString(), ALittleLinkData.ChangeType.CT_CHANGED);
                                        } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                            handleChangeForCsv(watchKey, event.context().toString(), ALittleLinkData.ChangeType.CT_DELETED);
                                        } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                            handleChangeForCsv(watchKey, event.context().toString(), ALittleLinkData.ChangeType.CT_CREATED);
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

    private static void handleChangeForCsv(WatchKey key, String relPath, ALittleLinkData.ChangeType changeType) {
        Set<Module> moduleSet = mKeyMap.get(key);
        if (moduleSet == null || moduleSet.isEmpty()) return;
        Project project = null;
        Module module = null;
        for (Module m : moduleSet) {
            module = m;
            project = m.getProject();
            break;
        }
        String path = ALittleLinkConfig.getConfig(module).getCsvPathWithEnd() + relPath;

        if (changeType == ALittleLinkData.ChangeType.CT_CREATED) {
            ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(project);
            if (listener == null) return;
            HashSet<ALittleStructDec> set = listener.getCsvData(relPath);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!moduleSet.contains(m)) continue;
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

        if (changeType == ALittleLinkData.ChangeType.CT_DELETED) {
            mDataMap.remove(path);
            HashSet<ALittleStructDec> set = listener.getCsvData(relPath);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!moduleSet.equals(m)) continue;

                WriteCommandAction.writeCommandAction(csvData.getProject()).run(() -> {
                    handleChangeForStruct(dec, new ArrayList<>());
                });
            }
            return;
        }

        if (changeType == ALittleLinkData.ChangeType.CT_CHANGED) {
            csvData.load();
            List<String> varList = csvData.generateVarList();
            HashSet<ALittleStructDec> set = listener.getCsvData(relPath);
            if (set == null) return;
            set = new HashSet<>(set);
            for (ALittleStructDec dec : set) {
                FileIndexFacade facade = FileIndexFacade.getInstance(dec.getProject());
                VirtualFile file = dec.getContainingFile().getVirtualFile();
                if (file == null) continue;
                Module m = facade.getModuleForFile(file);
                if (!module.equals(m)) continue;

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
        VirtualFile virtualFile = structDec.getContainingFile().getOriginalFile().getVirtualFile();
        if (virtualFile == null) return null;
        FileIndexFacade facade = FileIndexFacade.getInstance(structDec.getProject());
        Module module = facade.getModuleForFile(virtualFile);
        if (module == null) return null;
        String csvPath = ALittleLinkConfig.getConfig(module).getCsvPathWithEnd();
        if (csvPath.isEmpty()) {
            throw new ALittleGuessException(csvModifier, "所在模块没有设置Csv目录,无法解析");
        }
        PsiElement pathElement = csvModifier.getStringContent();
        if (pathElement == null)
            throw new ALittleGuessException(csvModifier, "Csv注解的格式错误, 格式为 @Csv \"相对路径\"");

        String path = pathElement.getText();
        path = csvPath + path.substring(1, path.length() - 1);

        ALittleCsvData csvData = mDataMap.get(path);
        if (csvData == null) {
            csvData = new ALittleCsvData(structDec.getProject(), path);
            String error = csvData.load();
            if (error != null) throw new ALittleGuessException(pathElement, error);
            mDataMap.put(path, csvData);
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
        WriteCommandAction.writeCommandAction(structDec.getProject()).run(() -> {
            ALittleCsvData csvData = ALittleCsvDataManager.checkForStruct(structDec);
            if (csvData == null) return;

            List<String> varList = csvData.generateVarList();
            ALittleMysqlDataManager.handleChangeForStruct(structDec, varList);
        });
    }
}
