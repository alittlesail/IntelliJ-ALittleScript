package plugin.alittle;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleClassData;
import plugin.index.ALittleEnumData;
import plugin.index.ALittleStructData;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsiHelper {
    // 判断字符串是不是整型值
    public static boolean isInt(@NotNull String content) {
        try {
            double v = Double.parseDouble(content);
            return v == Math.floor(v) && !content.contains(".");
        } catch (NumberFormatException e) {
            return true;
        }
    }

    // 计算哈希值
    public static int JSHash(@NotNull String content) {
        byte[] bytes = content.getBytes();
        int l = content.length();
        int h = l;
        int step = l >> 5 + 1;

        for (int i=l; i >= step; i-=step) {
            h = h ^ (h << 5 + bytes[i] + h >> 2);
        }
        return h;
    }

    // 类的属性类型
    public enum ClassAttrType
    {
        VAR,
        FUN,
        GETTER,
        SETTER,
        STATIC,
        TEMPLATE,
    }

    // 访问权限类型
    public enum ClassAccessType
    {
        PUBLIC,
        PROTECTED,
        PRIVATE,
    }

    // 元素类型
    public enum PsiElementType
    {
        CLASS_NAME,
        ENUM_NAME,
        STRUCT_NAME,
        INSTANCE_NAME,
        GLOBAL_METHOD,
    }

    // 获取访问权限类型
    public static ClassAccessType calcAccessType(ALittleAccessModifier accessModifier) {
        if (accessModifier == null || accessModifier.getText().equals("private")) {
            return ClassAccessType.PRIVATE;
        }
        if (accessModifier.getText().equals("protected")) {
            return ClassAccessType.PROTECTED;
        }
        return ClassAccessType.PUBLIC;
    }

    // 访问权限等级
    public static int sAccessOnlyPublic = 1;            // 可以访问public的属性和方法
    public static int sAccessProtectedAndPublic = 2;         // 可以访问public protected的属性和方法
    public static int sAccessPrivateAndProtectedAndPublic = 3;           // 可以public protected private的属性和方法

    // 计算访问权限等级
    public static int calcAccessLevel(int accessLevel, ClassAccessType accessType) {
        if (accessLevel > sAccessOnlyPublic) {
            if (accessType == ClassAccessType.PRIVATE) {
                accessLevel = sAccessOnlyPublic;
            } else if (accessType == ClassAccessType.PROTECTED) {
                --accessLevel;
            }
        }
        return accessLevel;
    }

    // 获取某个元素的命名域对象
    public static ALittleNamespaceNameDec getNamespaceNameDec(@NotNull PsiFile psiFile) {
        for(PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                return ((ALittleNamespaceDec) child).getNamespaceNameDec();
            }
        }
        return null;
    }

    // 获取某个元素的命名域对象
    public static ALittleNamespaceDec getNamespaceDec(@NotNull PsiFile psiFile) {
        for(PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                return ((ALittleNamespaceDec) child);
            }
        }
        return null;
    }

    // 获取某个元素的命名域
    @NotNull
    public static String getNamespaceName(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile().getOriginalFile();
        if (psiFile == null) return "";
        for(PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                ALittleNamespaceNameDec nameDec = ((ALittleNamespaceDec) child).getNamespaceNameDec();
                if (nameDec == null) return "";
                return nameDec.getText();
            }
        }
        return "";
    }

    // 获取这个元素所在的类
    public static ALittleClassDec findClassDecFromParent(@NotNull PsiElement dec) {
        while (dec != null) {
            if (dec instanceof ALittleClassDec) {
                return (ALittleClassDec)dec;
            }
            dec = dec.getParent();
        }
        return null;
    }

    // 检查是否在静态函数中
    public static boolean isInClassStaticMethod(@NotNull PsiElement dec) {

        PsiElement parent = dec;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleNamespaceDec) {
                return false;
            } else if (parent instanceof ALittleClassDec) {
                return false;
            } else if (parent instanceof ALittleClassCtorDec) {
                return false;
            } else if (parent instanceof ALittleClassSetterDec) {
                return false;
            } else if (parent instanceof ALittleClassMethodDec) {
                return false;
            } else if (parent instanceof ALittleClassStaticDec) {
                return true;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                return false;
            }

            parent = parent.getParent();
        }

        return false;
    }

    // 获取这个结构体的所有成员
    public static void findStructVarDecList(@NotNull ALittleStructDec structDec,
                                                String name,
                                                @NotNull List<ALittleStructVarDec> result,
                                                int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(structDec.getProject());
        if (listener == null) return;

        ALittleStructData data = listener.getStructData(structDec);
        if (data != null) {
            data.findVarDecList(name, result);
        }

        // 处理继承
        ALittleStructDec structExtendsDec = findStructExtends(structDec);
        if (structExtendsDec != null) {
            findStructVarDecList(structExtendsDec, name, result, deep - 1);
        }
    }

    public static void findEnumVarDecList(@NotNull ALittleEnumDec enumDec, String name, @NotNull List<ALittleEnumVarDec> result) {
        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(enumDec.getProject());
        if (listener == null) return;

        ALittleEnumData data = listener.getEnumData(enumDec);
        if (data != null) {
            data.findVarDecList(name, result);
        }
    }

    // 根据枚举字段名，获取对应的值
    public static Integer getEnumVarValue(ALittleEnumDec enumDec, String name) {
        if (enumDec == null) return null;

        // 初始值
        int enumValue = -1;

        List<ALittleEnumVarDec> varDecList = enumDec.getEnumVarDecList();
        for (ALittleEnumVarDec varDec : varDecList) {
            PsiElement varNameDec = varDec.getIdContent();
            if (varNameDec == null) continue;

            // 如果设定为数字，那么就要设置一下
            if (varDec.getDigitContent() != null) {
                String value = varDec.getDigitContent().getText();
                if (!isInt(value)) {
                    return null;
                }
                // 把字符串转为数值
                if (value.startsWith("0x"))
                    enumValue = Integer.parseInt(value.substring(2), 16);
                else
                    enumValue = Integer.parseInt(value);
                if (varNameDec.getText().equals(name)) {
                    return enumValue;
                }
                // 如果没有设定值，那么就默认+1
            } else if (varDec.getStringContent() == null) {
                ++enumValue;
                if (varNameDec.getText().equals(name)) {
                    return enumValue;
                }
            }
        }
        return null;
    }

    public static List<PsiElement> filterSameName(@NotNull List<PsiElement> list) {
        Map<String, PsiElement> map = new HashMap<>();
        for (int i = list.size() - 1; i >= 0; --i) {
            map.put(list.get(i).getText(), list.get(i));
        }
        if (map.size() == list.size()) return list;
        list = new ArrayList<>();
        list.addAll(map.values());
        return list;
    }

    // 计算struct的父类
    public static ALittleStructDec findStructExtends(@NotNull ALittleStructDec dec) {
        ALittleStructExtendsDec structExtendsDec = dec.getStructExtendsDec();
        if (structExtendsDec == null) return null;

        ALittleStructNameDec structNameDec = structExtendsDec.getStructNameDec();
        if (structNameDec == null) return null;

        String namespaceName;
        ALittleNamespaceNameDec structExtendsNamespaceNameDec = structExtendsDec.getNamespaceNameDec();
        if (structExtendsNamespaceNameDec != null) {
            namespaceName = structExtendsNamespaceNameDec.getText();
        } else {
            namespaceName = getNamespaceName(dec);
        }

        PsiElement result = ALittleTreeChangeListener.findALittleNameDec(dec.getProject()
                , PsiHelper.PsiElementType.STRUCT_NAME, dec.getContainingFile().getOriginalFile()
                , namespaceName, structNameDec.getText(), true);
        if (result instanceof ALittleStructNameDec) return (ALittleStructDec)result.getParent();
        return null;
    }

    public static class ClassExtendsData
    {
        public PsiHelper.ClassAccessType accessType;
        public ALittleClassDec dec;
    }
    // 计算class的父类
    public static ClassExtendsData findClassExtends(@NotNull ALittleClassDec dec) {
        ALittleClassExtendsDec classExtendsDec = dec.getClassExtendsDec();
        if (classExtendsDec == null) return null;

        ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
        if (classNameDec == null) return null;

        String namespaceName;
        ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
        if (classExtendsNamespaceNameDec != null) {
            namespaceName = classExtendsNamespaceNameDec.getText();
        } else {
            namespaceName = getNamespaceName(dec);
        }

        PsiElement result = ALittleTreeChangeListener.findALittleNameDec(dec.getProject()
                , PsiHelper.PsiElementType.CLASS_NAME, dec.getContainingFile().getOriginalFile()
                , namespaceName, classNameDec.getText(), true);
        if (result instanceof ALittleClassNameDec) {
            ClassExtendsData data = new ClassExtendsData();
            data.accessType = PsiHelper.calcAccessType(classExtendsDec.getAccessModifier());
            data.dec = (ALittleClassDec)result.getParent();
            return data;
        }
        return null;
    }

    public static int calcAccessLevelByTargetClassDec(int accessLevel, @NotNull ALittleClassDec dec, @NotNull ALittleClassDec targetDec) {
        if (accessLevel <= PsiHelper.sAccessOnlyPublic) {
            return accessLevel;
        }
        if (dec.equals(targetDec)) {
            return accessLevel;
        }
        ClassExtendsData classExtendsData = findClassExtends(dec);
        if (classExtendsData == null) return PsiHelper.sAccessOnlyPublic;
        accessLevel = calcAccessLevel(accessLevel, classExtendsData.accessType);
        return calcAccessLevelByTargetClassDec(accessLevel, classExtendsData.dec, targetDec);
    }

    // 获取函数列表
    public static void findClassMethodNameDecList(@NotNull ALittleClassDec classDec,
                                                  int accessLevel,
                                                  String name,
                                                  @NotNull List<PsiElement> result,
                                                  int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(classDec.getProject());
        if (listener == null) return;

        ALittleClassData data = listener.getClassData(classDec);
        if (data != null) {
            data.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.FUN, name, result);
            data.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.GETTER, name, result);
            data.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.SETTER, name, result);
            data.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.STATIC, name, result);
        }

        // 处理继承
        ClassExtendsData classExtendsData = findClassExtends(classDec);
        if (classExtendsData != null) {
            accessLevel = PsiHelper.calcAccessLevel(accessLevel, classExtendsData.accessType);
            findClassMethodNameDecList(classExtendsData.dec, accessLevel, name, result, deep - 1);
        }
    }

    // 获取类的属性列表
    public static void findClassAttrList(@NotNull ALittleClassDec classDec,
                                         int accessLevel,
                                         PsiHelper.ClassAttrType attrType,
                                         String name,
                                         @NotNull List<PsiElement> result,
                                         int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        // 处理成员
        ALittleTreeChangeListener.findClassAttrList(classDec, accessLevel, attrType, name, result);

        // 处理继承
        ClassExtendsData classExtendsData = findClassExtends(classDec);
        if (classExtendsData != null) {
            accessLevel = PsiHelper.calcAccessLevel(accessLevel, classExtendsData.accessType);
            findClassAttrList(classExtendsData.dec, accessLevel, attrType, name, result, deep - 1);
        }
    }

    // 获取继承的构造函数
    public static ALittleClassCtorDec findFirstCtorDecFromExtends(@NotNull ALittleClassDec classDec, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        // 处理成员函数
        List<ALittleClassCtorDec> classCtorDecList = classDec.getClassCtorDecList();
        for (ALittleClassCtorDec classCtorDec : classCtorDecList) {
            return classCtorDec;
        }

        // 处理继承
        ClassExtendsData classExtendsData = findClassExtends(classDec);
        if (classExtendsData != null) {
            return findFirstCtorDecFromExtends(classExtendsData.dec, deep - 1);
        }

        return null;
    }

    // 获取继承的属性
    public static PsiElement findFirstClassAttrFromExtends(@NotNull ALittleClassDec classDec,
                                                           PsiHelper.ClassAttrType attrType,
                                                           String name,
                                                           int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        // 处理setter函数
        PsiElement result = ALittleTreeChangeListener.findClassAttr(classDec,
                PsiHelper.sAccessPrivateAndProtectedAndPublic, attrType, name);
        if (result != null) return result;

        // 处理继承
        ClassExtendsData classExtendsData = findClassExtends(classDec);
        if (classExtendsData != null) {
            return findFirstClassAttrFromExtends(classExtendsData.dec, attrType, name, deep - 1);
        }

        return null;
    }

    // 查找函数的参数
    @NotNull
    public static List<ALittleMethodParamNameDec> findMethodParamNameDecList(PsiElement methodDec, String name) {
        List<ALittleMethodParamOneDec> paramOneDecList = new ArrayList<>();
        if (methodDec instanceof ALittleClassCtorDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassCtorDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (methodDec instanceof ALittleClassMethodDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassMethodDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (methodDec instanceof ALittleClassStaticDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassStaticDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (methodDec instanceof ALittleClassSetterDec) {
            paramOneDecList.add(((ALittleClassSetterDec) methodDec).getMethodParamOneDec());
        } else if (methodDec instanceof ALittleGlobalMethodDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleGlobalMethodDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        }

        // 收集所有的参数名元素
        List<ALittleMethodParamNameDec> result = new ArrayList<>();
        for (ALittleMethodParamOneDec oneDec : paramOneDecList) {
            ALittleMethodParamNameDec nameDec = oneDec.getMethodParamNameDec();
            if (nameDec == null) continue;
            if (name.isEmpty() || nameDec.getIdContent().getText().equals(name))
                result.add(nameDec);
        }
        return result;
    }

    // 查找表达式定义的函数名
    @NotNull
    public static List<ALittleVarAssignNameDec> findVarAssignNameDecList(PsiElement element, String name) {
        List<ALittleVarAssignNameDec> varDecList = new ArrayList<>();

        ALittleAllExpr rootAllExpr = null;
        // 计算出所在的表达式
        PsiElement parent = element;
        while (parent != null) {
            if (parent instanceof ALittleAllExpr) {
                rootAllExpr = (ALittleAllExpr)parent;
                break;
            }
            parent = parent.getParent();
        }
        if (rootAllExpr ==  null) return varDecList;

        findVarAssignNameDecList(rootAllExpr, varDecList, name);
        return varDecList;
    }

    private static void findVarAssignNameDecList(@NotNull ALittleAllExpr allExpr,
                                                 @NotNull List<ALittleVarAssignNameDec> varDecList,
                                                 @NotNull String name) {
        PsiElement parent = allExpr.getParent();
        List<ALittleAllExpr> allExprList = null;

        // 处理函数体
        if (parent instanceof ALittleMethodBodyDec) {
            ALittleMethodBodyDec curExpr = (ALittleMethodBodyDec)parent;
            allExprList = curExpr.getAllExprList();
            // 处理for循环
        } else if (parent instanceof ALittleForExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, name);

            ALittleForExpr curExpr = (ALittleForExpr)parent;
            allExprList = curExpr.getAllExprList();

            // for 内部有一个临时变量
            ALittleForStepCondition stepCondition = curExpr.getForStepCondition();
            ALittleForInCondition inCondition = curExpr.getForInCondition();
            if (stepCondition != null) {
                ALittleForStartStat startStat = stepCondition.getForStartStat();
                if (startStat != null) {
                    ALittleVarAssignNameDec varAssignNameDec = startStat.getForPairDec().getVarAssignNameDec();
                    if (varAssignNameDec != null) {
                        String varName = varAssignNameDec.getIdContent().getText();
                        if (name.isEmpty() || name.equals(varName)) {
                            varDecList.add(varAssignNameDec);
                        }
                    }
                }
            } else if (inCondition != null) {
                List<ALittleForPairDec> pairDecList = inCondition.getForPairDecList();
                for (ALittleForPairDec pairDec : pairDecList) {
                    ALittleVarAssignNameDec varAssignNameDec = pairDec.getVarAssignNameDec();
                    if (varAssignNameDec != null) {
                        String varName = varAssignNameDec.getIdContent().getText();
                        if (name.isEmpty() || name.equals(varName))
                            varDecList.add(varAssignNameDec);
                    }
                }
            }
            // 处理while循环
        } else if (parent instanceof ALittleWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, name);
            ALittleWhileExpr curExpr = (ALittleWhileExpr)parent;
            allExprList = curExpr.getAllExprList();
            // 处理do while
        } else if (parent instanceof ALittleDoWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, name);
            ALittleDoWhileExpr curExpr = (ALittleDoWhileExpr)parent;
            allExprList = curExpr.getAllExprList();
            // 处理 if
        } else if (parent instanceof ALittleIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, name);
            ALittleIfExpr curExpr = (ALittleIfExpr)parent;
            allExprList = curExpr.getAllExprList();
            // 处理 else if
        } else if (parent instanceof ALittleElseIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), varDecList, name);
            ALittleElseIfExpr curExpr = (ALittleElseIfExpr)parent;
            allExprList = curExpr.getAllExprList();
            // 处理 else
        } else if (parent instanceof ALittleElseExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), varDecList, name);
            ALittleElseExpr curExpr = (ALittleElseExpr)parent;
            allExprList = curExpr.getAllExprList();
            // 处理 wrap
        } else if (parent instanceof ALittleWrapExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, name);
            ALittleWrapExpr curExpr = (ALittleWrapExpr)parent;
            allExprList = curExpr.getAllExprList();
        }

        if (allExprList == null) return;

        for (ALittleAllExpr expr : allExprList) {
            // 如果已经遍历到当前，那么就可以返回了
            if (expr.equals(allExpr)) return;

            ALittleVarAssignExpr varAssignExpr = expr.getVarAssignExpr();
            if (varAssignExpr == null) continue;

            List<ALittleVarAssignDec> varAssignDecList = varAssignExpr.getVarAssignDecList();
            for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                ALittleVarAssignNameDec varAssignNameDec = varAssignDec.getVarAssignNameDec();
                if (varAssignNameDec == null) continue;
                String varName = varAssignNameDec.getIdContent().getText();
                if (name.isEmpty() || name.equals(varName))
                    varDecList.add(varAssignNameDec);
            }
        }
    }
}
