package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructDec;

public class ALittleGuessStruct extends ALittleGuess {
    public @NotNull ALittleStructDec element;
    public ALittleGuessStruct(@NotNull String v, @NotNull ALittleStructDec e) {
        super(v);
        element = e;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
