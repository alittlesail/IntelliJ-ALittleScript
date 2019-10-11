package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructNameDec;

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
}
