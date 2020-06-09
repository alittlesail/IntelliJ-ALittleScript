package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleClassElementDec;

public class ALittleClassElementDecReference extends ALittleReference<ALittleClassElementDec> {
    public ALittleClassElementDecReference(@NotNull ALittleClassElementDec element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiHelper.checkError(myElement, myElement.getModifierList());
    }
}
