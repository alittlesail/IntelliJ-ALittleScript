package plugin.mysql;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleStructVarDec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALittleMysqlData {
    public static class MysqlData {
        public String comment;  // 注解
        public String name;     // 字段名
        public String type;     // 字段类型
    }

    private Project mProject;
    private Timestamp mLastModified;
    private String mFilePath;
    private List<MysqlData> mVarList = new ArrayList<>();
    private List<String> mStringList = null;

    public ALittleMysqlData(Project project, String filePath) {
        mProject = project;
        mLastModified = null;
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

        Date oldLastModifier = mLastModified;
        load();
        if (oldLastModifier == null) {
            if (mLastModified == null) {
                return ChangeType.CT_NONE;
            }
            return ChangeType.CT_CHANGED;
        } else {
            if (mLastModified == null) {
                return ChangeType.CT_DELETED;
            } else if (oldLastModifier.equals(mLastModified)) {
                return ChangeType.CT_NONE;
            }
            return ChangeType.CT_CHANGED;
        }
    }

    // 读取文件并解析mysql头部
    public String load() {
        mVarList = new ArrayList<>();
        mStringList = null;

        long time1 = System.currentTimeMillis();
        try {
            Pattern pattern = Pattern.compile("/(\\w+)\\.(\\w+)\\?");
            Matcher matcher = pattern.matcher(mFilePath);
            if (!matcher.find() || matcher.groupCount() != 2) return "数据库名和表名抓取失败:" + mFilePath;
            String dbName = matcher.group(1);
            String tableName = matcher.group(2);

            String newFilePath = mFilePath.replaceFirst("/(\\w+)\\.(\\w+)\\?", "/information_schema?");

            Connection conn = DriverManager.getConnection("jdbc:mysql://" + newFilePath + "&serverTimezone=UTC");
            Map<String, String> indexMap = new HashMap<>();
            {
                Statement stmt = conn.createStatement();
                // 字段名，数据类型，注释，key类型
                ResultSet rs = stmt.executeQuery("SELECT `non_unique`,`INDEX_NAME`,`column_name` FROM `statistics` WHERE `table_schema`=\""
                        + dbName + "\" AND `table_name`=\"" + tableName + "\";");
                while (rs.next()) {
                    int nonUnique = rs.getInt(1);
                    String indexName = rs.getString(2).toUpperCase();
                    String columnName = rs.getString(3);
                    if (indexMap.get(columnName) != null) {
                        rs.close();
                        stmt.close();
                        conn.close();
                        return "赞不支持联合索引";
                    }
                    if (indexName.equals("PRIMARY")) {
                        indexMap.put(columnName, "ALittle.TABLE_PRIMARY_");
                    } else if (nonUnique == 0) {
                        indexMap.put(columnName, "ALittle.TABLE_UNIQUE_");
                    } else {
                        indexMap.put(columnName, "ALittle.TABLE_INDEX_");
                    }
                }
                rs.close();
                stmt.close();
            }
            {
                Statement stmt = conn.createStatement();
                // 字段名，数据类型，注释，key类型
                ResultSet rs = stmt.executeQuery("SELECT `column_name`,`data_type`,`column_comment`,`column_default` FROM `columns` WHERE `table_schema`=\""
                        + dbName + "\" AND `table_name`=\"" + tableName + "\" ORDER BY `ordinal_position` ASC;");
                while (rs.next()) {
                    MysqlData mysqlData = new MysqlData();
                    mysqlData.comment = rs.getString(3);
                    mysqlData.name = rs.getString(1);
                    String dataType = rs.getString(2).toLowerCase();
                    String columnDefault = rs.getString(4);
                    String index = indexMap.get(mysqlData.name);
                    if (dataType.equals("varchar")) {
                        if (index != null) {
                            dataType = index + "STRING";
                        } else {
                            dataType = "string";
                        }
                    } else if (dataType.equals("int")) {
                        if (index != null) {
                            dataType = index + "INT";
                        } else {
                            dataType = "int";
                        }
                    } else if (dataType.equals("tinyint")) {
                        if (index != null) {
                            rs.close();
                            stmt.close();
                            conn.close();
                            return "tinyint类型不支持作为表索引";
                        } else {
                            dataType = "bool";
                        }
                    } else if (dataType.equals("double")) {
                        if (index != null) {
                            rs.close();
                            stmt.close();
                            conn.close();
                            return "double类型不支持作为表索引";
                        } else {
                            dataType = "double";
                        }
                    } else if (dataType.equals("bigint")) {
                        if (index != null) {
                            dataType = index + "I64";
                        } else {
                            dataType = "I64";
                        }
                    } else if (dataType.equals("text")) {
                        if (index != null) {
                            rs.close();
                            stmt.close();
                            conn.close();
                            return "text类型不支持作为表索引";
                        } else {
                            if (columnDefault.isEmpty()) {
                                rs.close();
                                stmt.close();
                                conn.close();
                                return "text类型对应的高级类型必须填写在表字段默认值位置";
                            }
                            dataType = columnDefault;
                        }
                    } else {
                        rs.close();
                        stmt.close();
                        conn.close();
                        return "不支持生成的类型:" + dataType + ",请使用以下:varchar,int,bigint,text";
                    }
                    mysqlData.type = dataType;
                    mVarList.add(mysqlData);
                }
                rs.close();
                stmt.close();
            }
            {
                mLastModified = null;
                Statement stmt = conn.createStatement();
                // 字段名，数据类型，注释，key类型
                ResultSet rs = stmt.executeQuery("SELECT `update_time` FROM `tables` WHERE `table_schema`=\""
                        + dbName + "\" AND `table_name`=\"" + tableName + "\";");
                while (rs.next()) {
                    mLastModified = rs.getTimestamp(1);
                }
                rs.close();
                stmt.close();
            }
            conn.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
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
            element = varDec;
            do {
                element = element.getNextSibling();
                if (element == null || element instanceof ALittleStructVarDec) {
                    return !mysqlData.comment.isEmpty();
                } else if (element instanceof PsiComment) {
                    break;
                }
            } while (true);
            if (!element.getText().equals("// " + mysqlData.comment)) return true;
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
            if (!mysqlData.comment.isEmpty()) {
                value.append(" // ");
                value.append(mysqlData.comment);
            }
            mStringList.add(value.toString());
        }
        return mStringList;
    }
}
