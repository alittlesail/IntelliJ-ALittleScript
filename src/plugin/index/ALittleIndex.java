package plugin.index;

import com.intellij.ProjectTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.psi.*;

import java.util.*;

public class ALittleIndex {
    // 对应的项目
    Project mProject;

    // 保存关键的元素对象，用于快速语法树解析
    // Key1:文件对象，Key2:元素对象，Value:类型
    protected Map<PsiFile, Map<PsiElement, List<ALittleGuess>>> mGuessTypeMap;
    // Key1:文件对象，Key2:名称，Value:数据
    protected Map<PsiFile, Map<String, ALittleClassData>> mClassDataMap;
    protected Map<PsiFile, Map<String, ALittleStructData>> mStructDataMap;
    protected Map<PsiFile, Map<String, ALittleEnumData>> mEnumDataMap;

    // 按命名域来划分
    // String 表示命名域
    protected Map<String, Map<ALittleNamespaceNameDec, ALittleAccessData>> mAllDataMap;
    // 根据开放权限来划分
    // String 表示命名域
    protected Map<String, ALittleAccessData> mGlobalAccessMap;
    protected Map<String, ALittleAccessData> mNamespaceAccessMap;
    protected Map<PsiFile, ALittleAccessData> mFileAccessMap;

    // 已加载的文件列表
    boolean mReloading = false;         // 是否正在加载
    boolean mReloaded = false;          // 是否加载完成

    // Csv数据联动
    protected Map<String, HashSet<ALittleStructDec>> mCsvStructSet; // 收集csv路径对应的struct集合
    // Mysql数据联动
    protected Map<String, HashSet<ALittleStructDec>> mMysqlStructSet; // 收集mysql路径对应的struct集合

    public ALittleIndex(Project project) {
        mProject = project;
        mAllDataMap = new HashMap<>();
        mGlobalAccessMap = new HashMap<>();
        mNamespaceAccessMap = new HashMap<>();
        mFileAccessMap = new HashMap<>();

        mGuessTypeMap = new HashMap<>();
        mClassDataMap = new HashMap<>();
        mStructDataMap = new HashMap<>();
        mEnumDataMap = new HashMap<>();

        mCsvStructSet = new HashMap<>();
        mMysqlStructSet = new HashMap<>();
    }

    protected void loadDir(PsiManager psi_mgr, VirtualFile root) {
        if (root.isDirectory()) {
            VirtualFile[] files = root.getChildren();
            if (files != null) {
                for (VirtualFile file : files) {
                    loadDir(psi_mgr, file);
                }
            }
        } else {
            PsiFile psi_file = psi_mgr.findFile(root);
            if (psi_file instanceof ALittleFile) {
                List<ALittleNamespaceDec> namespaceDecList = new ArrayList<>();
                for (PsiElement child = psi_file.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child instanceof ALittleNamespaceDec) {
                        namespaceDecList.add((ALittleNamespaceDec) child);
                    }
                }
                for (ALittleNamespaceDec namespaceDec : namespaceDecList) {
                    ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
                    if (namespaceNameDec == null) continue;
                    addNamespaceName(namespaceNameDec);
                }
            }
        }
    }

    public boolean isLoaded() {
        return mReloaded;
    }

    public boolean isLoading() {
        return mReloading;
    }

    public void reload() {
        if (mReloaded) return;

        mAllDataMap = new HashMap<>();
        mGlobalAccessMap = new HashMap<>();
        mNamespaceAccessMap = new HashMap<>();
        mFileAccessMap = new HashMap<>();

        mGuessTypeMap = new HashMap<>();
        mClassDataMap = new HashMap<>();
        mStructDataMap = new HashMap<>();
        mEnumDataMap = new HashMap<>();

        mCsvStructSet = new HashMap<>();
        mMysqlStructSet = new HashMap<>();

        mReloading = true;

        PsiManager psi_mgr = PsiManager.getInstance(mProject);
        VirtualFile[] roots = ProjectRootManager.getInstance(mProject).getContentRoots();
        for (VirtualFile root : roots) {
            loadDir(psi_mgr, root);
        }

        mReloading = false;
        mReloaded = true;

        mProject.getMessageBus().connect().subscribe(ProjectTopics.MODULES
                , new ModuleListener() {
                    @Override
                    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
                        if (ALittleTreeChangeListener.isReloading(project)) return;
                        ALittleTreeChangeListener listener = ALittleTreeChangeListener.sMap.get(project);
                        if (listener == null) return;
                        listener.AddModule(module);
                    }

                    @Override
                    public void beforeModuleRemoved(@NotNull Project project, @NotNull Module module) {
                        if (ALittleTreeChangeListener.isReloading(project)) return;
                        ALittleTreeChangeListener listener = ALittleTreeChangeListener.sMap.get(project);
                        if (listener == null) return;
                        listener.RemoveModule(module);
                    }
                });
    }

    // 添加类索引数据
    private void addClassData(@NotNull ALittleClassDec classDec) {
        ALittleClassNameDec nameDec = classDec.getClassNameDec();
        if (nameDec == null) return;

        Map<String, ALittleClassData> map = mClassDataMap.computeIfAbsent(classDec.getContainingFile().getOriginalFile(), k -> new HashMap<>());
        ALittleClassData classData = new ALittleClassData();
        map.put(nameDec.getText(), classData);

        ALittleTemplateDec template_dec = classDec.getTemplateDec();
        if (template_dec != null)
            classData.addClassChildDec(template_dec);

        ALittleClassBodyDec body_dec = classDec.getClassBodyDec();
        if (body_dec == null) return;

        List<ALittleClassElementDec> element_dec_list = body_dec.getClassElementDecList();
        for (ALittleClassElementDec element_dec : element_dec_list)
            classData.addClassChildDec(element_dec);
    }

    // 获取类索引数据
    public ALittleClassData getClassData(@NotNull ALittleClassDec dec) {
        ALittleClassNameDec nameDec = dec.getClassNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleClassData> map = mClassDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    // 添加结构体数据
    private void addStructData(@NotNull ALittleStructDec structDec) {
        ALittleStructNameDec nameDec = structDec.getStructNameDec();
        if (nameDec == null) return;

        Map<String, ALittleStructData> map = mStructDataMap.computeIfAbsent(structDec.getContainingFile().getOriginalFile(), k -> new HashMap<>());
        ALittleStructData structData = new ALittleStructData();
        map.put(nameDec.getText(), structData);

        ALittleStructBodyDec body_dec = structDec.getStructBodyDec();
        if (body_dec == null) return;

        List<ALittleStructVarDec> var_dec_list = body_dec.getStructVarDecList();
        for (ALittleStructVarDec var_dec : var_dec_list)
            structData.addVarDec(var_dec);
    }

    // 获取结构体数据
    public ALittleStructData getStructData(@NotNull ALittleStructDec dec) {
        ALittleStructNameDec nameDec = dec.getStructNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleStructData> map = mStructDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    // 添加枚举数据
    private void addEnumData(@NotNull ALittleEnumDec enumDec) {
        ALittleEnumNameDec nameDec = enumDec.getEnumNameDec();
        if (nameDec == null) return;

        Map<String, ALittleEnumData> map = mEnumDataMap.computeIfAbsent(enumDec.getContainingFile().getOriginalFile(), k -> new HashMap<>());
        ALittleEnumData enumData = new ALittleEnumData();
        map.put(nameDec.getText(), enumData);

        ALittleEnumBodyDec body_dec = enumDec.getEnumBodyDec();
        if (body_dec == null) return;

        List<ALittleEnumVarDec> var_dec_list = body_dec.getEnumVarDecList();
        for (ALittleEnumVarDec var_dec : var_dec_list)
            enumData.addVarDec(var_dec);
    }

    // 获取枚举数据
    public ALittleEnumData getEnumData(@NotNull ALittleEnumDec dec) {
        ALittleEnumNameDec nameDec = dec.getEnumNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleEnumData> map = mEnumDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    // 添加命名域
    public void addNamespaceName(@NotNull ALittleNamespaceNameDec element) {
        // 清除标记
        mGuessTypeMap.remove(element.getContainingFile().getOriginalFile());
        mClassDataMap.remove(element.getContainingFile().getOriginalFile());
        mStructDataMap.remove(element.getContainingFile().getOriginalFile());
        mEnumDataMap.remove(element.getContainingFile().getOriginalFile());

        String namespaceName = element.getText();

        // 处理mNamespaceMap
        Map<ALittleNamespaceNameDec, ALittleAccessData> allDataMap = mAllDataMap.computeIfAbsent(namespaceName, k -> new HashMap<>());
        ALittleAccessData allAccessData = new ALittleAccessData();
        allDataMap.put(element, allAccessData);

        ALittleAccessData globalAccessData = mGlobalAccessMap.computeIfAbsent(namespaceName, k -> new ALittleAccessData());
        ALittleAccessData namespaceAccessData = mNamespaceAccessMap.computeIfAbsent(namespaceName, k -> new ALittleAccessData());
        ALittleAccessData fileAccessData = mFileAccessMap.computeIfAbsent(element.getContainingFile().getOriginalFile(), k -> new ALittleAccessData());

        ALittleNamespaceDec namespaceDec = (ALittleNamespaceDec) element.getParent();
        List<ALittleNamespaceElementDec> elementDecList = namespaceDec.getNamespaceElementDecList();
        for (ALittleNamespaceElementDec elementDec : elementDecList) {
            // 添加类
            if (elementDec.getClassDec() != null) {
                ALittleClassDec dec = elementDec.getClassDec();
                ALittleClassNameDec nameDec = dec.getClassNameDec();
                if (nameDec == null) continue;

                // 添加类数据
                addClassData(dec);
                // 添加到全权限
                allAccessData.addNameDec(nameDec);
                // 按访问权限划分
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addNameDec(nameDec);
                }
                // 添加枚举
            } else if (elementDec.getEnumDec() != null) {
                ALittleEnumDec dec = elementDec.getEnumDec();
                ALittleEnumNameDec nameDec = dec.getEnumNameDec();
                if (nameDec == null) continue;

                // 添加枚举数据
                addEnumData(dec);
                // 添加到全权限
                allAccessData.addNameDec(nameDec);
                // 按访问权限划分
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addNameDec(nameDec);
                }
                // 添加结构体
            } else if (elementDec.getStructDec() != null) {
                ALittleStructDec dec = elementDec.getStructDec();
                ALittleStructNameDec nameDec = dec.getStructNameDec();
                if (nameDec == null) continue;
                // 添加结构体数据
                addStructData(dec);
                // 添加到全权限
                allAccessData.addNameDec(nameDec);
                // 按访问权限划分
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addNameDec(nameDec);
                }
                // 添加全局函数
            } else if (elementDec.getGlobalMethodDec() != null) {
                ALittleGlobalMethodDec dec = elementDec.getGlobalMethodDec();
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;

                // 添加到全权限
                allAccessData.addNameDec(nameDec);
                // 按访问权限划分
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addNameDec(nameDec);
                }
                // 添加单例
            } else if (elementDec.getInstanceDec() != null) {
                ALittleInstanceDec dec = elementDec.getInstanceDec();
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());

                List<ALittleVarAssignDec> varAssignDecList = dec.getVarAssignExpr().getVarAssignDecList();
                for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                    ALittleVarAssignNameDec nameDec = varAssignDec.getVarAssignNameDec();

                    // 添加到全权限
                    allAccessData.addNameDec(nameDec);
                    // 按访问权限划分
                    if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                        globalAccessData.addNameDec(nameDec);
                    } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                        namespaceAccessData.addNameDec(nameDec);
                    } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                        fileAccessData.addNameDec(nameDec);
                    }
                }
                // 添加using
            } else if (elementDec.getUsingDec() != null) {
                ALittleUsingDec dec = elementDec.getUsingDec();
                ALittleUsingNameDec nameDec = dec.getUsingNameDec();
                if (nameDec == null) continue;

                // 添加到全权限
                allAccessData.addNameDec(nameDec);
                // 按访问权限划分
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(elementDec.getModifierList());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addNameDec(nameDec);
                }
            }
        }
    }

    // 移除命名域
    public void removeNamespaceName(ALittleNamespaceNameDec element) {
        // 清除标记
        mGuessTypeMap.remove(element.getContainingFile().getOriginalFile());
        mClassDataMap.remove(element.getContainingFile().getOriginalFile());
        mStructDataMap.remove(element.getContainingFile().getOriginalFile());
        mEnumDataMap.remove(element.getContainingFile().getOriginalFile());

        String namespaceName = element.getText();

        Map<ALittleNamespaceNameDec, ALittleAccessData> allDataMap = mAllDataMap.get(namespaceName);
        if (allDataMap == null) return;
        ALittleAccessData allAccessData = allDataMap.get(element);
        if (allAccessData == null) return;

        allDataMap.remove(element);
        if (allDataMap.isEmpty()) {
            mAllDataMap.remove(namespaceName);
        }

        ALittleAccessData globalAccessData = mGlobalAccessMap.get(namespaceName);
        ALittleAccessData namespaceAccessData = mNamespaceAccessMap.get(namespaceName);
        ALittleAccessData fileAccessData = mFileAccessMap.get(element.getContainingFile().getOriginalFile());

        for (Map.Entry<PsiHelper.PsiElementType, Map<String, Set<PsiElement>>> entry : allAccessData.elementMap.entrySet()) {
            for (Map.Entry<String, Set<PsiElement>> elementEntry : entry.getValue().entrySet()) {
                for (PsiElement nameDec : elementEntry.getValue()) {
                    if (globalAccessData != null) globalAccessData.removeNameDec(nameDec);
                    if (namespaceAccessData != null) namespaceAccessData.removeNameDec(nameDec);
                    if (fileAccessData != null) fileAccessData.removeNameDec(nameDec);
                }
            }
        }
    }
}
