package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessString;
import plugin.psi.ALittleAssertExpr;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleAssertExprReference extends ALittleReference<ALittleAssertExpr> {
    public ALittleAssertExprReference(@NotNull ALittleAssertExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "assert表达式不能没有参数");

        if (value_stat_list.size() != 2)
            throw new ALittleGuessException(myElement, "assert有且仅有两个参数，第一个是任意类型，第二个是string");

        ALittleValueStat value_stat = value_stat_list.get(0);

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw  new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess guess = value_stat.guessType();

        value_stat = value_stat_list.get(1);

        result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        guess = value_stat.guessType();
        if (!(guess instanceof ALittleGuessString))
            throw new ALittleGuessException(value_stat, "assert表达式第二个参数必须是string类型");
    }

    @NotNull
    @Override
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "assert表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleGuessException(myElement, "assert有且仅有两个参数，第一个是任意类型，第二个是string");

        // 构建对象
        for (int i = 0; i < valueStatList.size(); ++i) {
            ALittleValueStat valueStat = valueStatList.get(i);
            if (i == 0)
                result.add(new InlayInfo("c", valueStat.getNode().getStartOffset()));
            else
                result.add(new InlayInfo("e", valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
