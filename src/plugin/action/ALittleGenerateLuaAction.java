package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // 删除根目录并重新创建
        String root_path = project.getBasePath() + "/Script";
        delFolder(root_path);
        File root_file_path = new File(root_path);
        root_file_path.mkdirs();

        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ALittleFileType.INSTANCE, GlobalSearchScope.allScope(project));

        Messages.showMessageDialog(project, "开始执行lua代码生成", "提示", Messages.getInformationIcon());

        String error = null;
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (!(file instanceof ALittleFile)) continue;
            ALittleFile alittleFile = (ALittleFile)file;

            ALittleGenerateLua lua = new ALittleGenerateLua();
            error = lua.GenerateLua(alittleFile, true);
            if (error != null) {
                error = alittleFile.getName() + ":代码生成失败:" + error;
                break;
            }
        }

        if (error != null) {
            Messages.showMessageDialog(project, error, "错误", Messages.getErrorIcon());
        } else {
            Messages.showMessageDialog(project, "lua代码生成成功", "提示", Messages.getInformationIcon());
        }
    }
}
