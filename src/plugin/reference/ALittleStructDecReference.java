package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessStruct;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleStructDecReference extends ALittleReference<ALittleStructDec> {
    public ALittleStructDecReference(@NotNull ALittleStructDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = null;
        ALittleStructNameDec struct_name_dec = myElement.getStructNameDec();
        if (struct_name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义结构体名");

        ALittleGuessStruct info = new ALittleGuessStruct(mNamespace, struct_name_dec.getText(), myElement, false);
        info.updateValue();
        guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleStructNameDec struct_name_dec = myElement.getStructNameDec();
        if (struct_name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义结构体名");

        ALittleStructBodyDec body_dec = myElement.getStructBodyDec();
        if (body_dec == null)
            throw new ALittleGuessException(myElement, "没有定义结构体内容");

        List<ALittleStructVarDec> var_dec_list = body_dec.getStructVarDecList();
        Set<String> name_set = new HashSet<>();
        for (ALittleStructVarDec var_dec : var_dec_list)
        {
            ALittleStructVarNameDec var_name_dec = var_dec.getStructVarNameDec();
            if (var_name_dec == null) throw new ALittleGuessException(var_dec, "没有定义成员变量名");

            String text = var_name_dec.getText();
            if (name_set.contains(text))
                throw new ALittleGuessException(var_name_dec, "结构体字段名重复");
            name_set.add(text);
        }

        List<ALittleStructOptionDec> option_dec_list = body_dec.getStructOptionDecList();
        Set<String> option_set = new HashSet<String>();
        for (ALittleStructOptionDec option_dec : option_dec_list)
        {
            ALittleStructOptionNameDec option_name_dec = option_dec.getStructOptionNameDec();
            if (option_name_dec == null) throw new ALittleGuessException(option_dec, "没有定义附加信息名");

            String text = option_name_dec.getText();
            if (option_set.contains(text))
                throw new ALittleGuessException(option_name_dec, "附加信息名重复");
            option_set.add(text);

            PsiElement option_value = option_dec.getTextContent();
            if (option_value == null) throw new ALittleGuessException(option_dec, text + "没有设置对应的值");

            if (text.equals("primary"))
            {
                text = option_value.getText().trim();
                text = text.substring(1, text.length() - 2).trim();
                if (!name_set.contains(text))
                    throw new ALittleGuessException(option_value, "没有找到对应的字段名:" + text);
                continue;
            }

            if (text.equals("unique") || text.equals("index"))
            {
                text = option_value.getText().trim();
                text = text.substring(1, text.length() - 2).trim();
                String[] list = text.split(",");
                for (String name : list)
                {
                    text = name.trim();
                    if (!name_set.contains(text))
                        throw new ALittleGuessException(option_value, "没有找到对应的字段名:" + text);
                }
                continue;
            }
        }
    }
}
