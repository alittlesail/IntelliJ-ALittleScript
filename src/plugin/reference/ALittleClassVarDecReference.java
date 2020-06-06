package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessList;
import plugin.guess.ALittleGuessMap;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleClassVarDecReference extends ALittleReference<ALittleClassVarDec> {
    public ALittleClassVarDecReference(@NotNull ALittleClassVarDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleAllType all_type = myElement.getAllType();
        if (all_type != null)
        {
            List<ALittleGuess> guess_list = all_type.guessTypes();
            ALittleClassElementDec class_element_dec = (ALittleClassElementDec)myElement.getParent();
            if (class_element_dec == null) throw new ALittleGuessException(myElement, "父节点不是ALittleScriptClassElementDecElement类型");

            boolean is_native = PsiHelper.isNative(class_element_dec.getModifierList());
            for (int i = 0; i < guess_list.size(); ++i)
            {
                ALittleGuessList guess = (ALittleGuessList)guess_list.get(i);
                if (guess != null && guess.is_native != is_native)
                {
                    guess.is_native = is_native;
                    guess.updateValue();
                }
            }
        }

        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleGuess> guess_list = myElement.guessTypes();
        if (guess_list.size() == 0)
            throw new ALittleGuessException(myElement, "未知类型");
        else if (guess_list.size() != 1)
            throw new ALittleGuessException(myElement, "重复定义");

        // 检查赋值表达式
        ALittleClassVarValueDec value_dec = myElement.getClassVarValueDec();
        if (value_dec != null)
        {
            ALittleConstValue const_value = value_dec.getConstValue();
            if (const_value != null)
            {
                ALittleGuess guess = const_value.guessType();

                try {
                    ALittleReferenceOpUtil.guessTypeEqual(guess_list.get(0), const_value, guess, true, false);
                } catch (ALittleGuessException error) {
                    throw new ALittleGuessException(error.getElement(), "等号左边的变量和表达式的类型不同:" + error.getError());
                }

            }

            ALittleOpNewStat op_new_stat = value_dec.getOpNewStat();
            if (op_new_stat != null)
            {
                ALittleGuess guess = op_new_stat.guessType();
                if (!(guess instanceof ALittleGuessList) && !(guess instanceof ALittleGuessMap))
                    throw new ALittleGuessException(op_new_stat, "成员变量初始化只能赋值List或者Map或者常量");

                try {
                    ALittleReferenceOpUtil.guessTypeEqual(guess_list.get(0), op_new_stat, guess, true, false);
                } catch (ALittleGuessException error) {
                    throw new ALittleGuessException(error.getElement(), "等号左边的变量和表达式的类型不同:" + error.getError());
                }
            }
        }
    }
}
