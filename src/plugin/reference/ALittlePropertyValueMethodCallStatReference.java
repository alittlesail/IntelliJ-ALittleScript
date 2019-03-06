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

public class ALittlePropertyValueMethodCallStatReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    public ALittlePropertyValueMethodCallStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    public ALittleClassDec guessClassNameInvoke() {
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue)property_value_suffix.getParent();

        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        PsiElement pre_property_value = property_value.getPropertyValueCustomType();
        if (pre_property_value == null) {
            pre_property_value = property_value.getPropertyValueThisType();
        }

        int index = -1;
        for (int i = 0; i < suffix_list.size(); ++i) {
            if (suffix_list.get(i).equals(property_value_suffix)) {
                index = i;
                break;
            }
        }
        if (index == -1) return null;
        // 向前走两个后缀
        index -= 2;
        if (index < -1) return null;

        if (index >= 0) {
            ALittlePropertyValueSuffix suffix = suffix_list.get(index);
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null) pre_property_value = dot_id;
            ALittlePropertyValueBrackValueStat brack_value = suffix.getPropertyValueBrackValueStat();
            if (brack_value != null) pre_property_value = brack_value;
            ALittlePropertyValueMethodCallStat method_call = suffix.getPropertyValueMethodCallStat();
            if (method_call != null) pre_property_value = method_call;
        }

        PsiElement pre_type = null;
        if (pre_property_value instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) pre_property_value).guessType();
        } else if (pre_property_value instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dot_id_name = ((ALittlePropertyValueDotId) pre_property_value).getPropertyValueDotIdName();
            pre_type = dot_id_name.guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) pre_property_value).guessType();
        }

        if (!(pre_type instanceof ALittleClassNameDec)) return null;
        return (ALittleClassDec)pre_type.getParent();
    }

    public PsiElement guessTypesForPreType() {
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue)property_value_suffix.getParent();

        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        PsiElement pre_property_value = property_value.getPropertyValueCustomType();
        if (pre_property_value == null) {
            pre_property_value = property_value.getPropertyValueThisType();
        }

        for (ALittlePropertyValueSuffix suffix : suffix_list) {
            if (suffix.equals(property_value_suffix)) {
                break;
            }
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null) pre_property_value = dot_id;
            ALittlePropertyValueBrackValueStat brack_value = suffix.getPropertyValueBrackValueStat();
            if (brack_value != null) pre_property_value = brack_value;
            ALittlePropertyValueMethodCallStat method_call = suffix.getPropertyValueMethodCallStat();
            if (method_call != null) pre_property_value = method_call;
        }

        if (pre_property_value == null) {
            return null;
        }

        PsiElement pre_type = null;
        if (pre_property_value instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) pre_property_value).guessType();
        } else if (pre_property_value instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dot_id_name = ((ALittlePropertyValueDotId) pre_property_value).getPropertyValueDotIdName();
            pre_type = dot_id_name.guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) pre_property_value).guessType();
        }

        return pre_type;
    }

    @NotNull
    public List<PsiElement> guessTypes(boolean method_call_multi) {
        if (!method_call_multi) {
            return guessTypes();
        }

        List<PsiElement> guess_list = new ArrayList<>();

        PsiElement pre_type = guessTypesForPreType();
        if (pre_type == null) {
            return guess_list;
        }

        PsiElement guess = null;
        if (pre_type instanceof ALittleMethodNameDec) {
            guess_list = ((ALittleMethodNameDec)pre_type).guessTypes();
        } else if (pre_type instanceof ALittleClassDec) {
            guess = pre_type;
        } else if (pre_type instanceof ALittlePrimitiveType && pre_type.getText().equals("any")) {
            guess_list.add(myElement);
        } else if (pre_type instanceof ALittlePropertyValueMethodCallStat) {
            guess_list.add(myElement);
        } else if (pre_type instanceof ALittlePropertyValueBrackValueStat) {
            guess_list.add(myElement);
        } else if (pre_type instanceof ALittleGenericType) {
            ALittleGenericType dec = (ALittleGenericType)pre_type;
            if (dec.getGenericFunctorType() != null) {
                ALittleGenericFunctorType functor_dec = dec.getGenericFunctorType();
                if (functor_dec.getGenericFunctorReturnType() != null) {
                    List<ALittleAllType> all_type_list = functor_dec.getGenericFunctorReturnType().getAllTypeList();
                    List<PsiElement> tmp = new ArrayList<>();
                    for (ALittleAllType all_type : all_type_list) {
                        guess = ALittleUtil.guessType(all_type);
                        if (guess == null) {
                            tmp = null;
                            break;
                        } else {
                            tmp.add(guess);
                        }
                    }
                    if (tmp != null) guess_list.addAll(tmp);
                }
            }
        }

        if (guess != null) guess_list.add(guess);

        return guess_list;
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        PsiElement pre_type = guessTypesForPreType();
        if (pre_type == null) {
            return guess_list;
        }

        PsiElement guess = null;
        if (pre_type instanceof ALittleMethodNameDec) {
            guess = ((ALittleMethodNameDec)pre_type).guessType();
        } else if (pre_type instanceof ALittleClassDec) {
            guess = pre_type;
        } else if (pre_type instanceof ALittlePrimitiveType && pre_type.getText().equals("any")) {
            guess = myElement;
        } else if (pre_type instanceof ALittlePropertyValueMethodCallStat) {
            guess = myElement;
        } else if (pre_type instanceof ALittlePropertyValueBrackValueStat) {
            guess = myElement;
        } else if (pre_type instanceof ALittleGenericType) {
            ALittleGenericType dec = (ALittleGenericType)pre_type;
            if (dec.getGenericFunctorType() != null) {
                ALittleGenericFunctorType functor_dec = dec.getGenericFunctorType();
                if (functor_dec.getGenericFunctorReturnType() != null) {
                    List<ALittleAllType> all_type_list = functor_dec.getGenericFunctorReturnType().getAllTypeList();
                    if (all_type_list.size() > 0) {
                        guess = ALittleUtil.guessType(all_type_list.get(0));
                    }
                }
            }
        }

        if (guess != null) guess_list.add(guess);

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        return variants.toArray();
    }
}
