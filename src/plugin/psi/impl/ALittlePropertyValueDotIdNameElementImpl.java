package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueDotIdNameElement;

public abstract class ALittlePropertyValueDotIdNameElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueDotIdNameElement {
    public ALittlePropertyValueDotIdNameElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
