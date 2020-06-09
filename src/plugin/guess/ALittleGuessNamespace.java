package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceDec;

import java.util.Map;

public class ALittleGuessNamespace extends ALittleGuess {
    // 命名域
    public String namespace_name = "";

    // 元素对象
    public ALittleNamespaceDec namespace_dec;

    public ALittleGuessNamespace(String p_namespace_name, ALittleNamespaceDec p_namespace_dec) {
        is_register = PsiHelper.isRegister(p_namespace_dec);
        namespace_name = p_namespace_name;
        namespace_dec = p_namespace_dec;
    }

    @Override
    public PsiElement getElement() {
        return namespace_dec;
    }

    @Override
    public boolean needReplace() {
        return false;
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        return this;
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessNamespace guess = new ALittleGuessNamespace(namespace_name, namespace_dec);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = namespace_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(namespace_dec) == null;
    }
}
