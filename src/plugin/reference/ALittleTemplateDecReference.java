package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleTemplateDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleTemplateDecReference extends ALittleReference<ALittleTemplateDec> {
    public ALittleTemplateDecReference(@NotNull ALittleTemplateDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        List<ALittleTemplatePairDec> pairDecList = myElement.getTemplatePairDecList();
        for (ALittleTemplatePairDec pairDec : pairDecList) {
            guessList.add(pairDec.guessType());
        }
        return guessList;
    }

    @Override
    public boolean multiGuessTypes() {
        return true;
    }
}
