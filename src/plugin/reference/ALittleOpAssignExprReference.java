package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpAssignExprReference extends ALittleReference<ALittleOpAssignExpr> {
    public ALittleOpAssignExprReference(@NotNull ALittleOpAssignExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        List<ALittlePropertyValue> propertyValueList = myElement.getPropertyValueList();
        if (propertyValueList.isEmpty()) {
            return;
        }

        // 如果返回值只有一个函数调用
        if (propertyValueList.size() > 1) {
            // 获取右边表达式的
            List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = value_stat.guessTypes();
            if (methodCallGuessList.isEmpty()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数没有返回值");
            }
            if (methodCallGuessList.size() < propertyValueList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "调用的函数返回值数量少于定义的变量数量");
            }

            for (int i = 0; i < propertyValueList.size(); ++i) {
                ALittlePropertyValue pairDec = propertyValueList.get(i);
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(pairDec, pairDec.guessType(), value_stat, methodCallGuessList.get(i));
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的第" + (i + 1) + "个变量数量和函数定义的返回值类型不相等:" + e.getError());
                }
            }

            return;
        }

        ALittleReferenceUtil.GuessTypeInfo pairGuessType = propertyValueList.get(0).guessType();
        ALittleReferenceUtil.GuessTypeInfo valueGuessType = value_stat.guessType();

        try {
            ALittleReferenceOpUtil.guessTypeEqual(propertyValueList.get(0), pairGuessType, value_stat, valueGuessType);
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "等号左边的变量和表达式的类型不同:" + e.getError());
        }
    }
}