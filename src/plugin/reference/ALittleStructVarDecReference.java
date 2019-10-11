package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleStructVarDec;

import java.util.List;

public class ALittleStructVarDecReference extends ALittleReference<ALittleStructVarDec> {
    public ALittleStructVarDecReference(@NotNull ALittleStructVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return myElement.getAllType().guessTypes();
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
