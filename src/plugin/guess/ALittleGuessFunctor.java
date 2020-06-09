package plugin.guess;

import com.intellij.psi.PsiElement;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleGuessFunctor extends ALittleGuess {
    // 模板参数列表
    public List<ALittleGuessTemplate> template_param_list = new ArrayList<>();
    // 参数列表
    public List<ALittleGuess> param_list = new ArrayList<>();
    // 参数名列表
    public List<String> param_name_list = new ArrayList<>();
    // 参数是否可以为null
    public List<Boolean> param_nullable_list = new ArrayList<>();
    // 参数占位符
    public ALittleGuess param_tail;
    // 返回值列表
    public List<ALittleGuess> return_list = new ArrayList<>();
    // 返回值占位符
    public ALittleGuess return_tail;
    // 协议注解
    public String proto;
    // 是否是await
    public boolean await_modifier = false;
    // 是否有const修饰
    public boolean const_modifier = false;
    // 产生当前Functor的节点对象
    public PsiElement element;

    public ALittleGuessFunctor(PsiElement p_element) {
        is_register = PsiHelper.isRegister(p_element);
        element = p_element;
    }

    @Override
    public boolean hasAny() {
        for (ALittleGuess guess : param_list) {
            if (guess.hasAny()) return true;
        }
        for (ALittleGuess guess : return_list) {
            if (guess.hasAny()) return true;
        }
        return false;
    }

    @Override
    public PsiElement getElement() {
        return element;
    }

    @Override
    public boolean needReplace() {
        for (ALittleGuess guess : param_list) {
            if (guess.needReplace())
                return true;
        }
        for (ALittleGuess guess : return_list) {
            if (guess.needReplace())
                return true;
        }
        return false;
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        // 克隆一份
        ALittleGuessFunctor new_guess = (ALittleGuessFunctor) clone();
        // 清理参数列表，重新按模板替换
        new_guess.param_list.clear();
        new_guess.param_nullable_list.clear();
        for (int i = 0; i < param_list.size(); ++i) {
            ALittleGuess guess = param_list.get(i);
            ALittleGuess replace = guess.replaceTemplate(fill_map);
            if (replace == null) return null;
            new_guess.param_list.add(replace);
            if (i < param_nullable_list.size())
                new_guess.param_nullable_list.add(param_nullable_list.get(i));
            else
                new_guess.param_nullable_list.add(false);
        }
        // 清理返回值列表，重新按模板替换
        new_guess.return_list.clear();
        for (ALittleGuess guess : return_list) {
            ALittleGuess replace = guess.replaceTemplate(fill_map);
            if (replace == null) return null;
            new_guess.return_list.add(replace);
        }
        // 返回拷贝
        return new_guess;
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessFunctor guess = new ALittleGuessFunctor(element);
        guess.template_param_list.addAll(template_param_list);
        guess.param_list.addAll(param_list);
        guess.param_nullable_list.addAll(param_nullable_list);
        guess.param_name_list.addAll(param_name_list);
        guess.param_tail = param_tail;
        guess.return_list.addAll(return_list);
        guess.return_tail = return_tail;
        guess.proto = proto;
        guess.await_modifier = await_modifier;
        guess.const_modifier = const_modifier;
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = "Functor<";

        // proto和await修饰
        List<String> pre_list = new ArrayList<>();
        if (proto != null) pre_list.add(proto);
        if (const_modifier) pre_list.add("const");
        if (await_modifier) pre_list.add("await");
        value += String.join(",", pre_list);

        // 模板参数列表
        if (template_param_list.size() > 0) {
            List<String> template_String_list = new ArrayList<>();
            for (ALittleGuess guess : template_param_list)
                template_String_list.add(guess.getTotalValue());
            value += "<" + String.join(",", template_String_list) + ">";
        }

        // 参数类型列表
        List<String> param_String_list = new ArrayList<>();
        for (int i = 0; i < param_list.size(); ++i) {
            if (i < param_nullable_list.size() && param_nullable_list.get(i))
                param_String_list.add("[Nullable] " + param_list.get(i).getValue());
            else
                param_String_list.add(param_list.get(i).getValue());
        }
        if (param_tail != null)
            param_String_list.add(param_tail.getValue());
        value += "(" + String.join(",", param_String_list) + ")";

        // 返回值类型列表
        List<String> return_String_list = new ArrayList<>();
        for (ALittleGuess guess : return_list)
            return_String_list.add(guess.getValue());
        if (return_tail != null)
            return_String_list.add(return_tail.getValue());
        if (return_String_list.size() > 0) value += ":";
        value += String.join(",", return_String_list);

        value += ">";
    }

    @Override
    public boolean isChanged() {
        for (ALittleGuess guess : param_list) {
            if (guess.isChanged())
                return true;
        }
        for (ALittleGuess guess : return_list) {
            if (guess.isChanged())
                return true;
        }
        if (param_tail != null && param_tail.isChanged())
            return true;
        if (return_tail != null && return_tail.isChanged())
            return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
