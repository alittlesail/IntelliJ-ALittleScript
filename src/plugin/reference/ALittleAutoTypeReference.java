package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleAutoTypeReference extends PsiReferenceBase<PsiElement> implements ALittleReference {
    private String m_key;

    public ALittleAutoTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        m_key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    public PsiElement guessType() {
        List<PsiElement> guess_list = guessTypes();
        if (guess_list.isEmpty()) return null;
        return guess_list.get(0);
    }

    @NotNull
    public List<PsiElement> guessTypes() {
        List<PsiElement> guess_list = new ArrayList<>();
        PsiElement parent = myElement.getParent();
        // 处理定义并且赋值
        if (parent instanceof ALittleVarAssignPairDec) {
            // 获取父节点
            ALittleVarAssignPairDec var_assign_pair_dec = (ALittleVarAssignPairDec) parent;
            parent = var_assign_pair_dec.getParent();
            if (parent == null) {
                return guess_list;
            }
            if (parent instanceof ALittleVarAssignExpr) {
                // 获取父节点
                ALittleVarAssignExpr var_assign_expr = (ALittleVarAssignExpr) parent;

                // 获取等号右边的表达式
                ALittleValueStat value_stat = var_assign_expr.getValueStat();
                if (value_stat == null) return guess_list;

                // 获取左边的变量列表
                List<ALittleVarAssignPairDec> pair_dec_list = var_assign_expr.getVarAssignPairDecList();
                int index = -1;
                for (int i = 0; i < pair_dec_list.size(); ++i) {
                    if (pair_dec_list.get(i).equals(var_assign_pair_dec)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) return guess_list;
                boolean has_handle = false;

                // 如果左边有多个变量，那么右边肯定是一个多返回值的函数调用
                if (pair_dec_list.size() > 1) {
                    // 获取函数对应的那个返回值类型
                    List<PsiElement> method_call_guess_list = ALittleUtil.guessTypeForMethodCall(value_stat);
                    if (method_call_guess_list != null) {
                        if (index < method_call_guess_list.size()) {
                            guess_list.add(method_call_guess_list.get(index));
                        }
                        has_handle = true;
                    }
                }

                if (!has_handle && pair_dec_list.size() > 0) {
                    List<String> error_content_list = new ArrayList<>();
                    List<PsiElement> error_element_list = new ArrayList<>();
                    PsiElement value_stat_guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
                    if (value_stat_guess_type != null) guess_list.add(value_stat_guess_type);
                }
            }
        } else if (parent instanceof ALittleForPairDec) {
            // 获取父节点
            ALittleForPairDec for_pair_dec = (ALittleForPairDec) parent;
            if (!(for_pair_dec.getParent() instanceof ALittleForInCondition)) return guess_list;

            // 获取for in 表达式内容
            ALittleForInCondition in_expr = (ALittleForInCondition)for_pair_dec.getParent();
            ALittleValueStat value_stat = in_expr.getValueStat();
            if (value_stat == null) return guess_list;

            // 获取循环对象的类型
            List<String> error_content_list = new ArrayList<>();
            List<PsiElement> error_element_list = new ArrayList<>();
            PsiElement guess_type = ALittleUtil.guessSoftType(value_stat, value_stat, error_content_list, error_element_list);
            if (guess_type == null) return guess_list;

            // 必须是生成式
            if (!(guess_type instanceof ALittleGenericType)) return guess_list;

            ALittleGenericType generic_type = (ALittleGenericType)guess_type;
            // 如果是List
            if (generic_type.getGenericListType() != null) {
                ALittleGenericListType list_type = generic_type.getGenericListType();
                List<ALittleForPairDec> pair_dec_list = in_expr.getForPairDecList();
                if (pair_dec_list.size() != 2) return guess_list;

                // 因为Key的部分必须是int或者I64，就不允许使用auto了
                // 所以这里只获取List的元素类型
                if (pair_dec_list.get(1).equals(for_pair_dec)) {
                    PsiElement list_guess_type = ALittleUtil.guessType(list_type.getAllType());
                    if (list_guess_type != null) guess_list.add(list_guess_type);
                }
            } else if (generic_type.getGenericMapType() != null) {
                ALittleGenericMapType map_type = generic_type.getGenericMapType();
                List<ALittleForPairDec> pair_dec_list = in_expr.getForPairDecList();
                if (pair_dec_list.size() != 2) {
                    return guess_list;
                }

                List<ALittleAllType> all_type_list = map_type.getAllTypeList();
                if (all_type_list.size() != 2) {
                    return guess_list;
                }

                // 如果是key，那么就取key的类型
                if (pair_dec_list.get(0).equals(for_pair_dec)) {
                    PsiElement pair_guess_type = ALittleUtil.guessType(all_type_list.get(0));
                    if (pair_guess_type != null) guess_list.add(pair_guess_type);
                    // 如果是value，那么就去
                } else if (pair_dec_list.get(1).equals(for_pair_dec)) {
                    PsiElement pair_guess_type = ALittleUtil.guessType(all_type_list.get(1));
                    if (pair_guess_type != null) guess_list.add(pair_guess_type);
                }
            }
        }

        return guess_list;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        variants.add(LookupElementBuilder.create("auto"));
        return variants.toArray();
    }
}
