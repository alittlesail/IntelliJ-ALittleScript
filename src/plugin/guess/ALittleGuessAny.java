package plugin.guess;

public class ALittleGuessAny extends ALittleGuessPrimitive {
    public ALittleGuessAny(boolean p_is_const) {
        super("any", p_is_const);
    }

    @Override
    public ALittleGuess clone() {
        return new ALittleGuessAny(is_const);
    }
}