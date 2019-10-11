package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;
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
        ALittleGuess info = new ALittleGuess();
        info.type = ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE;
        info.value = myElement.getIdContent().getText();
        info.element = myElement;
        ALittleAllType allType = myElement.getAllType();
        if (allType != null) {
            info.classTemplateExtends = allType.guessType();
            if (info.classTemplateExtends.type != ALittleReferenceUtil.GuessType.GT_CLASS) {
                throw new ALittleGuessException(allType, "继承的对象必须是一个类");
            }
        }

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
