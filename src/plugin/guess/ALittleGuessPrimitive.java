package plugin.guess;

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
    public static ALittleGuess sLongGuess;
    public static ALittleGuess sAnyGuess;

    public static ALittleGuess sConstIntGuess;
    public static ALittleGuess sConstDoubleGuess;
    public static ALittleGuess sConstStringGuess;
    public static ALittleGuess sConstBoolGuess;
    public static ALittleGuess sConstLongGuess;
    public static ALittleGuess sConstAnyGuess;

    public static Map<String, List<ALittleGuess>> sPrimitiveGuessListMap;
    public static Map<String, ALittleGuess> sPrimitiveGuessMap;

    public static List<ALittleGuess> sConstNullGuess;

    static {
        // 基础变量
        sPrimitiveGuessMap = new HashMap<>();
        sPrimitiveGuessListMap = new HashMap<>();
        List<ALittleGuess> tmp;
        tmp = new ArrayList<>();
        sIntGuess = new ALittleGuessInt(false);
        tmp.add(sIntGuess);
        sPrimitiveGuessListMap.put(sIntGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sIntGuess.getValue(), sIntGuess);
        tmp = new ArrayList<>();
        sDoubleGuess = new ALittleGuessDouble(false);
        tmp.add(sDoubleGuess);
        sPrimitiveGuessListMap.put(sDoubleGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sDoubleGuess.getValue(), sDoubleGuess);
        tmp = new ArrayList<>();
        sStringGuess = new ALittleGuessString(false);
        tmp.add(sStringGuess);
        sPrimitiveGuessListMap.put(sStringGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sStringGuess.getValue(), sStringGuess);
        tmp = new ArrayList<>();
        sBoolGuess = new ALittleGuessBool(false);
        tmp.add(sBoolGuess);
        sPrimitiveGuessListMap.put(sBoolGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sBoolGuess.getValue(), sBoolGuess);
        tmp = new ArrayList<>();
        sLongGuess = new ALittleGuessLong(false);
        tmp.add(sLongGuess);
        sPrimitiveGuessListMap.put(sLongGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sLongGuess.getValue(), sLongGuess);
        tmp = new ArrayList<>();
        sAnyGuess = new ALittleGuessAny(false);
        tmp.add(sAnyGuess);
        sPrimitiveGuessListMap.put(sAnyGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sAnyGuess.getValue(), sAnyGuess);

        tmp = new ArrayList<>();
        sConstIntGuess = new ALittleGuessInt(true);
        tmp.add(sConstIntGuess);
        sPrimitiveGuessListMap.put(sConstIntGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstIntGuess.getValue(), sConstIntGuess);
        tmp = new ArrayList<>();
        sConstDoubleGuess = new ALittleGuessDouble(true);
        tmp.add(sConstDoubleGuess);
        sPrimitiveGuessListMap.put(sConstDoubleGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstDoubleGuess.getValue(), sConstDoubleGuess);
        tmp = new ArrayList<>();
        sConstStringGuess = new ALittleGuessString(true);
        tmp.add(sConstStringGuess);
        sPrimitiveGuessListMap.put(sConstStringGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstStringGuess.getValue(), sConstStringGuess);
        tmp = new ArrayList<>();
        sConstBoolGuess = new ALittleGuessBool(true);
        tmp.add(sConstBoolGuess);
        sPrimitiveGuessListMap.put(sConstBoolGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstBoolGuess.getValue(), sConstBoolGuess);
        tmp = new ArrayList<>();
        sConstLongGuess = new ALittleGuessLong(true);
        tmp.add(sConstLongGuess);
        sPrimitiveGuessListMap.put(sConstLongGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstLongGuess.getValue(), sConstLongGuess);
        tmp = new ArrayList<>();
        sConstAnyGuess = new ALittleGuessAny(true);
        tmp.add(sConstAnyGuess);
        sPrimitiveGuessListMap.put(sConstAnyGuess.getValue(), tmp);
        sPrimitiveGuessMap.put(sConstAnyGuess.getValue(), sConstAnyGuess);

        // null常量
        sConstNullGuess = new ArrayList<>();
        sConstNullGuess.add(new ALittleGuessConst("null"));
    }

    private String native_value = "";

    public ALittleGuessPrimitive(String p_value, boolean p_is_const) {
        is_const = p_is_const;
        native_value = p_value;
        updateValue();
    }

    @Override
    public boolean hasAny() {
        return value.equals("any") || value.equals("const any");
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
        return new ALittleGuessPrimitive(native_value, is_const);
    }

    @Override
    public void updateValue() {
        value = "";
        if (is_const) value += "const ";
        value += native_value;
    }

    @Override
    public boolean isChanged() {
        return false;
    }
}
