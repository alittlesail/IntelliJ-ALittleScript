package plugin;

import com.intellij.execution.process.ConsoleHighlighter;
import com.intellij.ide.highlighter.custom.CustomHighlighterColors;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReference;
import plugin.reference.ALittleReferenceUtil;

import java.util.List;

public class ALittleAnnotator implements Annotator {

    // 获取元素定义
    public static List<ALittleReferenceUtil.GuessTypeInfo> getGuessList(@NotNull PsiElement element) throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = null;

        // 类相关
        if (element instanceof ALittleClassNameDec) {
            guessList = ((ALittleClassNameDec) element).guessTypes();
        } else if (element instanceof ALittleNamespaceNameDec) {
            guessList = ((ALittleNamespaceNameDec) element).guessTypes();
        } else if (element instanceof ALittleClassVarDec) {
            guessList = ((ALittleClassVarDec) element).guessTypes();

        // 自定义类型相关
        } else if (element instanceof ALittleCustomType) {
            guessList = ((ALittleCustomType) element).guessTypes();

            // 枚举相关
        } else if (element instanceof ALittleEnumNameDec) {
            guessList = ((ALittleEnumNameDec) element).guessTypes();

            // 函数相关
        } else if (element instanceof ALittleMethodParamNameDec) {
            guessList = ((ALittleMethodParamNameDec) element).guessTypes();

            // 属性相关
        } else if (element instanceof ALittlePropertyValueBracketValue) {
            guessList = ((ALittlePropertyValueBracketValue) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCustomType) {
            guessList = ((ALittlePropertyValueCustomType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueDotIdName) {
            guessList = ((ALittlePropertyValueDotIdName) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueThisType) {
            guessList = ((ALittlePropertyValueThisType) element).guessTypes();
        } else if (element instanceof ALittlePropertyValueCastType) {
            guessList = ((ALittlePropertyValueCastType) element).guessTypes();

            // 结构体相关
        } else if (element instanceof ALittleStructNameDec) {
            guessList = ((ALittleStructNameDec) element).guessTypes();
        } else if (element instanceof ALittleStructVarDec) {
            guessList = ((ALittleStructVarDec) element).guessTypes();

            // 局部变量定义
        } else if (element instanceof ALittleVarAssignNameDec) {
            guessList = ((ALittleVarAssignNameDec) element).guessTypes();
        }

        return guessList;
    }

    public static void CheckErrorForGuessList(@NotNull PsiElement element, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        if (guessList == null) return;

        // 检查定义
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "未知类型");
        }

        // 检查重复定义
        if (guessList.size() > 1 && !(guessList.get(0) instanceof ALittleNamespaceNameDec)) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "重复定义");
        }
    }

    public static void CheckErrorForName(@NotNull PsiElement element) throws ALittleReferenceUtil.ALittleReferenceException {
        if  (element instanceof ALittleVarAssignNameDec) {
            if (element.getText().startsWith("___")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "局部变量名不能以3个下划线开头");
            }
        } else if (element instanceof ALittleNamespaceNameDec) {
            if (element.getText().startsWith("___")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "命名域不能以3个下划线开头");
            }
        } else if (element instanceof ALittleClassNameDec) {
            if (element.getText().startsWith("___")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "类名不能以3个下划线开头");
            }
        } else if (element instanceof ALittleStructNameDec) {
            if (element.getText().startsWith("___")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "结构体名不能以3个下划线开头");
            }
        } else if (element instanceof ALittleEnumNameDec) {
            if (element.getText().startsWith("___")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "枚举名不能以3个下划线开头");
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void ColorAnnotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder, List<ALittleReferenceUtil.GuessTypeInfo> guessList) throws ALittleReferenceUtil.ALittleReferenceException {
        // 函数的参数名
        if (element instanceof ALittleMethodParamNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
            return;
        }

        // this
        if (element instanceof ALittlePropertyValueThisType) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD2_ATTRIBUTES);
            return;
        }

        // 常量
        if (element instanceof ALittleConstValue) {
            ALittleConstValue dec = (ALittleConstValue)element;
            if (dec.getDigitContent() != null) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
                return;
            }

            if (dec.getStringContent() != null) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STRING);
                return;
            }

            if (dec.getText().equals("true") || dec.getText().equals("false") || dec.getText().equals("null")) {
                Annotation anno = holder.createInfoAnnotation(dec, null);
                anno.setTextAttributes(ConsoleHighlighter.CYAN_BRIGHT);
                return;
            }
            return;
        }

        // 类名
        if (element instanceof ALittleClassNameDec) {
            Annotation anno = holder.createInfoAnnotation(element, null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_NAME);
            return;
        }

        // 属性如果是函数名
        if (element instanceof ALittlePropertyValueDotIdName) {
            ALittlePropertyValueDotIdName dec = (ALittlePropertyValueDotIdName)element;
            ALittleReferenceUtil.GuessTypeInfo guess = null;
            if (guessList != null && !guessList.isEmpty()) guess = guessList.get(0);
            if (guess != null && guess.element instanceof ALittleMethodNameDec) {
                Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                return;
            }
            return;
        }

        // 起始属性
        if (element instanceof ALittlePropertyValueCustomType) {
            ALittlePropertyValueCustomType dec = (ALittlePropertyValueCustomType)element;
            PsiReference ref = dec.getReference();
            if (ref != null) {
                PsiElement resolve = ref.resolve();
                if (resolve instanceof ALittleMethodParamNameDec) {
                    Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                    anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
                    return;
                }

                if (resolve instanceof ALittleVarAssignNameDec) {
                    PsiElement parent = resolve.getParent();
                    if (parent instanceof ALittleForPairDec) {
                        Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                        anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
                        return;
                    }
                    return;
                }

                if (resolve instanceof ALittleMethodNameDec) {
                    Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                    anno.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                    return;
                }
            }

            return;
        }

        // 局部变量
        if (element instanceof ALittleVarAssignNameDec) {
            PsiElement parent = element.getParent();
            if (parent instanceof ALittleForPairDec) {
                ALittleVarAssignNameDec dec = (ALittleVarAssignNameDec)element;
                Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
                anno.setTextAttributes(CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES);
            }
            return;
        }

        // 自定义类型
        if (element instanceof ALittleCustomType) {
            ALittleCustomType dec = (ALittleCustomType)element;
            Annotation anno = holder.createInfoAnnotation(dec.getIdContent(), null);
            anno.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
            return;
        }
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        try {
            // 获取对应的定义
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = getGuessList(element);

            // 检查未定义或者重复定义
            CheckErrorForGuessList(element, guessList);

            // 检查变量名
            CheckErrorForName(element);

            // 检查错误，给元素上色
            PsiReference ref = element.getReference();
            if (ref instanceof ALittleReference) {
                ((ALittleReference) ref).checkError();
                ((ALittleReference) ref).colorAnnotator(holder);
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            holder.createErrorAnnotation(e.getElement(), e.getError());
        }
    }
}