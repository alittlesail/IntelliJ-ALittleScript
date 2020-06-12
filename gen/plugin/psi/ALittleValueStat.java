// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleValueStat extends PsiElement {

  @Nullable
  ALittleBindStat getBindStat();

  @Nullable
  ALittleOp2Stat getOp2Stat();

  @Nullable
  ALittleOpNewListStat getOpNewListStat();

  @Nullable
  ALittleOpNewStat getOpNewStat();

  @Nullable
  ALittleTcallStat getTcallStat();

  @Nullable
  ALittleValueOpStat getValueOpStat();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
