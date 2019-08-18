// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleOp5Suffix extends PsiElement {

  @Nullable
  ALittleOp2Value getOp2Value();

  @NotNull
  ALittleOp5 getOp5();

  @NotNull
  List<ALittleOp5SuffixEe> getOp5SuffixEeList();

  @Nullable
  ALittleValueFactor getValueFactor();

  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
