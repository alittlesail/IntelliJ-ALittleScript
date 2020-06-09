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
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleClassNameDecReference extends ALittleReference<ALittleClassNameDec> {
    public ALittleClassNameDecReference(@NotNull ALittleClassNameDec element, TextRange textRange) {
        super(element, textRange);

        mNamespace = PsiHelper.getNamespaceName(myElement);

        // 如果父节点是extends，那么就获取指定的命名域
        PsiElement parent = element.getParent();
        if (parent instanceof ALittleClassExtendsDec) {
            ALittleNamespaceNameDec namespace_name_dec = ((ALittleClassExtendsDec) parent).getNamespaceNameDec();
            if (namespace_name_dec != null)
                mNamespace = namespace_name_dec.getText();
        }

        mKey = myElement.getText();
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        PsiElement parent = myElement.getParent();

        // 如果直接就是定义，那么直接获取
        if (parent instanceof ALittleClassDec) {
            ALittleGuess guess = ((ALittleClassDec) parent).guessType();
            guess_list.add(guess);
        }
        // 如果是继承那么就从继承那边获取
        else if (parent instanceof ALittleClassExtendsDec) {
            if (mKey.length() == 0)
                throw new ALittleGuessException(myElement, "找不到类, namespace:" + mNamespace + ", key:" + mKey);

            // 查找继承
            List<PsiElement> class_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
            if (class_name_dec_list.size() == 0)
                throw new ALittleGuessException(myElement, "找不到类, namespace:" + mNamespace + ", key:" + mKey);

            for (PsiElement class_name_dec : class_name_dec_list) {
                ALittleGuess guess = ((ALittleClassNameDec) class_name_dec).guessType();
                if (!(guess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "继承的不是一个类, namespace:" + mNamespace + ", key:" + mKey);

                ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                if (guess_class.template_list.size() > 0) {
                    ALittleClassDec sub_class = (ALittleClassDec) parent.getParent();
                    if (sub_class == null)
                        throw new ALittleGuessException(parent, "定义不完整");

                    ALittleTemplateDec sub_template_dec = sub_class.getTemplateDec();
                    if (sub_template_dec == null)
                        throw new ALittleGuessException(parent, "子类的模板参数列表必须涵盖父类的模板参数列表");

                    List<ALittleTemplatePairDec> sub_template_pair_list = sub_template_dec.getTemplatePairDecList();
                    if (sub_template_pair_list.size() < guess_class.template_list.size())
                        throw new ALittleGuessException(parent, "子类的模板参数列表必须涵盖父类的模板参数列表");

                    for (int i = 0; i < guess_class.template_list.size(); ++i) {
                        ALittleGuess sub_template = sub_template_pair_list.get(i).guessType();
                        try {
                            ALittleReferenceOpUtil.guessTypeEqual(guess_class.template_list.get(i), sub_template_pair_list.get(i), sub_template, false, false);
                        } catch (ALittleGuessException error) {
                            throw new ALittleGuessException(sub_template_pair_list.get(i), "子类的模板参数和父类的模板参数不一致:" + error.getError());
                        }
                    }
                }
                guess_list.add(guess);
            }
        } else {
            throw new ALittleGuessException(myElement, "ALittleClassNameDec出现未知的父节点");
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject(),
                PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
        List<ResolveResult> results = new ArrayList<>();
        for (PsiElement dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject(),
                PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, "", true);
        List<LookupElement> variants = new ArrayList<>();
        for (PsiElement dec : decList) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.CLASS).
                    withTypeText(dec.getContainingFile().getName())
            );
        }

        if (myElement.getParent() instanceof ALittleClassExtendsDec) {
            Map<String, ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (Map.Entry<String, ALittleNamespaceNameDec> entry : dec_list.entrySet()) {
                variants.add(LookupElementBuilder.create(entry.getKey()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(entry.getValue().getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getText().startsWith("___"))
            throw new ALittleGuessException(myElement, "类名不能以3个下划线开头");

        List<ALittleGuess> guess_list = myElement.guessTypes();
        if (guess_list.size() == 0)
            throw new ALittleGuessException(myElement, "未知类型");
        else if (guess_list.size() != 1)
            throw new ALittleGuessException(myElement, "重复定义");
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement, null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_NAME);
    }
}
