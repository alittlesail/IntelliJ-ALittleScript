package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import plugin.generate.ALittleGenerateLua;
import plugin.alittle.FileHelper;
import plugin.alittle.SendLogRunnable;
import plugin.psi.ALittleFile;

import java.util.HashSet;
import java.util.Set;

public class BuildLuaAction extends AnAction {
    protected boolean mRebuild = false;

    private void generateDir(PsiManager psiMgr, VirtualFile root) throws Exception {
        if (root.isDirectory()) {
            VirtualFile[] files = root.getChildren();
            if (files != null) {
                for (VirtualFile file : files) {
                    generateDir(psiMgr, file);
                }
            }
        } else {
            PsiFile psiFile = psiMgr.findFile(root);
            if (psiFile instanceof ALittleFile) {
                ALittleGenerateLua lua = new ALittleGenerateLua();
                lua.GenerateLua((ALittleFile)psiFile, mRebuild, true);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;
        VirtualFile[] targetFileArray = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (targetFileArray == null) return;

        PsiManager psiMgr = PsiManager.getInstance(project);
        SendLogRunnable.SendLog("gen all lua file");
        Messages.showMessageDialog(project, "开始执行lua代码生成", "提示", Messages.getInformationIcon());

        // 创建文件夹
        try {
            if (mRebuild) {
                // 需要重新构建的模块
                Set<Module> moduleSet = new HashSet<>();

                // 检查是否有模块在里面
                for (VirtualFile targetFile : targetFileArray) {
                    String targetFilePath = targetFile.getPath();
                    Module[] modules = ModuleManager.getInstance(project).getModules();
                    for (Module module : modules) {
                        VirtualFile file = module.getModuleFile();
                        if (file == null) continue;
                        file = file.getParent();
                        if (file == null) continue;
                        if (file.getPath().equals(targetFilePath)) {
                            moduleSet.add(module);
                        }
                    }
                }

                // 重构涉及到的模块
                for (Module module : moduleSet) {
                    FileHelper.rebuildPath(FileHelper.calcScriptPath(module));
                }
            }

            for (VirtualFile targetFile : targetFileArray) {
                generateDir(psiMgr, targetFile);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "提示", Messages.getInformationIcon());
            return;
        }

        Messages.showMessageDialog(project, "代码生成成功", "提示", Messages.getInformationIcon());
    }
}
