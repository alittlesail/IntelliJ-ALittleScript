package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessList;
import plugin.psi.ALittleOpNewListStat;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewListStatReference extends ALittleReference<ALittleOpNewListStat> {
    public ALittleOpNewListStatReference(@NotNull ALittleOpNewListStat element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list;
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "List列表不能为空");

        ALittleGuess guess = value_stat_list.get(0).guessType();
        ALittleGuessList info = new ALittleGuessList(guess, false, false);
        info.updateValue();
        guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "这种方式必须填写参数，否则请使用new List的方式");

        // 列表里面的所有元素的类型必须和第一个元素一致
        ALittleGuess value_stat_first = value_stat_list.get(0).guessType();
        for (int i = 1; i < value_stat_list.size(); ++i) {
            ALittleValueStat value_stat = value_stat_list.get(i);
            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

            ALittleGuess guess = value_stat.guessType();
            if (!value_stat_first.getValue().equals(guess.getValue()))
                throw new ALittleGuessException(value_stat_list.get(i), "列表内的元素类型，必须和第一个元素类型一致");
        }
    }
}
