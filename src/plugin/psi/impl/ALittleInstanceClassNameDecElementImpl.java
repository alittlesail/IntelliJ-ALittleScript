package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleInstanceClassNameDecElement;

public abstract class ALittleInstanceClassNameDecElementImpl extends ASTWrapperPsiElement implements ALittleInstanceClassNameDecElement {
    public ALittleInstanceClassNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
