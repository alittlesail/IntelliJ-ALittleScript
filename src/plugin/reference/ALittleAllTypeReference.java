package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;

import java.util.ArrayList;
import java.util.List;

public class ALittleAllTypeReference extends ALittleReference<ALittleAllType> {
    public ALittleAllTypeReference(@NotNull ALittleAllType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getCustomType() != null) {
            return myElement.getCustomType().guessTypes();
        } else if (myElement.getGenericType() != null) {
            return myElement.getGenericType().guessTypes();
        } else if (myElement.getPrimitiveType() != null) {
            return myElement.getPrimitiveType().guessTypes();
        }
        return new ArrayList<>();
    }
}