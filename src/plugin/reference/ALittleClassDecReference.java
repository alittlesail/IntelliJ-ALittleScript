package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassDec;
import plugin.psi.ALittleClassNameDec;

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

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
