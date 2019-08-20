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

public class ALittleAllExprImpl extends ASTWrapperPsiElement implements ALittleAllExpr {

  public ALittleAllExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitAllExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleDoWhileExpr getDoWhileExpr() {
    return findChildByClass(ALittleDoWhileExpr.class);
  }

  @Override
  @Nullable
  public ALittleEmptyExpr getEmptyExpr() {
    return findChildByClass(ALittleEmptyExpr.class);
  }

  @Override
  @Nullable
  public ALittleFlowExpr getFlowExpr() {
    return findChildByClass(ALittleFlowExpr.class);
  }

  @Override
  @Nullable
  public ALittleForExpr getForExpr() {
    return findChildByClass(ALittleForExpr.class);
  }

  @Override
  @Nullable
  public ALittleIfExpr getIfExpr() {
    return findChildByClass(ALittleIfExpr.class);
  }

  @Override
  @Nullable
  public ALittleOp1Expr getOp1Expr() {
    return findChildByClass(ALittleOp1Expr.class);
  }

  @Override
  @Nullable
  public ALittleOpAssignExpr getOpAssignExpr() {
    return findChildByClass(ALittleOpAssignExpr.class);
  }

  @Override
  @Nullable
  public ALittlePropertyValueExpr getPropertyValueExpr() {
    return findChildByClass(ALittlePropertyValueExpr.class);
  }

  @Override
  @Nullable
  public ALittleReturnExpr getReturnExpr() {
    return findChildByClass(ALittleReturnExpr.class);
  }

  @Override
  @Nullable
  public ALittleVarAssignExpr getVarAssignExpr() {
    return findChildByClass(ALittleVarAssignExpr.class);
  }

  @Override
  @Nullable
  public ALittleWhileExpr getWhileExpr() {
    return findChildByClass(ALittleWhileExpr.class);
  }

  @Override
  @Nullable
  public ALittleWrapExpr getWrapExpr() {
    return findChildByClass(ALittleWrapExpr.class);
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
