package plugin.reference;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;

import java.util.List;

public class ALittleReferenceOpUtil {
    @NotNull
    private static ALittleGuess guessTypeForOp8Impl(String opString
            , PsiElement leftSrc, ALittleGuess leftGuessType
            , PsiElement rightSrc, ALittleGuess rightGuessType
            , ALittleOp8Suffix op8Suffix) throws ALittleGuessException {
        
        if (!leftGuessType.value.equals("bool")) {
            throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是bool类型.不能是:" + leftGuessType.value);
        }

        if (!rightGuessType.value.equals("bool")) {
            throw new ALittleGuessException(rightSrc, opString + "运算符右边边必须是bool类型.不能是:" + rightGuessType.value);
        }

        return leftGuessType;
    }

    @NotNull
    private static ALittleGuess guessTypeForOp8(PsiElement leftSrc
            , ALittleGuess leftGuessType
            , ALittleOp8Suffix op8Suffix) throws ALittleGuessException {
        String opString = op8Suffix.getOp8().getText();

        ALittleGuess suffixGuessType = null;
        PsiElement lastSrc = null;

        ALittleValueFactorStat valueFactorStat = op8Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op8Suffix.getOp2Value();
        if (valueFactorStat != null) {
            suffixGuessType = valueFactorStat.guessType();
            lastSrc = valueFactorStat;
        } else if (op2Value != null) {
            suffixGuessType = guessTypeForOp2Value(op2Value);
            lastSrc = op2Value;
        }

        List<ALittleOp8SuffixEe> suffixEeList = op8Suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffixEe : suffixEeList) {
            if (suffixEe.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEe.getOp3Suffix());
                lastSrc = suffixEe.getOp3Suffix();
            } else if (suffixEe.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEe.getOp4Suffix());
                lastSrc = suffixEe.getOp4Suffix();
            } else if (suffixEe.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEe.getOp5Suffix());
                lastSrc = suffixEe.getOp5Suffix();
            } else if (suffixEe.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEe.getOp6Suffix());
                lastSrc = suffixEe.getOp6Suffix();
            } else if (suffixEe.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEe.getOp7Suffix());
                lastSrc = suffixEe.getOp7Suffix();
            } else {
                throw new ALittleGuessException(suffixEe, "未知的表达式");
            }
        }

        return guessTypeForOp8Impl(opString, leftSrc, leftGuessType, lastSrc, suffixGuessType, op8Suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp7Impl(String opString
            , PsiElement leftSrc, ALittleGuess leftGuessType
            , PsiElement rightSrc, ALittleGuess rightGuessType
            , ALittleOp7Suffix op7Suffix) throws ALittleGuessException {
        if (!leftGuessType.value.equals("bool")) {
            throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是bool类型.不能是:" + leftGuessType.value);
        }

        if (!rightGuessType.value.equals("bool")) {
            throw new ALittleGuessException(rightSrc, opString + "运算符右边边必须是bool类型.不能是:" + rightGuessType.value);
        }

        return leftGuessType;
    }

    @NotNull
    private static ALittleGuess guessTypeForOp7(PsiElement leftSrc, ALittleGuess leftGuessType, ALittleOp7Suffix op7Suffix) throws ALittleGuessException {
        String opString = op7Suffix.getOp7().getText();

        ALittleGuess suffixGuessType = null;
        PsiElement lastSrc = null;

        ALittleValueFactorStat valueFactorStat = op7Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op7Suffix.getOp2Value();
        if (valueFactorStat != null) {
            suffixGuessType = valueFactorStat.guessType();
            lastSrc = valueFactorStat;
        } else if (op2Value != null) {
            suffixGuessType = guessTypeForOp2Value(op2Value);
            lastSrc = op2Value;
        }

        List<ALittleOp7SuffixEe> suffixEeList = op7Suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffixEe : suffixEeList) {
            if (suffixEe.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEe.getOp3Suffix());
                lastSrc = suffixEe.getOp3Suffix();
            } else if (suffixEe.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEe.getOp4Suffix());
                lastSrc = suffixEe.getOp4Suffix();
            } else if (suffixEe.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEe.getOp5Suffix());
                lastSrc = suffixEe.getOp5Suffix();
            } else if (suffixEe.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEe.getOp6Suffix());
                lastSrc = suffixEe.getOp6Suffix();
            } else {
                throw new ALittleGuessException(suffixEe, "未知的表达式");
            }
        }
        
        return guessTypeForOp7Impl(opString, leftSrc, leftGuessType, lastSrc, suffixGuessType, op7Suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp6Impl(String opString
            , PsiElement leftSrc, ALittleGuess leftGuessType
            , PsiElement rightSrc, ALittleGuess rightGuessType
            , ALittleOp6Suffix op6Suffix) throws ALittleGuessException {
        if (opString.equals("==") || opString.equals("!=")) {
            return ALittleGuessPrimitive.sBoolGuess;
        } else {
            if (leftGuessType.value.equals("int") || leftGuessType.value.equals("I64") || leftGuessType.value.equals("double")) {
                if (rightGuessType.value.equals("int") || rightGuessType.value.equals("I64") || rightGuessType.value.equals("double")) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(rightSrc, opString + "运算符左边是数字，那么右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }

            if (leftGuessType.value.equals("string")) {
                if (rightGuessType.value.equals("string")) {
                    return ALittleGuessPrimitive.sBoolGuess;
                }
                throw new ALittleGuessException(rightSrc, opString + "运算符左边是字符串，那么右边必须是string类型.不能是:" + rightGuessType.value);
            }

            throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是int,I64,double,string类型.不能是:" + leftGuessType.value);
        }
    }

    @NotNull
    private static ALittleGuess guessTypeForOp6(PsiElement leftSrc, ALittleGuess leftGuessType, ALittleOp6Suffix op6Suffix) throws ALittleGuessException {
        String opString = op6Suffix.getOp6().getText();

        ALittleGuess suffixGuessType = null;
        PsiElement lastSrc = null;

        ALittleValueFactorStat valueFactorStat = op6Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op6Suffix.getOp2Value();
        if (valueFactorStat != null) {
            suffixGuessType = valueFactorStat.guessType();
            lastSrc = valueFactorStat;
        } else if (op2Value != null) {
            suffixGuessType = guessTypeForOp2Value(op2Value);
            lastSrc = op2Value;
        }

        List<ALittleOp6SuffixEe> suffixEeList = op6Suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffixEe : suffixEeList) {
            if (suffixEe.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEe.getOp3Suffix());
                lastSrc = suffixEe.getOp3Suffix();
            } else if (suffixEe.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEe.getOp4Suffix());
                lastSrc = suffixEe.getOp4Suffix();
            } else if (suffixEe.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEe.getOp5Suffix());
                lastSrc = suffixEe.getOp5Suffix();
            } else {
                throw new ALittleGuessException(suffixEe, "未知的表达式");
            }
        }
        return guessTypeForOp6Impl(opString, leftSrc, leftGuessType, lastSrc, suffixGuessType, op6Suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp5Impl(String opString
            , PsiElement leftSrc, ALittleGuess leftGuessType
            , PsiElement rightSrc, ALittleGuess rightGuessType
            , ALittleOp5Suffix op5Suffix) throws ALittleGuessException {
        boolean left_check = leftGuessType.value.equals("int") || leftGuessType.value.equals("I64") || leftGuessType.value.equals("double") ||  leftGuessType.value.equals("string");
        if (!left_check) {
            throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是int,I64,double,string类型.不能是:" + leftGuessType.value);
        }

        boolean right_check = rightGuessType.value.equals("int") || rightGuessType.value.equals("I64") || rightGuessType.value.equals("double") ||  rightGuessType.value.equals("string");
        if (!right_check) {
            throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double,string类型.不能是:" + rightGuessType.value);
        }

        return ALittleGuessPrimitive.sStringGuess;
    }

    @NotNull
    private static ALittleGuess guessTypeForOp5(PsiElement leftSrc, ALittleGuess leftGuessType, ALittleOp5Suffix op5Suffix) throws ALittleGuessException {
        String opString = op5Suffix.getOp5().getText();

        ALittleGuess suffixGuessType = null;
        PsiElement lastSrc = null;

        ALittleValueFactorStat valueFactorStat = op5Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op5Suffix.getOp2Value();
        if (valueFactorStat != null) {
            suffixGuessType = valueFactorStat.guessType();
            lastSrc = valueFactorStat;
        } else if (op2Value != null) {
            suffixGuessType = guessTypeForOp2Value(op2Value);
            lastSrc = op2Value;
        }

        List<ALittleOp5SuffixEe> suffixEeList = op5Suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffixEe : suffixEeList) {
            if (suffixEe.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEe.getOp3Suffix());
                lastSrc = suffixEe.getOp3Suffix();
            } else if (suffixEe.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEe.getOp4Suffix());
                lastSrc = suffixEe.getOp4Suffix();
            } else {
                throw new ALittleGuessException(suffixEe, "未知的表达式");
            }
        }

        return guessTypeForOp5Impl(opString, leftSrc, leftGuessType, lastSrc, suffixGuessType, op5Suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp4Impl(String opString
            , PsiElement leftSrc, ALittleGuess leftGuessType
            , PsiElement rightSrc, ALittleGuess rightGuessType
            , ALittleOp4Suffix op4Suffix) throws ALittleGuessException {
        if (leftGuessType.value.equals("int") || leftGuessType.value.equals("I64")) {
            if (rightGuessType.value.equals("int") || rightGuessType.value.equals("I64")) {
                return leftGuessType;
            } else if (rightGuessType.value.equals("double")) {
                return rightGuessType;
            } else {
                throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }
        }

        if (leftGuessType.value.equals("double")) {
            if (rightGuessType.value.equals("int") || rightGuessType.value.equals("I64")) {
                return leftGuessType;
            } else if (rightGuessType.value.equals("double")) {
                return rightGuessType;
            } else {
                throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }
        }

        throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是int,I64,double类型.不能是:" + leftGuessType.value);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp4(PsiElement leftSrc, ALittleGuess leftGuessType, ALittleOp4Suffix op4Suffix) throws ALittleGuessException {
        String opString = op4Suffix.getOp4().getText();

        ALittleGuess suffixGuessType = null;
        PsiElement lastSrc = null;

        ALittleValueFactorStat valueFactorStat = op4Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op4Suffix.getOp2Value();
        if (valueFactorStat != null) {
            suffixGuessType = valueFactorStat.guessType();
            lastSrc = valueFactorStat;
        } else if (op2Value != null) {
            suffixGuessType = guessTypeForOp2Value(op2Value);
            lastSrc = op2Value;
        }

        List<ALittleOp4SuffixEe> suffixEeList = op4Suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffixEe : suffixEeList) {
            if (suffixEe.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEe.getOp3Suffix());
                lastSrc = suffixEe.getOp3Suffix();
            } else {
                throw new ALittleGuessException(suffixEe, "未知的表达式");
            }
        }

        return guessTypeForOp4Impl(opString, leftSrc, leftGuessType, lastSrc, suffixGuessType, op4Suffix);
    }

    @NotNull
    private static ALittleGuess guessTypeForOp3(PsiElement leftSrc, ALittleGuess leftGuessType, ALittleOp3Suffix op3Suffix) throws ALittleGuessException {
        String opString = op3Suffix.getOp3().getText();

        ALittleGuess rightGuessType = null;
        PsiElement rightSrc = null;

        ALittleValueFactorStat valueFactorStat = op3Suffix.getValueFactorStat();
        ALittleOp2Value op2Value = op3Suffix.getOp2Value();
        if (valueFactorStat != null) {
            rightGuessType = valueFactorStat.guessType();
            rightSrc = valueFactorStat;
        } else if (op2Value != null) {
            rightGuessType = guessTypeForOp2Value(op2Value);
            rightSrc = op2Value;
        } else {
            throw new ALittleGuessException(op3Suffix, "未知的表达式");
        }

        if (leftGuessType.value.equals("int")) {
            if (rightGuessType.value.equals("int")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return leftGuessType;
            } else if (rightGuessType.value.equals("I64")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return rightGuessType;
            } else if (rightGuessType.value.equals("double")) {
                return rightGuessType;
            } else {
                throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }
        }

        if (leftGuessType.value.equals("I64")) {
            if (rightGuessType.value.equals("int") || rightGuessType.value.equals("I64")) {
                // 这个是特殊的
                if (opString.equals("/")) {
                    return ALittleGuessPrimitive.sDoubleGuess;
                }
                return leftGuessType;
            } else if (rightGuessType.value.equals("double")) {
                return rightGuessType;
            } else {
                throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }
        }

        if (leftGuessType.value.equals("double")) {
            if (rightGuessType.value.equals("int") || rightGuessType.value.equals("I64")) {
                return leftGuessType;
            } else if (rightGuessType.value.equals("double")) {
                return rightGuessType;
            } else {
                throw new ALittleGuessException(rightSrc, opString + "运算符右边必须是int,I64,double类型.不能是:" + rightGuessType.value);
            }
        }

        throw new ALittleGuessException(leftSrc, opString + "运算符左边必须是int,I64,double类型.不能是:" + leftGuessType.value);
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp8Stat op8Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op8Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp8(valueFactorStat, factorGuessType, op8Stat.getOp8Suffix());

        PsiElement lastSrc = op8Stat.getOp8Suffix();
        List<ALittleOp8SuffixEx> suffixEx_list = op8Stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                if (suffixGuessType == null) return null;
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp7Stat op7Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op7Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp7(valueFactorStat, factorGuessType, op7Stat.getOp7Suffix());

        PsiElement lastSrc = op7Stat.getOp7Suffix();
        List<ALittleOp7SuffixEx> suffixEx_list = op7Stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp6Stat op6Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op6Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp6(valueFactorStat, factorGuessType, op6Stat.getOp6Suffix());

        PsiElement lastSrc = op6Stat.getOp6Suffix();
        List<ALittleOp6SuffixEx> suffixEx_list = op6Stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEx.getOp6Suffix());
                lastSrc = suffixEx.getOp6Suffix();
            } else if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp5Stat op5Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op5Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp5(valueFactorStat, factorGuessType, op5Stat.getOp5Suffix());

        PsiElement lastSrc = op5Stat.getOp5Suffix();
        List<ALittleOp5SuffixEx> suffixEx_list = op5Stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEx.getOp5Suffix());
                lastSrc = suffixEx.getOp5Suffix();
            } else if (suffixEx.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEx.getOp6Suffix());
                lastSrc = suffixEx.getOp6Suffix();
            } else if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp4Stat op4Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op4Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp4(valueFactorStat, factorGuessType, op4Stat.getOp4Suffix());

        PsiElement lastSrc = op4Stat.getOp4Suffix();
        List<ALittleOp4SuffixEx> suffixEx_list = op4Stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEx.getOp4Suffix());
                lastSrc = suffixEx.getOp4Suffix();
            } else if (suffixEx.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEx.getOp5Suffix());
                lastSrc = suffixEx.getOp5Suffix();
            } else if (suffixEx.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEx.getOp6Suffix());
                lastSrc = suffixEx.getOp6Suffix();
            } else if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }


    @NotNull
    public static ALittleGuess guessType(ALittleOp3Stat op3Stat) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op3Stat.getValueFactorStat();
        ALittleGuess factorGuessType = valueFactorStat.guessType();

        ALittleGuess suffixGuessType = guessTypeForOp3(valueFactorStat, factorGuessType, op3Stat.getOp3Suffix());

        PsiElement lastSrc = op3Stat.getOp3Suffix();
        List<ALittleOp3SuffixEx> suffixEx_list = op3Stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEx.getOp3Suffix());
                lastSrc = suffixEx.getOp3Suffix();
            } else if (suffixEx.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEx.getOp4Suffix());
                lastSrc = suffixEx.getOp4Suffix();
            } else if (suffixEx.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEx.getOp5Suffix());
                lastSrc = suffixEx.getOp5Suffix();
            } else if (suffixEx.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEx.getOp6Suffix());
                lastSrc = suffixEx.getOp6Suffix();
            } else if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp2Value op2Value) throws ALittleGuessException {
        ALittleValueFactorStat valueFactorStat = op2Value.getValueFactorStat();
        ALittleGuess suffixGuessType = valueFactorStat.guessType();

        ALittleGuess guess_info = valueFactorStat.guessType();

        String op_2 = op2Value.getOp2().getText();
        // guess_type必须是逻辑运算符
        if (op_2.equals("!")) {
            if (!guess_info.value.equals("bool")) {
                throw new ALittleGuessException(valueFactorStat, "!运算符右边必须是bool类型.不能是:" + guess_info.value);
            }
            // guess_type必须是数字
        } else if (op_2.equals("-")) {
            if (!guess_info.value.equals("int") && !guess_info.value.equals("I64") && !guess_info.value.equals("double")) {
                throw new ALittleGuessException(valueFactorStat, "-运算符右边必须是int,double类型.不能是:" + guess_info.value);
            }
        } else {
            throw new ALittleGuessException(op2Value.getOp2(), "未知的运算符:" + op_2);
        }

        return suffixGuessType;
    }

    @NotNull
    public static ALittleGuess guessType(ALittleOp2Stat op2Stat) throws ALittleGuessException {
        ALittleOp2Value op2Value = op2Stat.getOp2Value();
        ALittleGuess suffixGuessType = guessType(op2Value);

        PsiElement lastSrc = op2Value;
        List<ALittleOp2SuffixEx> suffixEx_list = op2Stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffixEx : suffixEx_list) {
            if (suffixEx.getOp3Suffix() != null) {
                suffixGuessType = guessTypeForOp3(lastSrc, suffixGuessType, suffixEx.getOp3Suffix());
                lastSrc = suffixEx.getOp3Suffix();
            } else if (suffixEx.getOp4Suffix() != null) {
                suffixGuessType = guessTypeForOp4(lastSrc, suffixGuessType, suffixEx.getOp4Suffix());
                lastSrc = suffixEx.getOp4Suffix();
            } else if (suffixEx.getOp5Suffix() != null) {
                suffixGuessType = guessTypeForOp5(lastSrc, suffixGuessType, suffixEx.getOp5Suffix());
                lastSrc = suffixEx.getOp5Suffix();
            } else if (suffixEx.getOp6Suffix() != null) {
                suffixGuessType = guessTypeForOp6(lastSrc, suffixGuessType, suffixEx.getOp6Suffix());
                lastSrc = suffixEx.getOp6Suffix();
            } else if (suffixEx.getOp7Suffix() != null) {
                suffixGuessType = guessTypeForOp7(lastSrc, suffixGuessType, suffixEx.getOp7Suffix());
                lastSrc = suffixEx.getOp7Suffix();
            } else if (suffixEx.getOp8Suffix() != null) {
                suffixGuessType = guessTypeForOp8(lastSrc, suffixGuessType, suffixEx.getOp8Suffix());
                lastSrc = suffixEx.getOp8Suffix();
            } else {
                throw new ALittleGuessException(suffixEx, "未知的表达式");
            }
        }

        return suffixGuessType;
    }

    public static ALittleGuess guessTypeForOp2Value(ALittleOp2Value op2Value) throws ALittleGuessException {
        ALittleGuess guessType = op2Value.getValueFactorStat().guessType();
        String opString = op2Value.getOp2().getText();

        if (opString.equals("!")) {
            if (!guessType.value.equals("bool")) {
                throw new ALittleGuessException(op2Value.getValueFactorStat(), "!运算符的右边必须是bool类型，不能是:" + guessType.value);
            }
            return guessType;
        }

        if (opString.equals("-")) {
            if (!guessType.value.equals("int") && !guessType.value.equals("I64") && !guessType.value.equals("double")) {
                throw new ALittleGuessException(op2Value.getValueFactorStat(), "-运算符的右边必须是int,I64,double类型，不能是:" + guessType.value);
            }
            return guessType;
        }

        throw new ALittleGuessException(op2Value.getOp2(), "未知的运算符");
    }

    public static void guessTypeEqual(@NotNull PsiElement leftSrc, @NotNull ALittleGuess leftGuess
            , @NotNull PsiElement rightSrc, @NotNull ALittleGuess rightGuess) throws ALittleGuessException {
        // 如果字符串直接相等，那么就直接返回成功
        if (leftGuess.value.equals(rightGuess.value)) return;

        // 如果任何一方是any，那么就认为可以相等
        if (leftGuess.value.equals("any") || rightGuess.value.equals("any")) return;
        // 如果值等于null，那么可以赋值
        if (rightGuess.value.equals("null")) return;

        if (leftGuess.value.equals("bool")) {
            throw new ALittleGuessException(rightSrc, "要求是bool,不能是:" + rightGuess.value);
        }

        if (leftGuess.value.equals("int")) {
            if (rightGuess.value.equals("I64")) {
                throw new ALittleGuessException(rightSrc, "I64赋值给int，需要使用cast<int>()做强制类型转换");
            }

            if (rightGuess.value.equals("double")) {
                throw new ALittleGuessException(rightSrc, "double赋值给int，需要使用cast<int>()做强制类型转换");
            }

            throw new ALittleGuessException(rightSrc, "要求是int, 不能是:" + rightGuess.value);
        }

        if (leftGuess.value.equals("I64")) {
            if (rightGuess.value.equals("int")) return;

            if (rightGuess.value.equals("double")) {
                throw new ALittleGuessException(rightSrc, "double赋值给I64，需要使用cast<I64>()做强制类型转换");
            }

            throw new ALittleGuessException(rightSrc, "要求是I64, 不能是:" + rightGuess.value);
        }

        if (leftGuess.value.equals("double")) {
            if (rightGuess.value.equals("int") || rightGuess.value.equals("I64")) return;
            throw new ALittleGuessException(rightSrc, "要求是double, 不能是:" + rightGuess.value);
        }

        if (leftGuess.value.equals("string")) {
            throw new ALittleGuessException(rightSrc, "要求是string,不能是:" + rightGuess.value);
        }

        if (leftGuess instanceof ALittleGuessMap) {
            if (!(rightGuess instanceof ALittleGuessMap)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            try {
                guessTypeEqual(leftSrc, ((ALittleGuessMap)leftGuess).keyType, rightSrc, ((ALittleGuessMap)rightGuess).keyType);
                guessTypeEqual(leftSrc, ((ALittleGuessMap)leftGuess).valueType, rightSrc, ((ALittleGuessMap)rightGuess).valueType);
            } catch (ALittleGuessException ignored) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessList) {
            if (!(rightGuess instanceof ALittleGuessList)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            try {
                guessTypeEqual(leftSrc, ((ALittleGuessList)leftGuess).subType, rightSrc, ((ALittleGuessList)rightGuess).subType);
            } catch (ALittleGuessException ignored) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessFunctor) {
            if (!(rightGuess instanceof ALittleGuessFunctor)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }
            ALittleGuessFunctor leftGuessFunctor = (ALittleGuessFunctor)leftGuess;
            ALittleGuessFunctor rightGuessFunctor = (ALittleGuessFunctor)rightGuess;

            if (leftGuessFunctor.functorParamList.size() != rightGuessFunctor.functorParamList.size()
                || leftGuessFunctor.functorReturnList.size() != rightGuessFunctor.functorReturnList.size()
                || leftGuessFunctor.functorAwait != rightGuessFunctor.functorAwait
                || leftGuessFunctor.functorProto == null && rightGuessFunctor.functorProto != null
                || leftGuessFunctor.functorProto != null && rightGuessFunctor.functorProto == null
                || (leftGuessFunctor.functorProto != null && !leftGuessFunctor.functorProto.equals(rightGuessFunctor.functorProto))
                || leftGuessFunctor.functorParamTail == null && rightGuessFunctor.functorParamTail != null
                || leftGuessFunctor.functorParamTail != null && rightGuessFunctor.functorParamTail == null
                || leftGuessFunctor.functorReturnTail == null && rightGuessFunctor.functorReturnTail != null
                || leftGuessFunctor.functorReturnTail != null && rightGuessFunctor.functorReturnTail == null
            ) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            for (int i = 0; i < leftGuessFunctor.functorParamList.size(); ++i) {
                guessTypeEqual(leftSrc, leftGuessFunctor.functorParamList.get(i), rightSrc, rightGuessFunctor.functorParamList.get(i));
            }

            for (int i = 0; i < leftGuessFunctor.functorReturnList.size(); ++i) {
                guessTypeEqual(leftSrc, leftGuessFunctor.functorReturnList.get(i), rightSrc, rightGuessFunctor.functorReturnList.get(i));
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessClass) {
            if (rightGuess instanceof ALittleGuessClassTemplate) {
                rightGuess = ((ALittleGuessClassTemplate)rightGuess).templateExtends;
            }
            if (!(rightGuess instanceof ALittleGuessClass)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            if (leftGuess.value.equals(rightGuess.value)) return;


            if (ALittleReferenceUtil.IsClassSuper(((ALittleGuessClass)leftGuess).element, rightGuess.value)) return;
            if (ALittleReferenceUtil.IsClassSuper(((ALittleGuessClass)rightGuess).element, leftGuess.value)) return;

            throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
        }

        if (leftGuess instanceof ALittleGuessClassTemplate) {
            ALittleGuessClassTemplate leftGuessClassTemplate = (ALittleGuessClassTemplate)leftGuess;
            if (leftGuessClassTemplate.templateExtends != null) {
                guessTypeEqual(leftSrc, leftGuessClassTemplate.templateExtends, rightSrc, rightGuess);
                return;
            }
            return;
        }

        if (leftGuess instanceof ALittleGuessStruct) {
            if (!(rightGuess instanceof ALittleGuessStruct)) {
                throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
            }

            if (leftGuess.value.equals(rightGuess.value)) return;

            if (ALittleReferenceUtil.IsStructSuper(((ALittleGuessStruct)leftGuess).element, rightGuess.value)) return;
            if (ALittleReferenceUtil.IsStructSuper(((ALittleGuessStruct)rightGuess).element, leftGuess.value)) return;

            throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
        }

        throw new ALittleGuessException(rightSrc, "要求是" + leftGuess.value + ",不能是:" + rightGuess.value);
    }
}
