package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleForPairDecReference extends ALittleReference<ALittleForPairDec> {
    public ALittleForPairDecReference(@NotNull ALittleForPairDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getAllType() != null) {
            return myElement.getAllType().guessTypes();
        } else if (myElement.getAutoType() != null) {
            return myElement.getAutoType().guessTypes();
        }
        throw new ALittleGuessException(myElement, "ALittleForPairDec出现未知的子节点");
    }
}
