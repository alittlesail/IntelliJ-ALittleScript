package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleTemplatePairDec;

public class ALittleGuessClassTemplate extends ALittleGuess {
    public ALittleGuess templateExtends;
    public @NotNull ALittleTemplatePairDec element;
    public ALittleGuessClassTemplate(@NotNull ALittleTemplatePairDec e, ALittleGuess t) {
        super(e.getIdContent().getText());
        element = e;
        templateExtends = t;
    }

    public boolean isChanged() {
        if (templateExtends != null && templateExtends.isChanged()) {
            return true;
        }
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
