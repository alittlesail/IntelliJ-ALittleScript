package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleClassGetterDec;
import plugin.psi.ALittleClassSetterDec;

public class ALittleClassSetterDecReference extends ALittleReference<ALittleClassSetterDec> {
    public ALittleClassSetterDecReference(@NotNull ALittleClassSetterDec element, TextRange textRange) {
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
