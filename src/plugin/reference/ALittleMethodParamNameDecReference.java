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
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            if (element instanceof ALittlePrimitiveType) {
                guessList.add(((ALittlePrimitiveType)element).guessType());
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
