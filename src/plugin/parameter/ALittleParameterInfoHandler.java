package plugin.parameter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.guess.*;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallReference;

import java.util.ArrayList;
import java.util.List;

public class ALittleParameterInfoHandler implements ParameterInfoHandler<PsiElement, String> {
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
    public PsiElement findElementForParameterInfo(CreateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittlePropertyValueMethodCall callStat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCall.class, false);
        if (callStat != null) {
            PsiReference ref = callStat.getReference();
            if (!(ref instanceof ALittlePropertyValueMethodCallReference)) return null;
            ALittlePropertyValueMethodCallReference reference = (ALittlePropertyValueMethodCallReference)ref;

            try {
                ALittleGuess preType = reference.guessPreType();
                if (!(preType instanceof ALittleGuessFunctor)) {
                    return null;
                }
                ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;
                if (preTypeFunctor.functorParamNameList.isEmpty()) {
                    return null;
                }
                List<String> list = new ArrayList<>();
                list.add(String.join(", ", preTypeFunctor.functorParamNameList));
                context.setItemsToShow(list.toArray());
            } catch (ALittleGuessException e) {
                return null;
            }

            return callStat;
        }

        ALittleOpNewStat newStat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittleOpNewStat.class, false);
        if (newStat != null) {
            ALittleCustomType customType = newStat.getCustomType();
            if (customType == null) return null;

            try {
                ALittleGuess guess = customType.guessType();

                if (guess instanceof ALittleGuessClassTemplate) {
                    ALittleGuessClassTemplate guessClassTemplate = (ALittleGuessClassTemplate)guess;
                    if (guessClassTemplate.templateExtends != null) {
                        guess = guessClassTemplate.templateExtends;
                    }
                }

                if (guess instanceof ALittleGuessClass) {
                    ALittleClassDec classDec = ((ALittleGuessClass) guess).element;
                    List<ALittleClassCtorDec> ctorDecList = classDec.getClassCtorDecList();
                    if (ctorDecList.size() < 1) {
                        return null;
                    }

                    ALittleMethodParamDec param_dec = ctorDecList.get(0).getMethodParamDec();
                    if (param_dec == null) {
                        return null;
                    }

                    List<ALittleMethodParamOneDec> param_oneDecList = param_dec.getMethodParamOneDecList();
                    if (param_oneDecList.isEmpty()) {
                        return null;
                    }
                    List<String> param_name_list = new ArrayList<>();
                    for (ALittleMethodParamOneDec param_one_dec : param_oneDecList) {
                        if (param_one_dec.getMethodParamNameDec() == null) {
                            return null;
                        }
                        param_name_list.add(param_one_dec.getMethodParamNameDec().getText());
                    }

                    List<String> list = new ArrayList<>();
                    list.add(String.join(", ", param_name_list));
                    context.setItemsToShow(list.toArray());
                }
            } catch (ALittleGuessException ignored) {
                return null;
            }

            return newStat;
        }

        return null;
    }

    @Override
    public void showParameterInfo(@NotNull PsiElement element, CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Override
    public PsiElement findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittlePropertyValueMethodCall callStat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCall.class, false);
        if (callStat != null) return callStat;
        ALittleOpNewStat newStat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittleOpNewStat.class, false);
        if (newStat != null) return newStat;
        return null;
    }

    @Override
    public void updateParameterInfo(PsiElement element, UpdateParameterInfoContext context) {
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
        String[] stringList = o.split(", ");

        int startIndex = 0;
        int endIndex = 0;
        if (stringList.length > index) {
            for (int i = 0; i <= index; ++i) {
                if (i != index) {
                    startIndex += stringList[i].length() + 2;
                } else {
                    endIndex = startIndex + stringList[i].length();
                }
            }
        }
        context.setupUIComponentPresentation(o, startIndex, endIndex, !context.isUIComponentEnabled(), false, false,
                context.getDefaultParameterColor());
    }
}
