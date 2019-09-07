package plugin.reference;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.List;

public class ALittleCustomTypeDotIdNameReference extends ALittleCustomTypeCommonReference<ALittleCustomTypeDotIdName> {
   public ALittleCustomTypeDotIdNameReference(@NotNull ALittleCustomTypeDotIdName element, TextRange textRange) {
        super((ALittleCustomType)element.getParent().getParent(), element, textRange);

        ALittleCustomType customType = (ALittleCustomType)element.getParent().getParent();
        mNamespace = customType.getIdContent().getText();
        mKey = element.getIdContent().getText();
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
        }
    }
}
