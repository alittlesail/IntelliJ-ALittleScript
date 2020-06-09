package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.ALittleAllType;

import java.util.ArrayList;
import java.util.List;

public class ALittleAllTypeReference extends ALittleReference<ALittleAllType> {
    public ALittleAllTypeReference(@NotNull ALittleAllType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        boolean is_const = myElement.getAllTypeConst() != null;

        if (myElement.getCustomType() != null)
            guess_list = myElement.getCustomType().guessTypes();
        else if (myElement.getGenericType() != null)
            guess_list = myElement.getGenericType().guessTypes();
        else if (myElement.getPrimitiveType() != null)
            guess_list = myElement.getPrimitiveType().guessTypes();

        if (guess_list.size() > 0) {
            if (!is_const) return guess_list;

            for (int i = 0; i < guess_list.size(); ++i) {
                ALittleGuess guess = guess_list.get(i);
                if (guess.is_const) continue;

                if (guess instanceof ALittleGuessPrimitive) {
                    guess_list = ALittleGuessPrimitive.sPrimitiveGuessListMap.get("const " + guess.getValue());
                    if (guess_list == null) throw new ALittleGuessException(myElement, "找不到const " + guess.getValue());
                    break;
                } else {
                    guess = guess.clone();
                    guess.is_const = true;
                    guess.updateValue();
                }

                guess_list.set(i, guess);
            }
            return guess_list;
        }

        throw new ALittleGuessException(myElement, "AllType出现未知的子节点");
    }
}
