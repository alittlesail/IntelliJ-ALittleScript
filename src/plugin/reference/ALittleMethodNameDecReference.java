package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
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
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();
        PsiElement parent = myElement.getParent();
        // 处理getter
        if (parent instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec classGetterDec = (ALittleClassGetterDec) parent;

            ALittleGuessFunctor info = new ALittleGuessFunctor(classGetterDec);
            // 第一个参数是类
            ALittleGuess classGuess = ((ALittleClassDec)classGetterDec.getParent()).guessType();
            info.functorParamList.add(classGuess);
            info.functorParamNameList.add(classGuess.value);

            // 添加返回值列表
            ALittleAllType allType = classGetterDec.getAllType();
            if (allType == null) throw new ALittleGuessException(myElement, "指向的getter函数没有定义返回值");
            info.functorReturnList.add(allType.guessType());

            info.UpdateValue();
            guessList.add(info);
        } else if (parent instanceof ALittleClassSetterDec) {
            ALittleClassSetterDec classSetterDec = (ALittleClassSetterDec) parent;

            ALittleGuessFunctor info = new ALittleGuessFunctor(classSetterDec);
            // 第一个参数是类
            ALittleGuess classGuess = ((ALittleClassDec) classSetterDec.getParent()).guessType();
            info.functorParamList.add(classGuess);
            info.functorParamNameList.add(classGuess.value);

            // 添加参数列表
            ALittleMethodParamOneDec oneDec = classSetterDec.getMethodParamOneDec();
            if (oneDec == null)
                throw new ALittleGuessException(myElement, "指向的setter函数没有定义参数");

            info.functorParamList.add(oneDec.getAllType().guessType());
            if (oneDec.getMethodParamNameDec() != null) {
                info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
            } else {
                info.functorParamNameList.add("");
            }

            info.UpdateValue();
            guessList.add(info);
        } else if (parent instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec classMethodDec = (ALittleClassMethodDec) parent;

            ALittleGuessFunctor info = new ALittleGuessFunctor(classMethodDec);
            info.functorAwait = classMethodDec.getCoModifier() != null && classMethodDec.getCoModifier().getText().equals("await");
            // 第一个参数是类
            ALittleGuess classGuess = ((ALittleClassDec)classMethodDec.getParent()).guessType();
            info.functorParamList.add(classGuess);
            info.functorParamNameList.add(classGuess.value);

            // 添加参数列表
            ALittleMethodParamDec paramDec = classMethodDec.getMethodParamDec();
            if (paramDec != null) {
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec oneDec : oneDecList) {
                    ALittleAllType allType = oneDec.getAllType();
                    info.functorParamList.add(allType.guessType());
                    if (oneDec.getMethodParamNameDec() != null) {
                        info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                    } else {
                        info.functorParamNameList.add("");
                    }
                }
                ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                if (tailDec != null) {
                    info.functorParamTail = tailDec.guessType();
                }
            }
            // 添加返回值列表
            ALittleMethodReturnDec returnDec = classMethodDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    info.functorReturnList.add(allType.guessType());
                }
                ALittleMethodReturnTailDec tailDec = returnDec.getMethodReturnTailDec();
                if (tailDec != null) {
                    info.functorReturnTail = tailDec.guessType();
                }
            }
            info.UpdateValue();
            guessList.add(info);
        } else if (parent instanceof ALittleClassStaticDec) {
            ALittleClassStaticDec classStaticDec = (ALittleClassStaticDec) parent;

            ALittleGuessFunctor info = new ALittleGuessFunctor(classStaticDec);
            info.functorAwait = classStaticDec.getCoModifier() != null && classStaticDec.getCoModifier().getText().equals("await");

            ALittleMethodParamDec paramDec = classStaticDec.getMethodParamDec();
            if (paramDec != null) {
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec oneDec : oneDecList) {
                    ALittleAllType allType = oneDec.getAllType();
                    info.functorParamList.add(allType.guessType());
                    if (oneDec.getMethodParamNameDec() != null) {
                        info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                    } else {
                        info.functorParamNameList.add("");
                    }
                }
                ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                if (tailDec != null) {
                    info.functorParamTail = tailDec.guessType();
                }
            }
            ALittleMethodReturnDec returnDec = classStaticDec.getMethodReturnDec();
            if (returnDec != null) {
                List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    info.functorReturnList.add(allType.guessType());
                }
                ALittleMethodReturnTailDec tailDec = returnDec.getMethodReturnTailDec();
                if (tailDec != null) {
                    info.functorReturnTail = tailDec.guessType();
                }
            }
            info.UpdateValue();
            guessList.add(info);
        } else if (parent instanceof ALittleGlobalMethodDec) {
            ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec) parent;

            ALittleGuessFunctor info = new ALittleGuessFunctor(globalMethodDec);
            info.functorAwait = globalMethodDec.getCoModifier() != null && globalMethodDec.getCoModifier().getText().equals("await");
            if (globalMethodDec.getProtoModifier() != null) {
                PsiElement error = globalMethodDec.getMethodNameDec();
                if (error == null) {
                    error = globalMethodDec;
                }

                // 如果是带协议注解，那么一定是一个await
                info.functorAwait = true;
                info.functorProto = globalMethodDec.getProtoModifier().getText();

                ALittleMethodParamDec paramDec = globalMethodDec.getMethodParamDec();
                if (paramDec == null) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数必须是两个参数");
                List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                if (oneDecList.size() != 2) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数必须是两个参数");
                ALittleGuess guess = oneDecList.get(1).getAllType().guessType();
                if (!(guess instanceof ALittleGuessStruct)) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数第二个参数必须是struct");

                if (info.functorProto.equals("@Http")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpSender", true);
                    if (!(element instanceof ALittleClassNameDec)) throw new ALittleGuessException(error, "语言框架中找不到ALittle.IHttpSender");
                    ALittleClassNameDec classNameDec = (ALittleClassNameDec)element;
                    info.functorParamList.add(classNameDec.guessType());
                    info.functorParamNameList.add("sender");
                    info.functorParamList.add(guess);
                    info.functorParamNameList.add("param");

                    ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                    if (returnDec == null) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值必须是struct");
                    List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                    if (allTypeList.size() != 1) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值有且仅有一个struct");
                    ALittleGuess returnGuess = allTypeList.get(0).guessType();
                    if (!(returnGuess instanceof ALittleGuessStruct)) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值必须是struct");
                    info.functorReturnList.add(ALittleGuessPrimitive.sStringGuess);
                    info.functorReturnList.add(returnGuess);
                } else if (info.functorProto.equals("@HttpDownload")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpFileSender", true);
                    if (!(element instanceof ALittleClassNameDec)) throw new ALittleGuessException(error, "语言框架中找不到ALittle.IHttpFileSender");
                    ALittleClassNameDec classNameDec = (ALittleClassNameDec)element;
                    info.functorParamList.add(classNameDec.guessType());
                    info.functorParamNameList.add("sender");
                    info.functorParamList.add(guess);
                    info.functorParamNameList.add("param");

                    info.functorReturnList.add(ALittleGuessPrimitive.sStringGuess);
                } else if (info.functorProto.equals("@HttpUpload")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpFileSender", true);
                    if (!(element instanceof ALittleClassNameDec)) throw new ALittleGuessException(error, "语言框架中找不到ALittle.IHttpFileSender");
                    ALittleClassNameDec classNameDec = (ALittleClassNameDec)element;
                    info.functorParamList.add(classNameDec.guessType());
                    info.functorParamNameList.add("sender");
                    info.functorParamList.add(guess);
                    info.functorParamNameList.add("param");

                    ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                    if (returnDec == null) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值必须是struct");
                    List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                    if (allTypeList.size() != 1) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值有且仅有一个struct");
                    ALittleGuess returnGuess = allTypeList.get(0).guessType();
                    if (!(returnGuess instanceof ALittleGuessStruct)) throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值必须是struct");
                    info.functorReturnList.add(ALittleGuessPrimitive.sStringGuess);
                    info.functorReturnList.add(returnGuess);
                } else if (info.functorProto.equals("@Msg")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IMsgCommon", true);
                    if (!(element instanceof ALittleClassNameDec)) throw new ALittleGuessException(error, "语言框架中找不到ALittle.IMsgCommon");
                    ALittleClassNameDec classNameDec = (ALittleClassNameDec)element;
                    info.functorParamList.add(classNameDec.guessType());
                    info.functorParamNameList.add("sender");
                    info.functorParamList.add(guess);
                    info.functorParamNameList.add("param");

                    ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                    if (returnDec != null) {
                        List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                        if (allTypeList.size() > 0) {
                            ALittleGuess returnGuess = allTypeList.get(0).guessType();
                            if (!(returnGuess instanceof ALittleGuessStruct))
                                throw new ALittleGuessException(error, "带" + info.functorProto + "注解的函数返回值必须是struct");
                            info.functorReturnList.add(ALittleGuessPrimitive.sStringGuess);
                            info.functorReturnList.add(returnGuess);
                        }
                    }
                } else {
                    throw new ALittleGuessException(error, "未知的注解类型:" + info.functorProto);
                }
            } else {
                ALittleMethodParamDec paramDec = globalMethodDec.getMethodParamDec();
                if (paramDec != null) {
                    List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec oneDec : oneDecList) {
                        ALittleAllType allType = oneDec.getAllType();
                        info.functorParamList.add(allType.guessType());
                        if (oneDec.getMethodParamNameDec() != null) {
                            info.functorParamNameList.add(oneDec.getMethodParamNameDec().getIdContent().getText());
                        } else {
                            info.functorParamNameList.add("");
                        }
                    }
                    ALittleMethodParamTailDec tailDec = paramDec.getMethodParamTailDec();
                    if (tailDec != null) {
                        info.functorParamTail = tailDec.guessType();
                    }
                }
                ALittleMethodReturnDec returnDec = globalMethodDec.getMethodReturnDec();
                if (returnDec != null) {
                    List<ALittleAllType> allTypeList = returnDec.getAllTypeList();
                    for (ALittleAllType allType : allTypeList) {
                        info.functorReturnList.add(allType.guessType());
                    }
                    ALittleMethodReturnTailDec tailDec = returnDec.getMethodReturnTailDec();
                    if (tailDec != null) {
                        info.functorReturnTail = tailDec.guessType();
                    }
                }
            }
            info.UpdateValue();
            guessList.add(info);
        }

        return guessList;
    }

    public void checkError() throws ALittleGuessException {
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

        ALittleGuess myGuessTypeInfo = guessTypes().get(0);
        if (myGuessTypeInfo == null) return;
        ALittleGuess extendsGuessTypeInfo = methodNameDec.guessType();
        try {
            ALittleReferenceOpUtil.guessTypeEqual(methodNameDec, extendsGuessTypeInfo, myElement, myGuessTypeInfo);
        } catch (ALittleGuessException ignored) {
            throw new ALittleGuessException(myElement, "该函数是从父类继承下来，但是定义不一致");
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
