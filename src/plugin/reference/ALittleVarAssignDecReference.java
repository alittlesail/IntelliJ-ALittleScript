package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessPrimitive;
import plugin.guess.ALittleGuessReturnTail;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleVarAssignDecReference extends ALittleReference<ALittleVarAssignDec> {
    private ALittleClassDec mClassDec;
    private ALittleTemplateDec mTemplateParamDec;

    public ALittleVarAssignDecReference(@NotNull ALittleVarAssignDec element, TextRange textRange) {
        super(element, textRange);
    }

    public ALittleClassDec getClassDec() {
        if (mClassDec != null) return mClassDec;
        mClassDec = PsiHelper.findClassDecFromParent(myElement);
        return mClassDec;
    }

    public ALittleTemplateDec getTemplateDec() {
        if (mTemplateParamDec != null) return mTemplateParamDec;
        mTemplateParamDec = PsiHelper.findMethodTemplateDecFromParent(myElement);
        return mTemplateParamDec;
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        ALittleAllType all_type = myElement.getAllType();
        if (all_type != null) return all_type.guessTypes();

        List<ALittleGuess> guess_list = new ArrayList<>();
        ALittleVarAssignNameDec name_dec = myElement.getVarAssignNameDec();
        if (name_dec == null) return guess_list;

        ALittleVarAssignExpr parent = (ALittleVarAssignExpr) myElement.getParent();
        ALittleValueStat value_stat = parent.getValueStat();
        if (value_stat == null)
            throw new ALittleGuessException(name_dec, "没有赋值对象，无法推导类型");

        // 获取等号左边的变量定义列表
        List<ALittleVarAssignDec> pair_dec_list = parent.getVarAssignDecList();
        // 计算当前是第几个参数
        int index = pair_dec_list.indexOf(myElement);
        // 获取函数对应的那个返回值类型
        List<ALittleGuess> method_call_guess_list = value_stat.guessTypes();
        // 如果有"..."作为返回值结尾
        boolean hasTail = method_call_guess_list.size() > 0 && method_call_guess_list.get(method_call_guess_list.size() - 1) instanceof ALittleGuessReturnTail;
        if (hasTail) {
            if (index >= method_call_guess_list.size() - 1)
                guess_list.add(ALittleGuessPrimitive.sAnyGuess);
            else
                guess_list.add(method_call_guess_list.get(index));
        } else {
            if (index >= method_call_guess_list.size())
                throw new ALittleGuessException(myElement, "没有赋值对象，无法推导类型");
            guess_list.add(method_call_guess_list.get(index));
        }

        return guess_list;
    }
}
