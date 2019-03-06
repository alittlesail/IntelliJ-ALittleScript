package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueBrackValueStatElement;
import plugin.psi.ALittlePropertyValueMethodCallStatElement;

public abstract class ALittlePropertyValueBrackValueStatElementImpl extends ASTWrapperPsiElement implements ALittlePropertyValueBrackValueStatElement {
    public ALittlePropertyValueBrackValueStatElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
