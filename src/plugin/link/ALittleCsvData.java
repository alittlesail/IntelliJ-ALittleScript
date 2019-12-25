package plugin.link;

import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ALittleCsvData extends ALittleLinkData {
    private Project mProject;
    private String mFilePath;

    public ALittleCsvData(Project project, String filePath) {
        mProject = project;
        mFilePath = filePath;
    }

    public Project getProject() { return mProject; }

    // 读取文件并解析csv头部
    public String load() {
        mVarList = new ArrayList<>();
        mStringList = null;
        try {
            File file = new File(mFilePath);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader);
            String commentLine = br.readLine();
            String typeLine = br.readLine();
            String nameLine = br.readLine();
            br.close();
            reader.close();

            if (commentLine == null) return "注释行读取失败";
            if (nameLine == null) return "字段行读取失败";
            if (typeLine == null) return "类型行读取失败";

            String[] commentList = commentLine.split(",");
            if (commentList.length == 0) return "注释行是空的";
            String[] typeList = typeLine.split(",");
            if (typeList.length == 0) return "类型行是空的";
            String[] nameList = nameLine.split(",");
            if (nameList.length == 0) return "字段行是空的";

            if (typeList.length != nameList.length) return "类型的列数和字段的列数不一致";

            for (int i = 0; i < nameList.length; ++i) {
                ALittleLinkInfo data = new ALittleLinkInfo();
                if (i >= commentList.length) {
                    data.comment = "";
                } else {
                    data.comment = commentList[i].trim();
                }
                data.name = nameList[i].trim();
                data.type = typeList[i].trim();
                mVarList.add(data);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
}
