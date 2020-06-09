package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessBool;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueStatReference extends ALittleReference<ALittleValueStat> {
    public ALittleValueStatReference(@NotNull ALittleValueStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        if (myElement.getOpNewStat() != null)
            return myElement.getOpNewStat().guessTypes();
        else if (myElement.getOpNewListStat() != null)
            return myElement.getOpNewListStat().guessTypes();
        else if (myElement.getValueOpStat() != null)
            return ALittleReferenceOpUtil.guessTypes(myElement.getValueOpStat());
        else if (myElement.getBindStat() != null)
            return myElement.getBindStat().guessTypes();
        else if (myElement.getTcallStat() != null)
            return myElement.getTcallStat().guessTypes();
        else if (myElement.getOp2Stat() != null)
        {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = ALittleReferenceOpUtil.guessType(myElement.getOp2Stat());
            guess_list.add(guess);
            return guess_list;
        }

        return new ArrayList<>();
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleIfExpr
                || parent instanceof ALittleElseIfExpr
                || parent instanceof ALittleWhileExpr
                || parent instanceof ALittleDoWhileExpr)
        {
            List<ALittleGuess> guess_list = myElement.guessTypes();
            if (guess_list.size() == 0) return;

            if (!(guess_list.get(0) instanceof ALittleGuessBool) && !guess_list.get(0).getValue().equals("null"))
            throw new ALittleGuessException(myElement, "条件语句中的表达式的类型必须是bool或者null");
        }
    }
}
