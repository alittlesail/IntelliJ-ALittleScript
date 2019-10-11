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

public class ALittleMethodParamNameDecReference extends ALittleReference<ALittleMethodParamNameDec> {
    public ALittleMethodParamNameDecReference(@NotNull ALittleMethodParamNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleMethodParamOneDec) {
            return ((ALittleMethodParamOneDec) parent).getAllType().guessTypes();
        }
        return new ArrayList<>();
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
        anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
