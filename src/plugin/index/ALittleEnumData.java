package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleEnumVarDec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleEnumData {
    // Key:名称，Value:成员
    private Map<String, ALittleEnumVarDec> dataMap;

    public ALittleEnumData() {
        dataMap = new HashMap<>();
    }

    public void addVarDec(@NotNull ALittleEnumVarDec dec) {
        PsiElement nameDec = dec.getEnumVarNameDec();
        if (nameDec == null) return;
        dataMap.put(nameDec.getText(), dec);
    }

    public void findVarDecList(@NotNull String name, @NotNull List<ALittleEnumVarDec> result) {
        if (name.isEmpty()) {
            result.addAll(dataMap.values());
        } else {
            ALittleEnumVarDec varDec = dataMap.get(name);
            if (varDec != null) result.add(varDec);
        }
    }
}
