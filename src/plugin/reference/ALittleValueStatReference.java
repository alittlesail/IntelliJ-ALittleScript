package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueStatReference extends ALittleReference {
    public ALittleValueStatReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStat valueStat = (ALittleValueStat)myElement;

        if (valueStat.getOpNewStat() != null) {
            return valueStat.getOpNewStat().guessTypes();
        } else if (valueStat.getOpNewList() != null) {
            return valueStat.getOpNewList().guessTypes();
        } else if (valueStat.getValueFactor() != null) {
            return valueStat.getValueFactor().guessTypes();
        } else if (valueStat.getOp2Stat() != null) {
            return valueStat.getOp2Stat().guessTypes();
        } else if (valueStat.getOp3Stat() != null) {
            return valueStat.getOp3Stat().guessTypes();
        } else if (valueStat.getOp4Stat() != null) {
            return valueStat.getOp4Stat().guessTypes();
        } else if (valueStat.getOp5Stat() != null) {
            return valueStat.getOp5Stat().guessTypes();
        } else if (valueStat.getOp6Stat() != null) {
            return valueStat.getOp6Stat().guessTypes();
        } else if (valueStat.getOp7Stat() != null) {
            return valueStat.getOp7Stat().guessTypes();
        } else if (valueStat.getOp8Stat() != null) {
            return valueStat.getOp8Stat().guessTypes();
        } else if (valueStat.getBindStat() != null) {
            return valueStat.getBindStat().guessTypes();
        }

        return new ArrayList<>();
    }
}
