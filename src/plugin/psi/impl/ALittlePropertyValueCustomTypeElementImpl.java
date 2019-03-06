package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueCustomTypeElement;

public abstract class ALittlePropertyValueCustomTypeElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueCustomTypeElement {
    public ALittlePropertyValueCustomTypeElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
