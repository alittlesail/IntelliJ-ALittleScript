package plugin.reference;

import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueThisTypeReference extends ALittleReference<ALittlePropertyValueThisType> {
    private ALittleClassDec mClassDec = null;
    private ALittleClassCtorDec mClassCtorDec = null;
    private ALittleClassSetterDec mClassSetterDec = null;
    private ALittleClassMethodDec mClassMethodDec = null;
    private ALittleClassStaticDec mClassStaticDec = null;
    private ALittleGlobalMethodDec mGlobalMethodDec = null;

    public ALittlePropertyValueThisTypeReference(@NotNull ALittlePropertyValueThisType element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
    }

    private void reloadInfo() {
        mClassDec = null;
        mClassCtorDec = null;
        mClassSetterDec = null;
        mClassMethodDec = null;
        mClassStaticDec = null;

        PsiElement parent = myElement;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleNamespaceDec) {
                break;
            } else if (parent instanceof ALittleClassDec) {
                mClassDec = (ALittleClassDec)parent;
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                mClassCtorDec = (ALittleClassCtorDec)parent;
            } else if (parent instanceof ALittleClassSetterDec) {
                mClassSetterDec = (ALittleClassSetterDec)parent;
            } else if (parent instanceof ALittleClassMethodDec) {
                mClassMethodDec = (ALittleClassMethodDec)parent;
            } else if (parent instanceof ALittleClassStaticDec) {
                mClassStaticDec = (ALittleClassStaticDec)parent;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                mGlobalMethodDec = (ALittleGlobalMethodDec)parent;
            }

            parent = parent.getParent();
        }
    }

    // 获取返回值类型
    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleClassDec) {
                guessList.add(((ALittleClassDec)element).guessType());
            }
        }

        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ALittleClassDec> decList = new ArrayList<>();
        if (mClassDec != null && mGlobalMethodDec == null && mClassStaticDec == null) {
            decList.add(mClassDec);
        }
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleClassDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }


    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement, null);
        anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD2_ATTRIBUTES);
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
