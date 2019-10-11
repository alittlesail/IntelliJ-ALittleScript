package plugin.reference;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.List;

public class ALittleCustomTypeReference extends ALittleCustomTypeCommonReference<ALittleCustomType> {
    public ALittleCustomTypeReference(@NotNull ALittleCustomType element, TextRange textRange) {
        super(element, element, textRange);

        mKey = element.getIdContent().getText();

        ALittleCustomTypeDotId dotId = element.getCustomTypeDotId();
        if (dotId != null) {
            ALittleCustomTypeDotIdName dotIdName = dotId.getCustomTypeDotIdName();
            if (dotIdName != null) {
                mNamespace = mKey;
                mKey = dotIdName.getText();
            }
        }
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
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
