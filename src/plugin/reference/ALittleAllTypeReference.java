package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleMethodReturnTypeDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleAllTypeReference extends ALittleReference {
    public ALittleAllTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleAllType allType = (ALittleAllType)myElement;

        if (allType.getCustomType() != null) {
            return allType.getCustomType().guessTypes();
        } else if (allType.getGenericType() != null) {
            return allType.getGenericType().guessTypes();
        } else if (allType.getPrimitiveType() != null) {
            return allType.getPrimitiveType().guessTypes();
        }

        return new ArrayList<>();
    }
}
