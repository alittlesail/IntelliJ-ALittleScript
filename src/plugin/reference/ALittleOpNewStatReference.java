package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewStatReference extends ALittleReference<ALittleOpNewStat> {
    public ALittleOpNewStatReference(@NotNull ALittleOpNewStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getCustomType() != null) {
            return myElement.getCustomType().guessTypes();
        } else if (myElement.getGenericType() != null) {
            return myElement.getGenericType().guessTypes();
        }
        throw new ALittleGuessException(myElement, "ALittleOpNewStat出现未知的子节点");
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();

        if (myElement.getGenericType() != null) {
            if (value_stat_list.size() > 0)
                throw new ALittleGuessException(myElement, "创建容器实例对象不能有参数");
            if (myElement.getGenericType().getGenericFunctorType() != null)
                throw new ALittleGuessException(myElement, "Functor不能new");
            return;
        }

        if (myElement.getCustomType() != null) {
            ALittleCustomType custom_type = myElement.getCustomType();
            ALittleGuess guess = custom_type.guessType();
            if (guess instanceof ALittleGuessStruct) {
                if (value_stat_list.size() > 0)
                    throw new ALittleGuessException(myElement, "new的结构体不能有参数");
                return;
            }

            if (guess instanceof ALittleGuessMap) {
                if (value_stat_list.size() > 0)
                    throw new ALittleGuessException(myElement, "new的Map不能有参数");
                return;
            }

            if (guess instanceof ALittleGuessList) {
                if (value_stat_list.size() > 0)
                    throw new ALittleGuessException(myElement, "new的List不能有参数");
                return;
            }

            if (guess instanceof ALittleGuessTemplate) {
                ALittleGuessTemplate guess_template = (ALittleGuessTemplate) guess;
                if (guess_template.template_extends != null)
                    guess = guess_template.template_extends;
                else if (guess_template.is_struct) {
                    if (value_stat_list.size() > 0) throw new ALittleGuessException(myElement, "new的结构体不能有参数");
                    return;
                } else if (guess_template.is_class) {
                    throw new ALittleGuessException(myElement, "如果要new改模板类型，请不要使用class，无法确定它的构造函数参数");
                }
            }

            if (guess instanceof ALittleGuessStruct) {
                if (value_stat_list.size() > 0) throw new ALittleGuessException(myElement, "new的结构体不能有参数");
                return;
            }

            if (guess instanceof ALittleGuessClass) {
                ALittleClassDec class_dec = ((ALittleGuessClass) guess).class_dec;
                ALittleClassCtorDec ctor_dec = PsiHelper.findFirstCtorDecFromExtends(class_dec, 100);
                if (ctor_dec == null) {
                    if (value_stat_list.size() > 0)
                        throw new ALittleGuessException(myElement, "new的类的构造函数没有参数");
                    return;
                }

                ALittleMethodParamDec param_dec = ctor_dec.getMethodParamDec();
                if (param_dec == null) {
                    if (value_stat_list.size() > 0)
                        throw new ALittleGuessException(myElement, "new的类的构造函数没有参数");
                    return;
                }

                List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                List<ALittleGuess> param_guess_list = new ArrayList<>();
                List<Boolean> param_nullable_list = new ArrayList<>();
                boolean has_param_tail = false;
                for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                    ALittleAllType all_type = param_one_dec.getAllType();
                    ALittleMethodParamTailDec param_tail = param_one_dec.getMethodParamTailDec();
                    if (all_type != null) {
                        ALittleGuess all_type_guess = all_type.guessType();
                        param_guess_list.add(all_type_guess);
                        param_nullable_list.add(PsiHelper.isNullable(param_one_dec.getModifierList()));
                    } else if (param_tail != null) {
                        has_param_tail = true;
                    }
                }

                // 如果参数数量不足以填充
                if (value_stat_list.size() < param_guess_list.size()) {
                    // 不足的部分，参数必须都是nullable
                    for (int i = value_stat_list.size(); i < param_nullable_list.size(); ++i) {
                        if (!param_nullable_list.get(i)) {
                            // 计算至少需要的参数个数
                            int count = param_nullable_list.size();
                            for (int j = param_nullable_list.size() - 1; j >= 0; --j) {
                                if (param_nullable_list.get(j))
                                    --count;
                                else
                                    break;
                            }
                            throw new ALittleGuessException(myElement, "new的类的构造函数调用需要" + count + "个参数,不能是:" + value_stat_list.size() + "个");
                        }
                    }
                }

                for (int i = 0; i < value_stat_list.size(); ++i) {
                    ALittleValueStat value_stat = value_stat_list.get(i);

                    Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
                    if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

                    ALittleGuess value_stat_guess = value_stat.guessType();
                    // 如果参数返回的类型是tail，那么就可以不用检查
                    if (value_stat_guess instanceof ALittleGuessReturnTail) continue;

                    if (i >= param_guess_list.size()) {
                        // 如果有参数占位符，那么就直接跳出，不检查了
                        // 如果没有，就表示超过参数数量了
                        if (has_param_tail)
                            break;
                        else
                            throw new ALittleGuessException(myElement, "该构造函数调用需要" + param_guess_list.size() + "个参数，而不是" + value_stat_list.size() + "个");
                    }

                    try {
                        ALittleReferenceOpUtil.guessTypeEqual(param_guess_list.get(i), value_stat, value_stat_guess, false, false);
                    } catch (ALittleGuessException error) {
                        throw new ALittleGuessException(value_stat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + error.getError());
                    }
                }
                return;
            }

            throw new ALittleGuessException(myElement, "只能new结构体和类");
        }
    }

    @NotNull
    @Override
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();

        List<ALittleValueStat> valueStatList = myElement.getValueStatList();

        ALittleCustomType customType = myElement.getCustomType();
        if (customType == null) return result;

        ALittleGuess guess = customType.guessType();

        if (guess instanceof ALittleGuessClassTemplate) {
            ALittleGuessClassTemplate guessClassTemplate = (ALittleGuessClassTemplate) guess;
            if (guessClassTemplate.template_extends != null) {
                guess = guessClassTemplate.template_extends;
            }
        }

        if (guess instanceof ALittleGuessClass) {
            ALittleClassDec classDec = (ALittleClassDec) guess.getElement();
            ALittleClassBodyDec bodyDec = classDec.getClassBodyDec();
            if (bodyDec == null) return result;
            ALittleClassCtorDec ctorDec = null;
            for (ALittleClassElementDec elementDec : classDec.getClassBodyDec().getClassElementDecList()) {
                if (elementDec.getClassCtorDec() != null) {
                    ctorDec = elementDec.getClassCtorDec();
                    break;
                }
            }
            if (ctorDec == null) {
                return result;
            }

            ALittleMethodParamDec paramDec = ctorDec.getMethodParamDec();
            if (paramDec == null) {
                return result;
            }

            List<ALittleMethodParamOneDec> paramOneDecList = paramDec.getMethodParamOneDecList();
            if (paramOneDecList.isEmpty()) {
                return result;
            }

            for (int i = 0; i < paramOneDecList.size(); ++i) {
                ALittleMethodParamOneDec paramOneDec = paramOneDecList.get(i);
                if (paramOneDec.getMethodParamNameDec() == null) {
                    return result;
                }
                if (i >= valueStatList.size()) {
                    return result;
                }
                String name = paramOneDec.getMethodParamNameDec().getText();
                // 参数占位符直接跳过
                if (name.equals("...")) continue;
                ALittleValueStat valueStat = valueStatList.get(i);
                String valueName = valueStat.getText();
                if (name.equals(valueName)) continue;
                result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
            }
        }
        return result;
    }
}
