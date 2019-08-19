// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleStructDec extends PsiElement {

  @Nullable
  ALittleAccessModifier getAccessModifier();

  @Nullable
  ALittleStructExtendsDec getStructExtendsDec();

  @Nullable
  ALittleStructNameDec getStructNameDec();

  @NotNull
  List<ALittleStructVarDec> getStructVarDecList();

  @NotNull
  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

  void checkError() throws ALittleReferenceException;

}
