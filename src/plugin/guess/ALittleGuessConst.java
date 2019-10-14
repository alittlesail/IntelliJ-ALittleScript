package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleGuessConst extends ALittleGuess {
    public static List<ALittleGuess> sConstNullGuess;
    static {
        sConstNullGuess = new ArrayList<>();
        sConstNullGuess.add(new ALittleGuessConst("null"));
    }

    public ALittleGuessConst(@NotNull String v) {
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
        return new ALittleGuessConst(value);
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
