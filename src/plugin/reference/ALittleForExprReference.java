package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessList;
import plugin.guess.ALittleGuessMap;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleForExprReference extends ALittleReference<ALittleForExpr> {
    public ALittleForExprReference(@NotNull ALittleForExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        ALittleForStepCondition stepExpr = myElement.getForStepCondition();
        ALittleForInCondition inExpr = myElement.getForInCondition();
        if (stepExpr != null) {
            if (stepExpr.getForStartStat() == null) return;

            ALittleGuess startGuess = stepExpr.getForStartStat().getForPairDec().guessType();
            if (!startGuess.value.equals("int") && startGuess.value.equals("I64")) {
                throw new ALittleGuessException(stepExpr.getForStartStat().getForPairDec(), "这个变量必须是int或I64类型");
            }

            ALittleValueStat valueStat = stepExpr.getForStartStat().getValueStat();
            if (valueStat == null) {
                throw new ALittleGuessException(stepExpr.getForStartStat(), "没有初始化表达式");
            }

            ALittleGuess guess = valueStat.guessType();
            if (!guess.value.equals("int") && !guess.value.equals("I64") && !guess.value.equals("double")) {
                throw new ALittleGuessException(valueStat, "等号右边的表达式类型必须是int,I64,double 不能是:" + guess.value);
            }

            // 结束表达式
            ALittleForEndStat endStat = stepExpr.getForEndStat();
            ALittleForStepStat stepStat = stepExpr.getForStepStat();
            if (endStat != null) {
                ALittleGuess endGuess = endStat.getValueStat().guessType();
                if (!endGuess.value.equals("int") && !endGuess.value.equals("I64") && !endGuess.value.equals("double")) {
                    throw new ALittleGuessException(endStat, "for的结束条件表达式类型必须是int,I64,double, 不能是:" + endGuess.value);
                }
            }

            // 步长表达式
            if (stepStat != null) {
                ALittleGuess stepGuess = stepStat.getValueStat().guessType();
                if (!stepGuess.value.equals("int") && !stepGuess.value.equals("I64") && !stepGuess.value.equals("double")) {
                    throw new ALittleGuessException(stepStat, "for的步长条件表达式类型必须是int,I64,double, 不能是:" + stepGuess.value);
                }
            }
        } else if (inExpr != null) {
            ALittleValueStat valueStat = inExpr.getValueStat();
            if (valueStat == null) return;
            List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();

            // 如果for的对象是any，那么就放过，不检查了
            List<ALittleGuess> guessList = valueStat.guessTypes();

            // 检查List
            if (guessList.size() == 1 && guessList.get(0) instanceof ALittleGuessList) {
                ALittleGuessList guess = (ALittleGuessList)guessList.get(0);

                // for变量必须是2个
                if (pairDecList.size() != 2) {
                    throw new ALittleGuessException(inExpr, "这里参数数量必须是2个");
                }

                // 第一个参数必须是 int或者I64
                ALittleGuess keyGuessType = pairDecList.get(0).guessType();
                if (!keyGuessType.value.equals("int") && !keyGuessType.value.equals("I64")) {
                    throw new ALittleGuessException(pairDecList.get(0), "这个变量必须是int或I64类型");
                }

                // 第二个参数必须和List元素相等
                ALittleGuess valueGuessType = pairDecList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guess.subType, pairDecList.get(1), valueGuessType);
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(e.getElement(), "变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            // 检查Map
            if (guessList.size() == 1 && guessList.get(0) instanceof ALittleGuessMap) {
                ALittleGuessMap guessMap = (ALittleGuessMap)guessList.get(0);

                // for变量必须是2个
                if (pairDecList.size() != 2) {
                    throw new ALittleGuessException(inExpr, "这里参数数量必须是2个");
                }

                // 第一个参数必须和Map的key元素相等
                ALittleGuess keyGuessType = pairDecList.get(0).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guessMap.keyType, pairDecList.get(0), keyGuessType);
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(e.getElement(), "key变量格式错误，不能是:" + keyGuessType.value + " :" + e.getError());
                }

                // 第二个参数必须和Map的key元素相等
                ALittleGuess valueGuessType = pairDecList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guessMap.valueType, pairDecList.get(1), valueGuessType);
                } catch (ALittleGuessException e) {
                    throw new ALittleGuessException(e.getElement(), "value变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            // 检查迭代函数
            if (ALittleReferenceUtil.IsPairsFunction(guessList)) return;

            throw new ALittleGuessException(valueStat, "遍历对象类型必须是List,Map或者迭代函数");
        }
    }
}
