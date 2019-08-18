package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleValueStatParen;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueStatParenReference extends ALittleReference {
    public ALittleValueStatParenReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStatParen valueStatParen = (ALittleValueStatParen)myElement;
        return valueStatParen.getValueStat().guessTypes();
    }
}
