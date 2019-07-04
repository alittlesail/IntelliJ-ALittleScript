// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ALittlePropertyValueCustomType extends ALittlePropertyValueCustomTypeElement {

  @NotNull
  PsiElement getIdContent();

  PsiElement guessType();

  @NotNull
  List<PsiElement> guessTypes();

  PsiReference getReference();

  String getName();

  PsiElement setName(String new_name);

  PsiElement getNameIdentifier();

}
