// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleAllExpr extends PsiElement {

  @Nullable
  ALittleDoWhileExpr getDoWhileExpr();

  @Nullable
  ALittleEmptyExpr getEmptyExpr();

  @Nullable
  ALittleFlowExpr getFlowExpr();

  @Nullable
  ALittleForExpr getForExpr();

  @Nullable
  ALittleIfExpr getIfExpr();

  @Nullable
  ALittleOp1Expr getOp1Expr();

  @Nullable
  ALittleOpAssignExpr getOpAssignExpr();

  @Nullable
  ALittlePropertyValueExpr getPropertyValueExpr();

  @Nullable
  ALittleReturnExpr getReturnExpr();

  @Nullable
  ALittleVarAssignExpr getVarAssignExpr();

  @Nullable
  ALittleWhileExpr getWhileExpr();

  @Nullable
  ALittleWrapExpr getWrapExpr();

  @NotNull
  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
