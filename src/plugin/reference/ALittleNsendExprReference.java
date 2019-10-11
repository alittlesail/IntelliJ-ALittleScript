package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleNsendExprReference extends ALittleReference<ALittleNsendExpr> {
    public ALittleNsendExprReference(@NotNull ALittleNsendExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "nend表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleGuessException(myElement, "nsend只有两个参数，第一个是ALittle.IMsgCommon或派生类，第二个是struct");

        // 第一个参数必须是ALittle.IMsgCommon的派生类
        ALittleValueStat valueStat = valueStatList.get(0);
        ALittleGuess guessInfo = valueStat.guessType();
        if (!ALittleReferenceUtil.IsClassSuper(guessInfo.element, "ALittle.IMsgCommon")) {
            throw new ALittleGuessException(valueStat, "nsend表达式第一个参数必须是ALittle.IMsgCommon的派生类");
        }

        // 第二个参数必须是struct
        valueStat = valueStatList.get(1);
        guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_STRUCT) {
            throw new ALittleGuessException(valueStat, "nsend表达式第二个参数必须是struct");
        }

        return new ArrayList<>();
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        if (valueStatList.size() != 2)
            throw new ALittleGuessException(myElement, "nsend只有两个参数，第一个是ALittle.IMsgCommon的派生类，第二个是struct");

        // 第一个参数必须是ALittle.IMsgCommon的派生类
        ALittleValueStat valueStat = valueStatList.get(0);
        ALittleGuess guessInfo = valueStat.guessType();
        if (!ALittleReferenceUtil.IsClassSuper(guessInfo.element, "ALittle.IMsgCommon")) {
            throw new ALittleGuessException(valueStat, "nsend表达式第一个参数必须是ALittle.IMsgCommon的派生类");
        }

        // 第二个参数必须是struct
        valueStat = valueStatList.get(1);
        guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_STRUCT) {
            throw new ALittleGuessException(valueStat, "nsend表达式第二个参数必须是struct");
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        // 构建对象
        for (int i = 0; i < valueStatList.size(); ++i) {
            ALittleValueStat valueStat = valueStatList.get(i);
            if (i == 0)
                result.add(new InlayInfo("client", valueStat.getNode().getStartOffset()));
            else if (i == 1)
                result.add(new InlayInfo("struct", valueStat.getNode().getStartOffset()));
            else
                break;
        }
        return result;
    }
}
