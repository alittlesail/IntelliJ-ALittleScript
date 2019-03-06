package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleEnumVarNameDecElement;
import plugin.psi.ALittleStructVarNameDecElement;

public abstract class ALittleEnumVarNameDecElementImpl extends ASTWrapperPsiElement implements ALittleEnumVarNameDecElement {
    public ALittleEnumVarNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
