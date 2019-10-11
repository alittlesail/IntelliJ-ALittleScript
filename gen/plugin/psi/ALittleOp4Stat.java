// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleOp4Stat extends PsiElement {

  @NotNull
  ALittleOp4Suffix getOp4Suffix();

  @NotNull
  List<ALittleOp4SuffixEx> getOp4SuffixExList();

  @NotNull
  ALittleValueFactorStat getValueFactorStat();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
