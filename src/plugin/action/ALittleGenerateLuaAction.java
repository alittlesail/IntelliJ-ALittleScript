package plugin.action;

import com.intellij.bootRuntime.command.Delete;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import plugin.ALittleFileType;
import plugin.ALittleGenerateLua;
import plugin.alittle.SendLogRunnable;
import plugin.psi.ALittleFile;

import java.io.File;
import java.util.Collection;

public class ALittleGenerateLuaAction extends AnAction {
    // 删除文件夹
    private static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            File myFilePath = new File(folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    private static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) return;
        if (!file.isDirectory()) return;

        String[] tempList = file.list();
        if (tempList == null) return;

        File temp;
        for (String file_name : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + file_name);
            } else {
                temp = new File(path + File.separator + file_name);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + file_name);//先删除文件夹里面的文件
                delFolder(path + "/" + file_name);//再删除空文件夹
            }
        }
    }

    public String DeleteDir(Project project, VirtualFile file) {
        FileIndexFacade facade = FileIndexFacade.getInstance(project);
        Module module = facade.getModuleForFile(file);
        if (module == null) return "DeleteDir:module获取失败";

        String out_path = CompilerPaths.getModuleOutputPath(module, false);
        if (out_path == null) return "DeleteDir:CompilerPaths.getModuleOutputPath调用失败";

        String end_path = "production/" + module.getName();
        if (!out_path.endsWith(end_path)) return "DeleteDir:end_path:" + end_path + "不是out_path:" + out_path + " 的结尾";
        out_path = out_path.substring(0, out_path.length() - end_path.length());

        // 删除根目录并重新创建
        String root_path = out_path + "Script";
        delFolder(root_path);
        File root_file_path = new File(root_path);
        root_file_path.mkdirs();

        // 删除根目录并重新创建
        root_path = out_path + "Protocol";
        delFolder(root_path);
        root_file_path = new File(root_path);
        root_file_path.mkdirs();

        return null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ALittleFileType.INSTANCE, GlobalSearchScope.allScope(project));

        SendLogRunnable.SendLog("gen all lua file");
        Messages.showMessageDialog(project, "开始执行lua代码生成", "提示", Messages.getInformationIcon());

        boolean delete_dir = false;

        String error = null;
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (!(file instanceof ALittleFile)) continue;
            ALittleFile alittleFile = (ALittleFile)file;

            if (!delete_dir) {
                error = DeleteDir(project, virtualFile);
                if (error != null) {
                    break;
                }
                delete_dir = true;
            }

            ALittleGenerateLua lua = new ALittleGenerateLua();
            error = lua.GenerateLua(alittleFile, true);
            if (error != null) {
                Messages.showMessageDialog(project, alittleFile.getName() + ":代码生成失败:" + error, "提示", Messages.getInformationIcon());
                break;
            }
        }

        if (error == null) {
            Messages.showMessageDialog(project, "代码生成成功", "提示", Messages.getInformationIcon());
        }
    }
}
