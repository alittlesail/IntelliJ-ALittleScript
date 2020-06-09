package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClassTemplate;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignExprReference extends ALittleReference<ALittleVarAssignExpr> {
    public ALittleVarAssignExprReference(@NotNull ALittleVarAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        List<ALittleVarAssignDec> pair_dec_list = myElement.getVarAssignDecList();
        if (pair_dec_list.size() == 0) return;

        // 如果返回值只有一个函数调用
        if (pair_dec_list.size() > 1)
        {
            // 获取右边表达式的
            List<ALittleGuess> method_call_guess_list = value_stat.guessTypes();
            if (method_call_guess_list.size() == 0)
                throw new ALittleGuessException(value_stat, "调用的函数没有返回值");
            boolean has_tail = method_call_guess_list.get(method_call_guess_list.size() - 1) instanceof ALittleGuessReturnTail;
            if (has_tail)
            {
                // 不需要检查
            }
            else
            {
                if (method_call_guess_list.size() < pair_dec_list.size())
                    throw new ALittleGuessException(value_stat, "调用的函数返回值数量少于定义的变量数量");
            }

            for (int i = 0; i < pair_dec_list.size(); ++i)
            {
                ALittleVarAssignDec pair_dec = pair_dec_list.get(i);
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

        ALittleGuess pair_guess = pair_dec_list.get(0).guessType();
        ALittleGuess value_guess = value_stat.guessType();

        try {
            ALittleReferenceOpUtil.guessTypeEqual(pair_guess, value_stat, value_guess, true, false);
        } catch (ALittleGuessException error) {
            throw new ALittleGuessException(error.getElement(), "等号左边的变量和表达式的类型不同:" + error.getError());
        }
    }
}
