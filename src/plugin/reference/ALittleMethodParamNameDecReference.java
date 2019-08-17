package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodParamNameDecReference extends ALittleReference {
    public ALittleMethodParamNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
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
        ALittleMethodParamOneDec param_one_dec = (ALittleMethodParamOneDec)myElement.getParent();
        List<ResolveResult> results = new ArrayList<>();
        ALittleAllType allType = param_one_dec.getMethodParamTypeDec().getAllType();
        if (allType.getPrimitiveType() != null) {
            results.add(new PsiElementResolveResult(allType.getPrimitiveType()));
        } else if (allType.getGenericType() != null) {
            results.add(new PsiElementResolveResult(allType.getGenericType()));
        } else if (allType.getCustomType() != null) {
            results.add(new PsiElementResolveResult(allType.getCustomType().getCustomTypeNameDec()));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }
}
