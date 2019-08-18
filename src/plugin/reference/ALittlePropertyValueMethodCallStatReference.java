package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueMethodCallStatReference extends ALittleReference {
    public ALittlePropertyValueMethodCallStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        try {
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix) myElement.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue) propertyValueSuffix.getParent();

            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
            PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
            if (prePropertyValue == null) {
                prePropertyValue = propertyValue.getPropertyValueThisType();
            }
            if (prePropertyValue == null) {
                prePropertyValue = propertyValue.getPropertyValueCastType();
            }

            for (ALittlePropertyValueSuffix suffix : suffixList) {
                if (suffix.equals(propertyValueSuffix)) {
                    break;
                }
                ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
                if (dotId != null) prePropertyValue = dotId;
                ALittlePropertyValueBrackValueStat brackValue = suffix.getPropertyValueBrackValueStat();
                if (brackValue != null) prePropertyValue = brackValue;
                ALittlePropertyValueMethodCallStat methodCall = suffix.getPropertyValueMethodCallStat();
                if (methodCall != null) prePropertyValue = methodCall;
            }

            if (prePropertyValue == null) {
                return guessList;
            }

            ALittleReferenceUtil.GuessTypeInfo preType = null;
            if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
                preType = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
                preType = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
                preType = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueDotId) {
                ALittlePropertyValueDotIdName dotIdName = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName();
                preType = dotIdName.guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
                preType = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
                preType = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
            }

            if (preType == null) {
                return guessList;
            }

            if (preType.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
                guessList.addAll(preType.functorReturnList);
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }

        return guessList;
    }
}
