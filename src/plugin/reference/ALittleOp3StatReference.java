package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp3StatReference extends ALittleReference {
    public ALittleOp3StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp3Stat op3Stat = (ALittleOp3Stat)myElement;

        ALittleValueFactor valueFactor = op3Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp3Suffix op3Suffix = op3Stat.getOp3Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op3Suffix.getOp3().getText(), valueFactor, factorGuessType, op3Suffix, op3Suffix.guessType());

        PsiElement lastSrc = op3Suffix;
        List<ALittleOp3SuffixEx> suffix_exList = op3Stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp3Suffix() != null) {
                op3Suffix = suffix_ex.getOp3Suffix();
                suffixGuessType = ALittleOp3StatReference.guessForOp(op3Suffix.getOp3().getText(), lastSrc, suffixGuessType, op3Suffix, op3Suffix.guessType());
                lastSrc = op3Suffix;
            } else if (suffix_ex.getOp4Suffix() != null) {
                ALittleOp4Suffix op4Suffix = suffix_ex.getOp4Suffix();
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
        if (leftGuessInfo.value.equals("int")) {
            if (rightGuessInfo.value.equals("int")) {
                // 两个整数相除，返回的是double
                if (opString.equals("/")) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "double";
                    return info;
                }
                return leftGuessInfo;
            } else if (rightGuessInfo.value.equals("I64")) {
                // 两个整数相除，返回的是double
                if (opString.equals("/")) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "double";
                    return info;
                }
                return rightGuessInfo;
            } else if (rightGuessInfo.value.equals("double")) {
                return rightGuessInfo;
            } else {
                throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }
        }

        if (leftGuessInfo.value.equals("I64")) {
            if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64")) {
                // 两个整数相除，返回的是double
                if (opString.equals("/")) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "double";
                    return info;
                }
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
