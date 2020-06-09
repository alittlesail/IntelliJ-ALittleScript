// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleAllExpr extends PsiElement {

  @Nullable
  ALittleAssertExpr getAssertExpr();

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

  @NotNull
  List<ALittleModifier> getModifierList();

  @Nullable
  ALittleOp1Expr getOp1Expr();

  @Nullable
  ALittleOpAssignExpr getOpAssignExpr();

  @Nullable
  ALittleReturnExpr getReturnExpr();

  @Nullable
  ALittleThrowExpr getThrowExpr();

  @Nullable
  ALittleVarAssignExpr getVarAssignExpr();

  @Nullable
  ALittleWhileExpr getWhileExpr();

  @Nullable
  ALittleWrapExpr getWrapExpr();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
