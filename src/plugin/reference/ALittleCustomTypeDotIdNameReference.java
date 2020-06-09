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

public class ALittleCustomTypeDotIdNameReference extends ALittleCustomTypeCommonReference<ALittleCustomTypeDotIdName> {
   public ALittleCustomTypeDotIdNameReference(@NotNull ALittleCustomTypeDotIdName element, TextRange textRange) {
        super((ALittleCustomType)element.getParent().getParent(), element, textRange);

       ALittleCustomType custom_type = (ALittleCustomType)element.getParent().getParent();
       ALittleCustomTypeName custom_type_name = custom_type.getCustomTypeName();
       if (custom_type_name != null)
           mNamespace = custom_type_name.getText();
       else
           mNamespace = "";
       mKey = myElement.getText();
    }

    @Override
    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement, null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    }
}
