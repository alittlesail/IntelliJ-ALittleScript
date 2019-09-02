package plugin.index;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.*;

public class ALittleClassData {
    private Map<PsiHelper.ClassAccessType, Map<PsiHelper.ClassAttrType, Map<String, PsiElement>>> dataMap;

    public ALittleClassData() {
        dataMap = new HashMap<>();
    }

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

        if (dec instanceof ALittleTemplatePairDec) {
            ALittleTemplatePairDec pairDec = (ALittleTemplatePairDec)dec;
            name = pairDec.getIdContent().getText();
            accessType = PsiHelper.ClassAccessType.PUBLIC;
            attrType = PsiHelper.ClassAttrType.TEMPLATE;
        } else if (dec instanceof ALittleClassVarDec) {
            ALittleClassVarDec varDec = (ALittleClassVarDec) dec;
            PsiElement nameDec = varDec.getIdContent();
            if (nameDec == null) return;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(varDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.VAR;
        } else if (dec instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec methodDec = (ALittleClassMethodDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.FUN;
        } else if (dec instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec methodDec = (ALittleClassGetterDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.GETTER;
        } else if (dec instanceof ALittleClassSetterDec) {
            ALittleClassSetterDec methodDec = (ALittleClassSetterDec)dec;
            ALittleMethodNameDec nameDec = methodDec.getMethodNameDec();
            if (nameDec == null) return;
            dec = nameDec;
            name = nameDec.getText();
            accessType = PsiHelper.calcAccessType(methodDec.getAccessModifier());
            attrType = PsiHelper.ClassAttrType.SETTER;
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

        Map<PsiHelper.ClassAttrType, Map<String, PsiElement>> map = dataMap.get(accessType);
        if (map == null) {
            map = new HashMap<>();
            dataMap.put(accessType, map);
        }
        Map<String, PsiElement> elementMap = map.get(attrType);
        if (elementMap == null) {
            elementMap = new HashMap<>();
            map.put(attrType, elementMap);
        }
        elementMap.put(name, dec);
    }

    private Map<String, PsiElement> getElementMap(PsiHelper.ClassAttrType attrType, PsiHelper.ClassAccessType accessType) {
        Map<PsiHelper.ClassAttrType, Map<String, PsiElement>> map = dataMap.get(accessType);
        if (map == null) return null;
        return map.get(attrType);
    }

    public void findClassAttrList(@NotNull ALittleClassDec classDec,
                                  int accessLevel,
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
