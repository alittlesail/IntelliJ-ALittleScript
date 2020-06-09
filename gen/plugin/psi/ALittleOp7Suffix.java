// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleOp7Suffix extends PsiElement {

  @Nullable
  ALittleOp2Value getOp2Value();

  @NotNull
  ALittleOp7 getOp7();

  @NotNull
  List<ALittleOp7SuffixEe> getOp7SuffixEeList();

  @Nullable
  ALittleValueFactorStat getValueFactorStat();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
