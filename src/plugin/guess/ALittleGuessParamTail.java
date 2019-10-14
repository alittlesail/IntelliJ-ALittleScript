package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ALittleGuessParamTail extends ALittleGuess {
    public ALittleGuessParamTail(@NotNull String v) {
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
        ALittleGuessParamTail guess = new ALittleGuessParamTail(value);
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
