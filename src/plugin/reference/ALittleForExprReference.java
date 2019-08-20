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
        ALittleForStepCondition step_expr = myElement.getForStepCondition();
        ALittleForInCondition in_expr = myElement.getForInCondition();
        if (step_expr != null) {
            if (step_expr.getForStartStat() == null) return;

            ALittleReferenceUtil.GuessTypeInfo startGuessType = step_expr.getForStartStat().getForPairDec().guessType();
            if (!startGuessType.value.equals("int") && startGuessType.value.equals("I64")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(step_expr.getForStartStat().getForPairDec(), "这个变量必须是int或I64类型");
            }

            ALittleValueStat value_stat = step_expr.getForStartStat().getValueStat();
            if (value_stat == null) {
                throw new ALittleReferenceUtil.ALittleReferenceException(step_expr.getForStartStat(), "没有初始化");
            }

            ALittleReferenceUtil.GuessTypeInfo guessType = value_stat.guessType();
            if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double") && !guessType.value.equals("any")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "等号右边的表达式类型必须是int,I64,double,any, 不能是:" + guessType.value);
            }

            // 结束表达式
            ALittleForEndStat endStat = step_expr.getForEndStat();
            ALittleForStepStat stepStat = step_expr.getForStepStat();
            if (endStat != null) {
                guessType = endStat.getValueStat().guessType();
                if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double") && !guessType.value.equals("any")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(endStat, "for的结束条件表达式类型必须是int,I64,double,any, 不能是:" + guessType.value);
                }
            }

            // 步长表达式
            if (stepStat != null) {
                guessType = stepStat.getValueStat().guessType();
                if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double") && !guessType.value.equals("any")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(stepStat, "for的步长条件表达式类型必须是int,I64,double,any, 不能是:" + guessType.value);
                }
            }
        } else if (in_expr != null) {
            ALittleValueStat value_stat = in_expr.getValueStat();
            if (value_stat == null) return;

            ALittleReferenceUtil.GuessTypeInfo guessType = value_stat.guessType();
            if (guessType.value.equals("any")) {
                return;
            }

            List<ALittleForPairDec> pair_decList = in_expr.getForPairDecList();
            if (guessType.type == ALittleReferenceUtil.GuessType.GT_LIST) {
                if (pair_decList.size() != 2) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(in_expr, "这里参数数量必须是2个");
                }

                ALittleReferenceUtil.GuessTypeInfo keyGuessType = pair_decList.get(0).guessType();
                if (!keyGuessType.value.equals("int") && !keyGuessType.value.equals("I64")) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(pair_decList.get(0), "这个变量必须是int或I64类型");
                }

                ALittleReferenceUtil.GuessTypeInfo valueGuessType = pair_decList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(value_stat, guessType.listSubType, pair_decList.get(1), valueGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            if (guessType.type == ALittleReferenceUtil.GuessType.GT_MAP) {
                if (pair_decList.size() != 2) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(in_expr, "这里参数数量必须是2个");
                }

                ALittleReferenceUtil.GuessTypeInfo keyGuessType = pair_decList.get(0).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(value_stat, guessType.mapKeyType, pair_decList.get(0), keyGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "key变量格式错误，不能是:" + keyGuessType.value + " :" + e.getError());
                }


                ALittleReferenceUtil.GuessTypeInfo valueGuessType = pair_decList.get(1).guessType();
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(value_stat, guessType.mapValueType, pair_decList.get(1), valueGuessType);
                } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "value变量格式错误，不能是:" + valueGuessType.value + " :" + e.getError());
                }

                return;
            }

            throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "遍历对象类型必须是List,Map,any");
        }
    }
}
