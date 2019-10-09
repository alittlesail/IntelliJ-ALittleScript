package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAssertExpr;
import plugin.psi.ALittleThrowExpr;
import plugin.psi.ALittleValueStat;

import java.util.ArrayList;
import java.util.List;

public class ALittleAssertExprReference extends ALittleReference<ALittleAssertExpr> {
    public ALittleAssertExprReference(@NotNull ALittleAssertExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert有且仅有两个参数，第一个是任意类型，第二个是string");

        ALittleValueStat valueStat = valueStatList.get(1);
        ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
        if (!guessInfo.value.equals("string")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "assert表达式第二个参数必须是string类型");
        }

        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert有且仅有两个参数，第一个是任意类型，第二个是string");

        ALittleValueStat valueStat = valueStatList.get(1);
        ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
        if (!guessInfo.value.equals("string")) {
            throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "assert表达式第二个参数必须是string类型");
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleReferenceUtil.ALittleReferenceException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "assert有且仅有两个参数，第一个是任意类型，第二个是string");

        // 构建对象
        for (int i = 0; i < valueStatList.size(); ++i) {
            ALittleValueStat valueStat = valueStatList.get(i);
            if (i == 0)
                result.add(new InlayInfo("c", valueStat.getNode().getStartOffset()));
            else
                result.add(new InlayInfo("e", valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
