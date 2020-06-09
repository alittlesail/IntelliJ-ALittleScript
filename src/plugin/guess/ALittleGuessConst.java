package plugin.guess;

import java.util.Map;

public class ALittleGuessConst extends ALittleGuess {
    public ALittleGuessConst(String p_value) {
        value = p_value;
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
        return new ALittleGuessConst(value);
    }

    @Override
    public void updateValue() {
    }

    @Override
    public boolean isChanged() {
        return false;
    }
}
