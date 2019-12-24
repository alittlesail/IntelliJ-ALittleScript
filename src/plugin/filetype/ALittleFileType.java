package plugin.filetype;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.component.ALittleIcons;
import plugin.component.ALittleLanguage;

import javax.swing.*;

public class ALittleFileType extends LanguageFileType  {
    public static final ALittleFileType INSTANCE = new ALittleFileType();

    protected ALittleFileType() {
        super(ALittleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "ALittle File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ALittle Language File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "alittle";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ALittleIcons.FILE;
    }
}