package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.ALittleMethodReturnDec;
import plugin.psi.ALittleMethodReturnTailDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodReturnDecReference extends ALittleReference<ALittleMethodReturnDec> {
    public ALittleMethodReturnDecReference(@NotNull ALittleMethodReturnDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getMethodReturnOneDecList().size() == 0)
            throw  new ALittleGuessException(myElement, "没有定义返回值类型");
        return new ArrayList<>();
    }
}
