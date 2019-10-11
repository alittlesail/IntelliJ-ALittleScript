package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassDec;
import plugin.psi.ALittleClassNameDec;
import plugin.psi.ALittleTemplateDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassDecReference extends ALittleReference<ALittleClassDec> {
    public ALittleClassDecReference(@NotNull ALittleClassDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleClassNameDec classNameDec = myElement.getClassNameDec();
        if (classNameDec == null) {
            throw new ALittleGuessException(myElement, "没有定义类名");
        }

        ALittleGuessClass info = new ALittleGuessClass(mNamespace, classNameDec.getIdContent().getText(), myElement);
        ALittleTemplateDec templateDec = myElement.getTemplateDec();
        if (templateDec != null) {
            info.templateList = templateDec.guessTypes();
        }
        info.UpdateValue();

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
