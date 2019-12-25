package plugin.alittle;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleClassData;
import plugin.index.ALittleEnumData;
import plugin.index.ALittleStructData;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsiHelper {
    // 判断字符串是不是整型值
    public static boolean isInt(@NotNull String content) {
        // 不论值如何，只要包含小数点，那么就不是整数
        if (content.contains(".")) {
            return false;
        }

        // 尝试转换为整型
        try {
            if (content.startsWith("0x"))
                Integer.parseInt(content.substring(2), 16);
            else
                Integer.parseInt(content, 10);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    // 计算哈希值
    public static int JSHash(@NotNull String content) {
        byte[] bytes = content.getBytes();
        int l = content.length();
        int h = l;
        int step = (l >> 5) + 1;

        for (int i=l; i >= step; i-=step) {
            h = h ^ ((h << 5) + bytes[i-1] + (h >> 2));
        }
        return h;
    }

    // 类的属性类型
    public enum ClassAttrType
    {
        VAR,            // 成员变量
        FUN,            // 成员函数
        GETTER,         // getter函数
        SETTER,         // setter函数
        STATIC,         // 静态函数
        TEMPLATE,       // 模板参数
    }

    // 访问权限类型
    public enum ClassAccessType
    {
        PUBLIC,         // 全局可访问
        PROTECTED,      // 本命名域可访问
        PRIVATE,        // 本类可访问
    }

    // 元素类型
    public enum PsiElementType
    {
        CLASS_NAME,         // 类名
        ENUM_NAME,          // 枚举名
        STRUCT_NAME,        // 结构体名
        INSTANCE_NAME,      // 单例名
        GLOBAL_METHOD,      // 全局函数
        USING_NAME,         // using名
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

    // 判断某个是不是register
    public static boolean isRegister(@NotNull PsiElement element) {
        ALittleNamespaceDec dec = getNamespaceDec(element.getContainingFile().getOriginalFile() );
        if (dec == null) {
            return false;
        }
        return dec.getRegisterModifier() != null;
    }

    // 获取某个元素的命名域
    @NotNull
    public static String getNamespaceName(@NotNull PsiElement element) {
        return getNamespaceName(element.getContainingFile().getOriginalFile());
    }

    @NotNull
    public static String getNamespaceName(PsiFile psiFile) {
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
        while (dec != null && !(dec instanceof PsiFile)) {
            if (dec instanceof ALittleClassDec) {
                return (ALittleClassDec)dec;
            }
            dec = dec.getParent();
        }
        return null;
    }

    // 获取这个元素所在的函数模板
    public static ALittleTemplateDec findTemplateDecFromParent(@NotNull PsiElement dec) {
        while (dec != null && !(dec instanceof PsiFile)) {
            if (dec instanceof ALittleClassDec) {
                return null;
            } else if (dec instanceof ALittleClassCtorDec) {
                return null;
            } else if (dec instanceof ALittleClassGetterDec) {
                return null;
            } else if (dec instanceof ALittleClassSetterDec) {
                return null;
            } else if (dec instanceof ALittleClassStaticDec) {
                return ((ALittleClassStaticDec) dec).getTemplateDec();
            } else if (dec instanceof ALittleClassMethodDec) {
                return ((ALittleClassMethodDec) dec).getTemplateDec();
            } else if (dec instanceof ALittleGlobalMethodDec) {
                return ((ALittleGlobalMethodDec) dec).getTemplateDec();
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

    // 根据名称，获取这个结构体的成员列表
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

    // 根据名称，获取这个枚举中的成员
    public static void findEnumVarDecList(@NotNull ALittleEnumDec enumDec, String name, @NotNull List<ALittleEnumVarDec> result) {
        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(enumDec.getProject());
        if (listener == null) return;

        ALittleEnumData data = listener.getEnumData(enumDec);
        if (data != null) {
            data.findVarDecList(name, result);
        }
    }

    // 过滤名称相同的元素
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

    // 计算class的父类
    public static ALittleClassDec findClassExtends(@NotNull ALittleClassDec dec) {
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
            return (ALittleClassDec)result.getParent();
        }
        return null;
    }

    // 计算在dec这个类中，对targetDec成员的访问权限
    public static int calcAccessLevelByTargetClassDec(int accessLevel, @NotNull ALittleClassDec dec, @NotNull ALittleClassDec targetDec) {
        // 如果当前访问权限已经只剩下public，就直接返回
        if (accessLevel <= PsiHelper.sAccessOnlyPublic) {
            return accessLevel;
        }
        // 如果dec和目标dec一致，那么直接返回
        if (dec.equals(targetDec)) {
            return accessLevel;
        }

        // 检查dec的父类，然后判断父类和targetDec的访问权限
        ALittleClassDec classExtendsDec = findClassExtends(dec);
        if (classExtendsDec != null) {
            return calcAccessLevelByTargetClassDec(accessLevel, classExtendsDec, targetDec);
        }

        // 如果没有父类，检查是否是在相同命名域下，如果是那么可以访问public和protected
        if (getNamespaceName(dec).equals(getNamespaceName(targetDec))) {
            return PsiHelper.sAccessProtectedAndPublic;
        }

        // 否则只能访问public
        return PsiHelper.sAccessOnlyPublic;
    }

    // 计算任意元素访问targetDec的访问权限
    public static int calcAccessLevelByTargetClassDecForElement(@NotNull PsiElement element, @NotNull ALittleClassDec targetDec) {
        // 默认为public
        int accessLevel =  PsiHelper.sAccessOnlyPublic;

        // 如果这个元素在类中，那么可以通过类和targetDec访问权限直接计算
        ALittleClassDec myClassDec = PsiHelper.findClassDecFromParent(element);
        if (myClassDec != null) {
            accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, targetDec);
        // 如果元素不在类中，那么element在lua中，或者和targetDec相同，则返回sAccessProtectedAndPublic
        } else {
            String namespaceName = PsiHelper.getNamespaceName(element);
            if (namespaceName.equals("lua") || namespaceName.equals(PsiHelper.getNamespaceName(targetDec))) {
                accessLevel = PsiHelper.sAccessProtectedAndPublic;
            }
        }

        return accessLevel;
    }

    // 根据名称，获取函数列表
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
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.FUN, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.GETTER, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.SETTER, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.STATIC, name, result);
        }

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            findClassMethodNameDecList(classExtendsDec, accessLevel, name, result, deep - 1);
        }
    }

    // 根据名称，获取类的属性列表
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
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            findClassAttrList(classExtendsDec, accessLevel, attrType, name, result, deep - 1);
        }
    }

    // 根据名称，获取继承的构造函数
    public static ALittleClassCtorDec findFirstCtorDecFromExtends(@NotNull ALittleClassDec classDec, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        // 处理成员函数
        List<ALittleClassCtorDec> classCtorDecList = classDec.getClassCtorDecList();
        for (ALittleClassCtorDec classCtorDec : classCtorDecList) {
            return classCtorDec;
        }

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            return findFirstCtorDecFromExtends(classExtendsDec, deep - 1);
        }

        return null;
    }

    // 根据名称，获取继承的属性
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
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            return findFirstClassAttrFromExtends(classExtendsDec, attrType, name, deep - 1);
        }

        return null;
    }

    // 根据名称，查找函数的参数列表
    @NotNull
    public static List<ALittleMethodParamNameDec> findMethodParamNameDecList(PsiElement methodDec, String name) {
        List<ALittleMethodParamOneDec> paramOneDecList = new ArrayList<>();
        // 处理构造函数的参数列表
        if (methodDec instanceof ALittleClassCtorDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassCtorDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        // 处理成员函数的参数列表
        } else if (methodDec instanceof ALittleClassMethodDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassMethodDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        // 处理静态函数的参数列表
        } else if (methodDec instanceof ALittleClassStaticDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassStaticDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        // 处理setter函数的参数列表
        } else if (methodDec instanceof ALittleClassSetterDec) {
            paramOneDecList.add(((ALittleClassSetterDec) methodDec).getMethodParamOneDec());
        // 处理全局函数的参数列表
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

    // 根据名称，查找变量名所在的定义元素
    @NotNull
    public static List<ALittleVarAssignNameDec> findVarAssignNameDecList(PsiElement element, String name) {
        List<ALittleVarAssignNameDec> varDecList = new ArrayList<>();

        // 计算出所在的表达式
        ALittleAllExpr rootAllExpr = null;
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

    // 根据名称，查找定义表达式名列表
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

            ALittleForStepCondition stepCondition = curExpr.getForStepCondition();
            ALittleForInCondition inCondition = curExpr.getForInCondition();
            // 步进式的for有一个临时变量
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
            // 迭代式的for有多个临时变量
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

            // 获取定义表达式
            ALittleVarAssignExpr varAssignExpr = expr.getVarAssignExpr();
            if (varAssignExpr == null) continue;

            // 获取变量名列表
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
