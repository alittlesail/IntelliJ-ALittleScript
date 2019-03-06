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
import plugin.psi.ALittleClassNameDec;
import plugin.psi.ALittleFile;
import plugin.psi.ALittlePrimitiveType;
import plugin.psi.ALittleVarAssignNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittlePrimitiveTypeReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    public ALittlePrimitiveTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();
        guess_list.add(myElement);
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
        variants.add(LookupElementBuilder.create("int"));
        variants.add(LookupElementBuilder.create("double"));
        variants.add(LookupElementBuilder.create("bool"));
        variants.add(LookupElementBuilder.create("string"));
        variants.add(LookupElementBuilder.create("any"));
        return variants.toArray();
    }
}
