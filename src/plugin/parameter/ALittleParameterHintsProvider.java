package plugin.parameter;

import com.intellij.codeInsight.hints.HintInfo;
import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.reference.ALittleReference;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALittleParameterHintsProvider implements InlayParameterHintsProvider {
    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement psiElement) {
        PsiReference ref = psiElement.getReference();
        try {
            if (ref instanceof ALittleReference) {
                return ((ALittleReference) ref).getParameterHints();
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {
        }
        return new ArrayList<>();
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
