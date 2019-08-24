package plugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import plugin.filetype.ALittleFileType;
import plugin.component.ALittleLanguage;

import javax.swing.*;

public class ALittleFile extends PsiFileBase {
    public ALittleFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ALittleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ALittleFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ALittle File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}