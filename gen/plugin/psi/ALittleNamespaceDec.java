// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public interface ALittleNamespaceDec extends PsiElement {

  @NotNull
  List<ALittleClassDec> getClassDecList();

  @NotNull
  List<ALittleEnumDec> getEnumDecList();

  @NotNull
  List<ALittleGlobalMethodDec> getGlobalMethodDecList();

  @NotNull
  List<ALittleInstanceDec> getInstanceDecList();

  @Nullable
  ALittleNamespaceNameDec getNamespaceNameDec();

  @NotNull
  List<ALittleOpAssignExpr> getOpAssignExprList();

  @NotNull
  List<ALittlePropertyValueExpr> getPropertyValueExprList();

  @Nullable
  ALittleRegisterModifier getRegisterModifier();

  @NotNull
  List<ALittleStructDec> getStructDecList();

  @NotNull
  List<ALittleUsingDec> getUsingDecList();

  @NotNull
  ALittleGuess guessType() throws ALittleGuessException;

  @NotNull
  List<ALittleGuess> guessTypes() throws ALittleGuessException;

  PsiReference getReference();

}
