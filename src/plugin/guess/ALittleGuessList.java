package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessList extends ALittleGuess {
    public @NotNull ALittleGuess subType;

    public ALittleGuessList(@NotNull ALittleGuess sub) {
        subType = sub;
    }

    @Override
    public void UpdateValue() {
        value = "List<" + subType.value + ">";
    }

    @Override
    public boolean isChanged() {
        return subType.isChanged();
    }
}
