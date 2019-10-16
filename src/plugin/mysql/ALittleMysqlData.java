package plugin.mysql;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleStructVarDec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALittleMysqlData {
    public static class MysqlData {
        public String comment;  // 注解
        public String name;     // 字段名
        public String type;     // 字段类型
    }

    private Project mProject;
    private long mLastModified;
    private String mFilePath;
    private List<MysqlData> mVarList = new ArrayList<>();
    private List<String> mStringList = null;

    public ALittleMysqlData(Project project, String filePath) {
        mProject = project;
        mLastModified = 0;
        mFilePath = filePath;
    }

    public Project getProject() { return mProject; }

    public enum ChangeType
    {
        CT_NONE,
        CT_DELETED,
        CT_CHANGED,
    }

    // 对应的文件是否发生变化
    public ChangeType isChanged() {
        if (mFilePath == null) return ChangeType.CT_NONE;

        File file = new File(mFilePath);
        if (!file.exists()) return ChangeType.CT_DELETED;
        if (mLastModified != file.lastModified()) {
            load();
            return ChangeType.CT_CHANGED;
        }
        return ChangeType.CT_NONE;
    }

    // 读取文件并解析mysql头部
    public String load() {
        mVarList = new ArrayList<>();
        mStringList = null;

        try {
            Pattern pattern = Pattern.compile("/(\\w+)\\.(\\w+)\\?");
            Matcher matcher = pattern.matcher(mFilePath);
            if (!matcher.find() || matcher.groupCount() != 2) return "数据库名和表名抓取失败:" + mFilePath;
            String dbName = matcher.group(1);
            String tableName = matcher.group(2);

            String newFilePath = mFilePath.replaceFirst("/(\\w+)\\.(\\w+)\\?", "/information_schema?");

            Connection conn = DriverManager.getConnection("jdbc:mysql://" + newFilePath + "&serverTimezone=UTC");

            Statement stmt = conn.createStatement();
            // 字段名，数据类型，注释，key类型
            ResultSet rs = stmt.executeQuery("SELECT `column_name`,`data_type`,`column_comment`,`column_key` FROM `columns` WHERE `table_schema`=\"" + dbName + "\" AND `table_name`=\"" + tableName + "\";");
            while (rs.next()) {
                MysqlData mysqlData = new MysqlData();
                mysqlData.comment = rs.getString(3);
                mysqlData.name = rs.getString(1);
                String dataType = rs.getString(2);
                String columnKey = rs.getString(4);
                if (dataType.equals("varchar")) dataType = "string";
                else if (dataType.equals("int")) dataType = "int";
                else if (dataType.equals("bigint")) dataType = "I64";
                mysqlData.type = dataType;
                mVarList.add(mysqlData);
            }
            rs.close();
            stmt.close();
            conn.close();
            mLastModified = 0;
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public boolean check(@NotNull List<ALittleStructVarDec> varDecList) {
        if (varDecList.size() != mVarList.size()) return true;

        for (int i = 0; i < varDecList.size(); ++i) {
            ALittleStructVarDec varDec = varDecList.get(i);
            MysqlData mysqlData = mVarList.get(i);

            ALittleAllType allType = varDec.getAllType();
            if (allType == null) return true;
            if (!mysqlData.type.equals(allType.getText())) return true;
            PsiElement element = varDec.getIdContent();
            if (element == null) return true;
            if (!mysqlData.name.equals(element.getText())) return true;
        }

        return false;
    }

    public List<String> generateVarList() {
        if (mStringList != null) return mStringList;
        mStringList = new ArrayList<>();

        int typeMaxLen = 0;
        int nameMaxLen = 0;
        for (MysqlData mysqlData : mVarList) {
            if (mysqlData.type.length() > typeMaxLen) typeMaxLen = mysqlData.type.length();
            if (mysqlData.name.length() > nameMaxLen) nameMaxLen = mysqlData.name.length();
        }

        for (MysqlData mysqlData : mVarList) {
            int deltaLen = typeMaxLen - mysqlData.type.length() + 1;
            StringBuilder value = new StringBuilder("\t" + mysqlData.type);
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            deltaLen = nameMaxLen - mysqlData.name.length();
            value.append(mysqlData.name).append(';');
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            value.append(" // ");
            value.append(mysqlData.comment);
            mStringList.add(value.toString());
        }
        return mStringList;
    }
}
