package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueFirstType;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueFirstTypeReference extends ALittleReference<ALittlePropertyValueFirstType> {
    public ALittlePropertyValueFirstTypeReference(@NotNull ALittlePropertyValueFirstType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getPropertyValueCastType() != null) {
            return myElement.getPropertyValueCastType().guessTypes();
        } else if (myElement.getPropertyValueCustomType() != null) {
            return myElement.getPropertyValueCustomType().guessTypes();
        } else if (myElement.getPropertyValueThisType() != null) {
            return myElement.getPropertyValueThisType().guessTypes();
        }

        return new ArrayList<>();
    }
}
