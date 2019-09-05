package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleValueStatReference extends ALittleReference<ALittleValueStat> {
    public ALittleValueStatReference(@NotNull ALittleValueStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getOpNewStat() != null) {
            return myElement.getOpNewStat().guessTypes();
        } else if (myElement.getOpNewListStat() != null) {
            return myElement.getOpNewListStat().guessTypes();
        } else if (myElement.getValueFactorStat() != null) {
            return myElement.getValueFactorStat().guessTypes();
        } else if (myElement.getBindStat() != null) {
            return myElement.getBindStat().guessTypes();
        } else if (myElement.getMethodParamTailDec() != null) {
            return myElement.getMethodParamTailDec().guessTypes();
        } else if (myElement.getPcallStat() != null) {
            return myElement.getPcallStat().guessTypes();
        } else if (myElement.getNcallStat() != null) {
            return myElement.getNcallStat().guessTypes();
        } else if (myElement.getOp2Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp2Stat()));
            return guessList;
        } else if (myElement.getOp3Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp3Stat()));
            return guessList;
        } else if (myElement.getOp4Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp4Stat()));
            return guessList;
        } else if (myElement.getOp5Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp5Stat()));
            return guessList;
        } else if (myElement.getOp6Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp6Stat()));
            return guessList;
        } else if (myElement.getOp7Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp7Stat()));
            return guessList;
        } else if (myElement.getOp8Stat() != null) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
            guessList.add(ALittleReferenceOpUtil.guessType(myElement.getOp8Stat()));
            return guessList;
        }

        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleIfExpr
                || parent instanceof ALittleElseIfExpr
                || parent instanceof ALittleWhileExpr
                || parent instanceof ALittleDoWhileExpr) {
            List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = myElement.guessTypes();
            if (guessTypeList.isEmpty()) return;

            if (!guessTypeList.get(0).value.equals("bool") && !guessTypeList.get(0).value.equals("null")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "条件语句中的表达式的类型必须是bool或者null");
            }
        }
    }
}
