package plugin.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.csv.ALittleCsvDataManager;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

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

        Map<PsiElement, List<ALittleGuess>> map = listener.mGuessTypeMap.get(element.getContainingFile().getOriginalFile());
        if (map == null) {
            map = new HashMap<>();
            listener.mGuessTypeMap.put(element.getContainingFile().getOriginalFile(), map);
        }

        map.put(element, guessTypeList);
    }

    @NotNull
    public static List<ALittleNamespaceNameDec> findNamespaceNameDecList(Project project, String namespaceName) {
        List<ALittleNamespaceNameDec> result = new ArrayList<>();

        ALittleTreeChangeListener listener = getListener(project);
        if (listener == null) return result;

        if (namespaceName.isEmpty()) {
            for (Map.Entry<String, Map<ALittleNamespaceNameDec, ALittleAccessData>> entry : listener.mAllDataMap.entrySet()) {
                result.addAll(entry.getValue().keySet());
            }
        } else {
            Map<ALittleNamespaceNameDec, ALittleAccessData> map = listener.mAllDataMap.get(namespaceName);
            if (map != null)
                result.addAll(map.keySet());
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
                data.findALittleNameDecList(type, name, result);
            }
        }

        // 查本命名域的
        if (fileNamespaceName.equals(namespaceName)) {
            data = listener.mNamespaceAccessMap.get(namespaceName);
            if (data != null) {
                data.findALittleNameDecList(type, name, result);
            }
        }

        // 查全局下
        if (findInGlobal) {
            if (type == PsiHelper.PsiElementType.INSTANCE_NAME) {
                for (ALittleAccessData accessData : listener.mGlobalAccessMap.values()) {
                    accessData.findALittleNameDecList(type, name, result);
                }
            } else {
                data = listener.mGlobalAccessMap.get(namespaceName);
                if (data != null) {
                    data.findALittleNameDecList(type, name, result);
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

        classData.findClassAttrList(classDec, accessLevel, attrType, name, result);
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
        listener.refresh();
    }

    public static boolean isReloading(Project project) {
        ALittleTreeChangeListener listener = sMap.get(project);
        if (listener == null) return false;
        if (listener.isLoading()) return true;
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// 静态通用函数结束
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(file);
        if (namespaceNameDec == null) return;
        removeNamespaceName(namespaceNameDec);
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
        ALittleNamespaceNameDec namespaceNameDec = PsiHelper.getNamespaceNameDec(file);
        if (namespaceNameDec == null) return;
        addNamespaceName(namespaceNameDec);
    }

    public void childMoved(@NotNull PsiTreeChangeEvent var1)
    {
    }

    public void propertyChanged(@NotNull PsiTreeChangeEvent var1)
    {
    }
}
