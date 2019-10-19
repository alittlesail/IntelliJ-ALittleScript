package plugin.link;

import com.intellij.openapi.project.Project;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALittleMysqlData extends ALittleLinkData {
    private Project mProject;
    private String mUrl;
    private String mName;
    
    public ALittleMysqlData(Project project, String url, String name) {
        mProject = project;
        mUrl = url;
        mName = name;
    }

    public Project getProject() { return mProject; }

    // 读取文件并解析mysql头部
    public String load() {
        try {
            mVarList = new ArrayList<>();
            mStringList = null;

            String[] split = mName.split("\\.");
            if (split.length != 2) {
                return "格式不正确:" + mName;
            }
            String dbName = split[0];
            String tableName = split[1];

            Connection conn = DriverManager.getConnection(mUrl);

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
                        return "暂不支持联合索引";
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
                    ALittleLinkInfo data = new ALittleLinkInfo();
                    data.comment = rs.getString(3).trim();
                    data.name = rs.getString(1);
                    String dataType = rs.getString(2).toLowerCase();
                    String columnDefault = rs.getString(4);
                    String index = indexMap.get(data.name);
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
                            return "tinyint类型(字段名:" + data.name + ")不支持作为表索引";
                        } else {
                            dataType = "bool";
                        }
                    } else if (dataType.equals("double")) {
                        if (index != null) {
                            rs.close();
                            stmt.close();
                            conn.close();
                            return "double类型(字段名:" + data.name + ")不支持作为表索引";
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
                            return "text类型(字段名:" + data.name + ")不支持作为表索引";
                        } else {
                            if (data.comment.isEmpty()) {
                                rs.close();
                                stmt.close();
                                conn.close();
                                return "text类型(字段名:" + data.name + ")对应的高级类型必须填写在注释字段,和注释内容用分号隔开, 格式为 类型;注释内容";
                            }
                            split = data.comment.split(";");
                            data.comment = "";
                            if (split.length > 1) {
                                data.comment = split[1].trim();
                            }
                            dataType = split[0].trim();
                        }
                    } else {
                        rs.close();
                        stmt.close();
                        conn.close();
                        return "不支持生成的类型:" + dataType + "(字段名:" + data.name + "),请使用以下:varchar,int,bigint,text";
                    }
                    data.type = dataType;
                    mVarList.add(data);
                }
                rs.close();
                stmt.close();
            }
            conn.close();
        } catch (Exception e) {
            mVarList = new ArrayList<>();
            mStringList = null;
            return e.getMessage();
        }
        return null;
    }
}
