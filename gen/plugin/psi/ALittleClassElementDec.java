// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleClassElementDec extends PsiElement {

  @Nullable
  ALittleClassCtorDec getClassCtorDec();

  @Nullable
  ALittleClassGetterDec getClassGetterDec();

  @Nullable
  ALittleClassMethodDec getClassMethodDec();

  @Nullable
  ALittleClassSetterDec getClassSetterDec();

  @Nullable
  ALittleClassStaticDec getClassStaticDec();

  @Nullable
  ALittleClassVarDec getClassVarDec();

  @NotNull
  List<ALittleModifier> getModifierList();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
