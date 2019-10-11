package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumDec;

public class ALittleGuessEnum extends ALittleGuess {
    public @NotNull ALittleEnumDec element;
    public ALittleGuessEnum(@NotNull String v, @NotNull ALittleEnumDec e) {
        super(v);
        element = e;
    }

    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
