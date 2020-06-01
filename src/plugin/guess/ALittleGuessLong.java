package plugin.guess;

public class ALittleGuessLong extends ALittleGuessPrimitive
{
    public ALittleGuessLong(boolean p_is_const) { super("long", p_is_const); }
    @Override
    public ALittleGuess clone() { return new ALittleGuessLong(is_const); }
}