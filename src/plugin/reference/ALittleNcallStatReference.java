package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleNcallStatReference extends ALittleReference<ALittleNcallStat> {
    public ALittleNcallStatReference(@NotNull ALittleNcallStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        // 第一个参数必须是函数
        ALittleValueStat valueStat = valueStatList.get(0);
        ALittleGuess guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        // 必须是一个全局函数
        if (!(guessInfo.element instanceof ALittleGlobalMethodDec)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        // 检查注解
        ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec)guessInfo.element;
        ALittleProtoModifier protoModifier = globalMethodDec.getProtoModifier();
        if (protoModifier == null) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个带@注解的全局函数");
        }

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(ALittleReferenceUtil.sStringGuessTypeInfo);
        guessList.addAll(guessInfo.functorReturnList);

        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleGuess guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        if (!(guessInfo.element instanceof ALittleGlobalMethodDec)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        // 检查注解
        ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec)guessInfo.element;
        ALittleProtoModifier protoModifier = globalMethodDec.getProtoModifier();
        if (protoModifier == null) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个带@注解的全局函数");
        }

        String text = protoModifier.getText();

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (valueStatList.size() - 1 > guessInfo.functorParamList.size()) {
            if (guessInfo.functorParamTail == null) {
                throw new ALittleGuessException(myElement, "ncall表达式参数太多了");
            } else {
                throw new ALittleGuessException(myElement, "ncall表达式参数太多了，即使被ncall的函数定义了参数占位符(...)也不行!");
            }
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < valueStatList.size(); ++i) {
            ALittleValueStat paramValueStat = valueStatList.get(i);
            ALittleGuess valueGuessInfo = paramValueStat.guessType();

            // 第二个参数要特殊处理，根据不同的注解进行调整
            if (i == 1) {
                if (text.equals("@Http") && !ALittleReferenceUtil.IsClassSuper(valueGuessInfo.element, "ALittle.IHttpSender")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IHttpSender的派生类");
                }

                if ((text.equals("@HttpUpload") || text.equals("@HttpDownload")) && !ALittleReferenceUtil.IsClassSuper(valueGuessInfo.element, "ALittle.IHttpFileSender")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IHttpFileSender的派生类");
                }

                if (text.equals("@Msg") && !ALittleReferenceUtil.IsClassSuper(valueGuessInfo.element, "ALittle.IMsgCommon")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IMsgCommon的派生类");
                }
                continue;
            }

            ALittleGuess paramGuessInfo = guessInfo.functorParamList.get(i - 1);
            try {
                ALittleReferenceOpUtil.guessTypeEqual(myElement, paramGuessInfo, paramValueStat, valueGuessInfo);
            } catch (ALittleGuessException e) {
                throw new ALittleGuessException(paramValueStat, "第" + i + "个参数类型和函数定义的参数类型不同:" + e.getError());
            }
        }

        // 检查这次所在的函数必须要有await或者async修饰
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                throw new ALittleGuessException(myElement, "全局表达式不能调用带有await的函数");
            } else if (parent instanceof ALittleClassCtorDec) {
                throw new ALittleGuessException(myElement, "构造函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassGetterDec) {
                throw new ALittleGuessException(myElement, "getter函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassSetterDec) {
                throw new ALittleGuessException(myElement, "setter函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassMethodDec) {
                if (((ALittleClassMethodDec)parent).getCoModifier() == null) {
                    throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                }
                break;
            } else if (parent instanceof ALittleClassStaticDec) {
                if (((ALittleClassStaticDec)parent).getCoModifier() == null) {
                    throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                }
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                if (((ALittleGlobalMethodDec)parent).getCoModifier() == null) {
                    throw new ALittleGuessException(myElement, "所在函数没有async或者await修饰");
                }
                break;
            }
            parent = parent.getParent();
        }
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleGuessException {
        List<InlayInfo> result = new ArrayList<>();
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleGuess guessInfo = valueStat.guessType();
        if (guessInfo.type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个函数");
        }

        // 构建对象
        for (int i = 0; i < valueStatList.size() - 1; ++i) {
            if (i >= guessInfo.functorParamNameList.size()) break;
            String name = guessInfo.functorParamNameList.get(i);
            valueStat = valueStatList.get(i + 1);
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
