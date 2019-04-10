package plugin;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;

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
            if (src_name.isEmpty() || var_name_dec.getIdContent().getText().equals(src_name))
                result.add(var_name_dec);
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

    // 查找所有的函数名节点对象
    private static void findMethodNameDecList(Project project, String src_namespace, String src_class, String src_method, @NotNull List<ALittleMethodNameDec> result, int deep)
    {        // 这个用于跳出无限递归
        if (deep <= 0) return;

        List<ALittleClassNameDec> name_dec_list = ALittleTreeChangeListener.findClassNameDecList(project, src_namespace, src_class);
        if (name_dec_list == null || name_dec_list.isEmpty()) return;
        ALittleClassDec class_dec = (ALittleClassDec)name_dec_list.get(0).getParent();

        findMethodNameDecList(project, src_namespace, class_dec, src_method, result, deep);
    }

    public static void findMethodNameDecList(Project project, String src_namespace, ALittleClassDec class_dec, String src_method, @NotNull List<ALittleMethodNameDec> result, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;
        if (class_dec == null) return;

        // 处理继承
        ALittleClassExtendsNameDec class_extends_name_dec = class_dec.getClassExtendsNameDec();
        if (class_extends_name_dec != null) {
            ALittleClassExtendsNamespaceNameDec class_extends_namespace_name_dec = class_dec.getClassExtendsNamespaceNameDec();
            String namespace_name = src_namespace;
            if (class_extends_namespace_name_dec != null) namespace_name = class_extends_namespace_name_dec.getText();
            findMethodNameDecList(project, namespace_name, class_extends_name_dec.getText(), src_method, result, deep - 1);
        }

        // 处理成员函数
        List<ALittleClassMethodDec> class_method_dec_list = class_dec.getClassMethodDecList();
        for (ALittleClassMethodDec class_method_dec : class_method_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_method_dec.getMethodNameDec();
            if (class_method_name_dec == null) continue;
            if (src_method.isEmpty() || class_method_name_dec.getIdContent().getText().equals(src_method)) {
                result.add(class_method_name_dec);
            }
        }

        // 处理getter函数
        List<ALittleClassGetterDec> class_getter_dec_list = class_dec.getClassGetterDecList();
        for (ALittleClassGetterDec class_getter_dec : class_getter_dec_list) {
            ALittleMethodNameDec class_getter_name_dec = class_getter_dec.getMethodNameDec();
            if (class_getter_name_dec == null) continue;
            if (src_method.isEmpty() || class_getter_name_dec.getIdContent().getText().equals(src_method)) {
                result.add(class_getter_name_dec);
            }
        }

        // 处理setter函数
        List<ALittleClassSetterDec> class_setter_dec_list = class_dec.getClassSetterDecList();
        for (ALittleClassSetterDec class_setter_dec : class_setter_dec_list) {
            ALittleMethodNameDec class_setter_name_dec = class_setter_dec.getMethodNameDec();
            if (class_setter_name_dec == null) continue;
            if (src_method.isEmpty() || class_setter_name_dec.getIdContent().getText().equals(src_method)) {
                result.add(class_setter_name_dec);
            }
        }

        // 处理静态函数
        List<ALittleClassStaticDec> class_static_dec_list = class_dec.getClassStaticDecList();
        for (ALittleClassStaticDec class_static_dec : class_static_dec_list) {
            ALittleMethodNameDec class_static_name_dec = class_static_dec.getMethodNameDec();
            if (class_static_name_dec == null) continue;
            if (src_method.isEmpty() || class_static_name_dec.getIdContent().getText().equals(src_method)) {
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
            if (src_name.isEmpty() || var_name_dec.getIdContent().getText().equals(src_name))
                result.add(var_name_dec);
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
            if (src_name.isEmpty() || name_dec.getIdContent().getText().equals(src_name))
                result.add(name_dec);
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
                    String name = var_assign_name_dec.getIdContent().getText();
                    if (src_name.isEmpty() || name.equals(src_name))
                        var_dec_list.add(var_assign_name_dec);
                }
            } else if (in_condition != null) {
                List<ALittleForPairDec> pair_dec_list = in_condition.getForPairDecList();
                for (ALittleForPairDec pair_dec : pair_dec_list) {
                    ALittleVarAssignNameDec var_assign_name_dec = pair_dec.getVarAssignNameDec();
                    String name = var_assign_name_dec.getIdContent().getText();
                    if (src_name.isEmpty() || name.equals(src_name))
                        var_dec_list.add(var_assign_name_dec);
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
                String name = var_assign_name_dec.getIdContent().getText();
                if (src_name.isEmpty() || name.equals(src_name))
                    var_dec_list.add(var_assign_name_dec);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 根据文件对象获取命名域
    @NotNull
    public static String getNamespaceName(ALittleFile alittleFile) {
        List<ALittleNamespaceDec> namespace_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
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
            return v == Math.floor(v);
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

        PsiReference[] reference_list = method_call.getReferences();
        if (reference_list == null || reference_list.length == 0) return null;
        if (reference_list[0] instanceof ALittlePropertyValueMethodCallStatReference) {
            ALittlePropertyValueMethodCallStatReference real_ref = (ALittlePropertyValueMethodCallStatReference)reference_list[0];
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
                return "ipairs";
            else if (generic_type.getGenericMapType() != null)
                return "pairs";
        }

        return "";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 一下guesstype得出的类型值可能是
    // ALittlePrimitiveType 基本类型
    // ALittleConstValue 常亮类型
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

    public static String guessTypeString(PsiElement src, PsiElement element, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        // 基本类型
        if (element instanceof ALittlePrimitiveType) {
            return element.getText();
        // 常量类型
        } else if (element instanceof ALittleConstValue) {
            ALittleConstValue dec = (ALittleConstValue) element;
            if (dec.getDigitContent() != null) {
                String value = dec.getDigitContent().getText();
                if (ALittleUtil.isInt(value)) return "int";
                return "double";
            } else if (dec.getStringContent() != null) {
                return "string";
            } else if (dec.getText().equals("true") || dec.getText().equals("false")) {
                return "bool";
            } else if (dec.getText().equals("null")) {
                return "null";
            }
        } else if (element instanceof ALittleOp3Suffix) {
            ALittleOp3Suffix dec = (ALittleOp3Suffix) element;
            String op_3 = dec.getOp3().getText();
            if (op_3.equals("/")) {
                return "double";
            }
        } else if (element instanceof ALittleOp5Suffix) {
            return "string";
        } else if (element instanceof ALittleOp6Suffix) {
            return "bool";
        } else if (element instanceof ALittleGenericType) {
            return guessTypeString(src, (ALittleGenericType)element, error_content_list, error_element_list);
        } else if (element instanceof ALittlePropertyValueBrackValueStat || element instanceof ALittlePropertyValueMethodCallStat) {
            return "any";
        } else if (element instanceof ALittleClassDec) {
            ALittleClassDec dec = (ALittleClassDec)element;
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleClassNameDec name_dec = dec.getClassNameDec();
            if (name_dec != null) {
                String class_name = name_dec.getIdContent().getText();
                return namespace_name + "." + class_name;
            }
        } else if (element instanceof ALittleClassNameDec) {
            return "any";
        } else if (element instanceof ALittleStructDec) {
            ALittleStructDec dec = (ALittleStructDec)element;
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleStructNameDec name_dec = dec.getStructNameDec();
            if (name_dec != null) {
                String struct_name = name_dec.getIdContent().getText();
                return namespace_name + "." + struct_name;
            }
        } else if (element instanceof ALittleEnumDec) {
            ALittleEnumDec dec = (ALittleEnumDec)element;
            String namespace_name = getNamespaceName((ALittleFile)dec.getContainingFile());
            ALittleEnumNameDec name_dec = dec.getEnumNameDec();
            if (name_dec != null) {
                String enum_name = name_dec.getIdContent().getText();
                return namespace_name + "." + enum_name;
            }
        } else if (element instanceof ALittleEnumVarNameDec) {
            ALittleEnumVarNameDec name_dec = (ALittleEnumVarNameDec)element;
            ALittleEnumVarDec dec = (ALittleEnumVarDec)name_dec.getParent();
            ALittleEnumVarValueDec value_dec = dec.getEnumVarValueDec();
            if (value_dec != null)
            {
                if (value_dec.getDigitContent() != null) return "int";
                if (value_dec.getStringContent() != null) return "string";
            }
        } else if (element instanceof ALittleNamespaceNameDec) {
            return "any";
        } else if (element instanceof ALittleMethodNameDec) {
            return "any";   // 这里本来构建一个Functor回去，但是为了减弱检查，就返回any类型
        }

        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static String guessTypeString(PsiElement src, ALittleGenericType generic_type, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        if (generic_type.getGenericListType() != null) {
            ALittleGenericListType dec = generic_type.getGenericListType();
            String name = guessTypeString(src, dec.getAllType(), error_content_list, error_element_list);
            if (name == null) return null;
            return "List<" + name + ">";
        } else if (generic_type.getGenericMapType() != null) {
            ALittleGenericMapType dec = generic_type.getGenericMapType();
            List<ALittleAllType> all_type_list = dec.getAllTypeList();
            if (all_type_list.size() != 2) return null;
            String key_name = guessTypeString(src, all_type_list.get(0), error_content_list, error_element_list);
            String value_name = guessTypeString(src, all_type_list.get(1), error_content_list, error_element_list);
            if (key_name == null || value_name == null) return null;
            return "Map<" + key_name + "," + value_name + ">";
        } else if (generic_type.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = generic_type.getGenericFunctorType();
            ALittleGenericFunctorParamType param_type = dec.getGenericFunctorParamType();
            String content = "Functor<(";
            if (param_type != null) {
                List<String> name_list = new ArrayList<>();
                List<ALittleAllType> all_type_list = param_type.getAllTypeList();
                for (ALittleAllType all_type : all_type_list) {
                    String name = guessTypeString(src, all_type, error_content_list, error_element_list);
                    if (name == null) return null;
                    name_list.add(name);
                }
                content += String.join(",", name_list);
            }
            content += ")";
            ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
            if (return_type != null) {
                List<String> name_list = new ArrayList<>();
                List<ALittleAllType> all_type_list = return_type.getAllTypeList();
                for (ALittleAllType all_type : all_type_list) {
                    String name = guessTypeString(src, all_type, error_content_list, error_element_list);
                    if (name == null) return null;
                    name_list.add(name);
                }
                content += String.join(",", name_list);
            }
            content += ">";
            return content;
        }
        error_content_list.add("未知的表达式");
        error_element_list.add(src);
        return null;
    }

    public static String guessTypeString(PsiElement src, ALittleAllType all_type, @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        PsiElement element = guessType(all_type);
        if (element == null) {
            error_content_list.add("未知的表达式");
            error_element_list.add(src);
            return null;
        }
        return guessTypeString(src, element, error_content_list, error_element_list);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static PsiElement guessSoftTypeForOp8Impl(String op_string
                                                    , PsiElement left_src, PsiElement left_guess
                                                    , PsiElement right_src, PsiElement right_guess
                                                    , ALittleOp8Suffix op_8_suffix
                                                    , @NotNull List<String> error_content_list
                                                    , @NotNull List<PsiElement> error_element_list) {
        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;

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
        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;

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
        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;

        if (op_string.equals("==") || op_string.equals("!=")) {
            return op_6_suffix;
        } else {
            if (left_guess_type_name.equals("int") || left_guess_type_name.equals("double")) {
                if (right_guess_type_name.equals("int") || right_guess_type_name.equals("double")) {
                    return op_6_suffix;
                }

                error_content_list.add(op_string + "运算符左边是数字，那么右边必须是int,double,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }

            if (left_guess_type_name.equals("string")) {
                if (right_guess_type_name.equals("string")) {
                    return op_6_suffix;
                }
                error_content_list.add(op_string + "运算符左边是字符串，那么右边必须是string,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }

            error_content_list.add(op_string + "运算符左边必须是int,double,string,any类型.不能是:" + left_guess_type_name);
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
        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;


        boolean left_check = left_guess_type_name.equals("int") || left_guess_type_name.equals("double") ||  left_guess_type_name.equals("string");
        if (!left_check) {
            error_content_list.add(op_string + "运算符左边必须是int,double,string,any类型.不能是:" + left_guess_type_name);
            error_element_list.add(left_src);
            return null;
        }

        boolean right_check = right_guess_type_name.equals("int") || right_guess_type_name.equals("double") ||  right_guess_type_name.equals("string");
        if (!right_check) {
            error_content_list.add(op_string + "运算符右边必须是int,double,string,any类型.不能是:" + right_guess_type_name);
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
        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;

        if (left_guess_type_name.equals("int")) {
            if (right_guess_type_name.equals("int")) {
                return left_guess;
            } else if (right_guess_type_name.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }
        }

        if (left_guess_type_name.equals("double")) {
            if (right_guess_type_name.equals("int")) {
                return left_guess;
            } else if (right_guess_type_name.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }
        }

        error_content_list.add(op_string + "运算符左边必须是int,double,any类型.不能是:" + left_guess_type_name);
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

        String left_guess_type_name = guessTypeString(left_src, left_guess, error_content_list, error_element_list);
        if (left_guess_type_name == null) return null;
        String right_guess_type_name = guessTypeString(right_src, right_guess, error_content_list, error_element_list);
        if (right_guess_type_name == null) return null;

        if (left_guess_type_name.equals("any")) return left_guess;
        if (right_guess_type_name.equals("any")) return right_guess;

        if (left_guess_type_name.equals("int")) {
            if (right_guess_type_name.equals("int")) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return op_3_suffix;
                }
                return left_guess;
            } else if (right_guess_type_name.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }
        }

        if (left_guess_type_name.equals("double")) {
            if (right_guess_type_name.equals("int")) {
                return left_guess;
            } else if (right_guess_type_name.equals("double")) {
                return right_guess;
            } else {
                error_content_list.add(op_string + "运算符右边必须是int,double,any类型.不能是:" + right_guess_type_name);
                error_element_list.add(right_src);
                return null;
            }
        }

        error_content_list.add(op_string + "运算符左边必须是int,double,any类型.不能是:" + left_guess_type_name);
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

        String guess_string = guessTypeString(value_factor, suffix_guess_type, error_content_list, error_element_list);
        if (guess_string == null) return null;

        String op_2 = op_2_value.getOp2().getText();
        // guess_type必须是逻辑运算符
        if (op_2.equals("!")) {
            if (!guess_string.equals("bool") && !guess_string.equals("any")) {
                error_content_list.add("!运算符右边必须是bool,any类型.不能是:" + guess_string);
                error_element_list.add(value_factor);
                return null;
            }
            // guess_type必须是数字
        } else if (op_2.equals("-")) {
            if (!guess_string.equals("int") && !guess_string.equals("double") && !guess_string.equals("any")) {
                error_content_list.add("-运算符右边必须是int,double,any类型.不能是:" + guess_string);
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

    public static boolean guessSoftTypeEqual(PsiElement src_left, PsiElement left, PsiElement src_right, PsiElement right
                                            , @NotNull List<String> error_content_list, @NotNull List<PsiElement> error_element_list) {
        String left_name = guessTypeString(src_left, left, error_content_list, error_element_list);
        if (left_name == null) return false;
        String right_name = guessTypeString(src_right, right, error_content_list, error_element_list);
        if (right_name == null) return false;
        // 如果任何一方是any，那么就认为可以相等
        if (left_name.equals("any") || right_name.equals("any")) return true;
        // 如果值等于null，那么可以赋值
        if (right_name.equals("null")) return true;

        if (left_name.equals("bool")) {
            if (right_name.equals("bool")) return true;
            error_content_list.add("要求是bool,不能是:" + right_name);
            error_element_list.add(src_right);
            return false;
        }

        if (left_name.equals("int") || left_name.equals("double")) {
            if (right_name.equals("int") || right_name.equals("double")) return true;
            error_content_list.add("要求是int,double,不能是:" + right_name);
            error_element_list.add(src_right);
            return false;
        }

        if (left_name.equals("string")) {
            if (right_name.equals("string")) return true;
            error_content_list.add("要求是string,不能是:" + right_name);
            error_element_list.add(src_right);
            return false;
        }

        // 通用类型
        if (left_name.startsWith("Map<") || left_name.startsWith("List<") || left_name.startsWith("Functor<")) {
            if (left_name.equals(right_name)) return true;
        }

        // 自定义类型 (只有left继承了right，或者right继承了left，才可以赋值)
        if (left instanceof ALittleClassDec) {
            if (!(right instanceof ALittleClassDec)) {
                error_content_list.add("要求是" + left_name + ",不能是:" + right_name);
                error_element_list.add(src_right);
                return false;
            }

            if (left.equals(right)) return true;

            if (IsClassSuper(left, right)) return true;
            if (IsClassSuper(right, left)) return true;

        } else if (left instanceof ALittleStructDec) {
            if (!(right instanceof ALittleStructDec)) {
                error_content_list.add("要求是" + left_name + ",不能是:" + right_name);
                error_element_list.add(src_right);
                return false;
            }

            if (left.equals(right)) return true;

            if (IsStructSuper(left, right)) return true;
            if (IsStructSuper(right, left)) return true;
        }

        error_content_list.add("要求是" + left_name + ",不能是:" + right_name);
        error_element_list.add(src_right);
        return false;
    }
}
