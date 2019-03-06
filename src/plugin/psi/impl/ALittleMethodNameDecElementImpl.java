package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleMethodNameDecElement;

public abstract class ALittleMethodNameDecElementImpl extends ASTWrapperPsiElement implements ALittleMethodNameDecElement {
    public ALittleMethodNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
