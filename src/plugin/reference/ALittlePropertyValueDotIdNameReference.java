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
import plugin.component.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdNameReference extends ALittleReference<ALittlePropertyValueDotIdName> {
    private List<ALittleMethodNameDec> mGetterList;
    private List<ALittleMethodNameDec> mSetterList;

    public ALittlePropertyValueDotIdNameReference(@NotNull ALittlePropertyValueDotIdName element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        mGetterList = null;
        mSetterList = null;

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
                guess = ((ALittleMethodNameDec)element).guessType();
                if (element.getParent() instanceof ALittleClassGetterDec) {
                    if (mGetterList != null && mGetterList.indexOf(element) >= 0) {
                        guess = guess.functorReturnList.get(0);
                    }
                } else if (element.getParent() instanceof ALittleClassSetterDec) {
                    if (mSetterList != null && mSetterList.indexOf(element) >= 0) {
                        guess = guess.functorParamList.get(1);
                    }
                }
            } else if (element instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) element).guessType();
            } else if (element instanceof ALittleEnumNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_ENUM_NAME;
                guess.value = ((ALittleEnumNameDec) element).guessType().value;
                guess.element = element;
            } else if (element instanceof ALittleStructNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_STRUCT_NAME;
                guess.value = ((ALittleStructNameDec) element).guessType().value;
                guess.element = element;
            } else if (element instanceof ALittleClassNameDec) {
                guess = new ALittleReferenceUtil.GuessTypeInfo();
                guess.type = ALittleReferenceUtil.GuessType.GT_CLASS_NAME;
                guess.element = element;
            }

            if (guess != null) guessList.add(guess);
        }

        mGetterList = null;
        mSetterList = null;

        return guessList;
    }

    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        PsiElement element = myElement.getIdContent();
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

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile();
        List<ResolveResult> results = new ArrayList<>();
        try {
            // 获取父节点
            ALittlePropertyValueDotId propertyValueDotId = (ALittlePropertyValueDotId)myElement.getParent();
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)propertyValueDotId.getParent();
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

                // 计算当前元素所在的类
                PsiElement classDecElement = myElement;
                while (classDecElement != null) {
                    if (classDecElement instanceof ALittleClassDec) {
                        break;
                    }
                    classDecElement = classDecElement.getParent();
                }
                int accessLevel = ALittleUtil.sAccessPublic;
                if (classDecElement != null) {
                    accessLevel = ALittleUtil.calcAccessLevel(classDecElement, classDec, ALittleUtil.sAccessPrivate);
                }

                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(project, psiFile, namespaceName, classDec, accessLevel, mKey, classVarDecList, 10);
                for (ALittleClassVarDec classVarDec : classVarDecList) {
                    results.add(new PsiElementResolveResult(classVarDec));
                }

                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                // 在当前情况下，只有当前propertyValue在等号的左边，并且是最后一个属性才是setter，否则都是getter
                if (nextSuffix == null && propertyValue.getParent() instanceof ALittleOpAssignExpr) {
                    mSetterList = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForSetter(project, psiFile, namespaceName, classDec, accessLevel, mKey, mSetterList, 10);
                    classMethodNameDecList.addAll(mSetterList);
                } else {
                    mGetterList = new ArrayList<>();
                    ALittleUtil.findMethodNameDecListForGetter(project, psiFile, namespaceName, classDec, accessLevel, mKey, mGetterList, 10);
                    classMethodNameDecList.addAll(mGetterList);
                }
                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(project, psiFile, namespaceName, classDec, accessLevel, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);
                // 添加函数名元素
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(classMethodNameDec));
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec)preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(project, psiFile, namespaceName, structDec, mKey, structVarDecList, 10);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    results.add(new PsiElementResolveResult(structVarDec));
                }
                // 比如 ALittleName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec)preType.element;
                String namespaceName = namespaceNameDec.getText();
                // 所有枚举名
                List<ALittleEnumNameDec> enumNameDecList = ALittleTreeChangeListener.findEnumNameDecList(project, psiFile, namespaceName, mKey);
                for (ALittleEnumNameDec enumNameDec : enumNameDecList) {
                    results.add(new PsiElementResolveResult(enumNameDec));
                }
                // 所有全局函数
                List<ALittleMethodNameDec> methodNameDecList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, psiFile, namespaceName, mKey);
                for (ALittleMethodNameDec methodNameDec : methodNameDecList) {
                    results.add(new PsiElementResolveResult(methodNameDec));
                }
                // 所有类名
                List<ALittleClassNameDec> classNameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespaceName, mKey);
                for (ALittleClassNameDec classNameDec : classNameDecList) {
                    results.add(new PsiElementResolveResult(classNameDec));
                }
                // 所有结构体名
                List<ALittleStructNameDec> structNameDecList = ALittleTreeChangeListener.findStructNameDecList(project, psiFile, namespaceName, mKey);
                for (ALittleStructNameDec structNameDec : structNameDecList) {
                    results.add(new PsiElementResolveResult(structNameDec));
                }
                // 所有单例
                List<ALittleVarAssignNameDec> instanceNameDecList = ALittleTreeChangeListener.findInstanceNameDecList(project, psiFile, namespaceName, mKey, false);
                for (ALittleVarAssignNameDec instanceNameDec : instanceNameDecList) {
                    results.add(new PsiElementResolveResult(instanceNameDec));
                }
                // 比如 AClassName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec)preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classNameDec.getContainingFile());

                // 计算当前元素所在的类
                PsiElement classDecElement = myElement;
                while (classDecElement != null) {
                    if (classDecElement instanceof ALittleClassDec) {
                        break;
                    }
                    classDecElement = classDecElement.getParent();
                }
                int accessLevel = ALittleUtil.sAccessPublic;
                if (classDecElement != null) {
                    accessLevel = ALittleUtil.calcAccessLevel(classDecElement, classDec, ALittleUtil.sAccessPrivate);
                }

                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(project, psiFile, namespaceName, classDec, accessLevel, mKey, classMethodNameDecList, 10);

                // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
                if (nextSuffix != null) {
                    ALittlePropertyValueMethodCall methodCallStat = nextSuffix.getPropertyValueMethodCall();
                    if (methodCallStat != null) {
                        int paramCount = methodCallStat.getValueStatList().size();
                        if (paramCount == 1) {
                            // 所有getter
                            ALittleUtil.findMethodNameDecListForGetter(project, psiFile, namespaceName, classDec, accessLevel, mKey, classMethodNameDecList, 10);
                        } else if (paramCount == 2) {
                            // 所有setter
                            ALittleUtil.findMethodNameDecListForSetter(project, psiFile, namespaceName, classDec, accessLevel, mKey, classMethodNameDecList, 10);
                        }
                    }
                }

                // 所有成员函数
                ALittleUtil.findMethodNameDecListForFun(project, psiFile, namespaceName, classDec, accessLevel, mKey, classMethodNameDecList, 10);
                // 过滤掉重复的函数名
                classMethodNameDecList = ALittleUtil.filterSameMethodName(classMethodNameDecList);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(classMethodNameDec));
                }
                // 比如 AEnumName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM_NAME) {
                // 所有枚举字段
                ALittleEnumNameDec enumNameDec = (ALittleEnumNameDec) preType.element;
                ALittleEnumDec enumDec = (ALittleEnumDec) enumNameDec.getParent();
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, mKey, varDecList);
                for (ALittleEnumVarDec varNameDec : varDecList) {
                    results.add(new PsiElementResolveResult(varNameDec));
                }
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();
        try {
            // 获取父节点
            ALittlePropertyValueDotId propertyValueDotId = (ALittlePropertyValueDotId)myElement.getParent();
            ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)propertyValueDotId.getParent();
            ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
            ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
            List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

            // 获取所在位置
            int index = suffixList.indexOf(propertyValueSuffix);
            if (index == -1) return variants.toArray();

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

                // 计算当前元素所在的类
                PsiElement classDecElement = myElement;
                while (classDecElement != null) {
                    if (classDecElement instanceof ALittleClassDec) {
                        break;
                    }
                    classDecElement = classDecElement.getParent();
                }
                int accessLevel = ALittleUtil.sAccessPublic;
                if (classDecElement != null) {
                    accessLevel = ALittleUtil.calcAccessLevel(classDecElement, classDec, ALittleUtil.sAccessPrivate);
                }

                List<ALittleClassVarDec> classVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findClassVarNameDecList(project, psiFile, namespaceName, classDec, accessLevel, "", classVarDecList, 10);
                for (ALittleClassVarDec classVarNameDec : classVarDecList) {
                    if (classVarNameDec.getIdContent() == null) continue;
                    variants.add(LookupElementBuilder.create(classVarNameDec.getIdContent().getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(classVarNameDec.getContainingFile().getName())
                    );
                }

                // 所有setter
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getIdContent().getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getIdContent().getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getIdContent().getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec) preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                ALittleUtil.findStructVarNameDecList(project, psiFile, namespaceName, structDec, "", structVarDecList, 10);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    variants.add(LookupElementBuilder.create(structVarDec.getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(structVarDec.getContainingFile().getName())
                    );
                }
                // 比如 ALittleName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec) preType.element;
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) namespaceNameDec.getContainingFile());
                // 所有枚举名
                List<ALittleEnumNameDec> enumNameDecList = ALittleTreeChangeListener.findEnumNameDecList(project, psiFile, namespaceName, "");
                for (ALittleEnumNameDec enumNameDec : enumNameDecList) {
                    variants.add(LookupElementBuilder.create(enumNameDec.getText()).
                            withIcon(ALittleIcons.ENUM).
                            withTypeText(enumNameDec.getContainingFile().getName())
                    );
                }
                // 所有全局函数
                List<ALittleMethodNameDec> methodNameDecList = ALittleTreeChangeListener.findGlobalMethodNameDecList(project, psiFile, namespaceName, "");
                for (ALittleMethodNameDec methodNameDec : methodNameDecList) {
                    variants.add(LookupElementBuilder.create(methodNameDec.getText()).
                            withIcon(ALittleIcons.GLOBAL_METHOD).
                            withTypeText(methodNameDec.getContainingFile().getName())
                    );
                }
                // 所有类名
                List<ALittleClassNameDec> classNameDecList = ALittleTreeChangeListener.findClassNameDecList(project, psiFile, namespaceName, "");
                for (ALittleClassNameDec classNameDec : classNameDecList) {
                    variants.add(LookupElementBuilder.create(classNameDec.getText()).
                            withIcon(ALittleIcons.CLASS).
                            withTypeText(classNameDec.getContainingFile().getName())
                    );
                }
                // 所有结构体名
                List<ALittleStructNameDec> structNameDecList = ALittleTreeChangeListener.findStructNameDecList(project, psiFile, namespaceName, "");
                for (ALittleStructNameDec structNameDec : structNameDecList) {
                    variants.add(LookupElementBuilder.create(structNameDec.getText()).
                            withIcon(ALittleIcons.STRUCT).
                            withTypeText(structNameDec.getContainingFile().getName())
                    );
                }
                // 所有单例
                List<ALittleVarAssignNameDec> instanceNameDecList = ALittleTreeChangeListener.findInstanceNameDecList(project, psiFile, namespaceName, "", false);
                for (ALittleVarAssignNameDec instanceNameDec : instanceNameDecList) {
                    variants.add(LookupElementBuilder.create(instanceNameDec.getText()).
                            withIcon(ALittleIcons.INSTANCE).
                            withTypeText(instanceNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AClassName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec) preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();
                String namespaceName = ALittleUtil.getNamespaceName((ALittleFile) classNameDec.getContainingFile());

                // 计算当前元素所在的类
                PsiElement classDecElement = myElement;
                while (classDecElement != null) {
                    if (classDecElement instanceof ALittleClassDec) {
                        break;
                    }
                    classDecElement = classDecElement.getParent();
                }
                int accessLevel = ALittleUtil.sAccessPublic;
                if (classDecElement != null) {
                    accessLevel = ALittleUtil.calcAccessLevel(classDecElement, classDec, ALittleUtil.sAccessPrivate);
                }

                // 所有静态函数
                List<ALittleMethodNameDec> classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForStatic(project, psiFile, namespaceName, classDec, accessLevel,"", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.STATIC_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 所有成员函数
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForFun(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.MEMBER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有setter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForSetter(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.SETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }

                // 所有getter
                classMethodNameDecList = new ArrayList<>();
                ALittleUtil.findMethodNameDecListForGetter(project, psiFile, namespaceName, classDec, accessLevel, "", classMethodNameDecList, 10);
                for (ALittleMethodNameDec classMethodNameDec : classMethodNameDecList) {
                    variants.add(LookupElementBuilder.create(classMethodNameDec.getText()).
                            withIcon(ALittleIcons.GETTER_METHOD).
                            withTypeText(classMethodNameDec.getContainingFile().getName())
                    );
                }
                // 比如 AEnum.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM_NAME) {
                // 所有枚举字段
                ALittleEnumNameDec enumNameDec = (ALittleEnumNameDec) preType.element;
                ALittleEnumDec enumDec = (ALittleEnumDec) enumNameDec.getParent();
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                ALittleUtil.findEnumVarNameDecList(enumDec, "", varDecList);
                for (ALittleEnumVarDec varNameDec : varDecList) {
                    variants.add(LookupElementBuilder.create(varNameDec.getIdContent().getText()).
                            withIcon(ALittleIcons.PROPERTY).
                            withTypeText(varNameDec.getContainingFile().getName())
                    );
                }
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }

        return variants.toArray();
    }
}
