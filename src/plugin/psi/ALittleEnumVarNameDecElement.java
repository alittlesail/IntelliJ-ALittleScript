package plugin.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public abstract class ALittleEnumVarNameDecElement extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    public ALittleEnumVarNameDecElement(@NotNull ASTNode node) {
        super(node);
    }
}
