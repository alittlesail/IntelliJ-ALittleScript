package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleOpNewStatReference extends ALittleReference<ALittleOpNewStat> {
    public ALittleOpNewStatReference(@NotNull ALittleOpNewStat element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        if (myElement.getCustomType() != null) {
            return myElement.getCustomType().guessTypes();
        } else if (myElement.getGenericType() != null) {
            return myElement.getGenericType().guessTypes();
        }

        return new ArrayList<>();
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleValueStat> valueStatList = myElement.getValueStatList();

        if (myElement.getGenericType() != null) {
            if (valueStatList.size() > 0) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "创建容器实例对象不能有参数");
            }
            return;
        }

        if (myElement.getCustomType() != null) {
            ALittleCustomType customType = myElement.getCustomType();
            ALittleReferenceUtil.GuessTypeInfo guessType = customType.guessType();
            if (guessType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                if (valueStatList.size() > 0) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的结构体不能有参数");
                }
                return;
            }

            if (guessType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec) guessType.element;
                List<ALittleClassCtorDec> ctor_decList = classDec.getClassCtorDecList();
                if (ctor_decList.size() > 1) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数个数不能超过1个");
                }

                if (ctor_decList.size() == 0) {
                    if (valueStatList.size() > 0) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数没有参数");
                    }
                    return;
                }

                ALittleMethodParamDec param_dec = ctor_decList.get(0).getMethodParamDec();
                if (param_dec == null) {
                    if (valueStatList.size() > 0) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数没有参数");
                    }
                    return;
                }

                List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
                List<ALittleReferenceUtil.GuessTypeInfo> param_type_list = new ArrayList<>();
                for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                    param_type_list.add(param_one_dec.getAllType().guessType());
                }

                if (param_type_list.size() < valueStatList.size()) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数调用最多需要" + param_type_list.size() + "个参数,不能是:" + valueStatList.size() + "个");
                }

                for (int i = 0; i < valueStatList.size(); ++i) {
                    try {
                        ALittleReferenceOpUtil.guessTypeEqual(param_one_decList.get(i), param_type_list.get(i),
                                valueStatList.get(i), valueStatList.get(i).guessType());
                    } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + e.getError());
                    }
                }
                return;
            }

            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "只能new结构体和类");
        }
    }
}
