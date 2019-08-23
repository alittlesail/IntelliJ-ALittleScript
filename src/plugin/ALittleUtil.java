package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import javax.swing.*;
import java.util.*;

public class ALittleUtil {

    // 查找所有的变量名节点对象
    private static void findStructVarNameDecList(Project project, PsiFile psiFile, String namespace, String structName, String varName, @NotNull List<ALittleStructVarDec> result, int deep)
    {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleStructNameDec> nameDecList = ALittleTreeChangeListener.findStructNameDecList(project, psiFile, namespace, structName);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleStructDec structDec = (ALittleStructDec)nameDecList.get(0).getParent();

        findStructVarNameDecList(project, psiFile, namespace, structDec, varName, result, deep);
    }

    public static void findStructVarNameDecList(Project project, PsiFile psiFile, String namespace, ALittleStructDec structDec, String varName, @NotNull List<ALittleStructVarDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (structDec == null) return;

        // 处理继承
        ALittleStructExtendsDec structExtendsDec = structDec.getStructExtendsDec();
        if (structExtendsDec != null) {
            ALittleNamespaceNameDec structExtendsNamespaceNameDec = structExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (structExtendsNamespaceNameDec != null) namespaceName = structExtendsNamespaceNameDec.getText();
            ALittleStructNameDec structNameDec = structExtendsDec.getStructNameDec();
            if (structNameDec != null) {
                findStructVarNameDecList(project, psiFile, namespaceName, structNameDec.getText(), varName, result, deep - 1);
            }
        }

        // 处理成员变量
        List<ALittleStructVarDec> structVarDecList = structDec.getStructVarDecList();
        for (ALittleStructVarDec structVarDec : structVarDecList) {
            PsiElement varNameDec = structVarDec.getIdContent();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getText().equals(varName)) {
                result.add(structVarDec);
            }
        }
    }

    public static void findEnumVarNameDecList(ALittleEnumDec enumDec, String varName, @NotNull List<ALittleEnumVarDec> result) {
        if (enumDec == null) return;

        // 处理成员变量
        List<ALittleEnumVarDec> enumVarDecList = enumDec.getEnumVarDecList();
        for (ALittleEnumVarDec enumVarDec : enumVarDecList) {
            PsiElement varNameDec = enumVarDec.getIdContent();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getText().equals(varName)) {
                result.add(enumVarDec);
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
                if (varNameDec.getText().equals(varName)) {
                    return enumValue;
                }
            // 如果没有设定值，那么就默认+1
            } else if (varDec.getStringContent() == null) {
                ++enumValue;
                if (varNameDec.getText().equals(varName)) {
                    return enumValue;
                }
            }
        }
        return null;
    }

    // 查找所有的函数名节点对象
    public static void findMethodNameDecList(Project project, PsiFile psiFile, String namespace, String className, String methodName, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecList(project, psiFile, namespace, classDec, methodName, result, icon, deep);
    }

    public static void findMethodNameDecList(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String methodName, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findMethodNameDecList(project, psiFile, namespaceName, classNameDec.getText(), methodName, result, icon, deep - 1);
            }
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
    private static void findClassVarNameDecList(Project project, PsiFile psiFile, String namespace, String className, String varName, @NotNull List<ALittleClassVarDec> result, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findClassVarNameDecList(project, psiFile, namespace, classDec, varName, result, deep);
    }

    public static void findClassVarNameDecList(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleClassVarDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findClassVarNameDecList(project, psiFile, namespaceName, classNameDec.getText(), varName, result, deep - 1);
            }
        }

        // 处理成员变量
        List<ALittleClassVarDec> classVarDecList = classDec.getClassVarDecList();
        for (ALittleClassVarDec classVarDec : classVarDecList) {
            PsiElement varNameDec = classVarDec.getIdContent();
            if (varNameDec == null) continue;
            if (varName.isEmpty() || varNameDec.getText().equals(varName))
                result.add(classVarDec);
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

    // 查找所有的成员函数节点对象
    private static ALittleClassCtorDec findFirstCtorDecFromExtends(Project project, PsiFile psiFile, String namespace, String className, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return null;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        return findFirstCtorDecFromExtends(project, psiFile, namespace, classDec, deep);
    }

    public static ALittleClassCtorDec findFirstCtorDecFromExtends(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;
        if (classDec == null) return null;

        // 处理成员函数
        List<ALittleClassCtorDec> classCtorDecList = classDec.getClassCtorDecList();
        for (ALittleClassCtorDec classCtorDec : classCtorDecList) {
            return classCtorDec;
        }

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                return findFirstCtorDecFromExtends(project, psiFile, namespaceName, classNameDec.getText(), deep - 1);
            }
        }

        return null;
    }

    // 查找所有的setter节点对象
    private static void findMethodNameDecListForSetter(Project project, PsiFile psiFile, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForSetter(project, psiFile, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForSetter(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findMethodNameDecListForSetter(project, psiFile, namespaceName, classNameDec.getText(), varName, result, deep - 1);
            }
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

    // 查找所有的getter函数节点对象
    private static ALittleMethodNameDec findFirstSetterDecFromExtends(Project project, PsiFile psiFile, String namespace, String className, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return null;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        return findFirstSetterDecFromExtends(project, psiFile, namespace, classDec, varName, deep);
    }

    public static ALittleMethodNameDec findFirstSetterDecFromExtends(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;
        if (classDec == null) return null;

        // 处理成员函数
        List<ALittleClassSetterDec> classSetterDecList = classDec.getClassSetterDecList();
        for (ALittleClassSetterDec classSetterDec : classSetterDecList) {
            ALittleMethodNameDec methodNameDec = classSetterDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (methodNameDec.getIdContent().getText().equals(varName))
                return methodNameDec;
        }

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                return findFirstSetterDecFromExtends(project, psiFile, namespaceName, classNameDec.getText(), varName, deep - 1);
            }
        }

        return null;
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForGetter(Project project, PsiFile psiFile, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForGetter(project, psiFile, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForGetter(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findMethodNameDecListForGetter(project, psiFile, namespaceName, classNameDec.getText(), varName, result, deep - 1);
            }
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

    // 查找所有的getter函数节点对象
    private static ALittleMethodNameDec findFirstGetterDecFromExtends(Project project, PsiFile psiFile, String namespace, String className, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return null;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        return findFirstGetterDecFromExtends(project, psiFile, namespace, classDec, varName, deep);
    }

    public static ALittleMethodNameDec findFirstGetterDecFromExtends(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;
        if (classDec == null) return null;

        // 处理成员函数
        List<ALittleClassGetterDec> classGetterDecList = classDec.getClassGetterDecList();
        for (ALittleClassGetterDec classGetterDec : classGetterDecList) {
            ALittleMethodNameDec methodNameDec = classGetterDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (methodNameDec.getIdContent().getText().equals(varName))
                return methodNameDec;
        }

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                return findFirstGetterDecFromExtends(project, psiFile, namespaceName, classNameDec.getText(), varName, deep - 1);
            }
        }

        return null;
    }

    // 查找所有的成员函数节点对象
    private static void findMethodNameDecListForFun(Project project, PsiFile psiFile, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForFun(project, psiFile, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForFun(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findMethodNameDecListForFun(project, psiFile, namespaceName, classNameDec.getText(), varName, result, deep - 1);
            }
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

    // 查找所有的成员函数节点对象
    private static ALittleMethodNameDec findFirstFunDecFromExtends(Project project, PsiFile psiFile, String namespace, String className, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return null;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        return findFirstFunDecFromExtends(project, psiFile, namespace, classDec, varName, deep);
    }

    public static ALittleMethodNameDec findFirstFunDecFromExtends(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;
        if (classDec == null) return null;

        // 处理成员函数
        List<ALittleClassMethodDec> classMethodDecList = classDec.getClassMethodDecList();
        for (ALittleClassMethodDec classMethodDec : classMethodDecList) {
            ALittleMethodNameDec methodNameDec = classMethodDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (methodNameDec.getIdContent().getText().equals(varName))
                return methodNameDec;
        }

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                return findFirstFunDecFromExtends(project, psiFile, namespaceName, classNameDec.getText(), varName, deep - 1);
            }
        }

        return null;
    }

    // 查找所有的static节点对象
    private static void findMethodNameDecListForStatic(Project project, PsiFile psiFile, String namespace, String className, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        findMethodNameDecListForStatic(project, psiFile, namespace, classDec, varName, result, deep);
    }

    public static void findMethodNameDecListForStatic(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (classDec == null) return;

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                findMethodNameDecListForStatic(project, psiFile, namespaceName, classNameDec.getText(), varName, result, deep - 1);
            }
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

    // 查找所有的静态函数节点对象
    private static ALittleMethodNameDec findFirstStaticDecFromExtends(Project project, PsiFile psiFile, String namespace, String className, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        List<ALittleClassNameDec> nameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespace, className);
        if (nameDecList == null || nameDecList.isEmpty()) return null;
        ALittleClassDec classDec = (ALittleClassDec)nameDecList.get(0).getParent();

        return findFirstStaticDecFromExtends(project, psiFile, namespace, classDec, varName, deep);
    }

    public static ALittleMethodNameDec findFirstStaticDecFromExtends(Project project, PsiFile psiFile, String namespace, ALittleClassDec classDec, String varName, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;
        if (classDec == null) return null;

        // 处理成员函数
        List<ALittleClassStaticDec> classStaticDecList = classDec.getClassStaticDecList();
        for (ALittleClassStaticDec classStaticDec : classStaticDecList) {
            ALittleMethodNameDec methodNameDec = classStaticDec.getMethodNameDec();
            if (methodNameDec == null) continue;
            if (methodNameDec.getIdContent().getText().equals(varName))
                return methodNameDec;
        }

        // 处理继承
        ALittleClassExtendsDec classExtendsDec = classDec.getClassExtendsDec();
        if (classExtendsDec != null) {
            ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
            String namespaceName = namespace;
            if (classExtendsNamespaceNameDec != null) namespaceName = classExtendsNamespaceNameDec.getText();
            ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
            if (classNameDec != null) {
                return findFirstStaticDecFromExtends(project, psiFile, namespaceName, classNameDec.getText(), varName, deep - 1);
            }
        }

        return null;
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

            List<ALittleVarAssignDec> varAssignDecList = varAssignExpr.getVarAssignDecList();
            for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                ALittleVarAssignNameDec varAssignNameDec = varAssignDec.getVarAssignNameDec();
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
        if (psiFile == null) return null;
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
}
