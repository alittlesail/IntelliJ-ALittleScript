// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleOp6SuffixEe extends PsiElement {

  @Nullable
  ALittleOp3Suffix getOp3Suffix();

  @Nullable
  ALittleOp4Suffix getOp4Suffix();

  @Nullable
  ALittleOp5Suffix getOp5Suffix();

  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
