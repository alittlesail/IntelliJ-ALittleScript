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
import gherkin.lexer.Da;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import javax.sound.midi.SysexMessage;
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
            for (Map.Entry<String, Map<ALittleNamespaceNameDec, Data>> entry : listener.mAllDataMap.entrySet()) {
                result.addAll(entry.getValue().keySet());
            }
        } else {
            Map<ALittleNamespaceNameDec, Data> map = listener.mAllDataMap.get(namespaceName);
            if (map != null)
                result.addAll(map.keySet());
        }
        return result;
    }

    public static List<ALittleClassNameDec> findClassNameDecList(Project project, PsiFile psiFile, String namespaceName, String name) {
        List<ALittleClassNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先查本文件的
        Data data = listener.mFileAccessMap.get(psiFile);
        if (data != null) {
            data.findClassNameDecList(name, result);
        }

        // 先查本命名域的
        data = listener.mNamespaceAccessMap.get(namespaceName);
        if (data != null) {
            data.findClassNameDecList(name, result);
        }

        // 查全局下
        data = listener.mGlobalAccessMap.get(namespaceName);
        if (data != null) {
            data.findClassNameDecList(name, result);
        }

        return result;
    }

    public static List<ALittleStructNameDec> findStructNameDecList(Project project, PsiFile psiFile, String namespaceName, String name) {
        List<ALittleStructNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先查本文件的
        Data data = listener.mFileAccessMap.get(psiFile);
        if (data != null) {
            data.findStructNameDecList(name, result);
        }

        // 先查本命名域的
        data = listener.mNamespaceAccessMap.get(namespaceName);
        if (data != null) {
            data.findStructNameDecList(name, result);
        }

        // 查全局下
        data = listener.mGlobalAccessMap.get(namespaceName);
        if (data != null) {
            data.findStructNameDecList(name, result);
        }

        return result;
    }

    public static List<ALittleEnumNameDec> findEnumNameDecList(Project project, PsiFile psiFile, String namespaceName, String name) {
        List<ALittleEnumNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先查本文件的
        Data data = listener.mFileAccessMap.get(psiFile);
        if (data != null) {
            data.findEnumNameDecList(name, result);
        }

        // 先查本命名域的
        data = listener.mNamespaceAccessMap.get(namespaceName);
        if (data != null) {
            data.findEnumNameDecList(name, result);
        }

        // 查全局下
        data = listener.mGlobalAccessMap.get(namespaceName);
        if (data != null) {
            data.findEnumNameDecList(name, result);
        }

        return result;
    }

    public static List<ALittleMethodNameDec> findGlobalMethodNameDecList(Project project, PsiFile psiFile, String namespaceName, String name) {
        List<ALittleMethodNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先查本文件的
        Data data = listener.mFileAccessMap.get(psiFile);
        if (data != null) {
            data.findGlobalMethodNameDecList(name, result);
        }

        // 先查本命名域的
        data = listener.mNamespaceAccessMap.get(namespaceName);
        if (data != null) {
            data.findGlobalMethodNameDecList(name, result);
        }

        // 查全局下
        data = listener.mGlobalAccessMap.get(namespaceName);
        if (data != null) {
            data.findGlobalMethodNameDecList(name, result);
        }

        return result;
    }

    public static List<ALittleVarAssignNameDec> findInstanceNameDecList(Project project, PsiFile psiFile, String namespaceName, String name, boolean findInGlobal) {
        List<ALittleVarAssignNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return result;
        if (!listener.mReloaded) listener.reload();

        // 先查本文件的
        Data data = listener.mFileAccessMap.get(psiFile);
        if (data != null) {
            data.findInstanceNameDecList(name, result);
        }

        // 先查本命名域的
        data = listener.mNamespaceAccessMap.get(namespaceName);
        if (data != null) {
            data.findInstanceNameDecList(name, result);
        }

        // 查全局下
        if (findInGlobal) {
            for (Data instData : listener.mGlobalAccessMap.values())
                instData.findInstanceNameDecList(name, result);
        }

        return result;
    }

    public static List<ALittleReferenceUtil.GuessTypeInfo> getGuessTypeList(PsiElement element) {
        ALittleTreeChangeListener listener = sMap.get(element.getProject());
        if (listener == null) return null;
        if (!listener.mReloaded) listener.reload();

        Map<PsiElement, List<ALittleReferenceUtil.GuessTypeInfo>> map = listener.mGuessTypeMap.get(element.getContainingFile());
        if (map == null) return null;

        return map.get(element);
    }

    public static void putGuessTypeList(PsiElement element, @NotNull List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList) {
        ALittleTreeChangeListener listener = sMap.get(element.getProject());
        if (listener == null) return;
        if (!listener.mReloaded) listener.reload();

        Map<PsiElement, List<ALittleReferenceUtil.GuessTypeInfo>> map = listener.mGuessTypeMap.get(element.getContainingFile());
        if (map == null) {
            map = new HashMap<>();
            listener.mGuessTypeMap.put(element.getContainingFile(), map);
        }

        map.put(element, guessTypeList);
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

        public Data() {
            classMap = new HashMap<>();
            enumMap = new HashMap<>();
            structMap = new HashMap<>();
            instanceMap = new HashMap<>();
            globalMethodMap = new HashMap<>();
        }

        public void addALittleClassNameDec(ALittleClassNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                classMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        public void findClassNameDecList(String name, @NotNull List<ALittleClassNameDec> result) {
            if (name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleClassNameDec>> entry : classMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleClassNameDec> set = classMap.get(name);
                if (set != null) {
                    result.addAll(set);
                }
            }
        }

        public void removeALittleClassNameDec(ALittleClassNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleClassNameDec> set = classMap.get(nameText);
            if (set == null) return;
            set.remove(nameDec);
            if (set.isEmpty()) classMap.remove(nameText);
        }

        public void addALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                enumMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        public void removeALittleEnumNameDec(ALittleEnumNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleEnumNameDec> set = enumMap.get(nameText);
            if (set == null) return;
            set.remove(nameDec);
            if (set.isEmpty()) enumMap.remove(nameText);
        }

        public void findEnumNameDecList(String name, @NotNull List<ALittleEnumNameDec> result) {
            if (name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleEnumNameDec>> entry : enumMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleEnumNameDec> set = enumMap.get(name);
                if (set != null) {
                    result.addAll(set);
                }
            }
        }

        public void addALittleStructNameDec(ALittleStructNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                structMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        public void removeALittleStructNameDec(ALittleStructNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleStructNameDec> set = structMap.get(nameText);
            if (set == null) return;
            set.remove(nameDec);
            if (set.isEmpty()) structMap.remove(nameText);
        }

        public void findStructNameDecList(String name, @NotNull List<ALittleStructNameDec> result) {
            if (name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleStructNameDec>> entry : structMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleStructNameDec> set = structMap.get(name);
                if (set != null) {
                    result.addAll(set);
                }
            }
        }

        public void addALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                globalMethodMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        public void removeALittleGlobalMethodNameDec(ALittleMethodNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleMethodNameDec> set = globalMethodMap.get(nameText);
            if (set == null) return;
            set.remove(nameDec);
            if (set.isEmpty()) globalMethodMap.remove(nameText);
        }

        public void findGlobalMethodNameDecList(String name, @NotNull List<ALittleMethodNameDec> result) {
            if (name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleMethodNameDec>> entry : globalMethodMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleMethodNameDec> set = globalMethodMap.get(name);
                if (set != null) {
                    result.addAll(set);
                }
            }
        }

        public void addALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(nameText);
            if (set == null) {
                set = new HashSet<>();
                instanceMap.put(nameText, set);
            }
            set.add(nameDec);
        }

        public void removeALittleInstanceNameDec(ALittleVarAssignNameDec nameDec) {
            String nameText = nameDec.getText();
            Set<ALittleVarAssignNameDec> set = instanceMap.get(nameText);
            if (set == null) return;
            set.remove(nameDec);
            if (set.isEmpty()) instanceMap.remove(nameText);
        }

        public void findInstanceNameDecList(String name, @NotNull List<ALittleVarAssignNameDec> result) {
            if (name.isEmpty()) {
                for (Map.Entry<String, Set<ALittleVarAssignNameDec>> entry : instanceMap.entrySet()) {
                    result.addAll(entry.getValue());
                }
            } else {
                Set<ALittleVarAssignNameDec> set = instanceMap.get(name);
                if (set != null) {
                    result.addAll(set);
                }
            }
        }
    }
    // 保存关键的元素对象，用于快速语法树解析
    private Map<PsiFile, Map<PsiElement, List<ALittleReferenceUtil.GuessTypeInfo>>> mGuessTypeMap;

    // key是命名域名称，value是这个命名域下所有的内容，这里的数据是最全的
    private Map<String, Map<ALittleNamespaceNameDec, Data>> mAllDataMap;

    // 全局可访问，key是命名域
    private Map<String, Data> mGlobalAccessMap;
    // 某个命名域下可访问，key是命名域
    private Map<String, Data> mNamespaceAccessMap;
    // 某个文件下可访问
    private Map<PsiFile, Data> mFileAccessMap;

    // 已加载的文件列表
    boolean mReloading = false;         // 是否正在加载
    boolean mReloaded = false;          // 是否加载完成
    boolean mIsRefreshed = false;       // 是否刷新过

    public ALittleTreeChangeListener(Project project) {
        mProject = project;
        mAllDataMap = new HashMap<>();
        mGlobalAccessMap = new HashMap<>();
        mNamespaceAccessMap = new HashMap<>();
        mFileAccessMap = new HashMap<>();
        mGuessTypeMap = new HashMap<>();
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
