package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReflectValueReference extends ALittleReference<ALittleReflectValue> {
    public ALittleReflectValueReference(@NotNull ALittleReflectValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(),
                PsiHelper.PsiElementType.STRUCT_NAME, null, "ALittle", "ReflectInfo", true);
        if (element instanceof ALittleStructNameDec) {
            return ((ALittleStructNameDec) element).guessTypes();
        }
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleCustomType customType = myElement.getCustomType();
        if (customType == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有指定反射对象");
        }

        ALittleReferenceUtil.GuessTypeInfo guessType = customType.guessType();
        if (guessType.type != ALittleReferenceUtil.GuessType.GT_STRUCT) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "反射对象必须是一个struct");
        }

        checkStructExtends((ALittleStructDec)guessType.element);
    }

    private void checkStructExtends(@NotNull ALittleStructDec dec) throws ALittleReferenceUtil.ALittleReferenceException {
        if (dec.getStructExtendsDec() != null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "反射对象不能使用具有继承的struct");
        }

        List<ALittleStructVarDec> varDecList = dec.getStructVarDecList();
        for (ALittleStructVarDec varDec : varDecList) {
            ALittleReferenceUtil.GuessTypeInfo guessInfo = varDec.guessType();
            if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                checkStructExtends((ALittleStructDec)guessInfo.element);
            } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "反射对象内部不能使用类");
            } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "反射对象内部不能使用函数");
            }
        }
    }
}
