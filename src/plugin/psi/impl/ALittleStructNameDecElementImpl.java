package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleStructNameDecElement;

public abstract class ALittleStructNameDecElementImpl extends ASTWrapperPsiElement implements ALittleStructNameDecElement {
    public ALittleStructNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
