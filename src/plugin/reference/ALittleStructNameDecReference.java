package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleNamespaceNameDec;
import plugin.psi.ALittleStructDec;
import plugin.psi.ALittleStructExtendsDec;
import plugin.psi.ALittleStructNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleStructNameDecReference extends ALittleReference<ALittleStructNameDec> {
    public ALittleStructNameDecReference(@NotNull ALittleStructNameDec element, TextRange textRange) {
        super(element, textRange);

        // 如果父节点是extends，那么就获取指定的命名域
        PsiElement parent = element.getParent();
        if (parent instanceof ALittleStructExtendsDec) {
            ALittleNamespaceNameDec namespace_name_dec = ((ALittleStructExtendsDec) parent).getNamespaceNameDec();
            if (namespace_name_dec != null)
                mNamespace = namespace_name_dec.getText();
        }
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        PsiElement parent = myElement.getParent();

        // 如果直接就是定义，那么直接获取
        if (parent instanceof ALittleStructDec) {
            ALittleGuess guess = ((ALittleStructDec) parent).guessType();
            guess_list.add(guess);
            // 如果是继承那么就从继承那边获取
        } else if (parent instanceof ALittleStructExtendsDec) {
            if (mKey.length() == 0) return new ArrayList<>();
            List<PsiElement> struct_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.STRUCT_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
            for (PsiElement struct_name_dec : struct_name_dec_list) {
                ALittleGuess guess = ((ALittleStructNameDec) struct_name_dec).guessType();
                guess_list.add(guess);
            }
        }
        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject(),
                PsiHelper.PsiElementType.STRUCT_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
        List<ResolveResult> results = new ArrayList<>();
        for (PsiElement dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject(),
                PsiHelper.PsiElementType.STRUCT_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, "", true);
        List<LookupElement> variants = new ArrayList<>();
        for (PsiElement dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.STRUCT).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }

    @Override
    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "结构体名不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
