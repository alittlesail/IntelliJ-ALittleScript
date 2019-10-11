package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
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
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        // 必须是一个全局函数
        if (!(guessFunctor.element instanceof ALittleGlobalMethodDec)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        // 检查注解
        ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec)guessFunctor.element;
        ALittleProtoModifier protoModifier = globalMethodDec.getProtoModifier();
        if (protoModifier == null) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个带@注解的全局函数");
        }

        List<ALittleGuess> guessList = new ArrayList<>();
        guessList.add(ALittleGuessPrimitive.sStringGuess);
        guessList.addAll(guessFunctor.functorReturnList);

        return guessList;
    }

    public void checkError() throws ALittleGuessException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        if (valueStatList.isEmpty()) {
            throw new ALittleGuessException(myElement, "ncall表达式不能没有参数");
        }

        ALittleValueStat valueStat = valueStatList.get(0);
        // 第一个参数必须是函数
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        if (!(guessFunctor.element instanceof ALittleGlobalMethodDec)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个全局函数");
        }

        // 检查注解
        ALittleGlobalMethodDec globalMethodDec = (ALittleGlobalMethodDec)guessFunctor.element;
        ALittleProtoModifier protoModifier = globalMethodDec.getProtoModifier();
        if (protoModifier == null) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个带@注解的全局函数");
        }

        String text = protoModifier.getText();

        // 后面跟的参数数量不能超过这个函数的参数个数
        if (valueStatList.size() - 1 > guessFunctor.functorParamList.size()) {
            throw new ALittleGuessException(myElement, "ncall表达式参数太多了");
        }

        // 遍历所有的表达式，看下是否符合
        for (int i = 1; i < valueStatList.size(); ++i) {
            valueStat = valueStatList.get(i);
            ALittleGuess valueGuess = valueStat.guessType();

            // 第二个参数要特殊处理，根据不同的注解进行调整
            if (i == 1) {
                if (!(valueGuess instanceof ALittleGuessClass))
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IHttpSender的派生类");
                ALittleGuessClass valueGuessClass = (ALittleGuessClass)valueGuess;

                if (text.equals("@Http") && !ALittleReferenceUtil.IsClassSuper(valueGuessClass.element, "ALittle.IHttpSender")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IHttpSender的派生类");
                }

                if ((text.equals("@HttpUpload") || text.equals("@HttpDownload")) && !ALittleReferenceUtil.IsClassSuper(valueGuessClass.element, "ALittle.IHttpFileSender")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IHttpFileSender的派生类");
                }

                if (text.equals("@Msg") && !ALittleReferenceUtil.IsClassSuper(valueGuessClass.element, "ALittle.IMsgCommon")) {
                    throw new ALittleGuessException(myElement, "ncall调用带" + text + "注解的函数时，第二个参数必须是ALittle.IMsgCommon的派生类");
                }
                continue;
            }

            ALittleGuess paramGuessInfo = guessFunctor.functorParamList.get(i - 1);
            try {
                ALittleReferenceOpUtil.guessTypeEqual(myElement, paramGuessInfo, valueStat, valueGuess);
            } catch (ALittleGuessException e) {
                throw new ALittleGuessException(valueStat, "第" + i + "个参数类型和函数定义的参数类型不同:" + e.getError());
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
        ALittleGuess guess = valueStat.guessType();
        if (!(guess instanceof ALittleGuessFunctor)) {
            throw new ALittleGuessException(valueStat, "ncall表达式第一个参数必须是一个函数");
        }
        ALittleGuessFunctor guessFunctor = (ALittleGuessFunctor)guess;

        // 构建对象
        for (int i = 0; i < valueStatList.size() - 1; ++i) {
            if (i >= guessFunctor.functorParamNameList.size()) break;
            String name = guessFunctor.functorParamNameList.get(i);
            valueStat = valueStatList.get(i + 1);
            result.add(new InlayInfo(name, valueStat.getNode().getStartOffset()));
        }
        return result;
    }
}
