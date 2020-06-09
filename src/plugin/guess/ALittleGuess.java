package plugin.guess;

import com.intellij.psi.PsiElement;

import java.util.Map;

public abstract class ALittleGuess {
    protected String value = "";
    public boolean is_register = false;
    public boolean is_const = false;

    public ALittleGuess() {
    }

    public boolean isChanged() {
        return true;
    }

    public String getValue() {
        return value;
    }

    public String getValueWithoutConst() {
        if (is_const) return value.substring("const ".length());
        return value;
    }

    public void updateValue() {
    }

    public ALittleGuess clone() {
        return null;
    }

    public boolean needReplace() {
        return false;
    }

    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        return null;
    }

    public boolean hasAny() {
        return false;
    }

    public String getTotalValue() {
        return value;
    }

    public PsiElement getElement() {
        return null;
    }
}
