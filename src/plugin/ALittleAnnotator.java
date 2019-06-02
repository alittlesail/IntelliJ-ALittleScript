package plugin;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueBrackValueStatReference;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;

import java.util.ArrayList;
import java.util.List;

public class ALittleAnnotator implements Annotator {

    // 获取元素定义
    public static List<PsiElement> GetGuessList(@NotNull PsiElement element) {
        List<PsiElement> guess_list = null;

        // 类相关
        if (element instanceof ALittleClassExtendsNameDec) {
            guess_list = ((ALittleClassExtendsNameDec) element).guessTypes();
        } else if (element instanceof ALittleClassExtendsNamespaceNameDec) {
            guess_list = ((ALittleClassExtendsNamespaceNameDec) element).guessTypes();
        } else if (element instanceof ALittleClassNameDec) {
            guess_list = ((ALittleClassNameDec) element).guessTypes();
        } else if (element instanceof ALittleClassVarNameDec) {
            guess_list = ((ALittleClassVarNameDec) element).guessTypes();

            // 自定义类型相关
        } else if (element instanceof ALittleCustomTypeNameDec) {
            guess_list = ((ALittleCustomTypeNameDec) element).guessTypes();
        } else if (element instanceof ALittleCustomTypeNamespaceNameDec) {
            guess_list = ((ALittleCustomTypeNamespaceNameDec) element).guessTypes();

            // 枚举相关
        } else if (element instanceof ALittleEnumNameDec) {
            guess_list = ((ALittleEnumNameDec) element).guessTypes();

            // 单例相关
        } else if (element instanceof ALittleInstanceClassNameDec) {
            guess_list = ((ALittleInstanceClassNameDec) element).guessTypes();
        } else if (element instanceof ALittleInstanceNameDec) {
            guess_list = ((ALittleInstanceNameDec) element).guessTypes();

            // 函数相关
        } else if (element instanceof ALittleMethodParamNameDec) {
            guess_list = ((ALittleMethodParamNameDec) element).guessTypes();

            // 属性相关
        } else if (element instanceof ALittlePropertyValueBrackValueStat) {
            guess_list = ((ALittlePropertyValueBrackValueStat) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCustomType) {
            guess_list = ((ALittlePropertyValueCustomType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueDotIdName) {
            guess_list = ((ALittlePropertyValueDotIdName) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueThisType) {
            guess_list = ((ALittlePropertyValueThisType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCastType) {
            guess_list = ((ALittlePropertyValueCastType) element).guessTypes();

            // 结构体相关
        } else if (element instanceof ALittleStructExtendsNameDec) {
            guess_list = ((ALittleStructExtendsNameDec) element).guessTypes();
        } else if (element instanceof ALittleStructExtendsNamespaceNameDec) {
            guess_list = ((ALittleStructExtendsNamespaceNameDec) element).guessTypes();
        } else if (element instanceof ALittleStructNameDec) {
            guess_list = ((ALittleStructNameDec) element).guessTypes();
        } else if (element instanceof ALittleStructVarNameDec) {
            guess_list = ((ALittleStructVarNameDec) element).guessTypes();

            // 局部变量定义
        } else if (element instanceof ALittleVarAssignNameDec) {
            guess_list = ((ALittleVarAssignNameDec) element).guessTypes();
        }

        return guess_list;
    }

    public static String CheckErrorForGuessList(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        // 检查未定义或者重复定义
        if (guess_list != null) {
            if (guess_list.isEmpty()) {
                error = "未知类型";
            } else if (guess_list.size() > 1 && !(guess_list.get(0) instanceof ALittleNamespaceNameDec)) {
                error = "重复定义";
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForStruct(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        // 结构体类型错误检查
        if (element instanceof ALittleCustomType) {
            do {
                ALittleCustomType custom_type = (ALittleCustomType) element;
                if (!(element.getParent() instanceof ALittleStructProtocolDec)) break;
                ALittleStructProtocolDec protocol_dec = (ALittleStructProtocolDec) element.getParent();
                ALittleStructDec struct_dec = (ALittleStructDec) protocol_dec.getParent();
                ALittleStructNameDec name_dec = struct_dec.getStructNameDec();
                if (name_dec == null) {
                    error = "没有定义协议名";
                    break;
                }

                PsiElement guess_type = custom_type.getCustomTypeNameDec().guessType();
                if (!(guess_type instanceof ALittleEnumDec)) {
                    error = "struct的(XXX)内必须使用enum";
                    break;
                }
                ALittleEnumDec enum_dec = (ALittleEnumDec) guess_type;
                if (enum_dec.getEnumProtocolDec() == null) {
                    error = "struct的(XXX)内必须使用带protocol的enum";
                    break;
                }

                String message_name = name_dec.getText();
                // 协议ID
                List<Integer> result = new ArrayList<>();
                if (!ALittleUtil.getEnumVarValue(enum_dec, "_" + message_name, result) || result.isEmpty())
                {
                    error = "找不到协议ID:_" + message_name;
                    break;
                }
            } while (false);
            // 枚举类型字段名不能重复
        } else if (element instanceof ALittleStructVarNameDec) {
            ALittleStructVarNameDec dec = (ALittleStructVarNameDec)element;
            String cur_name = dec.getIdContent().getText();
            ALittleStructVarDec var_dec = (ALittleStructVarDec)element.getParent();
            ALittleStructDec struct_dec = (ALittleStructDec)var_dec.getParent();
            List<ALittleStructVarDec> var_dec_list = struct_dec.getStructVarDecList();
            int count = 0;
            for (ALittleStructVarDec var : var_dec_list) {
                ALittleStructVarNameDec var_name_dec = var.getStructVarNameDec();
                if (var_name_dec != null) {
                    String name = var_name_dec.getIdContent().getText();
                    if (name.equals(cur_name)) {
                        ++ count;
                        if (count >= 2) break;
                    }
                }
            }
            if (count >= 2) error = "结构体字段名重复";
        } else if (element instanceof ALittleAllType) {
            ALittleAllType all_type = (ALittleAllType)element;
            do
            {
                if (!(all_type.getParent() instanceof ALittleStructVarDec))
                    break;
                ALittleStructVarDec var_dec = (ALittleStructVarDec)element.getParent();
                ALittleStructDec struct_dec = (ALittleStructDec)var_dec.getParent();
                if (struct_dec.getStructProtocolDec() == null) break;

                ALittleStructNameDec name_dec = struct_dec.getStructNameDec();
                if (name_dec == null) {
                    error = "没有定义协议名";
                    break;
                }

                String message_name = name_dec.getText();

                String type = all_type.getText();
                type = type.replace(" ", "");
                if (type.equals("any")
                    || type.contains("<any")
                    || type.contains("any>"))
                {
                    error = "协议字段不支持any类型:" + message_name;
                    break;
                }

            } while (false);
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForEnum(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        // 枚举类型错误检查
        if (element instanceof ALittleEnumVarValueDec) {
            ALittleEnumVarValueDec dec = (ALittleEnumVarValueDec)element;
            if (dec.getDigitContent() != null) {
                String value = dec.getDigitContent().getText();
                if (!ALittleUtil.isInt(value)) {
                    error = "枚举值必须是整数";
                }
            }
        // 枚举类型字段名不能重复
        } else if (element instanceof ALittleEnumVarNameDec) {
            ALittleEnumVarNameDec dec = (ALittleEnumVarNameDec)element;
            String cur_name = dec.getIdContent().getText();
            ALittleEnumVarDec var_dec = (ALittleEnumVarDec)element.getParent();
            ALittleEnumDec enum_dec = (ALittleEnumDec)var_dec.getParent();
            List<ALittleEnumVarDec> var_dec_list = enum_dec.getEnumVarDecList();
            int count = 0;
            for (ALittleEnumVarDec var : var_dec_list) {
                String name = var.getEnumVarNameDec().getIdContent().getText();
                if (name.equals(cur_name)) {
                    ++ count;
                    if (count >= 2) break;
                }
            }
            if (count >= 2) error = "枚举字段名重复";
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForReturn(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if (element instanceof ALittleReturnExpr) {
            ALittleReturnExpr dec = (ALittleReturnExpr)element;
            List<ALittleValueStat> value_stat_list = dec.getValueStatList();

            List<ALittleMethodReturnTypeDec> return_type_list = new ArrayList<>();

            // 获取对应的函数对象
            PsiElement parent = element;
            while (parent != null) {
                if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec getter_dec = (ALittleClassGetterDec)parent;
                    return_type_list = new ArrayList<>();
                    ALittleMethodReturnTypeDec return_type_dec = getter_dec.getMethodReturnTypeDec();
                    if (return_type_dec != null)
                        return_type_list.add(return_type_dec);
                    break;
                } else if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec method_dec = (ALittleClassMethodDec)parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) return_type_list = return_dec.getMethodReturnTypeDecList();
                    break;
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec method_dec = (ALittleClassStaticDec)parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) return_type_list = return_dec.getMethodReturnTypeDecList();
                    break;
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec method_dec = (ALittleGlobalMethodDec)parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) return_type_list = return_dec.getMethodReturnTypeDecList();
                    break;
                }

                parent = parent.getParent();
            }

            boolean has_handle = false;

            // 如果返回值只有一个函数调用
            if (value_stat_list.size() == 1 && return_type_list.size() > 1) {
                ALittleValueStat value_stat = value_stat_list.get(0);

                List<PsiElement> method_call_guess_list = ALittleUtil.guessTypeForMethodCall(value_stat);
                if (method_call_guess_list != null) {
                    if (method_call_guess_list.size() != return_type_list.size())
                        error = "return的函数调用的返回值数量和函数定义的返回值数量不相等";
                    else {
                        for (int i = 0; i < return_type_list.size(); ++i) {

                            ALittleMethodReturnTypeDec return_type_dec = return_type_list.get(i);
                            PsiElement return_type_guess_type = ALittleUtil.guessType(return_type_dec);
                            if (return_type_guess_type == null) {
                                error = "return所在的函数的第" + (i + 1) + "个返回值是未知类型";
                                break;
                            }

                            List<String> error_content_list = new ArrayList<>();
                            List<PsiElement> error_element_list = new ArrayList<>();
                            boolean result = ALittleUtil.guessSoftTypeEqual(return_type_dec, return_type_guess_type, null, value_stat, method_call_guess_list.get(i), null
                                    , error_content_list, error_element_list);
                            if (!result)
                            {
                                error = "return的第" + (i + 1) + "个返回值数量和函数定义的返回值类型不同";
                                if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                        }
                    }
                    // 标记为已处理
                    has_handle = true;
                }
            }

            if (!has_handle) {
                if (return_type_list.size() != value_stat_list.size()) {
                    error = "return的返回值数量和函数定义的返回值数量不相等";
                } else {
                    // 每个类型依次检查
                    for (int i = 0; i < return_type_list.size(); ++i) {
                        ALittleValueStat value_stat = value_stat_list.get(i);

                        List<String> error_content_list = new ArrayList<>();
                        List<PsiElement> error_element_list = new ArrayList<>();
                        PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                        if (value_stat_guess_type == null) {
                            error = "return的第" + (i + 1) + "个返回值是未知类型";
                            if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }

                        ALittleMethodReturnTypeDec return_type_dec = return_type_list.get(i);
                        PsiElement return_type_guess_type = ALittleUtil.guessType(return_type_dec);
                        if (return_type_guess_type == null) {
                            error = "return所在的函数的第" + (i + 1) + "个返回值是未知类型";
                            break;
                        }

                        boolean result = ALittleUtil.guessSoftTypeEqual(return_type_dec, return_type_guess_type, null, value_stat, value_stat_guess_type, null
                                , error_content_list, error_element_list);
                        if (!result) {
                            error = "return的第" + (i + 1) + "个返回值数量和函数定义的返回值类型不同";
                            if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }
                    }
                }
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForVarAssign(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if (element instanceof ALittleVarAssignExpr) {
            ALittleVarAssignExpr dec = (ALittleVarAssignExpr)element;

            ALittleValueStat value_stat = dec.getValueStat();
            if (value_stat == null) return null;
            List<ALittleVarAssignPairDec> pair_dec_list = dec.getVarAssignPairDecList();

            boolean has_handle = false;

            // 如果返回值只有一个函数调用
            if (pair_dec_list.size() > 1) {
                List<PsiElement> method_call_guess_list = ALittleUtil.guessTypeForMethodCall(value_stat);
                if (method_call_guess_list != null) {
                    for (int i = 0; i < pair_dec_list.size(); ++i) {
                        ALittleVarAssignPairDec pair_dec = pair_dec_list.get(i);
                        if (pair_dec.getAllType() == null) continue;

                        PsiElement pair_guess_type = ALittleUtil.guessType(pair_dec.getAllType());
                        if (pair_guess_type == null) {
                            error = "等号左边所在的第" + (i + 1) + "个变量是未知类型";
                            break;
                        }
                        if (i >= method_call_guess_list.size()) {
                            break;
                        }
                        List<String> error_content_list = new ArrayList<>();
                        List<PsiElement> error_element_list = new ArrayList<>();
                        boolean result = ALittleUtil.guessSoftTypeEqual(pair_dec, pair_guess_type, null, value_stat, method_call_guess_list.get(i), null
                                , error_content_list, error_element_list);
                        if (!result)
                        {
                            error = "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等";
                            if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }
                    }

                    // 标记为已处理
                    has_handle = true;
                }
            }

            if (!has_handle && pair_dec_list.size() > 0) {
                ALittleVarAssignPairDec pair_dec = pair_dec_list.get(0);

                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess_type == null) {
                    error = "等号右边的表达式是未知类型";
                    if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                } else {
                    if (pair_dec.getAllType() != null) {
                        PsiElement pair_guess_type = ALittleUtil.guessType(pair_dec.getAllType());
                        if (pair_guess_type == null) {
                            error = "等号左边的变量是未知类型";
                        } else {
                            boolean result = ALittleUtil.guessSoftTypeEqual(pair_dec, pair_guess_type, null, value_stat, value_stat_guess_type, null
                                    , error_content_list, error_element_list);
                            if (!result) {
                                error = "等号左边的变量和表达式的类型不同";
                                if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            }
                        }
                    }
                }
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForOpAssign(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if (element instanceof ALittleOpAssignExpr) {
            ALittleOpAssignExpr dec = (ALittleOpAssignExpr)element;
            ALittleValueStat value_stat = dec.getValueStat();
            if (value_stat == null) return null;

            List<ALittlePropertyValue> property_value_list = dec.getPropertyValueList();

            boolean has_handle = false;

            // 如果返回值只有一个函数调用
            if (property_value_list.size() > 1) {
                List<PsiElement> method_call_guess_list = ALittleUtil.guessTypeForMethodCall(value_stat);
                if (method_call_guess_list != null) {
                    for (int i = 0; i < property_value_list.size(); ++i) {
                        ALittlePropertyValue property_value_dec = property_value_list.get(i);
                        PsiElement property_guess_type = ALittleUtil.guessType(property_value_dec);
                        if (property_guess_type == null) {
                            error = "等号左边所在的第" + (i + 1) + "个变量是未知类型";
                            break;
                        }
                        if (i >= method_call_guess_list.size()) {
                            break;
                        }
                        List<String> error_content_list = new ArrayList<>();
                        List<PsiElement> error_element_list = new ArrayList<>();
                        boolean result = ALittleUtil.guessSoftTypeEqual(property_value_dec, property_guess_type, null, value_stat, method_call_guess_list.get(i), null
                                , error_content_list, error_element_list);
                        if (!result)
                        {
                            error = "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等";
                            if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }
                    }

                    // 标记为已处理
                    has_handle = true;
                }
            }

            if (!has_handle && property_value_list.size() > 0) {
                ALittlePropertyValue property_value_dec = property_value_list.get(0);

                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess_type == null) {
                    error = "等号右边的表达式是未知类型";
                    if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                } else {
                    PsiElement property_guess_type = ALittleUtil.guessType(property_value_dec);
                    if (property_guess_type == null) {
                        error = "等号左边的变量是未知类型";
                    } else {
                        boolean result = ALittleUtil.guessSoftTypeEqual(property_value_dec, property_guess_type, null, value_stat, value_stat_guess_type, null
                                , error_content_list, error_element_list);
                        if (!result) {
                            error = "等号左边的变量和表达式的类型不同";
                            if (!error_content_list.isEmpty()) error += ":" + error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                        }
                    }
                }
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForIfAndElseIfAndWhileAndDoWhile(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        ALittleValueStat value_stat = null;
        if (element instanceof ALittleIfExpr) {
            ALittleIfExpr expr = (ALittleIfExpr)element;
            value_stat = expr.getValueStat();
        } else if (element instanceof ALittleElseIfExpr) {
            ALittleElseIfExpr expr = (ALittleElseIfExpr)element;
            value_stat = expr.getValueStat();
        } else if (element instanceof ALittleWhileExpr) {
            ALittleWhileExpr expr = (ALittleWhileExpr)element;
            value_stat = expr.getValueStat();
        } else if (element instanceof ALittleDoWhileExpr) {
            ALittleDoWhileExpr expr = (ALittleDoWhileExpr)element;
            value_stat = expr.getValueStat();
        }

        if (value_stat != null) {
            List<String> error_content_list = new ArrayList<>();
            List<PsiElement> error_element_list = new ArrayList<>();
            PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
            if (value_stat_guess_type == null) {
                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForFor(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        do
        {
            if (element instanceof ALittleForStepCondition) {
                // 初始表达式
                ALittleForStepCondition step_expr = (ALittleForStepCondition)element;
                if (step_expr.getForStartStat() == null) break;

                ALittleAllType all_type = step_expr.getForStartStat().getForPairDec().getAllType();
                if (all_type.getPrimitiveType() == null
                    || (!all_type.getPrimitiveType().getText().equals("int")
                    && !all_type.getPrimitiveType().getText().equals("I64"))) {
                    error = "这个变量必须是int或I64类型";
                    element = step_expr.getForStartStat().getForPairDec().getVarAssignNameDec();
                    break;
                }

                ALittleValueStat value_stat = step_expr.getForStartStat().getValueStat();
                if (value_stat == null) break;

                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
                ALittleUtil.GuessTypeInfo guess_info = ALittleUtil.guessTypeString(value_stat, value_stat_guess_type, error_content_list, error_element_list);
                if (guess_info == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double") && !guess_info.value.equals("any")) {
                    error = "等号右边的表达式类型必须是int,I64,double,any, 不能是:" + guess_info.value;
                    element = value_stat;
                    break;
                }

                // 结束表达式
                if (step_expr.getForEndStat() == null) break;
                value_stat = step_expr.getForEndStat().getValueStat();
                if (value_stat == null) break;

                error_content_list = new ArrayList<>();
                error_element_list = new ArrayList<>();
                value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
                guess_info = ALittleUtil.guessTypeString(value_stat, value_stat_guess_type, error_content_list, error_element_list);
                if (guess_info == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double") && !guess_info.value.equals("any")) {
                    error = "结束表达式类型必须是int,I64,double,any, 不能是:" + guess_info.value;
                    element = value_stat;
                    break;
                }

                // 步长表达式
                if (step_expr.getForStepStat() == null) break;
                value_stat = step_expr.getForStepStat().getValueStat();
                if (value_stat == null) break;

                error_content_list = new ArrayList<>();
                error_element_list = new ArrayList<>();
                value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
                guess_info = ALittleUtil.guessTypeString(value_stat, value_stat_guess_type, error_content_list, error_element_list);
                if (guess_info == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double") && !guess_info.value.equals("any")) {
                    error = "步长表达式类型必须是int,I64,double,any, 不能是:" + guess_info.value;
                    element = value_stat;
                    break;
                }
            } else if (element instanceof ALittleForInCondition) {
                ALittleForInCondition in_expr = (ALittleForInCondition)element;
                ALittleValueStat value_stat = in_expr.getValueStat();
                if (value_stat == null) break;

                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (guess_type instanceof ALittlePrimitiveType) {
                    ALittlePrimitiveType primitive_type = (ALittlePrimitiveType) guess_type;
                    if (!primitive_type.getText().equals("any")) {
                        error = "遍历对象类型必须是List,Map,any";
                        element = value_stat;
                        break;
                    }
                } else if (guess_type instanceof ALittleGenericType) {
                    ALittleGenericType generic_type = (ALittleGenericType) guess_type;
                    if (generic_type.getGenericListType() != null) {
                        ALittleGenericListType list_type = generic_type.getGenericListType();
                        List<ALittleForPairDec> pair_dec_list = in_expr.getForPairDecList();
                        if (pair_dec_list.size() != 2) {
                            error = "这里参数数量必须是2个";
                            element = in_expr;
                            break;
                        }
                        ALittleAllType all_type = pair_dec_list.get(0).getAllType();
                        if (all_type == null || all_type.getPrimitiveType() == null
                                || (!all_type.getPrimitiveType().getText().equals("int")
                                && !all_type.getPrimitiveType().getText().equals("I64"))) {
                            error = "这个变量必须是int或I64类型";
                            element = all_type;
                            break;
                        }

                        if (pair_dec_list.get(1).getAutoType() != null) break;

                        all_type = pair_dec_list.get(1).getAllType();
                        PsiElement pair_guess_type = ALittleUtil.guessType(all_type);
                        if (pair_guess_type == null) {
                            error = "左边所在的第2个变量是未知类型";
                            element = all_type;
                            break;
                        }
                        PsiElement list_guess_type = ALittleUtil.guessType(list_type.getAllType());

                        boolean result = ALittleUtil.guessSoftTypeEqual(value_stat, list_guess_type, null, pair_dec_list.get(1), pair_guess_type, null
                                , error_content_list, error_element_list);
                        if (!result) {
                            if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }
                    } else if (generic_type.getGenericMapType() != null) {
                        ALittleGenericMapType map_type = generic_type.getGenericMapType();
                        List<ALittleForPairDec> pair_dec_list = in_expr.getForPairDecList();
                        if (pair_dec_list.size() != 2) {
                            error = "这里参数数量必须是2个";
                            element = in_expr;
                            break;
                        }

                        List<ALittleAllType> all_type_list = map_type.getAllTypeList();
                        if (all_type_list.size() != 2) {
                            error = "Map格式错误";
                            element = in_expr;
                            break;
                        }

                        if (pair_dec_list.get(0).getAutoType() != null) break;

                        ALittleAllType all_type = pair_dec_list.get(0).getAllType();
                        PsiElement pair_guess_type = ALittleUtil.guessType(all_type);
                        if (pair_guess_type == null) {
                            error = "左边所在的第1个变量是未知类型";
                            element = all_type;
                            break;
                        }
                        PsiElement map_guess_type = ALittleUtil.guessType(all_type_list.get(0));

                        boolean result = ALittleUtil.guessSoftTypeEqual(value_stat, map_guess_type, null, pair_dec_list.get(0), pair_guess_type, null
                                , error_content_list, error_element_list);
                        if (!result) {
                            if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }

                        if (pair_dec_list.get(1).getAutoType() != null) break;

                        all_type = pair_dec_list.get(1).getAllType();
                        pair_guess_type = ALittleUtil.guessType(all_type);
                        if (pair_guess_type == null) {
                            error = "左边所在的第2个变量是未知类型";
                            element = all_type;
                            break;
                        }
                        map_guess_type = ALittleUtil.guessType(all_type_list.get(1));

                        result = ALittleUtil.guessSoftTypeEqual(pair_dec_list.get(1), pair_guess_type, null, value_stat, map_guess_type, null
                                , error_content_list, error_element_list);
                        if (!result) {
                            if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                            if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                            break;
                        }
                    }
                } else {
                    error = "遍历对象类型必须是List,Map,any";
                    element = value_stat;
                    break;
                }
            }
        } while (false);


        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForBrackValue(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        do {
            if (!(element instanceof ALittlePropertyValueBrackValueStat)) {
                break;
            }
            ALittlePropertyValueBrackValueStat brack_value = (ALittlePropertyValueBrackValueStat) element;
            ALittleValueStat value_stat = brack_value.getValueStat();
            if (value_stat == null) {
                break;
            }

            PsiReference[] references = brack_value.getReferences();
            if (references == null || references.length <= 0) {
                break;
            }
            ALittlePropertyValueBrackValueStatReference reference = (ALittlePropertyValueBrackValueStatReference) references[0];
            PsiElement pre_type = reference.guessTypesForPreType();

            if (!(pre_type instanceof ALittleGenericType)) {
                break;
            }
            ALittleGenericType generic_type = (ALittleGenericType) pre_type;
            // 如果是列表那么value_stat必须是一个int，I64，double，any
            if (generic_type.getGenericListType() != null) {
                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
                ALittleUtil.GuessTypeInfo guess_info = ALittleUtil.guessTypeString(value_stat, guess_type, error_content_list, error_element_list);
                if (guess_info == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double") && !guess_info.value.equals("any")) {
                    error = "步长表达式类型必须是int,I64,double,any, 不能是:" + guess_info.value;
                    element = value_stat;
                    break;
                }
            // 如果是映射表，那么就需要和key类型等价
            } else if (generic_type.getGenericMapType() != null) {
                ALittleGenericMapType map_type = generic_type.getGenericMapType();
                List<ALittleAllType> all_type_list = map_type.getAllTypeList();
                if (all_type_list.size() != 2) {
                    break;
                }
                ALittleAllType all_type = all_type_list.get(0);
                PsiElement key_guess_type = ALittleUtil.guessType(all_type);
                if (key_guess_type == null) {
                    error = "Map的key是未知类型";
                    element = all_type;
                    break;
                }
                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (guess_type == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                boolean result = ALittleUtil.guessSoftTypeEqual(generic_type, key_guess_type, null, value_stat, guess_type, null
                        , error_content_list, error_element_list);
                if (!result) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
            }
        } while (false);

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForMethodCall(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if (element instanceof ALittlePropertyValueMethodCallStat) {
            ALittlePropertyValueMethodCallStat method_call = (ALittlePropertyValueMethodCallStat)element;

            PsiReference[] references = method_call.getReferences();
            if (references != null && references.length > 0) {
                ALittlePropertyValueMethodCallStatReference reference = (ALittlePropertyValueMethodCallStatReference)references[0];
                PsiElement pre_type = reference.guessTypesForPreType();

                List<PsiElement> param_type_list = new ArrayList<>();
                boolean need_handle = true;

                if (pre_type instanceof ALittleMethodNameDec) {
                    ALittleMethodNameDec method_name_dec = (ALittleMethodNameDec)pre_type;
                    PsiElement method_dec = method_name_dec.getParent();

                    // getter函数使用()来调用，只有这种情况 Class.getter_x(object)
                    if (method_dec instanceof ALittleClassGetterDec) {
                        // 第一个参数就是getter所在的类
                        PsiElement parent = method_dec;
                        while (parent != null) {
                            if (parent instanceof ALittleClassDec) {
                                param_type_list.add(parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                        if (param_type_list.size() != 1)
                            need_handle = false;
                    // setter函数使用()来调用，只有这种情况 Class.setter_x(object, value)
                    } else if (method_dec instanceof ALittleClassSetterDec) {
                        // 第一个参数就是setter所在的类
                        PsiElement parent = method_dec;
                        while (parent != null) {
                            if (parent instanceof ALittleClassDec) {
                                param_type_list.add(parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                        ALittleClassSetterDec dec = (ALittleClassSetterDec)method_dec;
                        ALittleMethodParamOneDec one_dec = dec.getMethodParamOneDec();
                        if (one_dec != null) {
                            PsiElement param_guess = ALittleUtil.guessType(one_dec.getMethodParamTypeDec().getAllType());
                            if (param_guess != null) {
                                param_type_list.add(param_guess);
                            }
                        }
                        if (param_type_list.size() != 2)
                            need_handle = false;
                    } else if (method_dec instanceof ALittleClassMethodDec) {
                        ALittleClassMethodDec dec = (ALittleClassMethodDec)method_dec;

                        // 如果是使用类的方式调用，那么还需要加上一个参数
                        ALittleClassDec class_dec = reference.guessClassNameInvoke();
                        if (class_dec != null) {
                            param_type_list.add(class_dec);
                        }

                        ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                        if (param_dec != null) {
                            List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                            for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                                PsiElement param_guess = ALittleUtil.guessType(one_dec.getMethodParamTypeDec().getAllType());
                                if (param_guess == null) {
                                    need_handle = false;
                                    break;
                                } else {
                                    param_type_list.add(param_guess);
                                }
                            }
                        }
                    } else if (method_dec instanceof ALittleClassStaticDec) {
                        ALittleClassStaticDec dec = (ALittleClassStaticDec)method_dec;
                        ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                        if (param_dec != null) {
                            List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                            for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                                PsiElement param_guess = ALittleUtil.guessType(one_dec.getMethodParamTypeDec().getAllType());
                                if (param_guess == null) {
                                    need_handle = false;
                                    break;
                                } else {
                                    param_type_list.add(param_guess);
                                }
                            }
                        }
                    } else if (method_dec instanceof ALittleGlobalMethodDec) {
                        ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec)method_dec;
                        ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                        if (param_dec != null) {
                            List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                            for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                                PsiElement param_guess = ALittleUtil.guessType(one_dec.getMethodParamTypeDec().getAllType());
                                if (param_guess == null) {
                                    need_handle = false;
                                    break;
                                } else {
                                    param_type_list.add(param_guess);
                                }
                            }
                        }
                    }
                } else if (pre_type instanceof ALittleGenericType) {
                    ALittleGenericType dec = (ALittleGenericType)pre_type;
                    if (dec.getGenericFunctorType() == null) {
                        need_handle = false;
                    } else {
                        ALittleGenericFunctorType functor_type = dec.getGenericFunctorType();
                        ALittleGenericFunctorParamType param_type = functor_type.getGenericFunctorParamType();
                        if (param_type != null) {
                            List<ALittleAllType> all_type_list = param_type.getAllTypeList();
                            for (ALittleAllType all_type : all_type_list) {
                                PsiElement guess_type = ALittleUtil.guessType(all_type);
                                if (guess_type == null) {
                                    need_handle = false;
                                    break;
                                } else {
                                    param_type_list.add(guess_type);
                                }
                            }
                        }
                    }
                } else if (pre_type instanceof ALittlePrimitiveType && pre_type.getText().equals("any")) {
                    need_handle = false;
                } else if (pre_type instanceof ALittlePropertyValueMethodCallStat) {
                    need_handle = false;
                } else if (pre_type instanceof ALittlePropertyValueBrackValueStat) {
                    need_handle = false;
                }

                // 如果需要处理
                if (need_handle) {
                    List<ALittleValueStat> value_stat_list = method_call.getValueStatList();
                    if (param_type_list.size() < value_stat_list.size()) {
                        error = "函数调用最多需要" + param_type_list.size() + "个参数,不能是:" + value_stat_list.size() + "个";
                    } else {
                        for (int i = 0; i < param_type_list.size(); ++i) {
                            if (i >= value_stat_list.size()) break;

                            List<String> error_content_list = new ArrayList<>();
                            List<PsiElement> error_element_list = new ArrayList<>();
                            PsiElement value_stat_guess = ALittleUtil.guessSoftType(value_stat_list.get(i), value_stat_list.get(i), error_content_list, error_element_list);
                            if (value_stat_guess == null) {
                                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                            boolean result = ALittleUtil.guessSoftTypeEqual(element, param_type_list.get(i), null, value_stat_list.get(i), value_stat_guess, null, error_content_list, error_element_list);
                            if (!result) {
                                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForOpNewStat(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if  (element instanceof ALittleOpNewStat) {
            ALittleOpNewStat op_new_stat = (ALittleOpNewStat) element;

            List<ALittleValueStat> value_stat_list = op_new_stat.getValueStatList();

            do {
                if (op_new_stat.getGenericType() != null) {
                    if (value_stat_list.size() > 0) {
                        error = "创建容器实例对象不能有参数";
                        break;
                    }
                } else if (op_new_stat.getCustomType() != null) {
                    ALittleCustomType custom_type = op_new_stat.getCustomType();
                    PsiElement guess_type = custom_type.getCustomTypeNameDec().guessType();
                    if (guess_type instanceof ALittleClassDec) {
                        ALittleClassDec class_dec = (ALittleClassDec) guess_type;
                        List<ALittleClassCtorDec> ctor_dec_list = class_dec.getClassCtorDecList();
                        if (ctor_dec_list.size() > 1) {
                            error = "new的类的构造函数个数不能超过1个";
                            break;
                        }

                        if (ctor_dec_list.size() == 0) {
                            if (value_stat_list.size() > 0) {
                                error = "new的类的构造函数没有参数";
                                break;
                            }
                            break;
                        }

                        ALittleMethodParamDec param_dec = ctor_dec_list.get(0).getMethodParamDec();
                        if (param_dec == null) {
                            if (value_stat_list.size() > 0) {
                                error = "new的类的构造函数没有参数";
                                break;
                            }
                            break;
                        }

                        List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                        List<PsiElement> param_type_list = new ArrayList<>();
                        boolean has_error = false;
                        for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                            ALittleAllType all_type = param_one_dec.getMethodParamTypeDec().getAllType();
                            PsiElement guess = ALittleUtil.guessType(all_type);
                            if (guess == null) {
                                has_error = true;
                                break;
                            }
                            param_type_list.add(guess);
                        }
                        if (has_error) break;

                        if (param_type_list.size() < value_stat_list.size()) {
                            error = "new的类的构造函数调用最多需要" + param_type_list.size() + "个参数,不能是:" + value_stat_list.size() + "个";
                            break;
                        }

                        for (int i = 0; i < param_type_list.size(); ++i) {
                            if (i >= value_stat_list.size()) break;

                            List<String> error_content_list = new ArrayList<>();
                            List<PsiElement> error_element_list = new ArrayList<>();
                            PsiElement value_stat_guess = ALittleUtil.guessSoftType(value_stat_list.get(i), value_stat_list.get(i), error_content_list, error_element_list);
                            if (value_stat_guess == null) {
                                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                            boolean result = ALittleUtil.guessSoftTypeEqual(element, param_type_list.get(i), null, value_stat_list.get(i), value_stat_guess, null, error_content_list, error_element_list);
                            if (!result) {
                                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                        }
                    }
                }
            } while (false);
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForOpNewList(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if  (element instanceof ALittleOpNewList) {
            ALittleOpNewList op_new_list = (ALittleOpNewList) element;

            List<ALittleValueStat> value_stat_list = op_new_list.getValueStatList();
            do {
                if (value_stat_list.isEmpty()) {
                    error = "这种方式不能内有元素，请使用new List的方式";
                    break;
                }

                // 列表里面的所有元素的类型必须和第一个元素一致
                ALittleUtil.GuessTypeInfo value_stat_first = null;
                {
                    List<String> error_content_list = new ArrayList<>();
                    List<PsiElement> error_element_list = new ArrayList<>();
                    value_stat_first = ALittleUtil.guessTypeString(value_stat_list.get(0), value_stat_list.get(0), error_content_list, error_element_list);
                    if (value_stat_first == null) {
                        if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                        if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                        break;
                    }
                }

                for (int i = 1; i < value_stat_list.size(); ++i) {
                    List<String> error_content_list = new ArrayList<>();
                    List<PsiElement> error_element_list = new ArrayList<>();
                    ALittleUtil.GuessTypeInfo value_stat_info = ALittleUtil.guessTypeString(value_stat_list.get(i), value_stat_list.get(i), error_content_list, error_element_list);
                    if (value_stat_info == null) {
                        if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                        if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                        break;
                    }
                    if (!value_stat_first.value.equals(value_stat_info.value)) {
                        error = "列表内的元素类型，必须和第一个元素类型一致";
                        break;
                    }
                }
            } while (false);
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static String CheckErrorForName(@NotNull PsiElement element, AnnotationHolder holder, List<PsiElement> guess_list) {
        String error = null;

        if  (element instanceof ALittleVarAssignNameDec) {
            if (element.getText().startsWith("__")) {
                error = "局部变量名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleNamespaceNameDec) {
            if (element.getText().startsWith("__")) {
                error = "命名域不能以两个下划线开头";
            }
        } else if (element instanceof ALittleClassNameDec) {
            if (element.getText().startsWith("__")) {
                error = "类名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleStructNameDec) {
            if (element.getText().startsWith("__")) {
                error = "结构体名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleEnumNameDec) {
            if (element.getText().startsWith("__")) {
                error = "枚举名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleInstanceNameDec) {
            if (element.getText().startsWith("__")) {
                error = "单例名不能以两个下划线开头";
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void ColorAnnotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder, List<PsiElement> guess_list) {
        // 函数的参数名
        if (element instanceof ALittleMethodParamNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
            return;
            // this
        } else if (element instanceof ALittlePropertyValueThisType) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD2_ATTRIBUTES);
            return;
            // 常量
        } else if (element instanceof ALittleConstValue) {
            ALittleConstValue dec = (ALittleConstValue)element;
            if (dec.getDigitContent() != null) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
                return;
            } else if (dec.getStringContent() != null) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STRING);
                return;
            } else if (dec.getText().equals("true") || dec.getText().equals("false") || dec.getText().equals("null")) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(ConsoleHighlighter.CYAN_BRIGHT);
                return;
            }
            // 类名
        } else if (element instanceof ALittleClassNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_NAME);
            return;
            // 属性如果是函数名
        } else if (element instanceof ALittlePropertyValueDotIdName) {
            ALittlePropertyValueDotIdName dec = (ALittlePropertyValueDotIdName)element;
            PsiElement guess = null;
            if (guess_list != null && !guess_list.isEmpty()) guess = guess_list.get(0);
            if (guess instanceof ALittleMethodNameDec) {
                Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                return;
            }
            // 起始属性
        } else if (element instanceof ALittlePropertyValueCustomType) {
            ALittlePropertyValueCustomType dec = (ALittlePropertyValueCustomType)element;
            PsiReference[] ref_list = dec.getReferences();
            if (ref_list.length > 0) {
                PsiElement resolve = ref_list[0].resolve();
                if (resolve instanceof ALittleMethodParamNameDec) {
                    Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                    anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
                    return;
                } else if (resolve instanceof ALittleVarAssignNameDec) {
                    PsiElement parent = resolve.getParent();
                    if (parent instanceof ALittleForPairDec) {
                        Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                        anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
                        return;
                    }
                } else if (resolve instanceof ALittleMethodNameDec) {
                    Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                    anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                    return;
                }
            }
            // 局部变量
        } else if (element instanceof ALittleVarAssignNameDec) {
            PsiElement parent = element.getParent();
            if (parent instanceof ALittleForPairDec) {
                ALittleVarAssignNameDec dec = (ALittleVarAssignNameDec)element;
                Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
                return;
            }
            // 自定义类型
        } else if (element instanceof ALittleCustomTypeNameDec) {
            ALittleCustomTypeNameDec dec = (ALittleCustomTypeNameDec)element;
            Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
            return;
        } else if (element instanceof ALittleInstanceClassNameDec) {
            ALittleInstanceClassNameDec dec = (ALittleInstanceClassNameDec)element;
            Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
            return;
        }
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // 获取对应的定义
        List<PsiElement> guess_list = GetGuessList(element);

        // 检查未定义或者重复定义
        CheckErrorForGuessList(element, holder, guess_list);

        // 枚举类型错误检查
        CheckErrorForEnum(element, holder, guess_list);

        // 结构体类型错误检查
        CheckErrorForStruct(element, holder, guess_list);

        // return语句返回的内容和函数定义的返回值相符
        CheckErrorForReturn(element, holder, guess_list);

        // 赋值语句左右两方那个的类型检查
        CheckErrorForVarAssign(element, holder, guess_list);

        // 赋值语句左右两方那个的类型检查
        CheckErrorForOpAssign(element, holder, guess_list);

        // if elseif while do while 条件表达式检查
        CheckErrorForIfAndElseIfAndWhileAndDoWhile(element, holder, guess_list);

        // for语句内部局部变量的类型
        CheckErrorForFor(element, holder, guess_list);

        // 检查函数调用时参数个数，和参数类型
        CheckErrorForMethodCall(element, holder, guess_list);

        // 检查中括号内部值的类型检查
        CheckErrorForBrackValue(element, holder, guess_list);

        // 检查new表达式的参数
        CheckErrorForOpNewStat(element, holder, guess_list);

        // 检查便捷List表达式
        CheckErrorForOpNewList(element, holder, guess_list);

        // 检查变量名
        CheckErrorForName(element, holder, guess_list);

        // 给元素上色
        ColorAnnotate(element, holder, guess_list);
    }
}