package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleClassElementDec;
import plugin.psi.ALittleClassExtendsDec;
import plugin.psi.ALittleClassNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassExtendsDecReference extends ALittleReference<ALittleClassExtendsDec> {
    public ALittleClassExtendsDecReference(@NotNull ALittleClassExtendsDec element, TextRange textRange) {
        super(element, textRange);

        if (myElement.getNamespaceNameDec() != null)
            mNamespace = myElement.getNamespaceNameDec().getText();
        else
            mNamespace = PsiHelper.getNamespaceName(myElement);

        mKey = "";
        if (myElement.getClassNameDec() != null)
            mKey = myElement.getClassNameDec().getText();
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleClassNameDec class_name_dec = myElement.getClassNameDec();
        if (class_name_dec == null)
        {
            return new ArrayList<>();
        }

        return class_name_dec.guessTypes();
    }
}
