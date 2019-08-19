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

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleReferenceUtil.GuessTypeInfo GuessInfo = valueStat.guessType();
        if (GuessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "bind表达式第一个参数必须是一个函数");
        }

        // 开始构建类型
        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
        info.value = "Functor<(";
        info.element = myElement;
        info.functorAwait = GuessInfo.functorAwait;
        if (info.functorAwait) {
            info.value = "Functor<await(";
        }
        info.functorParamList = GuessInfo.functorParamList;
        info.functorReturnList = GuessInfo.functorReturnList;
        // 移除掉以填写的参数
        int paramCount = valueStatList.size() - 1;
        while (paramCount > 0) {
            info.functorParamList.remove(0);
            --paramCount;
        }
        // 计算参数类型名列表
        List<String> nameList = new ArrayList<>();
        for (ALittleReferenceUtil.GuessTypeInfo paramInfo : info.functorParamList) {
            nameList.add(paramInfo.value);
        }
        info.value += String.join(",", nameList);
        info.value += ")";

        // 计算返回值类型名列表
        nameList = new ArrayList<>();
        for (ALittleReferenceUtil.GuessTypeInfo param_info : info.functorReturnList) {
            nameList.add(param_info.value);
        }
        if (!nameList.isEmpty()) info.value += ":";
        info.value += String.join(",", nameList);
        info.value += ">";

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
