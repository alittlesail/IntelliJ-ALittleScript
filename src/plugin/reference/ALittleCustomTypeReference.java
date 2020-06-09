package plugin.reference;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.ALittleCustomType;
import plugin.psi.ALittleCustomTypeDotId;
import plugin.psi.ALittleCustomTypeDotIdName;
import plugin.psi.ALittleCustomTypeName;

import java.util.List;

public class ALittleCustomTypeReference extends ALittleCustomTypeCommonReference<ALittleCustomType> {
    public ALittleCustomTypeReference(@NotNull ALittleCustomType element, TextRange textRange) {
        super(element, element, textRange);

        mNamespace = PsiHelper.getNamespaceName(myElement);
        ALittleCustomTypeName name_dec = myElement.getCustomTypeName();
        if (name_dec != null) mKey = name_dec.getText();

        ALittleCustomTypeDotId dot_id = myElement.getCustomTypeDotId();
        if (dot_id != null) {
            ALittleCustomTypeDotIdName dot_id_name = dot_id.getCustomTypeDotIdName();
            if (dot_id_name != null) {
                mNamespace = mKey;
                mKey = dot_id_name.getText();
            }
        }
    }

    @Override
    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getCustomTypeName(), null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
