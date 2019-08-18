package plugin.parameter;

import com.intellij.codeInsight.hints.HintInfo;
import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleUtil;
import plugin.alittle.PsiHelper;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallStatReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleParameterHintsProvider implements InlayParameterHintsProvider {
    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement psiElement) {
        List<InlayInfo> result = new ArrayList<>();

        if (psiElement instanceof ALittlePropertyValueMethodCallStat) {
            do {
                ALittlePropertyValueMethodCallStat stat = (ALittlePropertyValueMethodCallStat) psiElement;
                // 获取reference
                PsiReference ref = stat.getReference();
                if (!(ref instanceof ALittlePropertyValueMethodCallStatReference)) break;
                ALittlePropertyValueMethodCallStatReference reference = (ALittlePropertyValueMethodCallStatReference)ref;

                // 获取函数对象
                PsiElement preType = reference.guessTypesForPreType();
                if (!(preType instanceof ALittleMethodNameDec)) break;
                ALittleMethodNameDec method_name_dec = (ALittleMethodNameDec) preType;
                PsiElement method_dec = method_name_dec.getParent();

                // 收集参数名
                List<String> param_name_list = new ArrayList<>();

                // getter函数使用()来调用，只有这种情况 Class.getter_x(object)
                if (method_dec instanceof ALittleClassGetterDec) {
                    // 第一个参数就是getter所在的类
                    if (PsiHelper.findClassDecFromParent(method_dec) != null) {
                        param_name_list.add("this");
                    }
                // setter函数使用()来调用，只有这种情况 Class.setter_x(object, value)
                } else if (method_dec instanceof ALittleClassSetterDec) {
                    // 第一个参数就是setter所在的类
                    if (PsiHelper.findClassDecFromParent(method_dec) != null) {
                        param_name_list.add("this");
                    }
                    ALittleClassSetterDec dec = (ALittleClassSetterDec) method_dec;
                    ALittleMethodParamOneDec one_dec = dec.getMethodParamOneDec();
                    if (one_dec != null && one_dec.getMethodParamNameDec() != null) {
                        param_name_list.add(one_dec.getMethodParamNameDec().getText());
                    }
                } else if (method_dec instanceof ALittleClassMethodDec) {
                    ALittleClassMethodDec dec = (ALittleClassMethodDec) method_dec;

                    // 如果是使用类的方式调用，那么还需要加上一个参数
                    ALittleClassDec classDec = reference.guessClassNameInvoke();
                    if (classDec != null) {
                        param_name_list.add("this");
                    }

                    // 获取其他参数
                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_decList = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_decList) {
                            if (one_dec.getMethodParamNameDec() != null)
                                param_name_list.add(one_dec.getMethodParamNameDec().getText());
                        }
                    }
                } else if (method_dec instanceof ALittleClassStaticDec) {
                    ALittleClassStaticDec dec = (ALittleClassStaticDec) method_dec;
                    // 获取参数
                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_decList = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_decList) {
                            if (one_dec.getMethodParamNameDec() != null)
                                param_name_list.add(one_dec.getMethodParamNameDec().getText());
                        }
                    }
                } else if (method_dec instanceof ALittleGlobalMethodDec) {
                    ALittleGlobalMethodDec dec = (ALittleGlobalMethodDec) method_dec;
                    // 获取参数
                    ALittleMethodParamDec param_dec = dec.getMethodParamDec();
                    if (param_dec != null) {
                        List<ALittleMethodParamOneDec> one_decList = param_dec.getMethodParamOneDecList();
                        for (ALittleMethodParamOneDec one_dec : one_decList) {
                            if (one_dec.getMethodParamNameDec() != null)
                                param_name_list.add(one_dec.getMethodParamNameDec().getText());
                        }
                    }
                }

                // 构建对象
                List<ALittleValueStat> value_stat_list = stat.getValueStatList();
                for (int i = 0; i < value_stat_list.size(); ++i) {
                    if (i >= param_name_list.size()) break;
                    String name = param_name_list.get(i);
                    ALittleValueStat value_stat = value_stat_list.get(i);
                    result.add(new InlayInfo(name, value_stat.getNode().getStartOffset()));
                }

            } while (false);
        } else if (psiElement instanceof ALittleAutoType) {
            do {
                ALittleAutoType auto_type = (ALittleAutoType)psiElement;
                PsiElement guessType = auto_type.guessType();
                if (guessType == null) break;
                if (auto_type.getParent() instanceof ALittleVarAssignPairDec) {
                    ALittleVarAssignPairDec dec = (ALittleVarAssignPairDec) auto_type.getParent();
                    ALittleVarAssignNameDec name_dec = dec.getVarAssignNameDec();
                    if (name_dec == null) break;

                    ALittleUtil.GuessTypeInfo guess_info = ALittleUtil.guessTypeString(guessType, guessType, null, new ArrayList<>(), new ArrayList<>());
                    if (guess_info == null) break;
                    result.add(new InlayInfo(guess_info.value, name_dec.getNode().getStartOffset()));
                } else if (auto_type.getParent() instanceof  ALittleForPairDec) {
                    ALittleForPairDec dec = (ALittleForPairDec) auto_type.getParent();
                    ALittleVarAssignNameDec name_dec = dec.getVarAssignNameDec();
                    if (name_dec == null) break;

                    ALittleUtil.GuessTypeInfo guess_info = ALittleUtil.guessTypeString(guessType, guessType, null, new ArrayList<>(), new ArrayList<>());
                    if (guess_info == null) break;
                    result.add(new InlayInfo(guess_info.value, name_dec.getNode().getStartOffset()));
                }

            } while (false);
        }
        return result;
    }

    @Nullable
    @Override
    public HintInfo getHintInfo(PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public Set<String> getDefaultBlackList() {
        return new HashSet<>();
    }
}
