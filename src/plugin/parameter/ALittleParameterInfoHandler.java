package plugin.parameter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.guess.ALittleGuessFunctor;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallReference;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.List;


public class ALittleParameterInfoHandler implements ParameterInfoHandler<ALittlePropertyValueMethodCall, String> {
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
    public ALittlePropertyValueMethodCall findElementForParameterInfo(CreateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittlePropertyValueMethodCall stat = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCall.class, false);
        if (stat == null) return null;

        PsiReference ref = stat.getReference();
        if (!(ref instanceof ALittlePropertyValueMethodCallReference)) return null;
        ALittlePropertyValueMethodCallReference reference = (ALittlePropertyValueMethodCallReference)ref;

        try {
            ALittleGuess preType = reference.guessPreType();
            if (!(preType instanceof ALittleGuessFunctor)) {
                return null;
            }
            ALittleGuessFunctor preTypeFunctor = (ALittleGuessFunctor)preType;
            List<String> list = new ArrayList<>();
            list.add(String.join(", ", preTypeFunctor.functorParamNameList));
            context.setItemsToShow(list.toArray());
        } catch (ALittleGuessException e) {
            return null;
        }

        return stat;
    }

    @Override
    public void showParameterInfo(@NotNull ALittlePropertyValueMethodCall element, CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Override
    public ALittlePropertyValueMethodCall findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        PsiFile file = context.getFile();
        return PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittlePropertyValueMethodCall.class, false);
    }

    @Override
    public void updateParameterInfo(ALittlePropertyValueMethodCall element, UpdateParameterInfoContext context) {
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
