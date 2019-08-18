package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewStatReference extends ALittleReference {
    public ALittleOpNewStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOpNewStat opNewStat = (ALittleOpNewStat)myElement;

        if (opNewStat.getCustomType() != null) {
            return opNewStat.getCustomType().guessTypes();
        } else if (opNewStat.getGenericType() != null) {
            return opNewStat.getGenericType().guessTypes();
        }

        return new ArrayList<>();
    }
}
