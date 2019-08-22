package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePrimitiveType;

import java.util.ArrayList;
import java.util.List;

public class ALittlePrimitiveTypeReference extends ALittleReference<ALittlePrimitiveType> {
    public ALittlePrimitiveTypeReference(@NotNull ALittlePrimitiveType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = ALittleReferenceUtil.sPrimitiveGuessTypeMap.get(myElement.getText());
        if (guessTypeList == null) guessTypeList = new ArrayList<>();
        return guessTypeList;
    }
}
