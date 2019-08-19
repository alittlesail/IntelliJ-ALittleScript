package plugin;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class ALittleAnnotator implements Annotator {

    // 获取元素定义
    public static List<ALittleReferenceUtil.GuessTypeInfo> GetGuessList(@NotNull PsiElement element) throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = null;

        // 类相关
        if (element instanceof ALittleClassNameDec) {
            guessList = ((ALittleClassNameDec) element).guessTypes();
        } else if (element instanceof ALittleNamespaceNameDec) {
            guessList = ((ALittleNamespaceNameDec) element).guessTypes();
        } else if (element instanceof ALittleClassVarDec) {
            guessList = ((ALittleClassVarDec) element).guessTypes();

        // 自定义类型相关
        } else if (element instanceof ALittleCustomType) {
            guessList = ((ALittleCustomType) element).guessTypes();

            // 枚举相关
        } else if (element instanceof ALittleEnumNameDec) {
            guessList = ((ALittleEnumNameDec) element).guessTypes();

            // 函数相关
        } else if (element instanceof ALittleMethodParamNameDec) {
            guessList = ((ALittleMethodParamNameDec) element).guessTypes();

            // 属性相关
        } else if (element instanceof ALittlePropertyValueBracketValue) {
            guessList = ((ALittlePropertyValueBracketValue) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCustomType) {
            guessList = ((ALittlePropertyValueCustomType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueDotIdName) {
            guessList = ((ALittlePropertyValueDotIdName) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueThisType) {
            guessList = ((ALittlePropertyValueThisType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCastType) {
            guessList = ((ALittlePropertyValueCastType) element).guessTypes();

            // 结构体相关
        } else if (element instanceof ALittleStructNameDec) {
            guessList = ((ALittleStructNameDec) element).guessTypes();
        } else if (element instanceof ALittleStructVarDec) {
            guessList = ((ALittleStructVarDec) element).guessTypes();

            // 局部变量定义
        } else if (element instanceof ALittleVarAssignNameDec) {
            guessList = ((ALittleVarAssignNameDec) element).guessTypes();
        }

        return guessList;
    }

    public static void CheckErrorForGuessList(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        if (guessList == null) return;

        // 检查定义
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "未知类型");
        }

        // 检查重复定义
        if (guessList.size() > 1 && !(guessList.get(0) instanceof ALittleNamespaceNameDec)) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "重复定义");
        }
    }

    public static void CheckErrorForOpNewStat(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        String error = null;

        if  (element instanceof ALittleOpNewStat) {
            ALittleOpNewStat op_new_stat = (ALittleOpNewStat) element;

            List<ALittleValueStat> valueStatList = op_new_stat.getValueStatList();

            do {
                if (op_new_stat.getGenericType() != null) {
                    if (valueStatList.size() > 0) {
                        error = "创建容器实例对象不能有参数";
                        break;
                    }
                } else if (op_new_stat.getCustomType() != null) {
                    ALittleCustomType customType = op_new_stat.getCustomType();
                    PsiElement guessType = customType.getCustomTypeNameDec().guessType();
                    if (guessType instanceof ALittleClassDec) {
                        ALittleClassDec classDec = (ALittleClassDec) guessType;
                        List<ALittleClassCtorDec> ctor_decList = classDec.getClassCtorDecList();
                        if (ctor_decList.size() > 1) {
                            error = "new的类的构造函数个数不能超过1个";
                            break;
                        }

                        if (ctor_decList.size() == 0) {
                            if (valueStatList.size() > 0) {
                                error = "new的类的构造函数没有参数";
                                break;
                            }
                            break;
                        }

                        ALittleMethodParamDec param_dec = ctor_decList.get(0).getMethodParamDec();
                        if (param_dec == null) {
                            if (valueStatList.size() > 0) {
                                error = "new的类的构造函数没有参数";
                                break;
                            }
                            break;
                        }

                        List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
                        List<PsiElement> param_type_list = new ArrayList<>();
                        boolean has_error = false;
                        for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                            ALittleAllType allType = param_one_dec.getMethodParamTypeDec().getAllType();
                            PsiElement guess = ALittleUtil.guessType(allType);
                            if (guess == null) {
                                has_error = true;
                                break;
                            }
                            param_type_list.add(guess);
                        }
                        if (has_error) break;

                        if (param_type_list.size() < valueStatList.size()) {
                            error = "new的类的构造函数调用最多需要" + param_type_list.size() + "个参数,不能是:" + valueStatList.size() + "个";
                            break;
                        }

                        for (int i = 0; i < param_type_list.size(); ++i) {
                            if (i >= valueStatList.size()) break;

                            List<String> error_content_list = new ArrayList<>();
                            List<PsiElement> error_element_list = new ArrayList<>();
                            PsiElement value_stat_guess = ALittleUtil.guessSoftType(valueStatList.get(i), valueStatList.get(i), error_content_list, error_element_list);
                            if (value_stat_guess == null) {
                                if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                                if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                                break;
                            }
                            boolean result = ALittleUtil.guessSoftTypeEqual(element, param_type_list.get(i), null, valueStatList.get(i), value_stat_guess, null, error_content_list, error_element_list);
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

    public static void CheckErrorForOpNewList(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        String error = null;

        if  (element instanceof ALittleOpNewList) {
            ALittleOpNewList op_new_list = (ALittleOpNewList) element;

            List<ALittleValueStat> valueStatList = op_new_list.getValueStatList();
            do {
                if (valueStatList.isEmpty()) {
                    error = "这种方式不能内有元素，请使用new List的方式";
                    break;
                }

                // 列表里面的所有元素的类型必须和第一个元素一致
                ALittleUtil.GuessTypeInfo value_stat_first = null;
                {
                    List<String> error_content_list = new ArrayList<>();
                    List<PsiElement> error_element_list = new ArrayList<>();
                    value_stat_first = ALittleUtil.guessTypeString(valueStatList.get(0), valueStatList.get(0), null, error_content_list, error_element_list);
                    if (value_stat_first == null) {
                        if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                        if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                        break;
                    }
                }

                for (int i = 1; i < valueStatList.size(); ++i) {
                    List<String> error_content_list = new ArrayList<>();
                    List<PsiElement> error_element_list = new ArrayList<>();
                    ALittleUtil.GuessTypeInfo value_stat_info = ALittleUtil.guessTypeString(valueStatList.get(i), valueStatList.get(i), null, error_content_list, error_element_list);
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

    public static void CheckErrorForName(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        String error = null;

        if  (element instanceof ALittleVarAssignNameDec) {
            if (element.getText().startsWith("___")) {
                error = "局部变量名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleNamespaceNameDec) {
            if (element.getText().startsWith("___")) {
                error = "命名域不能以两个下划线开头";
            }
        } else if (element instanceof ALittleClassNameDec) {
            if (element.getText().startsWith("___")) {
                error = "类名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleStructNameDec) {
            if (element.getText().startsWith("___")) {
                error = "结构体名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleEnumNameDec) {
            if (element.getText().startsWith("___")) {
                error = "枚举名不能以两个下划线开头";
            }
        } else if (element instanceof ALittleInstanceNameDec) {
            if (element.getText().startsWith("___")) {
                error = "单例名不能以两个下划线开头";
            }
        }

        if (error != null && holder != null && element != null) {
            holder.createErrorAnnotation(element, error);
        }

        return error;
    }

    public static void CheckErrorForBindStat(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        String error = null;

        if (element instanceof ALittleBindStat) {
            ALittleBindStat bind_stat = (ALittleBindStat) element;

            do {
                List<ALittleValueStat> valueStatList = bind_stat.getValueStatList();
                if (valueStatList.isEmpty()) {
                    error = "bind 表达式不能没有参数";
                    break;
                }

                ALittleValueStat value_stat = valueStatList.get(0);
                // 第一个参数必须是函数
                List<String> error_content_list = new ArrayList<>();
                List<PsiElement> error_element_list = new ArrayList<>();
                PsiElement value_stat_guess = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                if (value_stat_guess == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }
                ALittleUtil.GuessTypeInfo guess_info = ALittleUtil.guessTypeString(value_stat, value_stat_guess, null, error_content_list, error_element_list);
                if (guess_info == null) {
                    if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                    if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                    break;
                }

                if (guess_info.type != ALittleUtil.GuessType.GT_FUNCTOR) {
                    element = value_stat;
                    error = "bind 表达式第一个参数必须是一个函数";
                    break;
                }

                // 后面跟的参数数量不能超过这个函数的参数个数
                if (valueStatList.size() - 1 > guess_info.functor_param_list.size()) {
                    error = "bind 表达式参数太多了";
                    break;
                }

                // 遍历所有的表达式，看下是否符合
                for (int i = 1; i < valueStatList.size(); ++i) {
                    value_stat = valueStatList.get(i);
                    ALittleUtil.GuessTypeInfo param_guess_info = guess_info.functor_param_list.get(i - 1);

                    value_stat_guess = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                    if (value_stat_guess == null) {
                        if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                        if (!error_element_list.isEmpty()) element = error_element_list.get(0);
                        break;
                    }

                    boolean result = ALittleUtil.guessSoftTypeEqual(valueStatList.get(0), null, param_guess_info, value_stat, value_stat_guess, null, error_content_list, error_element_list);
                    if (!result) {
                        if (!error_content_list.isEmpty()) error = error_content_list.get(0);
                        if (!error_element_list.isEmpty()) element = error_element_list.get(0);
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void ColorAnnotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        // 函数的参数名
        if (element instanceof ALittleMethodParamNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOmKeyWORD3_ATTRIBUTES);
            return;
            // this
        } else if (element instanceof ALittlePropertyValueThisType) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOmKeyWORD2_ATTRIBUTES);
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
            if (guessList != null && !guessList.isEmpty()) guess = guessList.get(0);
            if (guess instanceof ALittleMethodNameDec) {
                Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                return;
            }
            // 起始属性
        } else if (element instanceof ALittlePropertyValueCustomType) {
            ALittlePropertyValueCustomType dec = (ALittlePropertyValueCustomType)element;
            PsiReference ref = dec.getReference();
            if (ref != null) {
                PsiElement resolve = ref.resolve();
                if (resolve instanceof ALittleMethodParamNameDec) {
                    Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                    anno.setTextAttributes(CustomHighlighterColors.CUSTOmKeyWORD3_ATTRIBUTES);
                    return;
                } else if (resolve instanceof ALittleVarAssignNameDec) {
                    PsiElement parent = resolve.getParent();
                    if (parent instanceof ALittleForPairDec) {
                        Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                        anno.setTextAttributes(CustomHighlighterColors.CUSTOmKeyWORD3_ATTRIBUTES);
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
                anno.setTextAttributes(CustomHighlighterColors.CUSTOmKeyWORD3_ATTRIBUTES);
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
        List<PsiElement> guessList = GetGuessList(element);

        // 检查未定义或者重复定义
        CheckErrorForGuessList(element, holder, guessList);

        // 检查反射操作
        CheckErrorForReflect(element, holder, guessList);

        // 枚举类型错误检查
        CheckErrorForEnum(element, holder, guessList);

        // 结构体类型错误检查
        CheckErrorForStruct(element, holder, guessList);

        // return语句返回的内容和函数定义的返回值相符
        CheckErrorForReturn(element, holder, guessList);

        // 赋值语句左右两方那个的类型检查
        CheckErrorForVarAssign(element, holder, guessList);

        // 赋值语句左右两方那个的类型检查
        CheckErrorForOpAssign(element, holder, guessList);

        // if elseif while do while 条件表达式检查
        CheckErrorForIfAndElseIfAndWhileAndDoWhile(element, holder, guessList);

        // for语句内部局部变量的类型
        CheckErrorForFor(element, holder, guessList);

        // 检查函数调用时参数个数，和参数类型
        CheckErrorForMethodCall(element, holder, guessList);

        // 检查中括号内部值的类型检查
        CheckErrorForBrackValue(element, holder, guessList);

        // 检查new表达式的参数
        CheckErrorForOpNewStat(element, holder, guessList);

        // 检查便捷List表达式
        CheckErrorForOpNewList(element, holder, guessList);

        // 检查变量名
        CheckErrorForName(element, holder, guessList);

        // 检查bind表达式
        CheckErrorForBindStat(element, holder, guessList);

        // 给元素上色
        ColorAnnotate(element, holder, guessList);
    }
}