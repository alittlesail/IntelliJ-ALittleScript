package plugin.guess;

public class ALittleGuessInt extends ALittleGuessPrimitive
{
    public ALittleGuessInt(boolean p_is_const) { super("int", p_is_const); }
    @Override
    public ALittleGuess clone() { return new ALittleGuessInt(is_const); }
}