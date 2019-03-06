package plugin.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import plugin.ALittleIcons;

/**
 *
 * Created by tangzx on 2016/12/24.
 */
public class CreateALittleFileAction extends CreateFileFromTemplateAction implements DumbAware {
    private static final String CREATE_LUA_FILE = "New ALittle File";

    public CreateALittleFileAction() {
        super(CREATE_LUA_FILE, "", ALittleIcons.FILE);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(CREATE_LUA_FILE)
                .addKind("Source File", ALittleIcons.FILE, "NewALittle.alittle");
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s, String s1) {
        return CREATE_LUA_FILE;
    }
}

