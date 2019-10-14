package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessMap guess = new ALittleGuessMap(keyType, valueType);
        guess.UpdateValue();
        return guess;
    }

    @Override
    public boolean NeedReplace() {
        return keyType.NeedReplace() || valueType.NeedReplace();
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        ALittleGuessMap guess = new ALittleGuessMap(keyType.ReplaceTemplate(fillMap), valueType.ReplaceTemplate(fillMap));
        guess.UpdateValue();
        return guess;
    }
}
