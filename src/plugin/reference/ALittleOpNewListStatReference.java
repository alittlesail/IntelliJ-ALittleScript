package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "List列表不能为空");
        }

        ALittleGuessList info = new ALittleGuessList(valueStatList.get(0).guessType());
        info.UpdateValue();

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "这种方式必须填写参数，否则请使用new List的方式");
        }

        // 列表里面的所有元素的类型必须和第一个元素一致
        ALittleGuess valueStat_first = valueStatList.get(0).guessType();
        for (int i = 1; i < valueStatList.size(); ++i) {
            ALittleGuess guessTypeInfo = valueStatList.get(i).guessType();
            if (!valueStat_first.value.equals(guessTypeInfo.value)) {
                throw new ALittleGuessException(valueStatList.get(i), "列表内的元素类型，必须和第一个元素类型一致");
            }
        }
    }
}
