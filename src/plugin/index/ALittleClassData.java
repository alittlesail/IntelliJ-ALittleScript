package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.*;

public class ALittleClassData {
    // Key1:访问权限，Key2:属性类型，Key3:名称，Value:元素
    private Map<PsiHelper.ClassAccessType, Map<PsiHelper.ClassAttrType, Map<String, PsiElement>>> dataMap;

    public ALittleClassData() {
        dataMap = new HashMap<>();
    }

    // 添加类元素
    public void addALittleClassChildDec(@NotNull PsiElement dec) {
        // 模板定义特殊处理
        if (dec instanceof ALittleTemplateDec) {
            ALittleTemplateDec templateDec = (ALittleTemplateDec)dec;
            List<ALittleTemplatePairDec> pairDecList = templateDec.getTemplatePairDecList();
            for (ALittleTemplatePairDec pairDec : pairDecList) {
                addALittleClassChildDec(pairDec);
            }
            return;
        }

        PsiHelper.ClassAccessType accessType;
        PsiHelper.ClassAttrType attrType;
        String name;

        // 处理模板参数
        if (dec instanceof ALittleTemplatePairDec) {
            ALittleTemplatePairDec pairDec = (ALittleTemplatePairDec)dec;
            name = pairDec.getIdContent().getText();
            accessType = PsiHelper.ClassAccessType.PUBLIC;
            attrType = PsiHelper.ClassAttrType.TEMPLATE;
        // 处理成员变量
        } else if (dec instanceof ALittleClassVarDec) {
            ALittleClassVarDec varDec = (ALittleClassVarDec) dec;
            PsiElement nameDec = varDec.getIdContent();
            if (nameDec == null) return;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(varDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.VAR;
        // 处理成员函数
        } else if (dec instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec methodDec = (ALittleClassMethodDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.FUN;
        // 处理getter函数
        } else if (dec instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec methodDec = (ALittleClassGetterDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.GETTER;
        // 处理setter函数
        } else if (dec instanceof ALittleClassSetterDec) {
            ALittleClassSetterDec methodDec = (ALittleClassSetterDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.SETTER;
        // 处理静态函数
        } else if (dec instanceof ALittleClassStaticDec) {
            ALittleClassStaticDec methodDec = (ALittleClassStaticDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.STATIC;
        } else {
            return;
        }

        Map<PsiHelper.ClassAttrType, Map<String, PsiElement>> map = dataMap.computeIfAbsent(accessType, k -> new HashMap<>());
        Map<String, PsiElement> elementMap = map.computeIfAbsent(attrType, k -> new HashMap<>());
        elementMap.put(name, dec);
    }

    // 获取元素集合
    private Map<String, PsiElement> getElementMap(PsiHelper.ClassAttrType attrType, PsiHelper.ClassAccessType accessType) {
        Map<PsiHelper.ClassAttrType, Map<String, PsiElement>> map = dataMap.get(accessType);
        if (map == null) return null;
        return map.get(attrType);
    }

    // 查找类属性集合
    public void findClassAttrList(int accessLevel,
                                  PsiHelper.ClassAttrType type,
                                  String name,
                                  @NotNull List<PsiElement> result) {
        if (accessLevel >= PsiHelper.sAccessOnlyPublic) {
            Map<String, PsiElement> map = getElementMap(type, PsiHelper.ClassAccessType.PUBLIC);
            if (map != null) {
                if (name.isEmpty()) {
                    result.addAll(map.values());
                } else {
                    PsiElement dec = map.get(name);
                    if (dec != null) result.add(dec);
                }
            }
        }

        if (accessLevel >= PsiHelper.sAccessProtectedAndPublic) {
            Map<String, PsiElement> map = getElementMap(type, PsiHelper.ClassAccessType.PROTECTED);
            if (map != null) {
                if (name.isEmpty()) {
                    result.addAll(map.values());
                } else {
                    PsiElement dec = map.get(name);
                    if (dec != null) result.add(dec);
                }
            }
        }

        if (accessLevel >= PsiHelper.sAccessPrivateAndProtectedAndPublic) {
            Map<String, PsiElement> map = getElementMap(type, PsiHelper.ClassAccessType.PRIVATE);
            if (map != null) {
                if (name.isEmpty()) {
                    result.addAll(map.values());
                } else {
                    PsiElement dec = map.get(name);
                    if (dec != null) result.add(dec);
                }
            }
        }
    }
}
