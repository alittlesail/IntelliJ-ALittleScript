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

public class ALittleStructExtendsNameDecReference extends ALittleReference {
    public ALittleStructExtendsNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);

        ALittleStructDec classDec = (ALittleStructDec)element.getParent();
        ALittleStructExtendsNamespaceNameDec namespace_dec = classDec.getStructExtendsNamespaceNameDec();
        if (namespace_dec != null)
            mNamespace = namespace_dec.getIdContent().getText();
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleStructNameDec) {
                guessList.add(((ALittleStructDec)element.getParent()).guessType());
            }
        }
        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleStructNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<LookupElement> variants = new ArrayList<>();

        // 查找对应命名域下的类名
        {
            List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, "");
            for (ALittleStructNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 查找所有命名域
        if (!mKey.equals("IntellijIdeaRulezzz"))
        {
            List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (ALittleNamespaceNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
