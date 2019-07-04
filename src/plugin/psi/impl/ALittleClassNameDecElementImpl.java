package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassNameDecElement;

public abstract class ALittleClassNameDecElementImpl extends ASTWrapperPsiElement implements ALittleClassNameDecElement {
    public ALittleClassNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
