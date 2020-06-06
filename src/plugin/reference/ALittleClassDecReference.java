package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassDecReference extends ALittleReference<ALittleClassDec> {
    public ALittleClassDecReference(@NotNull ALittleClassDec element, TextRange textRange) {
        super(element, textRange);
        mNamespace = PsiHelper.getNamespaceName(element);
    }

    @Override
    public void checkError() throws ALittleGuessException {

        ALittleClassNameDec name_dec = myElement.getClassNameDec();
        if (name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义类名");

        ALittleClassBodyDec body_dec = myElement.getClassBodyDec();
        if (body_dec == null)
            throw new ALittleGuessException(myElement, "没有定义类体");
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleClassNameDec name_dec = myElement.getClassNameDec();
        if (name_dec == null)
            throw new ALittleGuessException(myElement, "没有定义类名");

        ALittleClassBodyDec body_dec = myElement.getClassBodyDec();
        if (body_dec == null)
            throw new ALittleGuessException(myElement, "没有定义类体");

        ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec)myElement.getParent();
        if (namespace_element_dec == null)
            throw new ALittleGuessException(myElement, "ALittleClassDecReference的父节点不是ALittleNamespaceElementDecElement");

        boolean is_native = PsiHelper.isNative(namespace_element_dec.getModifierList());
        ALittleGuessClass info = new ALittleGuessClass(mNamespace, name_dec.getText(), myElement, null, false, is_native);
        ALittleTemplateDec template_dec = myElement.getTemplateDec();
        if (template_dec != null)
        {
            info.template_list = template_dec.guessTypes();
        }
        info.updateValue();

        List<ALittleGuess> guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }
}
