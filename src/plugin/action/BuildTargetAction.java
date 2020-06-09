package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import plugin.alittle.FileHelper;
import plugin.alittle.SendLogRunnable;
import plugin.generate.ALittleTranslation;
import plugin.psi.ALittleFile;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BuildTargetAction extends AnAction {
    protected boolean mRebuild = false;

    private void generateDir(Project project, PsiManager psi_mgr, VirtualFile root) throws Exception {
        // 处理文件夹
        if (root.isDirectory()) {
            VirtualFile[] files = root.getChildren();
            if (files == null) return;
            for (VirtualFile file : files)
                generateDir(project, psi_mgr, file);
            return;
        }
        PsiFile psi_file = psi_mgr.findFile(root);
        if (!(psi_file instanceof ALittleFile))
            return;
        try {
            ALittleTranslation translation = ALittleTranslation.createTranslation(project);
            translation.generate((ALittleFile)psi_file, true);
        } catch (Exception e) {
            FileEditorManager.getInstance(psi_file.getProject()).openFile(psi_file.getVirtualFile(), true);
            throw e;
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // 获取当前项目
        Project project = event.getProject();
        if (project == null) return;
        // 获取选中的文件
        VirtualFile[] target_file_array = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (target_file_array == null) return;
        // 获取管理器
        PsiManager psi_mgr = PsiManager.getInstance(project);
        // 发送日志
        SendLogRunnable.SendLog("gen all target file");
        // 提示消息框
        Messages.showMessageDialog(project, "开始执行目标代码生成", "提示", Messages.getInformationIcon());

        // 创建文件夹
        try {
            if (mRebuild) {
                // 整理模块路径
                Map<String, Module> map = new HashMap<>();
                // 获取当前所有模块
                Module[] modules = ModuleManager.getInstance(project).getModules();
                // 遍历模块
                for (Module module : modules) {
                    map.put(FileHelper.getDirectoryName(module.getModuleFilePath(), true), module);
                }

                // 收集模块
                Set<Module> set = new HashSet<>();
                for (VirtualFile target_file : target_file_array) {
                    String path = target_file.getPath();
                    if (!path.endsWith("/") && !path.endsWith("\\")) path += "/";
                    Module module = map.get(path);
                    if (module != null) {
                        set.add(module);
                    }
                }

                // 重构涉及到的模块
                for (Module module : set) {
                    String path = FileHelper.getDirectoryName(module.getModuleFilePath(), false);
                    FileHelper.deepDeletePath(path);
                    File root_file_path = new File(path);
                    if (!root_file_path.exists() && !root_file_path.mkdirs())
                        throw new Exception("路径创建失败 path:" + root_file_path.getPath());
                }
            }

            for (VirtualFile targetFile : target_file_array) {
                generateDir(project, psi_mgr, targetFile);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "提示", Messages.getInformationIcon());
            return;
        }

        Messages.showMessageDialog(project, "代码生成成功", "提示", Messages.getInformationIcon());
    }
}
