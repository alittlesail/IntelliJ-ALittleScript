package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessClassTemplate;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleAllType;
import plugin.psi.ALittleTemplateExtendsClassDec;
import plugin.psi.ALittleTemplateExtendsStructDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleTemplatePairDecReference extends ALittleReference<ALittleTemplatePairDec> {
    public ALittleTemplatePairDecReference(@NotNull ALittleTemplatePairDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        ALittleAllType allType = myElement.getAllType();
        ALittleTemplateExtendsClassDec extendsClassDec = myElement.getTemplateExtendsClassDec();
        ALittleTemplateExtendsStructDec extendsStructDec = myElement.getTemplateExtendsStructDec();

        ALittleGuess templateExtends = null;
        boolean isClass = false;
        boolean isStruct = false;
        if (allType != null) {
            ALittleGuess guess = allType.guessType();
            if (!(guess instanceof ALittleGuessClass)) {
                throw new ALittleGuessException(allType, "继承的对象必须是一个类");
            }
            templateExtends = guess;
        } else if (extendsClassDec != null) {
            isClass = true;
        } else if (extendsStructDec != null) {
            isStruct = true;
        }
        ALittleGuessClassTemplate info = new ALittleGuessClassTemplate(myElement, templateExtends, isClass, isStruct);
        info.UpdateValue();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getIdContent().getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "局部变量名不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
