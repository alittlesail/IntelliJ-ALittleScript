package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessFunctor;
import plugin.psi.ALittleBindStat;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleBindStatReference extends ALittleReference<ALittleBindStat> {
    public ALittleBindStatReference(@NotNull ALittleBindStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "bind表达式不能没有参数");
        }

        // 第一个参数必须是函数
        ALittleValueStat valueStat = valueStatList.get(0);
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "bind表达式第一个参数必须是一个函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        // 开始构建类型
        ALittleGuessFunctor info = new ALittleGuessFunctor(myElement);
        info.functorAwait = guessFunctor.functorAwait;
        info.functorParamList.addAll(guessFunctor.functorParamList);
        info.functorParamNameList.addAll(guessFunctor.functorParamNameList);
        info.functorParamTail = guessFunctor.functorParamTail;
        info.functorReturnList.addAll(guessFunctor.functorReturnList);
        info.functorReturnTail = guessFunctor.functorReturnTail;
        // 移除掉已填写的参数
        int paramCount = valueStatList.size() - 1;
        while (paramCount > 0
                && info.functorParamList.size() > 0
                && info.functorParamNameList.size() > 0) {
            info.functorParamList.remove(0);
            info.functorParamNameList.remove(0);
            --paramCount;
        }
        info.UpdateValue();

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "bind表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "bind表达式第一个参数必须是一个函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (valueStatList.size() - 1 > guessFunctor.functorParamList.size()) {
            if (guessFunctor.functorParamTail == null) {
                throw new ALittleGuessException(myElement, "bind表达式参数太多了");
            } else {
                throw new ALittleGuessException(myElement, "bind表达式参数太多了，即使被bind的函数定义的参数占位符(...)也不行!");
            }
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < valueStatList.size(); ++i) {
            ALittleGuess paramGuess = guessFunctor.functorParamList.get(i - 1);
            ALittleValueStat paramValueStat = valueStatList.get(i);
            try {
                ALittleReferenceOpUtil.guessTypeEqual(myElement, paramGuess, paramValueStat, paramValueStat.guessType());
            } catch (ALittleGuessException e) {
                throw new ALittleGuessException(paramValueStat, "第" + i + "个参数类型和函数定义的参数类型不同:" + e.getError());
            }
        }
    }
}
