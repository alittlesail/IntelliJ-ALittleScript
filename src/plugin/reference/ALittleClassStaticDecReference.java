package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleClassElementDec;
import plugin.psi.ALittleClassSetterDec;
import plugin.psi.ALittleClassStaticDec;
import plugin.psi.ALittleMethodReturnDec;

public class ALittleClassStaticDecReference extends ALittleReference<ALittleClassStaticDec> {
    public ALittleClassStaticDecReference(@NotNull ALittleClassStaticDec element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getMethodNameDec() == null)
            throw new ALittleGuessException(myElement, "没有函数名");

        if (myElement.getMethodBodyDec() == null)
            throw new ALittleGuessException(myElement, "没有函数体");


        ALittleClassElementDec parent = (ALittleClassElementDec)myElement.getParent();
        if (parent == null)
            throw  new ALittleGuessException(myElement, "ALittleScriptClassStaticDecElement的父节点不是ALittleScriptClassElementDecElement");

        String co_text = PsiHelper.getCoroutineType(parent.getModifierList());

        int return_count = 0;
        ALittleMethodReturnDec return_dec = myElement.getMethodReturnDec();
        if (return_dec != null) return_count = return_dec.getMethodReturnOneDecList().size();

        if (co_text != null && co_text.equals("async") && return_count > 0)
            throw new ALittleGuessException(return_dec, "带async修饰的函数，不能有返回值");
    }
}
