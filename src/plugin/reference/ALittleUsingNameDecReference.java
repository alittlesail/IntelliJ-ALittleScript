package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleUsingDec;
import plugin.psi.ALittleUsingNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleUsingNameDecReference extends ALittleReference<ALittleUsingNameDec> {
    public ALittleUsingNameDecReference(@NotNull ALittleUsingNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleUsingDec) {
            return ((ALittleUsingDec)parent).guessTypes();
        }
        return new ArrayList<>();
    }
}
