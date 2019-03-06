package plugin.format;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.Nullable;

public class ALittleCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @Override
    public String getConfigurableDisplayName() {
        return "alittle.title";
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new ALittleCodeStyleSettings(settings);
    }
}
