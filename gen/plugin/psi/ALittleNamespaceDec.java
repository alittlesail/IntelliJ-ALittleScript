// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

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
  List<ALittleOpAssignExpr> getOpAssignExprList();

  @NotNull
  List<ALittlePropertyValueExpr> getPropertyValueExprList();

  @Nullable
  ALittleRegisterModifier getRegisterModifier();

  @NotNull
  List<ALittleStructDec> getStructDecList();

  @NotNull
  GuessTypeInfo guessType() throws ALittleReferenceException;

  @NotNull
  List<GuessTypeInfo> guessTypes() throws ALittleReferenceException;

  PsiReference getReference();

}
