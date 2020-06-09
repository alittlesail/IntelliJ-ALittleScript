package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleIcons;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceDec;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleNamespaceNameDecReference extends ALittleReference<ALittleNamespaceNameDec> {
    public ALittleNamespaceNameDecReference(@NotNull ALittleNamespaceNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        Map<String, ALittleNamespaceNameDec> nameDecMap = ALittleTreeChangeListener.findNamespaceNameDecList(myElement.getProject(), mKey);
        for (ALittleNamespaceNameDec nameDec : nameDecMap.values()) {
            guessList.add(((ALittleNamespaceDec)nameDec.getParent()).guessType());
        }

        return guessList;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "命名域不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final Map<String, ALittleNamespaceNameDec> decMap = ALittleTreeChangeListener.findNamespaceNameDecList(project, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleNamespaceNameDec dec : decMap.values()) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        final Map<String, ALittleNamespaceNameDec> decMap = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
        List<LookupElement> variants = new ArrayList<>();
        for (final ALittleNamespaceNameDec dec : decMap.values()) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.NAMESPACE).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }
}
