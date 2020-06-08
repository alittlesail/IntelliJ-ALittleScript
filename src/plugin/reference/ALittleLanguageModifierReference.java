package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuessException;
import plugin.module.ALittleConfig;
import plugin.psi.*;

import java.util.HashSet;
import java.util.List;

public class ALittleLanguageModifierReference extends ALittleReference<ALittleLanguageModifier> {
    private HashSet<String> m_name_set = null;

    public ALittleLanguageModifierReference(@NotNull ALittleLanguageModifier element, TextRange textRange) {
        super(element, textRange);
    }

    public void checkError() throws ALittleGuessException {
        ALittleLanguageBodyDec body_dec = myElement.getLanguageBodyDec();
        if (body_dec == null)
            throw new ALittleGuessException(myElement, "请定义你限定的语言范围");

        if (body_dec.getLanguageNameDecList().size() == 0)
            throw new ALittleGuessException(myElement, "请定义你限定的语言范围");
    }

    public boolean isLanguageEnable()
    {
        if (m_name_set == null)
        {
            m_name_set = new HashSet<>();

            ALittleLanguageBodyDec body_dec = myElement.getLanguageBodyDec();
            if (body_dec == null) return false;

            List<ALittleLanguageNameDec> name_list = body_dec.getLanguageNameDecList();
            for (ALittleLanguageNameDec name : name_list)
            {
                String text = name.getText();
                m_name_set.add(text);
            }
        }

        return m_name_set.contains(ALittleConfig.getConfig(myElement.getProject()).getTargetLanguage());
    }
}
