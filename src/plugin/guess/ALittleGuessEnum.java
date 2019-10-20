package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumDec;

import java.util.Map;

public class ALittleGuessEnum extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mEnumName;

    public @NotNull ALittleEnumDec element;
    public ALittleGuessEnum(@NotNull String namespaceName, @NotNull String enumName, @NotNull ALittleEnumDec e) {
        mNamespaceName = namespaceName;
        mEnumName = enumName;
        element = e;
    }

    @NotNull
    public String GetNamespaceName() {
        return mNamespaceName;
    }

    @NotNull
    public String GetEnumName() {
        return mEnumName;
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
    public boolean NeedReplace() {
        return false;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessEnum guess = new ALittleGuessEnum(mNamespaceName, mEnumName, element);
        guess.UpdateValue();
        return guess;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        return this;
    }
}
