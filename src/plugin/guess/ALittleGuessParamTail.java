package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessParamTail extends ALittleGuess {
    public ALittleGuessParamTail(@NotNull String v) {
        value = v;
    }

    @Override
    public boolean isChanged() {
        return false;
    }
}
