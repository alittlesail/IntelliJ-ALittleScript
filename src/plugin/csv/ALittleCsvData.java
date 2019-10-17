package plugin.csv;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleStructVarDec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ALittleCsvData {
    public static class CsvData {
        public String comment;  // 注解
        public String name;     // 字段名
        public String type;     // 字段类型
    }

    private Project mProject;
    private String mFilePath;
    private List<CsvData> mVarList = new ArrayList<>();
    private List<String> mStringList = null;

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
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader);
            String commentLine = br.readLine();
            String nameLine = br.readLine();
            String typeLine = br.readLine();
            br.close();
            reader.close();

            if (commentLine == null) return "注释行读取失败";
            if (nameLine == null) return "字段行读取失败";
            if (typeLine == null) return "类型行读取失败";

            String[] commentList = commentLine.split(",");
            if (commentList.length == 0) return "注释行是空的";
            String[] nameList = nameLine.split(",");
            if (nameList.length == 0) return "字段行是空的";
            String[] typeList = typeLine.split(",");
            if (typeList.length == 0) return "类型行是空的";

            if (typeList.length != nameList.length) return "类型的列数和字段的列数不一致";

            for (int i = 0; i < nameList.length; ++i) {
                CsvData csvData = new CsvData();
                if (i >= commentList.length) {
                    csvData.comment = "";
                } else {
                    csvData.comment = commentList[i];
                }
                csvData.name = nameList[i];
                csvData.type = typeList[i];
                mVarList.add(csvData);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public boolean check(@NotNull List<ALittleStructVarDec> varDecList) {
        if (varDecList.size() != mVarList.size()) return true;

        for (int i = 0; i < varDecList.size(); ++i) {
            ALittleStructVarDec varDec = varDecList.get(i);
            CsvData csvData = mVarList.get(i);

            ALittleAllType allType = varDec.getAllType();
            if (allType == null) return true;
            if (!csvData.type.equals(allType.getText())) return true;
            PsiElement element = varDec.getIdContent();
            if (element == null) return true;
            if (!csvData.name.equals(element.getText())) return true;
            element = varDec;
            do {
                element = element.getNextSibling();
                if (element == null || element instanceof ALittleStructVarDec) {
                    return !csvData.comment.isEmpty();
                } else if (element instanceof PsiComment) {
                    break;
                } else if (element instanceof PsiWhiteSpace) {
                    continue;
                } else {
                    System.out.println("-->" + element.getText());
                }
            } while (true);
            if (!element.getText().equals("// " + csvData.comment)) return true;
        }

        return false;
    }

    public List<String> generateVarList() {
        if (mStringList != null) return mStringList;
        mStringList = new ArrayList<>();

        int typeMaxLen = 0;
        int nameMaxLen = 0;
        for (CsvData csvData : mVarList) {
            if (csvData.type.length() > typeMaxLen) typeMaxLen = csvData.type.length();
            if (csvData.name.length() > nameMaxLen) nameMaxLen = csvData.name.length();
        }

        for (CsvData csvData : mVarList) {
            int deltaLen = typeMaxLen - csvData.type.length() + 1;
            StringBuilder value = new StringBuilder("\t" + csvData.type);
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            deltaLen = nameMaxLen - csvData.name.length();
            value.append(csvData.name).append(';');
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            if (!csvData.comment.isEmpty()) {
                value.append(" // ");
                value.append(csvData.comment);
            }
            mStringList.add(value.toString());
        }
        return mStringList;
    }
}
