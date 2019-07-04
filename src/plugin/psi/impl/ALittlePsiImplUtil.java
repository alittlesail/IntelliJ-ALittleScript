package plugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.reference.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePsiImplUtil {
    public static PsiElement getNameIdentifier(PsiElement element) {
        ASTNode keyNode = element.getNode().findChildByType(ALittleTypes.ID_CONTENT);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    public static String getName(PsiElement element) {
        PsiElement id_psi = getNameIdentifier(element);
        if (id_psi == null) return null;
        return id_psi.getText();
    }

    public static PsiElement setName(PsiElement element, String new_name) {
        ASTNode node = element.getNode().findChildByType(ALittleTypes.ID_CONTENT);
        if (node != null) {
            PsiElement dec = ALittleElementFactory.create(element, new_name);
            ASTNode new_node = dec.getNode().findChildByType(ALittleTypes.ID_CONTENT);
            if (new_node != null) element.getNode().replaceChild(node, new_node);
        }
        return element;
    }

    public static PsiReference getReference(PsiElement element) {
        return ALittleReferenceFactory.create(element);
    }

    public static PsiElement guessType(PsiElement element) {
        ALittleReference ref = ALittleReferenceFactory.create(element);
        if (ref == null) return null;
        return ref.guessType();
    }

    @NotNull
    public static List<PsiElement> guessTypes(PsiElement element) {
        ALittleReference ref = ALittleReferenceFactory.create(element);
        if (ref == null) return new ArrayList<>();
        return ref.guessTypes();
    }
}
