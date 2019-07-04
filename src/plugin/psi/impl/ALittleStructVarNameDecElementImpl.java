package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleStructVarNameDecElement;

public abstract class ALittleStructVarNameDecElementImpl extends ASTWrapperPsiElement implements ALittleStructVarNameDecElement {
    public ALittleStructVarNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
