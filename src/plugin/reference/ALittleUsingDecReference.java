package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleUsingDecReference extends ALittleReference<ALittleUsingDec> {
    public ALittleUsingDecReference(@NotNull ALittleUsingDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getCustomType() != null) {
            return myElement.getCustomType().guessTypes();
        }
        return new ArrayList<>();
    }
}
