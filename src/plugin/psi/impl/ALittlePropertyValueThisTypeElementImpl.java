package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueThisTypeElement;

public abstract class ALittlePropertyValueThisTypeElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueThisTypeElement {
    public ALittlePropertyValueThisTypeElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
