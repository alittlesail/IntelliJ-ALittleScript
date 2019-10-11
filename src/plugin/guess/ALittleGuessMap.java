package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessMap extends ALittleGuess {
    public @NotNull ALittleGuess keyType;
    public @NotNull ALittleGuess valueType;

    public ALittleGuessMap(@NotNull ALittleGuess key, @NotNull ALittleGuess value) {
        super("Map<" + key.value + "," + value.value + ">");
        keyType = key;
        valueType = value;
    }

    public boolean isChanged() {
        return keyType.isChanged() || valueType.isChanged();
    }
}
