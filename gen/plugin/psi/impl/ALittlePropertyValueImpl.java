// This is a generated file. Not intended for manual editing.
package plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static plugin.psi.ALittleTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import plugin.psi.*;

public class ALittlePropertyValueImpl extends ASTWrapperPsiElement implements ALittlePropertyValue {

  public ALittlePropertyValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitPropertyValue(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittlePropertyValueCastType getPropertyValueCastType() {
    return findChildByClass(ALittlePropertyValueCastType.class);
  }

  @Override
  @Nullable
  public ALittlePropertyValueCustomType getPropertyValueCustomType() {
    return findChildByClass(ALittlePropertyValueCustomType.class);
  }

  @Override
  @NotNull
  public List<ALittlePropertyValueSuffix> getPropertyValueSuffixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittlePropertyValueSuffix.class);
  }

  @Override
  @Nullable
  public ALittlePropertyValueThisType getPropertyValueThisType() {
    return findChildByClass(ALittlePropertyValueThisType.class);
  }

}
