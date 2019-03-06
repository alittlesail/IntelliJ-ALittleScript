// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleNamespaceDec extends PsiElement {

  @NotNull
  List<ALittleClassDec> getClassDecList();

  @NotNull
  List<ALittleEnumDec> getEnumDecList();

  @NotNull
  List<ALittleGlobalMethodDec> getGlobalMethodDecList();

  @NotNull
  List<ALittleInstanceDec> getInstanceDecList();

  @Nullable
  ALittleNamespaceNameDec getNamespaceNameDec();

  @NotNull
  List<ALittleStructDec> getStructDecList();

}
