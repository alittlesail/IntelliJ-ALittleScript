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

public class ALittleMethodNameDecReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;
    private String m_src_namespace;

    public ALittleMethodNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        m_src_namespace = ALittleUtil.getNamespaceName((ALittleFile)element.getContainingFile());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    public PsiElement guessTypeForSetter() {
        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();

                List<ALittleAllType> all_type_list = new ArrayList<>();

                if (parent instanceof ALittleClassSetterDec) {
                    ALittleClassSetterDec method_dec = (ALittleClassSetterDec) parent;
                    ALittleMethodParamOneDec param_one_dec = method_dec.getMethodParamOneDec();
                    if (param_one_dec != null) all_type_list.add(param_one_dec.getMethodParamTypeDec().getAllType());
                }

                for (ALittleAllType all_type : all_type_list) {
                    PsiElement guess_type = ALittleUtil.guessType(all_type);
                    if (guess_type != null) {
                        return guess_type;
                    }
                }
            }
        }

        return null;
    }

    public PsiElement guessTypeForGetter() {
        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();

                List<ALittleAllType> all_type_list = new ArrayList<>();

                if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec method_dec = (ALittleClassGetterDec) parent;
                    ALittleMethodReturnTypeDec return_type_dec = method_dec.getMethodReturnTypeDec();
                    if (return_type_dec != null) all_type_list.add(return_type_dec.getAllType());
                }

                for (ALittleAllType all_type : all_type_list) {
                    PsiElement guess_type = ALittleUtil.guessType(all_type);
                    if (guess_type != null) {
                        return guess_type;
                    }
                }
            }
        }

        return null;
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();

                List<ALittleAllType> all_type_list = new ArrayList<>();

                if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec method_dec = (ALittleClassMethodDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                            all_type_list.add(return_type_dec.getAllType());
                        }
                    }
                } else if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec method_dec = (ALittleClassGetterDec) parent;
                    ALittleMethodReturnTypeDec return_type_dec = method_dec.getMethodReturnTypeDec();
                    if (return_type_dec != null) all_type_list.add(return_type_dec.getAllType());
                } else if (parent instanceof ALittleClassSetterDec) {
                    ALittleClassSetterDec method_dec = (ALittleClassSetterDec) parent;
                    ALittleMethodParamOneDec param_one_dec = method_dec.getMethodParamOneDec();
                    if (param_one_dec != null) all_type_list.add(param_one_dec.getMethodParamTypeDec().getAllType());
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec method_dec = (ALittleClassStaticDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                            all_type_list.add(return_type_dec.getAllType());
                        }
                    }
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec method_dec = (ALittleGlobalMethodDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_dec_list = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_dec_list) {
                            all_type_list.add(return_type_dec.getAllType());
                        }
                    }
                }

                for (ALittleAllType all_type : all_type_list) {
                    PsiElement guess_type = ALittleUtil.guessType(all_type);
                    if (guess_type != null) {
                        guess_list.add(guess_type);
                    }
                }
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        List<ALittleMethodNameDec> dec_list = new ArrayList<>();

        PsiElement method_dec = myElement.getParent();
        if (method_dec.getParent() instanceof ALittleClassDec) {
            ALittleClassDec class_dec = (ALittleClassDec) method_dec.getParent();
            ALittleUtil.findMethodNameDecList(project, m_src_namespace, class_dec, m_key, dec_list, 100);
        } else if (method_dec.getParent() instanceof ALittleNamespaceDec) {
            dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, m_src_namespace, m_key);
        }
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleMethodNameDec dec : dec_list) {
            results.add(new PsiElementResolveResult(dec));
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
        List<ALittleMethodNameDec> dec_list = new ArrayList<>();
        PsiElement method_dec = myElement.getParent();
        // 类内部的函数
        if (method_dec.getParent() instanceof ALittleClassDec) {
            ALittleClassDec class_dec = (ALittleClassDec) method_dec.getParent();
            ALittleUtil.findMethodNameDecList(project, m_src_namespace, class_dec, "", dec_list, 100);
        // 全局函数
        } else if (method_dec.getParent() instanceof ALittleNamespaceDec) {
            dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, m_src_namespace, "");
        }
        List<LookupElement> variants = new ArrayList<>();
        for (ALittleMethodNameDec dec : dec_list) {
            variants.add(LookupElementBuilder.create(dec.getText()).
                    withIcon(ALittleIcons.FILE).
                    withTypeText(dec.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }
}
