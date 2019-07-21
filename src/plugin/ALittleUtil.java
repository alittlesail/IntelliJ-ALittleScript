package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;

import javax.swing.*;
import java.util.*;

public class ALittleUtil {

    // 查找所有的变量名节点对象
    private static void findStructVarNameDecList(Project project, String src_namespace, String src_struct, String src_name, @NotNull List<ALittleStructVarNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleStructNameDec> name_dec_list = ALittleTreeChangeListener.findStructNameDecList(project, src_namespace, src_struct);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleStructDec struct_dec = (ALittleStructDec)name_dec_list.get(0).getParent();

        findStructVarNameDecList(project, src_namespace, struct_dec, src_name, result, deep);
    }

    public static void findStructVarNameDecList(Project project, String src_namespace, ALittleStructDec struct_dec, String src_name, @NotNull List<ALittleStructVarNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (struct_dec == null) return;

        // 处理继承
        ALittleStructExtendsNameDec struct_extends_name_dec = struct_dec.getStructExtendsNameDec();
        if (struct_extends_name_dec != null) {
            ALittleStructExtendsNamespaceNameDec struct_extends_namespace_name_dec = struct_dec.getStructExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (struct_extends_namespace_name_dec != null) namespace_name = struct_extends_namespace_name_dec.getText();
            findStructVarNameDecList(project, namespace_name, struct_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理成员变量
        List<ALittleStructVarDec> struct_var_dec_list = struct_dec.getStructVarDecList();
        for (ALittleStructVarDec struct_var_dec : struct_var_dec_list) {
            ALittleStructVarNameDec var_name_dec = struct_var_dec.getStructVarNameDec();
            if (var_name_dec != null) {
                if (src_name.isEmpty() || var_name_dec.getIdContent().getText().equals(src_name))
                    result.add(var_name_dec);
            }
        }
    }

    public static void findEnumVarNameDecList(ALittleEnumDec enum_dec, String src_name, @NotNull List<ALittleEnumVarNameDec> result) {
        if (enum_dec == null) return;

        // 处理成员变量
        List<ALittleEnumVarDec> enum_var_dec_list = enum_dec.getEnumVarDecList();
        for (ALittleEnumVarDec enum_var_dec : enum_var_dec_list) {
            ALittleEnumVarNameDec var_name_dec = enum_var_dec.getEnumVarNameDec();
            if (src_name.isEmpty() || var_name_dec.getIdContent().getText().equals(src_name))
                result.add(var_name_dec);
        }
    }

    // 根据枚举字段名，获取对应的值
    public static boolean getEnumVarValue(ALittleEnumDec enum_dec, String var_name, @NotNull List<Integer> result) {
        if (enum_dec == null) return false;

        List<ALittleEnumVarDec> var_dec_list = enum_dec.getEnumVarDecList();
        int enum_value = -1;
        for (ALittleEnumVarDec var_dec : var_dec_list) {
            ALittleEnumVarNameDec var_name_dec = var_dec.getEnumVarNameDec();
            ALittleEnumVarValueDec var_value_dec = var_dec.getEnumVarValueDec();
            if (var_value_dec == null) {
                ++enum_value;
                if (var_name_dec.getIdContent().getText().equals(var_name))
                {
                    result.add(enum_value);
                    return true;
                }
            } else {
                if (var_value_dec.getDigitContent() != null) {
                    String value = var_value_dec.getDigitContent().getText();
                    if (!isInt(value)) {
                        return false;
                    }
                    String number_content = var_value_dec.getText();
                    if (number_content.startsWith("0x"))
                        enum_value = Integer.parseInt(number_content.substring(2), 16);
                    else
                        enum_value = Integer.parseInt(number_content);

                    if (var_name_dec.getIdContent().getText().equals(var_name))
                    {
                        result.add(enum_value);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 查找所有的函数名节点对象
    private static void findMethodNameDecList(Project project, String src_namespace, String src_class, String src_method, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecList(project, src_namespace, class_dec, src_method, result, icon, deep);
    }

    public static void findMethodNameDecList(Project project, String src_namespace, ALittleClassDec class_dec, String src_method, @NotNull List<ALittleMethodNameDec> result, List<Icon> icon, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecList(project, namespace_name, class_extends_name_dec.getText(), src_method, result, icon, deep - 1);
        }

        // 处理成员函数
        List<ALittleClassMethodDec> class_method_dec_list = class_dec.getClassMethodDecList();
        for (ALittleClassMethodDec class_method_dec : class_method_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_method_dec.getMethodNameDec();
            if (class_method_name_dec == null) continue;
            if (src_method.isEmpty() || class_method_name_dec.getIdContent().getText().equals(src_method)) {
                if (icon != null) icon.add(ALittleIcons.MEMBER_METHOD);
                result.add(class_method_name_dec);
            }
        }

        // 处理getter函数
        List<ALittleClassGetterDec> class_getter_dec_list = class_dec.getClassGetterDecList();
        for (ALittleClassGetterDec class_getter_dec : class_getter_dec_list) {
            ALittleMethodNameDec class_getter_name_dec = class_getter_dec.getMethodNameDec();
            if (class_getter_name_dec == null) continue;
            if (src_method.isEmpty() || class_getter_name_dec.getIdContent().getText().equals(src_method)) {
                if (icon != null) icon.add(ALittleIcons.GETTER_METHOD);
                result.add(class_getter_name_dec);
            }
        }

        // 处理setter函数
        List<ALittleClassSetterDec> class_setter_dec_list = class_dec.getClassSetterDecList();
        for (ALittleClassSetterDec class_setter_dec : class_setter_dec_list) {
            ALittleMethodNameDec class_setter_name_dec = class_setter_dec.getMethodNameDec();
            if (class_setter_name_dec == null) continue;
            if (src_method.isEmpty() || class_setter_name_dec.getIdContent().getText().equals(src_method)) {
                if (icon != null) icon.add(ALittleIcons.SETTER_METHOD);
                result.add(class_setter_name_dec);
            }
        }

        // 处理静态函数
        List<ALittleClassStaticDec> class_static_dec_list = class_dec.getClassStaticDecList();
        for (ALittleClassStaticDec class_static_dec : class_static_dec_list) {
            ALittleMethodNameDec class_static_name_dec = class_static_dec.getMethodNameDec();
            if (class_static_name_dec == null) continue;
            if (src_method.isEmpty() || class_static_name_dec.getIdContent().getText().equals(src_method)) {
                if (icon != null) icon.add(ALittleIcons.STATIC_METHOD);
                result.add(class_static_name_dec);
            }
        }
    }

    // 查找所有的变量名节点对象
    private static void findClassVarNameDecList(Project project, String src_namespace, String src_class, String src_name, @NotNull List<ALittleClassVarNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findClassVarNameDecList(project, src_namespace, class_dec, src_name, result, deep);
    }

    public static void findClassVarNameDecList(Project project, String src_namespace, ALittleClassDec class_dec, String src_name, @NotNull List<ALittleClassVarNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findClassVarNameDecList(project, namespace_name, class_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理成员变量
        List<ALittleClassVarDec> class_var_dec_list = class_dec.getClassVarDecList();
        for (ALittleClassVarDec class_var_dec : class_var_dec_list) {
            ALittleClassVarNameDec var_name_dec = class_var_dec.getClassVarNameDec();
            if (var_name_dec != null) {
                if (src_name.isEmpty() || var_name_dec.getIdContent().getText().equals(src_name))
                    result.add(var_name_dec);
            }
        }
    }

    @NotNull
    public static List<ALittleMethodNameDec> filterSameMethodName(@NotNull List<ALittleMethodNameDec> result)
    {
        Set<String> name_set = new HashSet<>();
        List<ALittleMethodNameDec> new_result = new ArrayList<>();
        for (int i = result.size() - 1; i >= 0; --i) {
            String name = result.get(i).getIdContent().getText();
            if (name_set.contains(name)) {
                continue;
            }
            name_set.add(name);
            new_result.add(result.get(i));
        }
        return new_result;
    }

    // 查找所有的setter节点对象
    private static void findMethodNameDecListForSetter(Project project, String src_namespace, String src_class, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecListForSetter(project, src_namespace, class_dec, src_name, result, deep);
    }

    public static void findMethodNameDecListForSetter(Project project, String src_namespace, ALittleClassDec class_dec, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecListForSetter(project, namespace_name, class_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理setter函数
        List<ALittleClassSetterDec> class_setter_dec_list = class_dec.getClassSetterDecList();
        for (ALittleClassSetterDec class_setter_dec : class_setter_dec_list) {
            ALittleMethodNameDec method_name_dec = class_setter_dec.getMethodNameDec();
            if (method_name_dec == null) continue;
            if (src_name.isEmpty() || method_name_dec.getIdContent().getText().equals(src_name))
                result.add(method_name_dec);
        }
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForGetter(Project project, String src_namespace, String src_class, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecListForGetter(project, src_namespace, class_dec, src_name, result, deep);
    }

    public static void findMethodNameDecListForGetter(Project project, String src_namespace, ALittleClassDec class_dec, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecListForGetter(project, namespace_name, class_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理getter函数
        List<ALittleClassGetterDec> class_getter_dec_list = class_dec.getClassGetterDecList();
        for (ALittleClassGetterDec class_getter_dec : class_getter_dec_list) {
            ALittleMethodNameDec method_name_dec = class_getter_dec.getMethodNameDec();
            if (method_name_dec == null) continue;
            if (src_name.isEmpty() || method_name_dec.getIdContent().getText().equals(src_name))
                result.add(method_name_dec);
        }
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForFun(Project project, String src_namespace, String src_class, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecListForFun(project, src_namespace, class_dec, src_name, result, deep);
    }

    public static void findMethodNameDecListForFun(Project project, String src_namespace, ALittleClassDec class_dec, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecListForFun(project, namespace_name, class_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理成员函数
        List<ALittleClassMethodDec> class_method_dec_list = class_dec.getClassMethodDecList();
        for (ALittleClassMethodDec class_method_dec : class_method_dec_list) {
            ALittleMethodNameDec method_name_dec = class_method_dec.getMethodNameDec();
            if (method_name_dec == null) continue;
            if (src_name.isEmpty() || method_name_dec.getIdContent().getText().equals(src_name))
                result.add(method_name_dec);
        }
    }

    // 查找所有的getter节点对象
    private static void findMethodNameDecListForStatic(Project project, String src_namespace, String src_class, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecListForStatic(project, src_namespace, class_dec, src_name, result, deep);
    }

    public static void findMethodNameDecListForStatic(Project project, String src_namespace, ALittleClassDec class_dec, String src_name, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecListForStatic(project, namespace_name, class_extends_name_dec.getText(), src_name, result, deep - 1);
        }

        // 处理惊天函数
        List<ALittleClassStaticDec> class_static_dec_list = class_dec.getClassStaticDecList();
        for (ALittleClassStaticDec class_static_dec : class_static_dec_list) {
            ALittleMethodNameDec method_name_dec = class_static_dec.getMethodNameDec();
            if (method_name_dec == null) continue;
            if (src_name.isEmpty() || method_name_dec.getIdContent().getText().equals(src_name))
                result.add(method_name_dec);
        }
    }

    // 查找函数的参数
    @NotNull
    public static List<ALittleMethodParamNameDec> findMethodParamNameDecList(
            ALittleClassCtorDec class_ctor_dec
            , ALittleClassSetterDec class_setter_dec
            , ALittleClassMethodDec class_method_dec
            , ALittleClassStaticDec class_static_dec
            , ALittleGlobalMethodDec global_method_dec
            , String src_name) {
        List<ALittleMethodParamOneDec> param_one_dec_list = new ArrayList<>();
        if (class_ctor_dec != null) {
            ALittleMethodParamDec method_param_dec = class_ctor_dec.getMethodParamDec();
            if (method_param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_tmp_list = method_param_dec.getMethodParamOneDecList();
                param_one_dec_list.addAll(param_one_dec_tmp_list);
            }
        } else if (class_method_dec != null) {
            ALittleMethodParamDec method_param_dec = class_method_dec.getMethodParamDec();
            if (method_param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_tmp_list = method_param_dec.getMethodParamOneDecList();
                param_one_dec_list.addAll(param_one_dec_tmp_list);
            }
        } else if (class_static_dec != null) {
            ALittleMethodParamDec method_param_dec = class_static_dec.getMethodParamDec();
            if (method_param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_tmp_list = method_param_dec.getMethodParamOneDecList();
                param_one_dec_list.addAll(param_one_dec_tmp_list);
            }
        } else if (class_setter_dec != null) {
            param_one_dec_list.add(class_setter_dec.getMethodParamOneDec());
        } else if (global_method_dec != null) {
            ALittleMethodParamDec method_param_dec = global_method_dec.getMethodParamDec();
            if (method_param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_tmp_list = method_param_dec.getMethodParamOneDecList();
                param_one_dec_list.addAll(param_one_dec_tmp_list);
            }
        }

        List<ALittleMethodParamNameDec> result = new ArrayList<>();
        for (ALittleMethodParamOneDec one_dec : param_one_dec_list) {
            ALittleMethodParamNameDec name_dec = one_dec.getMethodParamNameDec();
            if (name_dec != null) {
                if (src_name.isEmpty() || name_dec.getIdContent().getText().equals(src_name))
                    result.add(name_dec);
            }
        }
        return result;
    }

    // 查找表达式定义的函数名
    @NotNull
    public static List<ALittleVarAssignNameDec> findVarAssignNameDecList(PsiElement element, String src_name) {
        List<ALittleVarAssignNameDec> var_dec_list = new ArrayList<>();

        ALittleAllExpr root_all_expr = null;
        // 计算出所在的表达式
        PsiElement parent = element;
        while (true) {
            if (parent == null) break;
            if (parent instanceof ALittleAllExpr) {
                root_all_expr = (ALittleAllExpr)parent;
                break;
            }
            parent = parent.getParent();
        }
        if (root_all_expr ==  null) return var_dec_list;

        findVarAssignNameDecList(root_all_expr, var_dec_list, src_name);
        return var_dec_list;
    }

    private static void findVarAssignNameDecList(@NotNull ALittleAllExpr all_expr, @NotNull List<ALittleVarAssignNameDec> var_dec_list, @NotNull String src_name) {
        PsiElement parent = all_expr.getParent();

        List<ALittleAllExpr> all_expr_list = null;

        // 如果是函数体，那么就停止
        if (parent instanceof ALittleMethodBodyDec) {
            ALittleMethodBodyDec cur_expr = (ALittleMethodBodyDec)parent;
            all_expr_list = cur_expr.getAllExprList();

        } else if (parent instanceof ALittleForExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), var_dec_list, src_name);
            ALittleForExpr cur_expr = (ALittleForExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
            // for 内部有一个临时变量
            ALittleForStepCondition step_condition = cur_expr.getForStepCondition();
            ALittleForInCondition in_condition = cur_expr.getForInCondition();
            if (step_condition != null)
            {
                ALittleForStartStat start_stat = step_condition.getForStartStat();
                if (start_stat != null) {
                    ALittleVarAssignNameDec var_assign_name_dec = start_stat.getForPairDec().getVarAssignNameDec();
                    if (var_assign_name_dec != null) {
                        String name = var_assign_name_dec.getIdContent().getText();
                        if (src_name.isEmpty() || name.equals(src_name))
                            var_dec_list.add(var_assign_name_dec);
                    }
                }
            } else if (in_condition != null) {
                List<ALittleForPairDec> pair_dec_list = in_condition.getForPairDecList();
                for (ALittleForPairDec pair_dec : pair_dec_list) {
                    ALittleVarAssignNameDec var_assign_name_dec = pair_dec.getVarAssignNameDec();
                    if (var_assign_name_dec != null) {
                        String name = var_assign_name_dec.getIdContent().getText();
                        if (src_name.isEmpty() || name.equals(src_name))
                            var_dec_list.add(var_assign_name_dec);
                    }
                }
            }
        } else if (parent instanceof ALittleWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), var_dec_list, src_name);
            ALittleWhileExpr cur_expr = (ALittleWhileExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        } else if (parent instanceof ALittleDoWhileExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), var_dec_list, src_name);
            ALittleDoWhileExpr cur_expr = (ALittleDoWhileExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        } else if (parent instanceof ALittleIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), var_dec_list, src_name);
            ALittleIfExpr cur_expr = (ALittleIfExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        } else if (parent instanceof ALittleElseIfExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), var_dec_list, src_name);
            ALittleElseIfExpr cur_expr = (ALittleElseIfExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        } else if (parent instanceof ALittleElseExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent().getParent(), var_dec_list, src_name);
            ALittleElseExpr cur_expr = (ALittleElseExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        } else if (parent instanceof ALittleWrapExpr) {
            findVarAssignNameDecList((ALittleAllExpr)parent.getParent(), var_dec_list, src_name);
            ALittleWrapExpr cur_expr = (ALittleWrapExpr)parent;
            all_expr_list = cur_expr.getAllExprList();
        }

        if (all_expr_list == null) return;

        for (ALittleAllExpr expr : all_expr_list) {
            if (expr.equals(all_expr)) return;

            ALittleVarAssignExpr var_assign_expr = expr.getVarAssignExpr();
            if (var_assign_expr == null) continue;

            List<ALittleVarAssignPairDec> var_assign_pair_dec_list = var_assign_expr.getVarAssignPairDecList();
            for (ALittleVarAssignPairDec var_assign_pair_dec : var_assign_pair_dec_list) {
                ALittleVarAssignNameDec var_assign_name_dec = var_assign_pair_dec.getVarAssignNameDec();
                if (var_assign_name_dec != null) {
                    String name = var_assign_name_dec.getIdContent().getText();
                    if (src_name.isEmpty() || name.equals(src_name))
                        var_dec_list.add(var_assign_name_dec);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 从文件中获取AlittleNamespaceDec
    @NotNull
    public static List<ALittleNamespaceDec> getNamespaceDec(PsiFile psi_file) {
        List<ALittleNamespaceDec> namespace_dec_list = new ArrayList<>();
        for(PsiElement child = psi_file.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                namespace_dec_list.add((ALittleNamespaceDec)child);
            }
        }
        return namespace_dec_list;
    }

    // 根据文件对象获取命名域
    @NotNull
    public static String getNamespaceName(ALittleFile alittleFile) {
        List<ALittleNamespaceDec> namespace_list = getNamespaceDec(alittleFile);
        for (ALittleNamespaceDec namespace_dec : namespace_list) {
            ALittleNamespaceNameDec namespace_name_dec = namespace_dec.getNamespaceNameDec();
            if (namespace_name_dec == null) continue;
            return namespace_name_dec.getIdContent().getText();
        }
        return "";
    }

    public static boolean isInt(String content) {
        try {
            double v = Double.parseDouble(content);
            return v == Math.floor(v) && !content.contains(".");
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean IsClassSuper(PsiElement child, PsiElement parent) {
        if (!(child instanceof ALittleClassDec)) return false;
        ALittleClassDec child_class = (ALittleClassDec)child;

        ALittleClassExtendsNameDec extends_name_dec = child_class.getClassExtendsNameDec();
        if (extends_name_dec == null) return false;

        PsiElement guess_type = child_class.getClassExtendsNameDec().guessType();
        if (guess_type == null) return false;

        if (guess_type.equals(parent)) return true;

        return IsClassSuper(guess_type, parent);
    }

    public static boolean IsStructSuper(PsiElement child, PsiElement parent) {
        if (!(child instanceof ALittleStructDec)) return false;
        ALittleStructDec child_struct = (ALittleStructDec)child;

        ALittleStructExtendsNameDec extends_name_dec = child_struct.getStructExtendsNameDec();
        if (extends_name_dec == null) return false;

        PsiElement guess_type = child_struct.getStructExtendsNameDec().guessType();
        if (guess_type == null) return false;

        if (guess_type.equals(parent)) return true;

        return IsStructSuper(guess_type, parent);
    }

    public static List<PsiElement> guessTypeForMethodCall(ALittleValueStat value_stat) {
        if (value_stat.getValueFactor() == null) return null;

        ALittleValueFactor value_factor = value_stat.getValueFactor();
        if (value_factor.getValueStatParen() != null)
            return guessTypeForMethodCall(value_factor.getValueStatParen().getValueStat());

        ALittlePropertyValue property_value = value_factor.getPropertyValue();
        if (property_value == null) return null;

        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        if (suffix_list.isEmpty()) return null;

        ALittlePropertyValueSuffix last_suffix = suffix_list.get(suffix_list.size() - 1);
        ALittlePropertyValueMethodCallStat method_call = last_suffix.getPropertyValueMethodCallStat();
        if (method_call == null) return null;

        PsiReference ref = method_call.getReference();
        if (ref instanceof ALittlePropertyValueMethodCallStatReference) {
            ALittlePropertyValueMethodCallStatReference real_ref = (ALittlePropertyValueMethodCallStatReference)ref;
            return real_ref.guessTypes(true);
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static PsiElement guessType(ALittlePropertyValue property_value) {
        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        if (suffix_list.isEmpty()) {
            if (property_value.getPropertyValueThisType() != null) {
                return property_value.getPropertyValueThisType().guessType();
            } else if (property_value.getPropertyValueCastType() != null) {
                return property_value.getPropertyValueCastType().guessType();
            } else if (property_value.getPropertyValueCustomType() != null) {
                return property_value.getPropertyValueCustomType().guessType();
            }
        } else {
            ALittlePropertyValueSuffix suffix = suffix_list.get(suffix_list.size() -1);
            if (suffix.getPropertyValueDotId() != null) {
                return suffix.getPropertyValueDotId().getPropertyValueDotIdName().guessType();
            } else if (suffix.getPropertyValueBrackValueStat() != null) {
                return suffix.getPropertyValueBrackValueStat().guessType();
            } else if (suffix.getPropertyValueMethodCallStat() != null) {
                return suffix.getPropertyValueMethodCallStat().guessType();
            }
        }
        return null;
    }

    public static PsiElement guessType(ALittleAllType all_type) {
        if (all_type == null) return null;

        if (all_type.getCustomType() != null) {
            return all_type.getCustomType().getCustomTypeNameDec().guessType();
        } else if (all_type.getGenericType() != null) {
            return all_type.getGenericType();
        } else if (all_type.getPrimitiveType() != null) {
            return all_type.getPrimitiveType();
        }
        return null;
    }

    public static PsiElement guessType(ALittleMethodReturnTypeDec return_type_dec) {
        return guessType(return_type_dec.getAllType());
    }

    public static PsiElement guessType(ALittleOpNewStat op_new_stat) {
        if (op_new_stat.getCustomType() != null)
            return op_new_stat.getCustomType().getCustomTypeNameDec().guessType();
        else if (op_new_stat.getGenericType() != null)
            return op_new_stat.getGenericType();
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    // 只有new表达式，和factor表达式才会出现可以遍历对象
    public static String CalcPairsType(ALittleValueStat value_stat) {
        List<String> error_content_list = new ArrayList<>();
        List<PsiElement> error_element_list = new ArrayList<>();
        PsiElement guess_type = guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
        if (guess_type == null) return null;

        if (guess_type instanceof ALittleGenericType) {
            ALittleGenericType generic_type = (ALittleGenericType)guess_type;
            if (generic_type.getGenericListType() != null)
                return "__ipairs";
            else if (generic_type.getGenericMapType() != null)
                return "__pairs";
        }

        return "";
    }

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

    // ALittlePropertyValueBrackValueStat 中括号 表示any
    // ALittlePropertyValueMethodCallStat 括号函数调用 表示any
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum GuessType
    {
        GT_CONST,
        GT_PRIMITIVE,
        GT_CLASS,
        GT_STRUCT,
        GT_ENUM,
        GT_MAP,
        GT_LIST,
        GT_FUNCTOR,
    }

    public static class GuessTypeInfo
    {
        public GuessType type;                                 // 类型值为 Const Primitive Class Struct Enum Map List Functor
        public String value;                                // 完整类型的字符串
        public GuessTypeInfo list_sub_type;                 // type="List"时，表示List的子类型
        public GuessTypeInfo map_key_type;                  // type="Map"时, 表示Map的Key
        public GuessTypeInfo map_value_type;                // type="Map"时, 表示Map的Value
        public List<GuessTypeInfo> functor_param_list;      // type="Functor"时, 表示参数列表
        public List<GuessTypeInfo> functor_return_list;     // type="Functor"时, 表示返回值列表
        public List<GuessTypeInfo> list_var_type;           // type="class" 或者 type="struct"，表示成员的类型列表
        public List<String> list_var_name;                  // type="class" 或者 type="struct"，表示成员的变量列表
    }

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
        else if (info.type == GuessType.GT_LIST)
            content += "List";
        else if (info.type == GuessType.GT_MAP)
            content += "Map";
        else if (info.type == GuessType.GT_PRIMITIVE)
            content += "Primitive";
        else if (info.type == GuessType.GT_STRUCT)
            content += "struct";

        content += "\",\"name\":\"" + info.value + "\"";

        if (info.type == GuessType.GT_STRUCT || info.type == GuessType.GT_CLASS) {
            content += ",\"var_list\":[";
            List<String> var_list = new ArrayList<>();
            for (int i = 0; i < info.list_var_type.size(); ++i) {
                String child_content = "{\"type\":" + saveGuessTypeInfoToJson(info.list_var_type.get(i));
                child_content += ",\"name\":\"" + info.list_var_name.get(i) + "\"}";
                var_list.add(child_content);
            }
            content += String.join(",", var_list);
            content += "]";
        } else if (info.type == GuessType.GT_LIST) {
            content += ",\"sub_type\":" + saveGuessTypeInfoToJson(info.list_sub_type);
        } else if (info.type == GuessType.GT_MAP) {
            content += ",\"key_type\":" + saveGuessTypeInfoToJson(info.map_key_type);
            content += ",\"value_type\":" + saveGuessTypeInfoToJson(info.map_value_type);
        }

        content += "}";
        return content;
    }

    public static GuessTypeInfo guessTypeString(PsiElement src, PsiElement element, HashSet<PsiElement> deep_guess
            , @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
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
            return guessTypeString(src, (ALittleGenericType)element, deep_guess, error_content_list, error_element_list);
        } else if (element instanceof ALittlePropertyValueBrackValueStat || element instanceof ALittlePropertyValueMethodCallStat) {
            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_PRIMITIVE;
            info.value = "any";
            return info;
        } else if (element instanceof ALittleClassDec) {
            ALittleClassDec dec = (ALittleClassDec)element;
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleClassNameDec name_dec = dec.getClassNameDec();
            if (name_dec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_CLASS;
                info.value = namespace_name + "." + name_dec.getIdContent().getText();
                // 如果需要深度递归
                if (deep_guess != null) {
                    if (deep_guess.contains(dec)) {
                        error_content_list.add("class member has Recursive definition");
                        error_element_list.add(src);
                        return null;
                    }
                    deep_guess.add(dec);
                    info.list_var_name = new ArrayList<>();
                    info.list_var_type = new ArrayList<>();
                    for (ALittleClassVarDec var_dec : dec.getClassVarDecList()) {
                        ALittleClassVarNameDec var_name_dec = var_dec.getClassVarNameDec();
                        GuessTypeInfo child_info = guessTypeString(src, var_dec.getAllType(), deep_guess, error_content_list, error_element_list);
                        if (child_info == null) return null;
                        if (var_name_dec != null) {
                            info.list_var_type.add(child_info);
                            info.list_var_name.add(var_name_dec.getText());
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
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleStructNameDec name_dec = dec.getStructNameDec();
            if (name_dec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_STRUCT;
                info.value = namespace_name + "." + name_dec.getIdContent().getText();
                // 如果需要深度递归
                if (deep_guess != null) {
                    if (deep_guess.contains(dec)) {
                        error_content_list.add("struct member has Recursive definition");
                        error_element_list.add(src);
                        return null;
                    }
                    deep_guess.add(dec);
                    info.list_var_name = new ArrayList<>();
                    info.list_var_type = new ArrayList<>();
                    for (ALittleStructVarDec var_dec : dec.getStructVarDecList()) {
                        ALittleStructVarNameDec var_name_dec = var_dec.getStructVarNameDec();
                        GuessTypeInfo child_info = guessTypeString(src, var_dec.getAllType(), deep_guess, error_content_list, error_element_list);
                        if (child_info == null) return null;
                        if (var_name_dec != null) {
                            info.list_var_type.add(child_info);
                            info.list_var_name.add(var_name_dec.getText());
                        }
                    }
                }
                return info;
            }
        } else if (element instanceof ALittleEnumDec) {
            ALittleEnumDec dec = (ALittleEnumDec)element;
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleEnumNameDec name_dec = dec.getEnumNameDec();
            if (name_dec != null) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_ENUM;
                info.value = namespace_name + "." + name_dec.getIdContent().getText();
                return info;
            }
        } else if (element instanceof ALittleEnumVarNameDec) {
            ALittleEnumVarNameDec name_dec = (ALittleEnumVarNameDec)element;
            ALittleEnumVarDec dec = (ALittleEnumVarDec)name_dec.getParent();
            ALittleEnumVarValueDec value_dec = dec.getEnumVarValueDec();

            // 如果枚举带有protocol定义，那么就返回错误信息
            ALittleEnumDec enum_dec = (ALittleEnumDec)dec.getParent();
            if (enum_dec.getEnumProtocolDec() != null) {
                error_content_list.add("不能使用带protocol定义的enum的字段");
                error_element_list.add(src);
                return null;
            }

            if (value_dec != null) {
                if (value_dec.getDigitContent() != null) {
                    GuessTypeInfo info = new GuessTypeInfo();
                    info.type = GuessType.GT_PRIMITIVE;
                    info.value = "int";
                    return info;
                }
                if (value_dec.getStringContent() != null) {
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
                ALittleClassGetterDec class_getter_dec = (ALittleClassGetterDec)parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functor_param_list = new ArrayList<>();
                info.functor_return_list = new ArrayList<>();

                // 第一个参数是类
                GuessTypeInfo class_guess_info = guessTypeString(element, class_getter_dec.getParent(), deep_guess, error_content_list, error_element_list);
                if (class_guess_info == null) return null;
                info.functor_param_list.add(class_guess_info);
                info.value += class_guess_info.value + ")";

                List<String> type_list = new ArrayList<>();
                // 添加返回值列表
                ALittleMethodReturnTypeDec return_type_dec = class_getter_dec.getMethodReturnTypeDec();
                if (return_type_dec != null) {
                    ALittleAllType all_type = return_type_dec.getAllType();
                    GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                    if (guess_info == null) return null;
                    type_list.add(guess_info.value);
                    info.functor_return_list.add(guess_info);
                }
                if (!type_list.isEmpty()) info.value += ":";
                info.value += String.join(",", type_list) +  ">";
                return info;
            } else if (parent instanceof ALittleClassSetterDec) {
                ALittleClassSetterDec class_setter_dec = (ALittleClassSetterDec)parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functor_param_list = new ArrayList<>();
                info.functor_return_list = new ArrayList<>();

                List<String> type_list = new ArrayList<>();
                // 第一个参数是类
                GuessTypeInfo class_guess_info = guessTypeString(element, class_setter_dec.getParent(), deep_guess, error_content_list, error_element_list);
                if (class_guess_info == null) return null;
                type_list.add(class_guess_info.value);
                info.functor_param_list.add(class_guess_info);

                // 添加参数列表
                ALittleMethodParamOneDec one_dec = class_setter_dec.getMethodParamOneDec();
                if (one_dec != null) {
                    ALittleAllType all_type = one_dec.getMethodParamTypeDec().getAllType();
                    GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                    if (guess_info == null) return null;
                    type_list.add(guess_info.value);
                    info.functor_param_list.add(guess_info);
                }
                info.value += String.join(",", type_list) + ")";
                info.value += ">";
                return info;
            } else if (parent instanceof ALittleClassMethodDec) {
                ALittleClassMethodDec class_method_dec = (ALittleClassMethodDec)parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functor_param_list = new ArrayList<>();
                info.functor_return_list = new ArrayList<>();

                List<String> type_list = new ArrayList<>();
                // 第一个参数是类
                GuessTypeInfo class_guess_info = guessTypeString(element, class_method_dec.getParent(), deep_guess, error_content_list, error_element_list);
                if (class_guess_info == null) return null;
                type_list.add(class_guess_info.value);
                info.functor_param_list.add(class_guess_info);

                // 添加参数列表
                ALittleMethodParamDec param_dec = class_method_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                        ALittleAllType all_type = one_dec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_param_list.add(guess_info);
                    }
                }
                info.value += String.join(",", type_list) + ")";
                type_list = new ArrayList<>();
                // 添加返回值列表
                ALittleMethodReturnDec return_dec = class_method_dec.getMethodReturnDec();
                if (return_dec != null) {
                    List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                        ALittleAllType all_type = return_type_dec.getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_return_list.add(guess_info);
                    }
                }
                if (!type_list.isEmpty()) info.value += ":";
                info.value += String.join(",", type_list) +  ">";
                return info;
            } else if (parent instanceof ALittleClassStaticDec) {
                ALittleClassStaticDec class_static_dec = (ALittleClassStaticDec)parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functor_param_list = new ArrayList<>();
                info.functor_return_list = new ArrayList<>();

                List<String> type_list = new ArrayList<>();
                ALittleMethodParamDec param_dec = class_static_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                        ALittleAllType all_type = one_dec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_param_list.add(guess_info);
                    }
                }
                info.value += String.join(",", type_list) + ")";
                type_list = new ArrayList<>();
                ALittleMethodReturnDec return_dec = class_static_dec.getMethodReturnDec();
                if (return_dec != null) {
                    List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                        ALittleAllType all_type = return_type_dec.getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_return_list.add(guess_info);
                    }
                }
                if (!type_list.isEmpty()) info.value += ":";
                info.value += String.join(",", type_list) +  ">";
                return info;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec global_method_dec = (ALittleGlobalMethodDec)parent;

                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_FUNCTOR;
                info.value = "Functor<(";
                info.functor_param_list = new ArrayList<>();
                info.functor_return_list = new ArrayList<>();

                List<String> type_list = new ArrayList<>();
                ALittleMethodParamDec param_dec = global_method_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                        ALittleAllType all_type = one_dec.getMethodParamTypeDec().getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_param_list.add(guess_info);
                    }
                }
                info.value += String.join(",", type_list) + ")";
                type_list = new ArrayList<>();
                ALittleMethodReturnDec return_dec = global_method_dec.getMethodReturnDec();
                if (return_dec != null) {
                    List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                    for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                        ALittleAllType all_type = return_type_dec.getAllType();
                        GuessTypeInfo guess_info = guessTypeString(element, all_type, deep_guess, error_content_list, error_element_list);
                        if (guess_info == null) return null;
                        type_list.add(guess_info.value);
                        info.functor_return_list.add(guess_info);
                    }
                }
                if (!type_list.isEmpty()) info.value += ":";
                info.value += String.join(",", type_list) +  ">";
                return info;
            }
        } else if (element instanceof ALittleOpNewList) {
            ALittleOpNewList op_new_list = (ALittleOpNewList)element;
            List<ALittleValueStat> value_stat_list = op_new_list.getValueStatList();
            if (value_stat_list.isEmpty()) {
                GuessTypeInfo info = new GuessTypeInfo();
                info.type = GuessType.GT_LIST;
                info.value = "List<any>";
                info.list_sub_type = new GuessTypeInfo();
                info.list_sub_type.type = GuessType.GT_PRIMITIVE;
                info.list_sub_type.value = "any";
                return info;
            }
            GuessTypeInfo guess_info = guessTypeString(element, value_stat_list.get(0), deep_guess, error_content_list, error_element_list);
            if (guess_info == null) return null;

            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_LIST;
            info.value = "List<" + guess_info.value + ">";
            info.list_sub_type = guess_info;
            return info;
        } else if (element instanceof ALittleValueStat) {
            ALittleValueStat value_stat = (ALittleValueStat)element;
            PsiElement guess_type = guessSoftType(src, value_stat, error_content_list, error_element_list);
            if (guess_type == null) return null;

            return guessTypeString(src, guess_type, deep_guess, error_content_list, error_element_list);
        }

        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static GuessTypeInfo guessTypeString(PsiElement src, ALittleGenericType generic_type, HashSet<PsiElement> deep_guess
            , @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        if (generic_type.getGenericListType() != null) {
            ALittleGenericListType dec = generic_type.getGenericListType();
            GuessTypeInfo guess_info = guessTypeString(src, dec.getAllType(), deep_guess, error_content_list, error_element_list);
            if (guess_info == null) return null;

            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_LIST;
            info.value = "List<" + guess_info.value + ">";
            info.list_sub_type = guess_info;
            return info;
        } else if (generic_type.getGenericMapType() != null) {
            ALittleGenericMapType dec = generic_type.getGenericMapType();
            List<ALittleAllType> all_type_list = dec.getAllTypeList();
            if (all_type_list.size() != 2) return null;
            GuessTypeInfo key_guess_info = guessTypeString(src, all_type_list.get(0), deep_guess, error_content_list, error_element_list);
            GuessTypeInfo value_guess_info = guessTypeString(src, all_type_list.get(1), deep_guess, error_content_list, error_element_list);
            if (key_guess_info == null || value_guess_info == null) return null;

            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_MAP;
            info.value = "Map<" + key_guess_info.value + "," + value_guess_info.value + ">";
            info.map_key_type = key_guess_info;
            info.map_value_type = value_guess_info;
            return info;
        } else if (generic_type.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = generic_type.getGenericFunctorType();
            ALittleGenericFunctorParamType param_type = dec.getGenericFunctorParamType();

            GuessTypeInfo info = new GuessTypeInfo();
            info.type = GuessType.GT_FUNCTOR;
            info.value = "Functor<(";
            info.functor_param_list = new ArrayList<>();
            info.functor_return_list = new ArrayList<>();

            if (param_type != null) {
                List<String> name_list = new ArrayList<>();
                List<ALittleAllType> all_type_list = param_type.getAllTypeList();
                for (ALittleAllType all_type : all_type_list) {
                    GuessTypeInfo guess_info = guessTypeString(src, all_type, deep_guess, error_content_list, error_element_list);
                    if (guess_info == null) return null;
                    name_list.add(guess_info.value);
                    info.functor_param_list.add(guess_info);
                }
                info.value += String.join(",", name_list);
            }
            info.value += ")";
            ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
            if (return_type != null) {
                List<String> name_list = new ArrayList<>();
                List<ALittleAllType> all_type_list = return_type.getAllTypeList();
                for (ALittleAllType all_type : all_type_list) {
                    GuessTypeInfo guess_info = guessTypeString(src, all_type, deep_guess, error_content_list, error_element_list);
                    if (guess_info == null) return null;
                    name_list.add(guess_info.value);
                    info.functor_return_list.add(guess_info);
                }
                if (!name_list.isEmpty()) info.value += ":";
                info.value += String.join(",", name_list);
            }
            info.value += ">";
            return info;
        }
        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static GuessTypeInfo guessTypeString(PsiElement src, ALittleAllType all_type, HashSet<PsiElement> deep_guess
            , @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        PsiElement element = guessType(all_type);
        if (element == null) {
            error_content_list.add("未知的表达式");
            error_element_list.add(src);
            return null;
        }
        return guessTypeString(src, element, deep_guess, error_content_list, error_element_list);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static PsiElement guessSoftTypeForOp8Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp8Suffix op_8_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;

        if (!left_guess_info.value.equals("bool"))
        {
            error_content_list.add(op_string + "运算符左边必须是bool或者any类型.不能是:" + left_guess_info.value);
            error_element_list.add(left_src);
            return null;
        }

        if (!right_guess_info.value.equals("bool"))
        {
            error_content_list.add(op_string + "运算符右边边必须是bool或者any类型.不能是:" + right_guess_info.value);
            error_element_list.add(right_src);
            return null;
        }

        return left_guess;

    }
    public static PsiElement guessSoftTypeForOp8(PsiElement left_src, PsiElement left_guess, ALittleOp8Suffix op_8_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_8_suffix.getOp8().getText();

        PsiElement value_factor_guess = null;
        PsiElement last_src = null;
        if (op_8_suffix.getValueFactor() != null) {
            ALittleValueFactor value_factor = op_8_suffix.getValueFactor();
            value_factor_guess = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = value_factor;
        } else if (op_8_suffix.getOp2Value() != null) {
            ALittleOp2Value op_2_value = op_8_suffix.getOp2Value();
            value_factor_guess = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = op_2_value;
        }

        PsiElement suffix_guess_type = value_factor_guess;
        List<ALittleOp8SuffixEe> suffix_ee_list = op_8_suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp5Suffix();
            } else if (suffix_ee.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ee.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp6Suffix();
            } else if (suffix_ee.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ee.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp7Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ee);
                return null;
            }
        }

        return guessSoftTypeForOp8Impl(op_string, left_src, left_guess, last_src, suffix_guess_type, op_8_suffix, error_content_list, error_element_list);
    }

    public static PsiElement guessSoftTypeForOp7Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp7Suffix op_7_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;

        if (!left_guess_info.value.equals("bool"))
        {
            error_content_list.add(op_string + "运算符左边必须是bool或者any类型.不能是:" + left_guess_info.value);
            error_element_list.add(left_src);
            return null;
        }

        if (!right_guess_info.value.equals("bool"))
        {
            error_content_list.add(op_string + "运算符右边边必须是bool或者any类型.不能是:" + right_guess_info.value);
            error_element_list.add(right_src);
            return null;
        }

        return left_guess;
    }

    public static PsiElement guessSoftTypeForOp7(PsiElement left_src, PsiElement left_guess, ALittleOp7Suffix op_7_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_7_suffix.getOp7().getText();

        PsiElement value_factor_guess = null;
        PsiElement last_src = null;
        if (op_7_suffix.getValueFactor() != null) {
            ALittleValueFactor value_factor = op_7_suffix.getValueFactor();
            value_factor_guess = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = value_factor;
        } else if (op_7_suffix.getOp2Value() != null) {
            ALittleOp2Value op_2_value = op_7_suffix.getOp2Value();
            value_factor_guess = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = op_2_value;
        }

        PsiElement suffix_guess_type = value_factor_guess;
        List<ALittleOp7SuffixEe> suffix_ee_list = op_7_suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp5Suffix();
            } else if (suffix_ee.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ee.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp6Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ee);
                return null;
            }
        }
        return guessSoftTypeForOp7Impl(op_string, left_src, left_guess, last_src, suffix_guess_type, op_7_suffix, error_content_list, error_element_list);
    }

    public static PsiElement guessSoftTypeForOp6Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp6Suffix op_6_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;

        if (op_string.equals("==") || op_string.equals("!=")) {
            return op_6_suffix;
        } else {
            if (left_guess_info.value.equals("int") || left_guess_info.value.equals("I64") || left_guess_info.value.equals("double")) {
                if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64") || right_guess_info.value.equals("double")) {
                    return op_6_suffix;
                }

                error_content_list.add(op_string + "运算符左边是数字，那么右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }

            if (left_guess_info.value.equals("string")) {
                if (right_guess_info.value.equals("string")) {
                    return op_6_suffix;
                }
                error_content_list.add(op_string + "运算符左边是字符串，那么右边必须是string,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }

            error_content_list.add(op_string + "运算符左边必须是int,double,string,any类型.不能是:" + left_guess_info.value);
            error_element_list.add(left_src);
            return null;
        }
    }

    public static PsiElement guessSoftTypeForOp6(PsiElement left_src, PsiElement left_guess, ALittleOp6Suffix op_6_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_6_suffix.getOp6().getText();

        PsiElement value_factor_guess = null;
        PsiElement last_src = null;
        if (op_6_suffix.getValueFactor() != null) {
            ALittleValueFactor value_factor = op_6_suffix.getValueFactor();
            value_factor_guess = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = value_factor;
        } else if (op_6_suffix.getOp2Value() != null) {
            ALittleOp2Value op_2_value = op_6_suffix.getOp2Value();
            value_factor_guess = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = op_2_value;
        }

        PsiElement suffix_guess_type = value_factor_guess;
        List<ALittleOp6SuffixEe> suffix_ee_list = op_6_suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp5Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ee);
                return null;
            }
        }
        return guessSoftTypeForOp6Impl(op_string, left_src, left_guess, last_src, suffix_guess_type, op_6_suffix, error_content_list, error_element_list);
    }

    public static PsiElement guessSoftTypeForOp5Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp5Suffix op_5_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;


        boolean left_check = left_guess_info.value.equals("int") || left_guess_info.value.equals("I64") || left_guess_info.value.equals("double") ||  left_guess_info.value.equals("string");
        if (!left_check) {
            error_content_list.add(op_string + "运算符左边必须是int,double,string,any类型.不能是:" + left_guess_info.value);
            error_element_list.add(left_src);
            return null;
        }

        boolean right_check = right_guess_info.value.equals("int") || right_guess_info.value.equals("I64") || right_guess_info.value.equals("double") ||  right_guess_info.value.equals("string");
        if (!right_check) {
            error_content_list.add(op_string + "运算符右边必须是int,double,string,any类型.不能是:" + right_guess_info.value);
            error_element_list.add(right_src);
            return null;
        }

        return op_5_suffix;
    }

    public static PsiElement guessSoftTypeForOp5(PsiElement left_src, PsiElement left_guess, ALittleOp5Suffix op_5_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_5_suffix.getOp5().getText();

        PsiElement value_factor_guess = null;
        PsiElement last_src = null;
        if (op_5_suffix.getValueFactor() != null) {
            ALittleValueFactor value_factor = op_5_suffix.getValueFactor();
            value_factor_guess = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = value_factor;
        } else if (op_5_suffix.getOp2Value() != null) {
            ALittleOp2Value op_2_value = op_5_suffix.getOp2Value();
            value_factor_guess = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = op_2_value;
        }

        PsiElement suffix_guess_type = value_factor_guess;
        List<ALittleOp5SuffixEe> suffix_ee_list = op_5_suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp4Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ee);
                return null;
            }
        }

        return guessSoftTypeForOp5Impl(op_string, left_src, left_guess, last_src, suffix_guess_type, op_5_suffix, error_content_list, error_element_list);
    }

    public static PsiElement guessSoftTypeForOp4Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp4Suffix op_4_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;

        if (left_guess_info.value.equals("int") || left_guess_info.value.equals("I64")) {
            if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64")) {
                return left_guess;
            } else if (right_guess_info.value.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }
        }

        if (left_guess_info.value.equals("double")) {
            if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64")) {
                return left_guess;
            } else if (right_guess_info.value.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }
        }

        error_content_list.add(op_string + "运算符左边必须是int,double,any类型.不能是:" + left_guess_info.value);
        error_element_list.add(left_src);
        return null;

    }

    public static PsiElement guessSoftTypeForOp4(PsiElement left_src, PsiElement left_guess, ALittleOp4Suffix op_4_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_4_suffix.getOp4().getText();

        PsiElement value_factor_guess = null;
        PsiElement last_src = null;
        if (op_4_suffix.getValueFactor() != null) {
            ALittleValueFactor value_factor = op_4_suffix.getValueFactor();
            value_factor_guess = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = value_factor;
        } else if (op_4_suffix.getOp2Value() != null) {
            ALittleOp2Value op_2_value = op_4_suffix.getOp2Value();
            value_factor_guess = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
            if (value_factor_guess == null) return null;
            last_src = op_2_value;
        }

        PsiElement suffix_guess_type = value_factor_guess;
        List<ALittleOp4SuffixEe> suffix_ee_list = op_4_suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ee.getOp3Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ee);
                return null;
            }
        }

        return guessSoftTypeForOp4Impl(op_string, left_src, left_guess, last_src, suffix_guess_type, op_4_suffix, error_content_list, error_element_list);
    }

    public static PsiElement guessSoftTypeForOp3(PsiElement left_src, PsiElement left_guess, ALittleOp3Suffix op_3_suffix, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String op_string = op_3_suffix.getOp3().getText();

        PsiElement right_guess = null;
        PsiElement right_src = null;
        if (op_3_suffix.getValueFactor() != null) {
            ALittleValueFactor value = op_3_suffix.getValueFactor();
            right_guess = guessSoftType(value, value, error_content_list, error_element_list);
            if (right_guess == null) return null;
            right_src = value;
        } else if (op_3_suffix.getOp2Value() != null) {
            ALittleOp2Value value = op_3_suffix.getOp2Value();
            right_guess = guessSoftType(value, value, error_content_list, error_element_list);
            if (right_guess == null) return null;
            right_src = value;
        } else {
            error_content_list.add("未知的表达式");
            error_element_list.add(op_3_suffix);
            return null;
        }

        GuessTypeInfo left_guess_info = guessTypeString(left_src, left_guess, null, error_content_list, error_element_list);
        if (left_guess_info == null) return null;
        GuessTypeInfo right_guess_info = guessTypeString(right_src, right_guess, null, error_content_list, error_element_list);
        if (right_guess_info == null) return null;

        if (left_guess_info.value.equals("any")) return left_guess;
        if (right_guess_info.value.equals("any")) return right_guess;

        if (left_guess_info.value.equals("int")) {
            if (right_guess_info.value.equals("int")) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return op_3_suffix;
                }
                return left_guess;
            } else if (right_guess_info.value.equals("I64")) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return op_3_suffix;
                }
                return right_guess;
            } else if (right_guess_info.value.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }
        }

        if (left_guess_info.value.equals("I64")) {
            if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64")) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return op_3_suffix;
                }
                return left_guess;
            } else if (right_guess_info.value.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }
        }

        if (left_guess_info.value.equals("double")) {
            if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64")) {
                return left_guess;
            } else if (right_guess_info.value.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_info.value);
                error_element_list.add(right_src);
                return null;
            }
        }

        error_content_list.add(op_string + "运算符左边必须是int,double,any类型.不能是:" + left_guess_info.value);
        error_element_list.add(left_src);
        return null;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleValueFactor value_factor, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        if (value_factor.getPropertyValue() != null) {
            PsiElement guess_type = guessType(value_factor.getPropertyValue());
            if (guess_type != null) return guess_type;
            error_content_list.add("未知的表达式");
            error_element_list.add(value_factor.getPropertyValue());
            return null;
        } else if (value_factor.getReflectValue() != null) {
            return value_factor.getReflectValue();
        } else if (value_factor.getConstValue() != null) {
            return value_factor.getConstValue();
        } else if (value_factor.getValueStatParen() != null) {
            ALittleValueStat value = value_factor.getValueStatParen().getValueStat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        }
        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp8Stat op_8_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_8_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp8(value_factor, factor_guess_type, op_8_stat.getOp8Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_8_stat.getOp8Suffix();
        List<ALittleOp8SuffixEx> suffix_ex_list = op_8_stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp7Stat op_7_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_7_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp7(value_factor, factor_guess_type, op_7_stat.getOp7Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_7_stat.getOp7Suffix();
        List<ALittleOp7SuffixEx> suffix_ex_list = op_7_stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp6Stat op_6_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_6_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp6(value_factor, factor_guess_type, op_6_stat.getOp6Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_6_stat.getOp6Suffix();
        List<ALittleOp6SuffixEx> suffix_ex_list = op_6_stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp5Stat op_5_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_5_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp5(value_factor, factor_guess_type, op_5_stat.getOp5Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_5_stat.getOp5Suffix();
        List<ALittleOp5SuffixEx> suffix_ex_list = op_5_stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp4Stat op_4_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_4_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp4(value_factor, factor_guess_type, op_4_stat.getOp4Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_4_stat.getOp4Suffix();
        List<ALittleOp4SuffixEx> suffix_ex_list = op_4_stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }


    public static PsiElement guessSoftType(PsiElement src, ALittleOp3Stat op_3_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_3_stat.getValueFactor();
        PsiElement factor_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (factor_guess_type == null) return null;

        PsiElement suffix_guess_type = guessSoftTypeForOp3(value_factor, factor_guess_type, op_3_stat.getOp3Suffix(), error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = op_3_stat.getOp3Suffix();
        List<ALittleOp3SuffixEx> suffix_ex_list = op_3_stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ex.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp3Suffix();
            } else if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp2Value op_2_value, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleValueFactor value_factor = op_2_value.getValueFactor();
        PsiElement suffix_guess_type = guessSoftType(value_factor, value_factor, error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        GuessTypeInfo guess_info = guessTypeString(value_factor, suffix_guess_type, null, error_content_list, error_element_list);
        if (guess_info == null) return null;

        String op_2 = op_2_value.getOp2().getText();
        // guess_type必须是逻辑运算符
        if (op_2.equals("!")) {
            if (!guess_info.value.equals("bool") && !guess_info.value.equals("any")) {
                error_content_list.add("!运算符右边必须是bool,any类型.不能是:" + guess_info.value);
                error_element_list.add(value_factor);
                return null;
            }
            // guess_type必须是数字
        } else if (op_2.equals("-")) {
            if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double") && !guess_info.value.equals("any")) {
                error_content_list.add("-运算符右边必须是int,double,any类型.不能是:" + guess_info.value);
                error_element_list.add(value_factor);
                return null;
            }
        } else {
            error_content_list.add("未知的运算符:" + op_2);
            error_element_list.add(op_2_value.getOp2());
            return null;
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleOp2Stat op_2_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        ALittleOp2Value op_2_value = op_2_stat.getOp2Value();
        PsiElement suffix_guess_type = guessSoftType(op_2_value, op_2_value, error_content_list, error_element_list);
        if (suffix_guess_type == null) return null;

        PsiElement last_src = suffix_guess_type;
        List<ALittleOp2SuffixEx> suffix_ex_list = op_2_stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp3Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp3(last_src, suffix_guess_type, suffix_ex.getOp3Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp3Suffix();
            } else if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessSoftTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix(), error_content_list, error_element_list);
                if (suffix_guess_type == null) return null;
                last_src = suffix_ex.getOp8Suffix();
            } else {
                error_content_list.add("未知的表达式");
                error_element_list.add(suffix_ex);
                return null;
            }
        }

        return suffix_guess_type;
    }

    public static PsiElement guessSoftType(PsiElement src, ALittleValueStat value_stat, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        if (value_stat.getOpNewStat() != null) {
            PsiElement guess_type = guessType(value_stat.getOpNewStat());
            if (guess_type != null) return guess_type;
            error_content_list.add("未知的表达式");
            error_element_list.add(value_stat.getOpNewStat());
            return null;
        } else if (value_stat.getOpNewList() != null) {
            return value_stat.getOpNewList();
        } else if (value_stat.getValueFactor() != null) {
            ALittleValueFactor value = value_stat.getValueFactor();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp2Stat() != null) {
            ALittleOp2Stat value = value_stat.getOp2Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp3Stat() != null) {
            ALittleOp3Stat value = value_stat.getOp3Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp4Stat() != null) {
            ALittleOp4Stat value = value_stat.getOp4Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp5Stat() != null) {
            ALittleOp5Stat value = value_stat.getOp5Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp6Stat() != null) {
            ALittleOp6Stat value = value_stat.getOp6Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp7Stat() != null) {
            ALittleOp7Stat value = value_stat.getOp7Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        } else if (value_stat.getOp8Stat() != null) {
            ALittleOp8Stat value = value_stat.getOp8Stat();
            return guessSoftType(value, value, error_content_list, error_element_list);
        }

        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static boolean guessSoftTypeEqual(PsiElement src_left, PsiElement left, GuessTypeInfo left_guess_info
                                            , PsiElement src_right, PsiElement right, GuessTypeInfo right_guess_info
                                            , @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        if (left_guess_info == null)
            left_guess_info = guessTypeString(src_left, left, null, error_content_list, error_element_list);
        if (left_guess_info == null) return false;

        if (right_guess_info == null)
            right_guess_info = guessTypeString(src_right, right, null, error_content_list, error_element_list);
        if (right_guess_info == null) return false;

        // 如果字符串直接相等，那么就直接返回成功
        if (left_guess_info.value.equals(right_guess_info.value)) return true;

        // 如果任何一方是any，那么就认为可以相等
        if (left_guess_info.value.equals("any") || right_guess_info.value.equals("any")) return true;
        // 如果值等于null，那么可以赋值
        if (right_guess_info.value.equals("null")) return true;

        if (left_guess_info.value.equals("bool")) {
            error_content_list.add("要求是bool,不能是:" + right_guess_info.value);
            error_element_list.add(src_right);
            return false;
        }

        if (left_guess_info.value.equals("int")) {
            if (right_guess_info.value.equals("I64"))
            {
                error_content_list.add("I64赋值给int，需要使用cast<int>()做强制类型转换");
                error_element_list.add(src_right);
                return false;
            }

            if (right_guess_info.value.equals("double"))
            {
                error_content_list.add("double赋值给int，需要使用cast<int>()做强制类型转换");
                error_element_list.add(src_right);
                return false;
            }
            error_content_list.add("要求是int, 不能是:" + right_guess_info.value);
            error_element_list.add(src_right);
            return false;
        }

        if (left_guess_info.value.equals("I64")) {
            if (right_guess_info.value.equals("int")) return true;

            if (right_guess_info.value.equals("double"))
            {
                error_content_list.add("double赋值给I64，需要使用cast<I64>()做强制类型转换");
                error_element_list.add(src_right);
                return false;
            }
            error_content_list.add("要求是I64, 不能是:" + right_guess_info.value);
            error_element_list.add(src_right);
            return false;
        }

        if (left_guess_info.value.equals("double")) {
            if (right_guess_info.value.equals("int") || right_guess_info.value.equals("I64")) return true;
            error_content_list.add("要求是I64, 不能是:" + right_guess_info.value);
            error_element_list.add(src_right);
            return false;
        }

        if (left_guess_info.value.equals("string")) {
            error_content_list.add("要求是string,不能是:" + right_guess_info.value);
            error_element_list.add(src_right);
            return false;
        }

        // 通用类型Map
        if (left_guess_info.type == GuessType.GT_MAP) {
            if (right_guess_info.type == GuessType.GT_MAP) {
                if (guessSoftTypeEqual(src_left, null, left_guess_info.map_key_type, src_right, null, right_guess_info.map_key_type
                        , new ArrayList<>(), new ArrayList<>())
                && guessSoftTypeEqual(src_left, null, left_guess_info.map_value_type, src_right, null, right_guess_info.map_value_type
                        , new ArrayList<>(), new ArrayList<>()))
                    return true;
            }
        }

        // 通用类型List
        if (left_guess_info.type == GuessType.GT_LIST) {
            if (right_guess_info.type == GuessType.GT_LIST) {
                if (guessSoftTypeEqual(src_left, null, left_guess_info.list_sub_type, src_right, null, right_guess_info.list_sub_type
                        , new ArrayList<>(), new ArrayList<>()))
                    return true;
            }
        }

        // 通用类型Functor
        if (left_guess_info.type == GuessType.GT_FUNCTOR) {
            if (right_guess_info.type == GuessType.GT_FUNCTOR) {
                if (left_guess_info.functor_param_list.size() == right_guess_info.functor_param_list.size()
                && left_guess_info.functor_return_list.size() == right_guess_info.functor_return_list.size()) {
                    boolean result = true;
                    if (result) {
                        for (int i = 0; i < left_guess_info.functor_param_list.size(); ++i) {
                            if (!guessSoftTypeEqual(src_left, null, left_guess_info.functor_param_list.get(i)
                                                , src_right, null, right_guess_info.functor_param_list.get(i)
                                                , error_content_list, error_element_list)) {
                                result = false;
                                break;
                            }
                        }
                    }
                    if (result) {
                        for (int i = 0; i < left_guess_info.functor_return_list.size(); ++i) {
                            if (!guessSoftTypeEqual(src_left, null, left_guess_info.functor_return_list.get(i)
                                                , src_right, null, right_guess_info.functor_return_list.get(i)
                                                , error_content_list, error_element_list)) {
                                result = false;
                                break;
                            }
                        }
                    }
                    if (result) return true;
                }
            }
        }

        if (left == null && left_guess_info.type == GuessType.GT_CLASS) {
            String[] left_type = left_guess_info.value.split("\\.");
            if (left_type.length == 2) {
                List<ALittleClassNameDec> list = ALittleTreeChangeListener.findClassNameDecList(src_left.getProject(), left_type[0], left_type[1]);
                if (list != null && list.size() == 1) left = list.get(0).getParent();
            }
        }

        if (left == null && left_guess_info.type == GuessType.GT_STRUCT) {
            String[] left_type = left_guess_info.value.split("\\.");
            if (left_type.length == 2) {
                List<ALittleStructNameDec> list = ALittleTreeChangeListener.findStructNameDecList(src_left.getProject(), left_type[0], left_type[1]);
                if (list != null && list.size() == 1) left = list.get(0).getParent();
            }
        }

        if (right == null && right_guess_info.type == GuessType.GT_CLASS) {
            String[] right_type = right_guess_info.value.split("\\.");
            if (right_type.length == 2) {
                List<ALittleClassNameDec> list = ALittleTreeChangeListener.findClassNameDecList(src_right.getProject(), right_type[0], right_type[1]);
                if (list != null && list.size() == 1) right = list.get(0).getParent();
            }
        }

        if (right == null && right_guess_info.type == GuessType.GT_STRUCT) {
            String[] right_type = right_guess_info.value.split("\\.");
            if (right_type.length == 2) {
                List<ALittleStructNameDec> list = ALittleTreeChangeListener.findStructNameDecList(src_right.getProject(), right_type[0], right_type[1]);
                if (list != null && list.size() == 1) right = list.get(0).getParent();
            }
        }

        // 自定义类型 (只有left继承了right，或者right继承了left，才可以赋值)
        if (left instanceof ALittleClassDec) {
            if (!(right instanceof ALittleClassDec)) {
                error_content_list.add("要求是" + left_guess_info.value + ",不能是:" + right_guess_info.value);
                error_element_list.add(src_right);
                return false;
            }

            if (left.equals(right)) return true;

            if (IsClassSuper(left, right)) return true;
            if (IsClassSuper(right, left)) return true;

        } else if (left instanceof ALittleStructDec) {
            if (!(right instanceof ALittleStructDec)) {
                error_content_list.add("要求是" + left_guess_info.value + ",不能是:" + right_guess_info.value);
                error_element_list.add(src_right);
                return false;
            }

            if (left.equals(right)) return true;

            if (IsStructSuper(left, right)) return true;
            if (IsStructSuper(right, left)) return true;
        }

        error_content_list.add("要求是" + left_guess_info.value + ",不能是:" + right_guess_info.value);
        error_element_list.add(src_right);
        return false;
    }

    @NotNull
    public static String GenerateStructForJsonProto(ALittleStructDec root, String pre_tab) throws Exception {
        ALittleStructProtocolDec protocol_dec = root.getStructProtocolDec();
        // 如果没有带协议标志，那么就不需要生成协议
        if (protocol_dec == null) return "";

        ALittleCustomType custom_type = protocol_dec.getCustomType();
        if (custom_type == null) return "";

        PsiElement guess_type = custom_type.getCustomTypeNameDec().guessType();
        if (!(guess_type instanceof ALittleEnumDec))
            throw new Exception(root.getText() + "struct的(XXX)内必须使用enum");

        ALittleEnumDec enum_dec = (ALittleEnumDec)guess_type;
        if (enum_dec.getEnumProtocolDec() == null)
            throw new Exception(root.getText() + "struct的(XXX)内必须使用带protocol的enum");

        // 协议名
        ALittleStructNameDec name_dec = root.getStructNameDec();
        if (name_dec == null)
            throw new Exception(root.getText() + "没有定义协议名");

        String message_name = name_dec.getIdContent().getText();

        // 协议ID
        List<Integer> result = new ArrayList<>();
        if (!ALittleUtil.getEnumVarValue(enum_dec, "_" + message_name, result) || result.isEmpty())
            throw new Exception(root.getText() + "找不到协议ID:_" + message_name);

        String json = "{";
            json += "\"id\":" + result.get(0) + ",";
            json += "\"name\":\"" + message_name + "\",";
            json += "\"var_name_list\":[";
                List<String> var_name_list = new ArrayList<>();
                for (ALittleStructVarDec var_dec : root.getStructVarDecList()) {
                    ALittleStructVarNameDec var_name_dec = var_dec.getStructVarNameDec();
                    if (var_name_dec == null) {
                        throw new Exception(var_dec.getText() + "没有定义字段名");
                    }
                    var_name_list.add("\"" + var_name_dec.getText() + "\"");
                }
                json += String.join(",", var_name_list);
            json += "],";
            json += "\"var_type_list\":[";
                List<String> var_type_list = new ArrayList<>();
                for (ALittleStructVarDec var_dec : root.getStructVarDecList()) {
                    ALittleAllType all_type = var_dec.getAllType();
                    if (all_type == null) {
                        throw new Exception(var_dec.getText() + "没有定义字段类型");
                    }
                    String var_type = all_type.getText();
                    // 把空格替换掉
                    var_type = var_type.replace(" ", "");
                    // 不能支持任何any字段
                    if (var_type.equals("any")
                        || var_type.contains("<any")
                        || var_type.contains("any>")) {
                        throw new Exception(var_dec.getText() + "协议不支持any类型");
                    }
                    var_type_list.add("\"" + var_type + "\"");
                }
                json += String.join(",", var_type_list);
            json += "]";
        json += "}";

        return json;
    }

    @NotNull
    public static String GenerateStructForCPPProto(ALittleStructDec root, @NotNull List<String> class_list, String pre_tab) throws Exception {
        ALittleStructProtocolDec protocol_dec = root.getStructProtocolDec();
        // 如果没有带协议标志，那么就不需要生成协议
        if (protocol_dec == null) return "";

        // 协议名
        ALittleStructNameDec name_dec = root.getStructNameDec();
        if (name_dec == null)
            throw new Exception(root.getText() + "没有定义协议名");
        // 协议名
        String message_name = name_dec.getIdContent().getText();

        ALittleCustomType custom_type = protocol_dec.getCustomType();
        boolean is_json = custom_type == null;
        if (custom_type != null) {
            PsiElement guess_type = custom_type.getCustomTypeNameDec().guessType();
            if (!(guess_type instanceof ALittleEnumDec))
                throw new Exception(root.getText() + "struct的(XXX)内必须使用enum");

            ALittleEnumDec enum_dec = (ALittleEnumDec)guess_type;
            if (enum_dec.getEnumProtocolDec() == null)
                throw new Exception(root.getText() + "struct的(XXX)内必须使用带protocol的enum");

            // 协议ID
            List<Integer> result = new ArrayList<>();
            if (!ALittleUtil.getEnumVarValue(enum_dec, "_" + message_name, result) || result.isEmpty())
                throw new Exception(root.getText() + "找不到协议ID:_" + message_name);
        }

        List<String> typedef_list = new ArrayList<>();
        String proto = "MESSAGE_MACRO(" + message_name + "\n";
        if (is_json) proto = "JSON_MACRO(" + message_name + "\n";
        for (ALittleStructVarDec var_dec : root.getStructVarDecList()) {
            ALittleStructVarNameDec var_name_dec = var_dec.getStructVarNameDec();
            if (var_name_dec == null) {
                throw new Exception(var_dec.getText() + "没有定义字段名");
            }
            String var_name = var_name_dec.getText();
            ALittleAllType all_type = var_dec.getAllType();
            if (all_type == null) {
                throw new Exception(var_dec.getText() + "没有定义字段类型");
            }
            String var_type = all_type.getText();
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
                String typedef = (message_name + "_" + var_name).toUpperCase();
                typedef_list.add("typedef " + var_type + " " + typedef + ";");
                var_type = typedef;
            }

            proto += "\t, " + var_type + ", " + var_name_dec.getText() + "\n";
        }
        proto += ");\n";

        if (!typedef_list.isEmpty())
            proto = String.join("\n", typedef_list) + "\n" + proto;

        class_list.add("class " + message_name + ";");
        return proto;
    }

    @NotNull
    public static String GenerateEnumForCPPProto(ALittleEnumDec root, String pre_tab) throws Exception {
        // 如果没有带协议标志，那么就不需要生成协议
        if (root.getEnumProtocolDec() == null) return "";

        // 协议名
        ALittleEnumNameDec name_dec = root.getEnumNameDec();
        if (name_dec == null)
            throw new Exception(root.getText() + "没有定义枚举名");

        // 协议名
        String enum_name = name_dec.getIdContent().getText();

        List<String> typedef_list = new ArrayList<>();
        String proto = "enum " + enum_name + "\n";
        proto += "{\n";
        for (ALittleEnumVarDec var_dec : root.getEnumVarDecList()) {
            ALittleEnumVarNameDec var_name_dec = var_dec.getEnumVarNameDec();
            if (var_name_dec == null) {
                throw new Exception(var_dec.getText() + "没有定义字段名");
            }
            ALittleEnumVarValueDec var_value_dec = var_dec.getEnumVarValueDec();
            if (var_value_dec != null && var_value_dec.getStringContent() != null) {
                throw new Exception(var_dec.getText() + "协议枚举的值不能是字符串");
            }
            proto += "\t" + var_dec.getText() + ",\n";
        }
        proto += "};\n";
        return proto;
    }
}
