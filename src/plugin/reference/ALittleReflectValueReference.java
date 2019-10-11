package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReflectValueReference extends ALittleReference<ALittleReflectValue> {
    public ALittleReflectValueReference(@NotNull ALittleReflectValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(),
                PsiHelper.PsiElementType.STRUCT_NAME, null, "ALittle", "ReflectInfo", true);
        if (element instanceof ALittleStructNameDec) {
            return ((ALittleStructNameDec) element).guessTypes();
        }
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        ALittleCustomType customType = myElement.getCustomType();
        if (customType == null) {
            throw new ALittleGuessException(myElement, "没有指定反射对象");
        }

        ALittleGuess guess = customType.guessType();
        if (!(guess instanceof ALittleGuessStruct)) {
            throw new ALittleGuessException(myElement, "反射对象必须是一个struct");
        }

        checkStructExtends(((ALittleGuessStruct)guess).element);
    }

    private void checkStructExtends(@NotNull ALittleStructDec dec) throws ALittleGuessException {
        if (dec.getStructExtendsDec() != null) {
            throw new ALittleGuessException(myElement, "反射对象不能使用具有继承的struct");
        }

        List<ALittleStructVarDec> varDecList = dec.getStructVarDecList();
        for (ALittleStructVarDec varDec : varDecList) {
            ALittleGuess guess = varDec.guessType();
            if (guess instanceof ALittleGuessStruct) {
                checkStructExtends(((ALittleGuessStruct)guess).element);
            } else if (guess instanceof ALittleGuessClass) {
                throw new ALittleGuessException(myElement, "反射对象内部不能使用类");
            } else if (guess instanceof ALittleGuessFunctor) {
                throw new ALittleGuessException(myElement, "反射对象内部不能使用函数");
            }
        }
    }
}
