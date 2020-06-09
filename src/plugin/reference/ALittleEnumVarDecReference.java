package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.ALittleEnumVarDec;

import java.util.List;

public class ALittleEnumVarDecReference extends ALittleReference<ALittleEnumVarDec> {
    public ALittleEnumVarDecReference(@NotNull ALittleEnumVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getTextContent() != null) {
            return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("string");
        } else {
            return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("int");
        }
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getNumberContent() == null) return;

        String value = myElement.getNumberContent().getText();
        if (!PsiHelper.isInt(value)) {
            throw new ALittleGuessException(myElement.getNumberContent(), "枚举值必须是整数");
        }
    }
}
