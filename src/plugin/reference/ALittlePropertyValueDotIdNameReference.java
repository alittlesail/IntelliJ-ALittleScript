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

public class ALittlePropertyValueDotIdNameReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    private List<ALittleMethodNameDec> m_getter_list;
    private List<ALittleMethodNameDec> m_setter_list;

    public ALittlePropertyValueDotIdNameReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
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
                    PsiReference[] references = dec.getReferences();
                    if (references != null && references.length > 0) {
                        ALittleMethodNameDecReference reference = (ALittleMethodNameDecReference)references[0];
                        guess = reference.guessTypeForGetter();
                    }
                } else if (is_setter) {
                    ALittleMethodNameDec dec = (ALittleMethodNameDec)element;
                    PsiReference[] references = dec.getReferences();
                    if (references != null && references.length > 0) {
                        ALittleMethodNameDecReference reference = (ALittleMethodNameDecReference)references[0];
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

        ALittlePropertyValueDotId property_value_dot_id = (ALittlePropertyValueDotId)myElement.getParent();
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix)property_value_dot_id.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue)property_value_suffix.getParent();

        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        PsiElement pre_property_value = property_value.getPropertyValueCustomType();
        if (pre_property_value == null) {
            pre_property_value = property_value.getPropertyValueThisType();
        }

        // 判断当前后缀是否是最后一个后缀
        boolean is_last_value = false;
        ALittlePropertyValueSuffix next_suffix = null;
        for (int index = 0; index < suffix_list.size(); ++index) {
            ALittlePropertyValueSuffix suffix = suffix_list.get(index);

            if (suffix.equals(property_value_suffix)) {
                is_last_value = (index == suffix_list.size() - 1);
                if (!is_last_value) next_suffix = suffix_list.get(index + 1);
                break;
            }
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null)
                pre_property_value = dot_id;
            ALittlePropertyValueBrackValueStat brack_value = suffix.getPropertyValueBrackValueStat();
            if (brack_value != null)
                pre_property_value = brack_value;
            ALittlePropertyValueMethodCallStat method_call = suffix.getPropertyValueMethodCallStat();
            if (method_call != null)
                pre_property_value = method_call;
        }

        if (pre_property_value == null) {
            return results.toArray(new ResolveResult[results.size()]);
        }

        PsiElement pre_type = null;
        if (pre_property_value instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) pre_property_value).guessType();
        } else if (pre_property_value instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dot_id_name = ((ALittlePropertyValueDotId) pre_property_value).getPropertyValueDotIdName();
            pre_type = dot_id_name.guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) pre_property_value).guessType();
        }
        if (pre_type == null) {
            return results.toArray(new ResolveResult[results.size()]);
        }

        // 处理类的实例对象
        if (pre_type instanceof ALittleClassDec) {
            ALittleClassDec class_dec = (ALittleClassDec) pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_dec.getContainingFile());
            List<ALittleClassVarNameDec> class_var_name_dec_list = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, class_dec, m_key, class_var_name_dec_list, 10);
            for (ALittleClassVarNameDec class_var_name_dec : class_var_name_dec_list) {
                results.add(new PsiElementResolveResult(class_var_name_dec));
            }

            List<ALittleMethodNameDec> class_method_name_dec_list = new ArrayList<>();

            // 在当前情况下，只有当前propertyvalue在等号的左边，并且是最后一个属性才是setter，否则都是getter
            PsiElement parent = property_value.getParent();
            if (is_last_value && parent instanceof ALittleOpAssignExpr) {
                // 所有setter
                m_setter_list = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, class_dec, m_key, m_setter_list, 10);
                class_method_name_dec_list.addAll(m_setter_list);
            } else {
                // 所有getter
                m_getter_list = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, class_dec, m_key, m_getter_list, 10);
                class_method_name_dec_list.addAll(m_getter_list);
            }
            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, class_dec, m_key, class_method_name_dec_list, 10);
            // 过滤掉重复的函数名
            class_method_name_dec_list = ALittleUtil.filterSameMethodName(class_method_name_dec_list);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_dec_list) {
                results.add(new PsiElementResolveResult(class_method_name_dec));
            }
            // 处理结构体的实例对象
        } else if (pre_type instanceof ALittleStructDec) {
            ALittleStructDec struct_dec = (ALittleStructDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) struct_dec.getContainingFile());
            List<ALittleStructVarNameDec> struct_var_name_dec_list = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, struct_dec, m_key, struct_var_name_dec_list, 10);
            for (ALittleStructVarNameDec struct_var_name_dec : struct_var_name_dec_list) {
                results.add(new PsiElementResolveResult(struct_var_name_dec));
            }
            // 比如 ALittle.AEnum
        } else if (pre_type instanceof ALittleNamespaceNameDec) {
            ALittleNamespaceNameDec namespace_name_dec = (ALittleNamespaceNameDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) namespace_name_dec.getContainingFile());
            // 所有枚举名
            List<ALittleEnumNameDec> enum_name_dec_list = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), src_namespace, m_key);
            for (ALittleEnumNameDec enum_name_dec : enum_name_dec_list) {
                results.add(new PsiElementResolveResult(enum_name_dec));
            }
            // 所有全局函数
            List<ALittleMethodNameDec> method_name_dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), src_namespace, m_key);
            for (ALittleMethodNameDec method_name_dec : method_name_dec_list) {
                results.add(new PsiElementResolveResult(method_name_dec));
            }
            // 所有类名
            List<ALittleClassNameDec> class_name_dec_list = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), src_namespace, m_key);
            for (ALittleClassNameDec class_name_dec : class_name_dec_list) {
                results.add(new PsiElementResolveResult(class_name_dec));
            }
            // 所有结构体名
            List<ALittleStructNameDec> struct_name_dec_list = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), src_namespace, m_key);
            for (ALittleStructNameDec struct_name_dec : struct_name_dec_list) {
                results.add(new PsiElementResolveResult(struct_name_dec));
            }
            // 所有单例
            List<ALittleInstanceNameDec> instance_name_dec_list = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), src_namespace, m_key);
            for (ALittleInstanceNameDec instance_name_dec : instance_name_dec_list) {
                results.add(new PsiElementResolveResult(instance_name_dec));
            }
            // 比如 AClass.StaticMethod()
        } else if (pre_type instanceof ALittleClassNameDec) {
            ALittleClassNameDec class_name_dec = (ALittleClassNameDec)pre_type;
            ALittleClassDec class_dec = (ALittleClassDec)class_name_dec.getParent();
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_name_dec.getContainingFile());
            // 所有静态函数
            List<ALittleMethodNameDec> class_method_name_dec_list = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, class_dec, m_key, class_method_name_dec_list, 10);

            // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
            if (next_suffix != null) {
                ALittlePropertyValueMethodCallStat method_call_stat = next_suffix.getPropertyValueMethodCallStat();
                if (method_call_stat != null) {
                    int param_count = method_call_stat.getValueStatList().size();
                    if (param_count == 1) {
                        // 所有getter
                        ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, class_dec, m_key, class_method_name_dec_list, 10);
                    } else if (param_count == 2) {
                        // 所有setter
                        ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, class_dec, m_key, class_method_name_dec_list, 10);
                    }
                }
            }

            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, class_dec, m_key, class_method_name_dec_list, 10);
            // 过滤掉重复的函数名
            class_method_name_dec_list = ALittleUtil.filterSameMethodName(class_method_name_dec_list);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_dec_list) {
                results.add(new PsiElementResolveResult(class_method_name_dec));
            }
            // 比如 AEnum.Var
        } else if (pre_type instanceof ALittleEnumDec) {
            // 所有枚举字段
            ALittleEnumDec enum_dec = (ALittleEnumDec)pre_type;
            List<ALittleEnumVarNameDec> var_name_dec_list = new ArrayList<>();
            ALittleUtil.findEnumVarNameDecList(enum_dec, m_key, var_name_dec_list);
            for (ALittleEnumVarNameDec var_name_dec : var_name_dec_list) {
                results.add(new PsiElementResolveResult(var_name_dec));
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
        List<LookupElement> variants = new ArrayList<>();

        ALittlePropertyValueDotId property_value_dot_id = (ALittlePropertyValueDotId)myElement.getParent();
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix)property_value_dot_id.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue)property_value_suffix.getParent();

        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
        PsiElement pre_property_value = property_value.getPropertyValueCustomType();
        if (pre_property_value == null) {
            pre_property_value = property_value.getPropertyValueThisType();
        }

        for (ALittlePropertyValueSuffix suffix : suffix_list) {
            if (suffix.equals(property_value_suffix)) {
                break;
            }
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null)
                pre_property_value = dot_id;
            ALittlePropertyValueBrackValueStat brack_value = suffix.getPropertyValueBrackValueStat();
            if (brack_value != null)
                pre_property_value = brack_value;
            ALittlePropertyValueMethodCallStat method_call = suffix.getPropertyValueMethodCallStat();
            if (method_call != null)
                pre_property_value = method_call;
        }

        if (pre_property_value == null) {
            return variants.toArray();
        }

        PsiElement pre_type = null;
        if (pre_property_value instanceof ALittlePropertyValueCustomType) {
            pre_type = ((ALittlePropertyValueCustomType) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueThisType) {
            pre_type = ((ALittlePropertyValueThisType) pre_property_value).guessType();
        } else if (pre_property_value instanceof  ALittlePropertyValueDotId) {
            ALittlePropertyValueDotIdName dot_id_name = ((ALittlePropertyValueDotId) pre_property_value).getPropertyValueDotIdName();
            pre_type = dot_id_name.guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueMethodCallStat) {
            pre_type = ((ALittlePropertyValueMethodCallStat) pre_property_value).guessType();
        } else if (pre_property_value instanceof ALittlePropertyValueBrackValueStat) {
            pre_type = ((ALittlePropertyValueBrackValueStat) pre_property_value).guessType();
        }
        if (pre_type == null) {
            return variants.toArray();
        }

        // 处理类的实例对象
        if (pre_type instanceof ALittleClassDec) {
            ALittleClassDec class_dec = (ALittleClassDec) pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_dec.getContainingFile());
            List<ALittleClassVarNameDec> class_var_name_dec_list = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, class_dec, "", class_var_name_dec_list, 10);
            for (ALittleClassVarNameDec class_var_name_dec : class_var_name_dec_list) {
                variants.add(LookupElementBuilder.create(class_var_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(class_var_name_dec.getContainingFile().getName()));
            }

            List<ALittleMethodNameDec> class_method_name_dec_list = new ArrayList<>();
            // 所有setter
            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 所有getter
            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 过滤掉重复的函数名
            class_method_name_dec_list = ALittleUtil.filterSameMethodName(class_method_name_dec_list);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_dec_list) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(class_method_name_dec.getContainingFile().getName()));
            }
            // 处理结构体的实例对象
        } else if (pre_type instanceof ALittleStructDec) {
            ALittleStructDec struct_dec = (ALittleStructDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) struct_dec.getContainingFile());
            List<ALittleStructVarNameDec> struct_var_name_dec_list = new ArrayList<>();
            // 所有成员变量
            ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, struct_dec, "", struct_var_name_dec_list, 10);
            for (ALittleStructVarNameDec struct_var_name_dec : struct_var_name_dec_list) {
                variants.add(LookupElementBuilder.create(struct_var_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(struct_var_name_dec.getContainingFile().getName()));
            }
            // 比如 ALittle.AEnum
        } else if (pre_type instanceof ALittleNamespaceNameDec) {
            ALittleNamespaceNameDec namespace_name_dec = (ALittleNamespaceNameDec)pre_type;
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) namespace_name_dec.getContainingFile());
            // 所有枚举名
            List<ALittleEnumNameDec> enum_name_dec_list = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleEnumNameDec enum_name_dec : enum_name_dec_list) {
                variants.add(LookupElementBuilder.create(enum_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(enum_name_dec.getContainingFile().getName()));
            }
            // 所有全局函数
            List<ALittleMethodNameDec> method_name_dec_list = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleMethodNameDec method_name_dec : method_name_dec_list) {
                variants.add(LookupElementBuilder.create(method_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(method_name_dec.getContainingFile().getName()));
            }
            // 所有类名
            List<ALittleClassNameDec> class_name_dec_list = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleClassNameDec class_name_dec : class_name_dec_list) {
                variants.add(LookupElementBuilder.create(class_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(class_name_dec.getContainingFile().getName()));
            }
            // 所有结构体名
            List<ALittleStructNameDec> struct_name_dec_list = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleStructNameDec struct_name_dec : struct_name_dec_list) {
                variants.add(LookupElementBuilder.create(struct_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(struct_name_dec.getContainingFile().getName()));
            }
            // 所有单例
            List<ALittleInstanceNameDec> instance_name_dec_list = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), src_namespace, "");
            for (ALittleInstanceNameDec instance_name_dec : instance_name_dec_list) {
                variants.add(LookupElementBuilder.create(instance_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(instance_name_dec.getContainingFile().getName()));
            }
            // 比如 AClass.StaticMethod()
        } else if (pre_type instanceof ALittleClassNameDec) {
            ALittleClassNameDec class_name_dec = (ALittleClassNameDec)pre_type;
            ALittleClassDec class_dec = (ALittleClassDec)class_name_dec.getParent();
            String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_name_dec.getContainingFile());
            // 所有静态函数
            List<ALittleMethodNameDec> class_method_name_dec_list = new ArrayList<>();
            ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 所有成员函数
            ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 所有setter
            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 所有getter
            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, class_dec, "", class_method_name_dec_list, 10);
            // 过滤掉重复的函数名
            class_method_name_dec_list = ALittleUtil.filterSameMethodName(class_method_name_dec_list);

            for (ALittleMethodNameDec class_method_name_dec : class_method_name_dec_list) {
                variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(class_method_name_dec.getContainingFile().getName()));
            }
            // 比如 AEnum.Var
        } else if (pre_type instanceof ALittleEnumDec) {
            // 所有枚举字段
            ALittleEnumDec enum_dec = (ALittleEnumDec)pre_type;
            List<ALittleEnumVarNameDec> var_name_dec_list = new ArrayList<>();
            ALittleUtil.findEnumVarNameDecList(enum_dec, "", var_name_dec_list);
            for (ALittleEnumVarNameDec var_name_dec : var_name_dec_list) {
                variants.add(LookupElementBuilder.create(var_name_dec.getText()).
                        withIcon(ALittleIcons.FILE).
                        withTypeText(var_name_dec.getContainingFile().getName()));
            }
        }

        return variants.toArray();
    }
}
