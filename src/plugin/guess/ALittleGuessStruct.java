package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructDec;

public class ALittleGuessStruct extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mStructName;

    public @NotNull ALittleStructDec element;
    public ALittleGuessStruct(@NotNull String namespaceName, @NotNull String structName, @NotNull ALittleStructDec e) {
        mNamespaceName = namespaceName;
        mStructName = structName;
        element = e;
    }

    @NotNull
    public String GetNamespaceName() {
        return mNamespaceName;
    }

    @NotNull
    public String GetStructName() {
        return mStructName;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mStructName;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
