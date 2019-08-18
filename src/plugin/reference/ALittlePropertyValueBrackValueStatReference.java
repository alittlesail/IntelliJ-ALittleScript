package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueBrackValueStatReference extends ALittleReference {
    public ALittlePropertyValueBrackValueStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        // 获取父节点
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();

        // 获取第一个类型
        PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueThisType();
        }
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueCastType();
        }

        // 找到前一个的位置
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
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
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知的表达式类型");
        }

        // 获取类型
        ALittleReferenceUtil.GuessTypeInfo preType = null;
        if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
            preType = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
            preType = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            preType = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof  ALittlePropertyValueDotId) {
            preType = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName().guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
            preType = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
            preType = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
        }

        if (preType == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知的表达式类型");
        }

        if (preType.type == ALittleReferenceUtil.GuessType.GT_LIST) {
            guessList.add(preType.listSubType);
        } else if (preType.type == ALittleReferenceUtil.GuessType.GT_MAP) {
            guessList.add(preType.mapValueType);
        }

        return guessList;
    }
}
