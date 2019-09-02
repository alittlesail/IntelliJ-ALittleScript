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
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ALittleCustomTypeReference extends ALittleReference<ALittleCustomType> {
    private ALittleClassDec mClassDec;

    public ALittleCustomTypeReference(@NotNull ALittleCustomType element, TextRange textRange) {
        super(element, textRange);

        ALittleNamespaceNameDec namespaceNameDec = element.getNamespaceNameDec();
        if (namespaceNameDec != null) {
            mNamespace = namespaceNameDec.getIdContent().getText();
        }
        mKey = element.getIdContent().getText();
    }

    public ALittleClassDec getClassDec() {
        if (mClassDec != null) return mClassDec;
        mClassDec = PsiHelper.findClassDecFromParent(myElement);
        return mClassDec;
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile();
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, psiFile, mNamespace, mKey, true);

            // 获取填充的内容，并计算类型
            List<ALittleAllType> templateList = myElement.getAllTypeList();
            List<ALittleReferenceUtil.GuessTypeInfo> srcGuessList = new ArrayList<>();
            for (ALittleAllType allType : templateList) {
                srcGuessList.add(allType.guessType());
            }

            // 遍历
            for (PsiElement dec : decList) {
                // 获取模板参数，并把有类继承的拿出来比对，是否符合条件
                ALittleReferenceUtil.GuessTypeInfo guessInfo = ((ALittleClassNameDec)dec).guessType();
                List<ALittleReferenceUtil.GuessTypeInfo> templateGuessList = guessInfo.classTemplateList;
                if (templateList.size() != templateGuessList.size()) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "模板参数数量和类定义的不一致, namespace:" + mNamespace + ", key:" + mKey);
                }
                for (int i = 0; i < templateList.size(); ++i) {
                    ALittleReferenceOpUtil.guessTypeEqual(myElement, templateGuessList.get(i), myElement, srcGuessList.get(i));
                }

                if (!templateGuessList.isEmpty()) {
                    ALittleClassDec srcClassDec = (ALittleClassDec) guessInfo.element;
                    ALittleClassNameDec srcClassNameDec = srcClassDec.getClassNameDec();
                    if (srcClassNameDec == null)
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "类模板没有定义类名");

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = guessInfo.type;
                    info.element = guessInfo.element;
                    info.value = PsiHelper.getNamespaceName(srcClassDec) + "." + srcClassNameDec.getIdContent().getText();
                    info.classTemplateMap = new HashMap<>();
                    List<String> nameList = new ArrayList<>();
                    for (int i = 0; i < templateGuessList.size(); ++i) {
                        nameList.add(srcGuessList.get(i).value);
                        info.classTemplateMap.put(templateGuessList.get(i).value, srcGuessList.get(i));
                    }
                    info.value += "<" + String.join(",", nameList) + ">";
                    guessInfo = info;
                }

                guessList.add(guessInfo);
            }
        }
        {
            ALittleClassDec classDec = getClassDec();
            if (classDec != null) {
                List<PsiElement> decList = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, mKey, decList);
                // 不能再静态函数中使用模板定义
                if (!decList.isEmpty() && PsiHelper.isInClassStaticMethod(myElement)) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "类静态函数不能使用模板符号");
                }
                for (PsiElement dec : decList) {
                    guessList.add(((ALittleTemplatePairDec)dec).guessType());
                }
            }
        }
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                guessList.add(((ALittleStructNameDec)dec).guessType());
            }
        }
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.ENUM_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                guessList.add(((ALittleEnumNameDec)dec).guessType());
            }
        }

        if (guessList.isEmpty()) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "找不到指定类型, namespace:" + mNamespace + ", key:" + mKey);

        return guessList;
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
        anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = myElement.guessTypes();
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
        PsiFile psiFile = myElement.getContainingFile();

        List<ResolveResult> results = new ArrayList<>();
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        {
            ALittleClassDec classDec = getClassDec();
            if (classDec != null) {
                List<PsiElement> decList = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, mKey, decList);
                for (PsiElement dec : decList) {
                    results.add(new PsiElementResolveResult(dec));
                }
            }
        }
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();

        // 查找对应命名域下的类名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 查找模板
        {
            ALittleClassDec classDec = getClassDec();
            if (classDec != null) {
                List<PsiElement> decList = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, "", decList);
                for (PsiElement dec : decList) {
                    ALittleTemplatePairDec pairDec = (ALittleTemplatePairDec)dec;
                    variants.add(LookupElementBuilder.create(pairDec.getIdContent().getText()).
                            withIcon(ALittleIcons.TEMPLATE).
                            withTypeText(dec.getContainingFile().getName())
                    );
                }
            }
        }
        // 结构体名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 枚举名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.ENUM_NAME, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 查找全局函数
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.GLOBAL_METHOD, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
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
