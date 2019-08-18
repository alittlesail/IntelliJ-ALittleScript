package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdNameReference extends ALittleReference {
    private List<ALittleMethodNameDec> m_getter_list;
    private List<ALittleMethodNameDec> m_setter_list;

    public ALittlePropertyValueDotIdNameReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        m_getter_list = null;
        m_setter_list = null;

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleReferenceUtil.GuessTypeInfo guess = null;
            if (element instanceof ALittleClassVarNameDec) {
                guess = ((ALittleClassVarNameDec) element).guessType();
            } else if (element instanceof ALittleStructVarNameDec) {
                guess = ((ALittleStructVarNameDec) element).guessType();
            } else if (element instanceof ALittleMethodNameDec) {
                ALittleMethodNameDec dec = (ALittleMethodNameDec)element;
                // 如果是getter，那么就返回getter的返回值
                if (m_getter_list.indexOf(element) >= 0) {
                    ALittleReferenceUtil.GuessTypeInfo getterType = dec.guessType();
                    if (!getterType.functorReturnList.isEmpty()) {
                        guess = getterType.functorReturnList.get(0);
                    }
                // 如果是setter，那么就返回setter的第一个参数
                } else if (m_setter_list.indexOf(element) >= 0) {
                    ALittleReferenceUtil.GuessTypeInfo setterType = dec.guessType();
                    if (!setterType.functorParamList.isEmpty()) {
                        guess = setterType.functorParamList.get(0);
                    }
                // 如果是其他，那么就返回对应的Functor
                } else {
                    guess = dec.guessType();
                }
            } else if (element instanceof ALittleEnumNameDec) {
                guess = ((ALittleEnumNameDec) element).guessType();
            } else if (element instanceof ALittleInstanceNameDec) {
                guess = ((ALittleInstanceNameDec) element).guessType();
            } else if (element instanceof ALittleEnumVarNameDec) {
                guess = ((ALittleEnumVarNameDec) element).guessType();
            } else if (element instanceof ALittleClassNameDec) {
                guess = ((ALittleClassNameDec) element).guessType();
            }

            if (guess != null) guessList.add(guess);
        }

        m_getter_list = null;
        m_setter_list = null;
        return guessList;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        try {
            ALittlePropertyValueDotId propertyValueDotId = (ALittlePropertyValueDotId) myElement.getParent();
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix) propertyValueDotId.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue) propertyValueSuffix.getParent();

            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();
            PsiElement prePropertyValue = propertyValue.getPropertyValueCustomType();
            if (prePropertyValue == null) {
                prePropertyValue = propertyValue.getPropertyValueThisType();
            }
            if (prePropertyValue == null) {
                prePropertyValue = propertyValue.getPropertyValueCastType();
            }

            // 判断当前后缀是否是最后一个后缀
            boolean isLastValue = false;
            ALittlePropertyValueSuffix nextSuffix = null;
            for (int index = 0; index < suffixList.size(); ++index) {
                ALittlePropertyValueSuffix suffix = suffixList.get(index);

                if (suffix.equals(propertyValueSuffix)) {
                    isLastValue = (index == suffixList.size() - 1);
                    if (!isLastValue) nextSuffix = suffixList.get(index + 1);
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
                return new ResolveResult[0];
            }

            ALittleReferenceUtil.GuessTypeInfo preType = null;
            if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
                preType = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
                preType = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
                preType = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueDotId) {
                preType = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName().guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
                preType = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
                preType = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
            }

            if (preType == null) {
                return new ResolveResult[0];
            }

            // 处理类的实例对象
            if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec)preType.element;
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
                List<ALittleClassVarNameDec> classVarNameDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, classDec, mKey, classVarNameDecList, 10);
                for (ALittleClassVarNameDec class_var_name_dec : classVarNameDecList) {
                    results.add(new PsiElementResolveResult(class_var_name_dec));
                }

                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();

                // 在当前情况下，只有当前propertyvalue在等号的左边，并且是最后一个属性才是setter，否则都是getter
                PsiElement parent = propertyValue.getParent();
                if (isLastValue && parent instanceof ALittleOpAssignExpr) {
                    // 所有setter
                    m_setter_list = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, mKey, m_setter_list, 10);
                    classMethodNameDecList.addAll(m_setter_list);
                } else {
                    // 所有getter
                    m_getter_list = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, mKey, m_getter_list, 10);
                    classMethodNameDecList.addAll(m_getter_list);
                }
                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);

                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(class_method_name_dec));
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec)preType.element;
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarNameDec> structVarNameDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, structDec, mKey, structVarNameDecList, 10);
                for (ALittleStructVarNameDec struct_var_name_dec : structVarNameDecList) {
                    results.add(new PsiElementResolveResult(struct_var_name_dec));
                }
                // 比如 ALittle.AEnum
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec)preType.element;
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) namespaceNameDec.getContainingFile());
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
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec)preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) classNameDec.getContainingFile());
                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, classDec, mKey, classMethodNameDecList, 10);

                // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
                if (nextSuffix != null) {
                    ALittlePropertyValueMethodCallStat methodCall_stat = nextSuffix.getPropertyValueMethodCallStat();
                    if (methodCall_stat != null) {
                        int param_count = methodCall_stat.getValueStatList().size();
                        if (param_count == 1) {
                            // 所有getter
                            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, mKey, classMethodNameDecList, 10);
                        } else if (param_count == 2) {
                            // 所有setter
                            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, mKey, classMethodNameDecList, 10);
                        }
                    }
                }

                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);

                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(class_method_name_dec));
                }
                // 比如 AEnum.Var
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM) {
                // 所有枚举字段
                ALittleEnumDec enumDec = (ALittleEnumDec) preType.element;
                List<ALittleEnumVarNameDec> varNameDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, mKey, varNameDecList);
                for (ALittleEnumVarNameDec var_name_dec : varNameDecList) {
                    results.add(new PsiElementResolveResult(var_name_dec));
                }
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        try {
            ALittlePropertyValueDotId propertyValueDotId = (ALittlePropertyValueDotId) myElement.getParent();
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix) propertyValueDotId.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue) propertyValueSuffix.getParent();

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

            ALittleReferenceUtil.GuessTypeInfo preType = null;
            if (prePropertyValue instanceof ALittlePropertyValueCustomType) {
                preType = ((ALittlePropertyValueCustomType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueThisType) {
                preType = ((ALittlePropertyValueThisType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueCastType) {
                preType = ((ALittlePropertyValueCastType) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueDotId) {
                preType = ((ALittlePropertyValueDotId) prePropertyValue).getPropertyValueDotIdName().guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueMethodCallStat) {
                preType = ((ALittlePropertyValueMethodCallStat) prePropertyValue).guessType();
            } else if (prePropertyValue instanceof ALittlePropertyValueBrackValueStat) {
                preType = ((ALittlePropertyValueBrackValueStat) prePropertyValue).guessType();
            }
            if (preType == null) {
                return variants.toArray();
            }

            // 处理类的实例对象
            if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec) preType.element;
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
                List<ALittleClassVarNameDec> classVarNameDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(myElement.getProject(), src_namespace, classDec, "", classVarNameDecList, 10);
                for (ALittleClassVarNameDec class_var_name_dec : classVarNameDecList) {
                    variants.add(LookupElementBuilder.create(class_var_name_dec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(class_var_name_dec.getContainingFile().getName())
                    );
                }

                // 所有setter
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }

                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec) preType.element;
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarNameDec> structVarNameDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(myElement.getProject(), src_namespace, structDec, "", structVarNameDecList, 10);
                for (ALittleStructVarNameDec struct_var_name_dec : structVarNameDecList) {
                    variants.add(LookupElementBuilder.create(struct_var_name_dec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(struct_var_name_dec.getContainingFile().getName())
                    );
                }
                // 比如 ALittle.AEnum
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespace_name_dec = (ALittleNamespaceNameDec) preType.element;
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
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec class_name_dec = (ALittleClassNameDec) preType.element;
                ALittleClassDec classDec = (ALittleClassDec) class_name_dec.getParent();
                String src_namespace = ALittleUtil.getNamespaceName((ALittleFile) class_name_dec.getContainingFile());
                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.STATIC_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }
                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }

                // 所有setter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), src_namespace, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_method_name_dec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_method_name_dec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(class_method_name_dec.getContainingFile().getName())
                    );
                }
                // 比如 AEnum.Var
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM) {
                // 所有枚举字段
                ALittleEnumDec enumDec = (ALittleEnumDec) preType.element;
                List<ALittleEnumVarNameDec> varNameDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, "", varNameDecList);
                for (ALittleEnumVarNameDec var_name_dec : varNameDecList) {
                    variants.add(LookupElementBuilder.create(var_name_dec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(var_name_dec.getContainingFile().getName())
                    );
                }
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }

        return variants.toArray();
    }
}
