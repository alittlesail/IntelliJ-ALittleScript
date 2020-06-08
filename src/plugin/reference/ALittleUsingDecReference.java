package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleUsingDecReference extends ALittleReference<ALittleUsingDec> {
    public ALittleUsingDecReference(@NotNull ALittleUsingDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        ALittleUsingNameDec name_dec = myElement.getUsingNameDec();
        if (name_dec == null) throw new ALittleGuessException(myElement, "没有定义using的名称");

        if (myElement.getAllType() != null) {
            guess_list = myElement.getAllType().guessTypes();

            boolean has_template = false;
            for (ALittleGuess guess : guess_list) {
                if (guess instanceof ALittleGuessClass) {
                    ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                    if (guess_class.template_list.size() > 0) {
                        has_template = true;
                        break;
                    }
                }
            }
            if (!has_template) return guess_list;

            List<ALittleGuess> new_guess_list = new ArrayList<>();
            for (ALittleGuess guess : guess_list) {
                if (guess instanceof ALittleGuessClass) {
                    ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                    if (guess_class.template_list.size() == 0) {
                        new_guess_list.add(guess);
                    } else {
                        guess_class = (ALittleGuessClass) guess_class.clone();
                        guess_class.using_name = mNamespace + "." + name_dec.getText();
                        new_guess_list.add(guess_class);
                    }
                } else {
                    new_guess_list.add(guess);
                }
            }

            guess_list = new_guess_list;
        }
        return guess_list;
    }
}
