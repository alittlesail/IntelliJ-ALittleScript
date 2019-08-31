package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassDec;
import plugin.psi.ALittleClassNameDec;
import plugin.psi.ALittleTemplateDec;
import plugin.psi.ALittleTemplatePairDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassDecReference extends ALittleReference<ALittleClassDec> {
    public ALittleClassDecReference(@NotNull ALittleClassDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleClassNameDec classNameDec = myElement.getClassNameDec();
        if (classNameDec == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有定义类名");
        }

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_CLASS;
        info.value = mNamespace + "." + classNameDec.getIdContent().getText();
        info.element = myElement;
        ALittleTemplateDec templateDec = myElement.getTemplateDec();
        if (templateDec != null) {
            info.classTemplateList = templateDec.guessTypes();
            List<String> nameList = new ArrayList<>();
            for (ALittleReferenceUtil.GuessTypeInfo guessInfo : info.classTemplateList) {
                nameList.add(guessInfo.value);
            }
            info.value += "<" + String.join(",", nameList) + ">";
        }
        else
            info.classTemplateList = new ArrayList<>();

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
