package plugin.reference;

import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignNameDecReference extends ALittleReference<ALittleVarAssignNameDec> {
    public ALittleVarAssignNameDecReference(@NotNull ALittleVarAssignNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleVarAssignDec) {
            return ((ALittleVarAssignDec) parent).guessTypes();
        } else if (parent instanceof ALittleForPairDec) {
            return ((ALittleForPairDec) parent).guessTypes();
        }
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "局部变量名不能以3个下划线开头");
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
        }
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleForPairDec) {
            Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
        }
    }
}
