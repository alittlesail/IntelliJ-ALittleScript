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

public class ALittleVarAssignPairDecImpl extends ASTWrapperPsiElement implements ALittleVarAssignPairDec {

  public ALittleVarAssignPairDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitVarAssignPairDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleAllType getAllType() {
    return findChildByClass(ALittleAllType.class);
  }

  @Override
  @Nullable
  public ALittleAutoType getAutoType() {
    return findChildByClass(ALittleAutoType.class);
  }

  @Override
  @NotNull
  public ALittleVarAssignNameDec getVarAssignNameDec() {
    return findNotNullChildByClass(ALittleVarAssignNameDec.class);
  }

}
