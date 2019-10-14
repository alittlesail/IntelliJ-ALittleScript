package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassCtorDecReference extends ALittleReference<ALittleClassCtorDec> {
    public ALittleClassCtorDecReference(@NotNull ALittleClassCtorDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
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
            throw new ALittleGuessException(myElement, "该函数是从父类继承下来，但是参数数量不一致");
        }
        List<ALittleMethodParamOneDec> extendsParamOneDecList = extendsMethodParamDec.getMethodParamOneDecList();
        List<ALittleMethodParamOneDec> myParamOneDecList = myMethodParamDec.getMethodParamOneDecList();
        if (extendsParamOneDecList.size() > myParamOneDecList.size()) {
            throw new ALittleGuessException(myMethodParamDec, "该函数是从父类继承下来，但是子类的参数数量少于父类的构造函数");
        }
        for (int i = 0; i < extendsParamOneDecList.size(); ++i) {
            ALittleMethodParamOneDec extendsOneDec = extendsParamOneDecList.get(i);
            ALittleMethodParamNameDec extendsNameDec = extendsOneDec.getMethodParamNameDec();
            if (extendsNameDec == null) throw new ALittleGuessException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");
            ALittleMethodParamOneDec myOneDec = myParamOneDecList.get(i);
            ALittleMethodParamNameDec myNameDec = myOneDec.getMethodParamNameDec();
            if (myNameDec == null) throw new ALittleGuessException(myMethodParamDec, "该函数是从父类继承下来，但是定义不一致");

            if (!extendsNameDec.guessType().value.equals(myNameDec.guessType().value)) {
                throw new ALittleGuessException(myMethodParamDec, "该函数是从父类继承下来，但是子类参数和父类参数类型不一致");
            }
        }
    }
}
