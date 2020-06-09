package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassNameDec;

import java.util.Map;

public class ALittleGuessClassName extends ALittleGuess {
    // 命名域和类名
    public String namespace_name = "";
    public String class_name = "";

    // 元素对象
    public ALittleClassNameDec class_name_dec;

    public ALittleGuessClassName(String p_namespace_name, String p_class_name
            , ALittleClassNameDec p_class_name_dec) {
        is_register = PsiHelper.isRegister(p_class_name_dec);
        namespace_name = p_namespace_name;
        class_name = p_class_name;
        class_name_dec = p_class_name_dec;
    }

    @Override
    public PsiElement getElement() {
        return class_name_dec;
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
        ALittleGuessClassName guess = new ALittleGuessClassName(namespace_name, class_name, class_name_dec);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value += namespace_name + "." + class_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(class_name_dec) == null;
    }
}
