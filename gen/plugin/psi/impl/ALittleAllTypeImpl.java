// This is a generated file. Not intended for manual editing.
package plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static plugin.psi.ALittleTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import plugin.psi.*;

public class ALittleAllTypeImpl extends ASTWrapperPsiElement implements ALittleAllType {

  public ALittleAllTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitAllType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleCustomType getCustomType() {
    return findChildByClass(ALittleCustomType.class);
  }

  @Override
  @Nullable
  public ALittleGenericType getGenericType() {
    return findChildByClass(ALittleGenericType.class);
  }

  @Override
  @Nullable
  public ALittlePrimitiveType getPrimitiveType() {
    return findChildByClass(ALittlePrimitiveType.class);
  }

}
