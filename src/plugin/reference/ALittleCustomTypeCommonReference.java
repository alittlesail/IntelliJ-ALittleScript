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
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClass;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessStruct;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittleCustomTypeCommonReference<T extends PsiElement> extends ALittleReference<T> {
    private ALittleClassDec mClassDec;
    private ALittleTemplateDec mTemplateParamDec;
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

    public ALittleTemplateDec getMethodTemplateDec() {
        if (mTemplateParamDec != null) return mTemplateParamDec;
        mTemplateParamDec = PsiHelper.findMethodTemplateDecFromParent(myElement);
        return mTemplateParamDec;
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<ALittleGuess> guess_list = new ArrayList<>();

        if (mKey.length() == 0)
            throw new ALittleGuessException(myElement, "找不到指定类型, namespace:" + mNamespace + ", key:" + mKey);

        ALittleCustomTypeTemplate custom_type_template = mCustomType.getCustomTypeTemplate();
        {
            List<PsiElement> dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.USING_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
            for (PsiElement dec : dec_list) {
                ALittleGuess guess = ((ALittleUsingNameDec) dec).guessType();
                guess_list.add(guess);
            }

            if (dec_list.size() > 0 && custom_type_template != null && custom_type_template.getAllTypeList().size() > 0)
                throw new ALittleGuessException(myElement, "使用using定义的类不能再使用模板参数, namespace:" + mNamespace + ", key:" + mKey);
        }
        {
            // 根据名字获取对应的类
            List<PsiElement> dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);

            // 获取模板的填充对象，并计算类型
            List<ALittleGuess> src_guess_list = new ArrayList<>();
            List<ALittleAllType> template_list;
            if (custom_type_template != null) {
                template_list = custom_type_template.getAllTypeList();
                for (ALittleAllType all_type : template_list) {
                    ALittleGuess all_type_guess = all_type.guessType();
                    src_guess_list.add(all_type_guess);
                }
            } else {
                template_list = new ArrayList<ALittleAllType>();
            }

            // 遍历所有的类
            for (PsiElement dec : dec_list) {
                // 获取dec的类型
                ALittleGuess guess = ((ALittleClassNameDec) dec).guessType();
                if (!(guess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "模板参数数量和类定义的不一致, namespace:" + mNamespace + ", key:" + mKey);
                ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                // 类模板列表的参数数量必须和填充的一致
                if (template_list.size() != guess_class.template_list.size())
                    throw new ALittleGuessException(myElement, "模板参数数量和类定义的不一致, namespace:" + mNamespace + ", key:" + mKey);

                // 对比两种
                for (int i = 0; i < template_list.size(); ++i) {
                    ALittleReferenceOpUtil.guessTypeEqual(guess_class.template_list.get(i), template_list.get(i), src_guess_list.get(i), false, false);
                }

                if (guess_class.template_list.size() > 0) {
                    ALittleClassDec src_class_dec = guess_class.class_dec;
                    ALittleClassNameDec src_class_name_dec = src_class_dec.getClassNameDec();
                    if (src_class_name_dec == null)
                        throw new ALittleGuessException(mCustomType, "类模板没有定义类名");

                    ALittleGuessClass info = new ALittleGuessClass(PsiHelper.getNamespaceName(src_class_dec),
                            src_class_name_dec.getText(),
                            guess_class.class_dec, guess_class.using_name, guess_class.is_const, guess_class.is_native);
                    info.template_list.addAll(guess_class.template_list);
                    for (int i = 0; i < guess_class.template_list.size(); ++i) {
                        info.template_map.put(guess_class.template_list.get(i).getValueWithoutConst(), src_guess_list.get(i));
                    }
                    info.updateValue();
                    guess = info;
                }

                guess_list.add(guess);
            }
        }
        {
            ALittleClassDec class_dec = getClassDec();
            if (class_dec != null) {
                List<PsiElement> dec_list = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(class_dec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, mKey, dec_list);
                // 不能再静态函数中使用模板定义
                if (dec_list.size() > 0 && PsiHelper.isInClassStaticMethod(myElement))
                    throw new ALittleGuessException(myElement, "类静态函数不能使用模板符号");
                for (PsiElement dec : dec_list) {
                    ALittleGuess guess = ((ALittleTemplatePairDec) dec).guessType();
                    guess_list.add(guess);
                }
            }
        }
        {
            ALittleTemplateDec template_dec = getMethodTemplateDec();
            if (template_dec != null) {
                List<ALittleTemplatePairDec> pair_dec_list = template_dec.getTemplatePairDecList();
                for (ALittleTemplatePairDec dec : pair_dec_list) {
                    ALittleTemplateNameDec name_dec = dec.getTemplateNameDec();
                    if (name_dec == null) continue;

                    if (name_dec.getText().equals(mKey)) {
                        ALittleGuess guess = dec.guessType();
                        guess_list.add(guess);
                    }
                }
            }
        }
        {
            List<PsiElement> dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.STRUCT_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
            for (PsiElement dec : dec_list) {
                ALittleGuess guess = ((ALittleStructNameDec) dec).guessType();
                guess_list.add(guess);
            }
        }
        {
            List<PsiElement> dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                    , PsiHelper.PsiElementType.ENUM_NAME, myElement.getContainingFile().getOriginalFile(), mNamespace, mKey, true);
            for (PsiElement dec : dec_list) {
                ALittleGuess guess = ((ALittleEnumNameDec) dec).guessType();
                guess_list.add(guess);
            }
        }
        if (myElement instanceof ALittleCustomType) {
            Map<String, ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(myElement.getProject(), mKey);
            for (Map.Entry<String, ALittleNamespaceNameDec> dec : dec_list.entrySet()) {
                ALittleGuess guess = dec.getValue().guessType();
                guess_list.add(guess);
            }
        }

        if (guess_list.size() == 0)
            throw new ALittleGuessException(myElement, "找不到指定类型, namespace:" + mNamespace + ", key:" + mKey);

        return guess_list;
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
            ALittleTemplateDec templateDec = getMethodTemplateDec();
            if (templateDec != null) {
                List<ALittleTemplatePairDec> pairDecList = templateDec.getTemplatePairDecList();
                for (ALittleTemplatePairDec pairDec : pairDecList) {
                    if (pairDec.getTemplateNameDec().getText().equals(mKey)) {
                        results.add(new PsiElementResolveResult(pairDec.getTemplateNameDec()));
                    }
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
                    ALittleGuess guess = ((ALittleUsingNameDec) dec).guessType();
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
        // 查找类模板
        {
            ALittleClassDec classDec = getClassDec();
            if (classDec != null) {
                List<PsiElement> decList = new ArrayList<>();
                ALittleTreeChangeListener.findClassAttrList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, PsiHelper.ClassAttrType.TEMPLATE, "", decList);
                for (PsiElement dec : decList) {
                    ALittleTemplatePairDec pairDec = (ALittleTemplatePairDec) dec;
                    variants.add(LookupElementBuilder.create(pairDec.getTemplateNameDec().getText()).
                            withIcon(ALittleIcons.TEMPLATE).
                            withTypeText(dec.getContainingFile().getName())
                    );
                }
            }
        }
        // 查找函数模板
        {
            ALittleTemplateDec templateDec = getMethodTemplateDec();
            if (templateDec != null) {
                List<ALittleTemplatePairDec> pairDecList = templateDec.getTemplatePairDecList();
                for (ALittleTemplatePairDec pairDec : pairDecList) {
                    variants.add(LookupElementBuilder.create(pairDec.getTemplateNameDec().getText()).
                            withIcon(ALittleIcons.TEMPLATE).
                            withTypeText(pairDec.getContainingFile().getName())
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

        if (!mKey.equals("IntellijIdeaRulezzz")) {
            // 查找所有命名域
            Map<String, ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (Map.Entry<String, ALittleNamespaceNameDec> entry : decList.entrySet()) {
                variants.add(LookupElementBuilder.create(entry.getValue().getText()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(entry.getValue().getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
