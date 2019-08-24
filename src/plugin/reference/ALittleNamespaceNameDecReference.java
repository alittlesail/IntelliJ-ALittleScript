package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceDec;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleNamespaceNameDecReference extends ALittleReference<ALittleNamespaceNameDec> {
    public ALittleNamespaceNameDecReference(@NotNull ALittleNamespaceNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        List<ALittleNamespaceNameDec> nameDecList = ALittleTreeChangeListener.findNamespaceNameDecList(myElement.getProject(), mKey);
        for (ALittleNamespaceNameDec nameDec : nameDecList) {
            guessList.add(((ALittleNamespaceDec)nameDec.getParent()).guessType());
        }

        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "命名域不能以3个下划线开头");
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleNamespaceNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        final List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
        List<LookupElement> variants = new ArrayList<>();
        for (final ALittleNamespaceNameDec dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.NAMESPACE).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }
}
