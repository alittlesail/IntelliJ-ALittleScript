package plugin.alittle;

import com.intellij.psi.PsiElement;
import plugin.psi.ALittleClassDec;

public class PsiHelper {
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
