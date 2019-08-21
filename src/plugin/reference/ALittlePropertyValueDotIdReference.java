package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdReference extends ALittleReference<ALittlePropertyValueDotId> {
    public ALittlePropertyValueDotIdReference(@NotNull ALittlePropertyValueDotId element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getPropertyValueDotIdName() != null) {
            return myElement.getPropertyValueDotIdName().guessTypes();
        }
        return new ArrayList<>();
    }
}
