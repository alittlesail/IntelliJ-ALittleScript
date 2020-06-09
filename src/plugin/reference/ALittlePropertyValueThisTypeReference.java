package plugin.reference;

import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueThisTypeReference extends ALittleReference<ALittlePropertyValueThisType> {
    private ALittleClassDec mClassDec = null;
    private ALittleClassCtorDec mClassCtorDec = null;
    private ALittleClassGetterDec mClassGetterDec = null;
    private ALittleClassSetterDec mClassSetterDec = null;
    private ALittleClassMethodDec mClassMethodDec = null;
    private ALittleClassStaticDec mClassStaticDec = null;
    private ALittleGlobalMethodDec mGlobalMethodDec = null;
    private boolean mIsConst = false;

    public ALittlePropertyValueThisTypeReference(@NotNull ALittlePropertyValueThisType element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
    }

    private void reloadInfo() {
        mClassDec = null;
        mClassCtorDec = null;
        mClassGetterDec = null;
        mClassSetterDec = null;
        mClassMethodDec = null;
        mClassStaticDec = null;

        PsiElement parent = myElement;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleNamespaceDec) {
                break;
            } else if (parent instanceof ALittleClassDec) {
                mClassDec = (ALittleClassDec) parent;
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                mClassCtorDec = (ALittleClassCtorDec) parent;
            } else if (parent instanceof ALittleClassGetterDec) {
                mClassGetterDec = (ALittleClassGetterDec) parent;
                List<ALittleModifier> modifier = ((ALittleClassElementDec) mClassGetterDec.getParent()).getModifierList();
                mIsConst = PsiHelper.isConst(modifier);
            } else if (parent instanceof ALittleClassSetterDec) {
                mClassSetterDec = (ALittleClassSetterDec) parent;
                List<ALittleModifier> modifier = ((ALittleClassElementDec) mClassSetterDec.getParent()).getModifierList();
                mIsConst = PsiHelper.isConst(modifier);
            } else if (parent instanceof ALittleClassMethodDec) {
                mClassMethodDec = (ALittleClassMethodDec) parent;
                List<ALittleModifier> modifier = ((ALittleClassElementDec) mClassMethodDec.getParent()).getModifierList();
                mIsConst = PsiHelper.isConst(modifier);
            } else if (parent instanceof ALittleClassStaticDec) {
                mClassStaticDec = (ALittleClassStaticDec) parent;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                mGlobalMethodDec = (ALittleGlobalMethodDec) parent;
            }

            parent = parent.getParent();
        }
    }

    // 获取返回值类型
    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(true);
        for (ResolveResult resolve : result_list) {
            PsiElement result = resolve.getElement();
            if (result instanceof ALittleClassDec) {
                ALittleGuess guess = ((ALittleClassDec) result).guessType();
                if (mIsConst && !guess.is_const) {
                    if (guess instanceof ALittleGuessPrimitive) {
                        guess = ALittleGuessPrimitive.sPrimitiveGuessMap.get("const " + guess.getValue());
                        if (guess == null) throw new ALittleGuessException(myElement, "找不到const " + guess.getValue());
                    } else {
                        guess = guess.clone();
                        guess.is_const = true;
                        guess.updateValue();
                    }
                }
                guess_list.add(guess);
            }
        }
        return guess_list;
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
