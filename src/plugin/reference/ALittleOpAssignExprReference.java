package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.List;

public class ALittleOpAssignExprReference extends ALittleReference<ALittleOpAssignExpr> {
    public ALittleOpAssignExprReference(@NotNull ALittleOpAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        List<ALittlePropertyValue> property_value_list = myElement.getPropertyValueList();
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) {
            if (property_value_list.size() != 1)
                throw new ALittleGuessException(myElement, "没有赋值表达式时，只能是一个函数调用");
            ALittlePropertyValue property_value = property_value_list.get(0);
            List<ALittlePropertyValueSuffix> suffix_list = property_value.getPropertyValueSuffixList();
            if (suffix_list.size() == 0)
                throw new ALittleGuessException(myElement, "没有赋值表达式时，只能是一个函数调用");
            ALittlePropertyValueSuffix suffix = suffix_list.get(suffix_list.size() - 1);
            if (suffix.getPropertyValueMethodCall() == null)
                throw new ALittleGuessException(myElement, "没有赋值表达式时，只能是一个函数调用");
            return;
        }

        if (property_value_list.size() == 0) return;

        // 如果返回值只有一个函数调用
        if (property_value_list.size() > 1) {
            if (value_stat == null)
                throw new ALittleGuessException(myElement, "调用的函数没有返回值");
            // 获取右边表达式的
            List<ALittleGuess> method_call_guess_list = value_stat.guessTypes();
            if (method_call_guess_list.size() == 0)
                throw new ALittleGuessException(value_stat, "调用的函数没有返回值");

            boolean hasTail = method_call_guess_list.get(method_call_guess_list.size() - 1) instanceof ALittleGuessReturnTail;
            if (hasTail) {
                // 不做检查
            } else {
                if (method_call_guess_list.size() < property_value_list.size())
                    throw new ALittleGuessException(value_stat, "调用的函数返回值数量少于定义的变量数量");
            }

            for (int i = 0; i < property_value_list.size(); ++i) {
                ALittlePropertyValue pair_dec = property_value_list.get(i);
                if (i >= method_call_guess_list.size()) break;
                if (method_call_guess_list.get(i) instanceof ALittleGuessReturnTail) break;
                ALittleGuess pair_dec_guess = pair_dec.guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(pair_dec_guess, value_stat, method_call_guess_list.get(i), true, false);
                } catch (ALittleGuessException guess_error) {
                    throw new ALittleGuessException(value_stat, "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + guess_error.getError());
                }
            }

            return;
        }

        ALittleOpAssign op_assign = myElement.getOpAssign();
        if (op_assign == null)
            throw new ALittleGuessException(myElement, "没有赋值符号");
        String op_string = op_assign.getText();

        ALittleGuess pair_guess = property_value_list.get(0).guessType();
        ALittleGuess value_guess = value_stat.guessType();
        if (pair_guess instanceof ALittleGuessTemplate) {
            if (!pair_guess.getValue().equals(value_guess.getValue()) && !value_guess.getValue().equals("null"))
                throw new ALittleGuessException(value_stat, "等号左边的变量和表达式的类型不同");
        }

        if (op_string.equals("=")) {
            try {
                ALittleReferenceOpUtil.guessTypeEqual(pair_guess, value_stat, value_guess, true, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(error.getElement(), "等号左边的变量和表达式的类型不同:" + error.getError());
            }
        } else {
            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);

            if (result.getFirst() != 1)
                throw new ALittleGuessException(value_stat, op_string + "右边必须只能是一个返回值");

            if (pair_guess.is_const)
                throw new ALittleGuessException(property_value_list.get(0), "const类型不能使用" + op_string + "运算符");

            if (!(pair_guess instanceof ALittleGuessInt) && !(pair_guess instanceof ALittleGuessDouble) && !(pair_guess instanceof ALittleGuessLong))
                throw new ALittleGuessException(property_value_list.get(0), op_string + "左边必须是int, double, long");

            if (!(value_guess instanceof ALittleGuessInt) && !(value_guess instanceof ALittleGuessDouble) && !(value_guess instanceof ALittleGuessLong))
                throw new ALittleGuessException(value_stat, op_string + "右边必须是int, double, long");
        }
    }
}
