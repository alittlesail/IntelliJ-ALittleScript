package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessParamTail;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReturnExprReference extends ALittleReference<ALittleReturnExpr> {
    public ALittleReturnExprReference(@NotNull ALittleReturnExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getReturnYield() != null) {
            // 对于ReturnYield就不需要做返回值检查
            // 对所在函数进行检查，必须要有async和await表示
            // 获取对应的函数对象

            PsiElement element = null;

            PsiElement parent = myElement;
            while (parent != null) {
                if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec methodDec = (ALittleClassMethodDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec methodDec = (ALittleClassStaticDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec methodDec = (ALittleGlobalMethodDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                }

                parent = parent.getParent();
            }

            if (element != null) {
                throw new ALittleGuessException(element, "函数内部使用了return yield表达式，所以必须使用async或await修饰");
            }
            return;
        }
        
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        List<ALittleAllType> returnTypeList = new ArrayList<>();
        ALittleMethodReturnTailDec returnTailDec = null;

        // 获取对应的函数对象
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleClassGetterDec) {
                ALittleClassGetterDec getterDec = (ALittleClassGetterDec) parent;
                returnTypeList = new ArrayList<>();
                ALittleAllType returnTypeDec = getterDec.getAllType();
                if (returnTypeDec != null)
                    returnTypeList.add(returnTypeDec);
                break;
            } else if (parent instanceof ALittleClassMethodDec) {
                ALittleClassMethodDec methodDec = (ALittleClassMethodDec) parent;
                ALittleMethodReturnDec returnDec = methodDec.getMethodReturnDec();
                if (returnDec != null) {
                    returnTypeList = returnDec.getAllTypeList();
                    returnTailDec = returnDec.getMethodReturnTailDec();
                }
                break;
            } else if (parent instanceof ALittleClassStaticDec) {
                ALittleClassStaticDec methodDec = (ALittleClassStaticDec) parent;
                ALittleMethodReturnDec returnDec = methodDec.getMethodReturnDec();
                if (returnDec != null) {
                    returnTypeList = returnDec.getAllTypeList();
                    returnTailDec = returnDec.getMethodReturnTailDec();
                }
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec methodDec = (ALittleGlobalMethodDec) parent;
                ALittleMethodReturnDec returnDec = methodDec.getMethodReturnDec();
                if (returnDec != null) {
                    returnTypeList = returnDec.getAllTypeList();
                    returnTailDec = returnDec.getMethodReturnTailDec();
                }
                break;
            }

            parent = parent.getParent();
        }

        // 参数的类型
        List<ALittleGuess> guessTypeList = null;
        // 如果返回值只有一个函数调用
        if (valueStatList.size() == 1 && returnTypeList.size() > 1) {
            ALittleValueStat valueStat = valueStatList.get(0);
            guessTypeList = valueStat.guessTypes();
            boolean hasValueTail = !guessTypeList.isEmpty()
                    && guessTypeList.get(guessTypeList.size() - 1) instanceof ALittleGuessReturnTail;

            if (returnTailDec == null) {
                if (hasValueTail) {
                    if (guessTypeList.size() < returnTypeList.size() - 1) {
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量超过函数定义的返回值数量");
                    }
                } else {
                    if (guessTypeList.size() != returnTypeList.size()) {
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量和函数定义的返回值数量不相等");
                    }
                }
            } else {
                if (hasValueTail) {
                    // 不用检查
                } else {
                    if (guessTypeList.size() < returnTypeList.size()) {
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量少于函数定义的返回值数量");
                    }
                }
            }
        } else {
            if (returnTailDec == null) {
                if (valueStatList.size() != returnTypeList.size()) {
                    throw new ALittleGuessException(myElement, "return的返回值数量和函数定义的返回值数量不相等");
                }
            } else {
                if (valueStatList.size() < returnTypeList.size()) {
                    throw new ALittleGuessException(myElement, "return的返回值数量少于函数定义的返回值数量");
                }
            }
            guessTypeList = new ArrayList<>();
            for (ALittleValueStat valueStat : valueStatList) {
                ALittleGuess guess = valueStat.guessType();
                if (guess instanceof ALittleGuessParamTail) {
                    throw new ALittleGuessException(valueStat, "return表达式不能返回\"...\"");
                }
                guessTypeList.add(valueStat.guessType());
            }
        }

        // 每个类型依次检查
        for (int i = 0; i < guessTypeList.size(); ++i) {
            ALittleValueStat targetValueStat;
            if (i < valueStatList.size()) {
                targetValueStat = valueStatList.get(i);
            } else {
                targetValueStat = valueStatList.get(0);
            }
            if (guessTypeList.get(i) instanceof ALittleGuessReturnTail) break;
            if (i >= returnTypeList.size()) break;
            ALittleGuess returnTypeGuess = returnTypeList.get(i).guessType();
            if (returnTypeGuess instanceof ALittleGuessReturnTail) break;
            try {
                ALittleReferenceOpUtil.guessTypeEqual(returnTypeGuess, targetValueStat, guessTypeList.get(i));
            } catch (ALittleGuessException e) {
                throw new ALittleGuessException(targetValueStat, "return的第" + (i + 1) + "个返回值数量和函数定义的返回值类型不同:" + e.getError());
            }
        }
    }
}
