package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuess {

    public @NotNull String value;

    public ALittleGuess(@NotNull String v) {
    }

    public boolean isChanged() {
        return true;
    }
}
