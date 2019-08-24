package plugin.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.component.StdLibraryProvider;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import java.io.File;
import java.util.*;

public class ALittleIndex {
    // 对应的项目
    Project mProject;

    // 保存关键的元素对象，用于快速语法树解析
    private Map<PsiFile, Map<PsiElement, List<ALittleReferenceUtil.GuessTypeInfo>>> mGuessTypeMap;
    private Map<PsiFile, Map<ALittleClassDec, ALittleClassData>> mClassDataMap;

    // key是命名域名称，value是这个命名域下所有的内容，这里的数据是最全的
    private Map<String, Map<ALittleNamespaceNameDec, ALittleAccessData>> mAllDataMap;

    // 全局可访问，key是命名域
    private Map<String, ALittleAccessData> mGlobalAccessMap;
    // 某个命名域下可访问，key是命名域
    private Map<String, ALittleAccessData> mNamespaceAccessMap;
    // 某个文件下可访问
    private Map<PsiFile, ALittleAccessData> mFileAccessMap;

    // 已加载的文件列表
    boolean mReloading = false;         // 是否正在加载
    boolean mReloaded = false;          // 是否加载完成
    boolean mIsRefreshed = false;       // 是否刷新过

    public ALittleIndex(Project project) {
        mProject = project;
        mAllDataMap = new HashMap<>();
        mGlobalAccessMap = new HashMap<>();
        mNamespaceAccessMap = new HashMap<>();
        mFileAccessMap = new HashMap<>();
        mGuessTypeMap = new HashMap<>();
        mClassDataMap = new HashMap<>();
    }

    private void loadDir(PsiManager psi_mgr, VirtualFile root) {
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
                for(PsiElement child = psi_file.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child instanceof ALittleNamespaceDec) {
                        namespaceDecList.add((ALittleNamespaceDec)child);
                    }
                }
                for (ALittleNamespaceDec namespaceDec : namespaceDecList) {

                    ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
                    if (namespaceNameDec == null) continue;

                    addNamespaceName(namespaceNameDec.getText(), namespaceNameDec);
                }
            }
        }
    }

    public void reload() {
        mAllDataMap = new HashMap<>();
        mGlobalAccessMap = new HashMap<>();
        mNamespaceAccessMap = new HashMap<>();
        mFileAccessMap = new HashMap<>();
        mGuessTypeMap = new HashMap<>();
        mClassDataMap = new HashMap<>();

        mReloading = true;

        PsiManager psi_mgr = PsiManager.getInstance(mProject);
        VirtualFile[] roots = ProjectRootManager.getInstance(mProject).getContentRoots();
        for (VirtualFile root : roots) {
            loadDir(psi_mgr, root);
        }


        if (!StdLibraryProvider.isPluginSelf(mProject)) {
            try {
                // 适配代码
                String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);
                VirtualFile dir;
                if (jarPath.endsWith(".jar"))
                    dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "std"));
                else
                    dir = VfsUtil.findFileByIoFile(new File(jarPath + "/std"), true);

                if (dir != null) {
                    loadDir(psi_mgr, dir);
                }
            } catch (Exception e) {
            }
        }

        mReloading = false;

        if (!mReloaded)
            PsiManager.getInstance(mProject).addPsiTreeChangeListener(this);
        mReloaded = true;
    }

    private void addClassData(@NotNull ALittleClassDec classDec) {
        Map<ALittleClassDec, ClassData> map = mClassDataMap.get(classDec.getContainingFile());
        if (map == null) {
            map = new HashMap<>();
            mClassDataMap.put(classDec.getContainingFile(), map);
        }
        ClassData classData = new ClassData();
        map.put(classDec, classData);

        for(PsiElement child = classDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassVarDec) {
                ALittleClassVarDec dec = (ALittleClassVarDec)child;
                PsiElement nameDec = dec.getIdContent();
                if (nameDec == null) continue;
                int accessLevel = ALittleUtil.calcAccess(dec.getAccessModifier());
                if (accessLevel == ALittleUtil.sAccessPrivate) {
                    classData.privateData.varMap.put(nameDec.getText(), dec);
                } else if (accessLevel == ALittleUtil.sAccessProtected) {
                    classData.protectedData.varMap.put(nameDec.getText(), dec);
                } else {
                    classData.publicData.varMap.put(nameDec.getText(), dec);
                }
            } else if (child instanceof ALittleClassMethodDec) {
                ALittleClassMethodDec dec = (ALittleClassMethodDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                int accessLevel = ALittleUtil.calcAccess(dec.getAccessModifier());
                if (accessLevel == ALittleUtil.sAccessPrivate) {
                    classData.privateData.funMap.put(nameDec.getText(), nameDec);
                } else if (accessLevel == ALittleUtil.sAccessProtected) {
                    classData.protectedData.funMap.put(nameDec.getText(), nameDec);
                } else {
                    classData.publicData.funMap.put(nameDec.getText(), nameDec);
                }
            } else if (child instanceof ALittleClassGetterDec) {
                ALittleClassGetterDec dec = (ALittleClassGetterDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                int accessLevel = ALittleUtil.calcAccess(dec.getAccessModifier());
                if (accessLevel == ALittleUtil.sAccessPrivate) {
                    classData.privateData.getterMap.put(nameDec.getText(), nameDec);
                } else if (accessLevel == ALittleUtil.sAccessProtected) {
                    classData.protectedData.getterMap.put(nameDec.getText(), nameDec);
                } else {
                    classData.publicData.getterMap.put(nameDec.getText(), nameDec);
                }
            } else if (child instanceof ALittleClassSetterDec) {
                ALittleClassSetterDec dec = (ALittleClassSetterDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                int accessLevel = ALittleUtil.calcAccess(dec.getAccessModifier());
                if (accessLevel == ALittleUtil.sAccessPrivate) {
                    classData.privateData.setterMap.put(nameDec.getText(), nameDec);
                } else if (accessLevel == ALittleUtil.sAccessProtected) {
                    classData.protectedData.setterMap.put(nameDec.getText(), nameDec);
                } else {
                    classData.publicData.setterMap.put(nameDec.getText(), nameDec);
                }
            } else if (child instanceof ALittleClassStaticDec) {
                ALittleClassStaticDec dec = (ALittleClassStaticDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                int accessLevel = ALittleUtil.calcAccess(dec.getAccessModifier());
                if (accessLevel == ALittleUtil.sAccessPrivate) {
                    classData.privateData.staticMap.put(nameDec.getText(), nameDec);
                } else if (accessLevel == ALittleUtil.sAccessProtected) {
                    classData.protectedData.staticMap.put(nameDec.getText(), nameDec);
                } else {
                    classData.publicData.staticMap.put(nameDec.getText(), nameDec);
                }
            }
        }
    }

    private void addNamespaceName(String name, ALittleNamespaceNameDec element) {
        // 清除标记
        mGuessTypeMap.remove(element.getContainingFile());
        // 处理mNamespaceMap
        Map<ALittleNamespaceNameDec, Data> map = mAllDataMap.get(name);
        if (map == null) {
            map = new HashMap<>();
            mAllDataMap.put(name, map);
        }
        Data allData = new Data();
        map.put(element, allData);

        // 处理mGlobalAccessMap
        Data globalData = mGlobalAccessMap.get(name);
        if (globalData == null) {
            globalData = new Data();
            mGlobalAccessMap.put(name, globalData);
        }

        // 处理mNamespaceAccessMap
        Data namespaceData = mNamespaceAccessMap.get(name);
        if (namespaceData == null) {
            namespaceData = new Data();
            mNamespaceAccessMap.put(name, namespaceData);
        }

        // 处理mFileAccessMap
        Data psiFileData = mFileAccessMap.get(element.getContainingFile());
        if (psiFileData == null) {
            psiFileData = new Data();
            mFileAccessMap.put(element.getContainingFile(), psiFileData);
        }

        ALittleNamespaceDec namespaceDec = (ALittleNamespaceDec)element.getParent();
        for(PsiElement child = namespaceDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassDec) {
                ALittleClassDec dec = (ALittleClassDec)child;
                ALittleClassNameDec nameDec = dec.getClassNameDec();
                if (nameDec == null) continue;

                addClassData(dec);
                allData.addALittleClassNameDec(nameDec);

                String access = "private";
                if (dec.getAccessModifier() != null) {
                    access = dec.getAccessModifier().getText();
                }

                if (access.equals("private")) {
                    psiFileData.addALittleClassNameDec(nameDec);
                } else if (access.equals("protected")) {
                    namespaceData.addALittleClassNameDec(nameDec);
                } else if (access.equals("public")) {
                    globalData.addALittleClassNameDec(nameDec);
                }
            } else if (child instanceof ALittleEnumDec) {
                ALittleEnumDec dec = (ALittleEnumDec)child;
                ALittleEnumNameDec nameDec = dec.getEnumNameDec();
                if (nameDec == null) continue;

                allData.addALittleEnumNameDec(nameDec);

                String access = "private";
                if (dec.getAccessModifier() != null) {
                    access = dec.getAccessModifier().getText();
                }

                if (access.equals("private")) {
                    psiFileData.addALittleEnumNameDec(nameDec);
                } else if (access.equals("protected")) {
                    namespaceData.addALittleEnumNameDec(nameDec);
                } else if (access.equals("public")) {
                    globalData.addALittleEnumNameDec(nameDec);
                }
            } else if (child instanceof ALittleStructDec) {
                ALittleStructDec dec = (ALittleStructDec)child;
                ALittleStructNameDec nameDec = dec.getStructNameDec();
                if (nameDec == null) continue;

                allData.addALittleStructNameDec(nameDec);

                String access = "private";
                if (dec.getAccessModifier() != null) {
                    access = dec.getAccessModifier().getText();
                }

                if (access.equals("private")) {
                    psiFileData.addALittleStructNameDec(nameDec);
                } else if (access.equals("protected")) {
                    namespaceData.addALittleStructNameDec(nameDec);
                } else if (access.equals("public")) {
                    globalData.addALittleStructNameDec(nameDec);
                }
            } else if (child instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;

                allData.addALittleGlobalMethodNameDec(nameDec);

                String access = "private";
                if (dec.getAccessModifier() != null) {
                    access = dec.getAccessModifier().getText();
                }

                if (access.equals("private")) {
                    psiFileData.addALittleGlobalMethodNameDec(nameDec);
                } else if (access.equals("protected")) {
                    namespaceData.addALittleGlobalMethodNameDec(nameDec);
                } else if (access.equals("public")) {
                    globalData.addALittleGlobalMethodNameDec(nameDec);
                }
            } else if (child instanceof ALittleInstanceDec) {
                ALittleInstanceDec dec = (ALittleInstanceDec) child;
                String access = "private";
                if (dec.getAccessModifier() != null) {
                    access = dec.getAccessModifier().getText();
                }

                List<ALittleVarAssignDec> varAssignDecList = dec.getVarAssignExpr().getVarAssignDecList();
                for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                    ALittleVarAssignNameDec nameDec = varAssignDec.getVarAssignNameDec();

                    allData.addALittleInstanceNameDec(nameDec);
                    if (access.equals("private")) {
                        psiFileData.addALittleInstanceNameDec(nameDec);
                    } else if (access.equals("protected")) {
                        namespaceData.addALittleInstanceNameDec(nameDec);
                    } else if (access.equals("public")) {
                        globalData.addALittleInstanceNameDec(nameDec);
                    }
                }
            }
        }
    }

    private void removeNamespaceName(String name, ALittleNamespaceNameDec element) {
        // 清除标记
        mGuessTypeMap.remove(element.getContainingFile());
        mClassDataMap.remove(element.getContainingFile());

        Map<ALittleNamespaceNameDec, Data> map = mAllDataMap.get(name);
        if (map == null) return;

        Data allData = map.get(element);
        if (allData == null) return;
        map.remove(element);
        Data tmp;

        for (Map.Entry<String, Set<ALittleClassNameDec>> entry : allData.classMap.entrySet()) {
            for (ALittleClassNameDec nameDec : entry.getValue()) {
                tmp = mFileAccessMap.get(element.getContainingFile());
                if (tmp != null) tmp.removeALittleClassNameDec(nameDec);
                tmp = mNamespaceAccessMap.get(name);
                if (tmp != null) tmp.removeALittleClassNameDec(nameDec);
                tmp = mGlobalAccessMap.get(name);
                if (tmp != null) tmp.removeALittleClassNameDec(nameDec);
            }
        }
        for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : allData.enumMap.entrySet()) {
            for (ALittleEnumNameDec nameDec : entry .getValue()) {
                tmp = mFileAccessMap.get(element.getContainingFile());
                if (tmp != null) tmp.removeALittleEnumNameDec(nameDec);
                tmp = mNamespaceAccessMap.get(name);
                if (tmp != null) tmp.removeALittleEnumNameDec(nameDec);
                tmp = mGlobalAccessMap.get(name);
                if (tmp != null) tmp.removeALittleEnumNameDec(nameDec);
            }
        }
        for (Map.Entry<String, Set<ALittleStructNameDec>> entry : allData.structMap.entrySet()) {
            for (ALittleStructNameDec nameDec : entry .getValue()) {
                tmp = mFileAccessMap.get(element.getContainingFile());
                if (tmp != null) tmp.removeALittleStructNameDec(nameDec);
                tmp = mNamespaceAccessMap.get(name);
                if (tmp != null) tmp.removeALittleStructNameDec(nameDec);
                tmp = mGlobalAccessMap.get(name);
                if (tmp != null) tmp.removeALittleStructNameDec(nameDec);
            }
        }
        for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : allData.globalMethodMap.entrySet()) {
            for (ALittleMethodNameDec nameDec : entry .getValue()) {
                tmp = mFileAccessMap.get(element.getContainingFile());
                if (tmp != null) tmp.removeALittleGlobalMethodNameDec(nameDec);
                tmp = mNamespaceAccessMap.get(name);
                if (tmp != null) tmp.removeALittleGlobalMethodNameDec(nameDec);
                tmp = mGlobalAccessMap.get(name);
                if (tmp != null) tmp.removeALittleGlobalMethodNameDec(nameDec);
            }
        }
        for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry_instance : allData.instanceMap.entrySet()) {
            for (ALittleVarAssignNameDec nameDec : entry_instance.getValue()) {
                tmp = mFileAccessMap.get(element.getContainingFile());
                if (tmp != null) tmp.removeALittleInstanceNameDec(nameDec);
                tmp = mNamespaceAccessMap.get(name);
                if (tmp != null) tmp.removeALittleInstanceNameDec(nameDec);
                tmp = mGlobalAccessMap.get(name);
                if (tmp != null) tmp.removeALittleInstanceNameDec(nameDec);
            }
        }
    }

    public void beforeChildAddition(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void beforeChildMovement(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent var1)
    {
        PsiFile file = var1.getFile();
        if (!(file instanceof ALittleFile)) return;
        ALittleFile alittleFile = (ALittleFile)file;

        ALittleNamespaceDec namespaceDec = ALittleUtil.getNamespaceDec(alittleFile);
        if (namespaceDec == null) return;

        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return;

        removeNamespaceName(namespaceNameDec.getText(), namespaceNameDec);
    }

    public void beforePropertyChange(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void childAdded(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void childRemoved(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void childReplaced(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void childrenChanged(@NotNull PsiTreeChangeEvent var1)
    {
        PsiFile file = var1.getFile();
        if (!(file instanceof ALittleFile)) return;
        ALittleFile alittleFile = (ALittleFile)file;

        ALittleNamespaceDec namespaceDec = ALittleUtil.getNamespaceDec(alittleFile);
        if (namespaceDec == null) return;

        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return;

        addNamespaceName(namespaceNameDec.getText(), namespaceNameDec);
    }

    public void childMoved(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void propertyChanged(@NotNull PsiTreeChangeEvent var1)
    {
    }
}
