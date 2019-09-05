package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleBindStat;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleBindStatReference extends ALittleReference<ALittleBindStat> {
    public ALittleBindStatReference(@NotNull ALittleBindStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "bind表达式不能没有参数");
        }

        // 第一个参数必须是函数
        ALittleValueStat valueStat = valueStatList.get(0);
        ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "bind表达式第一个参数必须是一个函数");
        }

        // 开始构建类型
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
        info.value = "Functor<";
        info.element = myElement;
        info.functorAwait = guessInfo.functorAwait;
        if (info.functorAwait) {
            info.value = "Functor<await";
        }
        info.functorParamList = new ArrayList<>();
        info.functorParamList.addAll(guessInfo.functorParamList);
        info.functorParamNameList = new ArrayList<>();
        info.functorParamNameList.addAll(guessInfo.functorParamNameList);
        info.functorReturnList = new ArrayList<>();
        info.functorReturnList.addAll(guessInfo.functorReturnList);
        info.functorParamTail = guessInfo.functorParamTail;
        // 移除掉以填写的参数
        int paramCount = valueStatList.size() - 1;
        while (paramCount > 0
                && info.functorParamList.size() > 0
                && info.functorParamNameList.size() > 0) {
            info.functorParamList.remove(0);
            info.functorParamNameList.remove(0);
            --paramCount;
        }
        // 计算参数类型名列表
        List<String> nameList = new ArrayList<>();
        for (ALittleReferenceUtil.GuessTypeInfo paramInfo : info.functorParamList) {
            nameList.add(paramInfo.value);
        }
        if (info.functorParamTail != null) {
            nameList.add(info.functorParamTail.value);
        }
        info.value += "(";
        info.value += String.join(",", nameList);
        info.value += ")";

        // 计算返回值类型名列表
        nameList = new ArrayList<>();
        for (ALittleReferenceUtil.GuessTypeInfo returnInfo : info.functorReturnList) {
            nameList.add(returnInfo.value);
        }
        if (!nameList.isEmpty()) info.value += ":";
        info.value += String.join(",", nameList);
        info.value += ">";

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "bind表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "bind表达式第一个参数必须是一个函数");
        }

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (valueStatList.size() - 1 > guessInfo.functorParamList.size()) {
            if (guessInfo.functorParamTail == null) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "bind表达式参数太多了");
            } else {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "bind表达式参数太多了，即使被bind的函数定义的参数占位符(...)也不行!");
            }
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < valueStatList.size(); ++i) {
            ALittleReferenceUtil.GuessTypeInfo param_guess_info = guessInfo.functorParamList.get(i - 1);
            ALittleValueStat paramValueStat = valueStatList.get(i);
            try {
                ALittleReferenceOpUtil.guessTypeEqual(myElement, param_guess_info, paramValueStat, paramValueStat.guessType());
            } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                throw new ALittleReferenceUtil.ALittleReferenceException(paramValueStat, "第" + i + "个参数类型和函数定义的参数类型不同:" + e.getError());
            }
        }
    }
}
