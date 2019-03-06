package plugin.psi;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.*;
import plugin.ALittleLanguage;

public class ALittleElementType extends IElementType {
    public ALittleElementType(@NotNull @NonNls String debugName) {
        super(debugName, ALittleLanguage.INSTANCE);
    }
}
