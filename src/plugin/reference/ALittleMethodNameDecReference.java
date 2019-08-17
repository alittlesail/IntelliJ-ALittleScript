package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ALittleMethodNameDecReference extends ALittleReference {
    public ALittleMethodNameDecReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    public PsiElement guessTypeForSetter() {
        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();

                List<ALittleAllType> allTypeList = new ArrayList<>();

                if (parent instanceof ALittleClassSetterDec) {
                    ALittleClassSetterDec method_dec = (ALittleClassSetterDec) parent;
                    ALittleMethodParamOneDec param_one_dec = method_dec.getMethodParamOneDec();
                    if (param_one_dec != null) allTypeList.add(param_one_dec.getMethodParamTypeDec().getAllType());
                }

                for (ALittleAllType allType : allTypeList) {
                    try {
                        return ALittleUtil.guessType(allType);
                    } catch (ALittleUtil.ALittleElementException ignored) {
                    }
                }
            }
        }

        return null;
    }

    public PsiElement guessTypeForGetter() {
        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();

                List<ALittleAllType> allTypeList = new ArrayList<>();

                if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec method_dec = (ALittleClassGetterDec) parent;
                    ALittleMethodReturnTypeDec return_type_dec = method_dec.getMethodReturnTypeDec();
                    if (return_type_dec != null) allTypeList.add(return_type_dec.getAllType());
                }

                for (ALittleAllType allType : allTypeList) {
                    try {
                        return ALittleUtil.guessType(allType);
                    } catch (ALittleUtil.ALittleElementException ignored) {
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

                List<ALittleAllType> allTypeList = new ArrayList<>();

                // 类成员函数
                if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec method_dec = (ALittleClassMethodDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_decList = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_decList) {
                            allTypeList.add(return_type_dec.getAllType());
                        }
                    }
                // getter
                } else if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec method_dec = (ALittleClassGetterDec) parent;
                    ALittleMethodReturnTypeDec return_type_dec = method_dec.getMethodReturnTypeDec();
                    if (return_type_dec != null) allTypeList.add(return_type_dec.getAllType());

                // setter
                } else if (parent instanceof ALittleClassSetterDec) {
                    ALittleClassSetterDec method_dec = (ALittleClassSetterDec) parent;
                    ALittleMethodParamOneDec param_one_dec = method_dec.getMethodParamOneDec();
                    if (param_one_dec != null) allTypeList.add(param_one_dec.getMethodParamTypeDec().getAllType());
                // 类静态函数
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec method_dec = (ALittleClassStaticDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_decList = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_decList) {
                            allTypeList.add(return_type_dec.getAllType());
                        }
                    }
                // 全局函数
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec method_dec = (ALittleGlobalMethodDec) parent;
                    ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnTypeDec> return_type_decList = return_dec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec return_type_dec : return_type_decList) {
                            allTypeList.add(return_type_dec.getAllType());
                        }
                    }
                }

                for (ALittleAllType allType : allTypeList) {
                    try {
                        guess_list.add(ALittleUtil.guessType(allType));
                    } catch (ALittleUtil.ALittleElementException ignored) {

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
        List<ALittleMethodNameDec> decList = new ArrayList<>();

        PsiElement method_dec = myElement.getParent();
        if (method_dec.getParent() instanceof ALittleClassDec) {
            ALittleClassDec classDec = (ALittleClassDec) method_dec.getParent();
            ALittleUtil.findMethodNameDecList(project, mNamespace, classDec, mKey, decList, null, 100);
        } else if (method_dec.getParent() instanceof ALittleNamespaceDec) {
            decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, mNamespace, mKey);
        }
        List<ResolveResult> results = new ArrayList<>();
        for (ALittleMethodNameDec dec : decList) {
            results.add(new PsiElementResolveResult(dec));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiElement method_dec = myElement.getParent();
        List<LookupElement> variants = new ArrayList<>();
        // 类内部的函数
        if (method_dec.getParent() instanceof ALittleClassDec) {
            ALittleClassDec classDec = (ALittleClassDec) method_dec.getParent();

            List<ALittleMethodNameDec> decList = new ArrayList<>();
            List<Icon> iconList = new ArrayList<>();
            ALittleUtil.findMethodNameDecList(project, mNamespace, classDec, "", decList, iconList, 100);
            for (int i = 0; i < decList.size(); ++i) {
                ALittleMethodNameDec dec = decList.get(i);
                Icon icon = null;
                if (i < iconList.size()) icon = iconList.get(i);
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(icon).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        // 全局函数
        } else if (method_dec.getParent() instanceof ALittleNamespaceDec) {
            List<ALittleMethodNameDec> decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, mNamespace, "");
            for (ALittleMethodNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}
