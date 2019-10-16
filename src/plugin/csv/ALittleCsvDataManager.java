package plugin.csv;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.filetype.ALittleFileType;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.*;

public class ALittleCsvDataManager {
    private static Map<String, ALittleCsvData> mDataMap = new HashMap<>();
    private static Map<String, ALittleCsvData> mCheckMap = new HashMap<>();
    private static List<String> mRemoveList = new ArrayList<>();
    private static Timer mTimer;

    public static void Setup() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        ALittleCsvDataManager.checkRun();
                    }
                });
            }
        }, 5*1000, 5*1000);
    }

    public static void Shutdown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private static void checkRun() {
        // 把检查列表拷贝一份
        if (mCheckMap.size() == 0) {
            for (Map.Entry<String, ALittleCsvData> entry : mDataMap.entrySet()) {
                mCheckMap.put(entry.getKey(), entry.getValue());
            }
        }

        mRemoveList.clear();

        int count = 20;
        for (Map.Entry<String, ALittleCsvData> entry : mCheckMap.entrySet()) {
            if (count <= 0) break;
            -- count;
            mRemoveList.add(entry.getKey());

            ALittleCsvData csvData = entry.getValue();
            ALittleCsvData.ChangeType changeType = csvData.isChanged();
            ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(csvData.getProject());
            if (listener == null) continue;

            if (changeType == ALittleCsvData.ChangeType.CT_DELETED) {
                HashSet<ALittleStructDec> set = listener.getCsvData(entry.getKey());
                if (set != null) {
                    for (ALittleStructDec dec : set) {
                        WriteCommandAction.writeCommandAction(csvData.getProject()).run(() -> {
                            changeCsv(dec, new ArrayList<>());
                        });
                    }
                }
                mDataMap.remove(entry.getKey());
            } else if (changeType == ALittleCsvData.ChangeType.CT_CHANGED) {
                List<String> varList = csvData.generateVarList();
                HashSet<ALittleStructDec> set = listener.getCsvData(entry.getKey());
                if (set != null) {
                    for (ALittleStructDec dec : set) {
                        WriteCommandAction.writeCommandAction(csvData.getProject()).run(() -> {
                            changeCsv(dec, varList);
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

    public static void csvChanged(PsiFile file) {
        if (file == null) return;
        String ext = file.getVirtualFile().getExtension();
        if (ext == null) return;
        if (!ext.toUpperCase().equals("CSV")) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(file.getProject());
        if (listener == null) return;

        Module module = ProjectRootManager.getInstance(file.getProject()).getFileIndex().getModuleForFile(file.getVirtualFile());
        if (module == null) return;
        String path = module.getName();

        String filePath = file.getVirtualFile().getPath();

        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root : roots) {
            String rootPath = root.getPath();
            if (!filePath.startsWith(rootPath)) return;
            path += ":" + filePath.substring(rootPath.length());
            break;
        }

        HashSet<ALittleStructDec> set = listener.getCsvData(path);
        if (set == null) return;

        ALittleCsvData csvData = new ALittleCsvData(file.getProject(), file.getVirtualFile().getPath());
        csvData.load();
        List<String> varList = csvData.generateVarList();

        for (ALittleStructDec dec : set) {
            changeCsv(dec, varList);
        }
    }

    // 返回是否需要变化
    public static ALittleCsvData checkCsv(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
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
            VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
            for (VirtualFile root : roots) {
                ALittleCsvData tmp = new ALittleCsvData(structDec.getProject(), root.getPath() + split[1]);
                error = tmp.load();
                if (error == null) {
                    csvData = tmp;
                    break;
                }
            }
            if (error != null) throw new ALittleGuessException(pathElement, error);
            if (csvData == null) return null;

            mDataMap.put(path, csvData);
        }
        // 检查是否和csvData一致
        if (csvData.check(structDec.getStructVarDecList())) return csvData;
        return null;
    }

    // 处理变化
    public static void changeCsv(@NotNull ALittleStructDec dec, List<String> varList) {
        ALittleStructNameDec nameDec = dec.getStructNameDec();
        if (nameDec == null) return;
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

    public static void checkAndChange(@NotNull ALittleStructDec structDec) throws ALittleGuessException {
        ALittleCsvData csvData = ALittleCsvDataManager.checkCsv(structDec);
        if (csvData == null) return;

        List<String> varList = csvData.generateVarList();
        Project project = structDec.getProject();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    ALittleCsvDataManager.changeCsv(structDec, varList);
                });
            }
        });
    }
}
