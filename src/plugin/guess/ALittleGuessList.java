package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessList extends ALittleGuess {
    public @NotNull ALittleGuess subType;

    public ALittleGuessList(@NotNull ALittleGuess sub) {
        super("List<" + sub.value + ">");
        subType = sub;
    }

    public boolean isChanged() {
        return subType.isChanged();
    }
}
