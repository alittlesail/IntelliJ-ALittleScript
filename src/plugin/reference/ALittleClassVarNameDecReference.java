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

public class ALittleClassVarNameDecReference extends ALittleReference {
    public ALittleClassVarNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guessList = new ArrayList<>();

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            if (element instanceof ALittlePrimitiveType) {
                guessList.add(element);
            } else if (element instanceof ALittleGenericType) {
                guessList.add(element);
            } else if (element instanceof ALittleCustomTypeNameDec) {
                ALittleCustomTypeNameDec dec = (ALittleCustomTypeNameDec) element;
                PsiElement guess = dec.guessType();
                if (guess != null) guessList.add(guess);
            }
        }

        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        ALittleClassVarDec classVarDec = (ALittleClassVarDec)myElement.getParent();
        ALittleAllType allType = classVarDec.getAllType();
        if (allType != null) {
            if (allType.getPrimitiveType() != null) {
                results.add(new PsiElementResolveResult(allType.getPrimitiveType()));
            } else if (allType.getGenericType() != null) {
                results.add(new PsiElementResolveResult(allType.getGenericType()));
            } else if (allType.getCustomType() != null) {
                results.add(new PsiElementResolveResult(allType.getCustomType().getCustomTypeNameDec()));
            }
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        return variants.toArray();
    }
}
