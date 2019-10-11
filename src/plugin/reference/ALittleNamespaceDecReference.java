package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
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

        ALittleGuess info = new ALittleGuess();
        info.type = ALittleReferenceUtil.GuessType.GT_NAMESPACE;
        info.value = namespaceNameDec.getIdContent().getText();
        info.element = myElement;

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
