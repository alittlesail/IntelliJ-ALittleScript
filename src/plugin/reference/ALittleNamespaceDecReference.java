package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
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
        ALittleNamespaceNameDec namespaceNameDec = myElement.getNamespaceNameDec();
        if (namespaceNameDec == null) {
            throw new ALittleGuessException(myElement, "没有定义命名域");
        }

        ALittleGuessNamespace info = new ALittleGuessNamespace(namespaceNameDec.getIdContent().getText(), myElement);
        info.UpdateValue();

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
