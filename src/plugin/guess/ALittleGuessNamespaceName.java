package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.Map;

public class ALittleGuessNamespaceName extends ALittleGuess {
    // 命名域
    public String namespace_name = "";

    // 元素对象
    public ALittleNamespaceNameDec namespace_name_dec;

    public ALittleGuessNamespaceName(String p_namespace_name, ALittleNamespaceNameDec p_namespace_name_dec) {
        is_register = PsiHelper.isRegister(p_namespace_name_dec);
        namespace_name = p_namespace_name;
        namespace_name_dec = p_namespace_name_dec;
    }

    @Override
    public PsiElement getElement() {
        return namespace_name_dec;
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
        ALittleGuessNamespaceName guess = new ALittleGuessNamespaceName(namespace_name, namespace_name_dec);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = namespace_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(namespace_name_dec) == null;
    }
}
