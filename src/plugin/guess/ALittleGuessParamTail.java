package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessParamTail extends ALittleGuess {
    public ALittleGuessParamTail(@NotNull String v) {
        super(v);
    }

    public boolean isChanged() {
        return false;
    }
}
