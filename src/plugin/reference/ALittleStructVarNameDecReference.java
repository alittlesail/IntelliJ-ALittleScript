package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleStructVarNameDecReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;
    private String m_src_namespace;

    public ALittleStructVarNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        m_src_namespace = ALittleUtil.getNamespaceName((ALittleFile) element.getContainingFile());
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

            if (element instanceof ALittlePrimitiveType) {
                guess_list.add(element);
            } else if (element instanceof ALittleGenericType) {
                guess_list.add(element);
            } else if (element instanceof ALittleCustomTypeNameDec) {
                ALittleCustomTypeNameDec dec = (ALittleCustomTypeNameDec) element;
                PsiElement guess = dec.guessType();
                if (guess != null) guess_list.add(guess);
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        ALittleStructVarDec class_var_dec = (ALittleStructVarDec)myElement.getParent();
        ALittleAllType all_type = class_var_dec.getAllType();
        if (all_type != null) {
            if (all_type.getPrimitiveType() != null) {
                results.add(new PsiElementResolveResult(all_type.getPrimitiveType()));
            } else if (all_type.getGenericType() != null) {
                results.add(new PsiElementResolveResult(all_type.getGenericType()));
            } else if (all_type.getCustomType() != null) {
                results.add(new PsiElementResolveResult(all_type.getCustomType().getCustomTypeNameDec()));
            }
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
        return variants.toArray();
    }
}
