package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceNameDec;

public class ALittleGuessNamespaceName extends ALittleGuess {
    public @NotNull ALittleNamespaceNameDec element;
    public ALittleGuessNamespaceName(@NotNull String v, @NotNull ALittleNamespaceNameDec e) {
        super(v);
        element = e;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
