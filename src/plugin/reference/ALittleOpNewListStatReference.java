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
}
