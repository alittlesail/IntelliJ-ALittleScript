package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

import java.util.List;

public interface ALittleReferenceInterface {
    public void checkError() throws ALittleGuessException;

    public void colorAnnotator(@NotNull AnnotationHolder holder);
    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleGuessException;
    @NotNull
    public abstract List<ALittleGuess> guessTypes() throws ALittleGuessException;

    public abstract boolean multiGuessTypes();
}
