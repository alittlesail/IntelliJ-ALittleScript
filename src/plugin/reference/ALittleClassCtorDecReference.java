package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.psi.*;

import java.util.List;

public class ALittleClassCtorDecReference extends ALittleReference<ALittleClassCtorDec> {
    public ALittleClassCtorDecReference(@NotNull ALittleClassCtorDec element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleClassDec class_dec = PsiHelper.findClassDecFromParent(myElement);
        if (class_dec == null) return;

        ALittleClassDec class_extends_dec = PsiHelper.findClassExtends(class_dec);
        if (class_extends_dec == null) return;

        ALittleClassCtorDec extends_ctor_dec = PsiHelper.findFirstCtorDecFromExtends(class_extends_dec, 100);
        if (extends_ctor_dec == null) return;

        // 参数必须一致并且可转化
        ALittleMethodParamDec extends_method_param_dec = extends_ctor_dec.getMethodParamDec();
        ALittleMethodParamDec my_method_param_dec = myElement.getMethodParamDec();
        if (extends_method_param_dec == null && my_method_param_dec == null) return;
        if (extends_method_param_dec == null || my_method_param_dec == null)
            throw new ALittleGuessException(myElement, "该函数是从父类继承下来，但是参数数量不一致");

        List<ALittleMethodParamOneDec> extends_param_one_dec_list = extends_method_param_dec.getMethodParamOneDecList();
        List<ALittleMethodParamOneDec> my_param_one_dec_list = my_method_param_dec.getMethodParamOneDecList();
        if (extends_param_one_dec_list.size() > my_param_one_dec_list.size())
            throw new ALittleGuessException(my_method_param_dec, "该函数是从父类继承下来，但是子类的参数数量少于父类的构造函数");

        for (int i = 0; i < extends_param_one_dec_list.size(); ++i) {
            ALittleMethodParamOneDec extends_one_dec = extends_param_one_dec_list.get(i);
            ALittleMethodParamNameDec extends_name_dec = extends_one_dec.getMethodParamNameDec();
            if (extends_name_dec == null) throw new ALittleGuessException(my_method_param_dec, "该函数是从父类继承下来，但是定义不一致");
            ALittleMethodParamOneDec my_one_dec = my_param_one_dec_list.get(i);
            ALittleMethodParamNameDec my_name_dec = my_one_dec.getMethodParamNameDec();
            if (my_name_dec == null) throw new ALittleGuessException(my_method_param_dec, "该函数是从父类继承下来，但是定义不一致");

            ALittleGuess extends_name_dec_guess = extends_name_dec.guessType();
            ALittleGuess my_name_dec_guess = my_name_dec.guessType();
            if (!extends_name_dec_guess.getValue().equals(my_name_dec_guess.getValue()))
                throw new ALittleGuessException(my_method_param_dec, "该函数是从父类继承下来，但是子类参数和父类参数类型不一致");
        }
    }
}
