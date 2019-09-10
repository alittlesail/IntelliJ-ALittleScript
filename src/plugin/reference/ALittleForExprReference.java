package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleForExprReference extends ALittleReference<ALittleForExpr> {
    public ALittleForExprReference(@NotNull ALittleForExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleForStepCondition stepExpr = myElement.getForStepCondition();
        ALittleForInCondition inExpr = myElement.getForInCondition();
        if (stepExpr != null) {
            if (stepExpr.getForStartStat() == null) return;

            ALittleReferenceUtil.GuessTypeInfo startGuessType = stepExpr.getForStartStat().getForPairDec().guessType();
            if (!startGuessType.value.equals("int") && startGuessType.value.equals("I64")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(stepExpr.getForStartStat().getForPairDec(), "这个变量必须是int或I64类型");
            }

            ALittleValueStat valueStat = stepExpr.getForStartStat().getValueStat();
            if (valueStat == null) {
                throw new ALittleReferenceUtil.ALittleReferenceException(stepExpr.getForStartStat(), "没有初始化表达式");
            }

            ALittleReferenceUtil.GuessTypeInfo guessType = valueStat.guessType();
            if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "等号右边的表达式类型必须是int,I64,double 不能是:" + guessType.value);
            }

            // 结束表达式
            ALittleForEndStat endStat = stepExpr.getForEndStat();
            ALittleForStepStat stepStat = stepExpr.getForStepStat();
            if (endStat != null) {
                guessType = endStat.getValueStat().guessType();
                if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(endStat, "for的结束条件表达式类型必须是int,I64,double, 不能是:" + guessType.value);
                }
            }

            // 步长表达式
            if (stepStat != null) {
                guessType = stepStat.getValueStat().guessType();
                if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(stepStat, "for的步长条件表达式类型必须是int,I64,double, 不能是:" + guessType.value);
                }
            }
        } else if (inExpr != null) {
            ALittleValueStat valueStat = inExpr.getValueStat();
            if (valueStat == null) return;
            List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();

            // 如果for的对象是any，那么就放过，不检查了
            List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = valueStat.guessTypes();

            // 检查List
            if (guessTypeList.size() == 1 && guessTypeList.get(0).type == ALittleReferenceUtil.GuessType.GT_LIST) {
                if (pairDecList.size() != 2) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(inExpr, "这里参数数量必须是2个");
                }

                ALittleReferenceUtil.GuessTypeInfo keyGuessType = pairDecList.get(0).guessType();
                if (!keyGuessType.value.equals("int") && !keyGuessType.value.equals("I64")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(pairDecList.get(0), "这个变量必须是int或I64类型");
                }

                ALittleReferenceUtil.GuessTypeInfo valueGuessType = pairDecList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guessTypeList.get(0).listSubType, pairDecList.get(1), valueGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            // 检查Map
            if (guessTypeList.size() == 1 && guessTypeList.get(0).type == ALittleReferenceUtil.GuessType.GT_MAP) {
                if (pairDecList.size() != 2) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(inExpr, "这里参数数量必须是2个");
                }

                ALittleReferenceUtil.GuessTypeInfo keyGuessType = pairDecList.get(0).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guessTypeList.get(0).mapKeyType, pairDecList.get(0), keyGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "key变量格式错误，不能是:" + keyGuessType.value + " :" + e.getError());
                }

                ALittleReferenceUtil.GuessTypeInfo valueGuessType = pairDecList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(valueStat, guessTypeList.get(0).mapValueType, pairDecList.get(1), valueGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "value变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            // 检查迭代函数
            do {
                if (guessTypeList.size() != 3) break;
                if (guessTypeList.get(0).type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) break;
                if (guessTypeList.get(0).functorAwait) break;
                if (guessTypeList.get(0).functorParamList.size() != 2) break;
                if (guessTypeList.get(0).functorReturnList.size() != 0) break;
                if (!guessTypeList.get(0).functorParamList.get(0).value.equals(guessTypeList.get(1).value)) break;
                if (!guessTypeList.get(0).functorParamList.get(1).value.equals(guessTypeList.get(2).value)) break;
                return;
            } while (false);

            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "遍历对象类型必须是List,Map或者迭代函数");
        }
    }
}
