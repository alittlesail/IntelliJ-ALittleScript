package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessList guess = new ALittleGuessList(subType);
        guess.UpdateValue();
        return guess;
    }

    @Override
    public boolean NeedReplace() {
        return subType.NeedReplace();
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        ALittleGuessList guess = new ALittleGuessList(subType.ReplaceTemplate(fillMap));
        guess.UpdateValue();
        return guess;
    }
}
