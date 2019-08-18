package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp7StatReference extends ALittleReference {
    public ALittleOp7StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp7Stat op7Stat = (ALittleOp7Stat)myElement;

        ALittleValueFactor valueFactor = op7Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp7Suffix op7Suffix = op7Stat.getOp7Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op7Suffix.getOp7().getText(), valueFactor, factorGuessType, op7Suffix, op7Suffix.guessType());

        PsiElement lastSrc = op7Suffix;
        List<ALittleOp7SuffixEx> suffix_exList = op7Stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp7Suffix() != null) {
                op7Suffix = suffix_ex.getOp7Suffix();
                suffixGuessType = ALittleOp7StatReference.guessForOp(op7Suffix.getOp7().getText(), lastSrc, suffixGuessType, op7Suffix, op7Suffix.guessType());
                lastSrc = op7Suffix;
            } else if (suffix_ex.getOp8Suffix() != null) {
                ALittleOp8Suffix op8Suffix = suffix_ex.getOp8Suffix();
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
