package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;
import plugin.reference.ALittleReferenceUtil;

import javax.swing.*;
import java.util.*;

public class ALittleUtil {

    // 查找所有的变量名节点对象
    private static void findStructVarNameDecList(Project project, String namespace, String structName, String varName, @NotNull List<ALittleStructVarNameDec> result, int deep)
    {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleStructNameDec> nameDecList = ALittleTreeChangeListener.findStructNameDecList(project, namespace, structName);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleStructDec structDec = (ALittleStructDec)nameDecList.get(0).getParent();

        findStructVarNameDecList(project, namespace, structDec, varName, result, deep);
    }

    public static void findStructVarNameDecList(Project project, String namespace, ALittleStructDec structDec, String varName, @NotNull List<ALittleStructVarNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (structDec == null) return;

        // 处理继承
        ALittleStructExtendsNameDec structExtendsNameDec = structDec.getStructExtendsNameDec();
        if (structExtendsNameDec != null) {
            ALittleStructExtendsNamespaceNameDec structExtendsNamespaceNameDec = structDec.getStructExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (structExtendsNamespaceNameDec != null) namespaceName = structExtendsNamespaceNameDec.getText();
            findStructVarNameDecList(project, namespaceName, structExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理成员变量
        List<ALittleStructVarDec> structVarDecList = structDec.getStructVarDecList();
        for (ALittleStructVarDec structVarDec : structVarDecList) {
            ALittleStructVarNameDec varNameDec = structVarDec.getStructVarNameDec();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getIdContent().getText().equals(varName)) {
                result.add(varNameDec);
            }
        }
    }

    public static void findEnumVarNameDecList(ALittleEnumDec enumDec, String varName, @NotNull List<ALittleEnumVarNameDec> result) {
        if (enumDec == null) return;

        // 处理成员变量
        List<ALittleEnumVarDec> enumVarDecList = enumDec.getEnumVarDecList();
        for (ALittleEnumVarDec enumVarDec : enumVarDecList) {
            ALittleEnumVarNameDec varNameDec = enumVarDec.getEnumVarNameDec();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getIdContent().getText().equals(varName)) {
                result.add(varNameDec);
            }
        }
    }

    // 根据枚举字段名，获取对应的值
    public static Integer getEnumVarValue(ALittleEnumDec enumDec, String varName) {
        if (enumDec == null) return null;

        // 初始值
        int enumValue = -1;

        List<ALittleEnumVarDec> varDecList = enumDec.getEnumVarDecList();
        for (ALittleEnumVarDec varDec : varDecList) {
            ALittleEnumVarNameDec varNameDec = varDec.getEnumVarNameDec();
            if (varNameDec == null) continue;

            ALittleEnumVarValueDec varValueDec = varDec.getEnumVarValueDec();
            // 如果没有设定值，那么就默认+1
            if (varValueDec == null) {
                ++enumValue;
                if (varNameDec.getIdContent().getText().equals(varName)) {
                    return enumValue;
                }
            // 如果设定为数字，那么就要设置一下
            } else if (varValueDec.getDigitContent() != null) {
                String value = varValueDec.getDigitContent().getText();
                if (!isInt(value)) {
                    return null;
                }
                // 把字符串转为数值
                String numberContent = varValueDec.getText();
                if (numberContent.startsWith("0x"))
                    enumValue = Integer.parseInt(numberContent.substring(2), 16);
                else
                    enumValue = Integer.parseInt(numberContent);
                if (varNameDec.getIdContent().getText().equals(varName)) {
                    return enumValue;
                }
            }
        }
        return null;
    }

    // 查找所有的函数名节点对象
    private static void findMethodNameDecList(Project project, String namespace, String className, String methodName, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecList(project, namespace, classDec, methodName, result, icon, deep);
    }

    public static void findMethodNameDecList(Project project, String namespace, ALittleClassDec classDec, String methodName, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findMethodNameDecList(project, namespaceName, classExtendsNameDec.getText(), methodName, result, icon, deep - 1);
        }

        // 处理成员函数
        List<ALittleClassMethodDec> classMethodDecList = classDec.getClassMethodDecList();
        for (ALittleClassMethodDec classMethodDec : classMethodDecList) {
            ALittleMethodNameDec classMethodNameDec = classMethodDec.getMethodNameDec();
            if (classMethodNameDec == null) continue;
            if (methodName.isEmpty() || classMethodNameDec.getIdContent().getText().equals(methodName)) {
                if (icon != null) icon.add(ALittleIcons.MEMBER_METHOD);
                result.add(classMethodNameDec);
            }
        }

        // 处理getter函数
        List<ALittleClassGetterDec> classGetterDecList = classDec.getClassGetterDecList();
        for (ALittleClassGetterDec classGetterDec : classGetterDecList) {
            ALittleMethodNameDec classGetterNameDec = classGetterDec.getMethodNameDec();
            if (classGetterNameDec == null) continue;
            if (methodName.isEmpty() || classGetterNameDec.getIdContent().getText().equals(methodName)) {
                if (icon != null) icon.add(ALittleIcons.GETTER_METHOD);
                result.add(classGetterNameDec);
            }
        }

        // 处理setter函数
        List<ALittleClassSetterDec> classSetterDecList = classDec.getClassSetterDecList();
        for (ALittleClassSetterDec classSetterDec : classSetterDecList) {
            ALittleMethodNameDec classSetterNameDec = classSetterDec.getMethodNameDec();
            if (classSetterNameDec == null) continue;
            if (methodName.isEmpty() || classSetterNameDec.getIdContent().getText().equals(methodName)) {
                if (icon != null) icon.add(ALittleIcons.SETTER_METHOD);
                result.add(classSetterNameDec);
            }
        }

        // 处理静态函数
        List<ALittleClassStaticDec> classStaticDecList = classDec.getClassStaticDecList();
        for (ALittleClassStaticDec classStaticDec : classStaticDecList) {
            ALittleMethodNameDec classStaticNameDec = classStaticDec.getMethodNameDec();
            if (classStaticNameDec == null) continue;
            if (methodName.isEmpty() || classStaticNameDec.getIdContent().getText().equals(methodName)) {
                if (icon != null) icon.add(ALittleIcons.STATIC_METHOD);
                result.add(classStaticNameDec);
            }
        }
    }

    // 查找所有的变量名节点对象
    private static void findClassVarNameDecList(Project project, String namespace, String className, String varName, @NotNull List<ALittleClassVarNameDec> result, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findClassVarNameDecList(project, namespace, classDec, varName, result, deep);
    }

    public static void findClassVarNameDecList(Project project, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleClassVarNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findClassVarNameDecList(project, namespaceName, classExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理成员变量
        List<ALittleClassVarDec> classVarDecList = classDec.getClassVarDecList();
        for (ALittleClassVarDec class_varDec : classVarDecList) {
            ALittleClassVarNameDec varNameDec = class_varDec.getClassVarNameDec();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getIdContent().getText().equals(varName))
                result.add(varNameDec);
        }
    }

    // 过滤函数名相同的元素
    @NotNull
    public static List<ALittleMethodNameDec> filterSameMethodName(@NotNull List<ALittleMethodNameDec> result) {
        Set<String> nameSet = new HashSet<>();
        List<ALittleMethodNameDec> newResult = new ArrayList<>();
        for (int i = result.size() - 1; i >= 0; --i) {
            String name = result.get(i).getIdContent().getText();
            if (nameSet.contains(name)) {
                continue;
            }
            nameSet.add(name);
            newResult.add(result.get(i));
        }
        return newResult;
    }

    // 查找所有的setter节点对象
    private static void findMethodNameDecListForSetter(Project project, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForSetter(project, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForSetter(Project project, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findMethodNameDecListForSetter(project, namespaceName, classExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理setter函数
        List<ALittleClassSetterDec> classSetterDecList = classDec.getClassSetterDecList();
        for (ALittleClassSetterDec classSetterDec : classSetterDecList) {
            ALittleMethodNameDec methodNameDec = classSetterDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (varName.isEmpty() || methodNameDec.getIdContent().getText().equals(varName))
                result.add(methodNameDec);
        }
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForGetter(Project project, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForGetter(project, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForGetter(Project project, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findMethodNameDecListForGetter(project, namespaceName, classExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理getter函数
        List<ALittleClassGetterDec> classGetterDecList = classDec.getClassGetterDecList();
        for (ALittleClassGetterDec classGetterDec : classGetterDecList) {
            ALittleMethodNameDec methodNameDec = classGetterDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (varName.isEmpty() || methodNameDec.getIdContent().getText().equals(varName))
                result.add(methodNameDec);
        }
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForFun(Project project, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForFun(project, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForFun(Project project, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findMethodNameDecListForFun(project, namespaceName, classExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理成员函数
        List<ALittleClassMethodDec> classMethodDecList = classDec.getClassMethodDecList();
        for (ALittleClassMethodDec classMethodDec : classMethodDecList) {
            ALittleMethodNameDec methodNameDec = classMethodDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (varName.isEmpty() || methodNameDec.getIdContent().getText().equals(varName))
                result.add(methodNameDec);
        }
    }

    // 查找所有的static节点对象
    private static void findMethodNameDecListForStatic(Project project, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForStatic(project, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForStatic(Project project, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec classExtendsNameDec = classDec.getClassExtendsNameDec();
        if (classExtendsNameDec != null) {
            ALittleClassExtendsNamespaceNameDec classExtendsNamespaceNameDec = classDec.getClassExtendsNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            findMethodNameDecListForStatic(project, namespaceName, classExtendsNameDec.getText(), varName, result, deep - 1);
        }

        // 处理惊天函数
        List<ALittleClassStaticDec> classStaticDecList = classDec.getClassStaticDecList();
        for (ALittleClassStaticDec classStaticDec : classStaticDecList) {
            ALittleMethodNameDec methodNameDec = classStaticDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (varName.isEmpty() || methodNameDec.getIdContent().getText().equals(varName))
                result.add(methodNameDec);
        }
    }

    // 查找函数的参数
    @NotNull
    public static List<ALittleMethodParamNameDec> findMethodParamNameDecList(
            ALittleClassCtorDec classCtorDec,
            ALittleClassSetterDec classSetterDec,
            ALittleClassMethodDec classMethodDec,
            ALittleClassStaticDec classStaticDec,
            ALittleGlobalMethodDec globalMethodDec,
            String varName) {
        List<ALittleMethodParamOneDec> paramOneDecList = new ArrayList<>();
        if (classCtorDec != null) {
            ALittleMethodParamDec methodParamDec = classCtorDec.getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (classMethodDec != null) {
            ALittleMethodParamDec methodParamDec = classMethodDec.getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (classStaticDec != null) {
            ALittleMethodParamDec methodParamDec = classStaticDec.getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        } else if (classSetterDec != null) {
            paramOneDecList.add(classSetterDec.getMethodParamOneDec());
        } else if (globalMethodDec != null) {
            ALittleMethodParamDec methodParamDec = globalMethodDec.getMethodParamDec();
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
            if (varName.isEmpty() || nameDec.getIdContent().getText().equals(varName))
                result.add(nameDec);
        }
        return result;
    }

    // 查找表达式定义的函数名
    @NotNull
    public static List<ALittleVarAssignNameDec> findVarAssignNameDecList(PsiElement element, String varName) {
        List<ALittleVarAssignNameDec> varDecList = new ArrayList<>();

        ALittleAllExpr rootAllExpr = null;
        // 计算出所在的表达式
        PsiElement parent = element;
        while (true) {
            if (parent == null) break;
            if (parent instanceof ALittleAllExpr) {
                rootAllExpr = (ALittleAllExpr)parent;
                break;
            }
            parent = parent.getParent();
        }
        if (rootAllExpr ==  null) return varDecList;

        findVarAssignNameDecList(rootAllExpr, varDecList, varName);
        return varDecList;
    }

    private static void findVarAssignNameDecList(@NotNull ALittleAllExpr allExpr, @NotNull List<ALittleVarAssignNameDec> varDecList, @NotNull String varName) {
        PsiElement parent = allExpr.getParent();

        List<ALittleAllExpr> allExprList = null;

        // 处理函数体
        if (parent instanceof ALittleMethodBodyDec) {
            ALittleMethodBodyDec curExpr = (ALittleMethodBodyDec)parent;
            allExprList = curExpr.getAllExprList();
        // 处理for循环
        } else if (parent instanceof ALittleForExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, varName);

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
                        String name = varAssignNameDec.getIdContent().getText();
                        if (varName.isEmpty() || name.equals(varName))
                            varDecList.add(varAssignNameDec);
                    }
                }
            } else if (inCondition != null) {
                List<ALittleForPairDec> pairDecList = inCondition.getForPairDecList();
                for (ALittleForPairDec pairDec : pairDecList) {
                    ALittleVarAssignNameDec varAssignNameDec = pairDec.getVarAssignNameDec();
                    if (varAssignNameDec != null) {
                        String name = varAssignNameDec.getIdContent().getText();
                        if (varName.isEmpty() || name.equals(varName))
                            varDecList.add(varAssignNameDec);
                    }
                }
            }
        // 处理while循环
        } else if (parent instanceof ALittleWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, varName);
            ALittleWhileExpr curExpr = (ALittleWhileExpr)parent;
            allExprList = curExpr.getAllExprList();
        // 处理do while
        } else if (parent instanceof ALittleDoWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, varName);
            ALittleDoWhileExpr curExpr = (ALittleDoWhileExpr)parent;
            allExprList = curExpr.getAllExprList();
        // 处理 if
        } else if (parent instanceof ALittleIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, varName);
            ALittleIfExpr curExpr = (ALittleIfExpr)parent;
            allExprList = curExpr.getAllExprList();
        // 处理 else if
        } else if (parent instanceof ALittleElseIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), varDecList, varName);
            ALittleElseIfExpr curExpr = (ALittleElseIfExpr)parent;
            allExprList = curExpr.getAllExprList();
        // 处理 else
        } else if (parent instanceof ALittleElseExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), varDecList, varName);
            ALittleElseExpr curExpr = (ALittleElseExpr)parent;
            allExprList = curExpr.getAllExprList();
        // 处理 wrap
        } else if (parent instanceof ALittleWrapExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), varDecList, varName);
            ALittleWrapExpr curExpr = (ALittleWrapExpr)parent;
            allExprList = curExpr.getAllExprList();
        }

        if (allExprList == null) return;

        for (ALittleAllExpr expr : allExprList) {
            // 如果已经遍历到当前，那么就可以返回了
            if (expr.equals(allExpr)) return;

            ALittleVarAssignExpr varAssignExpr = expr.getVarAssignExpr();
            if (varAssignExpr == null) continue;

            List<ALittleVarAssignPairDec> varAssignPairDecList = varAssignExpr.getVarAssignPairDecList();
            for (ALittleVarAssignPairDec varAssignPairDec : varAssignPairDecList) {
                ALittleVarAssignNameDec varAssignNameDec = varAssignPairDec.getVarAssignNameDec();
                if (varAssignNameDec == null) continue;
                String name = varAssignNameDec.getIdContent().getText();
                if (varName.isEmpty() || name.equals(varName))
                    varDecList.add(varAssignNameDec);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 从文件中获取AlittleNamespaceDec
    public static ALittleNamespaceDec getNamespaceDec(PsiFile psiFile) {
        for(PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                return (ALittleNamespaceDec)child;
            }
        }
        return null;
    }

    // 根据文件对象获取命名域
    @NotNull
    public static String getNamespaceName(ALittleFile alittleFile) {
        ALittleNamespaceDec namespaceDec = getNamespaceDec(alittleFile);
        if (namespaceDec == null) return "";
        ALittleNamespaceNameDec namespaceNameDec = namespaceDec.getNamespaceNameDec();
        if (namespaceNameDec == null) return "";
        return namespaceNameDec.getIdContent().getText();
    }

    // 判断字符串是不是int值
    public static boolean isInt(String content) {
        try {
            double v = Double.parseDouble(content);
            return v == Math.floor(v) && !content.contains(".");
        } catch (NumberFormatException e) {
            return true;
        }
    }

    // 判断 parent是否是child的父类
    public static boolean IsClassSuper(PsiElement child, PsiElement parent) {
        if (!(child instanceof ALittleClassDec)) return false;
        ALittleClassDec childClass = (ALittleClassDec)child;

        ALittleClassExtendsNameDec extendsNameDec = childClass.getClassExtendsNameDec();
        if (extendsNameDec == null) return false;

        PsiElement guessType = childClass.getClassExtendsNameDec().guessType();
        if (guessType == null) return false;

        if (guessType.equals(parent)) return true;

        return IsClassSuper(guessType, parent);
    }

    // 判断 parent是否是child的父类
    public static boolean IsStructSuper(PsiElement child, PsiElement parent) {
        if (!(child instanceof ALittleStructDec)) return false;
        ALittleStructDec childStruct = (ALittleStructDec)child;

        ALittleStructExtendsNameDec extendsNameDec = childStruct.getStructExtendsNameDec();
        if (extendsNameDec == null) return false;

        PsiElement guessType = childStruct.getStructExtendsNameDec().guessType();
        if (guessType == null) return false;

        if (guessType.equals(parent)) return true;

        return IsStructSuper(guessType, parent);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 一下guesstype得出的类型值可能是
    // ALittlePrimitiveType 基本类型
    // ALittleConstValue 常量类型
    // ALittleOp3Stat   这个表示字符串
    // ALittleGenericType 通用类型

    // ALittleClassDec 类
    // ALittleClassNameDec 类名
    // ALittleStructDec 结构体
    // ALittleStructNameDec 结构体名
    // ALittleEnumDec 枚举
    // ALittleEnumNameDec 枚举名
    // ALittleEnumVarNameDec 枚举项的名称
    // ALittleNamespaceDec 命名域

    // ALittleMethodNameDec 函数名
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotNull
    public static String saveGuessTypeInfoToJson(@NotNull GuessTypeInfo info)
    {
        String content = "{\"type\":\"";
        if (info.type == GuessType.GT_CLASS)
            content += "class";
        else if (info.type == GuessType.GT_CONST)
            content += "const";
        else if (info.type == GuessType.GT_ENUM)
            content += "enum";
        else if (info.type == GuessType.GT_FUNCTOR)
            content += "Functor";
        else if (info.type == GuessType.GTList)
            content += "List";
        else if (info.type == GuessType.GT_MAP)
            content += "Map";
        else if (info.type == GuessType.GT_PRIMITIVE)
            content += "Primitive";
        else if (info.type == GuessType.GT_STRUCT)
            content += "struct";

        content += "\",\"name\":\"" + info.value + "\"";

        if (info.type == GuessType.GT_STRUCT || info.type == GuessType.GT_CLASS) {
            content += ",\"varList\":[";
            List<String> varList = new ArrayList<>();
            for (int i = 0; i < info.listVarType.size(); ++i) {
                String child_content = "{\"type\":" + saveGuessTypeInfoToJson(info.listVarType.get(i));
                child_content += ",\"name\":\"" + info.listVarName.get(i) + "\"}";
                varList.add(child_content);
            }
            content += String.join(",", varList);
            content += "]";
        } else if (info.type == GuessType.GTList) {
            content += ",\"sub_type\":" + saveGuessTypeInfoToJson(info.listSubType);
        } else if (info.type == GuessType.GT_MAP) {
            content += ",\"key_type\":" + saveGuessTypeInfoToJson(info.mapKeyType);
            content += ",\"value_type\":" + saveGuessTypeInfoToJson(info.mapValueType);
        }

        content += "}";
        return content;
    }

    @NotNull
    public static GuessTypeInfo guessTypeString(PsiElement src, PsiElement element, HashSet<PsiElement> deepGuess) throws ALittleElementException {
        // 基本类型
        if (element instanceof ALittlePrimitiveType) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = element.getText();
            return info;
        } else if (element instanceof ALittleReflectValue) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "string";
            return info;
        // 常量类型
        } else if (element instanceof ALittleConstValue) {
            ALittleConstValue dec = (ALittleConstValue) element;
            if (dec.getDigitContent() != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_PRIMITIVE;
                String value = dec.getDigitContent().getText();
                if (ALittleUtil.isInt(value))
                    info.value = "int";
                else
                    info.value = "double";
                return info;
            } else if (dec.getStringContent() != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_PRIMITIVE;
                info.value = "string";
                return info;
            } else if (dec.getText().equals("true") || dec.getText().equals("false")) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_PRIMITIVE;
                info.value = "bool";
                return info;
            } else if (dec.getText().equals("null")) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_CONST;
                info.value = "null";
                return info;
            }
        } else if (element instanceof ALittleOp3Suffix) {
            ALittleOp3Suffix dec = (ALittleOp3Suffix) element;
            String op_3 = dec.getOp3().getText();
            if (op_3.equals("/")) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_PRIMITIVE;
                info.value = "double";
                return info;
            }
        } else if (element instanceof ALittleOp5Suffix) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "string";
            return info;
        } else if (element instanceof ALittleOp6Suffix) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "bool";
            return info;
        } else if (element instanceof ALittleGenericType) {
            return guessTypeString(src, (ALittleGenericType)element, deepGuess);
        } else if (element instanceof ALittleClassDec) {
            ALittleClassDec dec = (ALittleClassDec)element;
            String namespaceName = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleClassNameDec nameDec = dec.getClassNameDec();
            if (nameDec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_CLASS;
                info.value = namespaceName + "." + nameDec.getIdContent().getText();
                // 如果需要深度递归
                if (deepGuess != null) {
                    if (deepGuess.contains(dec)) {
                        throw new ALittleElementException(src, "成员变量出现递归定义");
                    }
                    deepGuess.add(dec);
                    info.listVarName = new ArrayList<>();
                    info.listVarType = new ArrayList<>();
                    for (ALittleClassVarDec varDec : dec.getClassVarDecList()) {
                        ALittleClassVarNameDec varNameDec = varDec.getClassVarNameDec();
                        GuessTypeInfo child_info = guessTypeString(src, varDec.getAllType(), deepGuess);
                        if (varNameDec != null) {
                            info.listVarType.add(child_info);
                            info.listVarName.add(varNameDec.getText());
                        }
                    }
                }
                return info;
            }
        } else if (element instanceof ALittleClassNameDec) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "any";
            return info;
        } else if (element instanceof ALittleStructDec) {
            ALittleStructDec dec = (ALittleStructDec)element;
            String namespaceName = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleStructNameDec nameDec = dec.getStructNameDec();
            if (nameDec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_STRUCT;
                info.value = namespaceName + "." + nameDec.getIdContent().getText();
                // 如果需要深度递归
                if (deepGuess != null) {
                    if (deepGuess.contains(dec)) {
                        throw new ALittleElementException(src, "成员变量出现递归定义");
                    }
                    deepGuess.add(dec);
                    info.listVarName = new ArrayList<>();
                    info.listVarType = new ArrayList<>();
                    for (ALittleStructVarDec varDec : dec.getStructVarDecList()) {
                        ALittleStructVarNameDec varNameDec = varDec.getStructVarNameDec();
                        GuessTypeInfo child_info = guessTypeString(src, varDec.getAllType(), deepGuess);
                        if (varNameDec != null) {
                            info.listVarType.add(child_info);
                            info.listVarName.add(varNameDec.getText());
                        }
                    }
                }
                return info;
            }
        } else if (element instanceof ALittleEnumDec) {
            ALittleEnumDec dec = (ALittleEnumDec)element;
            String namespaceName = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleEnumNameDec nameDec = dec.getEnumNameDec();
            if (nameDec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_ENUM;
                info.value = namespaceName + "." + nameDec.getIdContent().getText();
                return info;
            }
        } else if (element instanceof ALittleEnumVarNameDec) {
            ALittleEnumVarNameDec nameDec = (ALittleEnumVarNameDec)element;
            ALittleEnumVarDec dec = (ALittleEnumVarDec)nameDec.getParent();
            ALittleEnumVarValueDec valueDec = dec.getEnumVarValueDec();

            // 如果枚举带有protocol定义，那么就返回错误信息
            ALittleEnumDec enumDec = (ALittleEnumDec)dec.getParent();
            if (enumDec.getEnumProtocolDec() != null) {
                throw new ALittleElementException(src, "不能使用带protocol定义的enum的字段");
            }

            if (valueDec != null) {
                if (valueDec.getDigitContent() != null) {
                    GuessTypeInfo info = new GuessTypeInfo();
                    info.type = GuessType.GT_PRIMITIVE;
                    info.value = "int";
                    return info;
                }
                if (valueDec.getStringContent() != null) {
                    GuessTypeInfo info = new GuessTypeInfo();
                    info.type = GuessType.GT_PRIMITIVE;
                    info.value = "string";
                    return info;
                }
            }
        } else if (element instanceof ALittleNamespaceNameDec) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "any";
            return info;
        } else if (element instanceof ALittleMethodNameDec) {
            PsiElement parent = element.getParent();
            if (parent instanceof ALittleClassGetterDec) {
                ALittleClassGetterDec classGetterDec = (ALittleClassGetterDec) parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functorAwait = false;
                info.functorParamList = new ArrayList<>();
                info.functorReturnList = new ArrayList<>();

                // 第一个参数是类
                GuessTypeInfo class_GuessInfo = guessTypeString(element, classGetterDec.getParent(), deepGuess);
                info.functorParamList.add(class_GuessInfo);
                info.value += class_GuessInfo.value + ")";

                List<String> typeList = new ArrayList<>();
                // 添加返回值列表
                ALittleMethodReturnTypeDec returnTypeDec = classGetterDec.getMethodReturnTypeDec();
                if (returnTypeDec != null) {
                    ALittleAllType allType = returnTypeDec.getAllType();
                    GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                    typeList.add(GuessInfo.value);
                    info.functorReturnList.add(GuessInfo);
                }
                if (!typeList.isEmpty()) info.value += ":";
                info.value += String.join(",", typeList) + ">";
                return info;
            } else if (parent instanceof ALittleClassSetterDec) {
                ALittleClassSetterDec classSetterDec = (ALittleClassSetterDec) parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functorAwait = false;
                info.functorParamList = new ArrayList<>();
                info.functorReturnList = new ArrayList<>();

                List<String> typeList = new ArrayList<>();
                // 第一个参数是类
                GuessTypeInfo class_GuessInfo = guessTypeString(element, classSetterDec.getParent(), deepGuess);
                typeList.add(class_GuessInfo.value);
                info.functorParamList.add(class_GuessInfo);

                // 添加参数列表
                ALittleMethodParamOneDec oneDec = classSetterDec.getMethodParamOneDec();
                if (oneDec != null) {
                    ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                    GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                    typeList.add(GuessInfo.value);
                    info.functorParamList.add(GuessInfo);
                }
                info.value += String.join(",", typeList) + ")";
                info.value += ">";
                return info;
            } else if (parent instanceof ALittleClassMethodDec) {
                ALittleClassMethodDec classMethodDec = (ALittleClassMethodDec) parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functorAwait = classMethodDec.getCoroutineModifier() != null && classMethodDec.getCoroutineModifier().getText().equals("await");
                if (info.functorAwait) {
                    info.value = "Functor<await(";
                }
                info.functorParamList = new ArrayList<>();
                info.functorReturnList = new ArrayList<>();

                List<String> typeList = new ArrayList<>();
                // 第一个参数是类
                GuessTypeInfo class_GuessInfo = guessTypeString(element, classMethodDec.getParent(), deepGuess);
                typeList.add(class_GuessInfo.value);
                info.functorParamList.add(class_GuessInfo);

                // 添加参数列表
                ALittleMethodParamDec paramDec = classMethodDec.getMethodParamDec();
                if (paramDec != null) {
                    List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec oneDec : oneDecList) {
                        ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorParamList.add(GuessInfo);
                    }
                }
                info.value += String.join(",", typeList) + ")";
                typeList = new ArrayList<>();
                // 添加返回值列表
                ALittleMethodReturnDec returnDec = classMethodDec.getMethodReturnDec();
                if (returnDec != null) {
                    List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                        ALittleAllType allType = returnTypeDec.getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorReturnList.add(GuessInfo);
                    }
                }
                if (!typeList.isEmpty()) info.value += ":";
                info.value += String.join(",", typeList) + ">";
                return info;
            } else if (parent instanceof ALittleClassStaticDec) {
                ALittleClassStaticDec classStaticDec = (ALittleClassStaticDec) parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functorAwait = classStaticDec.getCoroutineModifier() != null && classStaticDec.getCoroutineModifier().getText().equals("await");
                if (info.functorAwait) {
                    info.value = "Functor<await(";
                }
                info.functorParamList = new ArrayList<>();
                info.functorReturnList = new ArrayList<>();

                List<String> typeList = new ArrayList<>();
                ALittleMethodParamDec paramDec = classStaticDec.getMethodParamDec();
                if (paramDec != null) {
                    List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec oneDec : oneDecList) {
                        ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorParamList.add(GuessInfo);
                    }
                }
                info.value += String.join(",", typeList) + ")";
                typeList = new ArrayList<>();
                ALittleMethodReturnDec returnDec = classStaticDec.getMethodReturnDec();
                if (returnDec != null) {
                    List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                        ALittleAllType allType = returnTypeDec.getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorReturnList.add(GuessInfo);
                    }
                }
                if (!typeList.isEmpty()) info.value += ":";
                info.value += String.join(",", typeList) + ">";
                return info;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec) parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functorAwait = globalMethodDec.getCoroutineModifier() != null && globalMethodDec.getCoroutineModifier().getText().equals("await");
                if (info.functorAwait) {
                    info.value = "Functor<await(";
                }
                info.functorParamList = new ArrayList<>();
                info.functorReturnList = new ArrayList<>();

                List<String> typeList = new ArrayList<>();
                ALittleMethodParamDec paramDec = globalMethodDec.getMethodParamDec();
                if (paramDec != null) {
                    List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec oneDec : oneDecList) {
                        ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorParamList.add(GuessInfo);
                    }
                }
                info.value += String.join(",", typeList) + ")";
                typeList = new ArrayList<>();
                ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                if (returnDec != null) {
                    List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                        ALittleAllType allType = returnTypeDec.getAllType();
                        GuessTypeInfo GuessInfo = guessTypeString(element, allType, deepGuess);
                        typeList.add(GuessInfo.value);
                        info.functorReturnList.add(GuessInfo);
                    }
                }
                if (!typeList.isEmpty()) info.value += ":";
                info.value += String.join(",", typeList) + ">";
                return info;
            }
        } else if (element instanceof ALittleBindStat) {
            ALittleBindStat bind_stat = (ALittleBindStat)element;

            List<ALittleValueStat> valueStatList = bind_stat.getValueStatList();
            if (valueStatList.isEmpty()) {
                throw new ALittleElementException(bind_stat, "bind 表达式不能没有参数");
            }

            ALittleValueStat valueStat = valueStatList.get(0);
            // 第一个参数必须是函数
            PsiElement valueStat_guess = ALittleUtil.guessSoftType(valueStat, valueStat);
            ALittleUtil.GuessTypeInfo GuessInfo = ALittleUtil.guessTypeString(valueStat, valueStat_guess, null);
            if (GuessInfo.type != ALittleUtil.GuessType.GT_FUNCTOR) {
                throw new ALittleElementException(valueStat, "bind 表达式第一个参数必须是一个函数");
            }
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_FUNCTOR;
            info.value = "Functor<(";
            info.functorAwait = GuessInfo.functorAwait;
            if (info.functorAwait) {
                info.value = "Functor<await(";
            }
            info.functorParamList = GuessInfo.functorParamList;
            info.functorReturnList = GuessInfo.functorReturnList;
            int param_count = valueStatList.size() - 1;
            while (param_count > 0) {
                info.functorParamList.remove(0);
                --param_count;
            }
            List<String> nameList = new ArrayList<>();
            for (GuessTypeInfo param_info : info.functorParamList) {
                nameList.add(param_info.value);
            }
            info.value += String.join(",", nameList);
            info.value += ")";

            nameList = new ArrayList<>();
            for (GuessTypeInfo param_info : info.functorReturnList) {
                nameList.add(param_info.value);
            }
            if (!nameList.isEmpty()) info.value += ":";
            info.value += String.join(",", nameList);
            info.value += ">";
            return info;
        } else if (element instanceof ALittleOpNewList) {
            ALittleOpNewList opNewList = (ALittleOpNewList)element;
            List<ALittleValueStat> valueStatList = opNewList.getValueStatList();
            if (valueStatList.isEmpty()) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GTList;
                info.value = "List<any>";
                info.listSubType = new GuessTypeInfo();
                info.listSubType.type = GuessType.GT_PRIMITIVE;
                info.listSubType.value = "any";
                return info;
            }
            GuessTypeInfo GuessInfo = guessTypeString(element, valueStatList.get(0), deepGuess);

            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GTList;
            info.value = "List<" + GuessInfo.value + ">";
            info.listSubType = GuessInfo;
            return info;
        } else if (element instanceof ALittleValueStat) {
            ALittleValueStat valueStat = (ALittleValueStat)element;
            PsiElement guessType = guessSoftType(src, valueStat);

            return guessTypeString(src, guessType, deepGuess);
        }

        throw new ALittleElementException(src, "未知的表达式");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotNull
    public static PsiElement guessSoftTypeForOp8Impl(String opString
                                                    , PsiElement leftSrc, PsiElement leftGuess
                                                    , PsiElement rightSrc, PsiElement rightGuess
                                                    , ALittleOp8Suffix op8Suffix) throws ALittleElementException {
        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;

        if (!leftGuessInfo.value.equals("bool")) {
            throw new ALittleElementException(leftSrc, opString + "运算符左边必须是bool或者any类型.不能是:" + leftGuessInfo.value);
        }

        if (!rightGuessInfo.value.equals("bool")) {
            throw new ALittleElementException(rightSrc, opString + "运算符右边边必须是bool或者any类型.不能是:" + rightGuessInfo.value);
        }

        return leftGuess;

    }
    @NotNull
    public static PsiElement guessSoftTypeForOp8(PsiElement leftSrc, PsiElement leftGuess, ALittleOp8Suffix op8Suffix) throws ALittleElementException {
        String opString = op8Suffix.getOp8().getText();

        PsiElement valueFactor_guess = null;
        PsiElement lastSrc = null;
        if (op8Suffix.getValueFactor() != null) {
            ALittleValueFactor valueFactor = op8Suffix.getValueFactor();
            valueFactor_guess = guessSoftType(valueFactor, valueFactor);
            lastSrc = valueFactor;
        } else if (op8Suffix.getOp2Value() != null) {
            ALittleOp2Value op2Value = op8Suffix.getOp2Value();
            valueFactor_guess = guessSoftType(op2Value, op2Value);
            lastSrc = op2Value;
        }

        PsiElement suffixGuessType = valueFactor_guess;
        List<ALittleOp8SuffixEe> suffixEEList = op8Suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffixEE : suffixEEList) {
            if (suffixEE.getOp3Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp3(lastSrc, suffixGuessType, suffixEE.getOp3Suffix());
                lastSrc = suffixEE.getOp3Suffix();
            } else if (suffixEE.getOp4Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp4(lastSrc, suffixGuessType, suffixEE.getOp4Suffix());
                lastSrc = suffixEE.getOp4Suffix();
            } else if (suffixEE.getOp5Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp5(lastSrc, suffixGuessType, suffixEE.getOp5Suffix());
                lastSrc = suffixEE.getOp5Suffix();
            } else if (suffixEE.getOp6Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp6(lastSrc, suffixGuessType, suffixEE.getOp6Suffix());
                lastSrc = suffixEE.getOp6Suffix();
            } else if (suffixEE.getOp7Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp7(lastSrc, suffixGuessType, suffixEE.getOp7Suffix());
                lastSrc = suffixEE.getOp7Suffix();
            } else {
                throw new ALittleElementException(suffixEE, "未知的表达式");
            }
        }

        return guessSoftTypeForOp8Impl(opString, leftSrc, leftGuess, lastSrc, suffixGuessType, op8Suffix);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp7Impl(String opString
                                                    , PsiElement leftSrc, PsiElement leftGuess
                                                    , PsiElement rightSrc, PsiElement rightGuess
                                                    , ALittleOp7Suffix op_7_suffix) throws ALittleElementException {
        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;

        if (!leftGuessInfo.value.equals("bool")) {
            throw new ALittleElementException(leftSrc, opString + "运算符左边必须是bool或者any类型.不能是:" + leftGuessInfo.value);
        }

        if (!rightGuessInfo.value.equals("bool")) {
            throw new ALittleElementException(rightSrc, opString + "运算符右边边必须是bool或者any类型.不能是:" + rightGuessInfo.value);
        }

        return leftGuess;
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp7(PsiElement leftSrc, PsiElement leftGuess, ALittleOp7Suffix op_7_suffix) throws ALittleElementException {
        String opString = op_7_suffix.getOp7().getText();

        PsiElement valueFactor_guess = null;
        PsiElement lastSrc = null;
        if (op_7_suffix.getValueFactor() != null) {
            ALittleValueFactor valueFactor = op_7_suffix.getValueFactor();
            valueFactor_guess = guessSoftType(valueFactor, valueFactor);
            lastSrc = valueFactor;
        } else if (op_7_suffix.getOp2Value() != null) {
            ALittleOp2Value op2Value = op_7_suffix.getOp2Value();
            valueFactor_guess = guessSoftType(op2Value, op2Value);
            lastSrc = op2Value;
        }

        PsiElement suffixGuessType = valueFactor_guess;
        List<ALittleOp7SuffixEe> suffixEEList = op_7_suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffixEE : suffixEEList) {
            if (suffixEE.getOp3Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp3(lastSrc, suffixGuessType, suffixEE.getOp3Suffix());
                lastSrc = suffixEE.getOp3Suffix();
            } else if (suffixEE.getOp4Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp4(lastSrc, suffixGuessType, suffixEE.getOp4Suffix());
                lastSrc = suffixEE.getOp4Suffix();
            } else if (suffixEE.getOp5Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp5(lastSrc, suffixGuessType, suffixEE.getOp5Suffix());
                lastSrc = suffixEE.getOp5Suffix();
            } else if (suffixEE.getOp6Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp6(lastSrc, suffixGuessType, suffixEE.getOp6Suffix());
                lastSrc = suffixEE.getOp6Suffix();
            } else {
                throw new ALittleElementException(suffixEE, "未知的表达式");
            }
        }
        return guessSoftTypeForOp7Impl(opString, leftSrc, leftGuess, lastSrc, suffixGuessType, op_7_suffix);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp6Impl(String opString
                                                    , PsiElement leftSrc, PsiElement leftGuess
                                                    , PsiElement rightSrc, PsiElement rightGuess
                                                    , ALittleOp6Suffix op6Suffix) throws ALittleElementException {
        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;

        if (opString.equals("==") || opString.equals("!=")) {
            return op6Suffix;
        } else {
            if (leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64") || leftGuessInfo.value.equals("double")) {
                if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64") || rightGuessInfo.value.equals("double")) {
                    return op6Suffix;
                }

                throw new ALittleElementException(rightSrc, opString + "运算符左边是数字，那么右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }

            if (leftGuessInfo.value.equals("string")) {
                if (rightGuessInfo.value.equals("string")) {
                    return op6Suffix;
                }
                
                throw new ALittleElementException(rightSrc, opString + "运算符左边是字符串，那么右边必须是string,any类型.不能是:" + rightGuessInfo.value);
            }

            throw new ALittleElementException(leftSrc, opString + "运算符左边必须是int,double,string,any类型.不能是:" + leftGuessInfo.value);
        }
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp6(PsiElement leftSrc, PsiElement leftGuess, ALittleOp6Suffix op6Suffix) throws ALittleElementException {
        String opString = op6Suffix.getOp6().getText();

        PsiElement valueFactor_guess = null;
        PsiElement lastSrc = null;
        if (op6Suffix.getValueFactor() != null) {
            ALittleValueFactor valueFactor = op6Suffix.getValueFactor();
            valueFactor_guess = guessSoftType(valueFactor, valueFactor);
            lastSrc = valueFactor;
        } else if (op6Suffix.getOp2Value() != null) {
            ALittleOp2Value op2Value = op6Suffix.getOp2Value();
            valueFactor_guess = guessSoftType(op2Value, op2Value);
            lastSrc = op2Value;
        }

        PsiElement suffixGuessType = valueFactor_guess;
        List<ALittleOp6SuffixEe> suffixEEList = op6Suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffixEE : suffixEEList) {
            if (suffixEE.getOp3Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp3(lastSrc, suffixGuessType, suffixEE.getOp3Suffix());
                lastSrc = suffixEE.getOp3Suffix();
            } else if (suffixEE.getOp4Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp4(lastSrc, suffixGuessType, suffixEE.getOp4Suffix());
                lastSrc = suffixEE.getOp4Suffix();
            } else if (suffixEE.getOp5Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp5(lastSrc, suffixGuessType, suffixEE.getOp5Suffix());
                lastSrc = suffixEE.getOp5Suffix();
            } else {
                throw new ALittleElementException(suffixEE, "未知的表达式");
            }
        }
        return guessSoftTypeForOp6Impl(opString, leftSrc, leftGuess, lastSrc, suffixGuessType, op6Suffix);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp5Impl(String opString
                                                    , PsiElement leftSrc, PsiElement leftGuess
                                                    , PsiElement rightSrc, PsiElement rightGuess
                                                    , ALittleOp5Suffix op5Suffix) throws ALittleElementException {
        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;


        boolean left_check = leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64") || leftGuessInfo.value.equals("double") ||  leftGuessInfo.value.equals("string");
        if (!left_check) {
            throw new ALittleElementException(leftSrc, opString + "运算符左边必须是int,double,string,any类型.不能是:" + leftGuessInfo.value);
        }

        boolean right_check = rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64") || rightGuessInfo.value.equals("double") ||  rightGuessInfo.value.equals("string");
        if (!right_check) {
            throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,string,any类型.不能是:" + rightGuessInfo.value);
        }

        return op5Suffix;
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp5(PsiElement leftSrc, PsiElement leftGuess, ALittleOp5Suffix op5Suffix) throws ALittleElementException {
        String opString = op5Suffix.getOp5().getText();

        PsiElement valueFactor_guess = null;
        PsiElement lastSrc = null;
        if (op5Suffix.getValueFactor() != null) {
            ALittleValueFactor valueFactor = op5Suffix.getValueFactor();
            valueFactor_guess = guessSoftType(valueFactor, valueFactor);
            lastSrc = valueFactor;
        } else if (op5Suffix.getOp2Value() != null) {
            ALittleOp2Value op2Value = op5Suffix.getOp2Value();
            valueFactor_guess = guessSoftType(op2Value, op2Value);
            lastSrc = op2Value;
        }

        PsiElement suffixGuessType = valueFactor_guess;
        List<ALittleOp5SuffixEe> suffixEEList = op5Suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffixEE : suffixEEList) {
            if (suffixEE.getOp3Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp3(lastSrc, suffixGuessType, suffixEE.getOp3Suffix());
                lastSrc = suffixEE.getOp3Suffix();
            } else if (suffixEE.getOp4Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp4(lastSrc, suffixGuessType, suffixEE.getOp4Suffix());
                lastSrc = suffixEE.getOp4Suffix();
            } else {
                throw new ALittleElementException(suffixEE, "未知的表达式");
            }
        }

        return guessSoftTypeForOp5Impl(opString, leftSrc, leftGuess, lastSrc, suffixGuessType, op5Suffix);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp4Impl(String opString
                                                    , PsiElement leftSrc, PsiElement leftGuess
                                                    , PsiElement rightSrc, PsiElement rightGuess
                                                    , ALittleOp4Suffix op4Suffix) throws ALittleElementException {
        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;

        if (leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                return leftGuess;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuess;
            } else {
                throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        if (leftGuessInfo.value.equals("double")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                return leftGuess;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuess;
            } else {
                throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        throw new ALittleElementException(leftSrc, opString + "运算符左边必须是int,double,any类型.不能是:" + leftGuessInfo.value);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp4(PsiElement leftSrc, PsiElement leftGuess, ALittleOp4Suffix op4Suffix) throws ALittleElementException {
        String opString = op4Suffix.getOp4().getText();

        PsiElement valueFactor_guess = null;
        PsiElement lastSrc = null;
        if (op4Suffix.getValueFactor() != null) {
            ALittleValueFactor valueFactor = op4Suffix.getValueFactor();
            valueFactor_guess = guessSoftType(valueFactor, valueFactor);
            lastSrc = valueFactor;
        } else if (op4Suffix.getOp2Value() != null) {
            ALittleOp2Value op2Value = op4Suffix.getOp2Value();
            valueFactor_guess = guessSoftType(op2Value, op2Value);
            lastSrc = op2Value;
        }

        PsiElement suffixGuessType = valueFactor_guess;
        List<ALittleOp4SuffixEe> suffixEEList = op4Suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffixEE : suffixEEList) {
            if (suffixEE.getOp3Suffix() != null) {
                suffixGuessType = guessSoftTypeForOp3(lastSrc, suffixGuessType, suffixEE.getOp3Suffix());
                lastSrc = suffixEE.getOp3Suffix();
            } else {
                throw new ALittleElementException(suffixEE, "未知的表达式");
            }
        }

        return guessSoftTypeForOp4Impl(opString, leftSrc, leftGuess, lastSrc, suffixGuessType, op4Suffix);
    }

    @NotNull
    public static PsiElement guessSoftTypeForOp3(PsiElement leftSrc, PsiElement leftGuess, ALittleOp3Suffix op_3_suffix) throws ALittleElementException {
        String opString = op_3_suffix.getOp3().getText();

        PsiElement rightGuess = null;
        PsiElement rightSrc = null;
        if (op_3_suffix.getValueFactor() != null) {
            ALittleValueFactor value = op_3_suffix.getValueFactor();
            rightGuess = guessSoftType(value, value);
            rightSrc = value;
        } else if (op_3_suffix.getOp2Value() != null) {
            ALittleOp2Value value = op_3_suffix.getOp2Value();
            rightGuess = guessSoftType(value, value);
            rightSrc = value;
        } else {
            throw new ALittleElementException(op_3_suffix, "未知的表达式");
        }

        GuessTypeInfo leftGuessInfo = guessTypeString(leftSrc, leftGuess, null);
        GuessTypeInfo rightGuessInfo = guessTypeString(rightSrc, rightGuess, null);

        if (leftGuessInfo.value.equals("any")) return leftGuess;
        if (rightGuessInfo.value.equals("any")) return rightGuess;

        if (leftGuessInfo.value.equals("int")) {
            if (rightGuessInfo.value.equals("int")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return op_3_suffix;
                }
                return leftGuess;
            } else if (rightGuessInfo.value.equals("I64")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return op_3_suffix;
                }
                return rightGuess;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuess;
            } else {
                throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        if (leftGuessInfo.value.equals("I64")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return op_3_suffix;
                }
                return leftGuess;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuess;
            } else {
                throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        if (leftGuessInfo.value.equals("double")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                return leftGuess;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuess;
            } else {
                throw new ALittleElementException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        throw new ALittleElementException(leftSrc, opString + "运算符左边必须是int,double,any类型.不能是:" + leftGuessInfo.value);
    }

    public static boolean guessSoftTypeEqual(PsiElement src_left, PsiElement left, GuessTypeInfo leftGuessInfo
                                            , PsiElement src_right, PsiElement right, GuessTypeInfo rightGuessInfo) throws ALittleElementException {
        if (leftGuessInfo == null)
            leftGuessInfo = guessTypeString(src_left, left, null);

        if (rightGuessInfo == null)
            rightGuessInfo = guessTypeString(src_right, right, null);

        // 如果字符串直接相等，那么就直接返回成功
        if (leftGuessInfo.value.equals(rightGuessInfo.value)) return true;

        // 如果任何一方是any，那么就认为可以相等
        if (leftGuessInfo.value.equals("any") || rightGuessInfo.value.equals("any")) return true;
        // 如果值等于null，那么可以赋值
        if (rightGuessInfo.value.equals("null")) return true;

        if (leftGuessInfo.value.equals("bool")) {
            throw new ALittleElementException(src_right, "要求是bool,不能是:" + rightGuessInfo.value);
        }

        if (leftGuessInfo.value.equals("int")) {
            if (rightGuessInfo.value.equals("I64")) {
                throw new ALittleElementException(src_right, "I64赋值给int，需要使用cast<int>()做强制类型转换");
            }

            if (rightGuessInfo.value.equals("double")) {
                throw new ALittleElementException(src_right, "double赋值给int，需要使用cast<int>()做强制类型转换");
            }
            throw new ALittleElementException(src_right, "要求是int, 不能是:" + rightGuessInfo.value);
        }

        if (leftGuessInfo.value.equals("I64")) {
            if (rightGuessInfo.value.equals("int")) return true;

            if (rightGuessInfo.value.equals("double")) {
                throw new ALittleElementException(src_right, "double赋值给I64，需要使用cast<I64>()做强制类型转换");
            }
            throw new ALittleElementException(src_right, "要求是I64, 不能是:" + rightGuessInfo.value);
        }

        if (leftGuessInfo.value.equals("double")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) return true;
            throw new ALittleElementException(src_right, "要求是I64, 不能是:" + rightGuessInfo.value);
        }

        if (leftGuessInfo.value.equals("string")) {
            throw new ALittleElementException(src_right, "要求是string,不能是:" + rightGuessInfo.value);
        }

        // 通用类型Map
        if (leftGuessInfo.type == GuessType.GT_MAP) {
            if (rightGuessInfo.type == GuessType.GT_MAP) {
                if (guessSoftTypeEqual(src_left, null, leftGuessInfo.mapKeyType, src_right, null, rightGuessInfo.mapKeyType)
                && guessSoftTypeEqual(src_left, null, leftGuessInfo.mapValueType, src_right, null, rightGuessInfo.mapValueType))
                    return true;
            }
        }

        // 通用类型List
        if (leftGuessInfo.type == GuessType.GTList) {
            if (rightGuessInfo.type == GuessType.GTList) {
                if (guessSoftTypeEqual(src_left, null, leftGuessInfo.listSubType, src_right, null, rightGuessInfo.listSubType))
                    return true;
            }
        }

        // 通用类型Functor
        if (leftGuessInfo.type == GuessType.GT_FUNCTOR) {
            if (rightGuessInfo.type == GuessType.GT_FUNCTOR) {
                if (leftGuessInfo.functorParamList.size() == rightGuessInfo.functorParamList.size()
                && leftGuessInfo.functorReturnList.size() == rightGuessInfo.functorReturnList.size()
                && leftGuessInfo.functorAwait == rightGuessInfo.functorAwait) {
                    boolean result = true;
                    if (result) {
                        for (int i = 0; i < leftGuessInfo.functorParamList.size(); ++i) {
                            if (!guessSoftTypeEqual(src_left, null, leftGuessInfo.functorParamList.get(i)
                                                , src_right, null, rightGuessInfo.functorParamList.get(i))) {
                                result = false;
                                break;
                            }
                        }
                    }
                    if (result) {
                        for (int i = 0; i < leftGuessInfo.functorReturnList.size(); ++i) {
                            if (!guessSoftTypeEqual(src_left, null, leftGuessInfo.functorReturnList.get(i)
                                                , src_right, null, rightGuessInfo.functorReturnList.get(i))) {
                                result = false;
                                break;
                            }
                        }
                    }
                    if (result) return true;
                }
            }
        }

        if (left == null && leftGuessInfo.type == GuessType.GT_CLASS) {
            String[] left_type = leftGuessInfo.value.split("\\.");
            if (left_type.length == 2) {
                List<ALittleClassNameDec> list = ALittleTreeChangeListener.findClassNameDecList(src_left.getProject(), left_type[0], left_type[1]);
                if (list != null && list.size() == 1) left = list.get(0).getParent();
            }
        }

        if (left == null && leftGuessInfo.type == GuessType.GT_STRUCT) {
            String[] left_type = leftGuessInfo.value.split("\\.");
            if (left_type.length == 2) {
                List<ALittleStructNameDec> list = ALittleTreeChangeListener.findStructNameDecList(src_left.getProject(), left_type[0], left_type[1]);
                if (list != null && list.size() == 1) left = list.get(0).getParent();
            }
        }

        if (right == null && rightGuessInfo.type == GuessType.GT_CLASS) {
            String[] right_type = rightGuessInfo.value.split("\\.");
            if (right_type.length == 2) {
                List<ALittleClassNameDec> list = ALittleTreeChangeListener.findClassNameDecList(src_right.getProject(), right_type[0], right_type[1]);
                if (list != null && list.size() == 1) right = list.get(0).getParent();
            }
        }

        if (right == null && rightGuessInfo.type == GuessType.GT_STRUCT) {
            String[] right_type = rightGuessInfo.value.split("\\.");
            if (right_type.length == 2) {
                List<ALittleStructNameDec> list = ALittleTreeChangeListener.findStructNameDecList(src_right.getProject(), right_type[0], right_type[1]);
                if (list != null && list.size() == 1) right = list.get(0).getParent();
            }
        }

        // 自定义类型 (只有left继承了right，或者right继承了left，才可以赋值)
        if (left instanceof ALittleClassDec) {
            if (!(right instanceof ALittleClassDec)) {
                throw new ALittleElementException(src_right, "要求是" + leftGuessInfo.value + ",不能是:" + rightGuessInfo.value);
            }

            if (left.equals(right)) return true;

            if (IsClassSuper(left, right)) return true;
            if (IsClassSuper(right, left)) return true;

        } else if (left instanceof ALittleStructDec) {
            if (!(right instanceof ALittleStructDec)) {
                throw new ALittleElementException(src_right, "要求是" + leftGuessInfo.value + ",不能是:" + rightGuessInfo.value);
            }

            if (left.equals(right)) return true;

            if (IsStructSuper(left, right)) return true;
            if (IsStructSuper(right, left)) return true;
        }

        throw new ALittleElementException(src_right, "要求是" + leftGuessInfo.value + ",不能是:" + rightGuessInfo.value);
    }

    @NotNull
    public static String GenerateStructForJsonProto(ALittleStructDec root, String pre_tab) throws Exception {
        ALittleStructProtocolDec protocolDec = root.getStructProtocolDec();
        // 如果没有带协议标志，那么就不需要生成协议
        if (protocolDec == null) return "";

        ALittleCustomType custom_type = protocolDec.getCustomType();
        if (custom_type == null) return "";

        PsiElement guessType = custom_type.getCustomTypeNameDec().guessType();
        if (!(guessType instanceof ALittleEnumDec))
            throw new Exception(root.getText() + "struct的(XXX)内必须使用enum");

        ALittleEnumDec enumDec = (ALittleEnumDec)guessType;
        if (enumDec.getEnumProtocolDec() == null)
            throw new Exception(root.getText() + "struct的(XXX)内必须使用带protocol的enum");

        // 协议名
        ALittleStructNameDec nameDec = root.getStructNameDec();
        if (nameDec == null)
            throw new Exception(root.getText() + "没有定义协议名");

        String message_name = nameDec.getIdContent().getText();

        // 协议ID
        Integer result = ALittleUtil.getEnumVarValue(enumDec, "_" + message_name);
        if (result == null)
            throw new Exception(root.getText() + "找不到协议ID:_" + message_name);

        String json = "{";
            json += "\"id\":" + result + ",";
            json += "\"name\":\"" + message_name + "\",";
            json += "\"varNameList\":[";
                List<String> varNameList = new ArrayList<>();
                for (ALittleStructVarDec varDec : root.getStructVarDecList()) {
                    ALittleStructVarNameDec varNameDec = varDec.getStructVarNameDec();
                    if (varNameDec == null) {
                        throw new Exception(varDec.getText() + "没有定义字段名");
                    }
                    varNameList.add("\"" + varNameDec.getText() + "\"");
                }
                json += String.join(",", varNameList);
            json += "],";
            json += "\"var_typeList\":[";
                List<String> var_typeList = new ArrayList<>();
                for (ALittleStructVarDec varDec : root.getStructVarDecList()) {
                    ALittleAllType allType = varDec.getAllType();
                    if (allType == null) {
                        throw new Exception(varDec.getText() + "没有定义字段类型");
                    }
                    String var_type = allType.getText();
                    // 把空格替换掉
                    var_type = var_type.replace(" ", "");
                    // 不能支持任何any字段
                    if (var_type.equals("any")
                        || var_type.contains("<any")
                        || var_type.contains("any>")) {
                        throw new Exception(varDec.getText() + "协议不支持any类型");
                    }
                    var_typeList.add("\"" + var_type + "\"");
                }
                json += String.join(",", var_typeList);
            json += "]";
        json += "}";

        return json;
    }

    @NotNull
    public static String GenerateStructForCPPProto(ALittleStructDec root, @NotNull List<String> classList, String pre_tab) throws Exception {
        ALittleStructProtocolDec protocolDec = root.getStructProtocolDec();
        // 如果没有带协议标志，那么就不需要生成协议
        if (protocolDec == null) return "";

        // 协议名
        ALittleStructNameDec nameDec = root.getStructNameDec();
        if (nameDec == null)
            throw new Exception(root.getText() + "没有定义协议名");
        // 协议名
        String message_name = nameDec.getIdContent().getText();

        ALittleCustomType custom_type = protocolDec.getCustomType();
        boolean is_json = custom_type == null;
        if (custom_type != null) {
            PsiElement guessType = custom_type.getCustomTypeNameDec().guessType();
            if (!(guessType instanceof ALittleEnumDec))
                throw new Exception(root.getText() + "struct的(XXX)内必须使用enum");

            ALittleEnumDec enumDec = (ALittleEnumDec)guessType;
            if (enumDec.getEnumProtocolDec() == null)
                throw new Exception(root.getText() + "struct的(XXX)内必须使用带protocol的enum");

            // 协议ID
            Integer result = ALittleUtil.getEnumVarValue(enumDec, "_" + message_name);
            if (result == null)
                throw new Exception(root.getText() + "找不到协议ID:_" + message_name);
        }

        List<String> typedefList = new ArrayList<>();
        String proto = "MESSAGE_MACRO(" + message_name + "\n";
        if (is_json) proto = "JSON_MACRO(" + message_name + "\n";
        for (ALittleStructVarDec varDec : root.getStructVarDecList()) {
            ALittleStructVarNameDec varNameDec = varDec.getStructVarNameDec();
            if (varNameDec == null) {
                throw new Exception(varDec.getText() + "没有定义字段名");
            }
            String varName = varNameDec.getText();
            ALittleAllType allType = varDec.getAllType();
            if (allType == null) {
                throw new Exception(varDec.getText() + "没有定义字段类型");
            }
            String var_type = allType.getText();
            // 把空格替换掉
            var_type = var_type.replace(" ", "");
            var_type = var_type.replace("List<", "std::vector<");
            var_type = var_type.replace("Map<", "std::map<");
            if (var_type.equals("string")) var_type = "std::string";
            var_type = var_type.replace("<string>", "<std::string>");
            var_type = var_type.replace(",string>", ",std::string>");
            var_type = var_type.replace("<string,", "<std::string,");

            // 找出map定义，那么就直接使用预定义
            int index = var_type.indexOf("std::map<");
            if (index >= 0) {
                String typedef = (message_name + "_" + varName).toUpperCase();
                typedefList.add("typedef " + var_type + " " + typedef + ";");
                var_type = typedef;
            }

            proto += "\t, " + var_type + ", " + varNameDec.getText() + "\n";
        }
        proto += ");\n";

        if (!typedefList.isEmpty())
            proto = String.join("\n", typedefList) + "\n" + proto;

        classList.add("class " + message_name + ";");
        return proto;
    }

    @NotNull
    public static String GenerateEnumForCPPProto(ALittleEnumDec root, String pre_tab) throws Exception {
        // 如果没有带协议标志，那么就不需要生成协议
        if (root.getEnumProtocolDec() == null) return "";

        // 协议名
        ALittleEnumNameDec nameDec = root.getEnumNameDec();
        if (nameDec == null)
            throw new Exception(root.getText() + "没有定义枚举名");

        // 协议名
        String enum_name = nameDec.getIdContent().getText();

        String proto = "enum " + enum_name + "\n";
        proto += "{\n";
        for (ALittleEnumVarDec varDec : root.getEnumVarDecList()) {
            ALittleEnumVarNameDec varNameDec = varDec.getEnumVarNameDec();
            if (varNameDec == null) {
                throw new Exception(varDec.getText() + "没有定义字段名");
            }
            ALittleEnumVarValueDec varValueDec = varDec.getEnumVarValueDec();
            if (varValueDec != null && varValueDec.getStringContent() != null) {
                throw new Exception(varDec.getText() + "协议枚举的值不能是字符串");
            }
            proto += "\t" + varDec.getText() + ",\n";
        }
        proto += "};\n";
        return proto;
    }
}
