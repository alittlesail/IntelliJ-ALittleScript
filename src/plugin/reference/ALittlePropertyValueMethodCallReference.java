package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.psi.impl.ALittleClassMethodDecImpl;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueMethodCallReference extends ALittleReference<ALittlePropertyValueMethodCall> {
    public ALittlePropertyValueMethodCallReference(@NotNull ALittlePropertyValueMethodCall element, TextRange textRange) {
        super(element, textRange);
    }

    public ALittleReferenceUtil.GuessTypeInfo guessPreType() throws ALittleReferenceUtil.ALittleReferenceException {
        // 获取父节点
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
        ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffixList.indexOf(propertyValueSuffix);
        if (index == -1) return null;

        // 获取前一个类型
        ALittleReferenceUtil.GuessTypeInfo preType;
        ALittleReferenceUtil.GuessTypeInfo prePreType = null;
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
        if (preType.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            // 如果再往前一个是一个Class实例对象，那么就要去掉第一个参数
            if (prePreType != null && prePreType.type == ALittleReferenceUtil.GuessType.GT_CLASS && !preType.functorParamList.isEmpty()
                    && (preType.element instanceof ALittleClassMethodDec || preType.element instanceof ALittleClassGetterDec || preType.element instanceof ALittleClassSetterDec)) {
                ALittleReferenceUtil.GuessTypeInfo newPreType = new ALittleReferenceUtil.GuessTypeInfo();
                newPreType.type = preType.type;
                newPreType.element = preType.element;
                newPreType.functorAwait = preType.functorAwait;
                newPreType.functorParamList = new ArrayList<>();
                newPreType.functorParamList.addAll(preType.functorParamList);
                newPreType.functorParamNameList = new ArrayList<>();
                newPreType.functorParamNameList.addAll(preType.functorParamNameList);
                newPreType.functorReturnList = new ArrayList<>();
                newPreType.functorReturnList.addAll(preType.functorReturnList);
                preType = newPreType;

                preType.functorParamList.remove(0);
                preType.functorParamNameList.remove(0);
                preType.value = "Functor<(";
                if (preType.functorAwait) {
                    preType.value = "Functor<await(";
                }
                List<String> paramList = new ArrayList<>();
                for (ALittleReferenceUtil.GuessTypeInfo guessTypeInfo : preType.functorParamList) {
                    paramList.add(guessTypeInfo.value);
                }
                preType.value += String.join(",", paramList);
                preType.value += ")";
                List<String> returnList = new ArrayList<>();
                for (ALittleReferenceUtil.GuessTypeInfo guessTypeInfo : preType.functorReturnList) {
                    returnList.add(guessTypeInfo.value);
                }
                String returnString = String.join(",", returnList);
                if (!returnString.isEmpty()) preType.value += ":";
                preType.value += returnString;
                preType.value += ">";
            }
        }

        return preType;
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        ALittleReferenceUtil.GuessTypeInfo preType = guessPreType();
        if (preType == null) {
            return guessList;
        }

        if (preType.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            guessList.addAll(preType.functorReturnList);
        }

        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleReferenceUtil.GuessTypeInfo preType = guessPreType();
        if (preType == null) {
            return;
        }

        // 如果需要处理
        if (preType.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            List<ALittleValueStat> valueStatList = myElement.getValueStatList();
            if (preType.functorParamList.size() < valueStatList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "函数调用最多需要" + preType.functorParamList.size() + "个参数,不能是:" + valueStatList.size() + "个");
            }

            for (int i = 0; i < valueStatList.size(); ++i) {
                ALittleValueStat valueStat = valueStatList.get(i);
                ALittleReferenceUtil.GuessTypeInfo guessTypeInfo = valueStat.guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(myElement, preType.functorParamList.get(i), valueStat, guessTypeInfo);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + e.getError());
                }
            }

            // 检查这个函数是不是await
            if (preType.functorAwait) {
                // 检查这次所在的函数必须要有await或者async修饰
                PsiElement parent = myElement;
                while (parent != null) {
                    if (parent instanceof ALittleClassCtorDec) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "构造函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassGetterDec) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "getter函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassSetterDec) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "setter函数内不能调用带有await的函数");
                    } else if (parent instanceof ALittleClassMethodDec) {
                        if (((ALittleClassMethodDec)parent).getCoModifier() == null) {
                            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    } else if (parent instanceof ALittleClassStaticDec) {
                        if (((ALittleClassStaticDec)parent).getCoModifier() == null) {
                            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    } else if (parent instanceof ALittleGlobalMethodDec) {
                        if (((ALittleGlobalMethodDec)parent).getCoModifier() == null) {
                            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "所在函数没有async或者await修饰");
                        }
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleReferenceUtil.ALittleReferenceException {
        List<InlayInfo> result = new ArrayList<>();
        // 获取函数对象
        ALittleReferenceUtil.GuessTypeInfo preType = guessPreType();
        if (preType.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) return result;

        // 构建对象
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        for (int i = 0; i < valueStatList.size(); ++i) {
            if (i >= preType.functorParamNameList.size()) break;
            String name = preType.functorParamNameList.get(i);
            ALittleValueStat valueStat = valueStatList.get(i);
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
