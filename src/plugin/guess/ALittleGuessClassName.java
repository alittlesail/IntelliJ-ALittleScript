package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassNameDec;

public class ALittleGuessClassName extends ALittleGuess {
    public @NotNull ALittleClassNameDec element;
    public ALittleGuessClassName(@NotNull String v, @NotNull ALittleClassNameDec e) {
        super(v);
        element = e;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
