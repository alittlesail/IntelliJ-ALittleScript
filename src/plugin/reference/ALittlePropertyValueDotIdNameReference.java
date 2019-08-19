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

public class ALittlePropertyValueDotIdNameReference extends ALittleReference<ALittlePropertyValueDotIdName> {
    private List<ALittleMethodNameDec> m_getter_list;
    private List<ALittleMethodNameDec> m_setter_list;

    public ALittlePropertyValueDotIdNameReference(@NotNull ALittlePropertyValueDotIdName element, TextRange textRange) {
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
            if (element instanceof ALittleClassVarDec) {
                guess = ((ALittleClassVarDec) element).guessType();
            } else if (element instanceof ALittleStructVarDec) {
                guess = ((ALittleStructVarDec) element).guessType();
            } else if (element instanceof ALittleEnumVarDec) {
                guess = ((ALittleEnumVarDec) element).guessType();
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
            } else if (element instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) element).guessType();
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
            // 获取父节点
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
            ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

            // 获取所在位置
            int index = suffixList.indexOf(propertyValueSuffix);
            if (index == -1) return new ResolveResult[0];

            // 获取前一个类型
            ALittleReferenceUtil.GuessTypeInfo preType;
            if (index == 0) {
                preType = propertyValueFirstType.guessType();
            } else {
                preType = suffixList.get(index - 1).guessType();
            }

            // 判断当前后缀是否是最后一个后缀
            ALittlePropertyValueSuffix nextSuffix = null;
            if (index + 1 < suffixList.size()) {
                nextSuffix = suffixList.get(index + 1);
            }

            // 处理类的实例对象
            if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec)preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
                List<ALittleClassVarDec> classVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(myElement.getProject(), namespaceName, classDec, mKey, classVarDecList, 10);
                for (ALittleClassVarDec classVarDec : classVarDecList) {
                    results.add(new PsiElementResolveResult(classVarDec));
                }

                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();

                // 在当前情况下，只有当前propertyvalue在等号的左边，并且是最后一个属性才是setter，否则都是getter
                PsiElement parent = propertyValue.getParent();
                if (nextSuffix == null && parent instanceof ALittleOpAssignExpr) {
                    // 所有setter
                    m_setter_list = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), namespaceName, classDec, mKey, m_setter_list, 10);
                    classMethodNameDecList.addAll(m_setter_list);
                } else {
                    // 所有getter
                    m_getter_list = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), namespaceName, classDec, mKey, m_getter_list, 10);
                    classMethodNameDecList.addAll(m_getter_list);
                }
                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), namespaceName, classDec, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);

                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(class_methodNameDec));
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec)preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(myElement.getProject(), namespaceName, structDec, mKey, structVarDecList, 10);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    results.add(new PsiElementResolveResult(structVarDec));
                }
                // 比如 ALittle.AEnum
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec)preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) namespaceNameDec.getContainingFile());
                // 所有枚举名
                List<ALittleEnumNameDec> enumNameDecList = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), namespaceName, mKey);
                for (ALittleEnumNameDec enumNameDec : enumNameDecList) {
                    results.add(new PsiElementResolveResult(enumNameDec));
                }
                // 所有全局函数
                List<ALittleMethodNameDec> methodNameDecList = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), namespaceName, mKey);
                for (ALittleMethodNameDec methodNameDec : methodNameDecList) {
                    results.add(new PsiElementResolveResult(methodNameDec));
                }
                // 所有类名
                List<ALittleClassNameDec> classNameDecList = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), namespaceName, mKey);
                for (ALittleClassNameDec classNameDec : classNameDecList) {
                    results.add(new PsiElementResolveResult(classNameDec));
                }
                // 所有结构体名
                List<ALittleStructNameDec> structNameDecList = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), namespaceName, mKey);
                for (ALittleStructNameDec structNameDec : structNameDecList) {
                    results.add(new PsiElementResolveResult(structNameDec));
                }
                // 所有单例
                List<ALittleVarAssignNameDec> instanceNameDecList = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), namespaceName, mKey, false);
                for (ALittleVarAssignNameDec instanceNameDec : instanceNameDecList) {
                    results.add(new PsiElementResolveResult(instanceNameDec));
                }
                // 比如 AClass.StaticMethod()
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec)preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classNameDec.getContainingFile());
                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), namespaceName, classDec, mKey, classMethodNameDecList, 10);

                // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
                if (nextSuffix != null) {
                    ALittlePropertyValueMethodCall methodCall_stat = nextSuffix.getPropertyValueMethodCall();
                    if (methodCall_stat != null) {
                        int param_count = methodCall_stat.getValueStatList().size();
                        if (param_count == 1) {
                            // 所有getter
                            ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), namespaceName, classDec, mKey, classMethodNameDecList, 10);
                        } else if (param_count == 2) {
                            // 所有setter
                            ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), namespaceName, classDec, mKey, classMethodNameDecList, 10);
                        }
                    }
                }

                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), namespaceName, classDec, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);

                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(class_methodNameDec));
                }
                // 比如 AEnum.Var
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM) {
                // 所有枚举字段
                ALittleEnumDec enumDec = (ALittleEnumDec) preType.element;
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, mKey, varDecList);
                for (ALittleEnumVarDec var_name_dec : varDecList) {
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
            // 获取父节点
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
            ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

            // 获取所在位置
            int index = suffixList.indexOf(propertyValueSuffix);
            if (index == -1) return new ResolveResult[0];

            // 获取前一个类型
            ALittleReferenceUtil.GuessTypeInfo preType;
            if (index == 0) {
                preType = propertyValueFirstType.guessType();
            } else {
                preType = suffixList.get(index - 1).guessType();
            }

            // 处理类的实例对象
            if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec) preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classDec.getContainingFile());
                List<ALittleClassVarDec> classVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(myElement.getProject(), namespaceName, classDec, "", classVarDecList, 10);
                for (ALittleClassVarDec class_var_name_dec : classVarDecList) {
                    variants.add(LookupElementBuilder.create(class_var_name_dec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(class_var_name_dec.getContainingFile().getName())
                    );
                }

                // 所有setter
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }

                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec) preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(myElement.getProject(), namespaceName, structDec, "", structVarDecList, 10);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    variants.add(LookupElementBuilder.create(structVarDec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(structVarDec.getContainingFile().getName())
                    );
                }
                // 比如 ALittle.AEnum
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec) preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) namespaceNameDec.getContainingFile());
                // 所有枚举名
                List<ALittleEnumNameDec> enumNameDecList = ALittleTreeChangeListener.findEnumNameDecList(myElement.getProject(), namespaceName, "");
                for (ALittleEnumNameDec enumNameDec : enumNameDecList) {
                    variants.add(LookupElementBuilder.create(enumNameDec.getText()).
                            withIcon(ALittleIcons.ENUM).
                            withTypeText(enumNameDec.getContainingFile().getName())
                    );
                }
                // 所有全局函数
                List<ALittleMethodNameDec> methodNameDecList = ALittleTreeChangeListener.findGlobalMethodNameDecList(myElement.getProject(), namespaceName, "");
                for (ALittleMethodNameDec methodNameDec : methodNameDecList) {
                    variants.add(LookupElementBuilder.create(methodNameDec.getText()).
                            withIcon(ALittleIcons.GLOBAL_METHOD).
                            withTypeText(methodNameDec.getContainingFile().getName())
                    );
                }
                // 所有类名
                List<ALittleClassNameDec> classNameDecList = ALittleTreeChangeListener.findClassNameDecList(myElement.getProject(), namespaceName, "");
                for (ALittleClassNameDec classNameDec : classNameDecList) {
                    variants.add(LookupElementBuilder.create(classNameDec.getText()).
                            withIcon(ALittleIcons.CLASS).
                            withTypeText(classNameDec.getContainingFile().getName())
                    );
                }
                // 所有结构体名
                List<ALittleStructNameDec> structNameDecList = ALittleTreeChangeListener.findStructNameDecList(myElement.getProject(), namespaceName, "");
                for (ALittleStructNameDec structNameDec : structNameDecList) {
                    variants.add(LookupElementBuilder.create(structNameDec.getText()).
                            withIcon(ALittleIcons.STRUCT).
                            withTypeText(structNameDec.getContainingFile().getName())
                    );
                }
                // 所有单例
                List<ALittleVarAssignNameDec> instanceNameDecList = ALittleTreeChangeListener.findInstanceNameDecList(myElement.getProject(), namespaceName, "", false);
                for (ALittleVarAssignNameDec instanceNameDec : instanceNameDecList) {
                    variants.add(LookupElementBuilder.create(instanceNameDec.getText()).
                            withIcon(ALittleIcons.INSTANCE).
                            withTypeText(instanceNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AClass.StaticMethod()
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec) preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classNameDec.getContainingFile());
                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.STATIC_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }
                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }

                // 所有setter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(myElement.getProject(), namespaceName, classDec, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec class_methodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(class_methodNameDec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(class_methodNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AEnum.Var
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM) {
                // 所有枚举字段
                ALittleEnumDec enumDec = (ALittleEnumDec) preType.element;
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, "", varDecList);
                for (ALittleEnumVarDec var_name_dec : varDecList) {
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
