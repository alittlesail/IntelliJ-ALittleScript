package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueCustomTypeReference extends ALittleReference {
    private ALittleClassDec mClassDec = null;
    private ALittleClassCtorDec mClassCtorDec = null;
    private ALittleClassSetterDec mClassSetterDec = null;
    private ALittleClassMethodDec mClassMethodDec = null;
    private ALittleClassStaticDec mClassStaticDec = null;
    private ALittleGlobalMethodDec mGlobalMethodDec = null;

    public ALittlePropertyValueCustomTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
    }

    private void reloadInfo() {
        mClassDec = null;
        mClassCtorDec = null;
        mClassSetterDec = null;
        mClassMethodDec = null;
        mClassStaticDec = null;

        PsiElement parent = myElement;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleClassDec) {
                mClassDec = (ALittleClassDec)parent;
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                mClassCtorDec = (ALittleClassCtorDec)parent;
            } else if (parent instanceof ALittleClassSetterDec) {
                mClassSetterDec = (ALittleClassSetterDec)parent;
            } else if (parent instanceof ALittleClassMethodDec) {
                mClassMethodDec = (ALittleClassMethodDec)parent;
            } else if (parent instanceof ALittleClassStaticDec) {
                mClassStaticDec = (ALittleClassStaticDec)parent;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                mGlobalMethodDec = (ALittleGlobalMethodDec)parent;
            }

            parent = parent.getParent();
        }
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleReferenceUtil.GuessTypeInfo guess = null;
            if (element instanceof ALittleNamespaceNameDec) {
                guess = ((ALittleNamespaceNameDec)element).guessType();
            } else if (element instanceof ALittleClassNameDec) {
                guess = ((ALittleClassNameDec)element).guessType();
            } else if (element instanceof ALittleStructNameDec) {
                guess = ((ALittleStructNameDec)element).guessType();
            } else if (element instanceof ALittleEnumNameDec) {
                guess = ((ALittleEnumNameDec)element).guessType();
            } else if (element instanceof ALittleInstanceNameDec) {
                guess = ((ALittleInstanceNameDec) element).guessType();
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

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
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
            List<ALittleMethodNameDec> decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, mNamespace, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleMethodNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理类名
        {
            List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleClassNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理结构体名
        {
            List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleStructNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理枚举名
        {
            List<ALittleEnumNameDec> decList = ALittleTreeChangeListener.findEnumNameDecList(project, mNamespace, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleEnumNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理单例
        {
            List<ALittleInstanceNameDec> decList = ALittleTreeChangeListener.findInstanceNameDecList(project, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (ALittleInstanceNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理参数
        {
            List<ALittleMethodParamNameDec> decList = ALittleUtil.findMethodParamNameDecList(
                    mClassCtorDec, mClassSetterDec
                    , mClassMethodDec, mClassStaticDec
                    , mGlobalMethodDec
                    , mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleMethodParamNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理表达式定义
        {
            List<ALittleVarAssignNameDec> decList = ALittleUtil.findVarAssignNameDecList(myElement, mKey);
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
            List<ALittleMethodNameDec> decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, mNamespace, "");
            for (ALittleMethodNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理类名
        {
            List<ALittleClassNameDec> decList = ALittleTreeChangeListener.findClassNameDecList(project, mNamespace, "");
            for (ALittleClassNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理结构体名
        {
            List<ALittleStructNameDec> decList = ALittleTreeChangeListener.findStructNameDecList(project, mNamespace, "");
            for (ALittleStructNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理枚举名
        {
            List<ALittleEnumNameDec> decList = ALittleTreeChangeListener.findEnumNameDecList(project, mNamespace, "");
            for (ALittleEnumNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理单例
        {
            List<ALittleInstanceNameDec> decList = ALittleTreeChangeListener.findInstanceNameDecList(project, mNamespace, "", true);
            for (ALittleInstanceNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.INSTANCE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理参数
        {
            List<ALittleMethodParamNameDec> decList = ALittleUtil.findMethodParamNameDecList(
                    mClassCtorDec, mClassSetterDec
                    , mClassMethodDec, mClassStaticDec
                    , mGlobalMethodDec
                    , "");
            for (ALittleMethodParamNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.PARAM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理表达式
        {
            List<ALittleVarAssignNameDec> decList = ALittleUtil.findVarAssignNameDecList(myElement, "");
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
