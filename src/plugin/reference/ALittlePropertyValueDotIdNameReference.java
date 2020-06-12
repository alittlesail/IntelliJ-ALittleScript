package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittlePropertyValueDotIdNameReference extends ALittleReference<ALittlePropertyValueDotIdName> {
    private List<PsiElement> mGetterList;
    private List<PsiElement> mSetterList;
    private ALittleGuessClass mClassGuess;
    private PsiElement mMethodDec;
    private ALittleMethodBodyDec mMethodBodyDec;
    private ALittleGuess mPreType;

    public ALittlePropertyValueDotIdNameReference(@NotNull ALittlePropertyValueDotIdName element, TextRange textRange) {
        super(element, textRange);
    }

    private void reloadInfo() {
        mMethodDec = null;
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                break;
            } else if (parent instanceof ALittleClassDec) {
                break;
            } else if (parent instanceof ALittleClassCtorDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassCtorDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassSetterDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassSetterDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassGetterDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassGetterDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleClassMethodDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassMethodDec) parent).getMethodBodyDec();
                break;

            } else if (parent instanceof ALittleClassStaticDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleClassStaticDec) parent).getMethodBodyDec();
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                mMethodDec = parent;
                mMethodBodyDec = ((ALittleGlobalMethodDec) parent).getMethodBodyDec();
                break;
            }

            parent = parent.getParent();
        }
    }

    @NotNull
    private ALittleGuess replaceTemplate(@NotNull ALittleGuess guess) throws ALittleGuessException {
        if (mClassGuess == null) {
            return guess;
        }

        if (guess instanceof ALittleGuessTemplate && mClassGuess.template_map.size() > 0) {
            ALittleGuess guess_template = mClassGuess.template_map.get(guess.getValueWithoutConst());
            if (guess_template != null) {
                if (guess.is_const && !guess_template.is_const) {
                    guess_template = guess_template.clone();
                    guess_template.is_const = true;
                    guess_template.updateValue();
                }
                return guess_template;
            } else
                return guess;
        }

        if (guess instanceof ALittleGuessFunctor) {
            ALittleGuessFunctor guess_functor = (ALittleGuessFunctor) guess;
            ALittleGuessFunctor info = new ALittleGuessFunctor(guess_functor.element);
            info.await_modifier = guess_functor.await_modifier;
            info.const_modifier = guess_functor.const_modifier;
            info.proto = guess_functor.proto;
            info.template_param_list.addAll(guess_functor.template_param_list);
            info.param_tail = guess_functor.param_tail;
            info.param_name_list.addAll(guess_functor.param_name_list);
            info.return_tail = guess_functor.return_tail;

            int start_index = 0;
            if (guess_functor.element instanceof ALittleClassMethodDec
                    || guess_functor.element instanceof ALittleClassSetterDec
                    || guess_functor.element instanceof ALittleClassGetterDec) {
                info.param_list.add(mClassGuess);
                info.param_nullable_list.add(false);
                if (info.param_name_list.size() > 0)
                    info.param_name_list.set(0, mClassGuess.getValue());
                start_index = 1;
            }
            for (int i = start_index; i < guess_functor.param_list.size(); ++i) {
                ALittleGuess guess_info = replaceTemplate(guess_functor.param_list.get(i));
                info.param_list.add(guess_info);
            }
            for (int i = start_index; i < guess_functor.param_nullable_list.size(); ++i) {
                info.param_nullable_list.add(guess_functor.param_nullable_list.get(i));
            }
            for (int i = 0; i < guess_functor.return_list.size(); ++i) {
                ALittleGuess guess_info = replaceTemplate(guess_functor.return_list.get(i));
                info.return_list.add(guess_info);
            }
            info.updateValue();
            return info;
        }

        if (guess instanceof ALittleGuessList) {
            ALittleGuessList guess_list = (ALittleGuessList) guess;
            ALittleGuess sub_info = replaceTemplate(guess_list.sub_type);
            ALittleGuessList info = new ALittleGuessList(sub_info, guess_list.is_const, guess_list.is_native);
            info.updateValue();
            return info;
        }

        if (guess instanceof ALittleGuessMap) {
            ALittleGuessMap guess_map = (ALittleGuessMap) guess;
            ALittleGuess key_info = replaceTemplate(guess_map.key_type);
            ALittleGuess value_info = replaceTemplate(guess_map.value_type);

            ALittleGuessMap info = new ALittleGuessMap(key_info, value_info, guess.is_const);
            info.updateValue();
            return info;
        }

        if (guess instanceof ALittleGuessClass) {
            ALittleGuessClass guess_class = (ALittleGuessClass) guess;
            ALittleGuessClass info = new ALittleGuessClass(guess_class.namespace_name,
                    guess_class.class_name, guess_class.class_dec, guess_class.using_name, guess_class.is_const, guess_class.is_native);
            info.template_list.addAll(guess_class.template_list);
            for (Map.Entry<String, ALittleGuess> pair : guess_class.template_map.entrySet()) {
                ALittleGuess replace_guess = replaceTemplate(pair.getValue());
                info.template_map.put(pair.getKey(), replace_guess);
            }

            ALittleClassDec src_class_dec = guess_class.class_dec;
            ALittleClassNameDec src_class_name_dec = src_class_dec.getClassNameDec();
            if (src_class_name_dec == null)
                throw new ALittleGuessException(myElement, "类模板没有定义类名");
            info.updateValue();
            return info;
        }

        return guess;
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        mGetterList = null;
        mSetterList = null;
        mClassGuess = null;

        ResolveResult[] result_list = multiResolve(true);
        for (ResolveResult resolve : result_list) {
            PsiElement result = resolve.getElement();
            ALittleGuess guess = null;
            if (result instanceof ALittleClassVarDec) {
                guess = ((ALittleClassVarDec) result).guessType();

                if (mClassGuess != null && guess instanceof ALittleGuessTemplate) {
                    ALittleGuess guess_template = mClassGuess.template_map.get(guess.getValueWithoutConst());
                    if (guess_template == null) {
                        for (int i = 0; i < mClassGuess.template_list.size(); ++i) {
                            guess_template = mClassGuess.template_list.get(i);

                            if (guess_template.getValueWithoutConst().equals(guess.getValueWithoutConst()))
                                break;
                        }
                    }

                    if (guess_template != null) {
                        if (guess.is_const && !guess_template.is_const) {
                            guess_template = guess_template.clone();
                            guess_template.is_const = true;
                            guess_template.updateValue();
                        }

                        guess = guess_template;
                    }
                }
            } else if (result instanceof ALittleStructVarDec) {
                guess = ((ALittleStructVarDec) result).guessType();
            } else if (result instanceof ALittleEnumVarDec) {
                guess = ((ALittleEnumVarDec) result).guessType();
            } else if (result instanceof ALittleMethodNameDec) {
                guess = ((ALittleMethodNameDec) result).guessType();

                // 如果前一个数据是const，那么调用的函数也必须是const
                if (mPreType != null && mPreType.is_const) {
                    if (guess instanceof ALittleGuessFunctor && !((ALittleGuessFunctor) guess).const_modifier)
                        throw new ALittleGuessException(myElement, "请使用带Const修饰的函数");
                }

                if (result.getParent() instanceof ALittleClassGetterDec) {
                    if (mGetterList != null && mGetterList.contains(result) && guess instanceof ALittleGuessFunctor) {
                        guess = ((ALittleGuessFunctor) guess).return_list.get(0);
                    }
                } else if (result.getParent() instanceof ALittleClassSetterDec) {
                    if (mSetterList != null && mSetterList.contains(result) && guess instanceof ALittleGuessFunctor) {
                        guess = ((ALittleGuessFunctor) guess).param_list.get(1);
                    }
                }
                guess = replaceTemplate(guess);

            } else if (result instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) result).guessType();
            } else if (result instanceof ALittleEnumNameDec) {
                ALittleGuess enum_guess = ((ALittleEnumNameDec) result).guessType();
                if (!(enum_guess instanceof ALittleGuessEnum))
                    throw new ALittleGuessException(myElement, "ALittleEnumNameDec.guessType的结果不是ALittleGuessEnum");
                ALittleGuessEnum enum_guess_enum = (ALittleGuessEnum) enum_guess;
                ALittleGuessEnumName info = new ALittleGuessEnumName(enum_guess_enum.namespace_name, enum_guess_enum.enum_name, (ALittleEnumNameDec) result);
                info.updateValue();
                guess = info;
            } else if (result instanceof ALittleStructNameDec) {
                ALittleGuess struct_guess = ((ALittleStructNameDec) result).guessType();
                if (!(struct_guess instanceof ALittleGuessStruct))
                    throw new ALittleGuessException(myElement, "ALittleStructNameDec.guessType的结果不是ALittleGuessStruct");
                ALittleGuessStruct struct_guess_struct = (ALittleGuessStruct) struct_guess;
                ALittleGuessStructName info = new ALittleGuessStructName(struct_guess_struct.namespace_name, struct_guess_struct.struct_name, (ALittleStructNameDec) result);
                info.updateValue();
                guess = info;
            } else if (result instanceof ALittleClassNameDec) {
                ALittleGuess class_guess = ((ALittleClassNameDec) result).guessType();
                if (!(class_guess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "ALittleClassNameDec.guessType的结果不是ALittleGuessClass");
                ALittleGuessClass class_guess_class = (ALittleGuessClass) class_guess;
                if (class_guess_class.template_list.size() > 0)
                    throw new ALittleGuessException(myElement, "模板类" + class_guess_class.getValue() + "不能直接使用");
                ALittleGuessClassName info = new ALittleGuessClassName(class_guess_class.namespace_name, class_guess_class.class_name, (ALittleClassNameDec) result);
                info.updateValue();
                guess = info;
            }

            if (guess != null) {
                if (mPreType != null && mPreType.is_const && !guess.is_const) {
                    if (guess instanceof ALittleGuessPrimitive) {
                        String value = guess.getValue();
                        guess = ALittleGuessPrimitive.sPrimitiveGuessMap.get("const " + value);
                        if (guess == null) throw new ALittleGuessException(myElement, "找不到const " + value);
                    } else {
                        guess = guess.clone();
                        guess.is_const = true;
                        guess.updateValue();
                    }
                }
                guess_list.add(guess);
            }
        }

        mGetterList = null;
        mSetterList = null;
        mClassGuess = null;

        return guess_list;
    }

    @Override
    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        PsiElement element = myElement;
        if (element == null) return;

        PsiElement resolve = resolve();
        if (resolve instanceof ALittleMethodParamNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
            return;
        }

        if (resolve instanceof ALittleVarAssignNameDec) {
            PsiElement parent = resolve.getParent();
            if (parent instanceof ALittleForPairDec) {
                Annotation anno = holder.createInfoAnnotation(element, null);
                anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
            }
            return;
        }

        if (resolve instanceof ALittleMethodNameDec && resolve.getParent() instanceof ALittleClassStaticDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
            return;
        }
    }

    @Override
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
        mPreType = null;
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<ResolveResult> result_list = new ArrayList<>();
        if (mKey.length() == 0) return new ResolveResult[0];

        try {
            // 获取父节点
            ALittlePropertyValueDotId property_value_dot_id = (ALittlePropertyValueDotId) myElement.getParent();
            ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix) property_value_dot_id.getParent();
            ALittlePropertyValue property_value = (ALittlePropertyValue) property_value_suffix.getParent();
            ALittlePropertyValueFirstType property_value_first_type = property_value.getPropertyValueFirstType();
            List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();

            // 获取所在位置
            int index = suffix_list.indexOf(property_value_suffix);
            if (index == -1) return new ResolveResult[0];

            if (index == 0)
                mPreType = property_value_first_type.guessType();
            else
                mPreType = suffix_list.get(index - 1).guessType();

            // 判断当前后缀是否是最后一个后缀
            ALittlePropertyValueSuffix next_suffix = null;
            if (index + 1 < suffix_list.size())
                next_suffix = suffix_list.get(index + 1);

            boolean is_const = false;
            if (mPreType != null) is_const = mPreType.is_const;

            if (mPreType instanceof ALittleGuessTemplate) {
                mPreType = ((ALittleGuessTemplate) mPreType).template_extends;
                if (mPreType != null && is_const && !mPreType.is_const) {
                    mPreType = mPreType.clone();
                    mPreType.is_const = true;
                    mPreType.updateValue();
                }
            }

            if (mPreType == null) return new ResolveResult[0];

            // 处理类的实例对象
            if (mPreType instanceof ALittleGuessClass) {
                mClassGuess = (ALittleGuessClass) mPreType;
                ALittleClassDec class_dec = mClassGuess.class_dec;

                // 计算当前元素对这个类的访问权限
                int access_level = PsiHelper.calcAccessLevelByTargetClassDecForElement(myElement, class_dec);

                // 所有成员变量
                List<PsiElement> class_var_dec_list = new ArrayList<>();
                PsiHelper.findClassAttrList(class_dec,
                        access_level, PsiHelper.ClassAttrType.VAR, mKey, class_var_dec_list, 100);
                for (PsiElement class_var_dec : class_var_dec_list)
                    result_list.add(new PsiElementResolveResult(class_var_dec));

                List<PsiElement> class_method_name_dec_list = new ArrayList<>();
                // 在当前情况下，只有当前property_value在等号的左边，并且是最后一个属性才是setter，否则都是getter
                if (next_suffix == null && property_value.getParent() instanceof ALittleOpAssignExpr) {
                    mSetterList = new ArrayList<>();
                    PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.SETTER, mKey, mSetterList, 100);
                    class_method_name_dec_list.addAll(mSetterList);
                } else {
                    mGetterList = new ArrayList<>();
                    PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.GETTER, mKey, mGetterList, 100);
                    class_method_name_dec_list.addAll(mGetterList);
                }
                // 所有成员函数
                PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.FUN, mKey, class_method_name_dec_list, 100);
                // 添加函数名元素
                class_method_name_dec_list = PsiHelper.filterSameName(class_method_name_dec_list);
                for (PsiElement class_method_name_dec : class_method_name_dec_list)
                    result_list.add(new PsiElementResolveResult(class_method_name_dec));
                // 处理结构体的实例对象
            } else if (mPreType instanceof ALittleGuessStruct) {
                ALittleStructDec struct_dec = ((ALittleGuessStruct) mPreType).struct_dec;
                List<ALittleStructVarDec> struct_var_dec_list = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findStructVarDecList(struct_dec, mKey, struct_var_dec_list, 100);
                for (ALittleStructVarDec struct_var_dec : struct_var_dec_list)
                    result_list.add(new PsiElementResolveResult(struct_var_dec));
                // 比如 ALittleName.XXX
            } else if (mPreType instanceof ALittleGuessNamespaceName) {
                ALittleNamespaceNameDec namespace_name_dec = ((ALittleGuessNamespaceName) mPreType).namespace_name_dec;
                String namespace_name = namespace_name_dec.getText();
                // 所有枚举名
                List<PsiElement> enum_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                        , PsiHelper.PsiElementType.ENUM_NAME, myElement.getContainingFile().getOriginalFile(), namespace_name, mKey, true);
                for (PsiElement enum_name_dec : enum_name_dec_list)
                    result_list.add(new PsiElementResolveResult(enum_name_dec));
                // 所有全局函数
                List<PsiElement> method_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                        , PsiHelper.PsiElementType.GLOBAL_METHOD, myElement.getContainingFile().getOriginalFile(), namespace_name, mKey, true);
                for (PsiElement method_name_dec : method_name_dec_list)
                    result_list.add(new PsiElementResolveResult(method_name_dec));
                // 所有类名
                List<PsiElement> class_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                        , PsiHelper.PsiElementType.CLASS_NAME, myElement.getContainingFile().getOriginalFile(), namespace_name, mKey, true);
                for (PsiElement class_name_dec : class_name_dec_list)
                    result_list.add(new PsiElementResolveResult(class_name_dec));
                // 所有结构体名
                List<PsiElement> struct_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                        , PsiHelper.PsiElementType.STRUCT_NAME, myElement.getContainingFile().getOriginalFile(), namespace_name, mKey, true);
                for (PsiElement struct_name_dec : struct_name_dec_list)
                    result_list.add(new PsiElementResolveResult(struct_name_dec));
                // 所有单例
                List<PsiElement> instance_name_dec_list = ALittleTreeChangeListener.findALittleNameDecList(myElement.getProject()
                        , PsiHelper.PsiElementType.INSTANCE_NAME, myElement.getContainingFile().getOriginalFile(), namespace_name, mKey, false);
                for (PsiElement instance_name_dec : instance_name_dec_list)
                    result_list.add(new PsiElementResolveResult(instance_name_dec));
                // 比如 AClassName.XXX
            } else if (mPreType instanceof ALittleGuessClassName) {
                ALittleClassNameDec class_name_dec = ((ALittleGuessClassName) mPreType).class_name_dec;
                ALittleClassDec class_dec = (ALittleClassDec) class_name_dec.getParent();

                // 计算当前元素对这个类的访问权限
                int access_level = PsiHelper.calcAccessLevelByTargetClassDecForElement(myElement, class_dec);

                // 所有静态函数
                List<PsiElement> class_method_name_dec_list = new ArrayList<>();
                PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.STATIC, mKey, class_method_name_dec_list, 100);

                // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
                if (next_suffix != null) {
                    ALittlePropertyValueMethodCall method_call_stat = next_suffix.getPropertyValueMethodCall();
                    if (method_call_stat != null) {
                        int paramCount = method_call_stat.getValueStatList().size();
                        if (paramCount == 1) {
                            // 所有getter
                            PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.GETTER, mKey, class_method_name_dec_list, 100);
                        } else if (paramCount == 2) {
                            // 所有setter
                            PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.SETTER, mKey, class_method_name_dec_list, 100);
                        }
                    }
                }

                // 所有成员函数
                PsiHelper.findClassAttrList(class_dec, access_level, PsiHelper.ClassAttrType.FUN, mKey, class_method_name_dec_list, 100);
                class_method_name_dec_list = PsiHelper.filterSameName(class_method_name_dec_list);
                for (PsiElement class_method_name_dec : class_method_name_dec_list)
                    result_list.add(new PsiElementResolveResult(class_method_name_dec));
                // 比如 AEnumName.XXX
            } else if (mPreType instanceof ALittleGuessEnumName) {
                // 所有枚举字段
                ALittleEnumNameDec enum_name_dec = ((ALittleGuessEnumName) mPreType).enum_name_dec;
                ALittleEnumDec enum_dec = (ALittleEnumDec) enum_name_dec.getParent();
                List<ALittleEnumVarDec> var_dec_list = new ArrayList<>();
                PsiHelper.findEnumVarDecList(enum_dec, mKey, var_dec_list);
                for (ALittleEnumVarDec var_name_dec : var_dec_list)
                    result_list.add(new PsiElementResolveResult(var_name_dec));
            }
        } catch (ALittleGuessException ignored) {

        }
        return result_list.toArray(new ResolveResult[result_list.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile().getOriginalFile();
        List<LookupElement> variants = new ArrayList<>();
        try {
            // 获取父节点
            ALittlePropertyValueDotId propertyValueDotId = (ALittlePropertyValueDotId) myElement.getParent();
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix) propertyValueDotId.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue) propertyValueSuffix.getParent();
            ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

            // 获取所在位置
            int index = suffixList.indexOf(propertyValueSuffix);
            if (index == -1) return variants.toArray();

            // 获取前一个类型
            ALittleGuess preType;
            if (index == 0) {
                preType = propertyValueFirstType.guessType();
            } else {
                preType = suffixList.get(index - 1).guessType();
            }

            if (preType instanceof ALittleGuessClassTemplate) {
                preType = ((ALittleGuessClassTemplate) preType).template_extends;
            }

            if (preType == null) return variants.toArray();

            // 处理类的实例对象
            if (preType instanceof ALittleGuessClass) {
                ALittleClassDec classDec = (ALittleClassDec) preType.getElement();

                // 计算当前元素对这个类的访问权限
                int accessLevel = PsiHelper.calcAccessLevelByTargetClassDecForElement(myElement, classDec);

                List<PsiElement> classVarDecList = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.VAR, "", classVarDecList, 100);
                for (PsiElement classVarDec : classVarDecList) {
                    PsiElement nameDec = ((ALittleClassVarDec) classVarDec).getClassVarNameDec();
                    if (nameDec == null) continue;
                    variants.add(LookupElementBuilder.create(nameDec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(nameDec.getContainingFile().getName())
                    );
                }

                // 所有setter
                List<PsiElement> classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.SETTER, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.GETTER, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.FUN, "", classMethodNameDecList, 10);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 处理结构体的实例对象
            } else if (preType instanceof ALittleGuessStruct) {
                ALittleStructDec structDec = (ALittleStructDec) preType.getElement();
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findStructVarDecList(structDec, "", structVarDecList, 100);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    PsiElement nameDec = structVarDec.getStructVarNameDec();
                    if (nameDec != null) {
                        variants.add(LookupElementBuilder.create(nameDec.getText()).
                                withIcon(ALittleIcons.PROPERTY).
                                withTypeText(structVarDec.getContainingFile().getName())
                        );
                    }
                }
                // 比如 ALittleName.XXX
            } else if (preType instanceof ALittleGuessNamespaceName) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec) preType.getElement();
                String namespaceName = namespaceNameDec.getText();
                // 所有枚举名
                List<PsiElement> enumNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.ENUM_NAME, psiFile, namespaceName, "", true);
                for (PsiElement enumNameDec : enumNameDecList) {
                    variants.add(LookupElementBuilder.create(enumNameDec.getText()).
                            withIcon(ALittleIcons.ENUM).
                            withTypeText(enumNameDec.getContainingFile().getName())
                    );
                }
                // 所有全局函数
                List<PsiElement> methodNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.GLOBAL_METHOD, psiFile, namespaceName, "", true);
                for (PsiElement methodNameDec : methodNameDecList) {
                    variants.add(LookupElementBuilder.create(methodNameDec.getText()).
                            withIcon(ALittleIcons.GLOBAL_METHOD).
                            withTypeText(methodNameDec.getContainingFile().getName())
                    );
                }
                // 所有类名
                List<PsiElement> classNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.CLASS_NAME, psiFile, namespaceName, "", true);
                for (PsiElement classNameDec : classNameDecList) {
                    variants.add(LookupElementBuilder.create(classNameDec.getText()).
                            withIcon(ALittleIcons.CLASS).
                            withTypeText(classNameDec.getContainingFile().getName())
                    );
                }
                // 所有结构体名
                List<PsiElement> structNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.STRUCT_NAME, psiFile, namespaceName, "", true);
                for (PsiElement structNameDec : structNameDecList) {
                    variants.add(LookupElementBuilder.create(structNameDec.getText()).
                            withIcon(ALittleIcons.STRUCT).
                            withTypeText(structNameDec.getContainingFile().getName())
                    );
                }
                // 所有单例
                List<PsiElement> instanceNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.INSTANCE_NAME, psiFile, namespaceName, "", false);
                for (PsiElement instanceNameDec : instanceNameDecList) {
                    variants.add(LookupElementBuilder.create(instanceNameDec.getText()).
                            withIcon(ALittleIcons.INSTANCE).
                            withTypeText(instanceNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AClassName.XXX
            } else if (preType instanceof ALittleGuessClassName) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec) preType.getElement();
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();

                // 计算当前元素对这个类的访问权限
                int accessLevel = PsiHelper.calcAccessLevelByTargetClassDecForElement(myElement, classDec);

                // 所有静态函数
                List<PsiElement> classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.STATIC, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.STATIC_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.FUN, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有setter
                classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.SETTER, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.GETTER, "", classMethodNameDecList, 100);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AEnum.XXX
            } else if (preType instanceof ALittleGuessEnumName) {
                // 所有枚举字段
                ALittleEnumNameDec enumNameDec = (ALittleEnumNameDec) preType.getElement();
                ALittleEnumDec enumDec = (ALittleEnumDec) enumNameDec.getParent();
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                PsiHelper.findEnumVarDecList(enumDec, "", varDecList);
                for (ALittleEnumVarDec varNameDec : varDecList) {
                    variants.add(LookupElementBuilder.create(varNameDec.getEnumVarNameDec().getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(varNameDec.getContainingFile().getName())
                    );
                }
            }
        } catch (ALittleGuessException ignored) {

        }

        return variants.toArray();
    }
}
