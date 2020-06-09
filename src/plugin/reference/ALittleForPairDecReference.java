package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleForPairDecReference extends ALittleReference<ALittleForPairDec> {
    public ALittleForPairDecReference(@NotNull ALittleForPairDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {// 如果有定义类型
        ALittleAllType all_type = myElement.getAllType();
        if (all_type != null) return all_type.guessTypes();

        // 如果没有定义类型
        PsiElement parent = myElement.getParent();
        if (parent instanceof ALittleForCondition) {
            ALittleForCondition for_condition = (ALittleForCondition) parent;
            ALittleForStepCondition step_condition = for_condition.getForStepCondition();
            if (step_condition != null) {
                ALittleForStartStat start_stat = step_condition.getForStartStat();
                if (start_stat != null) {
                    ALittleValueStat value_stat = start_stat.getValueStat();
                    if (value_stat != null)
                        return value_stat.guessTypes();
                }
            } else {
                parent = for_condition.getForInCondition();
            }
        }

        List<ALittleGuess> guess_list = new ArrayList<>();
        if (parent instanceof ALittleForInCondition) {
            ALittleForInCondition in_condition = (ALittleForInCondition) parent;
            // 取出遍历的对象
            ALittleValueStat value_stat = in_condition.getValueStat();
            if (value_stat == null)
                throw new ALittleGuessException(myElement, "For没有遍历对象，无法推导类型");

            // 获取定义列表
            List<ALittleForPairDec> pair_dec_list = in_condition.getForPairDecList();
            // 查找是第几个，如果没有找到，那么就是第0个，如果有找到那就+1
            int index = pair_dec_list.indexOf(myElement);
            if (index < 0)
                index = 0;
            else
                index += 1;
            // 获取循环对象的类型
            List<ALittleGuess> value_guess_list = value_stat.guessTypes();
            // 处理List
            if (value_guess_list.size() == 1 && value_guess_list.get(0) instanceof ALittleGuessList) {
                // 对于List的key使用auto，那么就默认是int类型
                if (index == 0) {
                    if (value_guess_list.get(0).is_const) {
                        List<ALittleGuess> temp_guess_list = ALittleGuessPrimitive.sPrimitiveGuessListMap.get("const int");
                        if (temp_guess_list != null) guess_list = temp_guess_list;
                    } else {
                        List<ALittleGuess> temp_guess_list = ALittleGuessPrimitive.sPrimitiveGuessListMap.get("int");
                        if (temp_guess_list != null) guess_list = temp_guess_list;
                    }
                    return guess_list;
                } else if (index == 1) {
                    ALittleGuess sub_type = ((ALittleGuessList) value_guess_list.get(0)).sub_type;
                    if (value_guess_list.get(0).is_const && !sub_type.is_const) {
                        sub_type = sub_type.clone();
                        sub_type.is_const = true;
                        sub_type.updateValue();
                    }
                    guess_list.add(sub_type);
                }
            }
            // 处理Map
            else if (value_guess_list.size() == 1 && value_guess_list.get(0) instanceof ALittleGuessMap) {
                // 如果是key，那么就取key的类型
                if (index == 0) {
                    ALittleGuess key_type = ((ALittleGuessMap) value_guess_list.get(0)).key_type;
                    if (value_guess_list.get(0).is_const && !key_type.is_const) {
                        key_type = key_type.clone();
                        key_type.is_const = true;
                        key_type.updateValue();
                    }
                    guess_list.add(key_type);
                }
                // 如果是value，那么就取value的类型
                else if (index == 1) {
                    ALittleGuess value_type = ((ALittleGuessMap) value_guess_list.get(0)).value_type;
                    if (value_guess_list.get(0).is_const && !value_type.is_const) {
                        value_type = value_type.clone();
                        value_type.is_const = true;
                        value_type.updateValue();
                    }
                    guess_list.add(value_type);
                }
            }
            // 如果是pairs函数
            else if (PsiHelper.isPairsFunction(value_guess_list)) {
                guess_list.add(value_guess_list.get(2));
            }
        }

        return guess_list;
    }
}
