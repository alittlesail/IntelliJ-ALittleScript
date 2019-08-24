package plugin.alittle;

import com.intellij.psi.PsiElement;
import plugin.psi.ALittleAccessModifier;
import plugin.psi.ALittleClassDec;

public class PsiHelper {
    public enum ClassMethodType
    {
        CTOR,
        FUN,
        GETTER,
        SETTER,
        STATIC,
    }

    public enum ClassAccessType
    {
        PUBLIC,
        PROTECTED,
        PRIVATE,
    }

    public static ClassAccessType calcAccess(ALittleAccessModifier accessModifier) {
        if (accessModifier == null || accessModifier.getText().equals("private")) {
            return ClassAccessType.PRIVATE;
        }
        if (accessModifier.getText().equals("protected")) {
            return ClassAccessType.PROTECTED;
        }
        return ClassAccessType.PUBLIC;
    }

    public static int sAccessOnlyPublic = 1;            // 可以访问public的属性和方法
    public static int sAccessProtectedAndPublic = 2;         // 可以访问public protected的属性和方法
    public static int sAccessPrivateAndProtectedAndPublic = 3;           // 可以public protected private的属性和方法

    public static ALittleClassDec findClassDecFromParent(PsiElement dec) {
        while (dec != null) {
            if (dec instanceof ALittleClassDec) {
                return (ALittleClassDec)dec;
            }
            dec = dec.getParent();
        }
        return null;
    }
}
