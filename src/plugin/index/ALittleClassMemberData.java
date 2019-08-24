package plugin.index;

import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.*;

public class ALittleClassMemberData {
    private Map<String, ALittleClassVarDec> varMap;
    private Map<String, ALittleMethodNameDec> funMap;
    private Map<String, ALittleMethodNameDec> getterMap;
    private Map<String, ALittleMethodNameDec> setterMap;
    private Map<String, ALittleMethodNameDec> staticMap;

    public ALittleClassMemberData() {
        varMap = new HashMap<>();
        funMap = new HashMap<>();
        getterMap = new HashMap<>();
        setterMap = new HashMap<>();
        staticMap = new HashMap<>();
    }

    public void addClassVarDec(@NotNull String name, @NotNull ALittleClassVarDec dec) {
        varMap.put(name, dec);
    }

    public void addMethodNameDec(PsiHelper.ClassMethodType type, @NotNull String name, @NotNull ALittleMethodNameDec dec) {
        if (type == PsiHelper.ClassMethodType.FUN) {
            funMap.put(name, dec);
        } else if (type == PsiHelper.ClassMethodType.GETTER) {
            getterMap.put(name, dec);
        } else if (type == PsiHelper.ClassMethodType.SETTER) {
            setterMap.put(name, dec);
        } else if (type == PsiHelper.ClassMethodType.STATIC) {
            staticMap.put(name, dec);
        }
    }
}
