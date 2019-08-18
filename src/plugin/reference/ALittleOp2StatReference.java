package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOp2StatReference extends ALittleReference {
    public ALittleOp2StatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleOp2Stat op2Stat = (ALittleOp2Stat)myElement;
        ALittleOp2Value op2Value = op2Stat.getOp2Value();
        ALittleReferenceUtil.GuessTypeInfo suffixGuessType = op2Value.guessType();

        PsiElement lastSrc = op2Value;
        List<ALittleOp2SuffixEx> suffix_exList = op2Stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_exList) {
            if (suffix_ex.getOp3Suffix() != null) {
                ALittleOp3Suffix op3Suffix = suffix_ex.getOp3Suffix();
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
}
