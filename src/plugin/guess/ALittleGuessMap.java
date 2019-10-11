package plugin.guess;

import org.jetbrains.annotations.NotNull;

public class ALittleGuessMap extends ALittleGuess {
    public @NotNull ALittleGuess keyType;
    public @NotNull ALittleGuess valueType;

    public ALittleGuessMap(@NotNull ALittleGuess key, @NotNull ALittleGuess value) {
        keyType = key;
        valueType = value;
    }

    @Override
    public void UpdateValue() {
        value = "Map<" + keyType.value + "," + valueType.value + ">";
    }

    @Override
    public boolean isChanged() {
        return keyType.isChanged() || valueType.isChanged();
    }
}
