package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.ALittleConstValue;

import java.util.ArrayList;
import java.util.List;

public class ALittleConstValueReference extends ALittleReference {
    public ALittleConstValueReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ALittleConstValue dec = (ALittleConstValue)myElement;
        if (dec.getDigitContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            String value = dec.getDigitContent().getText();
            if (ALittleUtil.isInt(value))
                info.value = "int";
            else
                info.value = "double";
            info.element = myElement;
            guessList.add(info);
        } else if (dec.getStringContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "string";
            info.element = myElement;
            guessList.add(info);
        } else if (dec.getText().equals("true") || dec.getText().equals("false")) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "bool";
            info.element = myElement;
            guessList.add(info);
        } else if (dec.getText().equals("null")) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_CONST;
            info.value = "null";
            info.element = myElement;
            guessList.add(info);
        }
        return guessList;
    }
}
