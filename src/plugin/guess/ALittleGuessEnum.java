package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleEnumDec;

import java.util.Map;

public class ALittleGuessEnum extends ALittleGuess {
    // 命名域和枚举名
    public String namespace_name = "";
    public String enum_name = "";

    // 元素对象
    public ALittleEnumDec enum_dec;

    public ALittleGuessEnum(String p_namespace_name, String p_enum_name
            , ALittleEnumDec p_enum_dec) {
        is_register = PsiHelper.isRegister(p_enum_dec);
        namespace_name = p_namespace_name;
        enum_name = p_enum_name;
        enum_dec = p_enum_dec;
    }

    @Override
    public PsiElement getElement() {
        return enum_dec;
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
        ALittleGuessEnum guess = new ALittleGuessEnum(namespace_name, enum_name, enum_dec);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = namespace_name + "." + enum_name;
    }

    @Override
    public boolean isChanged() {
        return ALittleTreeChangeListener.getGuessTypeList(enum_dec) == null;
    }
}
