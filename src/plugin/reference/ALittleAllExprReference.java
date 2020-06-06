package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleAllExpr;

public class ALittleAllExprReference extends ALittleReference<ALittleAllExpr> {
    public ALittleAllExprReference(@NotNull ALittleAllExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiHelper.checkError(myElement, myElement.getModifierList());
    }
}
