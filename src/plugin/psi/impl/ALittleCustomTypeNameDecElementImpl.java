package plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleClassExtendsNameDecElement;
import plugin.psi.ALittleCustomTypeNameDecElement;

public abstract class ALittleCustomTypeNameDecElementImpl extends ASTWrapperPsiElement implements ALittleCustomTypeNameDecElement {
    public ALittleCustomTypeNameDecElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
