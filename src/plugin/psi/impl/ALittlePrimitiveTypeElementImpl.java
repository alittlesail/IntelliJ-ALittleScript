package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePrimitiveTypeElement;
import plugin.psi.ALittleVarAssignNameDecElement;

public abstract class ALittlePrimitiveTypeElementImpl extends ASTWrapperPsiElement implements ALittlePrimitiveTypeElement {
    public ALittlePrimitiveTypeElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
