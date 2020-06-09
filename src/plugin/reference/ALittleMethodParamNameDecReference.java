package plugin.reference;

import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
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
        ALittleMethodParamOneDec one_dec = (ALittleMethodParamOneDec)parent;
        if (one_dec != null)
        {
            ALittleAllType all_type = one_dec.getAllType();
            if (all_type != null)
                return all_type.guessTypes();
        }

        return new ArrayList<>();
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement, null);
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
