package plugin.alittle;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.VirtualFile;
import plugin.component.StdLibraryProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

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

    // 获取模块路径
    public static String calcModulePath(Module module) throws Exception {
        String module_name = module.getName();
        String module_file_path = module.getModuleFilePath();
        String module_file_name = module_name + ".iml";
        if (!module_file_path.endsWith(module_file_name))
            throw new Exception("模块文件路径:" + module_file_path + "没有以:" + module_file_name + "结尾");

        return module_file_path.substring(0, module_file_path.length() - module_file_name.length());
    }

    // 重新构建一个空的文件夹
    public static void rebuildPath(String path) throws Exception {
        FileHelper.deepDeletePath(path);
        File root_file_path = new File(path);
        if (!root_file_path.exists() && !root_file_path.mkdirs())
            throw new Exception("路径创建失败 path:" + root_file_path.getPath());
    }

    // 获取输出路径
    public static String calcOutPath(Module module) throws Exception {
        CompilerModuleExtension extension = CompilerModuleExtension.getInstance(module);
        if (extension == null) {
            throw new Exception("该模块没有编译目录设置");
        }
        VirtualFile outFile = extension.getCompilerOutputPath();
        if (outFile == null) {
            throw new Exception("没有设置脚本生成目录");
        }
        String outPath = outFile.getPath();
        if (outPath.endsWith(File.separator)) return outPath;
        return outPath + File.separator;
    }

    // 获取lua脚本路径
    public static String calcScriptPath(Module module) throws Exception {
        if (StdLibraryProvider.isPluginSelf(module.getProject())) {
            return calcModulePath(module) + "resources/adapter" + File.separator;
        } else {
            String outPath = calcOutPath(module);
            if (module.getName().equals("Engine")) return outPath + "Engine/";
            return outPath + "Script/";
        }
    }

    // 计算文件路径
    public static String calcALittleRelPath(Module module, VirtualFile file) throws Exception {
        String module_base_path = calcModulePath(module);

        String file_path = file.getPath();
        if (!file_path.startsWith(module_base_path)) {
            throw new Exception("当前文件不在模块路径下:" + file_path);
        }

        String alittle_rel_path = file_path.substring(module_base_path.length());
        // 如果是插件项目本身就特殊处理
        if (StdLibraryProvider.isPluginSelf(module.getProject())) {
            if (!alittle_rel_path.startsWith("resources/std")) {
                throw new Exception("不支持该目录下的文件生成:" + file_path);
            }
            alittle_rel_path = alittle_rel_path.substring("resources/std/".length());
        // 其他模块必须
        } else {
            if (!alittle_rel_path.startsWith("src")) {
                throw new Exception("不支持该目录下的文件生成:" + file_path);
            }
            alittle_rel_path = alittle_rel_path.substring("src/".length());
        }

        String ext = "alittle";
        if (!alittle_rel_path.endsWith(ext)) {
            throw new Exception("要生成的代码文件后缀名必须是alittle:" + file_path);
        }
        return alittle_rel_path.substring(0, alittle_rel_path.length() - ext.length());
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
}
