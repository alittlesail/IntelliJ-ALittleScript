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
