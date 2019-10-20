// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleGlobalMethodDec extends PsiElement {

  @Nullable
  ALittleAccessModifier getAccessModifier();

  @Nullable
  ALittleCmdModifier getCmdModifier();

  @Nullable
  ALittleCoModifier getCoModifier();

  @Nullable
  ALittleMethodBodyDec getMethodBodyDec();

  @Nullable
  ALittleMethodNameDec getMethodNameDec();

  @Nullable
  ALittleMethodParamDec getMethodParamDec();

  @Nullable
  ALittleMethodReturnDec getMethodReturnDec();

  @Nullable
  ALittleProtoModifier getProtoModifier();

  @Nullable
  ALittleTemplateDec getTemplateDec();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
