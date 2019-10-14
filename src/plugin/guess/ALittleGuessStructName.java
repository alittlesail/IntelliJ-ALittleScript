package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructNameDec;

import java.util.Map;

public class ALittleGuessStructName extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mStructName;

    public @NotNull ALittleStructNameDec element;
    public ALittleGuessStructName(@NotNull String namespaceName, @NotNull String structName, @NotNull ALittleStructNameDec e) {
        mNamespaceName = namespaceName;
        mStructName = structName;
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mStructName;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessStructName guess = new ALittleGuessStructName(mNamespaceName, mStructName, element);
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
