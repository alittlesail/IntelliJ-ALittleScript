package plugin;

import com.intellij.lang.annotation.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.reference.ALittleReference;
import plugin.reference.ALittleReferenceUtil;

public class ALittleAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        try {
            // 检查错误，给元素上色
            PsiReference ref = element.getReference();
            if (ref instanceof ALittleReference) {
                ((ALittleReference) ref).checkError();
                ((ALittleReference) ref).colorAnnotator(holder);
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            holder.createErrorAnnotation(e.getElement(), e.getError());
        }
    }
}