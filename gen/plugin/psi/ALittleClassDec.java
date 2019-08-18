// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

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

  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
