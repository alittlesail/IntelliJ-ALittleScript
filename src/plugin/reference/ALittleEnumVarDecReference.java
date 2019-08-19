package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleEnumVarDecReference extends ALittleReference<ALittleEnumVarDec> {
    public ALittleEnumVarDecReference(@NotNull ALittleEnumVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        if (myElement.getStringContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "string";
            info.element = myElement;
            guessList.add(info);
        } else {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "int";
            info.element = myElement;
            guessList.add(info);
        }

        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getDigitContent() == null) return;

        String value = myElement.getDigitContent().getText();
        if (!ALittleUtil.isInt(value)) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement.getDigitContent(), "枚举值必须是整数");
        }
    }
}
