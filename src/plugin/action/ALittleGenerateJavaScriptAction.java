package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import plugin.ALittleFileType;
import plugin.ALittleGenerateJavaScript;
import plugin.ALittleGenerateLua;
import plugin.psi.ALittleFile;

import java.io.File;
import java.util.Collection;

public class ALittleGenerateJavaScriptAction extends AnAction {
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

    public boolean DeleteDir(Project project, VirtualFile file) {
        FileIndexFacade facade = FileIndexFacade.getInstance(project);
        Module module = facade.getModuleForFile(file);
        if (module == null) return false;

        String out_path = CompilerPaths.getModuleOutputPath(module, false);
        if (out_path == null) return false;

        String end_path = "production/" + module.getName();
        if (out_path.endsWith(end_path)) {
            out_path = out_path.substring(0, out_path.length() - end_path.length());
        }

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

        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // 删除根目录并重新创建
        String root_path = project.getBasePath() + "/JavaScript";
        delFolder(root_path);
        File root_file_path = new File(root_path);
        root_file_path.mkdirs();

        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ALittleFileType.INSTANCE, GlobalSearchScope.allScope(project));

        Messages.showMessageDialog(project, "开始执行JavaScript代码生成", "提示", Messages.getInformationIcon());

        boolean delete_dir = false;

        String error = null;
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (!(file instanceof ALittleFile)) continue;
            ALittleFile alittleFile = (ALittleFile)file;

            if (!delete_dir) {
                if (!DeleteDir(project, virtualFile)) {
                    error = "Script和Protocol文件夹删除失败!";
                    break;
                }
                delete_dir = true;
            }

            ALittleGenerateJavaScript js = new ALittleGenerateJavaScript();
            error = js.GenerateJavaScript(alittleFile, true);
            if (error != null) {
                error = alittleFile.getName() + ":代码生成失败:" + error;
                break;
            }
        }

        if (error != null) {
            Messages.showMessageDialog(project, error, "错误", Messages.getErrorIcon());
        } else {
            Messages.showMessageDialog(project, "JavaScript代码生成成功", "提示", Messages.getInformationIcon());
        }
    }
}
