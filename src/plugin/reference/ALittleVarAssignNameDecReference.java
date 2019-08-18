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

public class ALittleVarAssignNameDecReference extends ALittleReference {
    public ALittleVarAssignNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            if (element instanceof ALittlePrimitiveType) {
                guessList.add(((ALittlePrimitiveType)element).guessType());
            } else if (element instanceof ALittleAutoType) {
                guessList.add(((ALittleAutoType)element).guessType());
            } else if (element instanceof ALittleGenericType) {
                guessList.add(((ALittleGenericType)element).guessType());
            } else if (element instanceof ALittleCustomTypeNameDec) {
                guessList.add(((ALittleCustomTypeNameDec)element).guessType());
            }
        }

        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        ALittleAllType allType = null;
        ALittleAutoType auto_type = null;
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleVarAssignPairDec) {
            ALittleVarAssignPairDec var_assign_pair = (ALittleVarAssignPairDec)myElement.getParent();
            allType = var_assign_pair.getAllType();
            auto_type = var_assign_pair.getAutoType();
        } else if (parent instanceof ALittleForPairDec) {
            ALittleForPairDec pair_dec = (ALittleForPairDec)parent;
            allType = pair_dec.getAllType();
            auto_type = pair_dec.getAutoType();
        }
        if (allType != null) {
            if (allType.getPrimitiveType() != null) {
                results.add(new PsiElementResolveResult(allType.getPrimitiveType()));
            } else if (allType.getGenericType() != null) {
                results.add(new PsiElementResolveResult(allType.getGenericType()));
            } else if (allType.getCustomType() != null) {
                results.add(new PsiElementResolveResult(allType.getCustomType().getCustomTypeNameDec()));
            }
        }
        if (auto_type != null) {
            results.add(new PsiElementResolveResult(auto_type));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }
}
