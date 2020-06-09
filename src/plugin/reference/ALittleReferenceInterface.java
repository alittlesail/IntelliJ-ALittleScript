package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

import java.util.List;

public interface ALittleReferenceInterface {
    void checkError() throws ALittleGuessException;

    void colorAnnotator(@NotNull AnnotationHolder holder);

    @NotNull
    List<InlayInfo> getParameterHints() throws ALittleGuessException;

    @NotNull
    List<ALittleGuess> guessTypes() throws ALittleGuessException;

    boolean multiGuessTypes();
}
