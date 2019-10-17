package plugin.index;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.StdLibraryProvider;
import plugin.csv.ALittleCsvDataManager;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.mysql.ALittleMysqlDataManager;
import plugin.psi.*;

import java.io.File;
import java.util.*;

public class ALittleIndex {
    // 对应的项目
    Project mProject;

    // 保存关键的元素对象，用于快速语法树解析
    protected Map<PsiFile, Map<PsiElement, List<ALittleGuess>>> mGuessTypeMap;
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
    boolean mIsRefreshed = false;       // 是否刷新过

    // Csv数据联动
    protected Map<String, HashSet<ALittleStructDec>>  mCsvStructSet;                        // 收集csv路径对应的struct集合
    // Mysql数据联动
    protected Map<String, HashSet<ALittleStructDec>>  mMysqlStructSet;                      // 收集mysql路径对应的struct集合

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
        mReloaded = true;
    }

    public void refresh() {
        if (mIsRefreshed) return;
        mIsRefreshed = true;
        reload();
    }

    private void addClassData(@NotNull ALittleClassDec classDec) {
        ALittleClassNameDec nameDec = classDec.getClassNameDec();
        if (nameDec == null) return;
        Map<String, ALittleClassData> map = mClassDataMap.get(classDec.getContainingFile().getOriginalFile());
        if (map == null) {
            map = new HashMap<>();
            mClassDataMap.put(classDec.getContainingFile().getOriginalFile(), map);
        }
        ALittleClassData classData = new ALittleClassData();
        map.put(nameDec.getText(), classData);

        for (PsiElement child = classDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            classData.addALittleClassChildDec(child);
        }
    }

    public ALittleClassData getClassData(@NotNull ALittleClassDec dec) {
        ALittleClassNameDec nameDec = dec.getClassNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleClassData> map = mClassDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    private void addStructData(@NotNull ALittleStructDec structDec) {
        ALittleStructNameDec nameDec = structDec.getStructNameDec();
        if (nameDec == null) return;
        Map<String, ALittleStructData> map = mStructDataMap.get(structDec.getContainingFile().getOriginalFile());
        if (map == null) {
            map = new HashMap<>();
            mStructDataMap.put(structDec.getContainingFile().getOriginalFile(), map);
        }
        ALittleStructData structData = new ALittleStructData();
        map.put(nameDec.getText(), structData);

        for (PsiElement child = structDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleStructVarDec) {
                structData.addVarDec((ALittleStructVarDec)child);
            }
        }
    }

    public ALittleStructData getStructData(@NotNull ALittleStructDec dec) {
        ALittleStructNameDec nameDec = dec.getStructNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleStructData> map = mStructDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    private void addEnumData(@NotNull ALittleEnumDec enumDec) {
        ALittleEnumNameDec nameDec = enumDec.getEnumNameDec();
        if (nameDec == null) return;
        Map<String, ALittleEnumData> map = mEnumDataMap.get(enumDec.getContainingFile().getOriginalFile());
        if (map == null) {
            map = new HashMap<>();
            mEnumDataMap.put(enumDec.getContainingFile().getOriginalFile(), map);
        }
        ALittleEnumData enumData = new ALittleEnumData();
        map.put(nameDec.getText(), enumData);

        for (PsiElement child = enumDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleEnumVarDec) {
                enumData.addVarDec((ALittleEnumVarDec)child);
            }
        }
    }

    public ALittleEnumData getEnumData(@NotNull ALittleEnumDec dec) {
        ALittleEnumNameDec nameDec = dec.getEnumNameDec();
        if (nameDec == null) return null;
        Map<String, ALittleEnumData> map = mEnumDataMap.get(dec.getContainingFile().getOriginalFile());
        if (map == null) return null;
        return map.get(nameDec.getText());
    }

    public void addCsvData(@NotNull ALittleStructDec structDec) {
        // 检查修饰符
        ALittleCsvModifier csvModifier = structDec.getCsvModifier();
        if (csvModifier == null) return;
        PsiElement pathElement = csvModifier.getStringContent();
        if (pathElement == null) return;
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);

        HashSet<ALittleStructDec> set = mCsvStructSet.get(path);
        if (set == null) {
            set = new HashSet<>();
            mCsvStructSet.put(path, set);
        }
        set.add(structDec);

        // 变化
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                try {
                    ALittleCsvDataManager.checkAndChangeForStruct(structDec);
                    ALittleMysqlDataManager.checkAndChange(structDec);
                } catch (ALittleGuessException ignored) {

                }
            }
        });
    }

    public HashSet<ALittleStructDec> getCsvData(@NotNull String path) {
        return mCsvStructSet.get(path);
    }

    public void removeCsvData(@NotNull ALittleStructDec structDec) {
        // 检查修饰符
        ALittleCsvModifier csvModifier = structDec.getCsvModifier();
        if (csvModifier == null) return;
        PsiElement pathElement = csvModifier.getStringContent();
        if (pathElement == null) return;
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);

        HashSet<ALittleStructDec> set = mCsvStructSet.get(path);
        if (set == null) return;
        set.remove(structDec);
        if (!set.isEmpty()) return;
        mCsvStructSet.remove(path);
    }

    public void addMysqlData(@NotNull ALittleStructDec structDec) {
        // 检查修饰符
        ALittleMysqlModifier mysqlModifier = structDec.getMysqlModifier();
        if (mysqlModifier == null) return;
        PsiElement pathElement = mysqlModifier.getStringContent();
        if (pathElement == null) return;
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);

        HashSet<ALittleStructDec> set = mMysqlStructSet.get(path);
        if (set == null) {
            set = new HashSet<>();
            mMysqlStructSet.put(path, set);
        }
        set.add(structDec);

        try {
            if (mIsRefreshed)
            ALittleMysqlDataManager.checkAndChange(structDec);
        } catch (ALittleGuessException ignored) {

        }
    }

    public HashSet<ALittleStructDec> getMysqlData(@NotNull String path) {
        return mMysqlStructSet.get(path);
    }

    public void removeMysqlData(@NotNull ALittleStructDec structDec) {
        // 检查修饰符
        ALittleCsvModifier csvModifier = structDec.getCsvModifier();
        if (csvModifier == null) return;
        PsiElement pathElement = csvModifier.getStringContent();
        if (pathElement == null) return;
        String path = pathElement.getText();
        path = path.substring(1, path.length() - 1);

        HashSet<ALittleStructDec> set = mMysqlStructSet.get(path);
        if (set == null) return;
        set.remove(structDec);
        if (!set.isEmpty()) return;
        mMysqlStructSet.remove(path);
    }

    public void addNamespaceName(@NotNull ALittleNamespaceNameDec element) {
        // 清除标记
        mGuessTypeMap.remove(element.getContainingFile().getOriginalFile());
        mClassDataMap.remove(element.getContainingFile().getOriginalFile());
        mStructDataMap.remove(element.getContainingFile().getOriginalFile());
        mEnumDataMap.remove(element.getContainingFile().getOriginalFile());

        String namespaceName = element.getText();

        // 处理mNamespaceMap
        Map<ALittleNamespaceNameDec, ALittleAccessData> allDataMap = mAllDataMap.get(namespaceName);
        if (allDataMap == null) {
            allDataMap = new HashMap<>();
            mAllDataMap.put(namespaceName, allDataMap);
        }

        ALittleAccessData allAccessData = new ALittleAccessData();
        allDataMap.put(element, allAccessData);

        ALittleAccessData globalAccessData = mGlobalAccessMap.get(namespaceName);
        if (globalAccessData == null) {
            globalAccessData = new ALittleAccessData();
            mGlobalAccessMap.put(namespaceName, globalAccessData);
        }

        ALittleAccessData namespaceAccessData = mNamespaceAccessMap.get(namespaceName);
        if (namespaceAccessData == null) {
            namespaceAccessData = new ALittleAccessData();
            mNamespaceAccessMap.put(namespaceName, namespaceAccessData);
        }

        ALittleAccessData fileAccessData = mFileAccessMap.get(element.getContainingFile().getOriginalFile());
        if (fileAccessData == null) {
            fileAccessData = new ALittleAccessData();
            mFileAccessMap.put(element.getContainingFile().getOriginalFile(), fileAccessData);
        }

        ALittleNamespaceDec namespaceDec = (ALittleNamespaceDec)element.getParent();
        for(PsiElement child = namespaceDec.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleClassDec) {
                ALittleClassDec dec = (ALittleClassDec)child;
                ALittleClassNameDec nameDec = dec.getClassNameDec();
                if (nameDec == null) continue;

                addClassData(dec);
                allAccessData.addALittleNameDec(nameDec);
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addALittleNameDec(nameDec);
                }
            } else if (child instanceof ALittleEnumDec) {
                ALittleEnumDec dec = (ALittleEnumDec)child;
                ALittleEnumNameDec nameDec = dec.getEnumNameDec();
                if (nameDec == null) continue;

                addEnumData(dec);
                allAccessData.addALittleNameDec(nameDec);
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addALittleNameDec(nameDec);
                }
            } else if (child instanceof ALittleStructDec) {
                ALittleStructDec dec = (ALittleStructDec)child;
                ALittleStructNameDec nameDec = dec.getStructNameDec();
                if (nameDec == null) continue;

                addCsvData(dec);
                addMysqlData(dec);
                addStructData(dec);
                allAccessData.addALittleNameDec(nameDec);
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addALittleNameDec(nameDec);
                }
            } else if (child instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)child;
                ALittleMethodNameDec nameDec = dec.getMethodNameDec();
                if (nameDec == null) continue;

                allAccessData.addALittleNameDec(nameDec);
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addALittleNameDec(nameDec);
                }
            } else if (child instanceof ALittleInstanceDec) {
                ALittleInstanceDec dec = (ALittleInstanceDec) child;
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());

                List<ALittleVarAssignDec> varAssignDecList = dec.getVarAssignExpr().getVarAssignDecList();
                for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                    ALittleVarAssignNameDec nameDec = varAssignDec.getVarAssignNameDec();

                    allAccessData.addALittleNameDec(nameDec);
                    if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                        globalAccessData.addALittleNameDec(nameDec);
                    } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                        namespaceAccessData.addALittleNameDec(nameDec);
                    } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                        fileAccessData.addALittleNameDec(nameDec);
                    }
                }
            } else if (child instanceof ALittleUsingDec) {
                ALittleUsingDec dec = (ALittleUsingDec) child;
                PsiHelper.ClassAccessType accessType = PsiHelper.calcAccessType(dec.getAccessModifier());

                ALittleUsingNameDec nameDec = dec.getUsingNameDec();
                if (nameDec == null) continue;

                allAccessData.addALittleNameDec(nameDec);
                if (accessType == PsiHelper.ClassAccessType.PUBLIC) {
                    globalAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
                    namespaceAccessData.addALittleNameDec(nameDec);
                } else if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
                    fileAccessData.addALittleNameDec(nameDec);
                }
            }
        }
    }

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
                    if (globalAccessData != null) globalAccessData.removeALittleNameDec(nameDec);
                    if (namespaceAccessData != null) namespaceAccessData.removeALittleNameDec(nameDec);
                    if (fileAccessData != null) fileAccessData.removeALittleNameDec(nameDec);
                    if (nameDec instanceof ALittleStructNameDec) {
                        removeCsvData((ALittleStructDec)nameDec.getParent());
                        removeMysqlData((ALittleStructDec)nameDec.getParent());
                    }
                }
            }
        }
    }
}
