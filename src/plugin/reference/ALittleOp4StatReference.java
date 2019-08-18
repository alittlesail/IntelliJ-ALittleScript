package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp4StatReference extends ALittleReference {
    public ALittleOp4StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp4Stat op4Stat = (ALittleOp4Stat)myElement;

        ALittleValueFactor valueFactor = op4Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp4Suffix op4Suffix = op4Stat.getOp4Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op4Suffix.getOp4().getText(), valueFactor, factorGuessType, op4Suffix, op4Suffix.guessType());

        PsiElement lastSrc = op4Suffix;
        List<ALittleOp4SuffixEx> suffix_exList = op4Stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp4Suffix() != null) {
                op4Suffix = suffix_ex.getOp4Suffix();
                suffixGuessType = ALittleOp4StatReference.guessForOp(op4Suffix.getOp4().getText(), lastSrc, suffixGuessType, op4Suffix, op4Suffix.guessType());
                lastSrc = op4Suffix;
            } else if (suffix_ex.getOp5Suffix() != null) {
                ALittleOp5Suffix op5Suffix = suffix_ex.getOp5Suffix();
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
        if (leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                return leftGuessInfo;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuessInfo;
            } else {
                throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        if (leftGuessInfo.value.equals("double")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                return leftGuessInfo;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuessInfo;
            } else {
                throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        throw new ALittleReferenceUtil.ALittleReferenceException(leftSrc, opString + "运算符左边必须是int,double,any类型.不能是:" + leftGuessInfo.value);
    }
}
