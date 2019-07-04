package plugin.alittle;

import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;

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

    // 重新构建一个空的文件夹
    public static void rebuildPath(String path) throws Exception {
        FileHelper.deepDeletePath(path);
        File root_file_path = new File(path);
        if (!root_file_path.exists() && !root_file_path.mkdirs())
            throw new Exception("路径创建失败 path:" + root_file_path.getPath());
    }

    // 获取输出路径
    public static String calcOutPath(Module module) throws Exception {
        String out_path = CompilerPaths.getModuleOutputPath(module, false);
        if (out_path == null) throw new Exception("CompilerPaths.getModuleOutputPath调用失败，是不是没有设置out目录");
        if (out_path.endsWith(File.separator)) return out_path;
        return out_path + File.separator;
    }

    // 获取lua脚本路径
    public static String calcScriptPath(Module module) throws Exception {
        String out_path = calcOutPath(module);
        if (module.getName().equals("Engine")) return out_path + "Engine/";
        return out_path + "Script/";
    }

    // 获取lua协议路径
    public static String calcProtocolPath(Module module) throws Exception {
        String out_path = calcOutPath(module);
        return out_path + "Protocol/";
    }

    // 获取C++协议路径
    public static String calcCPPProtoPath(Module module) throws Exception {
        String out_path = calcOutPath(module);
        return out_path + "CPPProto/";
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
