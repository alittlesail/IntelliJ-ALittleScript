package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
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
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        PsiElement parent = myElement.getParent();
        // 处理getter
        if (parent instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec class_getter_dec = (ALittleClassGetterDec) parent;
            ALittleClassElementDec class_element_dec = (ALittleClassElementDec) class_getter_dec.getParent();
            PsiElement class_body_dec = class_element_dec.getParent();
            ALittleClassDec class_dec = (ALittleClassDec) class_body_dec.getParent();

            ALittleGuessFunctor info = new ALittleGuessFunctor(class_getter_dec);
            info.const_modifier = PsiHelper.isConst(class_element_dec.getModifierList());

            // 第一个参数是类
            ALittleGuess class_guess = class_dec.guessType();
            info.param_list.add(class_guess);
            info.param_nullable_list.add(false);
            info.param_name_list.add(class_guess.getValue());

            // 添加返回值列表
            ALittleAllType all_type = class_getter_dec.getAllType();
            if (all_type == null) throw new ALittleGuessException(myElement, "指向的getter函数没有定义返回值");
            ALittleGuess all_type_guess = all_type.guessType();
            info.return_list.add(all_type_guess);

            info.updateValue();
            guess_list.add(info);
        } else if (parent instanceof ALittleClassSetterDec) {
            ALittleClassSetterDec class_setter_dec = (ALittleClassSetterDec) parent;
            ALittleClassElementDec class_element_dec = (ALittleClassElementDec) class_setter_dec.getParent();
            PsiElement class_body_dec = class_element_dec.getParent();
            ALittleClassDec class_dec = (ALittleClassDec) class_body_dec.getParent();

            ALittleGuessFunctor info = new ALittleGuessFunctor(class_setter_dec);
            info.const_modifier = PsiHelper.isConst(class_element_dec.getModifierList());

            // 第一个参数是类
            ALittleGuess class_guess = class_dec.guessType();
            info.param_list.add(class_guess);
            info.param_nullable_list.add(false);
            info.param_name_list.add(class_guess.getValue());

            ALittleMethodSetterParamDec param_dec = class_setter_dec.getMethodSetterParamDec();
            if (param_dec == null)
                throw new ALittleGuessException(myElement, "指向的setter函数没有定义参数");

            // 添加参数列表
            ALittleMethodParamOneDec one_dec = param_dec.getMethodParamOneDec();
            if (one_dec == null)
                throw new ALittleGuessException(myElement, "指向的setter函数没有定义参数");
            ALittleAllType all_type = one_dec.getAllType();
            if (all_type == null)
                throw new ALittleGuessException(myElement, "指向的setter函数没有定义参数类型");
            ALittleGuess all_type_guess = all_type.guessType();
            info.param_list.add(all_type_guess);
            info.param_nullable_list.add(PsiHelper.isNullable(one_dec.getModifierList()));
            if (one_dec.getMethodParamNameDec() != null) {
                info.param_name_list.add(one_dec.getMethodParamNameDec().getText());
            } else {
                info.param_name_list.add("");
            }

            info.updateValue();
            guess_list.add(info);
        } else if (parent instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec class_method_dec = (ALittleClassMethodDec) parent;
            ALittleClassElementDec class_element_dec = (ALittleClassElementDec) class_method_dec.getParent();
            PsiElement class_body_dec = class_element_dec.getParent();
            ALittleClassDec class_dec = (ALittleClassDec) class_body_dec.getParent();

            ALittleGuessFunctor info = new ALittleGuessFunctor(class_method_dec);
            List<ALittleModifier> modifier = class_element_dec.getModifierList();
            info.const_modifier = PsiHelper.isConst(class_element_dec.getModifierList());
            info.await_modifier = PsiHelper.getCoroutineType(modifier).equals("await");

            // 第一个参数是类
            ALittleGuess class_guess = class_dec.guessType();
            info.param_list.add(class_guess);
            info.param_nullable_list.add(false);
            info.param_name_list.add(class_guess.getValue());

            // 添加模板参数列表
            ALittleTemplateDec template_dec = class_method_dec.getTemplateDec();
            if (template_dec != null) {
                List<ALittleGuess> template_guess_list = template_dec.guessTypes();
                for (ALittleGuess guess : template_guess_list) {
                    if (!(guess instanceof ALittleGuessTemplate))
                        throw new ALittleGuessException(myElement, "template_dec.guessTypes()取到的不是ALittleGuessTemplate");
                    info.template_param_list.add((ALittleGuessTemplate) guess);
                }
            }

            // 添加参数列表
            ALittleMethodParamDec param_dec = class_method_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                for (int i = 0; i < one_dec_list.size(); ++i) {
                    ALittleMethodParamOneDec one_dec = one_dec_list.get(i);
                    ALittleAllType all_type = one_dec.getAllType();
                    ALittleMethodParamTailDec param_tail = one_dec.getMethodParamTailDec();
                    if (all_type != null) {
                        ALittleGuess all_type_guess = all_type.guessType();
                        info.param_list.add(all_type_guess);
                        info.param_nullable_list.add(PsiHelper.isNullable(one_dec.getModifierList()));
                        if (one_dec.getMethodParamNameDec() != null)
                            info.param_name_list.add(one_dec.getMethodParamNameDec().getText());
                        else
                            info.param_name_list.add("");
                    } else if (param_tail != null) {
                        if (i + 1 != one_dec_list.size())
                            throw new ALittleGuessException(one_dec, "参数占位符必须定义在最后");
                        info.param_tail = param_tail.guessType();
                    }
                }
            }

            // 添加返回值列表
            ALittleMethodReturnDec return_dec = class_method_dec.getMethodReturnDec();
            if (return_dec != null) {
                List<ALittleMethodReturnOneDec> one_dec_list = return_dec.getMethodReturnOneDecList();
                for (int i = 0; i < one_dec_list.size(); ++i) {
                    ALittleMethodReturnOneDec one_dec = one_dec_list.get(i);
                    ALittleAllType all_type = one_dec.getAllType();
                    ALittleMethodReturnTailDec return_tail = one_dec.getMethodReturnTailDec();
                    if (all_type != null) {
                        ALittleGuess all_type_guess = all_type.guessType();
                        info.return_list.add(all_type_guess);
                    } else if (return_tail != null) {
                        if (i + 1 != one_dec_list.size())
                            throw new ALittleGuessException(one_dec, "返回值占位符必须定义在最后");
                        info.return_tail = return_tail.guessType();
                    }
                }
            }
            info.updateValue();
            guess_list.add(info);
        } else if (parent instanceof ALittleClassStaticDec) {
            ALittleClassStaticDec class_static_dec = (ALittleClassStaticDec) parent;
            ALittleClassElementDec class_element_dec = (ALittleClassElementDec) class_static_dec.getParent();

            ALittleGuessFunctor info = new ALittleGuessFunctor(class_static_dec);
            info.await_modifier = PsiHelper.getCoroutineType(class_element_dec.getModifierList()).equals("await");

            // 添加模板参数列表
            ALittleTemplateDec template_dec = class_static_dec.getTemplateDec();
            if (template_dec != null) {
                List<ALittleGuess> template_guess_list = template_dec.guessTypes();
                for (ALittleGuess guess : template_guess_list) {
                    if (!(guess instanceof ALittleGuessTemplate))
                        throw new ALittleGuessException(myElement, "template_dec.guessTypes()取到的不是ALittleGuessTemplate");
                    info.template_param_list.add((ALittleGuessTemplate) guess);
                }
            }

            // 添加参数列表
            ALittleMethodParamDec param_dec = class_static_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                for (int i = 0; i < one_dec_list.size(); ++i) {
                    ALittleMethodParamOneDec one_dec = one_dec_list.get(i);
                    ALittleAllType all_type = one_dec.getAllType();
                    ALittleMethodParamTailDec param_tail = one_dec.getMethodParamTailDec();
                    if (all_type != null) {
                        ALittleGuess all_type_guess = all_type.guessType();
                        info.param_list.add(all_type_guess);
                        info.param_nullable_list.add(PsiHelper.isNullable(one_dec.getModifierList()));
                        if (one_dec.getMethodParamNameDec() != null)
                            info.param_name_list.add(one_dec.getMethodParamNameDec().getText());
                        else
                            info.param_name_list.add("");
                    } else if (param_tail != null) {
                        if (i + 1 != one_dec_list.size())
                            throw new ALittleGuessException(one_dec, "参数占位符必须定义在最后");
                        info.param_tail = param_tail.guessType();
                    }
                }
            }

            // 添加返回值列表
            ALittleMethodReturnDec return_dec = class_static_dec.getMethodReturnDec();
            if (return_dec != null) {
                List<ALittleMethodReturnOneDec> one_dec_list = return_dec.getMethodReturnOneDecList();
                for (int i = 0; i < one_dec_list.size(); ++i) {
                    ALittleMethodReturnOneDec one_dec = one_dec_list.get(i);
                    ALittleAllType all_type = one_dec.getAllType();
                    ALittleMethodReturnTailDec return_tail = one_dec.getMethodReturnTailDec();
                    if (all_type != null) {
                        ALittleGuess all_type_guess = all_type.guessType();
                        info.return_list.add(all_type_guess);
                    } else if (return_tail != null) {
                        if (i + 1 != one_dec_list.size())
                            throw new ALittleGuessException(one_dec, "返回值占位符必须定义在最后");
                        info.return_tail = return_tail.guessType();
                    }
                }
            }
            info.updateValue();
            guess_list.add(info);
        } else if (parent instanceof ALittleGlobalMethodDec) {
            ALittleGlobalMethodDec global_method_dec = (ALittleGlobalMethodDec) parent;
            ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec) global_method_dec.getParent();

            ALittleGuessFunctor info = new ALittleGuessFunctor(global_method_dec);
            info.await_modifier = PsiHelper.getCoroutineType(namespace_element_dec.getModifierList()).equals("await");

            String protocol_type = PsiHelper.getProtocolType(namespace_element_dec.getModifierList());
            if (protocol_type != null) {
                PsiElement error_element = global_method_dec.getMethodNameDec();
                if (error_element == null) error_element = global_method_dec;

                if (global_method_dec.getTemplateDec() != null)
                    throw new ALittleGuessException(error_element, "带" + info.proto + "不能定义函数模板");

                // 如果是带协议注解，那么一定是一个await
                info.await_modifier = true;
                info.proto = protocol_type;

                ALittleMethodParamDec param_dec = global_method_dec.getMethodParamDec();
                if (param_dec == null)
                    throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数必须是两个参数");
                List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                if (one_dec_list.size() != 2)
                    throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数必须是两个参数");
                if (PsiHelper.isNullable(one_dec_list.get(0).getModifierList()) || PsiHelper.isNullable(one_dec_list.get(1).getModifierList()))
                    throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数参数不能使用Nullable修饰");
                ALittleAllType all_type = one_dec_list.get(1).getAllType();
                if (all_type == null)
                    throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数，第二个参数没有定义类型");
                ALittleGuess guess = all_type.guessType();
                if (!(guess instanceof ALittleGuessStruct))
                    throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数第二个参数必须是struct");

                if (info.proto.equals("Http")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpSender", true);
                    if (!(element instanceof ALittleClassNameDec))
                        throw new ALittleGuessException(error_element, "语言框架中找不到ALittle.IHttpSender");
                    ALittleClassNameDec class_name_dec = (ALittleClassNameDec) element;
                    ALittleGuess class_name_dec_guess = class_name_dec.guessType();
                    info.param_list.add(class_name_dec_guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("sender");
                    info.param_list.add(guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("param");

                    ALittleMethodReturnDec return_dec = global_method_dec.getMethodReturnDec();
                    if (return_dec == null)
                        throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值必须是struct");
                    List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
                    if (return_one_list.size() != 1)
                        throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值有且仅有一个struct");
                    ALittleAllType return_one_all_type = return_one_list.get(0).getAllType();
                    if (return_one_all_type == null)
                        throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值有且仅有一个struct");
                    ALittleGuess return_guess = return_one_all_type.guessType();
                    if (!(return_guess instanceof ALittleGuessStruct))
                        throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值必须是struct");
                    info.return_list.add(ALittleGuessPrimitive.sStringGuess);
                    info.return_list.add(return_guess);
                } else if (info.proto.equals("HttpDownload")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpFileSender", true);
                    if (!(element instanceof ALittleClassNameDec))
                        throw new ALittleGuessException(error_element, "语言框架中找不到ALittle.IHttpFileSender");
                    ALittleClassNameDec class_name_dec = (ALittleClassNameDec) element;
                    ALittleGuess class_name_dec_guess = class_name_dec.guessType();
                    info.param_list.add(class_name_dec_guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("sender");
                    info.param_list.add(guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("param");

                    info.return_list.add(ALittleGuessPrimitive.sStringGuess);
                    ALittleGuess sender_guess = class_name_dec.guessType();
                    info.return_list.add(sender_guess);
                } else if (info.proto.equals("HttpUpload")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IHttpFileSender", true);
                    if (!(element instanceof ALittleClassNameDec))
                        throw new ALittleGuessException(error_element, "语言框架中找不到ALittle.IHttpFileSender");
                    ALittleClassNameDec class_name_dec = (ALittleClassNameDec) element;
                    ALittleGuess class_name_dec_guess = class_name_dec.guessType();
                    info.param_list.add(class_name_dec_guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("sender");
                    info.param_list.add(guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("param");

                    info.return_list.add(ALittleGuessPrimitive.sStringGuess);
                } else if (info.proto.equals("Msg")) {
                    PsiElement element = ALittleTreeChangeListener.findALittleNameDec(myElement.getProject(), PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), "ALittle", "IMsgCommon", true);
                    if (!(element instanceof ALittleClassNameDec))
                        throw new ALittleGuessException(error_element, "语言框架中找不到ALittle.IMsgCommon");
                    ALittleClassNameDec class_name_dec = (ALittleClassNameDec) element;
                    ALittleGuess class_name_dec_guess = class_name_dec.guessType();
                    info.param_list.add(class_name_dec_guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("sender");
                    info.param_list.add(guess);
                    info.param_nullable_list.add(false);
                    info.param_name_list.add("param");

                    ALittleMethodReturnDec return_dec = global_method_dec.getMethodReturnDec();
                    if (return_dec != null) {
                        List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
                        if (return_one_list.size() > 0) {
                            ALittleAllType return_one_all_type = return_one_list.get(0).getAllType();
                            if (return_one_all_type == null)
                                throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值必须是struct");
                            ALittleGuess return_guess = return_one_all_type.guessType();
                            if (!(return_guess instanceof ALittleGuessStruct))
                                throw new ALittleGuessException(error_element, "带" + info.proto + "注解的函数返回值必须是struct");
                            info.return_list.add(ALittleGuessPrimitive.sStringGuess);
                            info.return_list.add(return_guess);
                        }
                    }
                } else {
                    throw new ALittleGuessException(error_element, "未知的注解类型:" + info.proto);
                }
            } else {
                // 添加模板参数列表
                ALittleTemplateDec template_dec = global_method_dec.getTemplateDec();
                if (template_dec != null) {
                    List<ALittleGuess> template_guess_list = template_dec.guessTypes();
                    for (ALittleGuess guess : template_guess_list) {
                        if (!(guess instanceof ALittleGuessTemplate))
                            throw new ALittleGuessException(myElement, "template_dec.guessTypes()取到的不是ALittleGuessTemplate");
                        info.template_param_list.add((ALittleGuessTemplate) guess);
                    }
                }

                // 添加参数列表
                ALittleMethodParamDec param_dec = global_method_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                    for (int i = 0; i < one_dec_list.size(); ++i) {
                        ALittleMethodParamOneDec one_dec = one_dec_list.get(i);
                        ALittleAllType all_type = one_dec.getAllType();
                        ALittleMethodParamTailDec param_tail = one_dec.getMethodParamTailDec();
                        if (all_type != null) {
                            ALittleGuess all_type_guess = all_type.guessType();
                            info.param_list.add(all_type_guess);
                            info.param_nullable_list.add(PsiHelper.isNullable(one_dec.getModifierList()));
                            if (one_dec.getMethodParamNameDec() != null)
                                info.param_name_list.add(one_dec.getMethodParamNameDec().getText());
                            else
                                info.param_name_list.add("");
                        } else if (param_tail != null) {
                            if (i + 1 != one_dec_list.size())
                                throw new ALittleGuessException(one_dec, "参数占位符必须定义在最后");
                            info.param_tail = param_tail.guessType();
                        }
                    }
                }

                // 添加返回值列表
                ALittleMethodReturnDec return_dec = global_method_dec.getMethodReturnDec();
                if (return_dec != null) {
                    List<ALittleMethodReturnOneDec> one_dec_list = return_dec.getMethodReturnOneDecList();
                    for (int i = 0; i < one_dec_list.size(); ++i) {
                        ALittleMethodReturnOneDec one_dec = one_dec_list.get(i);
                        ALittleAllType all_type = one_dec.getAllType();
                        ALittleMethodReturnTailDec return_tail = one_dec.getMethodReturnTailDec();
                        if (all_type != null) {
                            ALittleGuess all_type_guess = all_type.guessType();
                            info.return_list.add(all_type_guess);
                        } else if (return_tail != null) {
                            if (i + 1 != one_dec_list.size())
                                throw new ALittleGuessException(one_dec, "返回值占位符必须定义在最后");
                            info.return_tail = return_tail.guessType();
                        }
                    }
                }
            }
            info.updateValue();
            guess_list.add(info);
        }

        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiElement method_dec = myElement.getParent();
        if (method_dec == null) return;
        PsiElement class_element_dec = method_dec.getParent();
        if (class_element_dec == null) return;
        PsiElement class_body = class_element_dec.getParent();
        if (class_body == null) return;
        if (!(class_body.getParent() instanceof ALittleClassDec)) return;
        ALittleClassDec class_dec = (ALittleClassDec) class_body.getParent();

        // 计算父类
        ALittleClassDec class_extends_dec = PsiHelper.findClassExtends(class_dec);
        if (class_extends_dec == null) return;

        PsiHelper.ClassAttrType attrType;
        if (method_dec instanceof ALittleClassMethodDec)
            attrType = PsiHelper.ClassAttrType.FUN;
        else if (method_dec instanceof ALittleClassStaticDec)
            attrType = PsiHelper.ClassAttrType.STATIC;
        else if (method_dec instanceof ALittleClassGetterDec)
            attrType = PsiHelper.ClassAttrType.GETTER;
        else if (method_dec instanceof ALittleClassSetterDec)
            attrType = PsiHelper.ClassAttrType.SETTER;
        else
            return;

        PsiElement result = PsiHelper.findFirstClassAttrFromExtends(class_extends_dec, attrType, mKey, 100);
        if (!(result instanceof ALittleMethodNameDec)) return;
        ALittleMethodNameDec method_name_dec = (ALittleMethodNameDec) result;

        ALittleGuess guess = myElement.guessType();
        ALittleGuess extends_guess = method_name_dec.guessType();
        try {
            ALittleReferenceOpUtil.guessTypeEqual(extends_guess, myElement, guess, false, false);
        } catch (ALittleGuessException error) {
            throw new ALittleGuessException(myElement, "该函数是从父类继承下来，但是定义不一致:" + extends_guess.getValue());
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
