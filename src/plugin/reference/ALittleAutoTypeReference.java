package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleAutoTypeReference extends ALittleReference {
    public ALittleAutoTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guessList = new ArrayList<>();
        PsiElement parent = myElement.getParent();

        try {
            // 处理定义并且赋值
            if (parent instanceof ALittleVarAssignPairDec) {
                // 获取父节点
                ALittleVarAssignPairDec varAssignPairDec = (ALittleVarAssignPairDec) parent;
                parent = varAssignPairDec.getParent();
                if (parent == null) return guessList;
                if (!(parent instanceof ALittleVarAssignExpr)) return guessList;
                // 获取父节点
                ALittleVarAssignExpr varAssignExpr = (ALittleVarAssignExpr) parent;
                // 获取等号右边的表达式
                ALittleValueStat valueStat = varAssignExpr.getValueStat();
                if (valueStat == null) return guessList;

                // 计算当前是第几个参数
                List<ALittleVarAssignPairDec> pairDecList = varAssignExpr.getVarAssignPairDecList();
                // 如果左边有多个变量，那么右边肯定是一个多返回值的函数调用
                if (pairDecList.size() > 1) {
                    int index = pairDecList.indexOf(varAssignPairDec);
                    if (index == -1) return guessList;
                    // 获取函数对应的那个返回值类型
                    List<PsiElement> methodCallGuessList = ALittleUtil.guessTypeForMethodCall(valueStat);
                    if (index < methodCallGuessList.size()) {
                        guessList.add(methodCallGuessList.get(index));
                    }
                } else if (pairDecList.size() == 1) {
                    guessList.add(ALittleUtil.guessSoftType(valueStat, valueStat));
                }
            } else if (parent instanceof ALittleForPairDec) {
                // 获取父节点
                ALittleForPairDec forPairDec = (ALittleForPairDec) parent;
                if (!(forPairDec.getParent() instanceof ALittleForInCondition)) return guessList;

                // 获取for in 表达式内容
                ALittleForInCondition inExpr = (ALittleForInCondition) forPairDec.getParent();
                ALittleValueStat valueStat = inExpr.getValueStat();
                if (valueStat == null) return guessList;

                // 获取循环对象的类型
                PsiElement guess_type = ALittleUtil.guessSoftType(valueStat, valueStat);
                // 必须是生成式
                if (!(guess_type instanceof ALittleGenericType)) return guessList;

                ALittleGenericType genericType = (ALittleGenericType) guess_type;
                // 如果是List
                if (genericType.getGenericListType() != null) {
                    ALittleGenericListType list_type = genericType.getGenericListType();
                    List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();
                    if (pairDecList.size() != 2) return guessList;

                    // 因为Key的部分必须是int或者I64，就不允许使用auto了
                    // 所以这里只获取List的元素类型
                    if (pairDecList.get(1).equals(forPairDec)) {
                        ALittleAllType allType = list_type.getAllType();
                        if (allType != null) {
                            guessList.add(ALittleUtil.guessType(allType));
                        }
                    }
                } else if (genericType.getGenericMapType() != null) {
                    ALittleGenericMapType map_type = genericType.getGenericMapType();
                    List<ALittleForPairDec> pairDecList = inExpr.getForPairDecList();
                    if (pairDecList.size() != 2) return guessList;

                    List<ALittleAllType> allTypeList = map_type.getAllTypeList();
                    if (allTypeList.size() != 2) return guessList;

                    // 如果是key，那么就取key的类型
                    if (pairDecList.get(0).equals(forPairDec)) {
                        guessList.add(ALittleUtil.guessType(allTypeList.get(0)));
                        // 如果是value，那么就去
                    } else if (pairDecList.get(1).equals(forPairDec)) {
                        guessList.add(ALittleUtil.guessType(allTypeList.get(1)));
                    }
                }
            }
        } catch (ALittleUtil.ALittleElementException e) {
            return guessList;
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
