// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleOp5SuffixEx extends PsiElement {

  @Nullable
  ALittleOp5Suffix getOp5Suffix();

  @Nullable
  ALittleOp6Suffix getOp6Suffix();

  @Nullable
  ALittleOp7Suffix getOp7Suffix();

  @Nullable
  ALittleOp8Suffix getOp8Suffix();

  @NotNull
  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
