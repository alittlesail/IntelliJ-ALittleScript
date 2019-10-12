package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;
import plugin.psi.impl.ALittleClassMethodDecImpl;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueMethodCallReference extends ALittleReference<ALittlePropertyValueMethodCall> {
    public ALittlePropertyValueMethodCallReference(@NotNull ALittlePropertyValueMethodCall element, TextRange textRange) {
        super(element, textRange);
    }

    public ALittleGuess guessPreType() throws ALittleGuessException {
        // 获取父节点
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
        ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffixList.indexOf(propertyValueSuffix);
        if (index == -1) return null;

        // 获取前一个类型
        ALittleGuess preType;
        ALittleGuess prePreType = null;
        if (index == 0) {
            preType = propertyValueFirstType.guessType();
        } else if (index == 1) {
            preType = suffixList.get(index - 1).guessType();
            prePreType = propertyValueFirstType.guessType();
        } else {
            preType = suffixList.get(index - 1).guessType();
            prePreType = suffixList.get(index - 2).guessType();
        }

        // 如果是Functor
        if (preType instanceof ALittleGuessFunctor) {
            ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;
            if (prePreType instanceof ALittleGuessClassTemplate) {
                prePreType = ((ALittleGuessClassTemplate)prePreType).templateExtends;
            }
            // 如果再往前一个是一个Class实例对象，那么就要去掉第一个参数
            if (prePreType instanceof ALittleGuessClass && !preTypeFunctor.functorParamList.isEmpty()
                    && (preTypeFunctor.element instanceof ALittleClassMethodDec
                        || preTypeFunctor.element instanceof ALittleClassGetterDec
                        || preTypeFunctor.element instanceof ALittleClassSetterDec)) {
                ALittleGuessFunctor newPreTypeFunctor = new ALittleGuessFunctor(preTypeFunctor.element);
                preType = newPreTypeFunctor;

                newPreTypeFunctor.functorAwait = preTypeFunctor.functorAwait;
                newPreTypeFunctor.functorProto = preTypeFunctor.functorProto;
                newPreTypeFunctor.functorParamList.addAll(preTypeFunctor.functorParamList);
                newPreTypeFunctor.functorParamNameList.addAll(preTypeFunctor.functorParamNameList);
                newPreTypeFunctor.functorParamTail = preTypeFunctor.functorParamTail;
                newPreTypeFunctor.functorReturnList.addAll(preTypeFunctor.functorReturnList);
                newPreTypeFunctor.functorReturnTail = preTypeFunctor.functorReturnTail;

                // 移除掉第一个参数
                newPreTypeFunctor.functorParamList.remove(0);
                newPreTypeFunctor.functorParamNameList.remove(0);

                newPreTypeFunctor.UpdateValue();
            }
        }

        return preType;
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        ALittleGuess preType = guessPreType();
        if (preType == null) {
            return guessList;
        }

        if (preType instanceof ALittleGuessFunctor) {
            ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;
            guessList.addAll(preTypeFunctor.functorReturnList);
            if (preTypeFunctor.functorReturnTail != null) {
                guessList.add(preTypeFunctor.functorReturnTail);
            }
        }

        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        ALittleGuess preType = guessPreType();
        if (preType == null) {
            return;
        }

        // 如果需要处理
        if (preType instanceof ALittleGuessFunctor) {
            ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;

            List<ALittleValueStat> valueStatList = myElement.getValueStatList();
            if (preTypeFunctor.functorParamList.size() < valueStatList.size() && preTypeFunctor.functorParamTail == null) {
                throw new ALittleGuessException(myElement, "函数调用最多需要" + preTypeFunctor.functorParamList.size() + "个参数,不能是:" + valueStatList.size() + "个");
            }

            for (int i = 0; i < valueStatList.size(); ++i) {
                ALittleValueStat valueStat = valueStatList.get(i);
                ALittleGuess guess = valueStat.guessType();
                // 如果参数返回的类型是tail，那么就可以不用检查
                if (guess instanceof ALittleGuessReturnTail) continue;
                if (i >= preTypeFunctor.functorParamList.size()) break;
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(myElement, preTypeFunctor.functorParamList.get(i), valueStat, guess);
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(valueStat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + e.getError());
                }
            }

            // 检查这个函数是不是await
            if (preTypeFunctor.functorAwait) {
                // 检查这次所在的函数必须要有await或者async修饰
                PsiElement parent = myElement;
                while (parent != null) {
                    if (parent instanceof ALittleNamespaceDec) {
                        throw new ALittleGuessException(myElement, "全局表达式不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassCtorDec) {
                        throw new ALittleGuessException(myElement, "构造函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassGetterDec) {
                        throw new ALittleGuessException(myElement, "getter函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassSetterDec) {
                        throw new ALittleGuessException(myElement, "setter函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassMethodDec) {
                        if (((ALittleClassMethodDec)parent).getCoModifier() == null) {
                            throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    } else if (parent instanceof ALittleClassStaticDec) {
                        if (((ALittleClassStaticDec)parent).getCoModifier() == null) {
                            throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    } else if (parent instanceof ALittleGlobalMethodDec) {
                        if (((ALittleGlobalMethodDec)parent).getCoModifier() == null) {
                            throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        // 获取函数对象
        ALittleGuess preType = guessPreType();
        if (!(preType instanceof ALittleGuessFunctor)) return result;
        ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;

        // 构建对象
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        for (int i = 0; i < valueStatList.size(); ++i) {
            if (i >= preTypeFunctor.functorParamNameList.size()) break;
            String name = preTypeFunctor.functorParamNameList.get(i);
            ALittleValueStat valueStat = valueStatList.get(i);
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
