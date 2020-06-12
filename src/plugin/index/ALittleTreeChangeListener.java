package plugin.index;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.sqlite.util.StringUtils;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.io.File;
import java.util.*;

public class ALittleTreeChangeListener extends ALittleIndex implements PsiTreeChangeListener {
    public ALittleTreeChangeListener(@NotNull Project project) {
        super(project);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// 静态通用函数开始
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Map<Project, ALittleTreeChangeListener> sMap = new HashMap<>();

    public static ALittleTreeChangeListener getListener(Project project) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return null;

        if (!listener.isLoaded()) {
            listener.reload();
            PsiManager.getInstance(project).addPsiTreeChangeListener(listener);
        }
        return listener;
    }

    public static List<ALittleGuess> getGuessTypeList(@NotNull PsiElement element) {
        ALittleTreeChangeListener listener = getListener(element.getProject());
        if (listener == null) return null;

        PsiFile psiFile = element.getContainingFile();
        if (psiFile == null) return null;
        psiFile = psiFile.getOriginalFile();
        Map<PsiElement, List<ALittleGuess>> map = listener.mGuessTypeMap.get(psiFile);
        if (map == null) return null;

        return map.get(element);
    }

    public static void putGuessTypeList(@NotNull PsiElement element, @NotNull List<ALittleGuess> guessTypeList) {
        ALittleTreeChangeListener listener = getListener(element.getProject());
        if (listener == null) return;

        Map<PsiElement, List<ALittleGuess>> map = listener.mGuessTypeMap.computeIfAbsent(element.getContainingFile().getOriginalFile(), k -> new HashMap<>());
        map.put(element, guessTypeList);
    }

    static class RelayInfo
    {
        public Set<RelayInfo> be_used_set; // 被依赖集合
        public Set<RelayInfo> use_set;    // 依赖集合

        public String path;
        public Set<String> relay_set;
        public String rel_path;
    }

    // 删除文件夹
    public static void getDeepFilePaths(Project project, File info, String parent_path, List<String> result) throws ALittleGuessException {
        if (!info.exists()) return;

        // 获取子级列表
        File[] list = info.listFiles();
        if (list == null) return;

        // 初始化依赖信息
        Map<String, RelayInfo> relay_map = new HashMap<>();
        for (File file : list) {
            if (!file.isDirectory()) {
                Set<String> relay_set = new HashSet<>();
                ALittleTreeChangeListener.findDefineRelay(project, file.getAbsolutePath(), relay_set);
                RelayInfo relay_info = new RelayInfo();
                relay_info.path = file.getAbsolutePath();
                relay_info.rel_path = parent_path + file.getName();
                relay_info.relay_set = relay_set;
                relay_info.be_used_set = new HashSet<>();
                relay_info.use_set = new HashSet<>();
                relay_map.put(relay_info.path, relay_info);
            }
        }

        // 形成通路
        for (RelayInfo relay_info : relay_map.values()) {
            for (String child_path : relay_info.relay_set) {
                RelayInfo child = relay_map.get(child_path);
                if (child == null) continue;
                child.be_used_set.add(relay_info);
                relay_info.use_set.add(child);
            }
        }

        // 都放进列表中，并排序
        List<RelayInfo> info_list = new ArrayList<>(relay_map.values());
        info_list.sort((RelayInfo a, RelayInfo b) -> { return a.path.compareTo(b.path); });

        // 遍历列表
        while (info_list.size() > 0) {
            // 用于接收未处理的列表
            List<RelayInfo> new_info_list = new ArrayList<>();
            // 遍历列表进行处理
            for (RelayInfo relay_info : info_list)
            {
                // 如果已经没有依赖了，那么就添加进result，然后解除依赖关系
                if (relay_info.use_set.size() == 0) {
                    result.add(relay_info.rel_path);
                    for (RelayInfo be_used_info : relay_info.be_used_set) {
                        be_used_info.use_set.remove(relay_info);
                    }
                    relay_info.be_used_set.clear();
                } else {
                     new_info_list.add(relay_info);
                }
            }
            // 如果一轮下来没有减少，那么就抛异常
            if (new_info_list.size() == info_list.size()) {
                String content = "";
                for (RelayInfo relayInfo : new_info_list) {
                    content += relayInfo.rel_path + " -> ";
                    for (RelayInfo use_info : relayInfo.use_set) {
                        content += use_info.rel_path;
                    }
                    content += ";";
                }
                throw new ALittleGuessException(null, "出现循环引用 " + content);
            }

            // 把收集的列表复制给info_list，进行下一轮循环
            info_list = new_info_list;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                getDeepFilePaths(project, file, parent_path + file.getName() + "/", result);
            }
        }
    }

    @NotNull
    public static void findDefineRelay(Project project, String file_path, Set<String> result) throws ALittleGuessException {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl("file://" + file_path);
        if (file == null) return;
        // 获取管理器
        PsiManager psi_mgr = PsiManager.getInstance(project);
        PsiFile psi_file = psi_mgr.findFile(file);
        if (!(psi_file instanceof ALittleFile)) return;
        ALittleNamespaceDec dec = PsiHelper.getNamespaceDec(psi_file);
        if (dec == null) return;
        List<ALittleNamespaceElementDec> element_dec_list =  dec.getNamespaceElementDecList();
        for (ALittleNamespaceElementDec element_dec : element_dec_list) {
            ALittleClassDec classDec = element_dec.getClassDec();
            if (classDec == null) continue;
            ALittleClassExtendsDec extendsDec = classDec.getClassExtendsDec();
            if (extendsDec == null) continue;
            ALittleGuess guess = extendsDec.guessType();
            if (!(guess instanceof ALittleGuessClass)) continue;
            PsiElement element = guess.getElement();
            if (element == null) return;
            result.add(element.getContainingFile().getOriginalFile().getVirtualFile().getPath());
        }
    }

    @NotNull
    public static Map<String, ALittleNamespaceNameDec> findNamespaceNameDecList(Project project, String namespaceName) {
        Map<String, ALittleNamespaceNameDec> result = new HashMap<>();

        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return result;

        if (namespaceName.isEmpty()) {
            for (Map.Entry<String, Map<ALittleNamespaceNameDec, ALittleAccessData>> entry : listener.mAllDataMap.entrySet()) {
                for (ALittleNamespaceNameDec dec : entry.getValue().keySet())
                    result.put(dec.getText(), dec);
            }
        } else {
            Map<ALittleNamespaceNameDec, ALittleAccessData> map = listener.mAllDataMap.get(namespaceName);
            if (map != null) {
                for (ALittleNamespaceNameDec dec : map.keySet())
                    result.put(dec.getText(), dec);
            }
        }
        return result;
    }

    @NotNull
    public static List<PsiElement> findALittleNameDecList(Project project, PsiHelper.PsiElementType type
            , PsiFile psiFile, String namespaceName, String name, boolean findInGlobal) {
        List<PsiElement> result = new ArrayList<>();

        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return result;

        ALittleAccessData data;
        // 查本文件的
        String fileNamespaceName = PsiHelper.getNamespaceName(psiFile);
        if (namespaceName.equals(fileNamespaceName)) {
            data = listener.mFileAccessMap.get(psiFile);
            if (data != null) {
                data.findNameDecList(type, name, result);
            }
        }

        // 查本命名域的
        if (fileNamespaceName.equals(namespaceName)) {
            data = listener.mNamespaceAccessMap.get(namespaceName);
            if (data != null) {
                data.findNameDecList(type, name, result);
            }
        }

        // 查全局下
        if (findInGlobal) {
            if (type == PsiHelper.PsiElementType.INSTANCE_NAME) {
                for (ALittleAccessData accessData : listener.mGlobalAccessMap.values()) {
                    accessData.findNameDecList(type, name, result);
                }
            } else {
                data = listener.mGlobalAccessMap.get(namespaceName);
                if (data != null) {
                    data.findNameDecList(type, name, result);
                }
            }
        }

        return result;
    }

    public static PsiElement findALittleNameDec(Project project, PsiHelper.PsiElementType type
            , PsiFile psiFile, String namespaceName, String name, boolean findInGlobal) {
        List<PsiElement> result = findALittleNameDecList(project, type, psiFile, namespaceName, name, findInGlobal);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public static List<ALittleGuess> findALittleStructGuessList(Project project,
                                                                String namespaceName,
                                                                String name) throws ALittleGuessException {
        PsiElement element = ALittleTreeChangeListener.findALittleNameDec(project,
                PsiHelper.PsiElementType.STRUCT_NAME, null, namespaceName, name, true);
        if (element instanceof ALittleStructNameDec) {
            return ((ALittleStructNameDec) element).guessTypes();
        }
        return new ArrayList<>();
    }

    public static ALittleGuess findALittleStructGuess(Project project, String namespaceName, String name) throws ALittleGuessException {
        PsiElement element = ALittleTreeChangeListener.findALittleNameDec(project,
                PsiHelper.PsiElementType.STRUCT_NAME, null, namespaceName, name, true);
        if (element instanceof ALittleStructNameDec) {
            return ((ALittleStructNameDec) element).guessType();
        }
        return null;
    }

    public static List<ALittleGuess> findALittleClassGuessList(Project project,
                                                               String namespaceName,
                                                               String name) throws ALittleGuessException {
        PsiElement element = ALittleTreeChangeListener.findALittleNameDec(project,
                PsiHelper.PsiElementType.CLASS_NAME, null, namespaceName, name, true);
        if (element instanceof ALittleClassNameDec) {
            return ((ALittleClassNameDec) element).guessTypes();
        }
        return new ArrayList<>();
    }

    public static void findClassAttrList(@NotNull ALittleClassDec classDec,
                                         int accessLevel,
                                         PsiHelper.ClassAttrType attrType,
                                         String name,
                                         @NotNull List<PsiElement> result) {
        ALittleTreeChangeListener listener = getListener(classDec.getProject());
        if (listener == null) return;

        ALittleClassNameDec nameDec = classDec.getClassNameDec();
        if (nameDec == null) return;

        PsiFile psiFile = classDec.getContainingFile().getOriginalFile();
        Map<String, ALittleClassData> map = listener.mClassDataMap.get(psiFile);
        if (map == null) return;
        ALittleClassData classData = map.get(nameDec.getText());
        if (classData == null) return;

        classData.findClassAttrList(accessLevel, attrType, name, result);
    }

    public static PsiElement findClassAttr(@NotNull ALittleClassDec classDec,
                                           int accessLevel,
                                           PsiHelper.ClassAttrType attrType,
                                           String name) {
        List<PsiElement> result = new ArrayList<>();
        findClassAttrList(classDec, accessLevel, attrType, name, result);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public static void handleDirDelete(Project project, VirtualFile virtualFile) {
        // 如果是文件夹
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
        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return;
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(alittleFile);
        if (namespaceNameDec == null) return;
        listener.removeNamespaceName(namespaceNameDec);
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
        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return;
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(alittleFile);
        if (namespaceNameDec == null) return;
        listener.addNamespaceName(namespaceNameDec);
    }

    public static void handleRefresh(Project project) {
        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return;
        listener.reload();
    }

    public static boolean isReloading(Project project) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return false;
        if (listener.isLoading()) return true;
        return false;
    }

    public void AddModule(@NotNull Module module) {
        PsiManager psi_mgr = PsiManager.getInstance(mProject);
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root : roots) {
            loadDir(psi_mgr, root);
        }
    }

    public void RemoveModule(@NotNull Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root : roots) {
            handleDirDelete(mProject, root);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// 静态通用函数结束
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void beforeChildAddition(@NotNull PsiTreeChangeEvent var1) {
    }

    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent var1) {
    }

    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent var1) {
    }

    public void beforeChildMovement(@NotNull PsiTreeChangeEvent var1) {
    }

    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent var1) {
        PsiFile file = var1.getFile();
        if (!(file instanceof ALittleFile)) return;
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(file);
        if (namespaceNameDec == null) return;
        removeNamespaceName(namespaceNameDec);
    }

    public void beforePropertyChange(@NotNull PsiTreeChangeEvent var1) {
    }

    public void childAdded(@NotNull PsiTreeChangeEvent var1) {
    }

    public void childRemoved(@NotNull PsiTreeChangeEvent var1) {
    }

    public void childReplaced(@NotNull PsiTreeChangeEvent var1) {
    }

    public void childrenChanged(@NotNull PsiTreeChangeEvent var1) {
        PsiFile file = var1.getFile();
        if (!(file instanceof ALittleFile)) return;
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(file);
        if (namespaceNameDec == null) return;
        addNamespaceName(namespaceNameDec);
    }

    public void childMoved(@NotNull PsiTreeChangeEvent var1) {
    }

    public void propertyChanged(@NotNull PsiTreeChangeEvent var1) {
    }
}
