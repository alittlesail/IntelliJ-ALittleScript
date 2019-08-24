package plugin.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleLanguage;

public class ALittleTokenType extends IElementType {
    public ALittleTokenType(@NotNull @NonNls String debugName) {
        super(debugName, ALittleLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ALittleTokenType." + super.toString();
    }
}
