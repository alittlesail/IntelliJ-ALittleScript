package plugin.parameter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.alittle.PsiHelper;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;

import java.util.ArrayList;
import java.util.List;


public class ALittleParameterInfoHandler implements ParameterInfoHandler<ALittlePropertyValueMethodCallStat, String> {
    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(@NotNull LookupElement item, ParameterInfoContext context) {
        return new Object[0];
    }

    @Override
    public ALittlePropertyValueMethodCallStat findElementForParameterInfo(CreateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittlePropertyValueMethodCallStat stat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCallStat.class, false);
        if (stat != null) {
            do {
                PsiReference ref = stat.getReference();
                if (!(ref instanceof ALittlePropertyValueMethodCallStatReference)) break;
                ALittlePropertyValueMethodCallStatReference reference = (ALittlePropertyValueMethodCallStatReference)ref;

                PsiElement pre_type = reference.guessTypesForPreType();
                if (!(pre_type instanceof ALittleMethodNameDec)) break;
                ALittleMethodNameDec method_name_dec = (ALittleMethodNameDec) pre_type;
                PsiElement method_dec = method_name_dec.getParent();

                List<String> param_name_list = new ArrayList<>();
                // getter函数使用()来调用，只有这种情况 Class.getter_x(object)
                if (method_dec instanceof ALittleClassGetterDec) {
                    // 第一个参数就是getter所在的类
                    ALittleClassDec class_dec = PsiHelper.findClassDecFromParent(method_dec);
                    if (class_dec != null) {
                        ALittleClassNameDec class_name_dec = class_dec.getClassNameDec();
                        if (class_name_dec != null) param_name_list.add("this:" + class_name_dec.getText());
                    }
                    // setter函数使用()来调用，只有这种情况 Class.setter_x(object, value)
                } else if (method_dec instanceof ALittleClassSetterDec) {
                    // 第一个参数就是setter所在的类
                    ALittleClassDec class_dec = PsiHelper.findClassDecFromParent(method_dec);
                    if (class_dec != null) {
                        ALittleClassNameDec class_name_dec = class_dec.getClassNameDec();
                        if (class_name_dec != null) param_name_list.add("this:" + class_name_dec.getText());
                    }
                    ALittleClassSetterDec dec = (ALittleClassSetterDec) method_dec;
                    ALittleMethodParamOneDec one_dec = dec.getMethodParamOneDec();
                    if (one_dec != null) {
                        param_name_list.add(one_dec.getText());
                    }
                } else if (method_dec instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec dec = (ALittleClassMethodDec) method_dec;

                    // 如果是使用类的方式调用，那么还需要加上一个参数
                    ALittleClassDec class_dec = reference.guessClassNameInvoke();
                    if (class_dec != null) {
                        ALittleClassNameDec class_name_dec = class_dec.getClassNameDec();
                        if (class_name_dec != null) param_name_list.add("this:" + class_name_dec.getText());
                    }

                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                            param_name_list.add(one_dec.getText());
                        }
                    }
                } else if (method_dec instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec dec = (ALittleClassStaticDec) method_dec;
                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                            param_name_list.add(one_dec.getText());
                        }
                    }
                } else if (method_dec instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec) method_dec;
                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                            param_name_list.add(one_dec.getText());
                        }
                    }
                }
                List<String> list = new ArrayList<>();
                list.add(String.join(", ", param_name_list));
                context.setItemsToShow(list.toArray());
            } while (false);
            return stat;
        }
        return null;
    }

    @Override
    public void showParameterInfo(@NotNull ALittlePropertyValueMethodCallStat element, CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Override
    public ALittlePropertyValueMethodCallStat findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        PsiFile file = context.getFile();
        return PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCallStat.class, false);
    }

    @Override
    public void updateParameterInfo(ALittlePropertyValueMethodCallStat element, UpdateParameterInfoContext context) {
        int index = ParameterInfoUtils.getCurrentParameterIndex(element.getNode(), context.getOffset(), ALittleTypes.COMMA);
        context.setCurrentParameter(index);
    }

    @Override
    public void updateUI(String o, @NotNull ParameterInfoUIContext context) {
        if (o == null) {
            context.setUIComponentEnabled(false);
            return;
        }
        int index = context.getCurrentParameterIndex();
        String[] string_list = o.split(", ");

        int start_index = 0;
        int end_index = 0;
        if (string_list.length > index) {
            for (int i = 0; i <= index; ++i) {
                if (i != index) {
                    start_index += string_list[i].length() + 2;
                } else {
                    end_index = start_index + string_list[i].length();
                }
            }
        }
        context.setupUIComponentPresentation(o, start_index, end_index, !context.isUIComponentEnabled(), false, false,
                context.getDefaultParameterColor());
    }
}
