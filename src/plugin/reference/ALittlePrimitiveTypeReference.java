package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePrimitiveType;

import java.util.ArrayList;
import java.util.List;

public class ALittlePrimitiveTypeReference extends ALittleReference<ALittlePrimitiveType> {
    public ALittlePrimitiveTypeReference(@NotNull ALittlePrimitiveType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
        info.value = myElement.getText();
        info.element = myElement;
        guessList.add(info);
        return guessList;
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
