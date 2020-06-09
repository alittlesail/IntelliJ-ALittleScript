package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleOp1Expr;
import plugin.psi.ALittleValueStat;

import java.util.List;

public class ALittleOp1ExprReference extends ALittleReference<ALittleOp1Expr> {
    public ALittleOp1ExprReference(@NotNull ALittleOp1Expr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess guess = value_stat.guessType();

        if (guess.is_const)
            throw new ALittleGuessException(myElement, "const类型不能使用--或者++运算符");
    }
}
