package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface ALittleReferenceInterface {
    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException;

    public void colorAnnotator(@NotNull AnnotationHolder holder);
    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleReferenceUtil.ALittleReferenceException;
    @NotNull
    public abstract List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException;
}
