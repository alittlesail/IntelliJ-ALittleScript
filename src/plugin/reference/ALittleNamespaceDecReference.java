package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessNamespace;
import plugin.psi.ALittleNamespaceDec;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleNamespaceDecReference extends ALittleReference<ALittleNamespaceDec> {
    public ALittleNamespaceDecReference(@NotNull ALittleNamespaceDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = null;
        ALittleNamespaceNameDec name_dec = myElement.getNamespaceNameDec();
        if (name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义命名域");

        ALittleGuessNamespace info = new ALittleGuessNamespace(name_dec.getText(), myElement);
        info.updateValue();

        guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiHelper.checkError(myElement, myElement.getModifierList());
    }
}
