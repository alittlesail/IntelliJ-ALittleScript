package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.List;

public class ALittleVarAssignNameDecReference extends ALittleReference<ALittleVarAssignNameDec> {
    public ALittleVarAssignNameDecReference(@NotNull ALittleVarAssignNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleVarAssignDec varAssignDec = (ALittleVarAssignDec)myElement.getParent();
        return varAssignDec.guessTypes();
    }
}
