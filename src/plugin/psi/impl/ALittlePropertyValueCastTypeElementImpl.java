package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueCastTypeElement;

public abstract class ALittlePropertyValueCastTypeElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueCastTypeElement {
    public ALittlePropertyValueCastTypeElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
