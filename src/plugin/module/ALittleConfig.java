package plugin.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.FileHelper;

import java.io.*;

public class ALittleConfig {
    private static Key<ALittleConfig> sALittleConfigKey = new Key<>("ALittleConfig");

    private Module mModule;

    private @NotNull String mTargetLanguage = "";

    public ALittleConfig(Module module) {
        mModule = module;
        module.putUserData(sALittleConfigKey, this);
        load();
    }

    public void load() {
        try {
            String path = FileHelper.calcModulePath(mModule) + "config.ini";
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                int index = line.indexOf('=');
                if (index == -1) continue;
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                if (key.equals("target_language")) {
                    mTargetLanguage = value;
                }
            }
            br.close();
            reader.close();
        } catch (Exception ignored) {
            mTargetLanguage = "Lua";
        }
    }

    public void save() {
        try {
            String path = FileHelper.calcModulePath(mModule) + "config.ini";
            FileWriter writer = new FileWriter(path);
            BufferedWriter wr = new BufferedWriter(writer); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            wr.write("output_path=" + mTargetLanguage + "\n");
            wr.close();
            writer.close();
        } catch (Exception ignored) {
        }
    }

    @NotNull
    public String getTargetLanguage() {
        return mTargetLanguage;
    }
    public void setTargetLanguage(@NotNull String value) {
        if (mTargetLanguage.equals(value)) return;
        mTargetLanguage = value;
    }

    @NotNull
    public static ALittleConfig getConfig(Module module) {
        ALittleConfig config = module.getUserData(sALittleConfigKey);
        if (config != null) return config;
        config = new ALittleConfig(module);
        module.putUserData(sALittleConfigKey, config);
        return config;
    }
}
