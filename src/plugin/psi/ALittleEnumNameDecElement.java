package plugin.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public abstract class ALittleEnumNameDecElement extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    public ALittleEnumNameDecElement(@NotNull ASTNode node) {
        super(node);
    }
}
