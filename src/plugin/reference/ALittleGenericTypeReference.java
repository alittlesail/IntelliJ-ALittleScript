package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
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

            ALittleGuess info = new ALittleGuess();
            info.type = ALittleReferenceUtil.GuessType.GT_LIST;
            info.value = "List<" + guessInfo.value + ">";
            info.element = myElement;
            info.listSubType = guessInfo;
            guessList.add(info);
        } else if (myElement.getGenericMapType() != null) {
            ALittleGenericMapType dec = myElement.getGenericMapType();
            List<ALittleAllType> allTypeList = dec.getAllTypeList();
            if (allTypeList.size() != 2) return guessList;

            ALittleGuess keyGuessInfo = allTypeList.get(0).guessType();
            ALittleGuess valueGuessInfo = allTypeList.get(1).guessType();

            ALittleGuess info = new ALittleGuess();
            info.type = ALittleReferenceUtil.GuessType.GT_MAP;
            info.value = "Map<" + keyGuessInfo.value + "," + valueGuessInfo.value + ">";
            info.element = myElement;
            info.mapKeyType = keyGuessInfo;
            info.mapValueType = valueGuessInfo;
            guessList.add(info);
        } else if (myElement.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = myElement.getGenericFunctorType();
            ALittleGenericFunctorParamType paramType = dec.getGenericFunctorParamType();

            ALittleGuess info = new ALittleGuess();
            info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
            info.value = "Functor<(";
            info.element = myElement;
            info.functorParamList = new ArrayList<>();
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();
            info.functorAwait = (dec.getCoModifier() != null && dec.getCoModifier().getText().equals("await"));
            if (info.functorAwait) {
                info.value = "Functor<await(";
            }

            if (paramType != null) {
                List<String> nameList = new ArrayList<>();
                List<ALittleAllType> allTypeList = paramType.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleGuess guessInfo = allType.guessType();
                    nameList.add(guessInfo.value);
                    info.functorParamList.add(guessInfo);
                    info.functorParamNameList.add(guessInfo.value);
                }
                info.value += String.join(",", nameList);
            }
            info.value += ")";
            ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
            if (return_type != null) {
                List<String> nameList = new ArrayList<>();
                List<ALittleAllType> allTypeList = return_type.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleGuess GuessInfo = allType.guessType();
                    nameList.add(GuessInfo.value);
                    info.functorReturnList.add(GuessInfo);
                }
                if (!nameList.isEmpty()) info.value += ":";
                info.value += String.join(",", nameList);
            }
            info.value += ">";
            guessList.add(info);
        }

        return guessList;
    }
}
