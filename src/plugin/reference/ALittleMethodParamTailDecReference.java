package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleMethodParamTailDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodParamTailDecReference extends ALittleReference<ALittleMethodParamTailDec> {
    public ALittleMethodParamTailDecReference(@NotNull ALittleMethodParamTailDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_PARAM_TAIL;
        info.value = myElement.getText();
        info.element = myElement;
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
