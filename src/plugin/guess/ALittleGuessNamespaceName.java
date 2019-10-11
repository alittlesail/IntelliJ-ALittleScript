package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceNameDec;

public class ALittleGuessNamespaceName extends ALittleGuess {
    private @NotNull String mNamespaceName;

    public @NotNull ALittleNamespaceNameDec element;
    public ALittleGuessNamespaceName(@NotNull String namespaceName, @NotNull ALittleNamespaceNameDec e) {
        mNamespaceName = namespaceName;
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
