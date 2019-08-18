package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;
import com.intellij.openapi.util.Key;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueThisTypeReference extends ALittleReference {
    private ALittleClassDec mClassDec = null;
    private ALittleClassCtorDec mClassCtorDec = null;
    private ALittleClassSetterDec mClassSetterDec = null;
    private ALittleClassMethodDec mClassMethodDec = null;
    private ALittleClassStaticDec mClassStaticDec = null;
    private ALittleGlobalMethodDec mGlobalMethodDec = null;

    public ALittlePropertyValueThisTypeReference(@NotNull PsiElement element, TextRange textRange) {
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

            if (parent instanceof ALittleClassDec) {
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
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

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

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        variants.add(LookupElementBuilder.create("this"));
        return variants.toArray();
    }
}
