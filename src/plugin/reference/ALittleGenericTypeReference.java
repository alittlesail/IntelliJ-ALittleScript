package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleGenericTypeReference extends ALittleReference<ALittleGenericType> {
    public ALittleGenericTypeReference(@NotNull ALittleGenericType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        if (myElement.getGenericListType() != null) {
            ALittleGenericListType dec = myElement.getGenericListType();
            ALittleAllType allType = dec.getAllType();
            if (allType == null) return guessList;
            ALittleGuess guessInfo = allType.guessType();

            ALittleGuessList info = new ALittleGuessList(guessInfo);
            info.UpdateValue();
            guessList.add(info);
        } else if (myElement.getGenericMapType() != null) {
            ALittleGenericMapType dec = myElement.getGenericMapType();
            List<ALittleAllType> allTypeList = dec.getAllTypeList();
            if (allTypeList.size() != 2) return guessList;

            ALittleGuess keyGuessInfo = allTypeList.get(0).guessType();
            ALittleGuess valueGuessInfo = allTypeList.get(1).guessType();

            ALittleGuessMap info = new ALittleGuessMap(keyGuessInfo, valueGuessInfo);
            info.UpdateValue();
            guessList.add(info);
        } else if (myElement.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = myElement.getGenericFunctorType();
            ALittleGenericFunctorParamType paramType = dec.getGenericFunctorParamType();

            ALittleGuessFunctor info = new ALittleGuessFunctor(myElement);
            info.functorAwait = (dec.getCoModifier() != null && dec.getCoModifier().getText().equals("await"));

            if (paramType != null) {
                List<ALittleAllType> allTypeList = paramType.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleGuess guess = allType.guessType();
                    info.functorParamList.add(guess);
                    info.functorParamNameList.add(guess.value);
                }
                ALittleGenericFunctorParamTail paramTail = paramType.getGenericFunctorParamTail();
                if (paramTail != null) {
                    info.functorParamTail = new ALittleGuessParamTail(paramTail.getText());
                }
            }
            ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
            if (return_type != null) {
                List<ALittleAllType> allTypeList = return_type.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    info.functorReturnList.add(allType.guessType());
                }
                ALittleGenericFunctorReturnTail returnTail = return_type.getGenericFunctorReturnTail();
                if (returnTail != null) {
                    info.functorReturnTail = new ALittleGuessReturnTail(returnTail.getText());
                }
            }
            info.UpdateValue();
            guessList.add(info);
        }

        return guessList;
    }
}
