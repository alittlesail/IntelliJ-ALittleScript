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

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ALittleTreeChangeListener implements PsiTreeChangeListener {
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

    public static List<ALittleStructNameDec> findStructNameDecList(Project project, String namespaceName, String src_name) {
        List<ALittleStructNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.structMap == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.structMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleStructNameDec> set = data.structMap.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleEnumNameDec> findEnumNameDecList(Project project, String namespaceName, String src_name) {
        List<ALittleEnumNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.enumMap == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enumMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleEnumNameDec> set = data.enumMap.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleMethodNameDec> findGlobalMethodNameDecList(Project project, String namespaceName, String src_name) {
        List<ALittleMethodNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        Data data = listener.mDataMap.get(namespaceName);
        if (data == null || data.globalMethodMap == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.globalMethodMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleMethodNameDec> set = data.globalMethodMap.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleVarAssignNameDec> findInstanceNameDecList(Project project, String namespaceName, String src_name, boolean find_in_global) {
        List<ALittleVarAssignNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先从指定模块中找
        Data data = listener.mDataMap.get(namespaceName);
        if (data != null && data.instanceMap != null) {
            if (src_name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry : data.instanceMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleVarAssignNameDec> set = data.instanceMap.get(src_name);
                if (set != null)
                    result.addAll(set);
            }
        }

        // 从全局找
        if (find_in_global) {
            if (src_name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry : listener.m_instanceMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleVarAssignNameDec> set = listener.m_instanceMap.get(src_name);
                if (set != null)
                    result.addAll(set);
            }
        }

        return result;
    }

    public static void handleDirDelete(Project project, VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
            VirtualFile[] file_list = virtualFile.getChildren();
            if (file_list != null) {
                for (VirtualFile file : file_list) {
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
            VirtualFile[] file_list = virtualFile.getChildren();
            if (file_list != null) {
                for (VirtualFile file : file_list) {
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
        if (listener.mIsRefresh) return;
        listener.mIsRefresh = true;
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
            String name_text = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                classMap.put(name_text, set);
            }
            set.add(nameDec);
        }

        void removeALittleClassNameDec(ALittleClassNameDec nameDec) {
            if (classMap == null) return;
            String name_text = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(name_text);
            if (set == null) return;
            set = classMap.get(name_text);
            set.remove(nameDec);
            if (set.isEmpty()) classMap.remove(name_text);
        }

        void addALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            if (enumMap == null) {
                enumMap = new HashMap<>();
            }
            String name_text = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                enumMap.put(name_text, set);
            }
            set.add(nameDec);
        }

        void removeALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            if (classMap == null) return;
            String name_text = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(name_text);
            if (set == null) return;
            set = enumMap.get(name_text);
            set.remove(nameDec);
            if (set.isEmpty()) enumMap.remove(name_text);
        }

        void addALittleStructNameDec(ALittleStructNameDec nameDec) {
            if (structMap == null) {
                structMap = new HashMap<>();
            }
            String name_text = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                structMap.put(name_text, set);
            }
            set.add(nameDec);
        }

        void removeALittleStructNameDec(ALittleStructNameDec nameDec) {
            if (structMap == null) return;
            String name_text = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(name_text);
            if (set == null) return;
            set = structMap.get(name_text);
            set.remove(nameDec);
            if (set.isEmpty()) structMap.remove(name_text);
        }

        void addALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            if (globalMethodMap == null) {
                globalMethodMap = new HashMap<>();
            }
            String name_text = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                globalMethodMap.put(name_text, set);
            }
            set.add(nameDec);
        }

        void removeALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            if (globalMethodMap == null) return;
            String name_text = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(name_text);
            if (set == null) return;
            set = globalMethodMap.get(name_text);
            set.remove(nameDec);
            if (set.isEmpty()) globalMethodMap.remove(name_text);
        }

        void addALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            if (instanceMap == null) {
                instanceMap = new HashMap<>();
            }
            String name_text = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                instanceMap.put(name_text, set);
            }
            set.add(nameDec);
        }

        void removeALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            if (instanceMap == null) return;
            String name_text = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(name_text);
            if (set == null) return;
            set = instanceMap.get(name_text);
            set.remove(nameDec);
            if (set.isEmpty()) instanceMap.remove(name_text);
        }
    }

    // 用于保存命名域对应的命名域对象
    private Map<String, Map<ALittleNamespaceNameDec, Data>> mNamespaceMap;
    private Map<String, Data> mDataMap;
    // 全局命名域下的单例
    private Map<String, Set<ALittleVarAssignNameDec>> m_instanceMap;
    // 已加载的文件列表
    boolean mReloading = false;
    boolean mReloaded = false;
    boolean mIsRefresh = false;

    public ALittleTreeChangeListener(Project project) {
        mProject = project;
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
        m_instanceMap = new HashMap<>();
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

    private void addNamespaceName(String name, ALittleNamespaceNameDec element) {
        Map<ALittleNamespaceNameDec, Data> map = mNamespaceMap.get(name);
        if (map == null) {
            map = new HashMap<>();
            mNamespaceMap.put(name, map);
        }

        Data data = new Data();

        Data fast_data = mDataMap.get(name);
        if (fast_data == null) {
            fast_data = new Data();
            mDataMap.put(name, fast_data);
        }

        ALittleNamespaceDec namespaceDec = (ALittleNamespaceDec)element.getParent();
        for(PsiElement child = namespaceDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassDec) {
                ALittleClassDec dec = (ALittleClassDec)child;
                ALittleClassNameDec nameDec = dec.getClassNameDec();
                if (nameDec == null) continue;
                data.addALittleClassNameDec(nameDec);
                fast_data.addALittleClassNameDec(nameDec);
            } else if (child instanceof ALittleEnumDec) {
                ALittleEnumDec dec = (ALittleEnumDec)child;
                ALittleEnumNameDec nameDec = dec.getEnumNameDec();
                if (nameDec == null) continue;
                data.addALittleEnumNameDec(nameDec);
                fast_data.addALittleEnumNameDec(nameDec);
            } else if (child instanceof ALittleStructDec) {
                ALittleStructDec dec = (ALittleStructDec)child;
                ALittleStructNameDec nameDec = dec.getStructNameDec();
                if (nameDec == null) continue;
                data.addALittleStructNameDec(nameDec);
                fast_data.addALittleStructNameDec(nameDec);
            } else if (child instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;
                data.addALittleGlobalMethodNameDec(nameDec);
                fast_data.addALittleGlobalMethodNameDec(nameDec);
            } else if (child instanceof ALittleVarAssignNameDec) {
                ALittleVarAssignNameDec nameDec = (ALittleVarAssignNameDec)child;
                ALittleVarAssignDec varNameDec = (ALittleVarAssignDec)nameDec.getParent();
                ALittleInstanceDec dec = (ALittleInstanceDec)varNameDec.getParent();

                ALittleAccessModifier access = dec.getAccessModifier();
                if (access != null && access.getText().equals("public")) {
                    String name_text = nameDec.getText();
                    Set<ALittleVarAssignNameDec> set = m_instanceMap.get(name_text);
                    if (set == null) {
                        set = new HashSet<>();
                        m_instanceMap.put(name_text, set);
                    }
                    set.add(nameDec);
                } else {
                    fast_data.addALittleInstanceNameDec(nameDec);
                }
                data.addALittleInstanceNameDec(nameDec);
            }
        }
        map.put(element, data);
    }

    private void removeNamespaceName(String name, ALittleNamespaceNameDec element) {
        Data fast_data = mDataMap.get(name);
        Map<ALittleNamespaceNameDec, Data> map = mNamespaceMap.get(name);
        if (map == null) return;

        Data data = map.get(element);
        if (data == null) return;

            if (fast_data != null) {
                if (data.classMap != null) {
                    for (Map.Entry<String, Set<ALittleClassNameDec>> entry : data.classMap.entrySet()) {
                        for (ALittleClassNameDec nameDec : entry .getValue()) {
                            fast_data.removeALittleClassNameDec(nameDec);
                        }
                    }
                }
                if (data.enumMap != null) {
                    for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enumMap.entrySet()) {
                        for (ALittleEnumNameDec nameDec : entry .getValue()) {
                            fast_data.removeALittleEnumNameDec(nameDec);
                        }
                    }
                }
                if (data.structMap != null) {
                    for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.structMap.entrySet()) {
                        for (ALittleStructNameDec nameDec : entry .getValue()) {
                            fast_data.removeALittleStructNameDec(nameDec);
                        }
                    }
                }
                if (data.globalMethodMap != null) {
                    for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.globalMethodMap.entrySet()) {
                        for (ALittleMethodNameDec nameDec : entry .getValue()) {
                            fast_data.removeALittleGlobalMethodNameDec(nameDec);
                        }
                    }
                }
                if (data.instanceMap != null) {
                    for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry_instance : data.instanceMap.entrySet()) {
                        for (ALittleVarAssignNameDec nameDec : entry_instance.getValue()) {
                            fast_data.removeALittleInstanceNameDec(nameDec);

                            String name_text = nameDec.getText();
                            Set<ALittleVarAssignNameDec> set = m_instanceMap.get(name_text);
                            if (set != null) {
                                set.remove(nameDec);
                                if (set.isEmpty()) m_instanceMap.remove(name_text);
                            }
                        }
                    }
                }
            }
        map.remove(element);
        if (map.isEmpty()) mNamespaceMap.remove(name);
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
