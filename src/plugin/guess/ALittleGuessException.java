package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ALittleGuessException extends Exception {
    private @NotNull String mError;
    private PsiElement mElement;

    public ALittleGuessException(PsiElement element, @NotNull String error) {
        mElement = element;
        mError = error;
    }

    @NotNull
    public String getError() { return mError; }

    public PsiElement getElement() { return mElement; }
}
