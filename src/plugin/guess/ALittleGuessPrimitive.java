package plugin.guess;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleGuessPrimitive extends ALittleGuess {
    // 基本变量类型
    public static ALittleGuess sIntGuess;
    public static ALittleGuess sDoubleGuess;
    public static ALittleGuess sStringGuess;
    public static ALittleGuess sBoolGuess;
    public static ALittleGuess sI64Guess;
    public static ALittleGuess sAnyGuess;
    public static Map<String, List<ALittleGuess>> sPrimitiveGuessMap;
    static {
        sPrimitiveGuessMap = new HashMap<>();
        List<ALittleGuess> tmp;
        tmp = new ArrayList<>(); sIntGuess = new ALittleGuessPrimitive("int"); tmp.add(sIntGuess); sPrimitiveGuessMap.put("int", tmp);
        tmp = new ArrayList<>(); sDoubleGuess = new ALittleGuessPrimitive("double"); tmp.add(sDoubleGuess); sPrimitiveGuessMap.put("double", tmp);
        tmp = new ArrayList<>(); sStringGuess = new ALittleGuessPrimitive("string"); tmp.add(sStringGuess); sPrimitiveGuessMap.put("string", tmp);
        tmp = new ArrayList<>(); sBoolGuess = new ALittleGuessPrimitive("bool"); tmp.add(sBoolGuess); sPrimitiveGuessMap.put("bool", tmp);
        tmp = new ArrayList<>(); sI64Guess = new ALittleGuessPrimitive("I64"); tmp.add(sI64Guess); sPrimitiveGuessMap.put("I64", tmp);
        tmp = new ArrayList<>(); sAnyGuess = new ALittleGuessPrimitive("any"); tmp.add(sAnyGuess); sPrimitiveGuessMap.put("any", tmp);
    }

    public ALittleGuessPrimitive(@NotNull String v) {
        super(v);
    }

    public boolean isChanged() {
        return false;
    }
}
