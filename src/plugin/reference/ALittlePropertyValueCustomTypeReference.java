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
import plugin.psi.impl.ALittlePsiImplUtil;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueCustomTypeReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;
    private ALittleClassDec m_class_dec = null;
    private ALittleClassCtorDec m_class_ctor_dec = null;
    private ALittleClassSetterDec m_class_setter_dec = null;
    private ALittleClassMethodDec m_class_method_dec = null;
    private ALittleClassStaticDec m_class_static_dec = null;
    private ALittleGlobalMethodDec m_global_method_dec = null;
    private String m_src_namespace;

    public ALittlePropertyValueCustomTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        reloadInfo();
    }

    private void reloadInfo() {
        m_class_dec = null;
        m_class_ctor_dec = null;
        m_class_setter_dec = null;
        m_class_method_dec = null;
        m_class_static_dec = null;
        m_src_namespace = "";

        PsiElement parent = myElement;
        while (true) {
            if (parent == null) break;

            if (parent instanceof ALittleClassDec) {
                m_class_dec = (ALittleClassDec)parent;
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                m_class_ctor_dec = (ALittleClassCtorDec)parent;
            } else if (parent instanceof ALittleClassSetterDec) {
                m_class_setter_dec = (ALittleClassSetterDec)parent;
            } else if (parent instanceof ALittleClassMethodDec) {
                m_class_method_dec = (ALittleClassMethodDec)parent;
            } else if (parent instanceof ALittleClassStaticDec) {
                m_class_static_dec = (ALittleClassStaticDec)parent;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                m_global_method_dec = (ALittleGlobalMethodDec)parent;
            }

            parent = parent.getParent();
        }

        m_src_namespace = ALittleUtil.getNamespaceName((ALittleFile) myElement.getContainingFile());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            PsiElement guess = null;
            if (element instanceof ALittleNamespaceNameDec) {
                guess = element;
            } else if (element instanceof ALittleClassNameDec) {
                guess = element;
            } else if (element instanceof ALittleStructNameDec) {
                guess = element;
            } else if (element instanceof ALittleEnumNameDec) {
                guess = element.getParent();
            } else if (element instanceof ALittleInstanceNameDec) {
                guess = ((ALittleInstanceNameDec) element).guessType();
            } else if (element instanceof ALittleMethodParamNameDec) {
                guess = ((ALittleMethodParamNameDec) element).guessType();
            } else if (element instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) element).guessType();
            } else if (element instanceof ALittleMethodNameDec) {
                guess = element;
            }

            if (guess != null) guess_list.add(guess);
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        List<ResolveResult> results = new ArrayList<>();

        // 处理命名域
        {
            List<ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleNamespaceNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理全局函数
        {
            List<ALittleMethodNameDec> dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, m_src_namespace, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleMethodNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理类名
        {
            List<ALittleClassNameDec> dec_list = ALittleTreeChangeListener.findClassNameDecList(project, m_src_namespace, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleClassNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理结构体名
        {
            List<ALittleStructNameDec> dec_list = ALittleTreeChangeListener.findStructNameDecList(project, m_src_namespace, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleStructNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理枚举名
        {
            List<ALittleEnumNameDec> dec_list = ALittleTreeChangeListener.findEnumNameDecList(project, m_src_namespace, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleEnumNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理单例
        {
            List<ALittleInstanceNameDec> dec_list = ALittleTreeChangeListener.findInstanceNameDecList(project, m_src_namespace, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleInstanceNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理参数
        {
            List<ALittleMethodParamNameDec> dec_list = ALittleUtil.findMethodParamNameDecList(
                    m_class_ctor_dec, m_class_setter_dec
                    , m_class_method_dec, m_class_static_dec
                    , m_global_method_dec
                    , m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleMethodParamNameDec dec : dec_list) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理表达式定义
        {
            List<ALittleVarAssignNameDec> dec_list = ALittleUtil.findVarAssignNameDecList(myElement, m_key);
            if (!dec_list.isEmpty()) results.clear();
            for (ALittleVarAssignNameDec dec : dec_list) {
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
        // 处理命名域
        {
            List<ALittleNamespaceNameDec> dec_list = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (ALittleNamespaceNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理全局函数
        {
            List<ALittleMethodNameDec> dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, m_src_namespace, "");
            for (ALittleMethodNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理类名
        {
            List<ALittleClassNameDec> dec_list = ALittleTreeChangeListener.findClassNameDecList(project, m_src_namespace, "");
            for (ALittleClassNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理结构体名
        {
            List<ALittleStructNameDec> dec_list = ALittleTreeChangeListener.findStructNameDecList(project, m_src_namespace, "");
            for (ALittleStructNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理枚举名
        {
            List<ALittleEnumNameDec> dec_list = ALittleTreeChangeListener.findEnumNameDecList(project, m_src_namespace, "");
            for (ALittleEnumNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理单例
        {
            List<ALittleInstanceNameDec> dec_list = ALittleTreeChangeListener.findInstanceNameDecList(project, m_src_namespace, "");
            for (ALittleInstanceNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理参数
        {
            List<ALittleMethodParamNameDec> dec_list = ALittleUtil.findMethodParamNameDecList(
                    m_class_ctor_dec, m_class_setter_dec
                    , m_class_method_dec, m_class_static_dec
                    , m_global_method_dec
                    , "");
            for (ALittleMethodParamNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理表达式
        {
            List<ALittleVarAssignNameDec> dec_list = ALittleUtil.findVarAssignNameDecList(myElement, "");
            for (ALittleVarAssignNameDec dec : dec_list) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
