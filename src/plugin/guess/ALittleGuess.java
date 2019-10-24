package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class ALittleGuess {
    public @NotNull String value = "";
    public boolean isRegister = false;
    public ALittleGuess() { }
    public boolean isChanged() {
        return true;
    }

    public abstract void UpdateValue();

    @NotNull
    public abstract ALittleGuess Clone();

    public abstract boolean NeedReplace();
    @NotNull
    public abstract ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap);
}
