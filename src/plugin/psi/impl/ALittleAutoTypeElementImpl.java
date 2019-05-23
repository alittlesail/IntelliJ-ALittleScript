package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleAutoTypeElement;

public abstract class ALittleAutoTypeElementImpl extends ASTWrapperPsiElement implements ALittleAutoTypeElement {
    public ALittleAutoTypeElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
