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
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public class ALittleVarAssignExprImpl extends ASTWrapperPsiElement implements ALittleVarAssignExpr {

  public ALittleVarAssignExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitVarAssignExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleValueStat getValueStat() {
    return findChildByClass(ALittleValueStat.class);
  }

  @Override
  @NotNull
  public List<ALittleVarAssignDec> getVarAssignDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleVarAssignDec.class);
  }

  @Override
  @NotNull
  public GuessTypeInfo guessType() throws ALittleReferenceException {
    return ALittlePsiImplUtil.guessType(this);
  }

  @Override
  @NotNull
  public List<GuessTypeInfo> guessTypes() throws ALittleReferenceException {
    return ALittlePsiImplUtil.guessTypes(this);
  }

  @Override
  public PsiReference getReference() {
    return ALittlePsiImplUtil.getReference(this);
  }

}
