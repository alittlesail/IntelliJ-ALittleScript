package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleStructDec;
import plugin.psi.ALittleStructNameDec;
import plugin.psi.ALittleStructVarDec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleStructDecReference extends ALittleReference<ALittleStructDec> {
    public ALittleStructDecReference(@NotNull ALittleStructDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleStructNameDec structNameDec = myElement.getStructNameDec();
        if (structNameDec == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有定义结构体名");
        }

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_STRUCT;
        info.value = mNamespace + "." + structNameDec.getIdContent().getText();
        info.element = myElement;

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleStructVarDec> varDecList = myElement.getStructVarDecList();
        Set<String> nameSet = new HashSet<>();
        for (ALittleStructVarDec varDec : varDecList) {
            PsiElement varNameDec = varDec.getIdContent();
            if (varNameDec == null) throw new ALittleReferenceUtil.ALittleReferenceException(varDec, "没有定义成员变量名");

            if (nameSet.contains(varNameDec.getText())) {
                throw new ALittleReferenceUtil.ALittleReferenceException(varNameDec, "结构体字段名重复");
            }
            nameSet.add(varNameDec.getText());
        }
    }
}
