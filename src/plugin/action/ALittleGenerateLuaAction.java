package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import plugin.ALittleGenerateLua;
import plugin.alittle.FileHelper;
import plugin.alittle.SendLogRunnable;
import plugin.psi.ALittleFile;

public class ALittleGenerateLuaAction extends AnAction {
    private void generateDir(PsiManager psi_mgr, VirtualFile root) throws Exception {
        if (root.isDirectory()) {
            VirtualFile[] files = root.getChildren();
            if (files != null) {
                for (VirtualFile file : files) {
                    generateDir(psi_mgr, file);
                }
            }
        } else {
            PsiFile psi_file = psi_mgr.findFile(root);
            if (psi_file instanceof ALittleFile) {
                ALittleGenerateLua lua = new ALittleGenerateLua();
                lua.GenerateLua((ALittleFile)psi_file, true);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        SendLogRunnable.SendLog("gen all lua file");
        Messages.showMessageDialog(project, "开始执行lua代码生成", "提示", Messages.getInformationIcon());

        // 创建文件夹
        try {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                FileHelper.rebuildPath(FileHelper.calcScriptPath(module));
            }

            PsiManager psi_mgr = PsiManager.getInstance(project);
            VirtualFile[] roots = ProjectRootManager.getInstance(project).getContentRoots();
            for (VirtualFile root : roots) {
                generateDir(psi_mgr, root);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "提示", Messages.getInformationIcon());
            return;
        }

        Messages.showMessageDialog(project, "代码生成成功", "提示", Messages.getInformationIcon());
    }
}
