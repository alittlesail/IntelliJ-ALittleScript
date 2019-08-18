package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleValueFactor;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueFactorReference extends ALittleReference {
    public ALittleValueFactorReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueFactor valueFactor = (ALittleValueFactor)myElement;

        if (valueFactor.getPropertyValue() != null) {
            return valueFactor.getPropertyValue().guessTypes();
        } else if (valueFactor.getReflectValue() != null) {
            return valueFactor.getReflectValue().guessTypes();
        } else if (valueFactor.getConstValue() != null) {
            return valueFactor.getConstValue().guessTypes();
        } else if (valueFactor.getValueStatParen() != null) {
            return valueFactor.getValueStatParen().guessTypes();
        }

        return new ArrayList<>();
    }
}
