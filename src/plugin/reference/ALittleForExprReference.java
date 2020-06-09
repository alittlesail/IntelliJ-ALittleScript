package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleForExprReference extends ALittleReference<ALittleForExpr> {
    public ALittleForExprReference(@NotNull ALittleForExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public void checkError() throws ALittleGuessException {
        ALittleForCondition for_condition = myElement.getForCondition();
        if (for_condition == null) return;

        ALittleForPairDec for_pair_dec = for_condition.getForPairDec();
        if (for_pair_dec == null) return;

        ALittleForStepCondition step_condition = for_condition.getForStepCondition();
        ALittleForInCondition in_condition = for_condition.getForInCondition();
        if (step_condition != null) {
            ALittleForStartStat for_start_stat = step_condition.getForStartStat();
            if (for_start_stat == null) return;

            ALittleGuess start_guess = for_pair_dec.guessType();
            if (!(start_guess instanceof ALittleGuessInt) && !(start_guess instanceof ALittleGuessLong))
                throw new ALittleGuessException(for_pair_dec.getVarAssignNameDec(), "这个变量必须是int或long类型");

            ALittleValueStat value_stat = for_start_stat.getValueStat();
            if (value_stat == null)
                throw new ALittleGuessException(for_pair_dec.getVarAssignNameDec(), "没有初始化表达式");

            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "等号右边的表达式类型必须只能是一个返回值");

            ALittleGuess guess = value_stat.guessType();
            if (!(guess instanceof ALittleGuessInt) && !(guess instanceof ALittleGuessLong) && !(guess instanceof ALittleGuessDouble))
                throw new ALittleGuessException(value_stat, "等号右边的表达式类型必须是int,long,double 不能是:" + guess.getValue());

            // 结束表达式
            ALittleForEndStat end_stat = step_condition.getForEndStat();
            ALittleForStepStat step_stat = step_condition.getForStepStat();

            if (end_stat == null || end_stat.getValueStat() == null)
                throw new ALittleGuessException(myElement, "必须有结束表达式");
            if (step_stat == null || step_stat.getValueStat() == null)
                throw new ALittleGuessException(myElement, "必须有步长表达式");

            ALittleGuess end_guess = end_stat.getValueStat().guessType();
            if (!(end_guess instanceof ALittleGuessBool))
                throw new ALittleGuessException(end_stat, "for的结束条件表达式类型必须是bool, 不能是:" + end_guess.getValue());

            // 返回值
            result = PsiHelper.calcReturnCount(step_stat.getValueStat());
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "for的步长条件表达式类型必须只能是一个返回值");

            ALittleGuess step_guess = step_stat.getValueStat().guessType();
            if (!(step_guess instanceof ALittleGuessInt) && !(step_guess instanceof ALittleGuessDouble) && !(step_guess instanceof ALittleGuessLong))
                throw new ALittleGuessException(step_stat, "for的步长条件表达式类型必须是int,double,long, 不能是:" + end_guess.getValue());
        } else if (in_condition != null) {
            ALittleValueStat value_stat = in_condition.getValueStat();
            if (value_stat == null) return;

            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            int return_count = result.getFirst();
            if (PsiHelper.isPairsFunction(result.getSecond()))
                return_count = 1;
            if (return_count != 1) throw new ALittleGuessException(value_stat, "for的遍历对象必须只能是一个返回值");

            List<ALittleForPairDec> src_pair_dec_list = in_condition.getForPairDecList();
            List<ALittleForPairDec> pair_dec_list = new ArrayList<>(src_pair_dec_list);
            pair_dec_list.add(0, for_pair_dec);
            List<ALittleGuess> guess_list = value_stat.guessTypes();

            // 检查List
            if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessList) {
                ALittleGuessList guess = (ALittleGuessList) guess_list.get(0);

                // for变量必须是2个
                if (pair_dec_list.size() != 2)
                    throw new ALittleGuessException(in_condition, "这里参数数量必须是2个");

                // 第一个参数必须是 int或者long
                ALittleGuess key_guess_type = pair_dec_list.get(0).guessType();
                if (!(key_guess_type instanceof ALittleGuessInt) && !(key_guess_type instanceof ALittleGuessLong))
                    throw new ALittleGuessException(pair_dec_list.get(0), "这个变量必须是int或long类型");

                // 第二个参数必须和List元素相等
                ALittleGuess value_guess_type = pair_dec_list.get(1).guessType();
                ALittleGuess sub_type = guess.sub_type;
                if (guess_list.get(0).is_const && !sub_type.is_const) {
                    sub_type = sub_type.clone();
                    sub_type.is_const = true;
                    sub_type.updateValue();
                }
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(sub_type, pair_dec_list.get(1), value_guess_type, false, false);
                } catch (ALittleGuessException error) {
                    throw new ALittleGuessException(error.getElement(), "变量格式错误，不能是:" + value_guess_type.getValue() + " :" + error.getError());
                }
                return;
            }

            // 检查Map
            if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessMap) {
                ALittleGuessMap guess_map = (ALittleGuessMap) guess_list.get(0);

                // for变量必须是2个
                if (pair_dec_list.size() != 2)
                    throw new ALittleGuessException(in_condition, "这里参数数量必须是2个");

                // 第一个参数必须和Map的key元素相等
                ALittleGuess key_guess_type = pair_dec_list.get(0).guessType();
                ALittleGuess map_key_type = guess_map.key_type;
                if (guess_list.get(0).is_const && !map_key_type.is_const) {
                    map_key_type = map_key_type.clone();
                    map_key_type.is_const = true;
                    map_key_type.updateValue();
                }
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(map_key_type, pair_dec_list.get(0), key_guess_type, false, false);
                } catch (ALittleGuessException error) {
                    throw new ALittleGuessException(error.getElement(), "key变量格式错误，不能是:" + key_guess_type.getValue() + " :" + error.getError());
                }

                // 第二个参数必须和Map的value元素相等
                ALittleGuess value_guess_type = pair_dec_list.get(1).guessType();
                ALittleGuess map_value_type = guess_map.value_type;
                if (guess_list.get(0).is_const && !map_value_type.is_const) {
                    map_value_type = map_value_type.clone();
                    map_value_type.is_const = true;
                    map_value_type.updateValue();
                }
                try {
                    ALittleReferenceOpUtil.guessTypeEqual(map_value_type, pair_dec_list.get(1), value_guess_type, false, false);
                } catch (ALittleGuessException error) {
                    throw new ALittleGuessException(error.getElement(), "value变量格式错误，不能是:" + value_guess_type.getValue() + " :" + error.getError());
                }
                return;
            }

            // 检查迭代函数
            if (PsiHelper.isPairsFunction(guess_list)) return;

            throw new ALittleGuessException(value_stat, "遍历对象类型必须是List,Map或者迭代函数");
        }
    }
}
