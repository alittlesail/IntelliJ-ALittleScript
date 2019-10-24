package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleTemplatePairDec;

import java.util.Map;

public class ALittleGuessClassTemplate extends ALittleGuess {
    public ALittleGuess templateExtends;
    public boolean isClass;
    public boolean isStruct;
    public @NotNull ALittleTemplatePairDec element;

    public ALittleGuessClassTemplate(@NotNull ALittleTemplatePairDec e, ALittleGuess t, boolean ic, boolean is) {
        isRegister = PsiHelper.isRegister(e);
        element = e;
        templateExtends = t;
        isClass = ic;
        isStruct = is;
    }

    @Override
    public boolean NeedReplace() {
        return true;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        ALittleGuess newGuess = fillMap.get(value);
        if (newGuess != null) return newGuess;
        return this;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessClassTemplate guess = new ALittleGuessClassTemplate(element, templateExtends, isClass, isStruct);
        guess.UpdateValue();
        return guess;
    }

    @NotNull
    public String GetTotalValue() {
        String v = element.getIdContent().getText();
        if (templateExtends != null) {
            return v + ":" + templateExtends.value;
        } else if (isClass) {
            return v + ":class";
        } else if (isStruct) {
            return v + ":struct";
        }
        return v;
    }

    @Override
    public void UpdateValue() {
        value = element.getIdContent().getText();
    }

    @Override
    public boolean isChanged() {
        if (templateExtends != null && templateExtends.isChanged()) {
            return true;
        }
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
