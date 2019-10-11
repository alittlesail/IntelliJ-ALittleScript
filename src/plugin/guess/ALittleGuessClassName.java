package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassNameDec;

public class ALittleGuessClassName extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mClassName;

    public @NotNull ALittleClassNameDec element;
    public ALittleGuessClassName(@NotNull String namespaceName, @NotNull String className, @NotNull ALittleClassNameDec e) {
        mNamespaceName = namespaceName;
        mClassName = className;
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mClassName;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
