package plugin.format;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class ALittleCodeStyleSettings extends CustomCodeStyleSettings {
    public boolean SPACE_AROUND_ARROW = true;
    public boolean SPACE_BEFORE_TYPE_REFERENCE_COLON = false;
    public boolean SPACE_AFTER_TYPE_REFERENCE_COLON = false;

    protected ALittleCodeStyleSettings(CodeStyleSettings container) {
        super("ALittleCodeStyleSettings", container);
    }
}

