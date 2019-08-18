package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittlePropertyValue;
import plugin.psi.ALittlePropertyValueSuffix;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueReference extends ALittleReference {
    public ALittlePropertyValueReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)myElement;

        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
        if (suffixList.isEmpty()) {
            if (propertyValue.getPropertyValueThisType() != null) {
                return propertyValue.getPropertyValueThisType().guessTypes();
            } else if (propertyValue.getPropertyValueCastType() != null) {
                return propertyValue.getPropertyValueCastType().guessTypes();
            } else if (propertyValue.getPropertyValueCustomType() != null) {
                return propertyValue.getPropertyValueCustomType().guessTypes();
            }
        } else {
            ALittlePropertyValueSuffix suffix = suffixList.get(suffixList.size() -1);
            if (suffix.getPropertyValueDotId() != null) {
                return suffix.getPropertyValueDotId().guessTypes();
            } else if (suffix.getPropertyValueBrackValueStat() != null) {
                return suffix.getPropertyValueBrackValueStat().guessTypes();
            } else if (suffix.getPropertyValueMethodCallStat() != null) {
                return suffix.getPropertyValueMethodCallStat().guessTypes();
            }
        }

        return new ArrayList<>();
    }
}
