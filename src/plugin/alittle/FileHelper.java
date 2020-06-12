package plugin.alittle;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileHelper {
    // 删除文件夹
    public static void deepDeletePath(String path) throws Exception {
        // 如果是文件，那么就直接删除掉
        File root = new File(path);
        if (!root.exists()) return;
        if (!root.isDirectory()) {
            if (!root.delete())
                throw new Exception("文件删除失败 path:" + root.getPath());
            return;
        }

        // 获取子级列表
        String[] list = root.list();
        if (list != null) {
            File file;
            for (String file_name : list) {
                if (path.endsWith(File.separator)) {
                    file = new File(path + file_name);
                } else {
                    file = new File(path + File.separator + file_name);
                }
                // 如果是目录，那么进行深度删除
                if (file.isDirectory()) {
                    deepDeletePath(path + "/" + file_name);//先删除文件夹里面的文件
                    // 如果是文件，那么就直接删除
                } else {
                    if (!file.delete())
                        throw new Exception("文件删除失败 path:" + file.getPath());
                }
            }
        }

        if (!root.delete())
            throw new Exception("文件夹删除失败 path:" + root.getPath());
    }

    // 删除文件夹
    public static void getDeepFilePaths(File info, String parent_path, List<String> result) {
        if (!info.exists()) return;

        // 获取子级列表
        File[] list = info.listFiles();
        if (list == null) return;

        for (File file : list) {
            if (file.isDirectory()) {
                getDeepFilePaths(file, parent_path + file.getName() + "/", result);
            } else {
                result.add(parent_path + file.getName());
            }
        }
    }

    // 创建文件并写入
    public static void writeFile(String path, String content) throws Exception {
        File file = new File(path);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new Exception("文件夹创建失败:" + file.getParentFile().getPath());
        FileOutputStream file_out = new FileOutputStream(new File(path));
        file_out.write(content.getBytes(StandardCharsets.UTF_8));
        file_out.close();
    }

    // 获取目标根路径
    public static String calcRootFullPath(String module_path, String ext) {
        String out_pre = "";
        if (ext.equals("js")) out_pre = "JS";
        return module_path + out_pre + "Script/";
    }

    // 改变路径
    public static String changeExtension(String path, String ext) {
        int index = path.lastIndexOf('.');
        if (index < 0) return path;
        return path.substring(0, index + 1) + ext;
    }

    // 获取路径名
    public static String getDirectoryName(String path, boolean include_split) {
        int index1 = path.lastIndexOf('/');
        int index2 = path.lastIndexOf('\\');

        if (index1 < 0) {
            if (index2 < 0) return path;
            if (include_split) return path.substring(0, index2 + 1);
            return path.substring(0, index2);
        }

        if (index2 < 0) {
            if (include_split) return path.substring(0, index1 + 1);
            return path.substring(0, index1);
        }

        if (include_split) return path.substring(0, Math.max(index1, index2) + 1);
        return path.substring(0, Math.max(index1, index2));
    }

    // 获取目标文件路径
    public static String calcTargetFullPath(String module_path, String ali_full_path, String ext) throws Exception {
        String ali_rel_path = changeExtension(ali_full_path.substring(module_path.length()), ext);
        if (!ali_rel_path.startsWith("src/"))
            throw new Exception("请把代码文件工程目录下的src文件夹中:" + module_path + "src/");

        return calcRootFullPath(module_path, ext) + ali_rel_path.substring("src/".length());
    }

    // 根据元素来说去模块路径
    @NotNull
    public static String calcModulePath(PsiElement element, boolean include_split) {
        // 保存到文件
        FileIndexFacade facade = FileIndexFacade.getInstance(element.getProject());
        Module module = facade.getModuleForFile(element.getContainingFile().getOriginalFile().getVirtualFile());
        if (module == null) return "";
        return getDirectoryName(module.getModuleFilePath(), include_split);
    }
}
