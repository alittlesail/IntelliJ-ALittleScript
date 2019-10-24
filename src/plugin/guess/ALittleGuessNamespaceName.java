package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.Map;

public class ALittleGuessNamespaceName extends ALittleGuess {
    private @NotNull String mNamespaceName;

    public @NotNull ALittleNamespaceNameDec element;
    public ALittleGuessNamespaceName(@NotNull String namespaceName, @NotNull ALittleNamespaceNameDec e) {
        isRegister = PsiHelper.isRegister(e);
        mNamespaceName = namespaceName;
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName;
    }

    @Override
    public boolean isChanged() {
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessNamespaceName guess = new ALittleGuessNamespaceName(mNamespaceName, element);
        guess.UpdateValue();
        return guess;
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
}
