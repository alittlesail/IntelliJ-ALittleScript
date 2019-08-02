package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleBindStatElement;

public abstract class ALittleBindStatElementImpl extends ASTWrapperPsiElement implements ALittleBindStatElement {
    public ALittleBindStatElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
