package plugin.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.reference.*;

import java.util.ArrayList;
import java.util.List;


public class ALittlePsiImplUtil {
    public static PsiReference getReference(PsiElement element) {
        return ALittleReferenceFactory.create(element);
    }

    public static PsiElement guessType(PsiElement element) {
        ALittleReference ref = ALittleReferenceFactory.create(element);
        if (ref == null) return null;
        return ref.guessType();
    }

    @NotNull
    public static List<PsiElement> guessTypes(PsiElement element) {
        ALittleReference ref = ALittleReferenceFactory.create(element);
        if (ref == null) return new ArrayList<>();
        return ref.guessTypes();
    }
}
