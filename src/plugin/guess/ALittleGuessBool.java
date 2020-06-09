package plugin.guess;

public class ALittleGuessBool extends ALittleGuessPrimitive {
    public ALittleGuessBool(boolean p_is_const) {
        super("bool", p_is_const);
    }

    @Override
    public ALittleGuess clone() {
        return new ALittleGuessBool(is_const);
    }
}