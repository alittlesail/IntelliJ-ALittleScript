package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import plugin.ALittleFileType;
import plugin.ALittleGenerateLua;
import plugin.alittle.FileHelper;
import plugin.alittle.SendLogRunnable;
import plugin.psi.ALittleFile;

import java.util.Collection;

public class ALittleGenerateLuaAction extends AnAction {
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
                FileHelper.rebuildPath(FileHelper.calcProtocolPath(module));
                FileHelper.rebuildPath(FileHelper.calcCPPProtoPath(module));
            }

            // 获取所有文件
            Collection<VirtualFile> virtual_files = FileTypeIndex.getFiles(ALittleFileType.INSTANCE, GlobalSearchScope.allScope(project));

            // 遍历文件，逐个生成
            for (VirtualFile virtual_file : virtual_files) {
                PsiFile file = PsiManager.getInstance(project).findFile(virtual_file);
                // 过滤掉非ALittle文件
                if (!(file instanceof ALittleFile)) continue;
                ALittleGenerateLua lua = new ALittleGenerateLua();
                lua.GenerateLua((ALittleFile)file, true);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "提示", Messages.getInformationIcon());
            return;
        }

        Messages.showMessageDialog(project, "代码生成成功", "提示", Messages.getInformationIcon());
    }
}
