package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassExtendsNameDecElement;
import plugin.psi.ALittleClassExtendsNamespaceNameDecElement;

public abstract class ALittleClassExtendsNamespaceNameDecElementImpl extends ASTWrapperPsiElement implements ALittleClassExtendsNamespaceNameDecElement {
    public ALittleClassExtendsNamespaceNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
