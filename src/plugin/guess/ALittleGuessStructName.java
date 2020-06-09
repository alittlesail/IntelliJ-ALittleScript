package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructNameDec;

import java.util.Map;

public class ALittleGuessStructName extends ALittleGuess {
    // 命名域和结构体名
    public String namespace_name = "";
    public String struct_name = "";

    // 元素对象
    public ALittleStructNameDec struct_name_dec;

    public ALittleGuessStructName(String p_namespace_name, String p_struct_name
            , ALittleStructNameDec p_struct_name_dec) {
        is_register = PsiHelper.isRegister(p_struct_name_dec);
        namespace_name = p_namespace_name;
        struct_name = p_struct_name;
        struct_name_dec = p_struct_name_dec;
    }

    @Override
    public PsiElement getElement() {
        return struct_name_dec;
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
        ALittleGuessStructName guess = new ALittleGuessStructName(namespace_name, struct_name, struct_name_dec);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = namespace_name + "." + struct_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(struct_name_dec) == null;
    }
}
