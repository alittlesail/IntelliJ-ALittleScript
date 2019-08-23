package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleIcons;
import plugin.ALittleTreeChangeListener;
import plugin.ALittleUtil;
import plugin.psi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ALittleClassCtorDecReference extends ALittleReference<ALittleClassCtorDec> {
    public ALittleClassCtorDecReference(@NotNull ALittleClassCtorDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        PsiElement parent = myElement.getParent();
        Project project = myElement.getProject();
        PsiFile psiFile = myElement.getContainingFile();
        if (!(parent instanceof ALittleClassDec)) return;
        ALittleClassDec classDec = (ALittleClassDec)parent;
        if (classDec.getClassExtendsDec() == null) return;
        ALittleClassExtendsDec extendsDec = classDec.getClassExtendsDec();
        if (extendsDec == null) return;
        ALittleClassNameDec classNameDec = extendsDec.getClassNameDec();
        if (classNameDec == null) return;
        ALittleReferenceUtil.GuessTypeInfo extendsGuess = classNameDec.guessType();

        ALittleClassCtorDec extendsCtorDec = ALittleUtil.findFirstCtorDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, 100);
        if (extendsCtorDec == null) return;

        // 参数必须一致并且可转化
        ALittleMethodParamDec extendsMethodParamDec = extendsCtorDec.getMethodParamDec();
        ALittleMethodParamDec myMethodParamDec = myElement.getMethodParamDec();
        if (extendsMethodParamDec == null && myMethodParamDec == null) return;
        if (extendsMethodParamDec == null || myMethodParamDec == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "该函数是从父类继承下来，但是定义不一致");
        }
        List<ALittleMethodParamOneDec> extendsParamOneDecList = extendsMethodParamDec.getMethodParamOneDecList();
        List<ALittleMethodParamOneDec> myParamOneDecList = myMethodParamDec.getMethodParamOneDecList();
        if (extendsParamOneDecList.size() > myParamOneDecList.size()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");
        }
        for (int i = 0; i < extendsParamOneDecList.size(); ++i) {
            ALittleMethodParamOneDec extendsOneDec = extendsParamOneDecList.get(i);
            ALittleMethodParamNameDec extendsNameDec = extendsOneDec.getMethodParamNameDec();
            if (extendsNameDec == null) throw new ALittleReferenceUtil.ALittleReferenceException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");
            ALittleMethodParamOneDec myOneDec = myParamOneDecList.get(i);
            ALittleMethodParamNameDec myNameDec = myOneDec.getMethodParamNameDec();
            if (myNameDec == null) throw new ALittleReferenceUtil.ALittleReferenceException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");
            try {
                ALittleReferenceOpUtil.guessTypeEqual(extendsNameDec, extendsNameDec.guessType(), myNameDec, myNameDec.guessType());
            } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");
            }
        }
    }
}
