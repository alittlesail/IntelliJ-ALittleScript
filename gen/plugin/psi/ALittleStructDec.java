// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleStructDec extends PsiElement {

  @Nullable
  ALittleAccessModifier getAccessModifier();

  @Nullable
  ALittleStructExtendsNameDec getStructExtendsNameDec();

  @Nullable
  ALittleStructExtendsNamespaceNameDec getStructExtendsNamespaceNameDec();

  @NotNull
  ALittleStructNameDec getStructNameDec();

  @NotNull
  List<ALittleStructVarDec> getStructVarDecList();

}
