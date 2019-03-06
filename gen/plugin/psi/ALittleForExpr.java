// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleForExpr extends PsiElement {

  @NotNull
  List<ALittleAllExpr> getAllExprList();

  @Nullable
  ALittleForInCondition getForInCondition();

  @Nullable
  ALittleForStepCondition getForStepCondition();

}
