// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleNamespaceElementDec extends PsiElement {

  @Nullable
  ALittleClassDec getClassDec();

  @Nullable
  ALittleEnumDec getEnumDec();

  @Nullable
  ALittleGlobalMethodDec getGlobalMethodDec();

  @Nullable
  ALittleInstanceDec getInstanceDec();

  @NotNull
  List<ALittleModifier> getModifierList();

  @Nullable
  ALittleOpAssignExpr getOpAssignExpr();

  @Nullable
  ALittleStructDec getStructDec();

  @Nullable
  ALittleUsingDec getUsingDec();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
