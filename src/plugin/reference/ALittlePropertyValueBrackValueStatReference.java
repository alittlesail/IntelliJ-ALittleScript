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

public class ALittlePropertyValueBrackValueStatReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    public ALittlePropertyValueBrackValueStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
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
                ALittleAllType all_type = list_type.getAllType();
                if (all_type != null) {
                    if (all_type.getPrimitiveType() != null) {
                        guess_list.add(all_type.getPrimitiveType());
                    } else if (all_type.getGenericType() != null) {
                        guess_list.add(all_type.getGenericType());
                    } else if (all_type.getCustomType() != null) {
                        PsiElement guess = all_type.getCustomType().getCustomTypeNameDec().guessType();
                        if (guess != null) guess_list.add(guess);
                    }
                }
            } else if (generic_type.getGenericMapType() != null) {
                ALittleGenericMapType map_type = generic_type.getGenericMapType();
                List<ALittleAllType> all_type_list = map_type.getAllTypeList();
                if (all_type_list.size() == 2) {
                    ALittleAllType all_type = all_type_list.get(1);
                    if (all_type != null) {
                        if (all_type.getPrimitiveType() != null) {
                            guess_list.add(all_type.getPrimitiveType());
                        } else if (all_type.getGenericType() != null) {
                            guess_list.add(all_type.getGenericType());
                        } else if (all_type.getCustomType() != null) {
                            PsiElement guess = all_type.getCustomType().getCustomTypeNameDec().guessType();
                            if (guess != null) guess_list.add(guess);
                        }
                    }
                }
            }
        // 对类的实例对象进行中括号取值，相当于调用了setter和getter
        // 这里直接返回自己，表示任意类型
        } else if (pre_type instanceof ALittleClassDec) {
            guess_list.add(myElement);
        // 这里直接返回自己，表示任意类型
        } else if (pre_type instanceof ALittlePropertyValueMethodCallStat) {
            guess_list.add(myElement);
        } else if (pre_type instanceof ALittlePropertyValueBrackValueStat) {
            guess_list.add(myElement);
        // 对于Any类型的值进行中括号取值
        // 这里直接返回自己，表示任意类型
        } else if (pre_type instanceof ALittlePrimitiveType && pre_type.getText().equals("any")) {
            guess_list.add(myElement);
        // 如果是命名域
        // 这里直接返回自己，表示任意类型
        } else if (pre_type instanceof ALittleNamespaceNameDec) {
            guess_list.add(myElement);
        // 如果是函数名，并且是getter，那么就返回对应的返回值
        } else if (pre_type instanceof ALittleMethodNameDec) {
            guess_list.add(((ALittleMethodNameDec)pre_type).guessType());
        }

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
