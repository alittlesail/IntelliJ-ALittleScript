package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;
import java.util.*;

public class ALittleAccessData {
    // Key1:元素类型，Key2:名称，Value:对应的元素集合
    public Map<PsiHelper.PsiElementType, Map<String, Set<PsiElement>>> elementMap;

    public ALittleAccessData() {
        elementMap = new HashMap<>();
    }

    // 添加元素
    public void addALittleNameDec(@NotNull PsiElement nameDec) {
        // 获取名称
        String name = nameDec.getText();

        // 计算类型
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
        } else if (nameDec instanceof ALittleUsingNameDec) {
            type = PsiHelper.PsiElementType.USING_NAME;
        } else {
            return;
        }

        // 添加到映射表
        Map<String, Set<PsiElement>> map = elementMap.computeIfAbsent(type, k -> new HashMap<>());
        Set<PsiElement> set = map.computeIfAbsent(name, k -> new HashSet<>());
        set.add(nameDec);
    }

    // 查找元素
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

    // 移除元素
    public void removeALittleNameDec(@NotNull PsiElement nameDec) {
        // 获取名称
        String name = nameDec.getText();

        // 计算类型
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
        } else if (nameDec instanceof ALittleUsingNameDec) {
            type = PsiHelper.PsiElementType.USING_NAME;
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
