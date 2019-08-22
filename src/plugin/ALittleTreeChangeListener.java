package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.SmartList;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ALittleTreeChangeListener implements PsiTreeChangeListener {
    // 元素版本ID，用来标记内容变化
    public static int sVersionId = 0;

    public static Map<Project, ALittleTreeChangeListener> sMap = new HashMap<>();

    public static List<ALittleNamespaceNameDec> findNamespaceNameDecList(Project project, String namespaceName) {
        List<ALittleNamespaceNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        if (namespaceName.isEmpty()) {
            for (Map.Entry<String, Map<ALittleNamespaceNameDec, Data>> entry : listener.mNamespaceMap.entrySet()) {
                result.add(entry.getValue().keySet().iterator().next());
            }
        } else {
            Map<ALittleNamespaceNameDec, Data> map = listener.mNamespaceMap.get(namespaceName);
            if (map != null)
                result.add(map.keySet().iterator().next());
        }
        return result;
    }

    public static List<ALittleClassNameDec> findClassNameDecList(Project project, String namespaceName, String name) {
        List<ALittleClassNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.classMap == null) return result;

        if (name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleClassNameDec>> entry : data.classMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleClassNameDec> set = data.classMap.get(name);
            if (set != null) {
                result.addAll(set);
            }
        }
        return result;
    }

    public static List<ALittleStructNameDec> findStructNameDecList(Project project, String namespaceName, String srcName) {
        List<ALittleStructNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.structMap == null) return result;

        if (srcName.isEmpty()) {
            for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.structMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleStructNameDec> set = data.structMap.get(srcName);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleEnumNameDec> findEnumNameDecList(Project project, String namespaceName, String srcName) {
        List<ALittleEnumNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.enumMap == null) return result;

        if (srcName.isEmpty()) {
            for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enumMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleEnumNameDec> set = data.enumMap.get(srcName);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleMethodNameDec> findGlobalMethodNameDecList(Project project, String namespaceName, String srcName) {
        List<ALittleMethodNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.globalMethodMap == null) return result;

        if (srcName.isEmpty()) {
            for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.globalMethodMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleMethodNameDec> set = data.globalMethodMap.get(srcName);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleVarAssignNameDec> findInstanceNameDecList(Project project, String namespaceName, String srcName, boolean findInGlobal) {
        List<ALittleVarAssignNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先从指定模块中找
        Data data = listener.mDataMap.get(namespaceName);
        if (data != null && data.instanceMap != null) {
            if (srcName.isEmpty()) {
                for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry : data.instanceMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleVarAssignNameDec> set = data.instanceMap.get(srcName);
                if (set != null)
                    result.addAll(set);
            }
        }

        // 从全局找
        if (findInGlobal) {
            if (srcName.isEmpty()) {
                for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry : listener.mInstanceMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleVarAssignNameDec> set = listener.mInstanceMap.get(srcName);
                if (set != null)
                    result.addAll(set);
            }
        }

        return result;
    }

    public static boolean isElementExist(PsiElement element) {
        ALittleTreeChangeListener listener = sMap.get(element.getProject());
        if (listener == null) return false;
        if (!listener.mReloaded) listener.reload();

        return listener.mElement.contains(element);
    }

    public static void handleDirDelete(Project project, VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
            VirtualFile[] fileList = virtualFile.getChildren();
            if (fileList != null) {
                for (VirtualFile file : fileList) {
                    handleDirDelete(project, file);
                }
            }
            // 如果是文件
        } else {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (file instanceof ALittleFile) {
                handleFileDelete(project, (ALittleFile) file);
            }
        }
    }

    private static void handleFileDelete(Project project, ALittleFile alittleFile) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return;

        if (!listener.mReloaded) listener.reload();

        ALittleNamespaceDec namespaceDec = ALittleUtil.getNamespaceDec(alittleFile);
        if (namespaceDec == null) return;

        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return;

        listener.removeNamespaceName(namespaceNameDec.getText(), namespaceNameDec);
    }

    public static void handleDirCreated(Project project, VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
            VirtualFile[] fileList = virtualFile.getChildren();
            if (fileList != null) {
                for (VirtualFile file : fileList) {
                    handleDirCreated(project, file);
                }
            }
            // 如果是文件
        } else {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (file instanceof ALittleFile) {
                handleFileCreated(project, (ALittleFile) file);
            }
        }
    }

    private static void handleFileCreated(Project project, ALittleFile alittleFile) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return;
        if (!listener.mReloaded) listener.reload();

        ALittleNamespaceDec namespaceDec = ALittleUtil.getNamespaceDec(alittleFile);
        if (namespaceDec == null) return;

        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return;

        listener.addNamespaceName(namespaceNameDec.getText(), namespaceNameDec);
    }

    public static void handleRefresh(Project project) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return;
        if (listener.mIsRefreshed) return;
        listener.mIsRefreshed = true;
        listener.reload();
    }

    public static boolean isReloading(Project project) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return true;
        if (listener.mReloading) return true;
        return false;
    }

    private Project mProject;
    private class Data
    {
        Map<String, Set<ALittleClassNameDec>> classMap;
        Map<String, Set<ALittleEnumNameDec>> enumMap;
        Map<String, Set<ALittleStructNameDec>> structMap;
        Map<String, Set<ALittleVarAssignNameDec>> instanceMap;
        Map<String, Set<ALittleMethodNameDec>> globalMethodMap;

        void addALittleClassNameDec(ALittleClassNameDec nameDec) {
            if (classMap == null) {
                classMap = new HashMap<>();
            }
            String nameText = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                classMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        void removeALittleClassNameDec(ALittleClassNameDec nameDec) {
            if (classMap == null) return;
            String nameText = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(nameText);
            if (set == null) return;
            set = classMap.get(nameText);
            set.remove(nameDec);
            if (set.isEmpty()) classMap.remove(nameText);
        }

        void addALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            if (enumMap == null) {
                enumMap = new HashMap<>();
            }
            String nameText = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                enumMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        void removeALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            if (classMap == null) return;
            String nameText = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(nameText);
            if (set == null) return;
            set = enumMap.get(nameText);
            set.remove(nameDec);
            if (set.isEmpty()) enumMap.remove(nameText);
        }

        void addALittleStructNameDec(ALittleStructNameDec nameDec) {
            if (structMap == null) {
                structMap = new HashMap<>();
            }
            String nameText = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                structMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        void removeALittleStructNameDec(ALittleStructNameDec nameDec) {
            if (structMap == null) return;
            String nameText = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(nameText);
            if (set == null) return;
            set = structMap.get(nameText);
            set.remove(nameDec);
            if (set.isEmpty()) structMap.remove(nameText);
        }

        void addALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            if (globalMethodMap == null) {
                globalMethodMap = new HashMap<>();
            }
            String nameText = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                globalMethodMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        void removeALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            if (globalMethodMap == null) return;
            String nameText = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(nameText);
            if (set == null) return;
            set = globalMethodMap.get(nameText);
            set.remove(nameDec);
            if (set.isEmpty()) globalMethodMap.remove(nameText);
        }

        void addALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            if (instanceMap == null) {
                instanceMap = new HashMap<>();
            }
            String nameText = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                instanceMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        void removeALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            if (instanceMap == null) return;
            String nameText = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(nameText);
            if (set == null) return;
            set = instanceMap.get(nameText);
            set.remove(nameDec);
            if (set.isEmpty()) instanceMap.remove(nameText);
        }
    }

    // key是命名域名称，Map的Key是命名域对象，Data是这个命名域对象下的所有内容
    private Map<String, Map<ALittleNamespaceNameDec, Data>> mNamespaceMap;
    // key是命名域名称，value是这个命名域下所有的内容
    private Map<String, Data> mDataMap;
    // 全局命名域下的单例
    private Map<String, Set<ALittleVarAssignNameDec>> mInstanceMap;
    // 保存关键的元素对象
    private Set<PsiElement> mElement;
    // 已加载的文件列表
    boolean mReloading = false;         // 是否正在加载
    boolean mReloaded = false;          // 是否加载完成
    boolean mIsRefreshed = false;       // 是否刷新过

    public ALittleTreeChangeListener(Project project) {
        mProject = project;
        mElement = new HashSet<>();
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
        mNamespaceMap = new HashMap<>();
        mDataMap = new HashMap<>();
        mInstanceMap = new HashMap<>();
        mReloading = true;

        PsiManager psi_mgr = PsiManager.getInstance(mProject);
        VirtualFile[] roots = ProjectRootManager.getInstance(mProject).getContentRoots();
        for (VirtualFile root : roots) {
            loadDir(psi_mgr, root);
        }

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

        mReloading = false;

        if (!mReloaded)
            PsiManager.getInstance(mProject).addPsiTreeChangeListener(this);
        mReloaded = true;
    }

    private void clearUserData(@NotNull  PsiElement element) {
        element.putUserData(ALittleReferenceUtil.sGuessTypeListKey, null);
        for(PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            clearUserData(child);
        }
    }

    private void addNamespaceName(String name, ALittleNamespaceNameDec element) {
        Map<ALittleNamespaceNameDec, Data> map = mNamespaceMap.get(name);
        if (map == null) {
            map = new HashMap<>();
            mNamespaceMap.put(name, map);
        }

        Data data = new Data();
        map.put(element, data);

        Data fastData = mDataMap.get(name);
        if (fastData == null) {
            fastData = new Data();
            mDataMap.put(name, fastData);
        }

        ALittleNamespaceDec namespaceDec = (ALittleNamespaceDec)element.getParent();
        clearUserData(namespaceDec);
        for(PsiElement child = namespaceDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassDec) {
                ALittleClassDec dec = (ALittleClassDec)child;
                mElement.add(dec);
                ALittleClassNameDec nameDec = dec.getClassNameDec();
                if (nameDec == null) continue;
                data.addALittleClassNameDec(nameDec);
                fastData.addALittleClassNameDec(nameDec);
            } else if (child instanceof ALittleEnumDec) {
                ALittleEnumDec dec = (ALittleEnumDec)child;
                mElement.add(dec);
                dec.putUserData(ALittleReferenceUtil.sGuessTypeListKey, null);
                ALittleEnumNameDec nameDec = dec.getEnumNameDec();
                if (nameDec == null) continue;
                data.addALittleEnumNameDec(nameDec);
                fastData.addALittleEnumNameDec(nameDec);
            } else if (child instanceof ALittleStructDec) {
                ALittleStructDec dec = (ALittleStructDec)child;
                mElement.add(dec);
                dec.putUserData(ALittleReferenceUtil.sGuessTypeListKey, null);
                ALittleStructNameDec nameDec = dec.getStructNameDec();
                if (nameDec == null) continue;
                data.addALittleStructNameDec(nameDec);
                fastData.addALittleStructNameDec(nameDec);
            } else if (child instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)child;
                mElement.add(dec);
                dec.putUserData(ALittleReferenceUtil.sGuessTypeListKey, null);
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                data.addALittleGlobalMethodNameDec(nameDec);
                fastData.addALittleGlobalMethodNameDec(nameDec);
            } else if (child instanceof ALittleInstanceDec) {
                ALittleInstanceDec dec = (ALittleInstanceDec) child;
                ALittleAccessModifier access = dec.getAccessModifier();
                boolean isPublic = access != null && access.getText().equals("public");

                List<ALittleVarAssignDec> varAssignDecList = dec.getVarAssignExpr().getVarAssignDecList();
                for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                    varAssignDec.putUserData(ALittleReferenceUtil.sGuessTypeListKey, null);
                    mElement.add(varAssignDec);
                    if (isPublic) {
                        String nameText = varAssignDec.getVarAssignNameDec().getText();
                        Set<ALittleVarAssignNameDec> set = mInstanceMap.get(nameText);
                        if (set == null) {
                            set = new HashSet<>();
                            mInstanceMap.put(nameText, set);
                        }
                        set.add(varAssignDec.getVarAssignNameDec());
                    } else{
                        fastData.addALittleInstanceNameDec(varAssignDec.getVarAssignNameDec());
                    }
                    data.addALittleInstanceNameDec(varAssignDec.getVarAssignNameDec());
                }
            }
        }
    }

    private void removeNamespaceName(String name, ALittleNamespaceNameDec element) {
        Data fastData = mDataMap.get(name);
        Map<ALittleNamespaceNameDec, Data> map = mNamespaceMap.get(name);
        if (map == null) return;

        Data data = map.get(element);
        if (data == null) return;

        map.remove(element);
        if (map.isEmpty()) mNamespaceMap.remove(name);

        if (fastData == null) return;

        if (data.classMap != null) {
            for (Map.Entry<String, Set<ALittleClassNameDec>> entry : data.classMap.entrySet()) {
                for (ALittleClassNameDec nameDec : entry.getValue()) {
                    mElement.remove(nameDec.getParent());
                    fastData.removeALittleClassNameDec(nameDec);
                }
            }
        }
        if (data.enumMap != null) {
            for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enumMap.entrySet()) {
                for (ALittleEnumNameDec nameDec : entry .getValue()) {
                    mElement.remove(nameDec.getParent());
                    fastData.removeALittleEnumNameDec(nameDec);
                }
            }
        }
        if (data.structMap != null) {
            for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.structMap.entrySet()) {
                for (ALittleStructNameDec nameDec : entry .getValue()) {
                    mElement.remove(nameDec.getParent());
                    fastData.removeALittleStructNameDec(nameDec);
                }
            }
        }
        if (data.globalMethodMap != null) {
            for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.globalMethodMap.entrySet()) {
                for (ALittleMethodNameDec nameDec : entry .getValue()) {
                    mElement.remove(nameDec.getParent());
                    fastData.removeALittleGlobalMethodNameDec(nameDec);
                }
            }
        }
        if (data.instanceMap != null) {
            for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry_instance : data.instanceMap.entrySet()) {
                for (ALittleVarAssignNameDec nameDec : entry_instance.getValue()) {
                    mElement.remove(nameDec.getParent());
                    fastData.removeALittleInstanceNameDec(nameDec);

                    String nameText = nameDec.getText();
                    Set<ALittleVarAssignNameDec> set = mInstanceMap.get(nameText);
                    if (set != null) {
                        set.remove(nameDec);
                        if (set.isEmpty()) mInstanceMap.remove(nameText);
                    }
                }
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
