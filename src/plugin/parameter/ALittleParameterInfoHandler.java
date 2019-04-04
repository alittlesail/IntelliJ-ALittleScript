package plugin.parameter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;


public class ALittleParameterInfoHandler implements ParameterInfoHandler<PsiElement, ALittleParameterInfoType> {
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
    public ALittleValueStat findElementForParameterInfo(CreateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittleValueStat args = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittleValueStat.class, false);
        if (args != null) {
            PsiElement parent = args.getParent();
            if (parent instanceof ALittlePropertyValueMethodCallStat) {
                List<ALittleParameterInfoType> list = new ArrayList<>();

                context.setItemsToShow(list.toArray());
                return args;
            }
        }
        return null;
    }

    @Override
    public void showParameterInfo(PsiElement element, CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Override
    public PsiElement findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        PsiFile file = context.getFile();
        ALittleValueStat args = PsiTreeUtil.findElementOfClassAtOffset(file, context.getOffset(), ALittleValueStat.class, false);
        if (args != null) {
            PsiElement parent = args.getParent();
            if (parent instanceof ALittlePropertyValueMethodCallStat) {
                return args;
            }
        }
        return null;
    }

    @Override
    public void updateParameterInfo(PsiElement element, UpdateParameterInfoContext context) {
        int index = ParameterInfoUtils.getCurrentParameterIndex(element.getNode(), context.getOffset(), ALittleTypes.COMMA);
        context.setCurrentParameter(index);
    }

    @Override
    public void updateUI(ALittleParameterInfoType o, ParameterInfoUIContext context) {

    }
}
