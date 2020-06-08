package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuessException;
import plugin.module.ALittleConfig;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleLanguageNameDecReference extends ALittleReference<ALittleLanguageNameDec> {
    public ALittleLanguageNameDecReference(@NotNull ALittleLanguageNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    public void checkError() throws ALittleGuessException {
        String text = myElement.getText();
        if (!ALittleConfig.getConfig(myElement.getProject()).getTargetLanguageNameSet().contains(text))
            throw new ALittleGuessException(myElement, "不支持该目标语言");
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<LookupElement> variants = new ArrayList<>();

        for (String name : ALittleConfig.getConfig(myElement.getProject()).getTargetLanguageNameSet())
            variants.add(LookupElementBuilder.create(name));

        return variants.toArray();
    }
}
