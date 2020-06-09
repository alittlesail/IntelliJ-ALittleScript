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
import plugin.module.ALittleConfig;
import plugin.psi.ALittleFile;

public class ShowTargetFileAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (file == null) return;

        // 保存到文件
        FileIndexFacade facade = FileIndexFacade.getInstance(project);
        Module module = facade.getModuleForFile(file);
        if (module == null) return;

        PsiFile psi_file = PsiManager.getInstance(project).findFile(file);
        if (!(psi_file instanceof ALittleFile)) return;
        try {
            String full_path = FileHelper.calcTargetFullPath(FileHelper.getDirectoryName(module.getModuleFilePath(), true)
                    , file.getPath(), ALittleConfig.getConfig(project).getTargetExt());
            VirtualFile new_file = VirtualFileManager.getInstance().findFileByUrl("file://" + full_path);
            if (new_file == null) return;
            FileEditorManager.getInstance(project).openFile(new_file, true);
        } catch (Exception ignored) {
        }
    }
}
