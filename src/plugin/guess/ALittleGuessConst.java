package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ALittleGuessConst extends ALittleGuess {
    public static List<ALittleGuess> sConstNullGuess;
    static {
        sConstNullGuess = new ArrayList<>();
        sConstNullGuess.add(new ALittleGuessConst("null"));
    }

    public ALittleGuessConst(@NotNull String v) {
        super(v);
    }

    public boolean isChanged() {
        return false;
    }
}
