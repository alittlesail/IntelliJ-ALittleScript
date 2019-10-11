package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleValueFactorStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueFactorStatReference extends ALittleReference<ALittleValueFactorStat> {
    public ALittleValueFactorStatReference(@NotNull ALittleValueFactorStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleReferenceUtil.ALittleGuessException {
        if (myElement.getPropertyValue() != null) {
            return myElement.getPropertyValue().guessTypes();
        } else if (myElement.getReflectValue() != null) {
            return myElement.getReflectValue().guessTypes();
        } else if (myElement.getConstValue() != null) {
            return myElement.getConstValue().guessTypes();
        } else if (myElement.getWrapValueStat() != null) {
            return myElement.getWrapValueStat().guessTypes();
        }

        return new ArrayList<>();
    }
}
