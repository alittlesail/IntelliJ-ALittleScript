package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessParamTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodParamTailDecReference extends ALittleReference<ALittleMethodParamTailDec> {
    public ALittleMethodParamTailDecReference(@NotNull ALittleMethodParamTailDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleGuessParamTail info = new ALittleGuessParamTail(myElement.getText());
        info.updateValue();
        List<ALittleGuess> guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    public void checkError() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleMethodParamDec) return;

        while (parent != null)
        {
            ALittleMethodParamDec param_dec = null;
            if (parent instanceof ALittleClassMethodDec) {
            param_dec = ((ALittleClassMethodDec)parent).getMethodParamDec();
        } else if (parent instanceof ALittleClassStaticDec) {
            param_dec = ((ALittleClassStaticDec)parent).getMethodParamDec();
        } else if (parent instanceof ALittleClassCtorDec) {
            param_dec = ((ALittleClassCtorDec)parent).getMethodParamDec();
        } else if (parent instanceof ALittleGlobalMethodDec) {
            param_dec = ((ALittleGlobalMethodDec)parent).getMethodParamDec();
        }

            if (param_dec != null)
            {
                List<ALittleMethodParamOneDec> param_one_list = param_dec.getMethodParamOneDecList();
                if (param_one_list.size() == 0)
                    throw new ALittleGuessException(myElement, "参数占位符未定义");
                ALittleMethodParamTailDec param_tail = param_one_list.get(param_one_list.size() - 1).getMethodParamTailDec();
                if (param_tail == null)
                    throw new ALittleGuessException(myElement, "参数占位符未定义");
                break;
            }

            parent = parent.getParent();
        }
    }
}
