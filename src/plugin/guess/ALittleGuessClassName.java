package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassNameDec;

import java.util.Map;

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
    public boolean NeedReplace() {
        return false;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        return this;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessClassName guess = new ALittleGuessClassName(mNamespaceName, mClassName, element);
        guess.UpdateValue();
        return guess;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mClassName;
    }

    @Override
    public boolean isChanged() {
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
