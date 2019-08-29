package plugin.reference;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleAutoTypeReference extends ALittleReference<ALittleAutoType> {
    public ALittleAutoTypeReference(@NotNull ALittleAutoType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        PsiElement parent = myElement.getParent();
        // 处理定义并且赋值
        if (parent instanceof ALittleVarAssignDec) {
            // 获取父节点
            ALittleVarAssignDec varAssignDec = (ALittleVarAssignDec) parent;
            ALittleVarAssignExpr varAssignExpr = (ALittleVarAssignExpr) varAssignDec.getParent();
            // 获取等号右边的表达式
            ALittleValueStat valueStat = varAssignExpr.getValueStat();
            if (valueStat == null) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "auto 没有赋值对象，无法推导类型");
            }
            // 获取等号坐标的变量定义列表
            List<ALittleVarAssignDec> pairDecList = varAssignExpr.getVarAssignDecList();
            // 计算当前是第几个参数
            int index = pairDecList.indexOf(varAssignDec);
            // 获取函数对应的那个返回值类型
            List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = valueStat.guessTypes();
            if (index >= methodCallGuessList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "auto 没有赋值对象，无法推导类型");
            }

            guessList.add(methodCallGuessList.get(index));
        // 处理for循环定义
        } else if (parent instanceof ALittleForPairDec) {
            // 获取父节点
            ALittleForPairDec forPairDec = (ALittleForPairDec) parent;
            parent = forPairDec.getParent();
            if (parent instanceof ALittleForInCondition) {
                ALittleForInCondition inExpr = (ALittleForInCondition)parent;
                // 取出遍历的对象
                ALittleValueStat valueStat = inExpr.getValueStat();
                if (valueStat == null) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "For没有遍历对象，auto 无法推导类型");
                }

                // 获取定义列表
                List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();
                int index = pairDecList.indexOf(forPairDec);
                // 获取循环对象的类型
                ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
                // 处理List
                if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_LIST) {
                    // 对于List的key使用auto，那么就默认是int类型
                    if (index == 0) {
                        return ALittleReferenceUtil.sPrimitiveGuessTypeMap.get("int");
                    } else if (index == 1) {
                        guessList.add(guessInfo.listSubType);
                    }
                    // 处理Map
                } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_MAP) {
                    // 如果是key，那么就取key的类型
                    if (index == 0) {
                        guessList.add(guessInfo.mapKeyType);
                        // 如果是value，那么就取value的类型
                    } else if (index == 1) {
                        guessList.add(guessInfo.mapValueType);
                    }
                }
            } else if (parent instanceof ALittleForStartStat) {
                ALittleForStartStat startStat = (ALittleForStartStat)parent;
                ALittleValueStat valueStat = startStat.getValueStat();
                if (valueStat == null) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "auto没有赋值对象，无法推导类型");
                }
                return valueStat.guessTypes();
            }
        } else {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "ALittleAutoType出现未知的父节点");
        }

        return guessList;
    }

    @NotNull
    public List<InlayInfo> getParameterHints() throws ALittleReferenceUtil.ALittleReferenceException {
        List<InlayInfo> result = new ArrayList<>();
        // 获取类型
        List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = guessTypes();
        if (guessTypeList.isEmpty()) return result;
        ALittleReferenceUtil.GuessTypeInfo guessType = guessTypeList.get(0);

        // 如果是定义并赋值
        if (myElement.getParent() instanceof ALittleVarAssignDec) {
            ALittleVarAssignDec dec = (ALittleVarAssignDec) myElement.getParent();
            ALittleVarAssignNameDec nameDec = dec.getVarAssignNameDec();
            if (nameDec == null) return result;

            result.add(new InlayInfo(guessType.value, nameDec.getNode().getStartOffset()));
        // 如果是for
        } else if (myElement.getParent() instanceof  ALittleForPairDec) {
            ALittleForPairDec dec = (ALittleForPairDec) myElement.getParent();
            ALittleVarAssignNameDec nameDec = dec.getVarAssignNameDec();
            if (nameDec == null) return result;

            result.add(new InlayInfo(guessType.value, nameDec.getNode().getStartOffset()));
        }

        return result;
    }
}
