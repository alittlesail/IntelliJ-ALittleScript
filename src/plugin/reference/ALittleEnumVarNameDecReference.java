package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleEnumVarNameDecReference extends ALittleReference {
    public ALittleEnumVarNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        ALittleEnumVarDec enumVarDec = (ALittleEnumVarDec)myElement.getParent();
        if (enumVarDec.getEnumVarValueDec() == null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "int";
            info.element = myElement;
            guessList.add(info);
        } else if (enumVarDec.getEnumVarValueDec().getStringContent() != null) {
            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
            info.value = "string";
            info.element = myElement;
            guessList.add(info);
        }

        return guessList;
    }
}
