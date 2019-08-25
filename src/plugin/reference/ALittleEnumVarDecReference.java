package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.List;

public class ALittleEnumVarDecReference extends ALittleReference<ALittleEnumVarDec> {
    public ALittleEnumVarDecReference(@NotNull ALittleEnumVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getStringContent() != null) {
            return ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("string");
        } else {
            return ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("int");
        }
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getDigitContent() == null) return;

        String value = myElement.getDigitContent().getText();
        if (!PsiHelper.isInt(value)) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement.getDigitContent(), "枚举值必须是整数");
        }
    }
}
