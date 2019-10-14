package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ALittleGuessReturnTail extends ALittleGuess {
    public ALittleGuessReturnTail(@NotNull String v) {
        value = v;
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public void UpdateValue() {}
    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessReturnTail guess = new ALittleGuessReturnTail(value);
        guess.UpdateValue();
        return guess;
    }

    @Override
    public boolean NeedReplace() {
        return false;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        return this;
    }
}
