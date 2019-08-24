package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleStructNameDecReference extends ALittleReference<ALittleStructNameDec> {
    public ALittleStructNameDecReference(@NotNull ALittleStructNameDec element, TextRange textRange) {
        super(element, textRange);

        // 如果父节点是extends，那么就获取指定的命名域
        PsiElement parent = element.getParent();
        if (parent instanceof ALittleStructExtendsDec) {
            ALittleNamespaceNameDec namespaceNameDec = ((ALittleStructExtendsDec)parent).getNamespaceNameDec();
            if (namespaceNameDec != null) {
                mNamespace = namespaceNameDec.getText();
            }
        }
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        PsiElement parent = myElement.getParent();

        // 如果直接就是定义，那么直接获取
        if (parent instanceof ALittleStructDec) {
            guessList.add(((ALittleStructDec)parent).guessType());
            // 如果是继承那么就从继承那边获取
        } else if (parent instanceof ALittleStructExtendsDec) {
            List<ALittleStructNameDec> structNameDecList = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), myElement.getContainingFile(), mNamespace, mKey);
            for (ALittleStructNameDec structNameDec : structNameDecList) {
                guessList.add(structNameDec.guessType());
            }
        }

        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, myElement.getContainingFile(), mNamespace, mKey);
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleStructNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, myElement.getContainingFile(), mNamespace, "");
        List<LookupElement> variants = new ArrayList<>();
        for (ALittleStructNameDec dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.STRUCT).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getText().startsWith("___")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "结构体名不能以3个下划线开头");
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
        }
    }
}
