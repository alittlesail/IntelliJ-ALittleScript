package plugin.guess;

public class ALittleGuessString extends ALittleGuessPrimitive
{
    public ALittleGuessString(boolean p_is_const) { super("string", p_is_const); }
    @Override
    public ALittleGuess clone() { return new ALittleGuessString(is_const); }
}