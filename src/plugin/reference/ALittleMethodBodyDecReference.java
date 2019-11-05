package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodBodyDecReference extends ALittleReference<ALittleMethodBodyDec> {
    public ALittleMethodBodyDecReference(@NotNull ALittleMethodBodyDec element, TextRange textRange) {
        super(element, textRange);
    }

    // 检查表达式是否有return
    public static boolean checkAllExpr(@NotNull List<ALittleGuess> returnList, @NotNull ALittleAllExpr allExpr) throws ALittleGuessException {
        if (allExpr.getIfExpr() != null) {
            ALittleIfExpr ifExpr = allExpr.getIfExpr();
            if (!checkAllExprList(returnList, ifExpr.getAllExprList())) {
                return false;
            }
            List<ALittleElseIfExpr> elseIfExprList = ifExpr.getElseIfExprList();
            for (ALittleElseIfExpr elseIfExpr : elseIfExprList) {
                if (!checkAllExprList(returnList, elseIfExpr.getAllExprList())) {
                    return false;
                }
            }
            ALittleElseExpr elseExpr = ifExpr.getElseExpr();
            if (elseExpr == null) {
                return false;
            }

            return checkAllExprList(returnList, elseExpr.getAllExprList());
        }

        if (allExpr.getForExpr() != null) {
            ALittleForExpr forExpr = allExpr.getForExpr();
            return checkAllExprList(returnList, forExpr.getAllExprList());
        }

        if (allExpr.getWhileExpr() != null) {
            ALittleWhileExpr whileExpr = allExpr.getWhileExpr();
            return checkAllExprList(returnList, whileExpr.getAllExprList());
        }

        if (allExpr.getDoWhileExpr() != null) {
            ALittleDoWhileExpr doWhileExpr = allExpr.getDoWhileExpr();
            return checkAllExprList(returnList, doWhileExpr.getAllExprList());
        }

        if (allExpr.getReturnExpr() != null) {
            // 这里检查return
            return true;
        }

        if (allExpr.getWrapExpr() != null) {
            ALittleWrapExpr wrapExpr = allExpr.getWrapExpr();
            return checkAllExprList(returnList, wrapExpr.getAllExprList());
        }

        if (allExpr.getThrowExpr() != null) {
            return true;
        }

        return false;
    }

    // 检查表达式是否有return
    public static boolean checkAllExprList(@NotNull List<ALittleGuess> returnList, @NotNull List<ALittleAllExpr> allExprList) throws ALittleGuessException {
        // 如果没有就检查子表达式
        int index = -1;
        for (int i = 0; i < allExprList.size(); ++i) {
            if (checkAllExpr(returnList, allExprList.get(i))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        } else if (index + 1 < allExprList.size()) {
            throw new ALittleGuessException(allExprList.get(index + 1), "当前分支内，从这里开始之后所有语句永远都不会执行到");
        }

        return true;
    }

    // 检查函数体
    public static void checkMethodBody(@NotNull List<ALittleGuess> returnList
            , @NotNull ALittleMethodNameDec methodNameDec
            , @NotNull ALittleMethodBodyDec methodBodyDec) throws ALittleGuessException {
        // 检查return
        if (!returnList.isEmpty() && !PsiHelper.isRegister(methodNameDec)) {
            List<ALittleAllExpr> allExprList = methodBodyDec.getAllExprList();
            if (!checkAllExprList(returnList, allExprList)) {
                throw new ALittleGuessException(methodNameDec, "不是所有分支都有return");
            }
        }
    }

    public void checkError() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleClassCtorDec) {
            return;
        }
        if (parent instanceof ALittleClassSetterDec) {
            return;
        }

        if (parent instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec getterDec = (ALittleClassGetterDec)parent;
            ALittleMethodNameDec nameDec = getterDec.getMethodNameDec();
            if (nameDec == null) {
                return;
            }
            List<ALittleGuess> returnList = new ArrayList<>();
            ALittleAllType allType = getterDec.getAllType();
            if (allType == null) {
                return;
            }
            returnList.add(allType.guessType());
            checkMethodBody(returnList, nameDec, myElement);
            return;
        }

        if (parent instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec methodDec = (ALittleClassMethodDec)parent;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) {
                return;
            }
            List<ALittleGuess> returnList = new ArrayList<>();
            ALittleMethodReturnDec returnDec = methodDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    returnList.add(allType.guessType());
                }
            }
            checkMethodBody(returnList, nameDec, myElement);
            return;
        }

        if (parent instanceof ALittleClassStaticDec) {
            ALittleClassStaticDec staticDec = (ALittleClassStaticDec)parent;
            ALittleMethodNameDec nameDec = staticDec.getMethodNameDec();
            if (nameDec == null) {
                return;
            }
            List<ALittleGuess> returnList = new ArrayList<>();
            ALittleMethodReturnDec returnDec = staticDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    returnList.add(allType.guessType());
                }
            }
            checkMethodBody(returnList, nameDec, myElement);
            return;
        }

        if (parent instanceof ALittleGlobalMethodDec) {
            ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec)parent;
            ALittleMethodNameDec nameDec = globalMethodDec.getMethodNameDec();
            if (nameDec == null) {
                return;
            }
            List<ALittleGuess> returnList = new ArrayList<>();
            ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    returnList.add(allType.guessType());
                }
            }
            checkMethodBody(returnList, nameDec, myElement);
            return;
        }
    }
}
