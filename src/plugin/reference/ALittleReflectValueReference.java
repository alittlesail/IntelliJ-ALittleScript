package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleCustomType;
import plugin.psi.ALittleReflectValue;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleReflectValueReference extends ALittleReference<ALittleReflectValue> {
    public ALittleReflectValueReference(@NotNull ALittleReflectValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        ALittleGuess guess = null;
        if (myElement.getReflectCustomType() != null) {
            ALittleCustomType custom_type = myElement.getReflectCustomType().getCustomType();
            if (custom_type == null) return guess_list;

            guess = custom_type.guessType();
        } else if (myElement.getReflectValueStat() != null) {
            ALittleValueStat value_stat = myElement.getReflectValueStat().getValueStat();
            if (value_stat == null) return guess_list;

            guess = value_stat.guessType();
        }

        if (guess instanceof ALittleGuessStruct)
            return ALittleTreeChangeListener.findALittleStructGuessList(myElement.getProject(), "ALittle", "StructInfo");
        else if (guess instanceof ALittleGuessClass)
            return ALittleTreeChangeListener.findALittleStructGuessList(myElement.getProject(), "ALittle", "ClassInfo");
        else if (guess instanceof ALittleGuessTemplate) {
            ALittleGuessTemplate guess_template = (ALittleGuessTemplate) guess;
            if (guess_template.template_extends instanceof ALittleGuessClass || guess_template.is_class)
                return ALittleTreeChangeListener.findALittleStructGuessList(myElement.getProject(), "ALittle", "ClassInfo");
            else if (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct)
                return ALittleTreeChangeListener.findALittleStructGuessList(myElement.getProject(), "ALittle", "StructInfo");
        }
        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleGuess guess = null;
        if (myElement.getReflectCustomType() != null) {
            ALittleCustomType custom_type = myElement.getReflectCustomType().getCustomType();
            if (custom_type == null) return;

            guess = custom_type.guessType();
        } else if (myElement.getReflectValueStat() != null) {
            ALittleValueStat value_stat = myElement.getReflectValueStat().getValueStat();
            if (value_stat == null) return;

            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

            guess = value_stat.guessType();
        }

        if (guess instanceof ALittleGuessStruct || guess instanceof ALittleGuessClass)
            return;

        if (guess instanceof ALittleGuessTemplate) {
            ALittleGuessTemplate guess_template = (ALittleGuessTemplate) guess;
            if (guess_template.template_extends instanceof ALittleGuessClass || guess_template.is_class)
                return;
            else if (myElement.getReflectCustomType() != null
                    && (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct))
                return;
        }

        throw new ALittleGuessException(myElement, "反射对象必须是struct或者是class以及class对象");
    }
}
