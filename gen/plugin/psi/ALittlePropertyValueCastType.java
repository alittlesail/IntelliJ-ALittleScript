// This is a generated file. Not intended for manual editing.
package plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ALittlePropertyValueCastType extends ALittlePropertyValueCastTypeElement {

  @NotNull
  ALittleAllType getAllType();

  @NotNull
  ALittleValueFactor getValueFactor();

  PsiElement guessType();

  @NotNull
  List<PsiElement> guessTypes();

  PsiReference[] getReferences();

  String getName();

  PsiElement setName(String new_name);

  PsiElement getNameIdentifier();

}