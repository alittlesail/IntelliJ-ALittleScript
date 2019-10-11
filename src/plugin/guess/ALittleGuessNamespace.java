package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;

public class ALittleGuessNamespace extends ALittleGuess {
    public @NotNull PsiElement element;
    public ALittleGuessNamespace(@NotNull String v, @NotNull PsiElement e) {
        super(v);
        element = e;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
