// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleEnumDec extends PsiElement {

  @Nullable
  ALittleAccessModifier getAccessModifier();

  @Nullable
  ALittleEnumNameDec getEnumNameDec();

  @Nullable
  ALittleEnumProtocolDec getEnumProtocolDec();

  @NotNull
  List<ALittleEnumVarDec> getEnumVarDecList();

}
