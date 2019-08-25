package plugin.reference;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.ALittleConstValue;

import java.util.ArrayList;
import java.util.List;

public class ALittleConstValueReference extends ALittleReference<ALittleConstValue> {
    public ALittleConstValueReference(@NotNull ALittleConstValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = new ArrayList<>();
        if (myElement.getDigitContent() != null) {
            if (PsiHelper.isInt(myElement.getDigitContent().getText()))
                guessTypeList = ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("int");
            else
                guessTypeList = ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("double");
        } else if (myElement.getStringContent() != null) {
            guessTypeList = ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("string");
        } else if (myElement.getText().equals("true") || myElement.getText().equals("false")) {
            guessTypeList = ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("bool");
        } else if (myElement.getText().equals("null")) {
            guessTypeList = ALittleReferenceUtil.sConstNullGuessType;
        } else {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知的常量类型:" + myElement.getText());
        }
        return guessTypeList;
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        if (myElement.getDigitContent() != null) {
            Annotation anno = holder.createInfoAnnotation(myElement, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
            return;
        }

        if (myElement.getStringContent() != null) {
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
