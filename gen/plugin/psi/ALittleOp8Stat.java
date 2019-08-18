// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleOp8Stat extends PsiElement {

  @NotNull
  ALittleOp8Suffix getOp8Suffix();

  @NotNull
  List<ALittleOp8SuffixEx> getOp8SuffixExList();

  @NotNull
  ALittleValueFactor getValueFactor();

  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
