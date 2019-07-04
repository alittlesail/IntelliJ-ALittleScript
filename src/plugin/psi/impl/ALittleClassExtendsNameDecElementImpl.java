package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassExtendsNameDecElement;

public abstract class ALittleClassExtendsNameDecElementImpl extends ASTWrapperPsiElement implements ALittleClassExtendsNameDecElement {
    public ALittleClassExtendsNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
