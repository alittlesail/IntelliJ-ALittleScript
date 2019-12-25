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
