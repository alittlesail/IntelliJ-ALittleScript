package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleTemplateDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleTemplateDecReference extends ALittleReference<ALittleTemplateDec> {
    public ALittleTemplateDecReference(@NotNull ALittleTemplateDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        List<ALittleTemplatePairDec> pairDecList = myElement.getTemplatePairDecList();
        for (ALittleTemplatePairDec pairDec : pairDecList) {
            guessList.add(pairDec.guessType());
        }
        return guessList;
    }
}
