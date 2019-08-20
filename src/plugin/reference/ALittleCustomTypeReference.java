package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleCustomTypeReference extends ALittleReference<ALittleCustomType> {
    public ALittleCustomTypeReference(@NotNull ALittleCustomType element, TextRange textRange) {
        super(element, textRange);

        ALittleNamespaceNameDec namespaceNameDec = element.getNamespaceNameDec();
        if (namespaceNameDec != null) {
            mNamespace = namespaceNameDec.getIdContent().getText();
        }
        mKey = element.getIdContent().getText();
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleClassNameDec) {
                guessList.add(((ALittleClassDec)element.getParent()).guessType());
            } else if (element instanceof ALittleStructNameDec) {
                guessList.add(((ALittleStructDec)element.getParent()).guessType());
            } else if (element instanceof ALittleEnumNameDec) {
                guessList.add(((ALittleEnumDec)element.getParent()).guessType());
            }
        }

        return guessList;
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        List<ResolveResult> results = new ArrayList<>();
        {
            List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, mKey);
            for (ALittleClassNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        {
            List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, mKey);
            for (ALittleStructNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        {
            List<ALittleEnumNameDec> decList = ALittleTreeChangeListener.findEnumNameDecList(project, mNamespace, mKey);
            for (ALittleEnumNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<LookupElement> variants = new ArrayList<>();

        // 查找对应命名域下的类名
        {
            List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, "");
            for (ALittleClassNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 结构体名
        {
            List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, "");
            for (ALittleStructNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 枚举名
        {
            List<ALittleEnumNameDec> decList = ALittleTreeChangeListener.findEnumNameDecList(project, mNamespace, "");
            for (ALittleEnumNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 查找全局函数
        {
            List<ALittleMethodNameDec> decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, mNamespace, "");
            for (ALittleMethodNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        if (!mKey.equals("IntellijIdeaRulezzz"))
        {
            // 查找所有命名域
            final List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (final ALittleNamespaceNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
