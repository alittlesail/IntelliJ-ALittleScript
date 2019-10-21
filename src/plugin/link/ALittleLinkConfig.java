package plugin.link;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.FileHelper;

import java.io.*;

public class ALittleLinkConfig {
    private static Key<ALittleLinkConfig> sALittleLinkConfigKey = new Key<>("ALittleLinkConfig");

    private Module mModule;

    private @NotNull String mOutputPath = "";
    private @NotNull String mCsvPath = "";
    private @NotNull String mMysqlIp = "";
    private @NotNull String mMysqlPort = "";
    private @NotNull String mMysqlUser = "";
    private @NotNull String mMysqlPassword = "";
    private @NotNull String mMysqlUrl = "";

    public ALittleLinkConfig(Module module) {
        mModule = module;
        module.putUserData(sALittleLinkConfigKey, this);

        load();
    }

    public void load() {
        try {
            String path = FileHelper.calcModulePath(mModule) + "link.ini";
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                int index = line.indexOf('=');
                if (index == -1) continue;
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                if (key.equals("csv_path")) {
                    mCsvPath = value;
                } else if (key.equals("output_path")) {
                    mOutputPath = value;
                } else if (key.equals("mysql_ip")) {
                    mMysqlIp = value;
                } else if (key.equals("mysql_port")) {
                    mMysqlPort = value;
                } else if (key.equals("mysql_user")) {
                    mMysqlUser = value;
                } else if (key.equals("mysql_password")) {
                    mMysqlPassword = value;
                }
            }
            br.close();
            reader.close();

            if (!mMysqlIp.isEmpty() && !mMysqlPort.isEmpty() && !mMysqlUser.isEmpty() && !mMysqlPassword.isEmpty()) {
                mMysqlUrl = "jdbc:mysql://" + mMysqlIp + ":" + mMysqlPort
                        + "/information_schema?user=" + mMysqlUser + "&password=" + mMysqlPassword + "&serverTimezone=UTC";
                ALittleMysqlDataManager.setWatch(mModule, mMysqlUrl);
            }
            if (!mCsvPath.isEmpty()) {
                ALittleCsvDataManager.setWatch(mModule, mCsvPath);
            }
        } catch (Exception ignored) {
        }
    }

    public void save() {
        try {
            String path = FileHelper.calcModulePath(mModule) + "link.ini";
            FileWriter writer = new FileWriter(path);
            BufferedWriter wr = new BufferedWriter(writer); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            wr.write("csv_path=" + mCsvPath + "\n");
            wr.write("output_path=" + mOutputPath + "\n");
            wr.write("mysql_ip=" + mMysqlIp + "\n");
            wr.write("mysql_port=" + mMysqlPort + "\n");
            wr.write("mysql_user=" + mMysqlUser + "\n");
            wr.write("mysql_password=" + mMysqlPassword + "\n");
            wr.close();
            writer.close();
        } catch (Exception ignored) {
        }
    }

    @NotNull
    public String getOutputPath() {
        return mOutputPath;
    }
    @NotNull
    public String getOutputPathWithEnd() {
        if (mOutputPath.isEmpty()) return mOutputPath;
        if (mOutputPath.endsWith("/")) return mOutputPath;
        if (mOutputPath.endsWith("\\")) return mOutputPath;
        if (mOutputPath.lastIndexOf('\\') != -1) return mOutputPath + "\\";
        return mOutputPath + "/";
    }
    public void setOutputPath(@NotNull String value) {
        if (mOutputPath.equals(value)) return;
        mOutputPath = value;
    }

    @NotNull
    public String getCsvPath() {
        return mCsvPath;
    }
    @NotNull
    public String getCsvPathWithEnd() {
        if (mCsvPath.isEmpty()) return mCsvPath;
        if (mCsvPath.endsWith("/")) return mCsvPath;
        if (mCsvPath.endsWith("\\")) return mCsvPath;
        if (mCsvPath.lastIndexOf('\\') != -1) return mCsvPath + "\\";
        return mCsvPath + "/";
    }
    public void setCsvPath(@NotNull String value) {
        if (mCsvPath.equals(value)) return;
        mCsvPath = value;
        ALittleCsvDataManager.setWatch(mModule, mCsvPath);
    }

    public String getMysqlIp() {
        return mMysqlIp;
    }
    public String getMysqlPort() {
        return mMysqlPort;
    }
    public String getMysqlUser() {
        return mMysqlUser;
    }
    public String getMysqlPassword() {
        return mMysqlPassword;
    }
    public void setMysql(@NotNull String ip, @NotNull String port, @NotNull String user, @NotNull String password) {
        mMysqlIp = ip;
        mMysqlPort = port;
        mMysqlUser = user;
        mMysqlPassword = password;

        if (!mMysqlIp.isEmpty() && !mMysqlPort.isEmpty() && !mMysqlUser.isEmpty() && !mMysqlPassword.isEmpty()) {
            mMysqlUrl = "jdbc:mysql://" + mMysqlIp + ":" + mMysqlPort
                    + "/information_schema?user=" + mMysqlUser + "&password=" + mMysqlPassword + "&serverTimezone=UTC";
        } else {
            mMysqlUrl = "";
        }

        ALittleMysqlDataManager.setWatch(mModule, mMysqlUrl);
    }
    @NotNull
    public String getMysqlUrl() {
        return mMysqlUrl;
    }

    @NotNull
    public static ALittleLinkConfig getConfig(Module module) {
        ALittleLinkConfig config = module.getUserData(sALittleLinkConfigKey);
        if (config != null) return config;
        config = new ALittleLinkConfig(module);
        module.putUserData(sALittleLinkConfigKey, config);
        return config;
    }
}
