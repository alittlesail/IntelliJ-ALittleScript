package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittlePropertyValue;
import plugin.psi.ALittlePropertyValueSuffix;

import java.util.List;

public class ALittlePropertyValueReference extends ALittleReference<ALittlePropertyValue> {
    public ALittlePropertyValueReference(@NotNull ALittlePropertyValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittlePropertyValueSuffix> suffixList = myElement.getPropertyValueSuffixList();
        if (suffixList.isEmpty()) {
            return myElement.getPropertyValueFirstType().guessTypes();
        } else {
            return suffixList.get(suffixList.size() -1).guessTypes();
        }
    }
}
