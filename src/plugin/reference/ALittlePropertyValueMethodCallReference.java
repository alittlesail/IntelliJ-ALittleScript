package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;
import plugin.psi.impl.ALittleClassMethodDecImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                newPreTypeFunctor.functorTemplateParamList.addAll(preTypeFunctor.functorTemplateParamList);
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

        Map<String, ALittleGuessClassTemplate> srcMap = new HashMap<>();
        Map<String, ALittleGuess> fillMap = new HashMap<>();
        ALittleGuessFunctor preTypeFunctor = checkTemplateMap(srcMap, fillMap);
        if (preTypeFunctor == null) return guessList;

        for (ALittleGuess guess : preTypeFunctor.functorReturnList) {
            if (guess.NeedReplace()) {
                guessList.add(guess.ReplaceTemplate(fillMap));
            } else {
                guessList.add(guess);
            }
        }
        if (preTypeFunctor.functorReturnTail != null) {
            guessList.add(preTypeFunctor.functorReturnTail);
        }

        return guessList;
    }

    private void AnalysisTemplate(@NotNull Map<String, ALittleGuess> fillMap,
                                  @NotNull ALittleGuess leftGuess, @NotNull PsiElement rightSrc, @NotNull ALittleGuess rightGuess) throws ALittleGuessException {
        // 如果任何一方是any，那么就认为可以相等
        if (leftGuess.value.equals("any")) return;

        // 如果值等于null，那么可以赋值
        if (rightGuess.value.equals("null")) return;

        if (leftGuess instanceof ALittleGuessPrimitive
            || leftGuess instanceof ALittleGuessStruct) {
            ALittleReferenceOpUtil.guessTypeEqual(leftGuess, rightSrc, rightGuess);
            return;
        }

        if (leftGuess instanceof ALittleGuessMap) {
            if (!(rightGuess instanceof ALittleGuessMap)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            try {
                AnalysisTemplate(fillMap, ((ALittleGuessMap)leftGuess).keyType, rightSrc, ((ALittleGuessMap)rightGuess).keyType);
                AnalysisTemplate(fillMap, ((ALittleGuessMap)leftGuess).valueType, rightSrc, ((ALittleGuessMap)rightGuess).valueType);
            } catch (ALittleGuessException ignored) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessList) {
            if (!(rightGuess instanceof ALittleGuessList)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            try {
                AnalysisTemplate(fillMap, ((ALittleGuessList)leftGuess).subType, rightSrc, ((ALittleGuessList)rightGuess).subType);
            } catch (ALittleGuessException ignored) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessFunctor) {
            if (!(rightGuess instanceof ALittleGuessFunctor)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            ALittleGuessFunctor leftGuessFunctor = (ALittleGuessFunctor)leftGuess;
            ALittleGuessFunctor rightGuessFunctor = (ALittleGuessFunctor)rightGuess;

            if (leftGuessFunctor.functorParamList.size() != rightGuessFunctor.functorParamList.size()
                    || leftGuessFunctor.functorReturnList.size() != rightGuessFunctor.functorReturnList.size()
                    || leftGuessFunctor.functorTemplateParamList.size() != rightGuessFunctor.functorTemplateParamList.size()
                    || leftGuessFunctor.functorAwait != rightGuessFunctor.functorAwait
                    || leftGuessFunctor.functorProto == null && rightGuessFunctor.functorProto != null
                    || leftGuessFunctor.functorProto != null && rightGuessFunctor.functorProto == null
                    || (leftGuessFunctor.functorProto != null && !leftGuessFunctor.functorProto.equals(rightGuessFunctor.functorProto))
                    || leftGuessFunctor.functorParamTail == null && rightGuessFunctor.functorParamTail != null
                    || leftGuessFunctor.functorParamTail != null && rightGuessFunctor.functorParamTail == null
                    || leftGuessFunctor.functorReturnTail == null && rightGuessFunctor.functorReturnTail != null
                    || leftGuessFunctor.functorReturnTail != null && rightGuessFunctor.functorReturnTail == null
            ) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            for (int i = 0; i < leftGuessFunctor.functorTemplateParamList.size(); ++i) {
                AnalysisTemplate(fillMap, leftGuessFunctor.functorTemplateParamList.get(i), rightSrc, rightGuessFunctor.functorTemplateParamList.get(i));
            }

            for (int i = 0; i < leftGuessFunctor.functorParamList.size(); ++i) {
                AnalysisTemplate(fillMap, leftGuessFunctor.functorParamList.get(i), rightSrc, rightGuessFunctor.functorParamList.get(i));
            }

            for (int i = 0; i < leftGuessFunctor.functorReturnList.size(); ++i) {
                AnalysisTemplate(fillMap, leftGuessFunctor.functorReturnList.get(i), rightSrc, rightGuessFunctor.functorReturnList.get(i));
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessClass) {
            if (rightGuess instanceof ALittleGuessClassTemplate) {
                rightGuess = ((ALittleGuessClassTemplate)rightGuess).templateExtends;
            }
            if (!(rightGuess instanceof ALittleGuessClass)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            if (leftGuess.value.equals(rightGuess.value)) return;

            if (ALittleReferenceUtil.IsClassSuper(((ALittleGuessClass)leftGuess).element, rightGuess.value)) return;
            if (ALittleReferenceUtil.IsClassSuper(((ALittleGuessClass)rightGuess).element, leftGuess.value)) return;

            throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
        }

        if (leftGuess instanceof ALittleGuessClassTemplate) {
            ALittleGuessClassTemplate leftGuessClassTemplate = (ALittleGuessClassTemplate)leftGuess;

            // 查看模板是否已经被填充，那么就按填充的检查
            ALittleGuess fillGuess = fillMap.get(leftGuessClassTemplate.value);
            if (fillGuess != null) {
                ALittleReferenceOpUtil.guessTypeEqual(fillGuess, rightSrc, rightGuess);
                return;
            }

            // 处理还未填充
            if (leftGuessClassTemplate.templateExtends != null) {
                AnalysisTemplate(fillMap, leftGuessClassTemplate.templateExtends, rightSrc, rightGuess);
                fillMap.put(leftGuessClassTemplate.value, rightGuess);
                return;
            } else if (leftGuessClassTemplate.isClass) {
                if (rightGuess instanceof ALittleGuessClass) {
                    fillMap.put(leftGuessClassTemplate.value, rightGuess);
                    return;
                } else if (rightGuess instanceof ALittleGuessClassTemplate) {
                    ALittleGuessClassTemplate rightGuessClassTemplate = (ALittleGuessClassTemplate)rightGuess;
                    if (rightGuessClassTemplate.templateExtends != null || rightGuessClassTemplate.isClass) {
                        fillMap.put(leftGuessClassTemplate.value, rightGuess);
                        return;
                    }
                }
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            } else if (leftGuessClassTemplate.isStruct) {
                if (rightGuess instanceof ALittleGuessStruct) {
                    fillMap.put(leftGuessClassTemplate.value, rightGuess);
                    return;
                } else if (rightGuess instanceof ALittleGuessClassTemplate) {
                    ALittleGuessClassTemplate rightGuessClassTemplate = (ALittleGuessClassTemplate)rightGuess;
                    if (rightGuessClassTemplate.isStruct) {
                        fillMap.put(leftGuessClassTemplate.value, rightGuess);
                        return;
                    }
                }
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            fillMap.put(leftGuessClassTemplate.value, rightGuess);
            return;
        }

        throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
    }

    private ALittleGuessFunctor checkTemplateMap(@NotNull Map<String, ALittleGuessClassTemplate> srcMap, @NotNull Map<String, ALittleGuess> fillMap) throws ALittleGuessException {
        ALittleGuess preType = guessPreType();
        if (preType == null) {
            return null;
        }

        // 如果需要处理
        if (!(preType instanceof ALittleGuessFunctor)) return null;
        ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;

        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (preTypeFunctor.functorParamList.size() < valueStatList.size() && preTypeFunctor.functorParamTail == null) {
            throw new ALittleGuessException(myElement, "函数调用最多需要" + preTypeFunctor.functorParamList.size() + "个参数,不能是:" + valueStatList.size() + "个");
        }

        // 检查模板参数
        if (!preTypeFunctor.functorTemplateParamList.isEmpty()) {
            for (ALittleGuessClassTemplate guess : preTypeFunctor.functorTemplateParamList) {
                srcMap.put(guess.value, guess);
            }
            ALittlePropertyValueMethodTemplate methodTemplate = myElement.getPropertyValueMethodTemplate();
            if (methodTemplate != null) {
                List<ALittleAllType> allTypeList = methodTemplate.getAllTypeList();
                if (allTypeList.size() > preTypeFunctor.functorTemplateParamList.size()) {
                    throw new ALittleGuessException(myElement, "函数调用最多需要" + preTypeFunctor.functorTemplateParamList.size() + "个模板参数,不能是:" + allTypeList.size() + "个");
                }
                for (int i = 0; i < allTypeList.size(); ++i) {
                    ALittleGuess allTypeGuess = allTypeList.get(i).guessType();
                    ALittleReferenceOpUtil.guessTypeEqual(preTypeFunctor.functorTemplateParamList.get(i), allTypeList.get(i), allTypeGuess);
                    fillMap.put(preTypeFunctor.functorTemplateParamList.get(i).value, allTypeGuess);
                }
            }

            // 根据填充的参数来分析以及判断
            for (int i = 0; i < valueStatList.size(); ++i) {
                ALittleValueStat valueStat = valueStatList.get(i);
                ALittleGuess guess = valueStat.guessType();
                // 如果参数返回的类型是tail，那么就可以不用检查
                if (guess instanceof ALittleGuessReturnTail) continue;
                if (i >= preTypeFunctor.functorParamList.size()) break;

                // 逐个分析，并且填充模板
                AnalysisTemplate(fillMap, preTypeFunctor.functorParamList.get(i), valueStat, guess);
            }

            // 判断如果还未有模板解析，就报错
            for (String key : srcMap.keySet()) {
                if (fillMap.get(key) == null) {
                    throw new ALittleGuessException(myElement, key + "模板无法解析");
                }
            }
        }

        return preTypeFunctor;
    }

    public @NotNull List<ALittleGuess> generateTemplateParamList() throws ALittleGuessException {
        List<ALittleGuess> paramList = new ArrayList<>();

        Map<String, ALittleGuessClassTemplate> srcMap = new HashMap<>();
        Map<String, ALittleGuess> fillMap = new HashMap<>();
        ALittleGuessFunctor preTypeFunctor = checkTemplateMap(srcMap, fillMap);
        if (preTypeFunctor == null) return paramList;

        for (int i = 0; i < preTypeFunctor.functorTemplateParamList.size(); ++i) {
            ALittleGuessClassTemplate guessClassTemplate = preTypeFunctor.functorTemplateParamList.get(i);
            if (guessClassTemplate.templateExtends != null || guessClassTemplate.isClass || guessClassTemplate.isStruct) {
                paramList.add(fillMap.get(guessClassTemplate.value));
            }
        }

        return paramList;
    }

    public void checkError() throws ALittleGuessException {
        Map<String, ALittleGuessClassTemplate> srcMap = new HashMap<>();
        Map<String, ALittleGuess> fillMap = new HashMap<>();
        ALittleGuessFunctor preTypeFunctor = checkTemplateMap(srcMap, fillMap);
        if (preTypeFunctor == null) return;

        // 如果没有模板参数，那么就直接检查
        if (preTypeFunctor.functorTemplateParamList.isEmpty()) {
            List<ALittleValueStat> valueStatList = myElement.getValueStatList();
            for (int i = 0; i < valueStatList.size(); ++i) {
                ALittleValueStat valueStat = valueStatList.get(i);
                ALittleGuess guess = valueStat.guessType();
                // 如果参数返回的类型是tail，那么就可以不用检查
                if (guess instanceof ALittleGuessReturnTail) continue;
                if (i >= preTypeFunctor.functorParamList.size()) break;
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(preTypeFunctor.functorParamList.get(i), valueStat, guess);
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(valueStat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + e.getError());
                }
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
