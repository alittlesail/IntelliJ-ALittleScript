package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleClassExtendsDec;
import plugin.psi.ALittleClassGetterDec;
import plugin.psi.ALittleClassNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassGetterDecReference extends ALittleReference<ALittleClassGetterDec> {
    public ALittleClassGetterDecReference(@NotNull ALittleClassGetterDec element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getMethodNameDec() == null)
            throw new ALittleGuessException(myElement, "没有函数名");

        if (myElement.getMethodBodyDec() == null)
            throw new ALittleGuessException(myElement, "没有函数体");
    }
}
