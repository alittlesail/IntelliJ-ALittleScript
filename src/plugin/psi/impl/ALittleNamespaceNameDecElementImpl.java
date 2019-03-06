package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleNamespaceNameDecElement;

public abstract class ALittleNamespaceNameDecElementImpl extends ASTWrapperPsiElement implements ALittleNamespaceNameDecElement {
    public ALittleNamespaceNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
