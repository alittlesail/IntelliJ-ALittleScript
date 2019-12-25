package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumNameDec;

import java.util.Map;

public class ALittleGuessEnumName extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mEnumName;

    public @NotNull ALittleEnumNameDec element;
    public ALittleGuessEnumName(@NotNull String namespaceName, @NotNull String enumName, @NotNull ALittleEnumNameDec e) {
        isRegister = PsiHelper.isRegister(e);
        mNamespaceName = namespaceName;
        mEnumName = enumName;
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mEnumName;
    }

    @Override
    public boolean isChanged() {
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessEnumName guess = new ALittleGuessEnumName(mNamespaceName, mEnumName, element);
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
