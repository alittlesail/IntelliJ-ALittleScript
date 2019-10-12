package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignExprReference extends ALittleReference<ALittleVarAssignExpr> {
    public ALittleVarAssignExprReference(@NotNull ALittleVarAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        ALittleValueStat valueStat = myElement.getValueStat();
        if (valueStat == null) return;

        List<ALittleVarAssignDec> pairDecList = myElement.getVarAssignDecList();
        if (pairDecList.isEmpty()) {
            return;
        }

        // 如果返回值只有一个函数调用
        if (pairDecList.size() > 1) {
            // 获取右边表达式的
            List<ALittleGuess> methodCallGuessList = valueStat.guessTypes();
            if (methodCallGuessList.isEmpty()) {
                throw new ALittleGuessException(valueStat, "调用的函数没有返回值");
            }
            boolean hasTail = methodCallGuessList.get(methodCallGuessList.size() - 1) instanceof ALittleGuessReturnTail;
            if (hasTail) {
                // 不需要检查
            } else {
                if (methodCallGuessList.size() < pairDecList.size()) {
                    throw new ALittleGuessException(valueStat, "调用的函数返回值数量少于定义的变量数量");
                }
            }

            for (int i = 0; i < pairDecList.size(); ++i) {
                ALittleVarAssignDec pairDec = pairDecList.get(i);
                if (i >= methodCallGuessList.size()) break;
                if (methodCallGuessList.get(i) instanceof ALittleGuessReturnTail) break;
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(pairDec, pairDec.guessType(), valueStat, methodCallGuessList.get(i));
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(valueStat, "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + e.getError());
                }
            }

            return;
        }

        ALittleGuess pairGuessType = pairDecList.get(0).guessType();
        ALittleGuess valueGuessType = valueStat.guessType();

        try {
            ALittleReferenceOpUtil.guessTypeEqual(pairDecList.get(0), pairGuessType, valueStat, valueGuessType);
        } catch (ALittleGuessException e) {
            throw new ALittleGuessException(e.getElement(), "等号左边的变量和表达式的类型不同:" + e.getError());
        }
    }
}
