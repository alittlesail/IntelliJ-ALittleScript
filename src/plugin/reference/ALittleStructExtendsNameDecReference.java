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
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleStructExtendsNameDecReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;
    private String m_src_namespace;

    public ALittleStructExtendsNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());

        ALittleStructDec class_dec = (ALittleStructDec)element.getParent();
        ALittleStructExtendsNamespaceNameDec namespace_dec = class_dec.getStructExtendsNamespaceNameDec();
        if (namespace_dec != null)
            m_src_namespace = namespace_dec.getIdContent().getText();
        else
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

            if (element instanceof ALittleStructNameDec) {
                guess_list.add(element.getParent());
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleStructNameDec> dec_list = ALittleTreeChangeListener.findStructNameDecList(project, m_src_namespace, m_key);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleStructNameDec dec : dec_list) {
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
        List<LookupElement> variants = new ArrayList<>();

        // 查找对应命名域下的类名
        {
            List<ALittleStructNameDec> dec_list = ALittleTreeChangeListener.findStructNameDecList(project, m_src_namespace, "");
            for (ALittleStructNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 查找所有命名域
        if (!m_key.equals("IntellijIdeaRulezzz"))
        {
            List<ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (ALittleNamespaceNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
