package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.psi.ALittleClassDec;
import plugin.psi.ALittleClassNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassNameDecReference extends ALittleReference {
    public ALittleClassNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleClassNameDec) {
                guessList.add(((ALittleClassDec)element.getParent()).guessType());
            }
        }
        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleClassNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, "");
        List<LookupElement> variants = new ArrayList<>();
        for (ALittleClassNameDec dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.CLASS).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }
}
