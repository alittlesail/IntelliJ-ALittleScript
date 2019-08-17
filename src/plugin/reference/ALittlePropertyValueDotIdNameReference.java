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

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdNameReference extends ALittleReference {
    private List<ALittleMethodNameDec> m_getter_list;
    private List<ALittleMethodNameDec> m_setter_list;

    public ALittlePropertyValueDotIdNameReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        m_getter_list = null;
        m_setter_list = null;

        List<PsiElement> guess_list = new ArrayList<>();

        ResolveResult[] result_list = multiResolve(false);
        for (ResolveResult result : result_list) {
            PsiElement element = result.getElement();

            PsiElement guess = null;
            if (element instanceof ALittleClassVarNameDec) {
                guess = ((ALittleClassVarNameDec) element).guessType();
            } else if (element instanceof ALittleStructVarNameDec) {
                guess = ((ALittleStructVarNameDec) element).guessType();
            } else if (element instanceof ALittleMethodNameDec) {
                // 处理getter的情况
                boolean is_getter = false;
                if (m_getter_list != null) {
                    for (int i = 0; i < m_getter_list.size(); ++i) {
                        if (element.equals(m_getter_list.get(i))) {
                            is_getter = true;
                            break;
                        }
                    }
                }
                // 处理setter的情况
                boolean is_setter = false;
                if (m_setter_list != null) {
                    for (int i = 0; i < m_setter_list.size(); ++i) {
                        if (element.equals(m_setter_list.get(i))) {
                            is_setter = true;
                            break;
                        }
                    }
                }
                if (is_getter) {
                    ALittleMethodNameDec dec = (ALittleMethodNameDec)element;
                    PsiReference ref = dec.getReference();
                    if (ref instanceof ALittleMethodNameDecReference) {
                        ALittleMethodNameDecReference reference = (ALittleMethodNameDecReference)ref;
                        guess = reference.guessTypeForGetter();
                    }
                } else if (is_setter) {
                    ALittleMethodNameDec dec = (ALittleMethodNameDec)element;
                    PsiReference ref = dec.getReference();
                    if (ref instanceof ALittleMethodNameDecReference) {
                        ALittleMethodNameDecReference reference = (ALittleMethodNameDecReference)ref;
                        guess = reference.guessTypeForSetter();
                    }
                } else {
                    guess = element;
                }
            } else if (element instanceof ALittleEnumNameDec) {
                guess = ((ALittleEnumNameDec) element).guessType();
            } else if (element instanceof ALittleInstanceNameDec) {
                guess = ((ALittleInstanceNameDec) element).guessType();
            } else if (element instanceof ALittleEnumVarNameDec) {
                guess = element;
            } else if (element instanceof ALittleClassNameDec) {
                guess = element;
            }

            if (guess != null) guess_list.add(guess);
        }

        m_getter_list = null;
        m_setter_list = null;
        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        ALittlePropertyValueDotId propertyValue_dotId = (ALittlePropertyValueDotId)myElement.getParent();
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)propertyValue_dotId.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();

        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
        PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueThisType();
        }
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueCastType();
        }

        // 判断当前后缀是否是最后一个后缀
        boolean is_last_value = false;
        ALittlePropertyValueSuffix next_suffix = null;
        for (int index = 0; index < suffixList.size(); ++index) {
            ALittlePropertyValueSuffix suffix = suffixList.get(index);

            if (suffix.equals(propertyValueSuffix)) {
                is_last_value = (index == suffixList.size() - 1);
                if (!is_last_value) next_suffix = suffixList.get(index + 1);
                break;
            }
            ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
            if (dotId != null) prePropertyValue = dotId;
            ALittlePropertyValueBrackValueStat brackValue = suffix.getPropertyValueBrackValueStat();
            if (brackValue != null) prePropertyValue = brackValue;
            ALittlePropertyValueMethodCallStat methodCall = suffix.getPropertyValueMethodCallStat();
            if (methodCall != null) prePropertyValue = methodCall;
        }

        if (prePropertyValue == null) {
            return results.toArray(new ResolveResult[results.size()]);
        }

        PsiElement pre_type = null;
        if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            pre_type = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dotId_name = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName();
            pre_type = dotId_name.guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
        }
        if (pre_type == null) {
            return results.toArray(new ResolveResult[results.size()]);
        }

        // 处理类的实例对象
        if (pre_type instanceof ALittleClassDec) {
            ALittleClassDec classDec = (ALittleClassDec) pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
            List<ALittleClassVarNameDec> class_var_name_decList = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, classDec, mKey, class_var_name_decList, 10);
            for (ALittleClassVarNameDec class_var_name_dec : class_var_name_decList) {
                results.add(new PsiElementResolveResult(class_var_name_dec));
            }

            List<ALittleMethodNameDec> class_method_name_decList = new ArrayList<>();

            // 在当前情况下，只有当前propertyvalue在等号的左边，并且是最后一个属性才是setter，否则都是getter
            PsiElement parent = propertyValue.getParent();
            if (is_last_value && parent instanceof ALittleOpAssignExpr) {
                // 所有setter
                m_setter_list = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, mKey, m_setter_list, 10);
                class_method_name_decList.addAll(m_setter_list);
            } else {
                // 所有getter
                m_getter_list = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, mKey, m_getter_list, 10);
                class_method_name_decList.addAll(m_getter_list);
            }
            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, mKey, class_method_name_decList, 10);
            // 过滤掉重复的函数名
            class_method_name_decList = ALittleUtil.filterSameMethodName(class_method_name_decList);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                results.add(new PsiElementResolveResult(class_method_name_dec));
            }
            // 处理结构体的实例对象
        } else if (pre_type instanceof ALittleStructDec) {
            ALittleStructDec struct_dec = (ALittleStructDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) struct_dec.getContainingFile());
            List<ALittleStructVarNameDec> struct_var_name_decList = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, struct_dec, mKey, struct_var_name_decList, 10);
            for (ALittleStructVarNameDec struct_var_name_dec : struct_var_name_decList) {
                results.add(new PsiElementResolveResult(struct_var_name_dec));
            }
            // 比如 ALittle.AEnum
        } else if (pre_type instanceof ALittleNamespaceNameDec) {
            ALittleNamespaceNameDec namespace_name_dec = (ALittleNamespaceNameDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) namespace_name_dec.getContainingFile());
            // 所有枚举名
            List<ALittleEnumNameDec> enum_name_decList = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), src_namespace, mKey);
            for (ALittleEnumNameDec enum_name_dec : enum_name_decList) {
                results.add(new PsiElementResolveResult(enum_name_dec));
            }
            // 所有全局函数
            List<ALittleMethodNameDec> method_name_decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), src_namespace, mKey);
            for (ALittleMethodNameDec method_name_dec : method_name_decList) {
                results.add(new PsiElementResolveResult(method_name_dec));
            }
            // 所有类名
            List<ALittleClassNameDec> class_name_decList = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), src_namespace, mKey);
            for (ALittleClassNameDec class_name_dec : class_name_decList) {
                results.add(new PsiElementResolveResult(class_name_dec));
            }
            // 所有结构体名
            List<ALittleStructNameDec> struct_name_decList = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), src_namespace, mKey);
            for (ALittleStructNameDec struct_name_dec : struct_name_decList) {
                results.add(new PsiElementResolveResult(struct_name_dec));
            }
            // 所有单例
            List<ALittleInstanceNameDec> instance_name_decList = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), src_namespace, mKey, false);
            for (ALittleInstanceNameDec instance_name_dec : instance_name_decList) {
                results.add(new PsiElementResolveResult(instance_name_dec));
            }
            // 比如 AClass.StaticMethod()
        } else if (pre_type instanceof ALittleClassNameDec) {
            ALittleClassNameDec class_name_dec = (ALittleClassNameDec)pre_type;
            ALittleClassDec classDec = (ALittleClassDec)class_name_dec.getParent();
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_name_dec.getContainingFile());
            // 所有静态函数
            List<ALittleMethodNameDec> class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, classDec, mKey, class_method_name_decList, 10);

            // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
            if (next_suffix != null) {
                ALittlePropertyValueMethodCallStat methodCall_stat = next_suffix.getPropertyValueMethodCallStat();
                if (methodCall_stat != null) {
                    int param_count = methodCall_stat.getValueStatList().size();
                    if (param_count == 1) {
                        // 所有getter
                        ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, mKey, class_method_name_decList, 10);
                    } else if (param_count == 2) {
                        // 所有setter
                        ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, mKey, class_method_name_decList, 10);
                    }
                }
            }

            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, mKey, class_method_name_decList, 10);
            // 过滤掉重复的函数名
            class_method_name_decList = ALittleUtil.filterSameMethodName(class_method_name_decList);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                results.add(new PsiElementResolveResult(class_method_name_dec));
            }
            // 比如 AEnum.Var
        } else if (pre_type instanceof ALittleEnumDec) {
            // 所有枚举字段
            ALittleEnumDec enum_dec = (ALittleEnumDec)pre_type;
            List<ALittleEnumVarNameDec> var_name_decList = new ArrayList<>();
            ALittleUtil.findEnumVarNameDecList(enum_dec, mKey, var_name_decList);
            for (ALittleEnumVarNameDec var_name_dec : var_name_decList) {
                results.add(new PsiElementResolveResult(var_name_dec));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();

        ALittlePropertyValueDotId propertyValue_dotId = (ALittlePropertyValueDotId)myElement.getParent();
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)propertyValue_dotId.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();

        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
        PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueThisType();
        }
        if (prePropertyValue == null) {
            prePropertyValue = propertyValue.getPropertyValueCastType();
        }

        for (ALittlePropertyValueSuffix suffix : suffixList) {
            if (suffix.equals(propertyValueSuffix)) {
                break;
            }
            ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
            if (dotId != null) prePropertyValue = dotId;
            ALittlePropertyValueBrackValueStat brackValue = suffix.getPropertyValueBrackValueStat();
            if (brackValue != null) prePropertyValue = brackValue;
            ALittlePropertyValueMethodCallStat methodCall = suffix.getPropertyValueMethodCallStat();
            if (methodCall != null) prePropertyValue = methodCall;
        }

        if (prePropertyValue == null) {
            return variants.toArray();
        }

        PsiElement pre_type = null;
        if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            pre_type = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
            pre_type = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dotId_name = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName();
            pre_type = dotId_name.guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
        } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
        }
        if (pre_type == null) {
            return variants.toArray();
        }

        // 处理类的实例对象
        if (pre_type instanceof ALittleClassDec) {
            ALittleClassDec classDec = (ALittleClassDec) pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
            List<ALittleClassVarNameDec> class_var_name_decList = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, classDec, "", class_var_name_decList, 10);
            for (ALittleClassVarNameDec class_var_name_dec : class_var_name_decList) {
                variants.add(LookupElementBuilder.create(class_var_name_dec.getText()).
                        withIcon(ALittleIcons.PROPERTY).
                        withTypeText(class_var_name_dec.getContainingFile().getName())
                );
            }

            // 所有setter
            List<ALittleMethodNameDec> class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.SETTER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }

            // 所有getter
            class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.GETTER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }

            // 所有成员函数
            class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.MEMBER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }
            // 处理结构体的实例对象
        } else if (pre_type instanceof ALittleStructDec) {
            ALittleStructDec struct_dec = (ALittleStructDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) struct_dec.getContainingFile());
            List<ALittleStructVarNameDec> struct_var_name_decList = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, struct_dec, "", struct_var_name_decList, 10);
            for (ALittleStructVarNameDec struct_var_name_dec : struct_var_name_decList) {
                variants.add(LookupElementBuilder.create(struct_var_name_dec.getText()).
                        withIcon(ALittleIcons.PROPERTY).
                        withTypeText(struct_var_name_dec.getContainingFile().getName())
                );
            }
            // 比如 ALittle.AEnum
        } else if (pre_type instanceof ALittleNamespaceNameDec) {
            ALittleNamespaceNameDec namespace_name_dec = (ALittleNamespaceNameDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) namespace_name_dec.getContainingFile());
            // 所有枚举名
            List<ALittleEnumNameDec> enum_name_decList = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleEnumNameDec enum_name_dec : enum_name_decList) {
                variants.add(LookupElementBuilder.create(enum_name_dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(enum_name_dec.getContainingFile().getName())
                );
            }
            // 所有全局函数
            List<ALittleMethodNameDec> method_name_decList = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleMethodNameDec method_name_dec : method_name_decList) {
                variants.add(LookupElementBuilder.create(method_name_dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(method_name_dec.getContainingFile().getName())
                );
            }
            // 所有类名
            List<ALittleClassNameDec> class_name_decList = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleClassNameDec class_name_dec : class_name_decList) {
                variants.add(LookupElementBuilder.create(class_name_dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(class_name_dec.getContainingFile().getName())
                );
            }
            // 所有结构体名
            List<ALittleStructNameDec> struct_name_decList = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleStructNameDec struct_name_dec : struct_name_decList) {
                variants.add(LookupElementBuilder.create(struct_name_dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(struct_name_dec.getContainingFile().getName())
                );
            }
            // 所有单例
            List<ALittleInstanceNameDec> instance_name_decList = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), src_namespace, "", false);
            for (ALittleInstanceNameDec instance_name_dec : instance_name_decList) {
                variants.add(LookupElementBuilder.create(instance_name_dec.getText()).
                        withIcon(ALittleIcons.INSTANCE).
                        withTypeText(instance_name_dec.getContainingFile().getName())
                );
            }
            // 比如 AClass.StaticMethod()
        } else if (pre_type instanceof ALittleClassNameDec) {
            ALittleClassNameDec class_name_dec = (ALittleClassNameDec)pre_type;
            ALittleClassDec classDec = (ALittleClassDec)class_name_dec.getParent();
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_name_dec.getContainingFile());
            // 所有静态函数
            List<ALittleMethodNameDec> class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.STATIC_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }
            // 所有成员函数
            class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.MEMBER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }

            // 所有setter
            class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.SETTER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }

            // 所有getter
            class_method_name_decList = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, "", class_method_name_decList, 10);
            for (ALittleMethodNameDec class_method_name_dec : class_method_name_decList) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.GETTER_METHOD).
                        withTypeText(class_method_name_dec.getContainingFile().getName())
                );
            }
            // 比如 AEnum.Var
        } else if (pre_type instanceof ALittleEnumDec) {
            // 所有枚举字段
            ALittleEnumDec enum_dec = (ALittleEnumDec)pre_type;
            List<ALittleEnumVarNameDec> var_name_decList = new ArrayList<>();
            ALittleUtil.findEnumVarNameDecList(enum_dec, "", var_name_decList);
            for (ALittleEnumVarNameDec var_name_dec : var_name_decList) {
                variants.add(LookupElementBuilder.create(var_name_dec.getText()).
                        withIcon(ALittleIcons.PROPERTY).
                        withTypeText(var_name_dec.getContainingFile().getName())
                );
            }
        }

        return variants.toArray();
    }
}
