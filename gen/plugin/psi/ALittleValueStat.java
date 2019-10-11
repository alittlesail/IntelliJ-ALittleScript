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
  ALittleMethodParamTailDec getMethodParamTailDec();

  @Nullable
  ALittleNcallStat getNcallStat();

  @Nullable
  ALittleOp2Stat getOp2Stat();

  @Nullable
  ALittleOp3Stat getOp3Stat();

  @Nullable
  ALittleOp4Stat getOp4Stat();

  @Nullable
  ALittleOp5Stat getOp5Stat();

  @Nullable
  ALittleOp6Stat getOp6Stat();

  @Nullable
  ALittleOp7Stat getOp7Stat();

  @Nullable
  ALittleOp8Stat getOp8Stat();

  @Nullable
  ALittleOpNewListStat getOpNewListStat();

  @Nullable
  ALittleOpNewStat getOpNewStat();

  @Nullable
  ALittleTcallStat getTcallStat();

  @Nullable
  ALittleValueFactorStat getValueFactorStat();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
