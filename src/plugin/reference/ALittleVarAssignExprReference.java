package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignExprReference extends ALittleReference<ALittleVarAssignExpr> {
    public ALittleVarAssignExprReference(@NotNull ALittleVarAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStat valueStat = myElement.getValueStat();
        if (valueStat == null) return;

        List<ALittleVarAssignDec> pairDecList = myElement.getVarAssignDecList();
        if (pairDecList.isEmpty()) {
            return;
        }

        // 如果返回值只有一个函数调用
        if (pairDecList.size() > 1) {
            // 获取右边表达式的
            List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = valueStat.guessTypes();
            if (methodCallGuessList.isEmpty()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "调用的函数没有返回值");
            }
            if (methodCallGuessList.size() < pairDecList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "调用的函数返回值数量少于定义的变量数量");
            }

            for (int i = 0; i < pairDecList.size(); ++i) {
                ALittleVarAssignDec pairDec = pairDecList.get(i);
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(pairDec, pairDec.guessType(), valueStat, methodCallGuessList.get(i));
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + e.getError());
                }
            }

            return;
        }

        ALittleReferenceUtil.GuessTypeInfo pairGuessType = pairDecList.get(0).guessType();
        ALittleReferenceUtil.GuessTypeInfo valueGuessType = valueStat.guessType();

        try {
            ALittleReferenceOpUtil.guessTypeEqual(pairDecList.get(0), pairGuessType, valueStat, valueGuessType);
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的变量和表达式的类型不同:" + e.getError());
        }
    }
}
