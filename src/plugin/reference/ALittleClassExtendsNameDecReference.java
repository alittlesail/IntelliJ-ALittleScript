package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassExtendsNameDecReference extends ALittleReference {
    public ALittleClassExtendsNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);

        // 如果有定义命名域，那么就获取指定的命名域
        ALittleClassDec classDec = (ALittleClassDec)element.getParent();
        ALittleClassExtendsNamespaceNameDec namespace_dec = classDec.getClassExtendsNamespaceNameDec();
        if (namespace_dec != null) {
            mNamespace = namespace_dec.getIdContent().getText();
        }
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleClassNameDec) {
                guessList.add(element.getParent());
            }
        }
        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        // 获取指定的类
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
        List<LookupElement> variants = new ArrayList<>();

        // 查找对应命名域下的类名
        {
            List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, "");
            for (ALittleClassNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
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
