package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueCastTypeReference extends ALittleReference {
    public ALittlePropertyValueCastTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        if (myElement instanceof ALittlePropertyValueCastType) {
            ALittlePropertyValueCastType element = (ALittlePropertyValueCastType)myElement;
            try {
                guess_list.add(ALittleUtil.guessType(element.getAllType()));
            } catch (ALittleUtil.ALittleElementException ignored) {
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        variants.add(LookupElementBuilder.create("cast"));
        return variants.toArray();
    }
}
