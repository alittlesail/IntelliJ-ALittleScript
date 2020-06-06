package plugin.reference;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.List;

public class ALittleCoroutineStatReference extends ALittleReference<ALittleCoroutineStat> {
    public ALittleCoroutineStatReference(@NotNull ALittleCoroutineStat element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        // 检查这次所在的函数必须要有await或者async修饰
        PsiElement parent = myElement;
        while (parent != null)
        {
            if (parent instanceof ALittleNamespaceDec)
            {
                break;
            }
                else if (parent instanceof ALittleClassCtorDec)
            {
                break;
            }
                else if (parent instanceof ALittleClassGetterDec)
            {
                break;
            }
                else if (parent instanceof ALittleClassSetterDec)
            {
                break;
            }
                else if (parent instanceof ALittleClassMethodDec)
            {
                List<ALittleModifier> modifier = ((ALittleClassElementDec)parent.getParent()).getModifierList();
                if (PsiHelper.getCoroutineType(modifier).equals("await"))
                    return;
                break;
            }
                else if (parent instanceof ALittleClassStaticDec)
            {
                List<ALittleModifier> modifier = ((ALittleClassElementDec)parent.getParent()).getModifierList();
                if (PsiHelper.getCoroutineType(modifier).equals("await"))
                    return;
                break;
            }
                else if (parent instanceof ALittleGlobalMethodDec)
            {
                List<ALittleModifier> modifier = ((ALittleNamespaceElementDec)parent.getParent()).getModifierList();
                if (PsiHelper.getCoroutineType(modifier).equals("await"))
                    return;
                break;
            }
            parent = parent.getParent();
        }

        throw  new ALittleGuessException(myElement, "co关键字只能在await修饰的函数中使用");
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return ALittleTreeChangeListener.findALittleStructGuessList(myElement.getProject(), "ALittle", "Thread");
    }
}
