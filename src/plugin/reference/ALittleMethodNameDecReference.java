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

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();
            if (element instanceof ALittleMethodNameDec) {
                PsiElement parent = element.getParent();
                if (parent instanceof ALittleClassGetterDec) {
                    ALittleClassGetterDec classGetterDec = (ALittleClassGetterDec) parent;

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
                    info.value = "Functor<(";
                    info.element = classGetterDec;
                    info.functorAwait = false;
                    info.functorParamList = new ArrayList<>();
                    info.functorReturnList = new ArrayList<>();

                    // 第一个参数是类
                    ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec)classGetterDec.getParent()).guessType();
                    info.functorParamList.add(classGuessInfo);
                    info.value += classGuessInfo.value + ")";

                    List<String> typeList = new ArrayList<>();
                    // 添加返回值列表
                    ALittleMethodReturnTypeDec returnTypeDec = classGetterDec.getMethodReturnTypeDec();
                    if (returnTypeDec != null) {
                        ALittleAllType allType = returnTypeDec.getAllType();
                        ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                        typeList.add(GuessInfo.value);
                        info.functorReturnList.add(GuessInfo);
                    }
                    if (!typeList.isEmpty()) info.value += ":";
                    info.value += String.join(",", typeList) + ">";
                    guessList.add(info);
                } else if (parent instanceof ALittleClassSetterDec) {
                    ALittleClassSetterDec classSetterDec = (ALittleClassSetterDec) parent;

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
                    info.value = "Functor<(";
                    info.element = classSetterDec;
                    info.functorAwait = false;
                    info.functorParamList = new ArrayList<>();
                    info.functorReturnList = new ArrayList<>();

                    List<String> typeList = new ArrayList<>();
                    // 第一个参数是类
                    ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec)classSetterDec.getParent()).guessType();
                    typeList.add(classGuessInfo.value);
                    info.functorParamList.add(classGuessInfo);

                    // 添加参数列表
                    ALittleMethodParamOneDec oneDec = classSetterDec.getMethodParamOneDec();
                    if (oneDec != null) {
                        ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                        ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                        typeList.add(GuessInfo.value);
                        info.functorParamList.add(GuessInfo);
                    }
                    info.value += String.join(",", typeList) + ")";
                    info.value += ">";
                    guessList.add(info);
                } else if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec classMethodDec = (ALittleClassMethodDec) parent;

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
                    info.value = "Functor<(";
                    info.element = classMethodDec;
                    info.functorAwait = classMethodDec.getCoroutineModifier() != null && classMethodDec.getCoroutineModifier().getText().equals("await");
                    if (info.functorAwait) {
                        info.value = "Functor<await(";
                    }
                    info.functorParamList = new ArrayList<>();
                    info.functorReturnList = new ArrayList<>();

                    List<String> typeList = new ArrayList<>();
                    // 第一个参数是类
                    ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec)classMethodDec.getParent()).guessType();
                    typeList.add(classGuessInfo.value);
                    info.functorParamList.add(classGuessInfo);

                    // 添加参数列表
                    ALittleMethodParamDec paramDec = classMethodDec.getMethodParamDec();
                    if (paramDec != null) {
                        List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec oneDec : oneDecList) {
                            ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorParamList.add(GuessInfo);
                        }
                    }
                    info.value += String.join(",", typeList) + ")";
                    typeList = new ArrayList<>();
                    // 添加返回值列表
                    ALittleMethodReturnDec returnDec = classMethodDec.getMethodReturnDec();
                    if (returnDec != null) {
                        List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                            ALittleAllType allType = returnTypeDec.getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorReturnList.add(GuessInfo);
                        }
                    }
                    if (!typeList.isEmpty()) info.value += ":";
                    info.value += String.join(",", typeList) + ">";
                    guessList.add(info);
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec classStaticDec = (ALittleClassStaticDec) parent;

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
                    info.value = "Functor<(";
                    info.element = classStaticDec;
                    info.functorAwait = classStaticDec.getCoroutineModifier() != null && classStaticDec.getCoroutineModifier().getText().equals("await");
                    if (info.functorAwait) {
                        info.value = "Functor<await(";
                    }
                    info.functorParamList = new ArrayList<>();
                    info.functorReturnList = new ArrayList<>();

                    List<String> typeList = new ArrayList<>();
                    ALittleMethodParamDec paramDec = classStaticDec.getMethodParamDec();
                    if (paramDec != null) {
                        List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec oneDec : oneDecList) {
                            ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorParamList.add(GuessInfo);
                        }
                    }
                    info.value += String.join(",", typeList) + ")";
                    typeList = new ArrayList<>();
                    ALittleMethodReturnDec returnDec = classStaticDec.getMethodReturnDec();
                    if (returnDec != null) {
                        List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                            ALittleAllType allType = returnTypeDec.getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorReturnList.add(GuessInfo);
                        }
                    }
                    if (!typeList.isEmpty()) info.value += ":";
                    info.value += String.join(",", typeList) + ">";
                    guessList.add(info);
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec) parent;

                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
                    info.value = "Functor<(";
                    info.element = globalMethodDec;
                    info.functorAwait = globalMethodDec.getCoroutineModifier() != null && globalMethodDec.getCoroutineModifier().getText().equals("await");
                    if (info.functorAwait) {
                        info.value = "Functor<await(";
                    }
                    info.functorParamList = new ArrayList<>();
                    info.functorReturnList = new ArrayList<>();

                    List<String> typeList = new ArrayList<>();
                    ALittleMethodParamDec paramDec = globalMethodDec.getMethodParamDec();
                    if (paramDec != null) {
                        List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec oneDec : oneDecList) {
                            ALittleAllType allType = oneDec.getMethodParamTypeDec().getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorParamList.add(GuessInfo);
                        }
                    }
                    info.value += String.join(",", typeList) + ")";
                    typeList = new ArrayList<>();
                    ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                    if (returnDec != null) {
                        List<ALittleMethodReturnTypeDec> returnTypeDecList = returnDec.getMethodReturnTypeDecList();
                        for (ALittleMethodReturnTypeDec returnTypeDec : returnTypeDecList) {
                            ALittleAllType allType = returnTypeDec.getAllType();
                            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                            typeList.add(GuessInfo.value);
                            info.functorReturnList.add(GuessInfo);
                        }
                    }
                    if (!typeList.isEmpty()) info.value += ":";
                    info.value += String.join(",", typeList) + ">";
                    guessList.add(info);
                }
            }
        }

        return guessList;
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
