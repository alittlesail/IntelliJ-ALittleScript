package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp5StatReference extends ALittleReference {
    public ALittleOp5StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp5Stat op5Stat = (ALittleOp5Stat)myElement;

        ALittleValueFactor valueFactor = op5Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp5Suffix op5Suffix = op5Stat.getOp5Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op5Suffix.getOp5().getText(), valueFactor, factorGuessType, op5Suffix, op5Suffix.guessType());

        PsiElement lastSrc = op5Suffix;
        List<ALittleOp5SuffixEx> suffix_exList = op5Stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp5Suffix() != null) {
                op5Suffix = suffix_ex.getOp5Suffix();
                suffixGuessType = ALittleOp5StatReference.guessForOp(op5Suffix.getOp5().getText(), lastSrc, suffixGuessType, op5Suffix, op5Suffix.guessType());
                lastSrc = op5Suffix;
            } else if (suffix_ex.getOp6Suffix() != null) {
                ALittleOp6Suffix op6Suffix = suffix_ex.getOp6Suffix();
                suffixGuessType = ALittleOp6StatReference.guessForOp(op6Suffix.getOp6().getText(), lastSrc, suffixGuessType, op6Suffix, op6Suffix.guessType());
                lastSrc = op6Suffix;
            } else if (suffix_ex.getOp7Suffix() != null) {
                ALittleOp7Suffix op7Suffix = suffix_ex.getOp7Suffix();
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
        boolean leftCheck = leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64") || leftGuessInfo.value.equals("double") ||  leftGuessInfo.value.equals("string");
        if (!leftCheck) {
            throw new ALittleReferenceUtil.ALittleReferenceException(leftSrc, opString + "运算符左边必须是int,double,string,any类型.不能是:" + leftGuessInfo.value);
        }

        boolean rightCheck = rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64") || rightGuessInfo.value.equals("double") ||  rightGuessInfo.value.equals("string");
        if (!rightCheck) {
            throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符右边必须是int,double,string,any类型.不能是:" + rightGuessInfo.value);
        }

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
        info.value = "string";
        return info;
    }
}
