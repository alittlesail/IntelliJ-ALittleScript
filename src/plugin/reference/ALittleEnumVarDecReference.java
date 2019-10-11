package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.*;

import java.util.List;

public class ALittleEnumVarDecReference extends ALittleReference<ALittleEnumVarDec> {
    public ALittleEnumVarDecReference(@NotNull ALittleEnumVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getStringContent() != null) {
            return ALittleGuessPrimitive.sPrimitiveGuessMap.get("string");
        } else {
            return ALittleGuessPrimitive.sPrimitiveGuessMap.get("int");
        }
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getDigitContent() == null) return;

        String value = myElement.getDigitContent().getText();
        if (!PsiHelper.isInt(value)) {
            throw new ALittleGuessException(myElement.getDigitContent(), "枚举值必须是整数");
        }
    }
}
