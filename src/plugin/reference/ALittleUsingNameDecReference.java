package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleUsingDec;
import plugin.psi.ALittleUsingNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleUsingNameDecReference extends ALittleReference<ALittleUsingNameDec> {
    public ALittleUsingNameDecReference(@NotNull ALittleUsingNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleUsingDec)
            return ((ALittleUsingDec) parent).guessTypes();
        return new ArrayList<>();
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___"))
            throw new ALittleGuessException(myElement, "using名不能以3个下划线开头");

        List<ALittleGuess> guess_list = myElement.guessTypes();
        if (guess_list.size() == 0)
            throw new ALittleGuessException(myElement, "未知类型");
        else if (guess_list.size() != 1)
            throw new ALittleGuessException(myElement, "重复定义");
    }
}
