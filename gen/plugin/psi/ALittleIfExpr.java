// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleIfExpr extends PsiElement {

  @NotNull
  List<ALittleAllExpr> getAllExprList();

  @Nullable
  ALittleElseExpr getElseExpr();

  @NotNull
  List<ALittleElseIfExpr> getElseIfExprList();

  @Nullable
  ALittleValueStat getValueStat();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
