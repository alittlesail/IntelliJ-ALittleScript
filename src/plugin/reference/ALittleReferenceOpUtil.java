package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReferenceOpUtil {
    // 创建引用对象
    public static ALittleReference create(PsiElement element) {
        TextRange range = new TextRange(0, element.getText().length());

        if (element instanceof ALittleAllExpr) return new ALittleAllExprReference((ALittleAllExpr) element, range);
        if (element instanceof ALittleAllType) return new ALittleAllTypeReference((ALittleAllType) element, range);
        if (element instanceof ALittleAssertExpr)
            return new ALittleAssertExprReference((ALittleAssertExpr) element, range);
        if (element instanceof ALittleBindStat) return new ALittleBindStatReference((ALittleBindStat) element, range);
        if (element instanceof ALittleClassCtorDec)
            return new ALittleClassCtorDecReference((ALittleClassCtorDec) element, range);
        if (element instanceof ALittleClassGetterDec)
            return new ALittleClassGetterDecReference((ALittleClassGetterDec) element, range);
        if (element instanceof ALittleClassSetterDec)
            return new ALittleClassSetterDecReference((ALittleClassSetterDec) element, range);
        if (element instanceof ALittleClassMethodDec)
            return new ALittleClassMethodDecReference((ALittleClassMethodDec) element, range);
        if (element instanceof ALittleClassStaticDec)
            return new ALittleClassStaticDecReference((ALittleClassStaticDec) element, range);
        if (element instanceof ALittleClassDec) return new ALittleClassDecReference((ALittleClassDec) element, range);
        if (element instanceof ALittleClassExtendsDec)
            return new ALittleClassExtendsDecReference((ALittleClassExtendsDec) element, range);
        if (element instanceof ALittleClassElementDec)
            return new ALittleClassElementDecReference((ALittleClassElementDec) element, range);
        if (element instanceof ALittleClassNameDec)
            return new ALittleClassNameDecReference((ALittleClassNameDec) element, range);
        if (element instanceof ALittleClassVarDec)
            return new ALittleClassVarDecReference((ALittleClassVarDec) element, range);
        if (element instanceof ALittleConstValue)
            return new ALittleConstValueReference((ALittleConstValue) element, range);
        if (element instanceof ALittleCoroutineStat)
            return new ALittleCoroutineStatReference((ALittleCoroutineStat) element, range);
        if (element instanceof ALittleCustomTypeDotIdName)
            return new ALittleCustomTypeDotIdNameReference((ALittleCustomTypeDotIdName) element, range);
        if (element instanceof ALittleCustomTypeDotId)
            return new ALittleCustomTypeDotIdReference((ALittleCustomTypeDotId) element, range);
        if (element instanceof ALittleCustomType)
            return new ALittleCustomTypeReference((ALittleCustomType) element, range);
        if (element instanceof ALittleEnumDec) return new ALittleEnumDecReference((ALittleEnumDec) element, range);
        if (element instanceof ALittleEnumNameDec)
            return new ALittleEnumNameDecReference((ALittleEnumNameDec) element, range);
        if (element instanceof ALittleEnumVarDec)
            return new ALittleEnumVarDecReference((ALittleEnumVarDec) element, range);
        if (element instanceof ALittleFlowExpr) return new ALittleFlowExprReference((ALittleFlowExpr) element, range);
        if (element instanceof ALittleForExpr) return new ALittleForExprReference((ALittleForExpr) element, range);
        if (element instanceof ALittleForPairDec)
            return new ALittleForPairDecReference((ALittleForPairDec) element, range);
        if (element instanceof ALittleGenericType)
            return new ALittleGenericTypeReference((ALittleGenericType) element, range);
        if (element instanceof ALittleGlobalMethodDec)
            return new ALittleGlobalMethodDecReference((ALittleGlobalMethodDec) element, range);
        if (element instanceof ALittleIfCondition)
            return new ALittleIfConditionReference((ALittleIfCondition) element, range);
        if (element instanceof ALittleElseIfCondition)
            return new ALittleElseIfConditionReference((ALittleElseIfCondition) element, range);
        if (element instanceof ALittleWhileCondition)
            return new ALittleWhileConditionReference((ALittleWhileCondition) element, range);
        if (element instanceof ALittleDoWhileCondition)
            return new ALittleDoWhileConditionReference((ALittleDoWhileCondition) element, range);
        if (element instanceof ALittleMethodBodyDec)
            return new ALittleMethodBodyDecReference((ALittleMethodBodyDec) element, range);
        if (element instanceof ALittleMethodNameDec)
            return new ALittleMethodNameDecReference((ALittleMethodNameDec) element, range);
        if (element instanceof ALittleMethodParamOneDec)
            return new ALittleMethodParamOneDecReference((ALittleMethodParamOneDec) element, range);
        if (element instanceof ALittleMethodParamNameDec)
            return new ALittleMethodParamNameDecReference((ALittleMethodParamNameDec) element, range);
        if (element instanceof ALittleMethodParamTailDec)
            return new ALittleMethodParamTailDecReference((ALittleMethodParamTailDec) element, range);
        if (element instanceof ALittleMethodReturnDec)
            return new ALittleMethodReturnDecReference((ALittleMethodReturnDec) element, range);
        if (element instanceof ALittleMethodReturnTailDec)
            return new ALittleMethodReturnTailDecReference((ALittleMethodReturnTailDec) element, range);
        if (element instanceof ALittleNamespaceDec)
            return new ALittleNamespaceDecReference((ALittleNamespaceDec) element, range);
        if (element instanceof ALittleNamespaceElementDec)
            return new ALittleNamespaceElementDecReference((ALittleNamespaceElementDec) element, range);
        if (element instanceof ALittleNamespaceNameDec)
            return new ALittleNamespaceNameDecReference((ALittleNamespaceNameDec) element, range);
        if (element instanceof ALittleOp1Expr) return new ALittleOp1ExprReference((ALittleOp1Expr) element, range);
        if (element instanceof ALittleOpAssignExpr)
            return new ALittleOpAssignExprReference((ALittleOpAssignExpr) element, range);
        if (element instanceof ALittleOpNewListStat)
            return new ALittleOpNewListStatReference((ALittleOpNewListStat) element, range);
        if (element instanceof ALittleOpNewStat)
            return new ALittleOpNewStatReference((ALittleOpNewStat) element, range);
        if (element instanceof ALittlePrimitiveType)
            return new ALittlePrimitiveTypeReference((ALittlePrimitiveType) element, range);
        if (element instanceof ALittlePropertyValueBracketValue)
            return new ALittlePropertyValueBracketValueReference((ALittlePropertyValueBracketValue) element, range);
        if (element instanceof ALittlePropertyValueCastType)
            return new ALittlePropertyValueCastTypeReference((ALittlePropertyValueCastType) element, range);
        if (element instanceof ALittlePropertyValueCustomType)
            return new ALittlePropertyValueCustomTypeReference((ALittlePropertyValueCustomType) element, range);
        if (element instanceof ALittlePropertyValueDotIdName)
            return new ALittlePropertyValueDotIdNameReference((ALittlePropertyValueDotIdName) element, range);
        if (element instanceof ALittlePropertyValueDotId)
            return new ALittlePropertyValueDotIdReference((ALittlePropertyValueDotId) element, range);
        if (element instanceof ALittlePropertyValueFirstType)
            return new ALittlePropertyValueFirstTypeReference((ALittlePropertyValueFirstType) element, range);
        if (element instanceof ALittlePropertyValueMethodCall)
            return new ALittlePropertyValueMethodCallReference((ALittlePropertyValueMethodCall) element, range);
        if (element instanceof ALittlePropertyValue)
            return new ALittlePropertyValueReference((ALittlePropertyValue) element, range);
        if (element instanceof ALittlePropertyValueSuffix)
            return new ALittlePropertyValueSuffixReference((ALittlePropertyValueSuffix) element, range);
        if (element instanceof ALittlePropertyValueThisType)
            return new ALittlePropertyValueThisTypeReference((ALittlePropertyValueThisType) element, range);
        if (element instanceof ALittleReflectValue)
            return new ALittleReflectValueReference((ALittleReflectValue) element, range);
        if (element instanceof ALittleReturnExpr)
            return new ALittleReturnExprReference((ALittleReturnExpr) element, range);
        if (element instanceof ALittleStructDec)
            return new ALittleStructDecReference((ALittleStructDec) element, range);
        if (element instanceof ALittleStructNameDec)
            return new ALittleStructNameDecReference((ALittleStructNameDec) element, range);
        if (element instanceof ALittleStructVarDec)
            return new ALittleStructVarDecReference((ALittleStructVarDec) element, range);
        if (element instanceof ALittleTcallStat)
            return new ALittleTcallStatReference((ALittleTcallStat) element, range);
        if (element instanceof ALittleTemplateDec)
            return new ALittleTemplateDecReference((ALittleTemplateDec) element, range);
        if (element instanceof ALittleTemplatePairDec)
            return new ALittleTemplatePairDecReference((ALittleTemplatePairDec) element, range);
        if (element instanceof ALittleThrowExpr)
            return new ALittleThrowExprReference((ALittleThrowExpr) element, range);
        if (element instanceof ALittleUsingDec) return new ALittleUsingDecReference((ALittleUsingDec) element, range);
        if (element instanceof ALittleUsingNameDec)
            return new ALittleUsingNameDecReference((ALittleUsingNameDec) element, range);
        if (element instanceof ALittleValueFactorStat)
            return new ALittleValueFactorStatReference((ALittleValueFactorStat) element, range);
        if (element instanceof ALittleValueStat)
            return new ALittleValueStatReference((ALittleValueStat) element, range);
        if (element instanceof ALittleVarAssignDec)
            return new ALittleVarAssignDecReference((ALittleVarAssignDec) element, range);
        if (element instanceof ALittleVarAssignExpr)
            return new ALittleVarAssignExprReference((ALittleVarAssignExpr) element, range);
        if (element instanceof ALittleVarAssignNameDec)
            return new ALittleVarAssignNameDecReference((ALittleVarAssignNameDec) element, range);
        if (element instanceof ALittleWrapValueStat)
            return new ALittleWrapValueStatReference((ALittleWrapValueStat) element, range);
        if (element instanceof ALittleLanguageModifier)
            return new ALittleLanguageModifierReference((ALittleLanguageModifier) element, range);
        if (element instanceof ALittleLanguageNameDec)
            return new ALittleLanguageNameDecReference((ALittleLanguageNameDec) element, range);

        return null;
    }

    @NotNull
    private static ALittleGuess guessTypeForOp8Impl(String op_string
            , PsiElement left_src, ALittleGuess left_guess_type
            , PsiElement right_src, ALittleGuess right_guess_type
            , ALittleOp8Suffix op_8_suffix
    ) throws ALittleGuessException {
        if (!(left_guess_type instanceof ALittleGuessBool))
            throw new ALittleGuessException(left_src, op_string + "运算符左边必须是bool类型.不能是:" + left_guess_type.getValue());

        if (!(right_guess_type instanceof ALittleGuessBool))
            throw new ALittleGuessException(right_src, op_string + "运算符右边边必须是bool类型.不能是:" + right_guess_type.getValue());

        return ALittleGuessPrimitive.sBoolGuess;
    }

    @NotNull
    private static ALittleGuess guessTypeForOp8(PsiElement left_src
            , ALittleGuess left_guess_type
            , ALittleOp8Suffix op_8_suffix
    ) throws ALittleGuessException {
        String op_string = op_8_suffix.getOp8().getText();

        ALittleGuess suffix_guess_type = null;
        PsiElement last_src = null;

        ALittleValueFactorStat value_factor_stat = op_8_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_8_suffix.getOp2Value();
        if (value_factor_stat != null) {
            suffix_guess_type = value_factor_stat.guessType();
            last_src = value_factor_stat;
        } else if (op_2_value != null) {
            suffix_guess_type = guessType(op_2_value);
            last_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_8_suffix, "未知的表达式");
        }

        List<ALittleOp8SuffixEe> suffix_ee_list = op_8_suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix());
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix());
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix());
                last_src = suffix_ee.getOp5Suffix();
            } else if (suffix_ee.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ee.getOp6Suffix());
                last_src = suffix_ee.getOp6Suffix();
            } else if (suffix_ee.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ee.getOp7Suffix());
                last_src = suffix_ee.getOp7Suffix();
            } else {
                throw new ALittleGuessException(suffix_ee, "未知的表达式");
            }
        }

        return guessTypeForOp8Impl(op_string, left_src, left_guess_type, last_src, suffix_guess_type, op_8_suffix);
    }


    @NotNull
    private static ALittleGuess guessTypeForOp7Impl(String op_string
            , PsiElement left_src, ALittleGuess left_guess_type
            , PsiElement right_src, ALittleGuess right_guess_type
            , ALittleOp7Suffix op_7_suffix
    ) throws ALittleGuessException {
        if (!(left_guess_type instanceof ALittleGuessBool))
            throw new ALittleGuessException(left_src, op_string + "运算符左边必须是bool类型.不能是:" + left_guess_type.getValue());

        if (!(right_guess_type instanceof ALittleGuessBool))
            throw new ALittleGuessException(right_src, op_string + "运算符右边边必须是bool类型.不能是:" + right_guess_type.getValue());

        return ALittleGuessPrimitive.sBoolGuess;
    }


    @NotNull
    private static ALittleGuess guessTypeForOp7(PsiElement left_src, ALittleGuess left_guess_type, ALittleOp7Suffix op_7_suffix) throws ALittleGuessException {
        String op_string = op_7_suffix.getOp7().getText();

        ALittleGuess suffix_guess_type = null;
        PsiElement last_src = null;

        ALittleValueFactorStat value_factor_stat = op_7_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_7_suffix.getOp2Value();
        if (value_factor_stat != null) {
            suffix_guess_type = value_factor_stat.guessType();
            last_src = value_factor_stat;
        } else if (op_2_value != null) {
            suffix_guess_type = guessType(op_2_value);
            last_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_7_suffix, "未知的表达式");
        }

        List<ALittleOp7SuffixEe> suffix_ee_list = op_7_suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix());
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix());
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix());
                last_src = suffix_ee.getOp5Suffix();
            } else if (suffix_ee.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ee.getOp6Suffix());
                last_src = suffix_ee.getOp6Suffix();
            } else {
                throw new ALittleGuessException(suffix_ee, "未知的表达式");
            }
        }

        return guessTypeForOp7Impl(op_string, left_src, left_guess_type, last_src, suffix_guess_type, op_7_suffix);
    }


    @NotNull
    private static ALittleGuess guessTypeForOp6Impl(String op_string
            , PsiElement left_src, ALittleGuess left_guess_type
            , PsiElement right_src, ALittleGuess right_guess_type
            , ALittleOp6Suffix op_6_suffix
    ) throws ALittleGuessException {
        if (op_string.equals("==") || op_string.equals("!=")) {
            if (left_guess_type instanceof ALittleGuessAny || left_guess_type.getValue().equals("null")
                    || right_guess_type instanceof ALittleGuessAny || right_guess_type.getValue().equals("null")) {
                return ALittleGuessPrimitive.sBoolGuess;
            }

            if (left_guess_type instanceof ALittleGuessInt || left_guess_type instanceof ALittleGuessLong || left_guess_type instanceof ALittleGuessDouble) {
                if (right_guess_type instanceof ALittleGuessInt
                        || right_guess_type instanceof ALittleGuessLong
                        || right_guess_type instanceof ALittleGuessDouble) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(right_src, op_string + "运算符左边是数字，那么右边必须是int,long,double,any,null类型.不能是:" + right_guess_type.getValue());
            }

            if (left_guess_type instanceof ALittleGuessString) {
                if (right_guess_type instanceof ALittleGuessString) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(right_src, op_string + "运算符左边是字符串，那么右边必须是string,any,null类型.不能是:" + right_guess_type.getValue());
            }

            return ALittleGuessPrimitive.sBoolGuess;
        } else {
            if (left_guess_type instanceof ALittleGuessInt || left_guess_type instanceof ALittleGuessLong || left_guess_type instanceof ALittleGuessDouble) {
                if (right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong || right_guess_type instanceof ALittleGuessDouble) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(right_src, op_string + "运算符左边是数字，那么右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
            }

            if (left_guess_type instanceof ALittleGuessString) {
                if (right_guess_type instanceof ALittleGuessString) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(right_src, op_string + "运算符左边是字符串，那么右边必须是string类型.不能是:" + right_guess_type.getValue());
            }

            throw new ALittleGuessException(left_src, op_string + "运算符左边必须是int,long,double,string类型.不能是:" + left_guess_type.getValue());
        }
    }

    @NotNull
    private static ALittleGuess guessTypeForOp6(PsiElement left_src, ALittleGuess left_guess_type, ALittleOp6Suffix op_6_suffix) throws ALittleGuessException {
        String op_string = op_6_suffix.getOp6().getText();

        ALittleGuess suffix_guess_type = null;
        PsiElement last_src = null;

        ALittleValueFactorStat value_factor_stat = op_6_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_6_suffix.getOp2Value();
        if (value_factor_stat != null) {
            suffix_guess_type = value_factor_stat.guessType();
            last_src = value_factor_stat;
        } else if (op_2_value != null) {
            suffix_guess_type = guessType(op_2_value);
            last_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_6_suffix, "未知的表达式");
        }

        List<ALittleOp6SuffixEe> suffix_ee_list = op_6_suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix());
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix());
                last_src = suffix_ee.getOp4Suffix();
            } else if (suffix_ee.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ee.getOp5Suffix());
                last_src = suffix_ee.getOp5Suffix();
            } else {
                throw new ALittleGuessException(suffix_ee, "未知的表达式");
            }
        }
        return guessTypeForOp6Impl(op_string, left_src, left_guess_type, last_src, suffix_guess_type, op_6_suffix);
    }


    @NotNull
    private static ALittleGuess guessTypeForOp5Impl(String op_string
            , PsiElement left_src, ALittleGuess left_guess_type
            , PsiElement right_src, ALittleGuess right_guess_type
            , ALittleOp5Suffix op_5_suffix
    ) throws ALittleGuessException {
        boolean left_check = left_guess_type instanceof ALittleGuessInt || left_guess_type instanceof ALittleGuessLong || left_guess_type instanceof ALittleGuessDouble || left_guess_type instanceof ALittleGuessString;
        if (!left_check)
            throw new ALittleGuessException(left_src, op_string + "运算符左边必须是int,long,double,string类型.不能是:" + left_guess_type.getValue());

        boolean right_check = right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong || right_guess_type instanceof ALittleGuessDouble || right_guess_type instanceof ALittleGuessString;
        if (!right_check)
            throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double,string类型.不能是:" + right_guess_type.getValue());

        if (!(left_guess_type instanceof ALittleGuessString || right_guess_type instanceof ALittleGuessString))
            throw new ALittleGuessException(left_src, op_string + "运算符左边和右边至少一个是string类型.不能是:" + left_guess_type.getValue() + "和" + right_guess_type.getValue());

        return ALittleGuessPrimitive.sStringGuess;
    }


    @NotNull
    private static ALittleGuess guessTypeForOp5(PsiElement left_src, ALittleGuess left_guess_type, ALittleOp5Suffix op_5_suffix) throws ALittleGuessException {
        String op_string = op_5_suffix.getOp5().getText();

        ALittleGuess suffix_guess_type = null;
        PsiElement last_src = null;

        ALittleValueFactorStat value_factor_stat = op_5_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_5_suffix.getOp2Value();
        if (value_factor_stat != null) {
            suffix_guess_type = value_factor_stat.guessType();
            last_src = value_factor_stat;
        } else if (op_2_value != null) {
            suffix_guess_type = guessType(op_2_value);
            last_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_5_suffix, "未知的表达式");
        }

        List<ALittleOp5SuffixEe> suffix_ee_list = op_5_suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix());
                last_src = suffix_ee.getOp3Suffix();
            } else if (suffix_ee.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ee.getOp4Suffix());
                last_src = suffix_ee.getOp4Suffix();
            } else {
                throw new ALittleGuessException(suffix_ee, "未知的表达式");
            }
        }

        return guessTypeForOp5Impl(op_string, left_src, left_guess_type, last_src, suffix_guess_type, op_5_suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp4Impl(String op_string
            , PsiElement left_src, ALittleGuess left_guess_type
            , PsiElement right_src, ALittleGuess right_guess_type
            , ALittleOp4Suffix op_4_suffix
    ) throws ALittleGuessException {
        if (left_guess_type instanceof ALittleGuessInt || left_guess_type instanceof ALittleGuessLong) {
            if (right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong) {
                return left_guess_type;
            } else if (right_guess_type instanceof ALittleGuessDouble) {
                return right_guess_type;
            } else
                throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
        }

        if (left_guess_type instanceof ALittleGuessDouble) {
            if (right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong) {
                return left_guess_type;
            } else if (right_guess_type instanceof ALittleGuessDouble) {
                return right_guess_type;
            } else
                throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
        }

        throw new ALittleGuessException(left_src, op_string + "运算符左边必须是int,long,double类型.不能是:" + left_guess_type.getValue());
    }


    @NotNull
    private static ALittleGuess guessTypeForOp4(PsiElement left_src, ALittleGuess left_guess_type, ALittleOp4Suffix op_4_suffix) throws ALittleGuessException {
        String op_string = op_4_suffix.getOp4().getText();

        ALittleGuess suffix_guess_type = null;
        PsiElement last_src = null;

        ALittleValueFactorStat value_factor_stat = op_4_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_4_suffix.getOp2Value();
        if (value_factor_stat != null) {
            suffix_guess_type = value_factor_stat.guessType();
            last_src = value_factor_stat;
        } else if (op_2_value != null) {
            suffix_guess_type = guessType(op_2_value);
            last_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_4_suffix, "未知的表达式");
        }

        List<ALittleOp4SuffixEe> suffix_ee_list = op_4_suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffix_ee : suffix_ee_list) {
            if (suffix_ee.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ee.getOp3Suffix());
                last_src = suffix_ee.getOp3Suffix();
            } else {
                throw new ALittleGuessException(suffix_ee, "未知的表达式");
            }
        }

        return guessTypeForOp4Impl(op_string, left_src, left_guess_type, last_src, suffix_guess_type, op_4_suffix);
    }


    @NotNull
    private static ALittleGuess guessTypeForOp3(PsiElement left_src, ALittleGuess left_guess_type, ALittleOp3Suffix op_3_suffix) throws ALittleGuessException {
        String op_string = op_3_suffix.getOp3().getText();

        ALittleGuess right_guess_type = null;
        PsiElement right_src = null;

        ALittleValueFactorStat value_factor_stat = op_3_suffix.getValueFactorStat();
        ALittleOp2Value op_2_value = op_3_suffix.getOp2Value();
        if (value_factor_stat != null) {
            right_guess_type = value_factor_stat.guessType();
            right_src = value_factor_stat;
        } else if (op_2_value != null) {
            right_guess_type = guessType(op_2_value);
            right_src = op_2_value;
        } else {
            throw new ALittleGuessException(op_3_suffix, "未知的表达式");
        }

        if (left_guess_type instanceof ALittleGuessInt) {
            if (right_guess_type instanceof ALittleGuessInt) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return left_guess_type;
            } else if (right_guess_type instanceof ALittleGuessLong) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return right_guess_type;
            } else if (right_guess_type instanceof ALittleGuessDouble) {
                return right_guess_type;
            } else {
                throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
            }
        }

        if (left_guess_type instanceof ALittleGuessLong) {
            if (right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong) {
                // 这个是特殊的
                if (op_string.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return left_guess_type;
            } else if (right_guess_type instanceof ALittleGuessDouble) {
                return right_guess_type;
            } else {
                throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
            }
        }

        if (left_guess_type instanceof ALittleGuessDouble) {
            if (right_guess_type instanceof ALittleGuessInt || right_guess_type instanceof ALittleGuessLong) {
                return left_guess_type;
            } else if (right_guess_type instanceof ALittleGuessDouble) {
                return right_guess_type;
            } else
                throw new ALittleGuessException(right_src, op_string + "运算符右边必须是int,long,double类型.不能是:" + right_guess_type.getValue());
        }

        throw new ALittleGuessException(left_src, op_string + "运算符左边必须是int,long,double类型.不能是:" + left_guess_type.getValue());
    }

    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp8Stat op_8_stat) throws ALittleGuessException {
        ALittleGuess factor_guess_type = value_factor_stat.guessType();

        ALittleGuess suffix_guess_type = guessTypeForOp8(value_factor_stat, factor_guess_type, op_8_stat.getOp8Suffix());

        PsiElement last_src = op_8_stat.getOp8Suffix();
        List<ALittleOp8SuffixEx> suffix_ex_list = op_8_stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());
                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }


    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp7Stat op_7_stat) throws ALittleGuessException {
        ALittleGuess factor_guess_type = value_factor_stat.guessType();
        ALittleGuess suffix_guess_type = guessTypeForOp7(value_factor_stat, factor_guess_type, op_7_stat.getOp7Suffix());

        PsiElement last_src = op_7_stat.getOp7Suffix();
        List<ALittleOp7SuffixEx> suffix_ex_list = op_7_stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());
                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }


    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp6Stat op_6_stat) throws ALittleGuessException {
        ALittleGuess factor_guess_type = value_factor_stat.guessType();
        ALittleGuess suffix_guess_type = guessTypeForOp6(value_factor_stat, factor_guess_type, op_6_stat.getOp6Suffix());

        PsiElement last_src = op_6_stat.getOp6Suffix();
        List<ALittleOp6SuffixEx> suffix_ex_list = op_6_stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix());
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());
                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp5Stat op_5_stat) throws ALittleGuessException {
        ALittleGuess factor_guess_type = value_factor_stat.guessType();


        ALittleGuess suffix_guess_type = guessTypeForOp5(value_factor_stat, factor_guess_type, op_5_stat.getOp5Suffix());


        PsiElement last_src = op_5_stat.getOp5Suffix();
        List<ALittleOp5SuffixEx> suffix_ex_list = op_5_stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix());
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix());
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());

                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());

                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }


    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp4Stat op_4_stat) throws ALittleGuessException {

        ALittleGuess factor_guess_type = value_factor_stat.guessType();


        ALittleGuess suffix_guess_type = guessTypeForOp4(value_factor_stat, factor_guess_type, op_4_stat.getOp4Suffix());


        PsiElement last_src = op_4_stat.getOp4Suffix();
        List<ALittleOp4SuffixEx> suffix_ex_list = op_4_stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix());

                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix());

                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix());

                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());

                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());

                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleValueFactorStat value_factor_stat, ALittleOp3Stat op_3_stat) throws ALittleGuessException {
        ALittleGuess factor_guess_type = value_factor_stat.guessType();
        ALittleGuess suffix_guess_type = guessTypeForOp3(value_factor_stat, factor_guess_type, op_3_stat.getOp3Suffix());

        PsiElement last_src = op_3_stat.getOp3Suffix();
        List<ALittleOp3SuffixEx> suffix_ex_list = op_3_stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ex.getOp3Suffix());
                last_src = suffix_ex.getOp3Suffix();
            } else if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix());
                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix());
                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix());
                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());
                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());
                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }

    @NotNull
    public static List<ALittleGuess> guessTypes(ALittleValueOpStat value_op_stat) throws ALittleGuessException {
        ALittleValueFactorStat value_factor_stat = value_op_stat.getValueFactorStat();

        if (value_op_stat.getOp3Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp3Stat());

            guess_list.add(guess);
            return guess_list;
        } else if (value_op_stat.getOp4Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp4Stat());

            guess_list.add(guess);
            return guess_list;
        } else if (value_op_stat.getOp5Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp5Stat());

            guess_list.add(guess);
            return guess_list;
        } else if (value_op_stat.getOp6Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp6Stat());

            guess_list.add(guess);
            return guess_list;
        } else if (value_op_stat.getOp7Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp7Stat());

            guess_list.add(guess);
            return guess_list;
        } else if (value_op_stat.getOp8Stat() != null) {
            List<ALittleGuess> guess_list = new ArrayList<>();
            ALittleGuess guess = guessType(value_factor_stat, value_op_stat.getOp8Stat());

            guess_list.add(guess);
            return guess_list;
        }
        return value_factor_stat.guessTypes();
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp2Value op_2_value) throws ALittleGuessException {
        ALittleValueFactorStat value_factor_stat = op_2_value.getValueFactorStat();
        if (value_factor_stat == null)
            throw new ALittleGuessException(null, "单目运算没有目标表达式");

        ALittleGuess guess_info = value_factor_stat.guessType();


        String op_2 = op_2_value.getOp2().getText();
        // guess_type必须是逻辑运算符
        if (op_2.equals("!")) {
            if (!(guess_info instanceof ALittleGuessBool))
                throw new ALittleGuessException(value_factor_stat, "!运算符右边必须是bool类型.不能是:" + guess_info.getValue());
            // guess_type必须是数字
        } else if (op_2.equals("-")) {
            if (!(guess_info instanceof ALittleGuessInt) && !(guess_info instanceof ALittleGuessLong) && !(guess_info instanceof ALittleGuessDouble))
                throw new ALittleGuessException(value_factor_stat, "-运算符右边必须是int,double类型.不能是:" + guess_info.getValue());
        } else {
            throw new ALittleGuessException(op_2_value.getOp2(), "未知的运算符:" + op_2);
        }

        return guess_info;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp2Stat op_2_stat) throws ALittleGuessException {
        ALittleOp2Value op_2_value = op_2_stat.getOp2Value();
        ALittleGuess suffix_guess_type = guessType(op_2_value);


        PsiElement last_src = op_2_value;
        List<ALittleOp2SuffixEx> suffix_ex_list = op_2_stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_ex_list) {
            if (suffix_ex.getOp3Suffix() != null) {
                suffix_guess_type = guessTypeForOp3(last_src, suffix_guess_type, suffix_ex.getOp3Suffix());

                last_src = suffix_ex.getOp3Suffix();
            } else if (suffix_ex.getOp4Suffix() != null) {
                suffix_guess_type = guessTypeForOp4(last_src, suffix_guess_type, suffix_ex.getOp4Suffix());

                last_src = suffix_ex.getOp4Suffix();
            } else if (suffix_ex.getOp5Suffix() != null) {
                suffix_guess_type = guessTypeForOp5(last_src, suffix_guess_type, suffix_ex.getOp5Suffix());

                last_src = suffix_ex.getOp5Suffix();
            } else if (suffix_ex.getOp6Suffix() != null) {
                suffix_guess_type = guessTypeForOp6(last_src, suffix_guess_type, suffix_ex.getOp6Suffix());

                last_src = suffix_ex.getOp6Suffix();
            } else if (suffix_ex.getOp7Suffix() != null) {
                suffix_guess_type = guessTypeForOp7(last_src, suffix_guess_type, suffix_ex.getOp7Suffix());

                last_src = suffix_ex.getOp7Suffix();
            } else if (suffix_ex.getOp8Suffix() != null) {
                suffix_guess_type = guessTypeForOp8(last_src, suffix_guess_type, suffix_ex.getOp8Suffix());

                last_src = suffix_ex.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffix_ex, "未知的表达式");
            }
        }

        return suffix_guess_type;
    }

    // assign_or_call 填true表示赋值，否则是函数调用的参数传递
    public static void guessTypeEqual(ALittleGuess left_guess, PsiElement right_src, ALittleGuess right_guess, boolean assign_or_call, boolean instanceof_return) throws ALittleGuessException {
        // 如果值等于null，那么可以赋值
        if (right_guess.getValue().equals("null")) return;

        // 如果字符串直接相等，那么直接返回成功
        if (!(left_guess instanceof ALittleGuessTemplate) && !(right_guess instanceof ALittleGuessTemplate)
                && left_guess.getValue().equals(right_guess.getValue())) return;

        // const是否可以赋值给非const
        if (assign_or_call) {
            if (left_guess.is_const && !right_guess.is_const)
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ", 不能是:" + right_guess.getValue());
        } else {
            // 如果不是基本变量类型（排除any），基本都是值传递，函数调用时就不用检查const
            if (!(left_guess instanceof ALittleGuessPrimitive) || left_guess.getValueWithoutConst().equals("any")) {
                if (!left_guess.is_const && right_guess.is_const)
                    throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ", 不能是:" + right_guess.getValue());
            }
        }

        // 如果字符串直接相等，那么直接返回成功
        if (!(left_guess instanceof ALittleGuessTemplate) && !(right_guess instanceof ALittleGuessTemplate)
                && left_guess.getValueWithoutConst().equals(right_guess.getValueWithoutConst())) return;

        // 如果任何一方是any，那么就认为是相等
        if (left_guess instanceof ALittleGuessAny || right_guess instanceof ALittleGuessAny) return;

        // 基本变量类型检查
        if (left_guess instanceof ALittleGuessBool)
            throw new ALittleGuessException(right_src, "要求是bool,不能是:" + right_guess.getValue());

        if (left_guess instanceof ALittleGuessInt) {
            if (right_guess instanceof ALittleGuessLong)
                throw new ALittleGuessException(right_src, "long赋值给int，需要使用cast<int>()做强制类型转换");
            if (right_guess instanceof ALittleGuessDouble)
                throw new ALittleGuessException(right_src, "double赋值给int，需要使用cast<int>()做强制类型转换");
            throw new ALittleGuessException(right_src, "要求是int, 不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessLong) {
            if (right_guess instanceof ALittleGuessInt) return;

            if (right_guess instanceof ALittleGuessDouble)
                throw new ALittleGuessException(right_src, "double赋值给long，需要使用cast<long>()做强制类型转换");
            throw new ALittleGuessException(right_src, "要求是long, 不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessDouble) {
            if (right_guess instanceof ALittleGuessInt || right_guess instanceof ALittleGuessLong) return;
            throw new ALittleGuessException(right_src, "要求是double, 不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessString)
            throw new ALittleGuessException(right_src, "要求是string,不能是:" + right_guess.getValue());

        if (left_guess instanceof ALittleGuessMap) {
            if (!(right_guess instanceof ALittleGuessMap))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            try {
                guessTypeEqual(((ALittleGuessMap) left_guess).key_type, right_src, ((ALittleGuessMap) right_guess).key_type, assign_or_call, instanceof_return);
            } catch (Exception e) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            try {
                guessTypeEqual(((ALittleGuessMap) left_guess).value_type, right_src, ((ALittleGuessMap) right_guess).value_type, assign_or_call, instanceof_return);
            } catch (Exception e) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            return;
        }

        if (left_guess instanceof ALittleGuessList) {
            if (!(right_guess instanceof ALittleGuessList))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            try {
                guessTypeEqual(((ALittleGuessList) left_guess).sub_type, right_src, ((ALittleGuessList) right_guess).sub_type, assign_or_call, instanceof_return);
            } catch (Exception e) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
            return;
        }

        if (left_guess instanceof ALittleGuessFunctor) {
            if (!(right_guess instanceof ALittleGuessFunctor))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            ALittleGuessFunctor left_guess_functor = (ALittleGuessFunctor) left_guess;
            ALittleGuessFunctor right_guess_functor = (ALittleGuessFunctor) right_guess;

            if (left_guess_functor.param_list.size() != right_guess_functor.param_list.size()
                    || left_guess_functor.param_nullable_list.size() != right_guess_functor.param_nullable_list.size()
                    || left_guess_functor.return_list.size() != right_guess_functor.return_list.size()
                    || left_guess_functor.template_param_list.size() != right_guess_functor.template_param_list.size()
                    || left_guess_functor.await_modifier != right_guess_functor.await_modifier
                    || left_guess_functor.proto == null && right_guess_functor.proto != null
                    || left_guess_functor.proto != null && right_guess_functor.proto == null
                    || (left_guess_functor.proto != null && left_guess_functor.proto != right_guess_functor.proto)
                    || left_guess_functor.param_tail == null && right_guess_functor.param_tail != null
                    || left_guess_functor.param_tail != null && right_guess_functor.param_tail == null
                    || left_guess_functor.return_tail == null && right_guess_functor.return_tail != null
                    || left_guess_functor.return_tail != null && right_guess_functor.return_tail == null
            ) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            for (int i = 0; i < left_guess_functor.template_param_list.size(); ++i) {
                guessTypeEqual(left_guess_functor.template_param_list.get(i), right_src, right_guess_functor.template_param_list.get(i), assign_or_call, instanceof_return);
            }

            for (int i = 0; i < left_guess_functor.param_list.size(); ++i) {
                guessTypeEqual(left_guess_functor.param_list.get(i), right_src, right_guess_functor.param_list.get(i), assign_or_call, instanceof_return);

            }

            for (int i = 0; i < left_guess_functor.param_nullable_list.size(); ++i) {
                if (left_guess_functor.param_nullable_list.get(i) != right_guess_functor.param_nullable_list.get(i))
                    throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            for (int i = 0; i < left_guess_functor.return_list.size(); ++i) {
                guessTypeEqual(left_guess_functor.return_list.get(i), right_src, right_guess_functor.return_list.get(i), assign_or_call, instanceof_return);

            }
            return;
        }

        if (left_guess instanceof ALittleGuessClass) {
            if (right_guess instanceof ALittleGuessTemplate) {
                ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                if (right_guess_template.template_extends != null)
                    right_guess = right_guess_template.template_extends;
            }

            if (!(right_guess instanceof ALittleGuessClass))
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());

            if (left_guess.getValueWithoutConst().equals(right_guess.getValueWithoutConst())) return;

            boolean result = PsiHelper.isClassSuper(((ALittleGuessClass) right_guess).class_dec, left_guess.getValue());

            if (result) return;

            throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessStruct) {
            if (right_guess instanceof ALittleGuessTemplate) {
                ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                if (right_guess_template.template_extends != null)
                    right_guess = right_guess_template.template_extends;
            }

            if (!(right_guess instanceof ALittleGuessStruct)) {
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }

            if (left_guess.getValueWithoutConst().equals(right_guess.getValueWithoutConst())) return;

            boolean result = PsiHelper.isStructSuper(((ALittleGuessStruct) right_guess).struct_dec, left_guess.getValue());

            if (result) return;

            throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
        }

        if (left_guess instanceof ALittleGuessTemplate) {
            ALittleGuessTemplate left_guess_template = (ALittleGuessTemplate) left_guess;
            if (left_guess_template.template_extends != null) {
                guessTypeEqual(left_guess_template.template_extends, right_src, right_guess, assign_or_call, instanceof_return);
                return;
            } else if (left_guess_template.is_class) {
                if (right_guess instanceof ALittleGuessClass) {
                    return;
                } else if (right_guess instanceof ALittleGuessTemplate) {
                    ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                    if (right_guess_template.template_extends instanceof ALittleGuessClass || right_guess_template.is_class)
                        return;
                }
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            } else if (left_guess_template.is_struct) {
                if (right_guess instanceof ALittleGuessStruct) {
                    return;
                } else if (right_guess instanceof ALittleGuessTemplate) {
                    ALittleGuessTemplate right_guess_template = (ALittleGuessTemplate) right_guess;
                    if (right_guess_template.template_extends instanceof ALittleGuessStruct || right_guess_template.is_struct)
                        return;
                }
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            } else {
                if (!assign_or_call && !instanceof_return) return;

                if (right_guess instanceof ALittleGuessTemplate) {
                    if (left_guess.getValue().equals(right_guess.getValue()))
                        return;
                    if (left_guess.getValueWithoutConst().equals(right_guess.getValue()))
                        return;
                }
                throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
            }
        }

        throw new ALittleGuessException(right_src, "要求是" + left_guess.getValue() + ",不能是:" + right_guess.getValue());
    }
}
