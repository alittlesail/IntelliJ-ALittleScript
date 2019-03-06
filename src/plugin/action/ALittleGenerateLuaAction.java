package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
    public static void delFolder(String folderPath) {
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
    public static boolean delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] tempList = file.list();
        File temp = null;
        boolean flag = false;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
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

        Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ALittleFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));

        System.out.println("执行开始");
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            if (!(file instanceof ALittleFile)) continue;
            ALittleFile alittleFile = (ALittleFile)file;

            ALittleGenerateLua lua = new ALittleGenerateLua();
            String error = lua.GenerateLua(alittleFile, true);
            if (error == null) {
                System.out.println(alittleFile.getName() + ":代码生成成功");
            } else {
                System.out.println(alittleFile.getName() + ":代码生成失败:" + error);
            }
        }


        System.out.println("执行完毕");
    }
}
