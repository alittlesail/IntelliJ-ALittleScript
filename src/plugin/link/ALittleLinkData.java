package plugin.link;

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
import java.util.ArrayList;
import java.util.List;

public class ALittleLinkData {
    protected List<ALittleLinkInfo> mVarList = new ArrayList<>();
    protected List<String> mStringList = null;

    public enum ChangeType
    {
        CT_CREATED,
        CT_DELETED,
        CT_CHANGED,
    }

    public boolean check(@NotNull List<ALittleStructVarDec> varDecList) {
        if (varDecList.size() != mVarList.size()) return true;

        for (int i = 0; i < varDecList.size(); ++i) {
            ALittleStructVarDec varDec = varDecList.get(i);
            ALittleLinkInfo data = mVarList.get(i);

            ALittleAllType allType = varDec.getAllType();
            if (allType == null) return true;
            if (!data.type.equals(allType.getText())) return true;
            PsiElement element = varDec.getIdContent();
            if (element == null) return true;
            if (!data.name.equals(element.getText())) return true;
            element = varDec;
            int loop = 10;  // 添加Loop，放置以后语法变更，这里出现非常大的循环
            do {
                element = element.getNextSibling();
                if (element == null || element instanceof ALittleStructVarDec) {
                    return !data.comment.isEmpty();
                } else if (element instanceof PsiComment) {
                    break;
                }
                --loop;
                if (loop <= 0) break;
            } while (true);
            if (loop <= 0) continue;
            if (!element.getText().equals("// " + data.comment)) return true;
        }

        return false;
    }

    public List<String> generateVarList() {
        if (mStringList != null) return mStringList;
        mStringList = new ArrayList<>();

        int typeMaxLen = 0;
        int nameMaxLen = 0;
        for (ALittleLinkInfo data : mVarList) {
            if (data.type.length() > typeMaxLen) typeMaxLen = data.type.length();
            if (data.name.length() > nameMaxLen) nameMaxLen = data.name.length();
        }

        for (ALittleLinkInfo data : mVarList) {
            int deltaLen = typeMaxLen - data.type.length() + 1;
            StringBuilder value = new StringBuilder("\t" + data.type);
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            deltaLen = nameMaxLen - data.name.length();
            value.append(data.name).append(';');
            for (int i = 0; i < deltaLen; ++i)
                value.append(' ');
            if (!data.comment.isEmpty()) {
                value.append(" // ");
                value.append(data.comment);
            }
            mStringList.add(value.toString());
        }
        return mStringList;
    }
}
