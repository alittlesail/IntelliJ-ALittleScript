package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessFunctor;
import plugin.guess.ALittleGuessPrimitive;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleTcallStatReference extends ALittleReference<ALittleTcallStat> {
    public ALittleTcallStatReference(@NotNull ALittleTcallStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = null;
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "tcall表达式不能没有参数");

        // 第一个参数必须是函数
        ALittleValueStat value_stat = value_stat_list.get(0);
        ALittleGuess guess = value_stat.guessType();
        if (!(guess instanceof ALittleGuessFunctor))
        throw new ALittleGuessException(value_stat, "tcall表达式第一个参数必须是一个函数");

        ALittleGuessFunctor guess_functor = (ALittleGuessFunctor)guess;
        if (guess_functor.template_param_list.size() > 0)
            throw new ALittleGuessException(value_stat, "tcall表达式要绑定的函数不能有模板定义");

        guess_list = new ArrayList<>();
        guess_list.add(ALittleGuessPrimitive.sStringGuess);
        guess_list.addAll(guess_functor.return_list);
        if (guess_functor.return_tail != null)
            guess_list.add(guess_functor.return_tail);

        return guess_list;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "tcall表达式不能没有参数");

        // 第一个参数必须是函数
        ALittleValueStat value_stat = value_stat_list.get(0);

        Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
        if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

        ALittleGuess guess = value_stat.guessType();
        if (!(guess instanceof ALittleGuessFunctor))
        throw new ALittleGuessException(value_stat, "tcall表达式第一个参数必须是一个函数");

        ALittleGuessFunctor guess_functor = (ALittleGuessFunctor)guess;
        if (guess_functor.template_param_list.size() > 0)
            throw new ALittleGuessException(value_stat, "tcall表达式要绑定的函数不能有模板定义");

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (value_stat_list.size() - 1 > guess_functor.param_list.size())
        {
            if (guess_functor.param_tail == null)
                throw new ALittleGuessException(myElement, "tcall表达式参数太多了");
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < value_stat_list.size(); ++i)
        {
            if (i - 1 >= guess_functor.param_list.size()) break;
            ALittleGuess param_guess = guess_functor.param_list.get(i - 1);
            ALittleValueStat param_value_stat = value_stat_list.get(i);

            result = PsiHelper.calcReturnCount(param_value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(param_value_stat, "表达式必须只能是一个返回值");

            ALittleGuess param_value_stat_guess = param_value_stat.guessType();
            try {
                ALittleReferenceOpUtil.guessTypeEqual(param_guess, param_value_stat, param_value_stat_guess, false, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(param_value_stat, "第" + i + "个参数类型和函数定义的参数类型不同:" + error.getError());
            }
        }

        // 检查这个函数是不是await
        if (guess_functor.await_modifier)
        {
            // 检查这次所在的函数必须要有await或者async修饰
            PsiHelper.checkInvokeAwait(myElement);
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "tcall表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "tcall表达式第一个参数必须是一个函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        // 构建对象
        for (int i = 0; i < valueStatList.size() - 1; ++i) {
            if (i >= guessFunctor.param_name_list.size()) break;
            String name = guessFunctor.param_name_list.get(i);
            valueStat = valueStatList.get(i + 1);
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
