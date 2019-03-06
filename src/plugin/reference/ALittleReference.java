package plugin.reference;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ALittleReference extends PsiPolyVariantReference {
    PsiElement guessType();

    @NotNull
    List<PsiElement> guessTypes();
}
