package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewStatReference extends ALittleReference<ALittleOpNewStat> {
    public ALittleOpNewStatReference(@NotNull ALittleOpNewStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getCustomType() != null) {
            return myElement.getCustomType().guessTypes();
        } else if (myElement.getGenericType() != null) {
            return myElement.getGenericType().guessTypes();
        }

        return new ArrayList<>();
    }
}
