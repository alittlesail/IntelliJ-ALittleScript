package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;
import java.util.*;

public class ALittleAccessData {
    public Map<PsiHelper.PsiElementType, Map<String, Set<PsiElement>>> elementMap;

    public ALittleAccessData() {
        elementMap = new HashMap<>();
    }

    public void addALittleNameDec(@NotNull PsiElement nameDec) {
        String name = nameDec.getText();
        PsiHelper.PsiElementType type;
        if (nameDec instanceof ALittleClassNameDec) {
            type = PsiHelper.PsiElementType.CLASS_NAME;
        } else if (nameDec instanceof ALittleEnumNameDec) {
            type = PsiHelper.PsiElementType.ENUM_NAME;
        } else if (nameDec instanceof ALittleStructNameDec) {
            type = PsiHelper.PsiElementType.STRUCT_NAME;
        } else if (nameDec instanceof  ALittleVarAssignNameDec) {
            type = PsiHelper.PsiElementType.INSTANCE_NAME;
        } else if (nameDec instanceof ALittleMethodNameDec) {
            type = PsiHelper.PsiElementType.GLOBAL_METHOD;
        } else {
            return;
        }
        Map<String, Set<PsiElement>> map = elementMap.get(type);
        if (map == null) {
            map = new HashMap<>();
            elementMap.put(type, map);
        }
        Set<PsiElement> set = map.get(name);
        if (set == null) {
            set = new HashSet<>();
            map.put(name, set);
        }
        set.add(nameDec);
    }

    public void findALittleNameDecList(PsiHelper.PsiElementType type, @NotNull String name, @NotNull List<PsiElement> result) {
        Map<String, Set<PsiElement>> map = elementMap.get(type);
        if (map == null) return;

        if (name.isEmpty()) {
            for (Map.Entry<String, Set<PsiElement>> entry : map.entrySet()) {
                result.addAll(entry.getValue());
            }
        } else {
            Set<PsiElement> set = map.get(name);
            if (set != null) {
                result.addAll(set);
            }
        }
    }

    public void removeALittleNameDec(@NotNull PsiElement nameDec) {
        String name = nameDec.getText();
        PsiHelper.PsiElementType type;
        if (nameDec instanceof ALittleClassNameDec) {
            type = PsiHelper.PsiElementType.CLASS_NAME;
        } else if (nameDec instanceof ALittleEnumNameDec) {
            type = PsiHelper.PsiElementType.ENUM_NAME;
        } else if (nameDec instanceof ALittleStructNameDec) {
            type = PsiHelper.PsiElementType.STRUCT_NAME;
        } else if (nameDec instanceof  ALittleVarAssignNameDec) {
            type = PsiHelper.PsiElementType.INSTANCE_NAME;
        } else if (nameDec instanceof ALittleMethodNameDec) {
            type = PsiHelper.PsiElementType.GLOBAL_METHOD;
        } else {
            return;
        }
        Map<String, Set<PsiElement>> map = elementMap.get(type);
        if (map == null) return;
        Set<PsiElement> set = map.get(name);
        if (set == null) return;
        set.remove(nameDec);
        if (set.isEmpty()) {
            map.remove(name);
        }
    }
}
