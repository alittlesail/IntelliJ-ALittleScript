package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumDec;

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
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
