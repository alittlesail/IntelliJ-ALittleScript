package plugin.guess;

public class ALittleGuessDouble extends ALittleGuessPrimitive
{
    public ALittleGuessDouble(boolean p_is_const) { super("double", p_is_const); }
    @Override
    public ALittleGuess clone() { return new ALittleGuessDouble(is_const); }
}