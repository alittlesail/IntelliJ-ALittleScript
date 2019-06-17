package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.SmartList;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

public class ALittleTreeChangeListener implements PsiTreeChangeListener {
    public static Map<Project, ALittleTreeChangeListener> s_map = new HashMap<>();

    public static List<ALittleNamespaceNameDec> findNamespaceNameDecList(Project project, String src_namespace) {
        List<ALittleNamespaceNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        if (src_namespace.isEmpty()) {
            for (Map.Entry<String, Map<ALittleNamespaceNameDec, Data>> entry : listener.m_namespace_map.entrySet()) {
                result.add(entry.getValue().keySet().iterator().next());
            }
        } else {
            Map<ALittleNamespaceNameDec, Data> map = listener.m_namespace_map.get(src_namespace);
            if (map != null)
                result.add(map.keySet().iterator().next());
        }
        return result;
    }

    public static List<ALittleClassNameDec> findClassNameDecList(Project project, String src_namespace, String src_name) {
        List<ALittleClassNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        Data data = listener.m_data_map.get(src_namespace);
        if (data == null || data.class_map == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleClassNameDec>> entry : data.class_map.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleClassNameDec> set = data.class_map.get(src_name);
            if (set != null) {
                result.addAll(set);
            }
        }
        return result;
    }

    public static List<ALittleStructNameDec> findStructNameDecList(Project project, String src_namespace, String src_name) {
        List<ALittleStructNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        Data data = listener.m_data_map.get(src_namespace);
        if (data == null || data.struct_map == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.struct_map.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleStructNameDec> set = data.struct_map.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleEnumNameDec> findEnumNameDecList(Project project, String src_namespace, String src_name) {
        List<ALittleEnumNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        Data data = listener.m_data_map.get(src_namespace);
        if (data == null || data.enum_map == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enum_map.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleEnumNameDec> set = data.enum_map.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleMethodNameDec> findGlobalMethodNameDecList(Project project, String src_namespace, String src_name) {
        List<ALittleMethodNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        Data data = listener.m_data_map.get(src_namespace);
        if (data == null || data.global_method_map == null) return result;

        if (src_name.isEmpty()) {
            for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.global_method_map.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<ALittleMethodNameDec> set = data.global_method_map.get(src_name);
            if (set != null)
                result.addAll(set);
        }
        return result;
    }

    public static List<ALittleInstanceNameDec> findInstanceNameDecList(Project project, String src_namespace, String src_name, boolean find_in_global) {
        List<ALittleInstanceNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return result;
        if (!listener.m_reload_ed) listener.reload();

        // 先从指定模块中找
        Data data = listener.m_data_map.get(src_namespace);
        if (data != null && data.instance_map != null) {
            if (src_name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleInstanceNameDec>> entry : data.instance_map.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleInstanceNameDec> set = data.instance_map.get(src_name);
                if (set != null)
                    result.addAll(set);
            }
        }
        // 如果已经找到，那么就直接返回
        if (!result.isEmpty()) return result;

        // 从全局找
        if (find_in_global) {
            if (src_name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleInstanceNameDec>> entry : listener.m_instance_map.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleInstanceNameDec> set = listener.m_instance_map.get(src_name);
                if (set != null)
                    result.addAll(set);
            }
        }

        return result;
    }

    public static void handleFileDelete(Project project, ALittleFile alittleFile) {
        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return;

        if (!listener.m_reload_ed) listener.reload();

        List<ALittleNamespaceDec> namespace_dec_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
        for (ALittleNamespaceDec namespace_dec : namespace_dec_list) {

            ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
            if (namespace_name_dec == null) continue;

            listener.removeNamespaceName(namespace_name_dec.getText(), namespace_name_dec);
        }
    }

    public static void handleRefresh(Project project) {
        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return;
        if (listener.m_is_refresh) return;
        listener.m_is_refresh = true;
        listener.reload();
    }

    public static boolean isReloading(Project project) {
        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return true;
        if (listener.m_reload_ing) return true;
        return false;
    }

    public static void handleFileCreated(Project project, ALittleFile alittleFile) {
        ALittleTreeChangeListener listener = s_map.get(project);
        if (listener == null) return;
        if (!listener.m_reload_ed) listener.reload();

        List<ALittleNamespaceDec> namespace_dec_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
        for (ALittleNamespaceDec namespace_dec : namespace_dec_list) {

            ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
            if (namespace_name_dec == null) continue;

            listener.addNamespaceName(namespace_name_dec.getText(), namespace_name_dec);
        }
    }

    private Project m_project;
    private class Data
    {
        Map<String, Set<ALittleClassNameDec>> class_map;
        Map<String, Set<ALittleEnumNameDec>> enum_map;
        Map<String, Set<ALittleStructNameDec>> struct_map;
        Map<String, Set<ALittleInstanceNameDec>> instance_map;
        Map<String, Set<ALittleMethodNameDec>> global_method_map;

        void addALittleClassNameDec(ALittleClassNameDec name_dec) {
            if (class_map == null) {
                class_map = new HashMap<>();
            }
            String name_text = name_dec.getText();
            Set<ALittleClassNameDec> set = class_map.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                class_map.put(name_text, set);
            }
            set.add(name_dec);
        }

        void removeALittleClassNameDec(ALittleClassNameDec name_dec) {
            if (class_map == null) return;
            String name_text = name_dec.getText();
            Set<ALittleClassNameDec> set = class_map.get(name_text);
            if (set == null) return;
            set = class_map.get(name_text);
            set.remove(name_dec);
            if (set.isEmpty()) class_map.remove(name_text);
        }

        void addALittleEnumNameDec(ALittleEnumNameDec name_dec) {
            if (enum_map == null) {
                enum_map = new HashMap<>();
            }
            String name_text = name_dec.getText();
            Set<ALittleEnumNameDec> set = enum_map.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                enum_map.put(name_text, set);
            }
            set.add(name_dec);
        }

        void removeALittleEnumNameDec(ALittleEnumNameDec name_dec) {
            if (class_map == null) return;
            String name_text = name_dec.getText();
            Set<ALittleEnumNameDec> set = enum_map.get(name_text);
            if (set == null) return;
            set = enum_map.get(name_text);
            set.remove(name_dec);
            if (set.isEmpty()) enum_map.remove(name_text);
        }

        void addALittleStructNameDec(ALittleStructNameDec name_dec) {
            if (struct_map == null) {
                struct_map = new HashMap<>();
            }
            String name_text = name_dec.getText();
            Set<ALittleStructNameDec> set = struct_map.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                struct_map.put(name_text, set);
            }
            set.add(name_dec);
        }

        void removeALittleStructNameDec(ALittleStructNameDec name_dec) {
            if (struct_map == null) return;
            String name_text = name_dec.getText();
            Set<ALittleStructNameDec> set = struct_map.get(name_text);
            if (set == null) return;
            set = struct_map.get(name_text);
            set.remove(name_dec);
            if (set.isEmpty()) struct_map.remove(name_text);
        }

        void addALittleGlobalMethodNameDec(ALittleMethodNameDec name_dec) {
            if (global_method_map == null) {
                global_method_map = new HashMap<>();
            }
            String name_text = name_dec.getText();
            Set<ALittleMethodNameDec> set = global_method_map.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                global_method_map.put(name_text, set);
            }
            set.add(name_dec);
        }

        void removeALittleGlobalMethodNameDec(ALittleMethodNameDec name_dec) {
            if (global_method_map == null) return;
            String name_text = name_dec.getText();
            Set<ALittleMethodNameDec> set = global_method_map.get(name_text);
            if (set == null) return;
            set = global_method_map.get(name_text);
            set.remove(name_dec);
            if (set.isEmpty()) global_method_map.remove(name_text);
        }

        void addALittleInstanceNameDec(ALittleInstanceNameDec name_dec) {
            if (instance_map == null) {
                instance_map = new HashMap<>();
            }
            String name_text = name_dec.getText();
            Set<ALittleInstanceNameDec> set = instance_map.get(name_text);
            if (set == null) {
                set = new HashSet<>();
                instance_map.put(name_text, set);
            }
            set.add(name_dec);
        }

        void removeALittleInstanceNameDec(ALittleInstanceNameDec name_dec) {
            if (instance_map == null) return;
            String name_text = name_dec.getText();
            Set<ALittleInstanceNameDec> set = instance_map.get(name_text);
            if (set == null) return;
            set = instance_map.get(name_text);
            set.remove(name_dec);
            if (set.isEmpty()) instance_map.remove(name_text);
        }
    }

    // 用于保存命名域对应的命名域对象
    private Map<String, Map<ALittleNamespaceNameDec, Data>> m_namespace_map;
    private Map<String, Data> m_data_map;
    // 全局命名域下的单例
    private Map<String, Set<ALittleInstanceNameDec>> m_instance_map;
    // 已加载的文件列表
    boolean m_reload_ing = false;
    boolean m_reload_ed = false;
    boolean m_is_refresh = false;

    public ALittleTreeChangeListener(Project project) {
        m_project = project;
    }

    public void reload() {
        m_namespace_map = new HashMap<>();
        m_data_map = new HashMap<>();
        m_instance_map = new HashMap<>();
        m_reload_ing = true;

        // 遍历所有文件，预加载所有内容
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ALittleFileType.INSTANCE, GlobalSearchScope.allScope(m_project));

        // 加载标准库
        String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);
        VirtualFile dir = null;

        try {
            if (jarPath.endsWith(".jar"))
                dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "std"));
            else
                dir = VfsUtil.findFileByIoFile(new File(jarPath + "/std"), true);

            if (dir != null) {
                for (VirtualFile child_dir : dir.getChildren()) {
                    virtualFiles.addAll(Arrays.asList(child_dir.getChildren()));
                }
            }
        } catch (MalformedURLException e) {
        }

        // 加载所有代码文件
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(m_project).findFile(virtualFile);
            if (!(file instanceof ALittleFile)) continue;

            List<ALittleNamespaceDec> namespace_dec_list = PsiTreeUtil.getChildrenOfTypeAsList(file, ALittleNamespaceDec.class);
            for (ALittleNamespaceDec namespace_dec : namespace_dec_list) {

                ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
                if (namespace_name_dec == null) continue;

                addNamespaceName(namespace_name_dec.getText(), namespace_name_dec);
            }
        }

        m_reload_ing = false;

        if (!m_reload_ed)
            PsiManager.getInstance(m_project).addPsiTreeChangeListener(this);
        m_reload_ed = true;
    }

    private void addNamespaceName(String name, ALittleNamespaceNameDec element) {
        Map<ALittleNamespaceNameDec, Data> map = m_namespace_map.get(name);
        if (map == null) {
            map = new HashMap<>();
            m_namespace_map.put(name, map);
        }

        Data data = new Data();

        Data fast_data = m_data_map.get(name);
        if (fast_data == null) {
            fast_data = new Data();
            m_data_map.put(name, fast_data);
        }

        ALittleNamespaceDec namespace_dec = (ALittleNamespaceDec)element.getParent();
        for(PsiElement child = namespace_dec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassDec) {
                ALittleClassDec dec = (ALittleClassDec)child;
                ALittleClassNameDec name_dec = dec.getClassNameDec();
                if (name_dec == null) continue;
                data.addALittleClassNameDec(name_dec);
                fast_data.addALittleClassNameDec(name_dec);
            } else if (child instanceof ALittleEnumDec) {
                ALittleEnumDec dec = (ALittleEnumDec)child;
                ALittleEnumNameDec name_dec = dec.getEnumNameDec();
                if (name_dec == null) continue;
                data.addALittleEnumNameDec(name_dec);
                fast_data.addALittleEnumNameDec(name_dec);
            } else if (child instanceof ALittleStructDec) {
                ALittleStructDec dec = (ALittleStructDec)child;
                ALittleStructNameDec name_dec = dec.getStructNameDec();
                if (name_dec == null) continue;
                data.addALittleStructNameDec(name_dec);
                fast_data.addALittleStructNameDec(name_dec);
            } else if (child instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)child;
                ALittleMethodNameDec name_dec = dec.getMethodNameDec();
                if (name_dec == null) continue;
                data.addALittleGlobalMethodNameDec(name_dec);
                fast_data.addALittleGlobalMethodNameDec(name_dec);
            } else if (child instanceof ALittleInstanceDec) {
                ALittleInstanceDec dec = (ALittleInstanceDec)child;
                ALittleInstanceNameDec name_dec = dec.getInstanceNameDec();
                if (name_dec == null) continue;

                ALittleAccessModifier access = dec.getAccessModifier();
                if (access != null && access.getText().equals("public")) {
                    String name_text = name_dec.getText();
                    Set<ALittleInstanceNameDec> set = m_instance_map.get(name_text);
                    if (set == null) {
                        set = new HashSet<>();
                        m_instance_map.put(name_text, set);
                    }
                    set.add(name_dec);
                } else {
                    fast_data.addALittleInstanceNameDec(name_dec);
                }
                data.addALittleInstanceNameDec(name_dec);
            }
        }
        map.put(element, data);
    }

    private void removeNamespaceName(String name, ALittleNamespaceNameDec element) {
        Data fast_data = m_data_map.get(name);
        Map<ALittleNamespaceNameDec, Data> map = m_namespace_map.get(name);
        if (map == null) return;

        Data data = map.get(element);
        if (data == null) return;

            if (fast_data != null) {
                if (data.class_map != null) {
                    for (Map.Entry<String, Set<ALittleClassNameDec>> entry : data.class_map.entrySet()) {
                        for (ALittleClassNameDec name_dec : entry .getValue()) {
                            fast_data.removeALittleClassNameDec(name_dec);
                        }
                    }
                }
                if (data.enum_map != null) {
                    for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : data.enum_map.entrySet()) {
                        for (ALittleEnumNameDec name_dec : entry .getValue()) {
                            fast_data.removeALittleEnumNameDec(name_dec);
                        }
                    }
                }
                if (data.struct_map != null) {
                    for (Map.Entry<String, Set<ALittleStructNameDec>> entry : data.struct_map.entrySet()) {
                        for (ALittleStructNameDec name_dec : entry .getValue()) {
                            fast_data.removeALittleStructNameDec(name_dec);
                        }
                    }
                }
                if (data.global_method_map != null) {
                    for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : data.global_method_map.entrySet()) {
                        for (ALittleMethodNameDec name_dec : entry .getValue()) {
                            fast_data.removeALittleGlobalMethodNameDec(name_dec);
                        }
                    }
                }
                if (data.instance_map != null) {
                    for (Map.Entry<String, Set<ALittleInstanceNameDec>> entry_instance : data.instance_map.entrySet()) {
                        for (ALittleInstanceNameDec name_dec : entry_instance.getValue()) {
                            fast_data.removeALittleInstanceNameDec(name_dec);

                            String name_text = name_dec.getText();
                            Set<ALittleInstanceNameDec> set = m_instance_map.get(name_text);
                            if (set != null) {
                                set.remove(name_dec);
                                if (set.isEmpty()) m_instance_map.remove(name_text);
                            }
                        }
                    }
                }
            }
        map.remove(element);
        if (map.isEmpty()) m_namespace_map.remove(name);
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
        if (file instanceof ALittleFile) {
            ALittleFile alittleFile = (ALittleFile)file;
            List<ALittleNamespaceDec> namespace_dec_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
            for (ALittleNamespaceDec namespace_dec : namespace_dec_list) {

                ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
                if (namespace_name_dec == null) continue;

                removeNamespaceName(namespace_name_dec.getText(), namespace_name_dec);
            }
        }
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
        if (file instanceof ALittleFile) {
            ALittleFile alittleFile = (ALittleFile)file;
            List<ALittleNamespaceDec> namespace_dec_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
            for (ALittleNamespaceDec namespace_dec : namespace_dec_list) {

                ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
                if (namespace_name_dec == null) continue;

                addNamespaceName(namespace_name_dec.getText(), namespace_name_dec);
            }
        }
    }

    public void childMoved(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void propertyChanged(@NotNull PsiTreeChangeEvent var1)
    {
    }
}
