package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleCustomType;
import plugin.psi.ALittleReflectValue;

import java.util.ArrayList;
import java.util.List;

public class ALittleReflectValueReference extends ALittleReference<ALittleReflectValue> {
    public ALittleReflectValueReference(@NotNull ALittleReflectValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("string");
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleCustomType customType = myElement.getCustomType();
        if (customType == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有指定反射对象");
        }

        ALittleReferenceUtil.GuessTypeInfo guessType = customType.guessType();
        if (!myElement.getContainingFile().equals(guessType.element.getContainingFile())) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "反射对象必须和反射操作在同一个文件");
        }
    }
}
