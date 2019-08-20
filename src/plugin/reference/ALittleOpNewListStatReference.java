package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleOpNewListStat;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewListStatReference extends ALittleReference<ALittleOpNewListStat> {
    public ALittleOpNewListStatReference(@NotNull ALittleOpNewListStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "List列表不能为空");
        }
        ALittleReferenceUtil.GuessTypeInfo GuessInfo = valueStatList.get(0).guessType();

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_LIST;
        info.value = "List<" + GuessInfo.value + ">";
        info.element = myElement;
        info.listSubType = GuessInfo;

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "这种方式必须填写参数，否则请使用new List的方式");
        }

        // 列表里面的所有元素的类型必须和第一个元素一致
        ALittleReferenceUtil.GuessTypeInfo value_stat_first = valueStatList.get(0).guessType();
        for (int i = 1; i < valueStatList.size(); ++i) {
            ALittleReferenceUtil.GuessTypeInfo guessTypeInfo = valueStatList.get(i).guessType();
            if (!value_stat_first.value.equals(guessTypeInfo.value)) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueStatList.get(i), "列表内的元素类型，必须和第一个元素类型一致");
            }
        }
    }
}
