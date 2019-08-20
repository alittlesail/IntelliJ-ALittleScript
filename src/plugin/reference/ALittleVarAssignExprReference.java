package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
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
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        List<ALittleVarAssignDec> pairDecList = myElement.getVarAssignDecList();
        if (pairDecList.isEmpty()) {
            return;
        }

        // 如果返回值只有一个函数调用
        if (pairDecList.size() > 1) {
            // 获取右边表达式的
            List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = value_stat.guessTypes();
            if (methodCallGuessList.isEmpty()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数没有返回值");
            }
            if (methodCallGuessList.size() < pairDecList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数返回值数量少于定义的变量数量");
            }

            for (int i = 0; i < pairDecList.size(); ++i) {
                ALittleVarAssignDec pairDec = pairDecList.get(i);
                try {
                    boolean result = ALittleReferenceOpUtil.guessTypeEqual(pairDec, pairDec.guessType(), value_stat, methodCallGuessList.get(i));
                    if (!result) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(pairDec, "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等");
                    }
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + e.getError());
                }
            }

            return;
        }

        ALittleReferenceUtil.GuessTypeInfo pairGuessType = pairDecList.get(0).guessType();
        ALittleReferenceUtil.GuessTypeInfo valueGuessType = value_stat.guessType();

        try {
            boolean result = ALittleReferenceOpUtil.guessTypeEqual(pairDecList.get(0), pairGuessType, value_stat, valueGuessType);
            if (!result) {
                throw new ALittleReferenceUtil.ALittleReferenceException(pairDecList.get(0), "等号左边的变量和表达式的类型不同");
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的变量和表达式的类型不同:" + e.getError());
        }
    }
}
