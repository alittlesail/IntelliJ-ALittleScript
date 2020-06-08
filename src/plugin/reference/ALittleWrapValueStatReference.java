package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleValueStat;
import plugin.psi.ALittleWrapValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleWrapValueStatReference extends ALittleReference<ALittleWrapValueStat> {
    public ALittleWrapValueStatReference(@NotNull ALittleWrapValueStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat != null)
        {
            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");
        }
        return new ArrayList<>();
    }
}
