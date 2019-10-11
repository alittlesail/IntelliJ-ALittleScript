package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleWrapValueStat;

import java.util.List;

public class ALittleWrapValueStatReference extends ALittleReference<ALittleWrapValueStat> {
    public ALittleWrapValueStatReference(@NotNull ALittleWrapValueStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return myElement.getValueStat().guessTypes();
    }
}
