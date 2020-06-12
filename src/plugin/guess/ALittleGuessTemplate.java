package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleTemplateNameDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.Map;

public class ALittleGuessTemplate extends ALittleGuess {
    // 模板范围限定
    public ALittleGuess template_extends;
    public boolean is_class;
    public boolean is_struct;

    // 元素对象
    public ALittleTemplatePairDec template_pair_dec;
    private String native_value = "";

    public ALittleGuessTemplate(ALittleTemplatePairDec p_template_pair_dec
            , ALittleGuess p_template_extends
            , boolean p_is_class, boolean p_is_struct, boolean p_is_const) {
        is_register = PsiHelper.isRegister(p_template_pair_dec);
        template_pair_dec = p_template_pair_dec;
        template_extends = p_template_extends;
        is_class = p_is_class;
        is_struct = p_is_struct;
        is_const = p_is_const;
        if (p_template_extends != null) is_const = p_template_extends.is_const;

        ALittleTemplateNameDec name_dec = template_pair_dec.getTemplateNameDec();
        if (name_dec != null) native_value = name_dec.getText();
    }

    @Override
    public PsiElement getElement() {
        return template_pair_dec;
    }

    @Override
    public boolean needReplace() {
        return true;
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        ALittleGuess new_guess = fill_map.get(native_value);
        if (new_guess != null) return new_guess;
        return this;
    }

    @Override
    public String getTotalValue() {
        String v = "";
        if (is_const) v += "const ";
        v += native_value;
        if (template_extends != null)
            return v + ":" + template_extends.getValue();
        else if (is_class)
            return v + ":class";
        else if (is_struct)
            return v + ":struct";
        return v;
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        value += native_value;
    }

    @Override
    public boolean isChanged() {
        if (template_extends != null && template_extends.isChanged()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(template_pair_dec) == null;
    }
}
