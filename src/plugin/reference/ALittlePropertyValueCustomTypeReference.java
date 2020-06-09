package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.guess.*;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ALittlePropertyValueCustomTypeReference extends ALittleReference<ALittlePropertyValueCustomType> {
    private PsiElement mMethodDec = null;
    private ALittleMethodBodyDec mMethodBodyDec;

    public ALittlePropertyValueCustomTypeReference(@NotNull ALittlePropertyValueCustomType element, TextRange textRange) {
        super(element, textRange);
        reloadInfo();
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
    public String calcNamespaceName() throws ALittleGuessException {
        ResolveResult[] result_list = multiResolve(true);
        for (ResolveResult resolve : result_list) {
            PsiElement result = resolve.getElement();
            if (result instanceof ALittleNamespaceNameDec) {
                return "";
            } else if (result instanceof ALittleClassNameDec) {
                ALittleGuess class_guess = ((ALittleClassNameDec) result).guessType();
                if (!(class_guess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "ALittleClassNameDec.guessType()的结果不是ALittleGuessClass");

                ALittleGuessClass class_guess_class = (ALittleGuessClass) class_guess;
                return class_guess_class.namespace_name;
            } else if (result instanceof ALittleStructNameDec) {
                ALittleGuess struct_guess = ((ALittleStructNameDec) result).guessType();
                if (!(struct_guess instanceof ALittleGuessStruct))
                    throw new ALittleGuessException(myElement, "ALittleStructNameDec.guessType()的结果不是ALittleGuessStruct");
                ALittleGuessStruct struct_guess_struct = (ALittleGuessStruct) struct_guess;
                return struct_guess_struct.namespace_name;
            } else if (result instanceof ALittleEnumNameDec) {
                ALittleGuess enum_guess = ((ALittleEnumNameDec) result).guessType();
                if (!(enum_guess instanceof ALittleGuessEnum))
                    throw new ALittleGuessException(myElement, "ALittleEnumNameDec.guessType()的结果不是ALittleGuessEnum");
                ALittleGuessEnum enum_guess_enum = (ALittleGuessEnum) enum_guess;
                return enum_guess_enum.namespace_name;
            } else if (result instanceof ALittleMethodParamNameDec) {
                return "";
            } else if (result instanceof ALittleVarAssignNameDec) {
                if (!(result.getParent() instanceof ALittleVarAssignDec)) return "";
                ALittleVarAssignDec assign_dec = (ALittleVarAssignDec) result.getParent();
                if (!(assign_dec.getParent() instanceof ALittleVarAssignExpr))
                    throw new ALittleGuessException(myElement, "ALittleVarAssignDecElement的父节点不是ALittleVarAssignExprElement");
                ALittleVarAssignExpr expr_dec = (ALittleVarAssignExpr) assign_dec.getParent();

                // 如果父节点是instance
                if (!(expr_dec.getParent() instanceof ALittleInstanceDec)) return "";
                ALittleInstanceDec instance_dec = (ALittleInstanceDec) expr_dec.getParent();

                ALittleNamespaceElementDec element_dec = (ALittleNamespaceElementDec) instance_dec.getParent();
                if (element_dec == null)
                    throw new ALittleGuessException(myElement, "ALittleInstanceDecElement的父节点不是ALittleNamespaceElementDecElement");
                PsiHelper.ClassAccessType access_type = PsiHelper.calcAccessType(element_dec.getModifierList());
                if (access_type == PsiHelper.ClassAccessType.PROTECTED)
                    return PsiHelper.getNamespaceName(result);
                return "";
            } else if (result instanceof ALittleMethodNameDec) {
                ALittleGuess method_guess = ((ALittleMethodNameDec) result).guessType();
                if (!(method_guess instanceof ALittleGuessFunctor))
                    throw new ALittleGuessException(myElement, "ALittleMethodNameDecElement.guessType()的结果不是ALittleGuessFunctor");
                ALittleGuessFunctor method_guess_functor = (ALittleGuessFunctor) method_guess;
                if (method_guess_functor.element instanceof ALittleGlobalMethodDec) {
                    ALittleNamespaceElementDec element_dec = (ALittleNamespaceElementDec) method_guess_functor.element.getParent();
                    if (element_dec == null)
                        throw new ALittleGuessException(myElement, "ALittleGlobalMethodDecElement的父节点不是ALittleNamespaceElementDecElement");
                    PsiHelper.ClassAccessType access_type = PsiHelper.calcAccessType(element_dec.getModifierList());
                    if (access_type != PsiHelper.ClassAccessType.PRIVATE)
                        return PsiHelper.getNamespaceName(method_guess_functor.element);
                }
                return "";

            } else if (result instanceof ALittleUsingNameDec) {
                ALittleUsingDec using_dec = (ALittleUsingDec) result.getParent();
                if (using_dec == null)
                    throw new ALittleGuessException(myElement, "ALittleUsingNameDecElement的父节点不是ALittleUsingDecElement");
                ALittleNamespaceElementDec element_dec = (ALittleNamespaceElementDec) using_dec.getParent();
                if (element_dec == null)
                    throw new ALittleGuessException(myElement, "ALittleUsingDecElement的父节点不是ALittleNamespaceElementDecElement");
                PsiHelper.ClassAccessType access_type = PsiHelper.calcAccessType(element_dec.getModifierList());
                if (access_type != PsiHelper.ClassAccessType.PRIVATE)
                    return PsiHelper.getNamespaceName(result);
                return "";
            }
        }

        return "";
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guessList = new ArrayList<>();
        // 标记是否已经包含了命名域，命名域的guess不要重复
        boolean hasNamespace = false;

        ResolveResult[] resultList = multiResolve(false);
        for (ResolveResult result : resultList) {
            PsiElement element = result.getElement();

            ALittleGuess guess = null;
            if (element instanceof ALittleNamespaceNameDec) {
                if (!hasNamespace) {
                    hasNamespace = true;
                    ALittleGuessNamespaceName guessNamespaceName = new ALittleGuessNamespaceName(
                            ((ALittleNamespaceNameDec) element).guessType().getValue(),
                            (ALittleNamespaceNameDec) element
                    );
                    guessNamespaceName.updateValue();
                    guess = guessNamespaceName;
                }
            } else if (element instanceof ALittleClassNameDec) {
                ALittleGuess classGuess = ((ALittleClassNameDec) element).guessType();
                if (!(classGuess instanceof ALittleGuessClass)) {
                    throw new ALittleGuessException(myElement, "ALittleClassNameDec.guessType()的结果不是ALittleGuessClass");
                }
                ALittleGuessClass classGuessClass = (ALittleGuessClass) classGuess;
                if (!classGuessClass.template_list.isEmpty()) {
                    throw new ALittleGuessException(myElement, "模板类" + classGuessClass.getValue() + "不能直接使用");
                }
                ALittleGuessClassName guessClassName = new ALittleGuessClassName(classGuessClass.namespace_name,
                        classGuessClass.class_name, (ALittleClassNameDec) element);
                guessClassName.updateValue();
                guess = guessClassName;
            } else if (element instanceof ALittleStructNameDec) {
                ALittleGuess structGuess = ((ALittleStructNameDec) element).guessType();
                if (!(structGuess instanceof ALittleGuessStruct)) {
                    throw new ALittleGuessException(myElement, "ALittleStructNameDec.guessType()的结果不是ALittleGuessStruct");
                }
                ALittleGuessStruct structGuessStruct = (ALittleGuessStruct) structGuess;

                ALittleGuessStructName guessStructName = new ALittleGuessStructName(structGuessStruct.namespace_name,
                        structGuessStruct.struct_name, (ALittleStructNameDec) element);
                guessStructName.updateValue();
                guess = guessStructName;
            } else if (element instanceof ALittleEnumNameDec) {
                ALittleGuess enumGuess = ((ALittleEnumNameDec) element).guessType();
                if (!(enumGuess instanceof ALittleGuessEnum)) {
                    throw new ALittleGuessException(myElement, "ALittleEnumNameDec.guessType()的结果不是ALittleGuessEnum");
                }
                ALittleGuessEnum enumGuessEnum = (ALittleGuessEnum) enumGuess;

                ALittleGuessEnumName guessEnumName = new ALittleGuessEnumName(enumGuessEnum.namespace_name,
                        enumGuessEnum.enum_name, (ALittleEnumNameDec) element);
                guessEnumName.updateValue();
                guess = guessEnumName;
            } else if (element instanceof ALittleMethodParamNameDec) {
                guess = ((ALittleMethodParamNameDec) element).guessType();
            } else if (element instanceof ALittleVarAssignNameDec) {
                guess = ((ALittleVarAssignNameDec) element).guessType();
            } else if (element instanceof ALittleMethodNameDec) {
                guess = ((ALittleMethodNameDec) element).guessType();
            } else if (element instanceof ALittleUsingNameDec) {
                guess = ((ALittleUsingNameDec) element).guessType();
            }

            if (guess != null) guessList.add(guess);
        }

        return guessList;
    }

    @Override
    public void colorAnnotator(@NotNull AnnotationHolder holder) {
        try {
            List<ALittleGuess> guessList = guessTypes();
            if (guessList.isEmpty()) return;

            ALittleGuess guess = guessList.get(0);
            if (!(guess instanceof ALittleGuessFunctor)) return;
            ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor) guess;
            if (guessFunctor.element instanceof ALittleClassStaticDec
                    || guessFunctor.element instanceof ALittleGlobalMethodDec) {
                Annotation anno = holder.createInfoAnnotation(myElement, null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
            }
        } catch (ALittleGuessException ignored) {

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
        Project project = myElement.getProject();
        PsiFile file = myElement.getContainingFile().getOriginalFile();
        List<ResolveResult> results = new ArrayList<>();

        // 处理命名域
        {
            Map<String, ALittleNamespaceNameDec> decMap = ALittleTreeChangeListener.findNamespaceNameDecList(project, mKey);
            if (!decMap.isEmpty()) results.clear();
            for (ALittleNamespaceNameDec dec : decMap.values()) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理全局函数
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.GLOBAL_METHOD, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理类名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.CLASS_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理结构体名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.STRUCT_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理using
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.USING_NAME, file, mNamespace, mKey, true);
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理枚举名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.ENUM_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理单例
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project,
                    PsiHelper.PsiElementType.INSTANCE_NAME, file, mNamespace, mKey, true);
            if (!decList.isEmpty()) results.clear();
            for (PsiElement dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理参数
        if (mMethodDec != null) {
            List<ALittleMethodParamNameDec> decList = PsiHelper.findMethodParamNameDecList(mMethodDec, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleMethodParamNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
            }
        }
        // 处理表达式定义
        {
            List<ALittleVarAssignNameDec> decList = PsiHelper.findVarAssignNameDecList(myElement, mKey);
            if (!decList.isEmpty()) results.clear();
            for (ALittleVarAssignNameDec dec : decList) {
                results.add(new PsiElementResolveResult(dec));
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
        Project project = myElement.getProject();
        PsiFile file = myElement.getContainingFile().getOriginalFile();
        List<LookupElement> variants = new ArrayList<>();
        if (mKey.equals("IntellijIdeaRulezzz"))
            return variants.toArray();

        // 处理命名域
        {
            Map<String, ALittleNamespaceNameDec> decMap = ALittleTreeChangeListener.findNamespaceNameDecList(project, "");
            for (ALittleNamespaceNameDec dec : decMap.values()) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.NAMESPACE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理全局函数
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.GLOBAL_METHOD, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.GLOBAL_METHOD).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理类名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.CLASS_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.CLASS).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理结构体名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.STRUCT_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.STRUCT).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理using
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.USING_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                try {
                    ALittleGuess guess = ((ALittleUsingNameDec) dec).guessType();
                    if (guess instanceof ALittleGuessClass) {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.CLASS).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    } else if (guess instanceof ALittleGuessStruct) {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.STRUCT).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    } else {
                        variants.add(LookupElementBuilder.create(dec.getText()).
                                withIcon(ALittleIcons.PROPERTY).
                                withTypeText(dec.getContainingFile().getName())
                        );
                    }
                } catch (ALittleGuessException ignored) {
                    variants.add(LookupElementBuilder.create(dec.getText()).
                            withIcon(ALittleIcons.CLASS).
                            withTypeText(dec.getContainingFile().getName())
                    );
                }
            }
        }
        // 处理枚举名
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.ENUM_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.ENUM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        // 处理单例
        {
            List<PsiElement> decList = ALittleTreeChangeListener.findALittleNameDecList(project, PsiHelper.PsiElementType.INSTANCE_NAME, file, mNamespace, "", true);
            for (PsiElement dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.INSTANCE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理参数
        if (mMethodDec != null) {
            List<ALittleMethodParamNameDec> decList = PsiHelper.findMethodParamNameDecList(mMethodDec, "");
            for (ALittleMethodParamNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.PARAM).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }

        // 处理表达式
        {
            List<ALittleVarAssignNameDec> decList = PsiHelper.findVarAssignNameDecList(myElement, "");
            for (ALittleVarAssignNameDec dec : decList) {
                variants.add(LookupElementBuilder.create(dec.getText()).
                        withIcon(ALittleIcons.VARIABLE).
                        withTypeText(dec.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}
