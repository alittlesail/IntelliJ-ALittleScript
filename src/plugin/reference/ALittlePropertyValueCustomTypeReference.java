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
import org.jetbrains.annotations.Nullable;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueCustomTypeReference extends ALittleReference<ALittlePropertyValueCustomType> {
    private PsiElement mMethodDec = null;

    public ALittlePropertyValueCustomTypeReference(@NotNull ALittlePropertyValueCustomType element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
    }

    private void reloadInfo() {
        mMethodDec = null;
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                break;
            } else if (parent instanceof ALittleClassDec) {
                break;
            } else if (parent instanceof ALittleClassCtorDec
                || parent instanceof ALittleClassSetterDec
                || parent instanceof ALittleClassGetterDec
                || parent instanceof ALittleClassMethodDec
                || parent instanceof ALittleClassStaticDec
                || parent instanceof ALittleGlobalMethodDec) {
                mMethodDec = parent;
                break;
            }

            parent = parent.getParent();
        }
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();
        // 标记是否已经包含了命名域，命名域的guess不要重复
        boolean hasNamespace = false;

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleGuess guess = null;
            if (element instanceof ALittleNamespaceNameDec) {
                if (!hasNamespace) {
                    hasNamespace = true;
                    ALittleGuessNamespaceName guessNamespaceName = new ALittleGuessNamespaceName(
                            ((ALittleNamespaceNameDec) element).guessType().value,
                            (ALittleNamespaceNameDec) element
                    );
                    guessNamespaceName.UpdateValue();
                    guess = guessNamespaceName;
                }
            } else if (element instanceof ALittleClassNameDec) {
                ALittleGuess classGuess = ((ALittleClassNameDec) element).guessType();
                if (!(classGuess instanceof ALittleGuessClass)) {
                    throw new ALittleGuessException(myElement, "ALittleClassNameDec.guessType()的结果不是ALittleGuessClass");
                }
                ALittleGuessClass classGuessClass = (ALittleGuessClass)classGuess;
                if (!classGuessClass.templateList.isEmpty()) {
                    throw new ALittleGuessException(myElement, "模板类" + classGuessClass.value + "不能直接使用");
                }
                ALittleGuessClassName guessClassName = new ALittleGuessClassName(classGuessClass.GetNamespaceName(),
                        classGuessClass.GetClassName(), (ALittleClassNameDec) element);
                guessClassName.UpdateValue();
                guess = guessClassName;
            } else if (element instanceof ALittleStructNameDec) {
                ALittleGuess structGuess = ((ALittleStructNameDec) element).guessType();
                if (!(structGuess instanceof ALittleGuessStruct)) {
                    throw new ALittleGuessException(myElement, "ALittleStructNameDec.guessType()的结果不是ALittleGuessStruct");
                }
                ALittleGuessStruct structGuessStruct = (ALittleGuessStruct)structGuess;

                ALittleGuessStructName guessStructName = new ALittleGuessStructName(structGuessStruct.GetNamespaceName(),
                        structGuessStruct.GetStructName(), (ALittleStructNameDec) element);
                guessStructName.UpdateValue();
                guess = guessStructName;
            } else if (element instanceof ALittleEnumNameDec) {
                ALittleGuess enumGuess = ((ALittleEnumNameDec) element).guessType();
                if (!(enumGuess instanceof ALittleGuessEnum)) {
                    throw new ALittleGuessException(myElement, "ALittleEnumNameDec.guessType()的结果不是ALittleGuessEnum");
                }
                ALittleGuessEnum enumGuessEnum = (ALittleGuessEnum)enumGuess;

                ALittleGuessEnumName guessEnumName = new ALittleGuessEnumName(enumGuessEnum.GetNamespaceName(),
                        enumGuessEnum.GetEnumName(), (ALittleEnumNameDec) element);
                guessEnumName.UpdateValue();
                guess = guessEnumName;
            } else if (element instanceof ALittleMethodParamNameDec) {
                guess = ((ALittleMethodParamNameDec) element).guessType();
            } else if (element instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) element).guessType();
            } else if (element instanceof ALittleMethodNameDec) {
                guess = ((ALittleMethodNameDec) element).guessType();
            }

            if (guess != null) guessList.add(guess);
        }

        return guessList;
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        try {
            List<ALittleGuess> guessList = guessTypes();
            if (guessList.isEmpty()) return;

            ALittleGuess guess = guessList.get(0);
            if (!(guess instanceof ALittleGuessFunctor)) return;
            ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;
            if (guessFunctor.element instanceof ALittleClassStaticDec
                || guessFunctor.element instanceof ALittleGlobalMethodDec) {
                Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
            }
        } catch (ALittleGuessException ignored) {

        }
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        PsiFile file = myElement.getContainingFile().getOriginalFile();
        List<ResolveResult> results = new ArrayList<>();

        // 处理命名域
        {
            List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleNamespaceNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理全局函数
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.GLOBAL_METHOD, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理类名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理结构体名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理枚举名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.ENUM_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理单例
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.INSTANCE_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理参数
        if (mMethodDec != null)
        {
            List<ALittleMethodParamNameDec> decList = PsiHelper.findMethodParamNameDecList(mMethodDec, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleMethodParamNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理表达式定义
        {
            List<ALittleVarAssignNameDec> decList = PsiHelper.findVarAssignNameDecList(myElement, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleVarAssignNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile file = myElement.getContainingFile().getOriginalFile();
        List<LookupElement> variants = new ArrayList<>();
        if (mKey.equals("IntellijIdeaRulezzz"))
            return variants.toArray();

        // 处理命名域
        {
            List<ALittleNamespaceNameDec> decList = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (ALittleNamespaceNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理全局函数
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.GLOBAL_METHOD, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理类名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.CLASS_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理结构体名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.STRUCT_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理枚举名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.ENUM_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理单例
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.INSTANCE_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.INSTANCE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理参数
        if (mMethodDec != null)
        {
            List<ALittleMethodParamNameDec> decList = PsiHelper.findMethodParamNameDecList(mMethodDec, "");
            for (ALittleMethodParamNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.PARAM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理表达式
        {
            List<ALittleVarAssignNameDec> decList = PsiHelper.findVarAssignNameDecList(myElement, "");
            for (ALittleVarAssignNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.VARIABLE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
