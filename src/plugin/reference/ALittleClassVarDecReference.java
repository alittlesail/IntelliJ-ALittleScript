package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.List;

public class ALittleClassVarDecReference extends ALittleReference<ALittleClassVarDec> {
    public ALittleClassVarDecReference(@NotNull ALittleClassVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return myElement.getAllType().guessTypes();
    }
}
