package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.ALittleConstValue;

import java.util.ArrayList;
import java.util.List;

public class ALittleConstValueReference extends ALittleReference<ALittleConstValue> {
    public ALittleConstValueReference(@NotNull ALittleConstValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        if (myElement.getDigitContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            String value = myElement.getDigitContent().getText();
            if (ALittleUtil.isInt(value))
                info.value = "int";
            else
                info.value = "double";
            info.element = myElement;
            guessList.add(info);
        } else if (myElement.getStringContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "string";
            info.element = myElement;
            guessList.add(info);
        } else if (myElement.getText().equals("true") || myElement.getText().equals("false")) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "bool";
            info.element = myElement;
            guessList.add(info);
        } else if (myElement.getText().equals("null")) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_CONST;
            info.value = "null";
            info.element = myElement;
            guessList.add(info);
        }
        return guessList;
    }
}
