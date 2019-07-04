package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleCustomTypeNamespaceNameDecElement;

public abstract class ALittleCustomTypeNamespaceNameDecElementImpl extends ASTWrapperPsiElement implements ALittleCustomTypeNamespaceNameDecElement {
    public ALittleCustomTypeNamespaceNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
