package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueDotIdNameElement;
import plugin.psi.ALittlePropertyValueMethodCallStatElement;

public abstract class ALittlePropertyValueMethodCallStatElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueMethodCallStatElement {
    public ALittlePropertyValueMethodCallStatElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
