package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessEnum;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

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
        ALittleEnumNameDec name_dec = myElement.getEnumNameDec();
        if (name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义枚举名");

        ALittleGuessEnum info = new ALittleGuessEnum(mNamespace, name_dec.getText(), myElement);
        info.updateValue();
        List<ALittleGuess> guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    public void checkError() throws ALittleGuessException {
        ALittleEnumNameDec enum_name_dec = myElement.getEnumNameDec();
        if (enum_name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义枚举名");

        ALittleEnumBodyDec body_dec = myElement.getEnumBodyDec();
        if (body_dec == null)
            throw new ALittleGuessException(myElement, "没有定义枚举内容");

        List<ALittleEnumVarDec> var_dec_list = body_dec.getEnumVarDecList();
        Set<String> name_set = new HashSet<>();
        for (ALittleEnumVarDec var_dec : var_dec_list)
        {
            ALittleEnumVarNameDec name_dec = var_dec.getEnumVarNameDec();
            if (name_dec == null) continue;

            String name = name_dec.getText();
            if (name_set.contains(name))
                throw new ALittleGuessException(name_dec, "枚举字段名重复");
            name_set.add(name);
        }
    }
}
