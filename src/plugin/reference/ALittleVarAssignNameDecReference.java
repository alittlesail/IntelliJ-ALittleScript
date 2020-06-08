package plugin.reference;

import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignNameDecReference extends ALittleReference<ALittleVarAssignNameDec> {
    private PsiElement mMethodDec;
    private ALittleMethodBodyDec mMethodBodyDec;

    public ALittleVarAssignNameDecReference(@NotNull ALittleVarAssignNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    private void ReloadInfo() {
        mMethodDec = null;
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                break;
            } else if (parent instanceof ALittleClassDec) {
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassCtorDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassSetterDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassSetterDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassGetterDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassGetterDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassMethodDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassMethodDec) parent).getMethodBodyDec();
                break;

            } else if (parent instanceof ALittleClassStaticDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassStaticDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleGlobalMethodDec) parent).getMethodBodyDec();
                break;
            }

            parent = parent.getParent();
        }
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleVarAssignDec) {
            return ((ALittleVarAssignDec) parent).guessTypes();
        } else if (parent instanceof ALittleForPairDec) {
            return ((ALittleForPairDec) parent).guessTypes();
        }
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "局部变量名不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleForPairDec) {
            Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
        }
    }
}
