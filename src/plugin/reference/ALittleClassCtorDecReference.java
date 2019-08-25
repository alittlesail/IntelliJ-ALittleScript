package plugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

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
        if (!(parent instanceof ALittleClassDec)) return;
        ALittleClassDec classDec = (ALittleClassDec)parent;
        PsiHelper.ClassExtendsData classExtendsData = PsiHelper.findClassExtends(classDec);
        if (classExtendsData == null) return;

        ALittleClassCtorDec extendsCtorDec = PsiHelper.findFirstCtorDecFromExtends(classExtendsData.dec, 100);
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
