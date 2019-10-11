package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessEnum;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleEnumDec;
import plugin.psi.ALittleEnumNameDec;
import plugin.psi.ALittleEnumVarDec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleEnumDecReference extends ALittleReference<ALittleEnumDec> {
    public ALittleEnumDecReference(@NotNull ALittleEnumDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleEnumNameDec enumNameDec = myElement.getEnumNameDec();
        if (enumNameDec == null) {
            throw new ALittleGuessException(myElement, "没有定义枚举名");
        }

        ALittleGuessEnum info = new ALittleGuessEnum(mNamespace + "." + enumNameDec.getIdContent().getText(), myElement);

        List<ALittleGuess> guessTypeList = new ArrayList<>();
        guessTypeList.add(info);
        return guessTypeList;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleEnumVarDec> varDecList = myElement.getEnumVarDecList();
        Set<String> nameSet = new HashSet<>();
        for (ALittleEnumVarDec varDec : varDecList) {
            PsiElement varNameDec = varDec.getIdContent();
            if (nameSet.contains(varNameDec.getText())) {
                throw new ALittleGuessException(varNameDec, "枚举字段名重复");
            }
            nameSet.add(varNameDec.getText());
        }
    }
}
