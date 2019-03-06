// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleIfExpr extends PsiElement {

  @NotNull
  List<ALittleAllExpr> getAllExprList();

  @Nullable
  ALittleElseExpr getElseExpr();

  @NotNull
  List<ALittleElseIfExpr> getElseIfExprList();

  @Nullable
  ALittleValueStat getValueStat();

}
