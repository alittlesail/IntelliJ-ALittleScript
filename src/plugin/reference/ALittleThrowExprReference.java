package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessString;
import plugin.psi.ALittleThrowExpr;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleThrowExprReference extends ALittleReference<ALittleThrowExpr> {
    public ALittleThrowExprReference(@NotNull ALittleThrowExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "throw表达式不能没有参数");

        if (value_stat_list.size() != 1)
            throw new ALittleGuessException(myElement, "throw只有string一个参数");

        ALittleValueStat value_stat = value_stat_list.get(0);
        ALittleGuess guess = value_stat.guessType();
        if (!(guess instanceof ALittleGuessString))
            throw new ALittleGuessException(value_stat, "throw表达式第一个参数必须是string类型");

        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "throw表达式不能没有参数");

        if (value_stat_list.size() != 1)
            throw new ALittleGuessException(myElement, "throw只有string一个参数");

        ALittleValueStat value_stat = value_stat_list.get(0);

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess guess = value_stat.guessType();
        if (!(guess instanceof ALittleGuessString))
            throw new ALittleGuessException(value_stat, "throw表达式第一个参数必须是string类型");
    }

    @NotNull
    @Override
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "throw表达式不能没有参数");
        }

        if (valueStatList.size() != 1)
            throw new ALittleGuessException(myElement, "throw只有string一个参数");

        // 构建对象
        for (int i = 0; i < valueStatList.size(); ++i) {
            ALittleValueStat valueStat = valueStatList.get(i);
            result.add(new InlayInfo("error", valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
