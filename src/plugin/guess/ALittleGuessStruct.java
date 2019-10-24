package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructDec;

import java.util.Map;

public class ALittleGuessStruct extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mStructName;

    public @NotNull ALittleStructDec element;
    public ALittleGuessStruct(@NotNull String namespaceName, @NotNull String structName, @NotNull ALittleStructDec e) {
        isRegister = PsiHelper.isRegister(e);
        mNamespaceName = namespaceName;
        mStructName = structName;
        element = e;
    }

    @NotNull
    public String GetNamespaceName() {
        return mNamespaceName;
    }

    @NotNull
    public String GetStructName() {
        return mStructName;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mStructName;
    }

    @Override
    public boolean isChanged() {
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessStruct guess = new ALittleGuessStruct(mNamespaceName, mStructName, element);
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
