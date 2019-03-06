package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleEnumNameDecElement;
import plugin.psi.ALittleStructNameDecElement;

public abstract class ALittleEnumNameDecElementImpl extends ASTWrapperPsiElement implements ALittleEnumNameDecElement {
    public ALittleEnumNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
