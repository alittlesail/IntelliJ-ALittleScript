package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassMethodParamNameDecElement;
import plugin.psi.ALittleVarAssignNameDecElement;

public abstract class ALittleVarAssignNameDecElementImpl extends ASTWrapperPsiElement implements ALittleVarAssignNameDecElement {
    public ALittleVarAssignNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
