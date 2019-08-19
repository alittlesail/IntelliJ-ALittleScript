package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpAssignExprReference extends ALittleReference<ALittleOpAssignExpr> {
    public ALittleOpAssignExprReference(@NotNull ALittleOpAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        List<ALittlePropertyValue> propertyValueList = myElement.getPropertyValueList();
        if (propertyValueList.isEmpty()) {
            return;
        }

        // 如果返回值只有一个函数调用
        if (propertyValueList.size() > 1) {
            // 获取右边表达式的
            List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = value_stat.guessTypes();
            if (methodCallGuessList.isEmpty()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数没有返回值");
            }
            if (methodCallGuessList.size() < propertyValueList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数返回值数量少于定义的变量数量");
            }

            ALittleReferenceUtil.GuessTypeInfo guessType = methodCallGuessList.get(0);
            // 检查这个函数是不是await
            if (guessType.callAwait) {
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

            for (int i = 0; i < propertyValueList.size(); ++i) {
                ALittlePropertyValue pairDec = propertyValueList.get(i);
                try {
                    boolean result = ALittleReferenceOpUtil.guessTypeEqual(pairDec, pairDec.guessType(), value_stat, methodCallGuessList.get(i));
                    if (!result) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(pairDec, "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等");
                    }
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + e.getError());
                }
            }

            return;
        }

        ALittleReferenceUtil.GuessTypeInfo pairGuessType = propertyValueList.get(0).guessType();
        ALittleReferenceUtil.GuessTypeInfo valueGuessType = value_stat.guessType();

        try {
            boolean result = ALittleReferenceOpUtil.guessTypeEqual(propertyValueList.get(0), pairGuessType, value_stat, valueGuessType);
            if (!result) {
                throw new ALittleReferenceUtil.ALittleReferenceException(propertyValueList.get(0), "等号左边的变量和表达式的类型不同");
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的变量和表达式的类型不同:" + e.getError());
        }
    }
}
