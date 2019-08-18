package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.List;

public abstract class ALittleReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    protected String mKey;
    protected String mNamespace;

    public ALittleReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        mKey = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        mNamespace = ALittleUtil.getNamespaceName((ALittleFile) element.getContainingFile());
    }

    @NotNull
    public abstract List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException;

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[0];
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new LookupElement[0];
    }
}
