package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassExtendsNameDecElement;
import plugin.psi.ALittleStructExtendsNameDecElement;

public abstract class ALittleStructExtendsNameDecElementImpl extends ASTWrapperPsiElement implements ALittleStructExtendsNameDecElement {
    public ALittleStructExtendsNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
