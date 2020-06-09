package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessBool;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleDoWhileCondition;
import plugin.psi.ALittleElseIfCondition;
import plugin.psi.ALittleValueStat;

import java.util.List;

public class ALittleElseIfConditionReference extends ALittleReference<ALittleElseIfCondition> {
    public ALittleElseIfConditionReference(@NotNull ALittleElseIfCondition element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null)
            throw new ALittleGuessException(myElement, "没有条件表达式");

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess guess = value_stat.guessType();
        if (guess instanceof ALittleGuessBool) return;

        throw new ALittleGuessException(myElement, "这里必须是一个bool表达式");
    }
}
