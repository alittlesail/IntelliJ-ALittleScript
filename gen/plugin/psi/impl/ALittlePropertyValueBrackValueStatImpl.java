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
import com.intellij.psi.PsiReference;

public class ALittlePropertyValueBrackValueStatImpl extends ASTWrapperPsiElement implements ALittlePropertyValueBrackValueStat {

  public ALittlePropertyValueBrackValueStatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitPropertyValueBrackValueStat(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleValueStat getValueStat() {
    return findChildByClass(ALittleValueStat.class);
  }

  @Override
  public PsiElement guessType() {
    return ALittlePsiImplUtil.guessType(this);
  }

  @Override
  @NotNull
  public List<PsiElement> guessTypes() {
    return ALittlePsiImplUtil.guessTypes(this);
  }

  @Override
  public PsiReference getReference() {
    return ALittlePsiImplUtil.getReference(this);
  }

}
