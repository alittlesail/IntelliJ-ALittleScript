package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessParamTail;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodReturnTailDecReference extends ALittleReference<ALittleMethodReturnTailDec> {
    public ALittleMethodReturnTailDecReference(@NotNull ALittleMethodReturnTailDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleGuessReturnTail info = new ALittleGuessReturnTail(myElement.getText());
        info.updateValue();
        List<ALittleGuess> guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }
}
