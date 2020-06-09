package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleMethodParamOneDec;

public class ALittleMethodParamOneDecReference extends ALittleReference<ALittleMethodParamOneDec> {
    public ALittleMethodParamOneDecReference(@NotNull ALittleMethodParamOneDec element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiHelper.checkError(myElement, myElement.getModifierList());
    }
}
