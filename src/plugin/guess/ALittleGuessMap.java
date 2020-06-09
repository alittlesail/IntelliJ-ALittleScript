package plugin.guess;

import java.util.Map;

public class ALittleGuessMap extends ALittleGuess {
    public ALittleGuess key_type;
    public ALittleGuess value_type;

    public ALittleGuessMap(ALittleGuess p_key_type, ALittleGuess p_value_type, boolean p_is_const) {
        key_type = p_key_type;
        value_type = p_value_type;
        is_const = p_is_const;
    }

    @Override
    public boolean hasAny() {
        return key_type.hasAny() || value_type.hasAny();
    }

    @Override
    public boolean needReplace() {
        return key_type.needReplace() || value_type.needReplace();
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map) {
        ALittleGuess key_replace = key_type.replaceTemplate(fill_map);
        if (key_replace == null) return null;

        ALittleGuess value_replace = value_type.replaceTemplate(fill_map);
        if (value_replace == null) return null;

        ALittleGuessMap guess = new ALittleGuessMap(key_replace, value_replace, is_const);
        guess.updateValue();
        return guess;
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessMap guess = new ALittleGuessMap(key_type, value_type, is_const);
        guess.updateValue();
        return guess;
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        value += "Map<" + key_type.getValue() + "," + value_type.getValue() + ">";
    }

    @Override
    public boolean isChanged() {
        return key_type.isChanged() || value_type.isChanged();
    }
}
