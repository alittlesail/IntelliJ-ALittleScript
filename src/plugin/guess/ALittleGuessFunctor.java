package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleGuessFunctor extends ALittleGuess {
    public @NotNull List<ALittleGuessClassTemplate> functorTemplateParamList = new ArrayList<>(); // 参数列表
    public @NotNull List<ALittleGuess> functorParamList = new ArrayList<>(); // 参数列表
    public @NotNull List<String> functorParamNameList = new ArrayList<>();   // 参数名列表
    public ALittleGuess functorParamTail;                                   // 参数占位符
    public @NotNull List<ALittleGuess> functorReturnList = new ArrayList<>();     // 返回值列表
    public ALittleGuess functorReturnTail;                                  // 返回值占位符
    public String functorProto;                                 // 协议注解
    public boolean functorAwait = false;                      // 表示是否是await

    public @NotNull PsiElement element; // 产生当前Functor的节点对象
    public ALittleGuessFunctor(@NotNull PsiElement e) {
        isRegister = PsiHelper.isRegister(e);
        element = e;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessFunctor guess = new ALittleGuessFunctor(element);
        guess.functorTemplateParamList.addAll(functorTemplateParamList);
        guess.functorParamList.addAll(functorParamList);
        guess.functorParamNameList.addAll(functorParamNameList);
        guess.functorParamTail = functorParamTail;
        guess.functorReturnList.addAll(functorReturnList);
        guess.functorReturnTail = functorReturnTail;
        guess.functorProto = functorProto;
        guess.functorAwait = functorAwait;
        guess.UpdateValue();
        return guess;
    }
    @Override
    public boolean NeedReplace() {
        for (ALittleGuess guess : functorParamList) {
            if (guess.NeedReplace()) return true;
        }
        for (ALittleGuess guess : functorReturnList) {
            if (guess.NeedReplace()) return true;
        }
        return false;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        ALittleGuessFunctor newGuess = (ALittleGuessFunctor)Clone();
        newGuess.functorParamList = new ArrayList<>();
        for (ALittleGuess guess : functorParamList) {
            newGuess.functorParamList.add(guess.ReplaceTemplate(fillMap));
        }
        newGuess.functorReturnList = new ArrayList<>();
        for (ALittleGuess guess : functorReturnList) {
            newGuess.functorReturnList.add(guess.ReplaceTemplate(fillMap));
        }
        return newGuess;
    }

    @Override
    public void UpdateValue() {
        value = "Functor<";
        List<String> preList = new ArrayList<>();
        if (functorProto != null) {
            preList.add(functorProto);
        }
        if (functorAwait) {
            preList.add("await");
        }
        value += String.join(",", preList);

        if (!functorTemplateParamList.isEmpty()) {
            List<String> templateList = new ArrayList<>();
            for (ALittleGuessClassTemplate guess : functorTemplateParamList) {
                templateList.add(guess.GetTotalValue());
            }
            value += "<" + String.join(",", templateList) + ">";
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
        if (functorReturnTail != null && functorReturnTail.isChanged()) {
            return true;
        }
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
