package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassVarNameDecElement;

public abstract class ALittleClassVarNameDecElementImpl extends ASTWrapperPsiElement implements ALittleClassVarNameDecElement {
    public ALittleClassVarNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
