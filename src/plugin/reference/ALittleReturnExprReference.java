package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReturnExprReference extends ALittleReference<ALittleReturnExpr> {
    public ALittleReturnExprReference(@NotNull ALittleReturnExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        PsiElement parent = null;
        if (myElement.getReturnYield() != null)
        {
            // 对于ReturnYield就不需要做返回值检查
            // 对所在函数进行检查，必须要有async和await表示
            // 获取对应的函数对象
            PsiElement element = null;

            parent = myElement;
            while (parent != null)
            {
                if (parent instanceof ALittleClassMethodDec)
                {
                    ALittleClassMethodDec method_dec = (ALittleClassMethodDec)parent;
                    List<ALittleModifier> modifier = ((ALittleClassElementDec)method_dec.getParent()).getModifierList();
                    if (PsiHelper.getCoroutineType(modifier) == null)
                    {
                        element = method_dec.getMethodNameDec();
                        if (element == null) element = method_dec;
                    }
                    break;
                }
                    else if (parent instanceof ALittleClassStaticDec)
                {
                    ALittleClassStaticDec method_dec = (ALittleClassStaticDec)parent;
                    List<ALittleModifier> modifier = ((ALittleClassElementDec)method_dec.getParent()).getModifierList();
                    if (PsiHelper.getCoroutineType(modifier) == null)
                    {
                        element = method_dec.getMethodNameDec();
                        if (element == null) element = method_dec;
                    }
                    break;
                }
                    else if (parent instanceof ALittleGlobalMethodDec)
                {
                    ALittleGlobalMethodDec method_dec = (ALittleGlobalMethodDec)parent;
                    List<ALittleModifier> modifier = ((ALittleNamespaceElementDec)method_dec.getParent()).getModifierList();
                    if (PsiHelper.getCoroutineType(modifier) == null)
                    {
                        element = method_dec.getMethodNameDec();
                        if (element == null) element = method_dec;
                    }
                    break;
                }

                parent = parent.getParent();
            }

            if (element != null)
                throw new ALittleGuessException(element, "函数内部使用了return yield表达式，所以必须使用async或await修饰");
            return;
        }

        List<ALittleValueStat> value_stat_list = myElement.getValueStatList();
        List<ALittleAllType> return_type_list = new ArrayList<>();
        ALittleMethodReturnTailDec return_tail_dec = null;

        // 获取对应的函数对象
        parent = myElement;
        while (parent != null)
        {
            if (parent instanceof ALittleClassGetterDec)
            {
                ALittleClassGetterDec getterDec = (ALittleClassGetterDec)parent;
                return_type_list.clear();
                ALittleAllType return_type_dec = getterDec.getAllType();
                if (return_type_dec != null)
                    return_type_list.add(return_type_dec);
                break;
            }
                else if (parent instanceof ALittleClassSetterDec)
            {
                break;
            }
                else if (parent instanceof ALittleClassMethodDec)
            {
                ALittleClassMethodDec method_dec = (ALittleClassMethodDec)parent;
                ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                if (return_dec != null)
                {
                    List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
                    for (ALittleMethodReturnOneDec return_one : return_one_list)
                    {
                        ALittleAllType all_type = return_one.getAllType();
                        if (all_type != null) return_type_list.add(all_type);

                        ALittleMethodReturnTailDec return_tail = return_one.getMethodReturnTailDec();
                        if (return_tail != null) return_tail_dec = return_tail;
                    }
                }
                break;
            }
                else if (parent instanceof ALittleClassStaticDec)
            {
                ALittleClassStaticDec method_dec = (ALittleClassStaticDec)parent;
                ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                if (return_dec != null)
                {
                    List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
                    for (ALittleMethodReturnOneDec return_one : return_one_list)
                    {
                        ALittleAllType all_type = return_one.getAllType();
                        if (all_type != null) return_type_list.add(all_type);

                        ALittleMethodReturnTailDec return_tail = return_one.getMethodReturnTailDec();
                        if (return_tail != null) return_tail_dec = return_tail;
                    }
                }
                break;
            }
                else if (parent instanceof ALittleGlobalMethodDec)
            {
                ALittleGlobalMethodDec method_dec = (ALittleGlobalMethodDec)parent;
                ALittleMethodReturnDec return_dec = method_dec.getMethodReturnDec();
                if (return_dec != null)
                {
                    List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
                    for (ALittleMethodReturnOneDec return_one : return_one_list)
                    {
                        ALittleAllType all_type = return_one.getAllType();
                        if (all_type != null) return_type_list.add(all_type);

                        ALittleMethodReturnTailDec return_tail = return_one.getMethodReturnTailDec();
                        if (return_tail != null) return_tail_dec = return_tail;
                    }
                }
                break;
            }

            parent = parent.getParent();
        }

        // 参数的类型
        List<ALittleGuess> guess_list = null;
        // 如果返回值只有一个函数调用
        if (value_stat_list.size() == 1 && (return_type_list.size() > 1 || return_tail_dec != null))
        {
            ALittleValueStat value_stat = value_stat_list.get(0);
            guess_list = value_stat.guessTypes();
            boolean has_value_tail = guess_list.size() > 0
                    && guess_list.get(guess_list.size() - 1) instanceof ALittleGuessReturnTail;

            if (return_tail_dec == null)
            {
                if (has_value_tail)
                {
                    if (guess_list.size() < return_type_list.size() - 1)
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量超过函数定义的返回值数量");
                }
                else
                {
                    if (guess_list.size() != return_type_list.size())
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量和函数定义的返回值数量不相等");
                }
            }
            else
            {
                if (has_value_tail)
                {
                    // 不用检查
                }
                else
                {
                    if (guess_list.size() < return_type_list.size())
                        throw new ALittleGuessException(myElement, "return的函数调用的返回值数量少于函数定义的返回值数量");
                }
            }
        }
        else
        {
            if (return_tail_dec == null)
            {
                if (value_stat_list.size() != return_type_list.size())
                    throw new ALittleGuessException(myElement, "return的返回值数量和函数定义的返回值数量不相等");
            }
            else
            {
                if (value_stat_list.size() < return_type_list.size())
                    throw new ALittleGuessException(myElement, "return的返回值数量少于函数定义的返回值数量");
            }
            guess_list = new ArrayList<>();
            for (ALittleValueStat value_stat : value_stat_list)
            {
                Tuple2<Integer, List<ALittleGuess>> result = PsiHelper.calcReturnCount(value_stat);
                if (result.getFirst() != 1) throw new ALittleGuessException(value_stat, "表达式必须只能是一个返回值");

                ALittleGuess guess = value_stat.guessType();
                if (guess instanceof ALittleGuessParamTail)
                throw new ALittleGuessException(value_stat, "return表达式不能返回\"...\"");
                ALittleGuess value_stat_guess = value_stat.guessType();
                guess_list.add(value_stat_guess);
            }
        }

        // 每个类型依次检查
        for (int i = 0; i < guess_list.size(); ++i)
        {
            ALittleValueStat target_value_stat = null;
            if (i < value_stat_list.size())
                target_value_stat = value_stat_list.get(i);
            else
                target_value_stat = value_stat_list.get(0);

            if (guess_list.get(i) instanceof ALittleGuessReturnTail) break;
            if (i >= return_type_list.size()) break;
            ALittleGuess return_type_guess = return_type_list.get(i).guessType();
            if (return_type_guess instanceof ALittleGuessReturnTail) break;

            try {
                ALittleReferenceOpUtil.guessTypeEqual(return_type_guess, target_value_stat, guess_list.get(i), false, true);
            } catch (ALittleGuessException error) {
                throw new ALittleGuessException(target_value_stat, "return的第" + (i + 1) + "个返回值数量和函数定义的返回值类型不同:" + error.getError());
            }
        }
    }
}
