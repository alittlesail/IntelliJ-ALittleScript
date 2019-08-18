package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleAutoTypeReference extends ALittleReference {
    public ALittleAutoTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        PsiElement parent = myElement.getParent();

        // 处理定义并且赋值
        if (parent instanceof ALittleVarAssignPairDec) {
            // 获取父节点
            ALittleVarAssignPairDec varAssignPairDec = (ALittleVarAssignPairDec) parent;
            parent = varAssignPairDec.getParent();
            if (!(parent instanceof ALittleVarAssignExpr)) return guessList;
            ALittleVarAssignExpr varAssignExpr = (ALittleVarAssignExpr) parent;
            // 获取等号右边的表达式
            ALittleValueStat valueStat = varAssignExpr.getValueStat();
            if (valueStat == null) return guessList;
            // 获取等号坐标的变量定义列表
            List<ALittleVarAssignPairDec> pairDecList = varAssignExpr.getVarAssignPairDecList();
            // 如果左边有多个变量，那么右边肯定是一个多返回值的函数调用
            if (pairDecList.size() > 1) {
                // 计算当前是第几个参数
                int index = pairDecList.indexOf(varAssignPairDec);
                if (index == -1) return guessList;
                // 获取函数对应的那个返回值类型
                List<ALittleReferenceUtil.GuessTypeInfo> methodCallGuessList = valueStat.guessTypes();
                if (index < methodCallGuessList.size()) {
                    guessList.add(methodCallGuessList.get(index));
                }
            // 如果左边只有一个变量，那么就是直接赋值
            } else if (pairDecList.size() == 1) {
                guessList.add(valueStat.guessType());
            }
        // 处理for循环定义
        } else if (parent instanceof ALittleForPairDec) {
            // 获取父节点
            ALittleForPairDec forPairDec = (ALittleForPairDec) parent;
            if (!(forPairDec.getParent() instanceof ALittleForInCondition)) return guessList;

            // 获取for in 表达式内容
            ALittleForInCondition inExpr = (ALittleForInCondition) forPairDec.getParent();
            ALittleValueStat valueStat = inExpr.getValueStat();
            if (valueStat == null) return guessList;

            // 获取定义列表
            List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();
            int index = pairDecList.indexOf(forPairDec);

            // 获取循环对象的类型
            ALittleReferenceUtil.GuessTypeInfo guessInfo = valueStat.guessType();
            if (guessInfo == null) return guessList;

            // 必须是通用类型
            if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_LIST) {
                // 对于List的key使用auto，那么就默认是int类型
                if (index == 0) {
                    ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
                    info.type = ALittleReferenceUtil.GuessType.GT_PRIMITIVE;
                    info.value = "int";
                    info.element = myElement;
                    guessList.add(info);
                } else if (index == 1) {
                    guessList.add(guessInfo.listSubType);
                }
            } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_MAP) {
                // 如果是key，那么就取key的类型
                if (index == 0) {
                    guessList.add(guessInfo.mapKeyType);
                // 如果是value，那么就取value的类型
                } else if (index == 1) {
                    guessList.add(guessInfo.mapValueType);
                }
            }
        }

        return guessList;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        variants.add(LookupElementBuilder.create("auto"));
        return variants.toArray();
    }
}
