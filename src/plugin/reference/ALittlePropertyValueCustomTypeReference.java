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
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        // 标记是否已经包含了命名域，命名域的guess不要重复
        boolean hasNamespace = false;

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleReferenceUtil.GuessTypeInfo guess = null;
            if (element instanceof ALittleNamespaceNameDec) {
                if (!hasNamespace) {
                    hasNamespace = true;
                    guess = new ALittleReferenceUtil.GuessTypeInfo();
                    guess.type = ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME;
                    guess.value = ((ALittleNamespaceNameDec) element).guessType().value;
                    guess.element = element;
                }
            } else if (element instanceof ALittleClassNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_CLASS_NAME;
                guess.value = ((ALittleClassNameDec) element).guessType().value;
                guess.element = element;
            } else if (element instanceof ALittleStructNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_STRUCT_NAME;
                guess.value = ((ALittleStructNameDec) element).guessType().value;
                guess.element = element;
            } else if (element instanceof ALittleEnumNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_ENUM_NAME;
                guess.value = ((ALittleEnumNameDec) element).guessType().value;
                guess.element = element;
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
            List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = guessTypes();
            if (guessTypeList.isEmpty()) return;

            ALittleReferenceUtil.GuessTypeInfo guessType = guessTypeList.get(0);
            if (guessType.element instanceof ALittleClassStaticDec
                || guessType.element instanceof ALittleGlobalMethodDec) {
                Annotation anno = holder.createInfoAnnotation(myElement.getIdContent(), null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }
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
        PsiFile file = myElement.getContainingFile();
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
        PsiFile file = myElement.getContainingFile();
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
