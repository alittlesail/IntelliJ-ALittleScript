package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleInstanceClassNameDecElement;
import plugin.psi.ALittleInstanceNameDecElement;

public abstract class ALittleInstanceNameDecElementImpl extends ASTWrapperPsiElement implements ALittleInstanceNameDecElement {
    public ALittleInstanceNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
