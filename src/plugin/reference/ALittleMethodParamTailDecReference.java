package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodParamTailDecReference extends ALittleReference<ALittleMethodParamTailDec> {
    public ALittleMethodParamTailDecReference(@NotNull ALittleMethodParamTailDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_PARAM_TAIL;
        info.value = myElement.getText();
        info.element = myElement;
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleMethodParamDec) {
            return;
        }

        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "参数占位符未定义");
            }

            ALittleMethodParamDec paramDec = null;
            if (parent instanceof ALittleClassMethodDec) {
                paramDec = ((ALittleClassMethodDec) parent).getMethodParamDec();
            } else if (parent instanceof ALittleClassStaticDec) {
                paramDec = ((ALittleClassStaticDec) parent).getMethodParamDec();
            } else if (parent instanceof ALittleClassCtorDec) {
                paramDec = ((ALittleClassCtorDec) parent).getMethodParamDec();
            } else if (parent instanceof ALittleGlobalMethodDec) {
                paramDec = ((ALittleGlobalMethodDec) parent).getMethodParamDec();
            }

            if (paramDec != null) {
                if (paramDec.getMethodParamTailDec() == null) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "参数占位符未定义");
                }
                break;
            }

            parent = parent.getParent();
        }
    }
}
