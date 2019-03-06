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

public class ALittleValueFactorImpl extends ASTWrapperPsiElement implements ALittleValueFactor {

  public ALittleValueFactorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitValueFactor(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleConstValue getConstValue() {
    return findChildByClass(ALittleConstValue.class);
  }

  @Override
  @Nullable
  public ALittlePropertyValue getPropertyValue() {
    return findChildByClass(ALittlePropertyValue.class);
  }

  @Override
  @Nullable
  public ALittleValueStatParen getValueStatParen() {
    return findChildByClass(ALittleValueStatParen.class);
  }

}
