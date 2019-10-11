package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassNameDec;
import plugin.psi.ALittleGenericFunctorType;
import plugin.psi.ALittleGenericType;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class ALittleGuessFunctor extends ALittleGuess {
    public @NotNull List<ALittleGuess> functorParamList = new ArrayList<>(); // 参数列表
    public @NotNull List<String> functorParamNameList = new ArrayList<>();   // 参数名列表
    public ALittleGuess functorParamTail;                                   // 参数占位符
    public @NotNull List<ALittleGuess> functorReturnList = new ArrayList<>();     // 返回值列表
    public ALittleGuess functorReturnTail;                                  // 返回值占位符
    public boolean functorAwait = false;                      // 表示是否是await

    public @NotNull PsiElement element; // 产生当前Functor的节点对象
    public ALittleGuessFunctor(@NotNull PsiElement e) {
        element = e;
    }

    @Override
    public void UpdateValue() {
        value = "Functor<";
        if (functorAwait) {
            value += "await";
        }
        List<String> paramList = new ArrayList<>();
        for (ALittleGuess guess : functorParamList) {
            paramList.add(guess.value);
        }
        if (functorParamTail != null) {
            paramList.add(functorParamTail.value);
        }
        value += "(" + String.join(",", paramList) + ")";

        List<String> returnList = new ArrayList<>();
        for (ALittleGuess guess : functorReturnList) {
            returnList.add(guess.value);
        }
        if (functorReturnTail != null) {
            returnList.add(functorReturnTail.value);
        }
        if (!returnList.isEmpty()) value += ":";
        value += String.join(",", returnList);
        value += ">";
    }

    @Override
    public boolean isChanged() {
        for (ALittleGuess paramInfo : functorParamList) {
            if (paramInfo.isChanged()) return true;
        }
        for (ALittleGuess returnInfo : functorReturnList) {
            if (returnInfo.isChanged()) return true;
        }
        if (functorParamTail != null && functorParamTail.isChanged()) {
            return true;
        }
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
