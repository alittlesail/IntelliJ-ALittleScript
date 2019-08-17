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

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        PsiElement pre_type = guessTypesForPreType();
        if (pre_type == null) {
            return guess_list;
        }

        if (pre_type instanceof ALittleGenericType) {
            ALittleGenericType generic_type = (ALittleGenericType)pre_type;
            if (generic_type.getGenericListType() != null) {
                ALittleGenericListType list_type = generic_type.getGenericListType();
                ALittleAllType allType = list_type.getAllType();
                if (allType != null) {
                    if (allType.getPrimitiveType() != null) {
                        guess_list.add(allType.getPrimitiveType());
                    } else if (allType.getGenericType() != null) {
                        guess_list.add(allType.getGenericType());
                    } else if (allType.getCustomType() != null) {
                        PsiElement guess = allType.getCustomType().getCustomTypeNameDec().guessType();
                        if (guess != null) guess_list.add(guess);
                    }
                }
            } else if (generic_type.getGenericMapType() != null) {
                ALittleGenericMapType map_type = generic_type.getGenericMapType();
                List<ALittleAllType> allTypeList = map_type.getAllTypeList();
                if (allTypeList.size() == 2) {
                    ALittleAllType allType = allTypeList.get(1);
                    if (allType != null) {
                        if (allType.getPrimitiveType() != null) {
                            guess_list.add(allType.getPrimitiveType());
                        } else if (allType.getGenericType() != null) {
                            guess_list.add(allType.getGenericType());
                        } else if (allType.getCustomType() != null) {
                            PsiElement guess = allType.getCustomType().getCustomTypeNameDec().guessType();
                            if (guess != null) guess_list.add(guess);
                        }
                    }
                }
            }
        // 如果是函数名，并且是getter，那么就返回对应的返回值
        } else if (pre_type instanceof ALittleMethodNameDec) {
            guess_list.add(((ALittleMethodNameDec)pre_type).guessType());
        }

        return guess_list;
    }
}
