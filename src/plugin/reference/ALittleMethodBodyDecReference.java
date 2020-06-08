package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleMethodBodyDecReference extends ALittleReference<ALittleMethodBodyDec> {
    public ALittleMethodBodyDecReference(@NotNull ALittleMethodBodyDec element, TextRange textRange) {
        super(element, textRange);
    }

    // 检查表达式是否有return
    public static boolean checkAllExpr(@NotNull List<ALittleGuess> return_list, @NotNull ALittleAllExpr all_expr) throws ALittleGuessException {
        if (all_expr.getIfExpr() != null)
        {
                ALittleIfExpr if_expr = all_expr.getIfExpr();
            ALittleAllExpr sub_all_expr = if_expr.getAllExpr();
            ALittleIfBody if_body = if_expr.getIfBody();
            if (sub_all_expr != null)
            {
                boolean sub_result = checkAllExpr(return_list, sub_all_expr);
                if (!sub_result) return false;
            }
            else if (if_body != null)
            {
                boolean sub_result = checkAllExprList(return_list, if_body.getAllExprList());
                if (!sub_result) return false;
            }
            else
            {
                return false;
            }

            List<ALittleElseIfExpr> else_if_expr_list = if_expr.getElseIfExprList();
            for (ALittleElseIfExpr else_if_expr : else_if_expr_list)
            {
                sub_all_expr = else_if_expr.getAllExpr();
                ALittleElseIfBody else_if_body = else_if_expr.getElseIfBody();
                if (sub_all_expr != null)
                {
                    boolean sub_result = checkAllExpr(return_list, sub_all_expr);
                    if (!sub_result) return false;
                }
                else if (else_if_body != null)
                {
                    boolean sub_result = checkAllExprList(return_list, else_if_body.getAllExprList());
                    if (!sub_result) return false;
                }
                else
                {
                    return false;
                }
            }

            ALittleElseExpr else_expr = if_expr.getElseExpr();
            if (else_expr == null) return false;

            sub_all_expr = else_expr.getAllExpr();
            ALittleElseBody else_body = else_expr.getElseBody();
            if (sub_all_expr != null)
                return checkAllExpr(return_list, sub_all_expr);
            else if (else_body != null)
                return checkAllExprList(return_list, else_body.getAllExprList());
            else
                return false;
        }
/*
        if (all_expr.getForExpr() != null)
        {
            var for_expr = all_expr.getForExpr();
            var sub_all_expr = for_expr.getAllExpr();
            var for_body = for_expr.getForBody();
            if (sub_all_expr != null)
                return checkAllExpr(return_list, sub_all_expr, out result);
            else if (for_body != null)
                return checkAllExprList(return_list, for_body.getAllExprList(), out result);
            else
                return null;
        }

        if (all_expr.getWhileExpr() != null)
        {
            var while_expr = all_expr.getWhileExpr();
            var sub_all_expr = while_expr.getAllExpr();
            var while_body = while_expr.getWhileBody();
            if (sub_all_expr != null)
                return checkAllExpr(return_list, sub_all_expr, out result);
            else if (while_body != null)
                return checkAllExprList(return_list, while_body.getAllExprList(), out result);
            else
                return null;
        }
*/
        if (all_expr.getDoWhileExpr() != null)
        {
            ALittleDoWhileExpr do_while_expr = all_expr.getDoWhileExpr();
            ALittleDoWhileBody do_while_body = do_while_expr.getDoWhileBody();
            if (do_while_body != null)
                return checkAllExprList(return_list, do_while_body.getAllExprList());
            else
                return false;
        }

        if (all_expr.getReturnExpr() != null)
        {
            return true;
        }

        if (all_expr.getWrapExpr() != null)
        {
            ALittleWrapExpr wrap_expr = all_expr.getWrapExpr();
            return checkAllExprList(return_list, wrap_expr.getAllExprList());
        }

        if (all_expr.getThrowExpr() != null)
        {
            return true;
        }

        return false;
    }

    // 检查表达式是否有return
    public static boolean checkAllExprList(@NotNull List<ALittleGuess> return_list, @NotNull List<ALittleAllExpr> all_expr_list) throws ALittleGuessException {
        // 如果没有就检查子表达式
        int index = -1;
        for (int i = 0; i < all_expr_list.size(); ++i)
        {
            ALittleAllExpr all_expr = all_expr_list.get(i);
            if (!PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;

            boolean sub_result = checkAllExpr(return_list, all_expr_list.get(i));
            if (sub_result)
            {
                index = i;
                break;
            }
        }
        if (index == -1)
            return false;

        for (int i = index + 1; i < all_expr_list.size(); ++i)
        {
            ALittleAllExpr all_expr = all_expr_list.get(i);
            if (!PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            throw new ALittleGuessException(all_expr, "当前分支内，从这里开始之后所有语句永远都不会执行到");
        }

        return true;
    }

    // 检查函数体
    public static void checkMethodBody(@NotNull List<ALittleGuess> return_list
            , @NotNull ALittleMethodNameDec method_name_dec
            , @NotNull ALittleMethodBodyDec method_body_dec) throws ALittleGuessException {
        // 检查return
        if (return_list.size() > 0 && !PsiHelper.isRegister(method_name_dec))
        {
            List<ALittleAllExpr> all_expr_list = method_body_dec.getAllExprList();
            boolean result = checkAllExprList(return_list, all_expr_list);
            if (!result)
                throw new ALittleGuessException(method_name_dec, "不是所有分支都有return");
        }
    }

    public void checkError() throws ALittleGuessException {
        PsiElement parent = myElement.getParent();
        
        if (parent instanceof ALittleClassCtorDec) return;
        if (parent instanceof ALittleClassSetterDec) return;

        List<ALittleGuess> return_list = new ArrayList<>();
        ALittleMethodReturnDec return_dec = null;
        ALittleMethodNameDec name_dec = null;

        if (parent instanceof ALittleClassGetterDec)
        {
            ALittleClassGetterDec getter_dec = (ALittleClassGetterDec)parent;
            name_dec = getter_dec.getMethodNameDec();
            if (name_dec == null) return;

            ALittleAllType all_type = getter_dec.getAllType();
            if (all_type == null) return;
            ALittleGuess all_type_guess = all_type.guessType();
            return_list.add(all_type_guess);
            checkMethodBody(return_list, name_dec, myElement);
            return;
        }

        if (parent instanceof ALittleClassMethodDec)
        {
            ALittleClassMethodDec method_dec = (ALittleClassMethodDec)parent;
            name_dec = method_dec.getMethodNameDec();
            if (name_dec == null) return;
            return_dec = method_dec.getMethodReturnDec();
        }

        if (parent instanceof ALittleClassStaticDec)
        {
            ALittleClassStaticDec static_dec = (ALittleClassStaticDec)parent;
            name_dec = static_dec.getMethodNameDec();
            if (name_dec == null) return;
            return_dec = static_dec.getMethodReturnDec();
        }

        if (parent instanceof ALittleGlobalMethodDec) {
            ALittleGlobalMethodDec global_method_dec = (ALittleGlobalMethodDec)parent;
            name_dec = global_method_dec.getMethodNameDec();
            if (name_dec == null) return;
            return_dec = global_method_dec.getMethodReturnDec();
        }

        if (name_dec == null) return;

        if (return_dec != null)
        {
            List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
            for (int i = 0; i < return_one_list.size(); ++i)
            {
                ALittleMethodReturnOneDec return_one = return_one_list.get(i);
                ALittleAllType all_type = return_one.getAllType();
                ALittleMethodReturnTailDec return_tail = return_one.getMethodReturnTailDec();
                if (all_type != null)
                {
                    ALittleGuess all_type_guess = all_type.guessType();
                    return_list.add(all_type_guess);
                }
                else if (return_tail != null)
                {
                    if (i + 1 != return_one_list.size())
                        throw new ALittleGuessException(return_one, "返回值占位符必须定义在最后");
                    ALittleGuess return_tail_guess = return_tail.guessType();
                    return_list.add(return_tail_guess);
                }
            }
        }

        checkMethodBody(return_list, name_dec, myElement);
    }
}
