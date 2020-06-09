package plugin.guess;

import java.util.Map;

public class ALittleGuessList extends ALittleGuess {
    public ALittleGuess sub_type;
    public boolean is_native;

    public ALittleGuessList(ALittleGuess p_sub_type, boolean p_is_const, boolean p_is_native) {
        sub_type = p_sub_type;
        is_const = p_is_const;
        is_native = p_is_native;
    }

    @Override
    public boolean hasAny() {
        return sub_type.hasAny();
    }

    @Override
    public boolean needReplace() {
        return sub_type.needReplace();
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        ALittleGuess replace = sub_type.replaceTemplate(fill_map);
        if (replace == null) return null;
        ALittleGuessList guess = new ALittleGuessList(replace, is_const, is_native);
        guess.updateValue();
        return guess;
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessList guess = new ALittleGuessList(sub_type, is_const, is_native);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        if (is_native) value += "native ";
        value += "List<" + sub_type.getValue() + ">";
    }

    @Override
    public boolean isChanged() {
        return sub_type.isChanged();
    }
}
