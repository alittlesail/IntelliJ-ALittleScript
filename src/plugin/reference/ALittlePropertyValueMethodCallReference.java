package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittlePropertyValueMethodCallReference extends ALittleReference<ALittlePropertyValueMethodCall> {
    public ALittlePropertyValueMethodCallReference(@NotNull ALittlePropertyValueMethodCall element, TextRange textRange) {
        super(element, textRange);
    }

    public ALittleGuess guessPreType() throws ALittleGuessException {
        ALittleGuess guess = null;

        // 获取父节点
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix) myElement.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue) property_value_suffix.getParent();
        ALittlePropertyValueFirstType property_value_first_type = property_value.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffix_list.indexOf(property_value_suffix);
        if (index == -1) return guess;

        // 获取前一个类型
        ALittleGuess pre_type = null;
        ALittleGuess pre_pre_type = null;
        if (index == 0) {
            pre_type = property_value_first_type.guessType();
        } else if (index == 1) {
            pre_type = suffix_list.get(index - 1).guessType();
            pre_pre_type = property_value_first_type.guessType();
        } else {
            pre_type = suffix_list.get(index - 1).guessType();
            pre_pre_type = suffix_list.get(index - 2).guessType();
        }

        // 如果是Functor
        if (pre_type instanceof ALittleGuessFunctor) {
            ALittleGuessFunctor pre_type_functor = (ALittleGuessFunctor) pre_type;
            if (pre_pre_type instanceof ALittleGuessTemplate)
                pre_pre_type = ((ALittleGuessTemplate) pre_pre_type).template_extends;

            // 如果再往前一个是一个Class实例对象，那么就要去掉第一个参数
            if (pre_pre_type instanceof ALittleGuessClass && pre_type_functor.param_list.size() > 0
                    && (pre_type_functor.element instanceof ALittleClassMethodDec
                    || pre_type_functor.element instanceof ALittleClassGetterDec
                    || pre_type_functor.element instanceof ALittleClassSetterDec)) {
                ALittleGuessFunctor new_pre_type_functor = new ALittleGuessFunctor(pre_type_functor.element);
                pre_type = new_pre_type_functor;

                new_pre_type_functor.await_modifier = pre_type_functor.await_modifier;
                new_pre_type_functor.const_modifier = pre_type_functor.const_modifier;
                new_pre_type_functor.proto = pre_type_functor.proto;
                new_pre_type_functor.template_param_list.addAll(pre_type_functor.template_param_list);
                new_pre_type_functor.param_list.addAll(pre_type_functor.param_list);
                new_pre_type_functor.param_nullable_list.addAll(pre_type_functor.param_nullable_list);
                new_pre_type_functor.param_name_list.addAll(pre_type_functor.param_name_list);
                new_pre_type_functor.param_tail = pre_type_functor.param_tail;
                new_pre_type_functor.return_list.addAll(pre_type_functor.return_list);
                new_pre_type_functor.return_tail = pre_type_functor.return_tail;

                // 移除掉第一个参数
                new_pre_type_functor.param_list.remove(0);
                new_pre_type_functor.param_nullable_list.remove(0);
                new_pre_type_functor.param_name_list.remove(0);

                new_pre_type_functor.updateValue();
            }
        }

        guess = pre_type;
        return guess;
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        Map<String, ALittleGuessTemplate> src_map = new HashMap<>();
        Map<String, ALittleGuess> fill_map = new HashMap<String, ALittleGuess>();
        ALittleGuessFunctor pre_type_functor = checkTemplateMap(src_map, fill_map);
        if (pre_type_functor == null) return guess_list;

        for (ALittleGuess guess : pre_type_functor.return_list) {
            if (guess.needReplace()) {
                ALittleGuess replace = guess.replaceTemplate(fill_map);
                if (replace == null) throw new ALittleGuessException(myElement, "模板替换失败:" + guess.getValue());
                guess_list.add(replace);
            } else
                guess_list.add(guess);
        }

        if (pre_type_functor.return_tail != null)
            guess_list.add(pre_type_functor.return_tail);

        return guess_list;
    }

    @Override
    public boolean multiGuessTypes() {
        return true;
    }

    private void analysisTemplate(@NotNull Map<String, ALittleGuess> fill_map,
                                  @NotNull ALittleGuess left_guess, @NotNull PsiElement right_src, @NotNull ALittleGuess right_guess, boolean assign_or_call) throws ALittleGuessException {
        // 如果值等于null，那么可以赋值
        if (right_guess.getValue().equals("null")) return;

        // const是否可以赋值给非const
        if (assign_or_call) {
            if (left_guess.is_const && !right_guess.is_const)
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ", 不能是:" + right_guess.getValue());
        } else {
            // 如果不是基本变量类型（排除any），基本都是值传递，函数调用时就不用检查const
            if (!(left_guess instanceof ALittleGuessPrimitive) || left_guess.getValueWithoutConst().equals("any")) {
                if (!left_guess.is_const && right_guess.is_const)
                    throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ", 不能是:" + right_guess.getValue());
            }
        }

        // 如果任何一方是any，那么就认为可以相等
        if (left_guess instanceof ALittleGuessAny) return;

        if (left_guess instanceof ALittleGuessPrimitive
                || left_guess instanceof ALittleGuessStruct) {
            ALittleReferenceOpUtil.guessTypeEqual(left_guess, right_src, right_guess, assign_or_call, false);
            return;
        }

        if (left_guess instanceof ALittleGuessMap) {
            if (!(right_guess instanceof ALittleGuessMap))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            try {
                analysisTemplate(fill_map, ((ALittleGuessMap) left_guess).key_type, right_src, ((ALittleGuessMap) right_guess).key_type, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            try {
                analysisTemplate(fill_map, ((ALittleGuessMap) left_guess).value_type, right_src, ((ALittleGuessMap) right_guess).value_type, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            return;
        }

        if (left_guess instanceof ALittleGuessList) {
            if (!(right_guess instanceof ALittleGuessList))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            try {
                analysisTemplate(fill_map, ((ALittleGuessList) left_guess).sub_type, right_src, ((ALittleGuessList) right_guess).sub_type, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            return;
        }

        if (left_guess instanceof ALittleGuessFunctor) {
            if (!(right_guess instanceof ALittleGuessFunctor))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            ALittleGuessFunctor left_guess_functor = (ALittleGuessFunctor) left_guess;
            ALittleGuessFunctor right_guess_functor = (ALittleGuessFunctor) right_guess;

            if (left_guess_functor.param_list.size() != right_guess_functor.param_list.size()
                    || left_guess_functor.param_nullable_list.size() != right_guess_functor.param_nullable_list.size()
                    || left_guess_functor.return_list.size() != right_guess_functor.return_list.size()
                    || left_guess_functor.template_param_list.size() != right_guess_functor.template_param_list.size()
                    || left_guess_functor.await_modifier != right_guess_functor.await_modifier
                    || left_guess_functor.proto == null && right_guess_functor.proto != null
                    || left_guess_functor.proto != null && right_guess_functor.proto == null
                    || (left_guess_functor.proto != null && left_guess_functor.proto != right_guess_functor.proto)
                    || left_guess_functor.param_tail == null && right_guess_functor.param_tail != null
                    || left_guess_functor.param_tail != null && right_guess_functor.param_tail == null
                    || left_guess_functor.return_tail == null && right_guess_functor.return_tail != null
                    || left_guess_functor.return_tail != null && right_guess_functor.return_tail == null
            ) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            for (int i = 0; i < left_guess_functor.template_param_list.size(); ++i) {
                analysisTemplate(fill_map, left_guess_functor.template_param_list.get(i), right_src, right_guess_functor.template_param_list.get(i), false);
            }

            for (int i = 0; i < left_guess_functor.param_list.size(); ++i) {
                analysisTemplate(fill_map, left_guess_functor.param_list.get(i), right_src, right_guess_functor.param_list.get(i), false);
            }

            for (int i = 0; i < left_guess_functor.param_nullable_list.size(); ++i) {
                if (left_guess_functor.param_nullable_list.get(i) != right_guess_functor.param_nullable_list.get(i))
                    throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            for (int i = 0; i < left_guess_functor.return_list.size(); ++i) {
                analysisTemplate(fill_map, left_guess_functor.return_list.get(i), right_src, right_guess_functor.return_list.get(i), false);
            }
            return;
        }

        if (left_guess instanceof ALittleGuessClass) {
            if (right_guess instanceof ALittleGuessTemplate)
                right_guess = ((ALittleGuessTemplate) right_guess).template_extends;

            if (!(right_guess instanceof ALittleGuessClass))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            if (left_guess.getValue() == right_guess.getValue()) return;

            boolean result = PsiHelper.isClassSuper(((ALittleGuessClass) left_guess).class_dec, right_guess.getValue());
            if (result) return;
            result = PsiHelper.isClassSuper(((ALittleGuessClass) right_guess).class_dec, left_guess.getValue());
            if (result) return;

            throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessTemplate) {
            ALittleGuessTemplate left_guess_template = (ALittleGuessTemplate) left_guess;

            // 查看模板是否已经被填充，那么就按填充的检查
            ALittleGuess fill_guess = fill_map.get(left_guess_template.getValue());
            if (fill_guess != null) {
                ALittleReferenceOpUtil.guessTypeEqual(fill_guess, right_src, right_guess, false, false);
                return;
            }

            // 处理还未填充
            if (left_guess_template.template_extends != null) {
                analysisTemplate(fill_map, left_guess_template.template_extends, right_src, right_guess, false);
                fill_map.put(left_guess_template.getValue(), right_guess);
                return;
            } else if (left_guess_template.is_class) {
                if (right_guess instanceof ALittleGuessClass) {
                    fill_map.put(left_guess_template.getValue(), right_guess);
                    return;
                } else if (right_guess instanceof ALittleGuessTemplate) {
                    ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                    if (right_guess_template.template_extends instanceof ALittleGuessClass || right_guess_template.is_class) {
                        fill_map.put(right_guess_template.getValue(), right_guess);
                        return;
                    }
                }
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            } else if (left_guess_template.is_struct) {
                if (right_guess instanceof ALittleGuessStruct) {
                    fill_map.put(left_guess_template.getValue(), right_guess);
                    return;
                } else if (right_guess instanceof ALittleGuessTemplate) {
                    ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                    if (right_guess_template.template_extends instanceof ALittleGuessStruct || right_guess_template.is_struct) {
                        fill_map.put(left_guess_template.getValue(), right_guess);
                        return;
                    }
                }
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            fill_map.put(left_guess_template.getValue(), right_guess);
            return;
        }

        throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
    }

    private ALittleGuessFunctor checkTemplateMap(@NotNull Map<String, ALittleGuessTemplate> src_map, @NotNull Map<String, ALittleGuess> fill_map) throws ALittleGuessException {
        ALittleGuessFunctor guess = null;
        ALittleGuess pre_type = guessPreType();
        if (pre_type == null) return guess;

        // 如果需要处理
        if (!(pre_type instanceof ALittleGuessFunctor)) return guess;
        ALittleGuessFunctor pre_type_functor = (ALittleGuessFunctor) pre_type;

        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (pre_type_functor.param_list.size() < value_stat_list.size() && pre_type_functor.param_tail == null)
            throw new ALittleGuessException(myElement, "函数调用最多需要" + pre_type_functor.param_list.size() + "个参数,不能是:" + value_stat_list.size() + "个");

        // 检查模板参数
        if (pre_type_functor.template_param_list.size() > 0) {
            for (ALittleGuessTemplate template_param : pre_type_functor.template_param_list) {
                src_map.put(template_param.getValue(), template_param);
            }

            ALittlePropertyValueMethodTemplate method_template = myElement.getPropertyValueMethodTemplate();
            if (method_template != null) {
                List<ALittleAllType> all_type_list = method_template.getAllTypeList();
                if (all_type_list.size() > pre_type_functor.template_param_list.size())
                    throw new ALittleGuessException(myElement, "函数调用最多需要" + pre_type_functor.template_param_list.size() + "个模板参数,不能是:" + all_type_list.size() + "个");

                for (int i = 0; i < all_type_list.size(); ++i) {
                    ALittleGuess all_type_guess = all_type_list.get(i).guessType();
                    ALittleReferenceOpUtil.guessTypeEqual(pre_type_functor.template_param_list.get(i), all_type_list.get(i), all_type_guess, false, false);
                    String key = pre_type_functor.template_param_list.get(i).getValue();
                    fill_map.put(key, all_type_guess);
                }
            }

            // 根据填充的参数来分析以及判断
            for (int i = 0; i < value_stat_list.size(); ++i) {
                ALittleValueStat value_stat = value_stat_list.get(i);
                ALittleGuess value_stat_guess = value_stat.guessType();
                // 如果参数返回的类型是tail，那么就可以不用检查
                if (value_stat_guess instanceof ALittleGuessReturnTail) continue;
                if (i >= pre_type_functor.param_list.size()) break;

                // 逐个分析，并且填充模板
                analysisTemplate(fill_map, pre_type_functor.param_list.get(i), value_stat, value_stat_guess, false);
            }

            // 判断如果还未有模板解析，就报错
            for (String key : src_map.keySet()) {
                if (!fill_map.containsKey(key))
                    throw new ALittleGuessException(myElement, key + "模板无法解析");
            }
        }

        guess = pre_type_functor;
        return guess;
    }

    public @NotNull
    List<ALittleGuess> generateTemplateParamList() throws ALittleGuessException {
        List<ALittleGuess> param_list = new ArrayList<>();

        Map<String, ALittleGuessTemplate> src_map = new HashMap<>();
        Map<String, ALittleGuess> fill_map = new HashMap<>();
        ALittleGuessFunctor pre_type_functor = checkTemplateMap(src_map, fill_map);
        if (pre_type_functor == null) return param_list;

        for (int i = 0; i < pre_type_functor.template_param_list.size(); ++i) {
            ALittleGuessTemplate guess_template = pre_type_functor.template_param_list.get(i);
            if (guess_template.template_extends != null || guess_template.is_class || guess_template.is_struct) {
                ALittleGuess value = fill_map.get(guess_template.getValue());
                if (value != null)
                    param_list.add(value);
            }
        }

        return param_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        Map<String, ALittleGuessTemplate> src_map = new HashMap<>();
        Map<String, ALittleGuess> fill_map = new HashMap<>();
        ALittleGuessFunctor pre_type_functor = checkTemplateMap(src_map, fill_map);
        if (pre_type_functor == null) throw new ALittleGuessException(myElement, "括号前面必须是函数");

        // 检查填写的和函数定义的参数是否一致
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        for (int i = 0; i < value_stat_list.size(); ++i) {
            ALittleValueStat value_stat = value_stat_list.get(i);

            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

            ALittleGuess guess = value_stat.guessType();
            // 如果参数返回的类型是tail，那么就可以不用检查
            if (guess instanceof ALittleGuessReturnTail) continue;

            if (i >= pre_type_functor.param_list.size()) {
                // 如果有参数占位符，那么就直接跳出，不检查了
                // 如果没有，就表示超过参数数量了
                if (pre_type_functor.param_tail != null)
                    break;
                else
                    throw new ALittleGuessException(myElement, "该函数调用需要" + pre_type_functor.param_list.size() + "个参数，而不是" + value_stat_list.size() + "个");
            }

            try {
                ALittleReferenceOpUtil.guessTypeEqual(pre_type_functor.param_list.get(i), value_stat, guess, false, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(value_stat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + error.getError());
            }
        }

        // 如果参数数量不足以填充
        if (value_stat_list.size() < pre_type_functor.param_list.size()) {
            // 不足的部分，参数必须都是nullable
            for (int i = value_stat_list.size(); i < pre_type_functor.param_nullable_list.size(); ++i) {
                if (!pre_type_functor.param_nullable_list.get(i)) {
                    // 计算至少需要的参数个数
                    int count = pre_type_functor.param_nullable_list.size();
                    for (int j = pre_type_functor.param_nullable_list.size() - 1; j >= 0; --j) {
                        if (pre_type_functor.param_nullable_list.get(j))
                            --count;
                        else
                            break;
                    }
                    throw new ALittleGuessException(myElement, "该函数调用至少需要" + count + "个参数，而不是" + value_stat_list.size() + "个");
                }
            }
        }

        // 检查这个函数是不是await
        if (pre_type_functor.await_modifier) {
            // 检查这次所在的函数必须要有await或者async修饰
            PsiHelper.checkInvokeAwait(myElement);
        }
    }

    @NotNull
    @Override
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        // 获取函数对象
        ALittleGuess preType = guessPreType();
        if (!(preType instanceof ALittleGuessFunctor)) return result;
        ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor) preType;

        // 构建对象
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        for (int i = 0; i < valueStatList.size(); ++i) {
            if (i >= preTypeFunctor.param_name_list.size()) break;
            String name = preTypeFunctor.param_name_list.get(i);
            // 参数占位符直接跳过
            if (name.equals("...")) continue;
            ALittleValueStat valueStat = valueStatList.get(i);
            String valueName = valueStat.getText();
            if (name.equals(valueName)) continue;
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
