package plugin.reference;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessConst;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.ALittleConstValue;

import java.util.ArrayList;
import java.util.List;

public class ALittleConstValueReference extends ALittleReference<ALittleConstValue> {
    public ALittleConstValueReference(@NotNull ALittleConstValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        String text = myElement.getText();

        if (myElement.getNumberContent() != null)
        {
            if (PsiHelper.isInt(myElement.getNumberContent().getText()))
                return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("int");
            else
                return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("double");
        }
        else if (myElement.getTextContent() != null)
        {
            return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("string");
        }
        else if (text.equals("true") || text.equals("false"))
        {
            return ALittleGuessPrimitive.sPrimitiveGuessListMap.get("bool");
        }
        else if (text.equals("null"))
        {
            return ALittleGuessPrimitive.sConstNullGuess;
        }
        else
        {
            throw new ALittleGuessException(myElement, "未知的常量类型:" + text);
        }
    }

    @Override
    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        if (myElement.getNumberContent() != null) {
            Annotation anno = holder.createInfoAnnotation(myElement, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
            return;
        }

        if (myElement.getTextContent() != null) {
            Annotation anno = holder.createInfoAnnotation(myElement, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.STRING);
            return;
        }

        if (myElement.getText().equals("true") || myElement.getText().equals("false") || myElement.getText().equals("null")) {
            Annotation anno = holder.createInfoAnnotation(myElement, null);
            anno.setTextAttributes(ConsoleHighlighter.CYAN_BRIGHT);
            return;
        }
    }
}
