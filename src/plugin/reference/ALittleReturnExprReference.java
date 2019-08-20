package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleReturnExprReference extends ALittleReference<ALittleReturnExpr> {
    public ALittleReturnExprReference(@NotNull ALittleReturnExpr element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getReturnYield() != null) {
            // 对于ReturnYield就不需要做返回值检查
            // 对所在函数进行检查，必须要有async和await表示
            // 获取对应的函数对象

            PsiElement element = null;

            PsiElement parent = myElement;
            while (parent != null) {
                if (parent instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec methodDec = (ALittleClassMethodDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                } else if (parent instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec methodDec = (ALittleClassStaticDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                } else if (parent instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec methodDec = (ALittleGlobalMethodDec) parent;
                    if (methodDec.getCoModifier() == null) {
                        element = methodDec.getMethodNameDec();
                        if (element == null) element = methodDec;
                    }
                    break;
                }

                parent = parent.getParent();
            }

            if (element != null) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "函数内部使用了return yield表达式，所以必须使用async或await修饰");
            }
            return;
        }
        
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();
        List<ALittleAllType> returnTypeList = new ArrayList<>();

        // 获取对应的函数对象
        PsiElement parent = myElement;
        while (parent != null) {
            if (parent instanceof ALittleClassGetterDec) {
                ALittleClassGetterDec getterDec = (ALittleClassGetterDec) parent;
                returnTypeList = new ArrayList<>();
                ALittleAllType returnTypeDec = getterDec.getAllType();
                if (returnTypeDec != null)
                    returnTypeList.add(returnTypeDec);
                break;
            } else if (parent instanceof ALittleClassMethodDec) {
                ALittleClassMethodDec methodDec = (ALittleClassMethodDec) parent;
                ALittleMethodReturnDec return_dec = methodDec.getMethodReturnDec();
                if (return_dec != null) returnTypeList = return_dec.getAllTypeList();
                break;
            } else if (parent instanceof ALittleClassStaticDec) {
                ALittleClassStaticDec methodDec = (ALittleClassStaticDec) parent;
                ALittleMethodReturnDec return_dec = methodDec.getMethodReturnDec();
                if (return_dec != null) returnTypeList = return_dec.getAllTypeList();
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                ALittleGlobalMethodDec methodDec = (ALittleGlobalMethodDec) parent;
                ALittleMethodReturnDec return_dec = methodDec.getMethodReturnDec();
                if (return_dec != null) returnTypeList = return_dec.getAllTypeList();
                break;
            }

            parent = parent.getParent();
        }

        List<ALittleReferenceUtil.GuessTypeInfo> guessTypeList = null;
        // 如果返回值只有一个函数调用
        if (valueStatList.size() == 1 && returnTypeList.size() > 1) {
            ALittleValueStat value_stat = valueStatList.get(0);
            guessTypeList = value_stat.guessTypes();
            if (guessTypeList.size() != returnTypeList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "return的函数调用的返回值数量和函数定义的返回值数量不相等");
            }
        } else {
            if (returnTypeList.size() != valueStatList.size()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "return的返回值数量和函数定义的返回值数量不相等");
            }
            guessTypeList = new ArrayList<>();
            for (ALittleAllType allType : returnTypeList) {
                guessTypeList.add(allType.guessType());
            }
        }

        // 每个类型依次检查
        for (int i = 0; i < guessTypeList.size(); ++i) {
            ALittleValueStat value_stat = valueStatList.get(i);
            try {
                ALittleReferenceOpUtil.guessTypeEqual(returnTypeList.get(i), guessTypeList.get(i), value_stat, value_stat.guessType());
            } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "return的第" + (i + 1) + "个返回值数量和函数定义的返回值类型不同:" + e.getError());
            }
        }
    }
}
