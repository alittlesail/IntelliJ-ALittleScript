package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAllType;

import java.util.ArrayList;
import java.util.List;

public class ALittleReflectValueReference extends ALittleReference {
    public ALittleReflectValueReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
        info.value = "string";
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
