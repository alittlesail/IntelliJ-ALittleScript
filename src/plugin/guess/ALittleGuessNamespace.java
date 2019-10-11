package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceDec;

public class ALittleGuessNamespace extends ALittleGuess {
    private @NotNull String mNamespaceName;
    public @NotNull ALittleNamespaceDec element;
    public ALittleGuessNamespace(@NotNull String namespaceName, @NotNull ALittleNamespaceDec e) {
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
