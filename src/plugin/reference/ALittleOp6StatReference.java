package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp6StatReference extends ALittleReference {
    public ALittleOp6StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp6Stat op6Stat = (ALittleOp6Stat)myElement;

        ALittleValueFactor valueFactor = op6Stat.getValueFactor();
        ALittleReferenceUtil.GuessTypeInfo factorGuessType = valueFactor.guessType();
        ALittleOp6Suffix op6Suffix = op6Stat.getOp6Suffix();

        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = guessForOp(op6Suffix.getOp6().getText(), valueFactor, factorGuessType, op6Suffix, op6Suffix.guessType());

        PsiElement lastSrc = op6Suffix;
        List<ALittleOp6SuffixEx> suffix_exList = op6Stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp6Suffix() != null) {
                op6Suffix = suffix_ex.getOp6Suffix();
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
        if (opString.equals("==") || opString.equals("!=")) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "bool";
            return info;
        } else {
            if (leftGuessInfo.value.equals("int") || leftGuessInfo.value.equals("I64") || leftGuessInfo.value.equals("double")) {
                if (rightGuessInfo.value.equals("int") || rightGuessInfo.value.equals("I64") || rightGuessInfo.value.equals("double")) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "bool";
                    return info;
                }

                throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符左边是数字，那么右边必须是int,double,any类型.不能是:" + rightGuessInfo.value);
            }

            if (leftGuessInfo.value.equals("string")) {
                if (rightGuessInfo.value.equals("string")) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "bool";
                    return info;
                }

                throw new ALittleReferenceUtil.ALittleReferenceException(rightSrc, opString + "运算符左边是字符串，那么右边必须是string,any类型.不能是:" + rightGuessInfo.value);
            }

            throw new ALittleReferenceUtil.ALittleReferenceException(leftSrc, opString + "运算符左边必须是int,double,string,any类型.不能是:" + leftGuessInfo.value);
        }
    }
}
