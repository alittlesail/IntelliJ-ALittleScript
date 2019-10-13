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
        ALittleUsingNameDec nameDec = myElement.getUsingNameDec();
        if (nameDec == null) throw new ALittleGuessException(myElement, "没有定义using的名称");

        if (myElement.getAllType() != null) {
            List<ALittleGuess> guessList = myElement.getAllType().guessTypes();

            boolean hasTemplate = false;
            for (ALittleGuess guess : guessList) {
                if (guess instanceof ALittleGuessClass) {
                    ALittleGuessClass guessClass = (ALittleGuessClass) guess;
                    if (!guessClass.templateList.isEmpty()) {
                        hasTemplate = true;
                        break;
                    }
                }
            }
            if (!hasTemplate) return guessList;

            List<ALittleGuess> newGuessList = new ArrayList<>();
            for (ALittleGuess guess : guessList) {
                if (guess instanceof ALittleGuessClass) {
                    ALittleGuessClass guessClass = (ALittleGuessClass) guess;
                    if (guessClass.templateList.isEmpty()) {
                        newGuessList.add(guess);
                    } else {
                        guessClass = guessClass.Clone();
                        guessClass.usingName = mNamespace + "." + nameDec.getText();
                        newGuessList.add(guessClass);
                    }
                } else {
                    newGuessList.add(guess);
                }
            }

            return newGuessList;
        }
        return new ArrayList<>();
    }
}
