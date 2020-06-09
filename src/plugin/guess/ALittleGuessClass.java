package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassDec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleGuessClass extends ALittleGuess {
    // 命名域和类名
    public String namespace_name = "";
    public String class_name = "";

    // 类本身定义的模板列表
    public List<ALittleGuess> template_list = new ArrayList<>();
    // 填充后的模板实例
    public Map<String, ALittleGuess> template_map = new HashMap<>();

    // 如果是using定义出来的，那么就有这个值
    public String using_name;
    public ALittleClassDec class_dec;

    // 是否是原生类
    public boolean is_native = false;

    public ALittleGuessClass(String p_namespace_name, String p_class_name
            , ALittleClassDec p_class_dec, String p_using_name, boolean p_is_const, boolean p_is_native) {
        is_register = PsiHelper.isRegister(p_class_dec);
        namespace_name = p_namespace_name;
        class_name = p_class_name;
        class_dec = p_class_dec;
        using_name = p_using_name;
        is_const = p_is_const;
        is_native = p_is_native;
    }

    @Override
    public PsiElement getElement() {
        return class_dec;
    }

    @Override
    public boolean needReplace() {
        if (template_list.size() == 0) return false;
        for (Map.Entry<String, ALittleGuess> pair : template_map.entrySet()) {
            if (pair.getValue().needReplace())
                return true;
        }
        return false;
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        ALittleGuessClass new_guess = (ALittleGuessClass) clone();
        for (Map.Entry<String, ALittleGuess> pair : template_map.entrySet()) {
            ALittleGuess guess = pair.getValue().replaceTemplate(fill_map);
            if (guess == null) return null;
            if (guess != pair.getValue()) {
                ALittleGuess replace = pair.getValue().replaceTemplate(fill_map);
                if (replace == null) return null;
                new_guess.template_map.put(pair.getKey(), replace);
            }
        }
        return new_guess;
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessClass guess = new ALittleGuessClass(namespace_name, class_name, class_dec, using_name, is_const, is_native);
        guess.template_list.addAll(template_list);
        for (Map.Entry<String, ALittleGuess> pair : template_map.entrySet())
            guess.template_map.put(pair.getKey(), pair.getValue());
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        if (is_native) value += "native ";
        value += namespace_name + "." + class_name;
        ArrayList<String> name_list = new ArrayList<String>();
        for (ALittleGuess template : template_list) {
            ALittleGuess impl = template_map.get(template.getValueWithoutConst());
            if (impl != null) {
                if (template.is_const && !impl.is_const) {
                    impl = impl.clone();
                    impl.is_const = true;
                    impl.updateValue();
                }
                name_list.add(impl.getValue());
            } else
                name_list.add(template.getValue());
        }
        if (name_list.size() > 0)
            value += "<" + String.join(",", name_list) + ">";
    }

    @Override
    public boolean isChanged() {
        for (ALittleGuess guess : template_list) {
            if (guess.isChanged())
                return true;
        }
        for (Map.Entry<String, ALittleGuess> pair : template_map.entrySet()) {
            if (pair.getValue().isChanged())
                return true;
        }
        return ALittleTreeChangeListener.getGuessTypeList(class_dec) == null;
    }
}
