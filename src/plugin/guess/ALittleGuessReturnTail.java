package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessReturnTail extends ALittleGuess {
    public ALittleGuessReturnTail(@NotNull String v) {
        value = v;
    }

    @Override
    public boolean isChanged() {
        return false;
    }
}
