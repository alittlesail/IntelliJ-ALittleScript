// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleOp8SuffixEe extends PsiElement {

  @Nullable
  ALittleOp3Suffix getOp3Suffix();

  @Nullable
  ALittleOp4Suffix getOp4Suffix();

  @Nullable
  ALittleOp5Suffix getOp5Suffix();

  @Nullable
  ALittleOp6Suffix getOp6Suffix();

  @Nullable
  ALittleOp7Suffix getOp7Suffix();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
