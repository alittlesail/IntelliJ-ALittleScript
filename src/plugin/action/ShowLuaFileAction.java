package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import plugin.alittle.FileHelper;
import plugin.psi.ALittleFile;

public class ShowLuaFileAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file == null) return;

        // 保存到文件
        FileIndexFacade facade = FileIndexFacade.getInstance(project);
        Module module = facade.getModuleForFile(file);
        if (module == null) {
            return;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile instanceof ALittleFile) {
            try {
                String alittleRelPath = FileHelper.calcALittleRelPath(module, file);
                String fullPath = FileHelper.calcScriptPath(module) + alittleRelPath + "lua";
                VirtualFile newFile = VirtualFileManager.getInstance().findFileByUrl("file://" + fullPath);
                if (newFile == null) return;
                FileEditorManager.getInstance(project).openFile(newFile, true);
            } catch (Exception ignored) {
            }
        }
    }
}
