package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleStructExtendsNamespaceNameDecReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    public ALittleStructExtendsNamespaceNameDecReference(@NotNull PsiElement element, TextRange textRange) {
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

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleNamespaceNameDec) {
                guess_list.add(element.getParent());
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, m_key);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleNamespaceNameDec dec : dec_list) {
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
        Project project = myElement.getProject();
        final List<ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
        List<LookupElement> variants = new ArrayList<>();
        for (final ALittleNamespaceNameDec dec : dec_list) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.FILE).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }
}
