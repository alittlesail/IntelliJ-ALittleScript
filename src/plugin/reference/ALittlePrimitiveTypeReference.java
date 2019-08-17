package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleUtil;
import plugin.psi.ALittleClassNameDec;
import plugin.psi.ALittleFile;
import plugin.psi.ALittlePrimitiveType;
import plugin.psi.ALittleVarAssignNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittlePrimitiveTypeReference extends ALittleReference {
    public ALittlePrimitiveTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();
        guess_list.add(myElement);
        return guess_list;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        variants.add(LookupElementBuilder.create("int"));
        variants.add(LookupElementBuilder.create("I64"));
        variants.add(LookupElementBuilder.create("double"));
        variants.add(LookupElementBuilder.create("bool"));
        variants.add(LookupElementBuilder.create("string"));
        variants.add(LookupElementBuilder.create("any"));
        return variants.toArray();
    }
}
