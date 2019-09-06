package plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.component.ALittleIcons;
import plugin.index.ALittleTreeChangeListener;
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
        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "ALittleOpNewStat出现未知的子节点");
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

            if (guessType.type == ALittleReferenceUtil.GuessType.GT_CLASS_TEMPLATE
                && guessType.classTemplateExtends != null) {
                guessType = guessType.classTemplateExtends;
            }

            if (guessType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                ALittleClassDec classDec = (ALittleClassDec) guessType.element;
                List<ALittleClassCtorDec> ctorDecList = classDec.getClassCtorDecList();
                if (ctorDecList.size() > 1) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数个数不能超过1个");
                }

                if (ctorDecList.size() == 0) {
                    if (valueStatList.size() > 0) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数没有参数");
                    }
                    return;
                }

                ALittleMethodParamDec param_dec = ctorDecList.get(0).getMethodParamDec();
                if (param_dec == null) {
                    if (valueStatList.size() > 0) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数没有参数");
                    }
                    return;
                }

                List<ALittleMethodParamOneDec> param_oneDecList = param_dec.getMethodParamOneDecList();
                List<ALittleReferenceUtil.GuessTypeInfo> param_type_list = new ArrayList<>();
                for (ALittleMethodParamOneDec param_one_dec : param_oneDecList) {
                    param_type_list.add(param_one_dec.getAllType().guessType());
                }

                if (param_type_list.size() < valueStatList.size()) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "new的类的构造函数调用最多需要" + param_type_list.size() + "个参数,不能是:" + valueStatList.size() + "个");
                }

                for (int i = 0; i < valueStatList.size(); ++i) {
                    ALittleValueStat valueStat = valueStatList.get(i);
                    try {
                        ALittleReferenceOpUtil.guessTypeEqual(param_oneDecList.get(i), param_type_list.get(i), valueStat, valueStat.guessType());
                    } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                        throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "第" + (i + 1) + "个参数类型和函数定义的参数类型不同:" + e.getError());
                    }
                }
                return;
            }

            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "只能new结构体和类");
        }
    }
}
