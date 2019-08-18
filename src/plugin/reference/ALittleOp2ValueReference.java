package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp2ValueReference extends ALittleReference {
    public ALittleOp2ValueReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp2Value op2Value = (ALittleOp2Value)myElement;
        ALittleValueFactor valueFactor = op2Value.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = valueFactor.guessType();

        String op2 = op2Value.getOp2().getText();
        // guessType必须是逻辑运算符
        if (op2.equals("!")) {
            if (!suffixGuessType.value.equals("bool") && !suffixGuessType.value.equals("any")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueFactor, "!运算符右边必须是bool,any类型.不能是:" + suffixGuessType.value);
            }
            // guessType必须是数字
        } else if (op2.equals("-")) {
            if (!suffixGuessType.value.equals("int") && !suffixGuessType.value.equals("I64") && !suffixGuessType.value.equals("double") && !suffixGuessType.value.equals("any")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueFactor, "-运算符右边必须是int,double,any类型.不能是:" + suffixGuessType.value);
            }
        } else {
            throw new ALittleReferenceUtil.ALittleReferenceException(op2Value.getOp2(), "未知的运算符:" + op2);
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(suffixGuessType);
        return guessList;
    }
}
