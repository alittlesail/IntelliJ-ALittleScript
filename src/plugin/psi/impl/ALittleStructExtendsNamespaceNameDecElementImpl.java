package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleStructExtendsNamespaceNameDecElement;

public abstract class ALittleStructExtendsNamespaceNameDecElementImpl extends ASTWrapperPsiElement implements ALittleStructExtendsNamespaceNameDecElement {
    public ALittleStructExtendsNamespaceNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
