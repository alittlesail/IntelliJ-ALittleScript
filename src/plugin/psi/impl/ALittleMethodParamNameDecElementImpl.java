package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleMethodParamNameDecElement;

public abstract class ALittleMethodParamNameDecElementImpl extends ASTWrapperPsiElement implements ALittleMethodParamNameDecElement {
    public ALittleMethodParamNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
