package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleStructDec;

import java.util.Map;

public class ALittleGuessStruct extends ALittleGuess {
    // 命名域和结构体名
    public String namespace_name = "";
    public String struct_name = "";

    // 元素对象
    public ALittleStructDec struct_dec;

    public ALittleGuessStruct(String p_namespace_name, String p_struct_name
            , ALittleStructDec p_struct_dec, boolean p_is_const) {
        is_register = PsiHelper.isRegister(p_struct_dec);
        namespace_name = p_namespace_name;
        struct_name = p_struct_name;
        struct_dec = p_struct_dec;
        is_const = p_is_const;
    }

    @Override
    public PsiElement getElement() {
        return struct_dec;
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
        ALittleGuessStruct guess = new ALittleGuessStruct(namespace_name, struct_name, struct_dec, is_const);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        value += namespace_name + "." + struct_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(struct_dec) == null;
    }
}
