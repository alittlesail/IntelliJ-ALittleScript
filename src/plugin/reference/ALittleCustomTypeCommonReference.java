package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ALittleCustomTypeCommonReference<T extends PsiElement> extends ALittleReference<T> {
    private ALittleClassDec mClassDec;
    private ALittleCustomType mCustomType;

    public ALittleCustomTypeCommonReference(@NotNull ALittleCustomType customType, @NotNull T element, TextRange textRange) {
        super(element, textRange);
        mCustomType = customType;
    }

    public ALittleClassDec getClassDec() {
        if (mClassDec != null) return mClassDec;
        mClassDec = PsiHelper.findClassDecFromParent(myElement);
        return mClassDec;
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<ALittleGuess> guessList = new ArrayList<>();

        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.USING_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                guessList.add(((ALittleUsingNameDec)dec).guessType());
            }
            if (!decList.isEmpty() && !mCustomType.getAllTypeList().isEmpty()) {
                throw new ALittleGuessException(myElement, "使用using定义的类不能再使用模板参数, namespace:" + mNamespace + ", key:" + mKey);
            }
        }
        {
            // 根据名字获取对应的类
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, psiFile, mNamespace, mKey, true);

            // 获取模板的填充对象，并计算类型
            List<ALittleAllType> templateList = mCustomType.getAllTypeList();
            List<ALittleGuess> srcGuessList = new ArrayList<>();
            for (ALittleAllType allType : templateList) {
                srcGuessList.add(allType.guessType());
            }

            // 遍历所有的类
            for (PsiElement dec : decList) {
                // 获取dec的类型
                ALittleGuess guess = ((ALittleClassNameDec)dec).guessType();
                if (!(guess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "模板参数数量和类定义的不一致, namespace:" + mNamespace + ", key:" + mKey);
                ALittleGuessClass guessClass = (ALittleGuessClass)guess;
                // 类模板列表的参数数量必须和填充的一致
                if (templateList.size() != guessClass.templateList.size()) {
                    throw new ALittleGuessException(myElement, "模板参数数量和类定义的不一致, namespace:" + mNamespace + ", key:" + mKey);
                }
                // 对比两种
                for (int i = 0; i < templateList.size(); ++i) {
                    if (!(guessClass.templateList.get(i) instanceof ALittleGuessClassTemplate))
                        throw new ALittleGuessException(myElement, "guessClass.templateList不是ALittleGuessClassTemplate");
                    ALittleGuessClassTemplate guessClassTemplate = (ALittleGuessClassTemplate)guessClass.templateList.get(i);
                    if (guessClassTemplate.templateExtends != null) {
                        ALittleReferenceOpUtil.guessTypeEqual(myElement, guessClassTemplate.templateExtends, myElement, srcGuessList.get(i));
                    } else if (guessClassTemplate.isClass) {
                        if (!(srcGuessList.get(i) instanceof ALittleGuessClass)) {
                            throw new ALittleGuessException(templateList.get(i), "模板要求的是class，不能是:" + srcGuessList.get(i).value);
                        }
                    } else if (guessClassTemplate.isStruct) {
                        if (!(srcGuessList.get(i) instanceof ALittleGuessStruct)) {
                            throw new ALittleGuessException(templateList.get(i), "模板要求的是struct，不能是:" + srcGuessList.get(i).value);
                        }
                    }
                }

                if (!guessClass.templateList.isEmpty()) {
                    ALittleClassDec srcClassDec = guessClass.element;
                    ALittleClassNameDec srcClassNameDec = srcClassDec.getClassNameDec();
                    if (srcClassNameDec == null)
                        throw new ALittleGuessException(mCustomType, "类模板没有定义类名");

                    ALittleGuessClass info = new ALittleGuessClass(PsiHelper.getNamespaceName(srcClassDec),
                            srcClassNameDec.getIdContent().getText(),
                            guessClass.element, guessClass.usingName);
                    info.templateList.addAll(guessClass.templateList);
                    for (int i = 0; i < guessClass.templateList.size(); ++i) {
                        info.templateMap.put(guessClass.templateList.get(i).value, srcGuessList.get(i));
                    }
                    info.UpdateValue();
                    guess = info;
                }

                guessList.add(guess);
            }
        }
        {
            ALittleClassDec classDec = getClassDec();
            if (classDec != null) {
                List<PsiElement> decList = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, mKey, decList);
                // 不能再静态函数中使用模板定义
                if (!decList.isEmpty() && PsiHelper.isInClassStaticMethod(myElement)) {
                    throw new ALittleGuessException(myElement, "类静态函数不能使用模板符号");
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

        if (guessList.isEmpty()) throw new ALittleGuessException(myElement, "找不到指定类型, namespace:" + mNamespace + ", key:" + mKey);

        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();

        List<ResolveResult> results = new ArrayList<>();

        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.USING_NAME, psiFile, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
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
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<LookupElement> variants = new ArrayList<>();

        // 查找该命名域下的
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.USING_NAME, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
                try {
                    ALittleGuess guess = ((ALittleUsingNameDec)dec).guessType();
                    if (guess instanceof ALittleGuessClass) {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.CLASS).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    } else if (guess instanceof ALittleGuessStruct) {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.STRUCT).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    } else {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.PROPERTY).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    }
                } catch (ALittleGuessException ignored) {
                    variants.add(LookupElementBuilder.create(dec.getText()).
                            withIcon(ALittleIcons.CLASS).
                            withTypeText(dec.getContainingFile().getName())
                    );
                }
            }
        }

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
