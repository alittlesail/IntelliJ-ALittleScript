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

    // 获取类名调用
    public ALittleClassDec guessClassNameInvoke() {
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
        PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueThisType();
        }
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueCastType();
        }

        int index = suffixList.indexOf(propertyValueSuffix);
        if (index == -1) return null;
        // 向前走两个后缀
        index -= 2;
        if (index < -1) return null;

        if (index >= 0) {
            ALittlePropertyValueSuffix suffix = suffixList.get(index);
            ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
            if (dotId != null) prePropertyValue = dotId;
            ALittlePropertyValueBrackValueStat brackValue = suffix.getPropertyValueBrackValueStat();
            if (brackValue != null) prePropertyValue = brackValue;
            ALittlePropertyValueMethodCallStat methodCall = suffix.getPropertyValueMethodCallStat();
            if (methodCall != null) prePropertyValue = methodCall;
        }

        PsiElement pre_type = null;
        if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            pre_type = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dotId_name = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName();
            pre_type = dotId_name.guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
        }

        if (!(pre_type instanceof ALittleClassNameDec)) return null;
        return (ALittleClassDec)pre_type.getParent();
    }

    // 获取前缀的类型
    public PsiElement guessTypesForPreType() {
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();

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
            return null;
        }

        PsiElement pre_type = null;
        if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            pre_type = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dotId_name = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName();
            pre_type = dotId_name.guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
        }

        return pre_type;
    }

    // 获取返回值类型
    @NotNull
    public List<PsiElement> guessTypes() {
        PsiElement pre_type = guessTypesForPreType();
        if (pre_type == null) {
            return new ArrayList<>();
        }

        // 如果是方法名，那么就返回方法名的返回值类型列表
        if (pre_type instanceof ALittleMethodNameDec) {
            return ((ALittleMethodNameDec)pre_type).guessTypes();
        // 如果是Functor那么就返回Functor的返回值类型列表
        } else if (pre_type instanceof ALittleGenericType) {
            do {
                ALittleGenericType dec = (ALittleGenericType) pre_type;
                if (dec.getGenericFunctorType() == null) break;
                ALittleGenericFunctorType functor_dec = dec.getGenericFunctorType();
                if (functor_dec.getGenericFunctorReturnType() == null) break;

                // 遍历列表获取返回值
                List<ALittleAllType> allTypeList = functor_dec.getGenericFunctorReturnType().getAllTypeList();
                List<PsiElement> guessList = new ArrayList<>();
                for (ALittleAllType allType : allTypeList) {
                    try {
                        guessList.add(ALittleUtil.guessType(allType));
                    } catch (ALittleUtil.ALittleElementException ignored) {
                        return new ArrayList<>();
                    }
                }
                return guessList;
            } while (false);
        }

        return new ArrayList<>();
    }
}
