package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleInstanceNameDecReference extends ALittleReference {
    public ALittleInstanceNameDecReference(@NotNull PsiElement element, TextRange textRange) {
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
        Project project = myElement.getProject();
        List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleClassNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        ALittleInstanceDec instance_dec = (ALittleInstanceDec)myElement.getParent();
        ALittleAllType allType = instance_dec.getAllType();
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
}
