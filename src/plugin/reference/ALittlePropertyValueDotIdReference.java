package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdReference extends ALittleReference<ALittlePropertyValueDotId> {
    public ALittlePropertyValueDotIdReference(@NotNull ALittlePropertyValueDotId element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getPropertyValueDotIdName() != null) {
            return myElement.getPropertyValueDotIdName().guessTypes();
        }
        return new ArrayList<>();
    }
}
