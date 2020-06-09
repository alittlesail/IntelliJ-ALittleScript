package plugin.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ALittleConfig {
    private static final Key<ALittleConfig> sALittleConfigKey = new Key<>("ALittleConfig");

    private final Project mProject;
    private final Set<String> mLanguageNameSet;

    private @NotNull String mTargetLanguage = "";

    public ALittleConfig(Project project) {
        mProject = project;
        mProject.putUserData(sALittleConfigKey, this);
        mLanguageNameSet = new HashSet<>();
        mLanguageNameSet.add("Lua");
        mLanguageNameSet.add("JavaScript");
        load();
    }

    public void load() {
        try {
            String path = mProject.getBasePath() + "config.ini";
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
            String path = mProject.getBasePath() + "config.ini";
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
    public String getTargetExt() {
        if (mTargetLanguage.equals("Lua"))
            return "lua";
        else if (mTargetLanguage.equals("JavaScript"))
            return "js";
        return "";
    }

    @NotNull
    public Set<String> getTargetLanguageNameSet() {
        return mLanguageNameSet;
    }

    @NotNull
    public static ALittleConfig getConfig(Project project) {
        ALittleConfig config = project.getUserData(sALittleConfigKey);
        if (config != null) return config;
        config = new ALittleConfig(project);
        project.putUserData(sALittleConfigKey, config);
        return config;
    }
}
