package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp8StatReference extends ALittleReference {
    public ALittleOp8StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp8Stat op8Stat = (ALittleOp8Stat)myElement;

        ALittleValueFactor valueFactor = op8Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp8Suffix op8Suffix = op8Stat.getOp8Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op8Suffix.getOp8().getText(), valueFactor, factorGuessType, op8Suffix, op8Suffix.guessType());

        PsiElement lastSrc = op8Suffix;
        List<ALittleOp8SuffixEx> suffix_exList = op8Stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp8Suffix() != null) {
                op8Suffix = suffix_ex.getOp8Suffix();
                suffixGuessType = ALittleOp8StatReference.guessForOp(op8Suffix.getOp8().getText(), lastSrc, suffixGuessType, op8Suffix, op8Suffix.guessType());
                lastSrc = op8Suffix;
            } else {
                throw new ALittleReferenceUtil.ALittleReferenceException(suffix_ex, "未知的表达式");
            }
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(suffixGuessType);
        return guessList;
    }

    @NotNull
    public static ALittleReferenceUtil.GuessTypeInfo guessForOp(String opString,
                                                                PsiElement leftSrc,
                                                                ALittleReferenceUtil.GuessTypeInfo leftGuessInfo,
                                                                PsiElement rightSrc,
                                                                ALittleReferenceUtil.GuessTypeInfo rightGuessInfo) throws ALittleReferenceUtil.ALittleReferenceException {
        if (!leftGuessInfo.value.equals("bool")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(leftSrc, opString + "运算符左边必须是bool类型.不能是:" + leftGuessInfo.value);
        }

        if (!rightGuessInfo.value.equals("bool")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符右边边必须是bool类型.不能是:" + rightGuessInfo.value);
        }

        return leftGuessInfo;
    }
}
