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

import java.util.HashMap;
import java.util.HashSet;

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
        // 获取当前项目
        Project project = event.getProject();
        if (project == null) return;
        // 获取选中的文件
        VirtualFile[] targetFileArray = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (targetFileArray == null) return;
        // 获取管理器
        PsiManager psiMgr = PsiManager.getInstance(project);
        // 发送日志
        SendLogRunnable.SendLog("gen all lua file");
        // 提示消息框
        Messages.showMessageDialog(project, "开始执行lua代码生成", "提示", Messages.getInformationIcon());

        // 创建文件夹
        try {
            if (mRebuild) {
                // 整理模块路径
                HashMap<String, Module> map = new HashMap<>();
                // 获取当前所有模块
                Module[] modules = ModuleManager.getInstance(project).getModules();
                // 遍历模块
                for (Module module : modules) {
                    map.put(FileHelper.calcModulePath(module), module);
                }

                // 收集模块
                HashSet<Module> set = new HashSet<>();
                for (VirtualFile targetFile : targetFileArray) {
                    String path = targetFile.getPath();
                    if (!path.endsWith("/") && !path.endsWith("\\"))
                        path += "/";
                    Module module = map.get(path);
                    if (module != null) {
                        set.add(module);
                    }
                }

                // 重构涉及到的模块
                for (Module module : set) {
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
