package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleEnumDec;
import plugin.psi.ALittleEnumNameDec;
import plugin.psi.ALittleEnumVarDec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleEnumDecReference extends ALittleReference<ALittleEnumDec> {
    public ALittleEnumDecReference(@NotNull ALittleEnumDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleEnumNameDec enumNameDec = myElement.getEnumNameDec();
        if (enumNameDec == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有定义枚举名");
        }

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_ENUM;
        info.value = mNamespace + "." + enumNameDec.getIdContent().getText();
        info.element = myElement;

        List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = new ArrayList<>();
        guessTypeList.add(info);
        return guessTypeList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleEnumVarDec> varDecList = myElement.getEnumVarDecList();
        Set<String> nameSet = new HashSet<>();
        for (ALittleEnumVarDec varDec : varDecList) {
            PsiElement varNameDec = varDec.getIdContent();
            if (nameSet.contains(varNameDec.getText())) {
                throw new ALittleReferenceUtil.ALittleReferenceException(varNameDec, "枚举字段名重复");
            }
            nameSet.add(varNameDec.getText());
        }
    }
}
