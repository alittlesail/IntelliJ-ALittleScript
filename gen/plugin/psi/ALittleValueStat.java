// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public interface ALittleValueStat extends PsiElement {

  @Nullable
  ALittleBindStat getBindStat();

  @Nullable
  ALittleOp2Stat getOp2Stat();

  @Nullable
  ALittleOp3Stat getOp3Stat();

  @Nullable
  ALittleOp4Stat getOp4Stat();

  @Nullable
  ALittleOp5Stat getOp5Stat();

  @Nullable
  ALittleOp6Stat getOp6Stat();

  @Nullable
  ALittleOp7Stat getOp7Stat();

  @Nullable
  ALittleOp8Stat getOp8Stat();

  @Nullable
  ALittleOpNewList getOpNewList();

  @Nullable
  ALittleOpNewStat getOpNewStat();

  @Nullable
  ALittleValueFactor getValueFactor();

  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
