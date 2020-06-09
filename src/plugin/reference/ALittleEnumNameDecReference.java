package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumDec;
import plugin.psi.ALittleEnumNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleEnumNameDecReference extends ALittleReference<ALittleEnumNameDec> {
    public ALittleEnumNameDecReference(@NotNull ALittleEnumNameDec element, TextRange textRange) {
        super(element, textRange);
        mKey = element.getText();
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return ((ALittleEnumDec) myElement.getParent()).guessTypes();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                PsiHelper.PsiElementType.ENUM_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
        List<ResolveResult> results = new ArrayList<>();
        for (PsiElement dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        final List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                PsiHelper.PsiElementType.ENUM_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, "", true);
        List<LookupElement> variants = new ArrayList<>();
        for (PsiElement dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.ENUM).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "枚举名不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
