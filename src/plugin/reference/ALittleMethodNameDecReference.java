package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ALittleMethodNameDecReference extends ALittleReference<ALittleMethodNameDec> {
    public ALittleMethodNameDecReference(@NotNull ALittleMethodNameDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        PsiElement parent = myElement.getParent();
        // 处理getter
        if (parent instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec classGetterDec = (ALittleClassGetterDec) parent;

            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
            info.value = "Functor<(";
            info.element = classGetterDec;
            info.functorAwait = false;
            info.functorParamList = new ArrayList<>();
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            // 第一个参数是类
            ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec)classGetterDec.getParent()).guessType();
            info.functorParamList.add(classGuessInfo);
            info.functorParamNameList.add(classGuessInfo.value);
            info.value += classGuessInfo.value + ")";

            List<String> typeList = new ArrayList<>();
            // 添加返回值列表
            ALittleAllType allType = classGetterDec.getAllType();
            if (allType == null) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "指向的getter函数没有定义返回值");
            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
            typeList.add(GuessInfo.value);
            info.functorReturnList.add(GuessInfo);

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
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            List<String> typeList = new ArrayList<>();
            // 第一个参数是类
            ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec) classSetterDec.getParent()).guessType();
            typeList.add(classGuessInfo.value);
            info.functorParamList.add(classGuessInfo);
            info.functorParamNameList.add(classGuessInfo.value);

            // 添加参数列表
            ALittleMethodParamOneDec oneDec = classSetterDec.getMethodParamOneDec();
            if (oneDec == null)
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "指向的setter函数没有定义参数");

            ALittleReferenceUtil.GuessTypeInfo guessInfo = oneDec.getAllType().guessType();
            typeList.add(guessInfo.value);
            info.functorParamList.add(guessInfo);
            if (oneDec.getMethodParamNameDec() != null) {
                info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
            } else {
                info.functorParamNameList.add("");
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
            info.functorAwait = classMethodDec.getCoModifier() != null && classMethodDec.getCoModifier().getText().equals("await");
            if (info.functorAwait) {
                info.value = "Functor<await(";
            }
            info.functorParamList = new ArrayList<>();
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            List<String> typeList = new ArrayList<>();
            // 第一个参数是类
            ALittleReferenceUtil.GuessTypeInfo classGuessInfo = ((ALittleClassDec)classMethodDec.getParent()).guessType();
            typeList.add(classGuessInfo.value);
            info.functorParamList.add(classGuessInfo);
            info.functorParamNameList.add(classGuessInfo.value);

            // 添加参数列表
            ALittleMethodParamDec paramDec = classMethodDec.getMethodParamDec();
            if (paramDec != null) {
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec oneDec : oneDecList) {
                    ALittleAllType allType = oneDec.getAllType();
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    typeList.add(GuessInfo.value);
                    info.functorParamList.add(GuessInfo);
                    if (oneDec.getMethodParamNameDec() != null) {
                        info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                    } else {
                        info.functorParamNameList.add("");
                    }
                }
                ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                if (tailDec != null) {
                    info.functorParamTail = tailDec.guessType();
                    typeList.add(tailDec.getText());
                }
            }
            info.value += String.join(",", typeList) + ")";
            typeList = new ArrayList<>();
            // 添加返回值列表
            ALittleMethodReturnDec returnDec = classMethodDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
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
            info.functorAwait = classStaticDec.getCoModifier() != null && classStaticDec.getCoModifier().getText().equals("await");
            if (info.functorAwait) {
                info.value = "Functor<await(";
            }
            info.functorParamList = new ArrayList<>();
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            List<String> typeList = new ArrayList<>();
            ALittleMethodParamDec paramDec = classStaticDec.getMethodParamDec();
            if (paramDec != null) {
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec oneDec : oneDecList) {
                    ALittleAllType allType = oneDec.getAllType();
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    typeList.add(GuessInfo.value);
                    info.functorParamList.add(GuessInfo);
                    if (oneDec.getMethodParamNameDec() != null) {
                        info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                    } else {
                        info.functorParamNameList.add("");
                    }
                }
                ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                if (tailDec != null) {
                    info.functorParamTail = tailDec.guessType();
                    typeList.add(tailDec.getText());
                }
            }
            info.value += String.join(",", typeList) + ")";
            typeList = new ArrayList<>();
            ALittleMethodReturnDec returnDec = classStaticDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
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
            info.functorAwait = globalMethodDec.getCoModifier() != null && globalMethodDec.getCoModifier().getText().equals("await");
            if (info.functorAwait) {
                info.value = "Functor<await(";
            }
            info.functorParamList = new ArrayList<>();
            info.functorParamNameList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            List<String> typeList = new ArrayList<>();
            ALittleMethodParamDec paramDec = globalMethodDec.getMethodParamDec();
            if (paramDec != null) {
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec oneDec : oneDecList) {
                    ALittleAllType allType = oneDec.getAllType();
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    typeList.add(GuessInfo.value);
                    info.functorParamList.add(GuessInfo);
                    if (oneDec.getMethodParamNameDec() != null) {
                        info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                    } else {
                        info.functorParamNameList.add("");
                    }
                }
                ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                if (tailDec != null) {
                    info.functorParamTail = tailDec.guessType();
                    typeList.add(tailDec.getText());
                }
            }
            info.value += String.join(",", typeList) + ")";
            typeList = new ArrayList<>();
            ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    typeList.add(GuessInfo.value);
                    info.functorReturnList.add(GuessInfo);
                }
            }
            if (!typeList.isEmpty()) info.value += ":";
            info.value += String.join(",", typeList) + ">";
            guessList.add(info);
        }

        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement parent = myElement.getParent();
        if (!(parent.getParent() instanceof ALittleClassDec)) return;
        ALittleClassDec classDec = (ALittleClassDec)parent.getParent();
        // 计算父类
        PsiHelper.ClassExtendsData classExtendsData = PsiHelper.findClassExtends(classDec);
        if (classExtendsData == null) return;

        PsiHelper.ClassAttrType attrType;
        if (parent instanceof ALittleClassMethodDec) {
            attrType = PsiHelper.ClassAttrType.FUN;
        } else if (parent instanceof ALittleClassStaticDec) {
            attrType = PsiHelper.ClassAttrType.STATIC;
        } else if (parent instanceof ALittleClassGetterDec) {
            attrType = PsiHelper.ClassAttrType.GETTER;
        } else if (parent instanceof ALittleClassSetterDec) {
            attrType = PsiHelper.ClassAttrType.SETTER;
        } else {
            return;
        }

        PsiElement result = PsiHelper.findFirstClassAttrFromExtends(classExtendsData.dec, attrType, mKey, 100);
        if (!(result instanceof ALittleMethodNameDec)) return;
        ALittleMethodNameDec methodNameDec = (ALittleMethodNameDec)result;

        ALittleReferenceUtil.GuessTypeInfo myGuessTypeInfo = guessTypes().get(0);
        if (myGuessTypeInfo == null) return;
        ALittleReferenceUtil.GuessTypeInfo extendsGuessTypeInfo = methodNameDec.guessType();
        try {
            ALittleReferenceOpUtil.guessTypeEqual(methodNameDec, extendsGuessTypeInfo, myElement, myGuessTypeInfo);
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "该函数是从父类继承下来，但是定义不一致");
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        PsiElement methodDec = myElement.getParent();
        List<LookupElement> variants = new ArrayList<>();
        // 类内部的函数
        if (methodDec.getParent() instanceof ALittleClassDec) {
            ALittleClassDec classDec = (ALittleClassDec) methodDec.getParent();

            List<PsiElement> decList = new ArrayList<>();
            PsiHelper.findClassMethodNameDecList(classDec, PsiHelper.sAccessPrivateAndProtectedAndPublic, "", decList, 100);
            for (int i = 0; i < decList.size(); ++i) {
                PsiElement dec = decList.get(i);
                Icon icon = null;
                if (dec.getParent() instanceof ALittleClassMethodDec) {
                    icon = ALittleIcons.MEMBER_METHOD;
                } else if (dec.getParent() instanceof ALittleClassSetterDec) {
                    icon = ALittleIcons.SETTER_METHOD;
                } else if (dec.getParent() instanceof ALittleClassGetterDec) {
                    icon = ALittleIcons.GETTER_METHOD;
                } else if (dec.getParent() instanceof ALittleClassStaticDec) {
                    icon = ALittleIcons.STATIC_METHOD;
                }
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(icon).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        // 全局函数
        } else if (methodDec.getParent() instanceof ALittleNamespaceDec) {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.GLOBAL_METHOD, psiFile, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}
