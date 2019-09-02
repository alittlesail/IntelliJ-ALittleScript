package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueDotIdNameReference extends ALittleReference<ALittlePropertyValueDotIdName> {
    private List<PsiElement> mGetterList;
    private List<PsiElement> mSetterList;
    private ALittleReferenceUtil.GuessTypeInfo mClassGuessInfo;

    public ALittlePropertyValueDotIdNameReference(@NotNull ALittlePropertyValueDotIdName element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        mGetterList = null;
        mSetterList = null;
        mClassGuessInfo = null;

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleReferenceUtil.GuessTypeInfo guess = null;
            if (element instanceof ALittleClassVarDec) {
                guess = ((ALittleClassVarDec) element).guessType();

                if (mClassGuessInfo != null && guess.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE && mClassGuessInfo.classTemplateMap != null) {
                    if (!mClassGuessInfo.classTemplateMap.containsKey(guess.value)) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, mClassGuessInfo.value + "没有定义模板" + guess.value);
                    }
                    guess = mClassGuessInfo.classTemplateMap.get(guess.value);
                }

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

                if (mClassGuessInfo != null && mClassGuessInfo.classTemplateMap != null) {
                    // 检查是否有模板
                    boolean has_template = false;
                    for (ALittleReferenceUtil.GuessTypeInfo returnGuessInfo : guess.functorReturnList) {
                        if (returnGuessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE) {
                            has_template = true;
                            break;
                        }
                    }
                    if (!has_template) {
                        for (ALittleReferenceUtil.GuessTypeInfo paramGuessInfo : guess.functorParamList) {
                            if (paramGuessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE) {
                                has_template = true;
                                break;
                            }
                        }
                    }

                    // 如果有模板参数，那么就要重新构建一下
                    if (has_template) {
                        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                        info.type = guess.type;
                        info.element = guess.element;
                        info.functorAwait = guess.functorAwait;
                        info.functorParamTail = guess.functorParamTail;
                        info.value = "Functor<(";
                        if (info.functorAwait) {
                            info.value = "Functor<await(";
                        }

                        info.functorParamList = new ArrayList<>();
                        info.functorParamNameList = new ArrayList<>();
                        info.functorParamNameList.addAll(guess.functorParamNameList);

                        List<String> typeList = new ArrayList<>();
                        int start_index = 0;
                        if (element.getParent() instanceof ALittleClassMethodDec) {
                            info.functorParamList.add(mClassGuessInfo);
                            typeList.add(mClassGuessInfo.value);
                            start_index = 1;
                        }
                        for (int i = start_index; i < guess.functorParamList.size(); ++i) {
                            ALittleReferenceUtil.GuessTypeInfo guessInfo = guess.functorParamList.get(i);
                            if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE) {
                                if (!mClassGuessInfo.classTemplateMap.containsKey(guessInfo.value)) {
                                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, mClassGuessInfo.value + "没有定义模板" + guessInfo.value);
                                }
                                ALittleReferenceUtil.GuessTypeInfo templateGuessInfo = mClassGuessInfo.classTemplateMap.get(guessInfo.value);
                                info.functorParamList.add(templateGuessInfo);
                                typeList.add(templateGuessInfo.value);
                            } else {
                                info.functorParamList.add(guessInfo);
                                typeList.add(guessInfo.value);
                            }
                        }
                        info.value += String.join(",", typeList) + ")";
                        info.functorReturnList = new ArrayList<>();
                        typeList = new ArrayList<>();
                        for (ALittleReferenceUtil.GuessTypeInfo guessInfo : guess.functorReturnList) {
                            if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE) {
                                if (!mClassGuessInfo.classTemplateMap.containsKey(guessInfo.value)) {
                                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, mClassGuessInfo.value + "没有定义模板" + guessInfo.value);
                                }
                                ALittleReferenceUtil.GuessTypeInfo templateGuessInfo = mClassGuessInfo.classTemplateMap.get(guessInfo.value);
                                info.functorReturnList.add(templateGuessInfo);
                                typeList.add(templateGuessInfo.value);
                            } else {
                                info.functorReturnList.add(guessInfo);
                                typeList.add(guessInfo.value);
                            }
                        }
                        if (!typeList.isEmpty()) info.value += ":";
                        info.value += String.join(",", typeList);

                        guess = info;
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
        mClassGuessInfo = null;

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
                mClassGuessInfo = preType;
                ALittleClassDec classDec = (ALittleClassDec)preType.element;

                // 计算当前元素所在的类
                int accessLevel =  PsiHelper.sAccessOnlyPublic;
                ALittleClassDec myClassDec = PsiHelper.findClassDecFromParent(myElement);
                if (myClassDec != null) {
                    accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, classDec);
                }

                // 所有成员变量
                List<PsiElement> classVarDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec,
                        accessLevel, PsiHelper.ClassAttrType.VAR, mKey, classVarDecList, 100);
                for (PsiElement classVarDec : classVarDecList) {
                    results.add(new PsiElementResolveResult(classVarDec));
                }

                List<PsiElement> classMethodNameDecList = new ArrayList<>();
                // 在当前情况下，只有当前propertyValue在等号的左边，并且是最后一个属性才是setter，否则都是getter
                if (nextSuffix == null && propertyValue.getParent() instanceof ALittleOpAssignExpr) {
                    mSetterList = new ArrayList<>();
                    PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.SETTER, mKey, mSetterList, 100);
                    classMethodNameDecList.addAll(mSetterList);
                } else {
                    mGetterList = new ArrayList<>();
                    PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.GETTER, mKey, mGetterList, 100);
                    classMethodNameDecList.addAll(mGetterList);
                }
                // 所有成员函数
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.FUN, mKey, classMethodNameDecList, 100);
                // 添加函数名元素
                classMethodNameDecList = PsiHelper.filterSameName(classMethodNameDecList);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(classMethodNameDec));
                }
                // 处理结构体的实例对象
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec)preType.element;
                String namespaceName = PsiHelper.getNamespaceName(structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findStructVarDecList(structDec, mKey, structVarDecList, 100);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    results.add(new PsiElementResolveResult(structVarDec));
                }
                // 比如 ALittleName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec)preType.element;
                String namespaceName = namespaceNameDec.getText();
                // 所有枚举名
                List<PsiElement> enumNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.ENUM_NAME, psiFile, namespaceName, mKey, true);
                for (PsiElement enumNameDec : enumNameDecList) {
                    results.add(new PsiElementResolveResult(enumNameDec));
                }
                // 所有全局函数
                List<PsiElement> methodNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.GLOBAL_METHOD, psiFile, namespaceName, mKey, true);
                for (PsiElement methodNameDec : methodNameDecList) {
                    results.add(new PsiElementResolveResult(methodNameDec));
                }
                // 所有类名
                List<PsiElement> classNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.CLASS_NAME, psiFile, namespaceName, mKey, true);
                for (PsiElement classNameDec : classNameDecList) {
                    results.add(new PsiElementResolveResult(classNameDec));
                }
                // 所有结构体名
                List<PsiElement> structNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.STRUCT_NAME, psiFile, namespaceName, mKey, true);
                for (PsiElement structNameDec : structNameDecList) {
                    results.add(new PsiElementResolveResult(structNameDec));
                }
                // 所有单例
                List<PsiElement> instanceNameDecList = ALittleTreeChangeListener.findALittleNameDecList(project,
                        PsiHelper.PsiElementType.INSTANCE_NAME, psiFile, namespaceName, mKey, false);
                for (PsiElement instanceNameDec : instanceNameDecList) {
                    results.add(new PsiElementResolveResult(instanceNameDec));
                }
                // 比如 AClassName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec)preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();

                // 计算当前元素所在的类
                int accessLevel =  PsiHelper.sAccessOnlyPublic;
                ALittleClassDec myClassDec = PsiHelper.findClassDecFromParent(myElement);
                if (myClassDec != null) {
                    accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, classDec);
                }

                // 所有静态函数
                List<PsiElement> classMethodNameDecList = new ArrayList<>();
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.STATIC, mKey, classMethodNameDecList, 100);

                // 如果后面那个是MethodCall，并且有两个参数的是setter，是一个参数的是getter，否则两个都不是
                if (nextSuffix != null) {
                    ALittlePropertyValueMethodCall methodCallStat = nextSuffix.getPropertyValueMethodCall();
                    if (methodCallStat != null) {
                        int paramCount = methodCallStat.getValueStatList().size();
                        if (paramCount == 1) {
                            // 所有getter
                            PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.GETTER, mKey, classMethodNameDecList, 100);
                        } else if (paramCount == 2) {
                            // 所有setter
                            PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.SETTER, mKey, classMethodNameDecList, 100);
                        }
                    }
                }

                // 所有成员函数
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.FUN, mKey, classMethodNameDecList, 100);
                classMethodNameDecList = PsiHelper.filterSameName(classMethodNameDecList);
                for (PsiElement classMethodNameDec : classMethodNameDecList) {
                    results.add(new PsiElementResolveResult(classMethodNameDec));
                }
                // 比如 AEnumName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM_NAME) {
                // 所有枚举字段
                ALittleEnumNameDec enumNameDec = (ALittleEnumNameDec) preType.element;
                ALittleEnumDec enumDec = (ALittleEnumDec) enumNameDec.getParent();
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                PsiHelper.findEnumVarDecList(enumDec, mKey, varDecList);
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

                // 计算当前元素所在的类
                int accessLevel =  PsiHelper.sAccessOnlyPublic;
                ALittleClassDec myClassDec = PsiHelper.findClassDecFromParent(myElement);
                if (myClassDec != null) {
                    accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, classDec);
                }

                List<PsiElement> classVarDecList = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findClassAttrList(classDec, accessLevel, PsiHelper.ClassAttrType.VAR, "", classVarDecList, 100);
                for (PsiElement classVarDec : classVarDecList) {
                    PsiElement nameDec = ((ALittleClassVarDec)classVarDec).getIdContent();
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
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                ALittleStructDec structDec = (ALittleStructDec) preType.element;
                String namespaceName = PsiHelper.getNamespaceName((ALittleFile) structDec.getContainingFile());
                List<ALittleStructVarDec> structVarDecList = new ArrayList<>();
                // 所有成员变量
                PsiHelper.findStructVarDecList(structDec, "", structVarDecList, 100);
                for (ALittleStructVarDec structVarDec : structVarDecList) {
                    PsiElement nameDec = structVarDec.getIdContent();
                    if (nameDec != null) {
                        variants.add(LookupElementBuilder.create(nameDec.getText()).
                                withIcon(ALittleIcons.PROPERTY).
                                withTypeText(structVarDec.getContainingFile().getName())
                        );
                    }
                }
                // 比如 ALittleName.XXX
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME) {
                ALittleNamespaceNameDec namespaceNameDec = (ALittleNamespaceNameDec) preType.element;
                String namespaceName = PsiHelper.getNamespaceName((ALittleFile) namespaceNameDec.getContainingFile());
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
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                ALittleClassNameDec classNameDec = (ALittleClassNameDec) preType.element;
                ALittleClassDec classDec = (ALittleClassDec) classNameDec.getParent();

                // 计算当前元素所在的类
                int accessLevel =  PsiHelper.sAccessOnlyPublic;
                ALittleClassDec myClassDec = PsiHelper.findClassDecFromParent(myElement);
                if (myClassDec != null) {
                    accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, classDec);
                }

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
            } else if (preType.type == ALittleReferenceUtil.GuessType.GT_ENUM_NAME) {
                // 所有枚举字段
                ALittleEnumNameDec enumNameDec = (ALittleEnumNameDec) preType.element;
                ALittleEnumDec enumDec = (ALittleEnumDec) enumNameDec.getParent();
                List<ALittleEnumVarDec> varDecList = new ArrayList<>();
                PsiHelper.findEnumVarDecList(enumDec, "", varDecList);
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
