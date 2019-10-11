package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignDecReference extends ALittleReference<ALittleVarAssignDec> {
    public ALittleVarAssignDecReference(@NotNull ALittleVarAssignDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getAllType() != null) {
            return myElement.getAllType().guessTypes();
        } else if (myElement.getAutoType() != null) {
            return myElement.getAutoType().guessTypes();
        }

        return new ArrayList<>();
    }
}
