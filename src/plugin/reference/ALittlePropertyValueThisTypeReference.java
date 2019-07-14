package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;
import com.intellij.openapi.util.Key;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueThisTypeReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private ALittleClassDec m_class_dec = null;
    private ALittleClassCtorDec m_class_ctor_dec = null;
    private ALittleClassSetterDec m_class_setter_dec = null;
    private ALittleClassMethodDec m_class_method_dec = null;
    private ALittleClassStaticDec m_class_static_dec = null;
    private ALittleGlobalMethodDec m_global_method_dec = null;
    private String m_src_namespace;

    public ALittlePropertyValueThisTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
    }

    private void reloadInfo() {
        m_class_dec = null;
        m_class_ctor_dec = null;
        m_class_setter_dec = null;
        m_class_method_dec = null;
        m_class_static_dec = null;
        m_src_namespace = "";

        PsiElement parent = myElement;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleClassDec) {
                m_class_dec = (ALittleClassDec)parent;
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                m_class_ctor_dec = (ALittleClassCtorDec)parent;
            } else if (parent instanceof ALittleClassSetterDec) {
                m_class_setter_dec = (ALittleClassSetterDec)parent;
            } else if (parent instanceof ALittleClassMethodDec) {
                m_class_method_dec = (ALittleClassMethodDec)parent;
            } else if (parent instanceof ALittleClassStaticDec) {
                m_class_static_dec = (ALittleClassStaticDec)parent;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                m_global_method_dec = (ALittleGlobalMethodDec)parent;
            }

            parent = parent.getParent();
        }

        m_src_namespace = ALittleUtil.getNamespaceName((ALittleFile) myElement.getContainingFile());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleClassDec) {
                guess_list.add(element);
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ALittleClassDec> dec_list = new ArrayList<>();
        if (m_class_dec != null && m_global_method_dec == null && m_class_static_dec == null) {
            dec_list.add(m_class_dec);
        }
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleClassDec dec : dec_list) {
            results.add(new PsiElementResolveResult(dec));
        }
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
        variants.add(LookupElementBuilder.create("this").withCaseSensitivity(false));
        return variants.toArray();
    }
}
