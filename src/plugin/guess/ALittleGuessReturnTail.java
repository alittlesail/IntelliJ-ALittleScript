package plugin.guess;

import java.util.Map;

public class ALittleGuessReturnTail extends ALittleGuess {
    public ALittleGuessReturnTail(String p_value)
    {
        value = p_value;
    }

    @Override
    public boolean needReplace()
    {
        return false;
    }

    @Override
    public ALittleGuess replaceTemplate(Map<String, ALittleGuess> fill_map)
    {
        return this;
    }

    @Override
    public ALittleGuess clone()
    {
        return new ALittleGuessReturnTail(value);
    }

    @Override
    public void updateValue()
    {
    }

    public boolean isChanged()
    {
        return false;
    }
}
