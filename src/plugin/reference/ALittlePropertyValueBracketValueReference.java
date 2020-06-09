package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueBracketValueReference extends ALittleReference<ALittlePropertyValueBracketValue> {
    public ALittlePropertyValueBracketValueReference(@NotNull ALittlePropertyValueBracketValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        // 获取父节点
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix) myElement.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue) property_value_suffix.getParent();
        ALittlePropertyValueFirstType property_value_first_type = property_value.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffix_list.indexOf(property_value_suffix);
        if (index == -1) return guess_list;

        // 获取前一个类型
        ALittleGuess pre_type;
        if (index == 0)
            pre_type = property_value_first_type.guessType();
        else
            pre_type = suffix_list.get(index - 1).guessType();

        // 获取类型
        if (pre_type instanceof ALittleGuessList) {
            ALittleGuess sub_type = ((ALittleGuessList) pre_type).sub_type;
            if (pre_type.is_const && !sub_type.is_const) {
                sub_type = sub_type.clone();
                sub_type.is_const = true;
                sub_type.updateValue();
            }
            guess_list.add(sub_type);
        } else if (pre_type instanceof ALittleGuessMap) {
            ALittleGuess value_type = ((ALittleGuessMap) pre_type).value_type;
            if (pre_type.is_const && !value_type.is_const) {
                value_type = value_type.clone();
                value_type.is_const = true;
                value_type.updateValue();
            }
            guess_list.add(value_type);
        }

        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        // 获取父节点
        ALittlePropertyValueSuffix property_value_suffix = (ALittlePropertyValueSuffix) myElement.getParent();
        ALittlePropertyValue property_value = (ALittlePropertyValue) property_value_suffix.getParent();
        ALittlePropertyValueFirstType property_value_first_type = property_value.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffixList = property_value.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffixList.indexOf(property_value_suffix);
        if (index == -1) return;

        // 获取前一个类型
        ALittleGuess pre_type;
        if (index == 0)
            pre_type = property_value_first_type.guessType();
        else
            pre_type = suffixList.get(index - 1).guessType();

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess key_guess_type = value_stat.guessType();

        // 获取类型
        if (pre_type instanceof ALittleGuessList) {
            if (!(key_guess_type instanceof ALittleGuessInt) && !(key_guess_type instanceof ALittleGuessLong))
                throw new ALittleGuessException(value_stat, "索引值的类型必须是int或者是long，不能是:" + key_guess_type.getValue());
        } else if (pre_type instanceof ALittleGuessMap) {
            ALittleGuessMap pre_type_map = (ALittleGuessMap) pre_type;
            try {
                ALittleReferenceOpUtil.guessTypeEqual(((ALittleGuessMap) pre_type).key_type, value_stat, key_guess_type, true, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(error.getElement(), "索引值的类型不能是:" + key_guess_type.getValue() + " :" + error.getError());
            }
        }

        {
            List<ALittleGuess> guess_list = myElement.guessTypes();
            if (guess_list.size() == 0)
                throw new ALittleGuessException(myElement, "该元素不能直接使用[]取值，请先cast");
            else if (guess_list.size() != 1)
                throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
