package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessList;
import plugin.guess.ALittleGuessMap;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleFlowExprReference extends ALittleReference<ALittleFlowExpr> {
    public ALittleFlowExprReference(@NotNull ALittleFlowExpr element, TextRange textRange) {
        super(element, textRange);
    }

    public void checkError() throws ALittleGuessException {
        // 获取对应的函数对象
        PsiElement parent = myElement;
        while (parent != null)
        {
            if (parent instanceof ALittleClassGetterDec
                    || parent instanceof ALittleClassSetterDec
                || parent instanceof ALittleClassMethodDec
                    || parent instanceof ALittleClassCtorDec
                || parent instanceof ALittleClassStaticDec
                    || parent instanceof ALittleGlobalMethodDec)
            {
                break;
            }

            if (parent instanceof ALittleForExpr
                    || parent instanceof ALittleWhileExpr
                || parent instanceof ALittleDoWhileExpr)
            {
                return;
            }
            parent = parent.getParent();
        }

        throw new ALittleGuessException(myElement, "break和continue只能在for,while,do while中");
    }
}
