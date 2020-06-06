package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleStructData {
    // Key:名称，Value:成员
    private Map<String, ALittleStructVarDec> dataMap;

    public ALittleStructData() {
        dataMap = new HashMap<>();
    }

    public void addVarDec(@NotNull ALittleStructVarDec dec) {
        PsiElement nameDec = dec.getStructVarNameDec();
        if (nameDec == null) return;
        dataMap.put(nameDec.getText(), dec);
    }

    public void findVarDecList(@NotNull String name, @NotNull List<ALittleStructVarDec> result) {
        if (name.isEmpty()) {
            result.addAll(dataMap.values());
        } else {
            ALittleStructVarDec varDec = dataMap.get(name);
            if (varDec != null) result.add(varDec);
        }
    }
}
