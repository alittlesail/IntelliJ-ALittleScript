// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ALittleClassDec extends PsiElement {

  @Nullable
  ALittleClassAccessModifier getClassAccessModifier();

  @NotNull
  List<ALittleClassCtorDec> getClassCtorDecList();

  @Nullable
  ALittleClassExtendsAccessModifier getClassExtendsAccessModifier();

  @Nullable
  ALittleClassExtendsNameDec getClassExtendsNameDec();

  @Nullable
  ALittleClassExtendsNamespaceNameDec getClassExtendsNamespaceNameDec();

  @NotNull
  List<ALittleClassGetterDec> getClassGetterDecList();

  @NotNull
  List<ALittleClassMethodDec> getClassMethodDecList();

  @Nullable
  ALittleClassNameDec getClassNameDec();

  @NotNull
  List<ALittleClassSetterDec> getClassSetterDecList();

  @NotNull
  List<ALittleClassStaticDec> getClassStaticDecList();

  @NotNull
  List<ALittleClassVarDec> getClassVarDecList();

}
