package plugin.reference;

import com.intellij.openapi.util.TextRange;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessClassTemplate;
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
        List<ALittleGuess> guess_list = null;

        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "bind表达式不能没有参数");

        ALittleValueStat value_stat = value_stat_list.get(0);
        ALittleGuess guess = value_stat.guessType();

        if (!(guess instanceof ALittleGuessFunctor))
            throw new ALittleGuessException(myElement, "bind表达式第一个参数必须是一个函数");
        ALittleGuessFunctor guess_functor = (ALittleGuessFunctor)guess;

        if (guess_functor.template_param_list.size() > 0)
            throw new ALittleGuessException(myElement, "bind表达式要绑定的函数不能有模板定义");

        // 开始构建类型
        ALittleGuessFunctor info = new ALittleGuessFunctor(myElement);
        info.await_modifier = guess_functor.await_modifier;
        info.const_modifier = guess_functor.const_modifier;
        info.proto = guess_functor.proto;
        info.template_param_list.addAll(guess_functor.template_param_list);
        info.param_list.addAll(guess_functor.param_list);
        info.param_nullable_list.addAll(guess_functor.param_nullable_list);
        info.param_name_list.addAll(guess_functor.param_name_list);
        info.param_tail = guess_functor.param_tail;
        info.return_list.addAll(guess_functor.return_list);
        info.return_tail = guess_functor.return_tail;
        // 移除已填写的参数
        int param_count = value_stat_list.size() - 1;
        while (param_count > 0
                && info.param_list.size() > 0
                && info.param_nullable_list.size() > 0
                && info.param_name_list.size() > 0)
        {
            info.param_list.remove(0);
            info.param_nullable_list.remove(0);
            info.param_name_list.remove(0);
            --param_count;
        }
        info.updateValue();
        guess_list = new ArrayList<>();
        guess_list.add(info);
        return guess_list;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        if (value_stat_list.size() == 0)
            throw new ALittleGuessException(myElement, "bind表达式不能没有参数");

        ALittleValueStat value_stat = value_stat_list.get(0);
        ALittleGuess guess = value_stat.guessType();

        if (!(guess instanceof ALittleGuessFunctor))
            throw new ALittleGuessException(myElement, "bind表达式第一个参数必须是一个函数");
        ALittleGuessFunctor guess_functor = (ALittleGuessFunctor)guess;

        if (guess_functor.template_param_list.size() > 0)
            throw new ALittleGuessException(myElement, "bind表达式要绑定的函数不能有模板定义");

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (value_stat_list.size() - 1 > guess_functor.param_list.size())
        {
            if (guess_functor.param_tail == null)
                throw  new ALittleGuessException(myElement, "bind表达式参数太多了");
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < value_stat_list.size(); ++i)
        {
            if (i - 1 >= guess_functor.param_list.size()) break;

            ALittleGuess param_guess = guess_functor.param_list.get(i - 1);
            value_stat = value_stat_list.get(i);

            Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
            if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

            ALittleGuess value_stat_guess = value_stat.guessType();
            try {
                ALittleReferenceOpUtil.guessTypeEqual(param_guess, value_stat, value_stat_guess, false, false);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(value_stat, "第" + i + "个参数类型和函数定义的参数类型不同:" + error.getError());
            }
        }
    }
}
