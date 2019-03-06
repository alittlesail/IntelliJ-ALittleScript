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

public class ALittleForInConditionImpl extends ASTWrapperPsiElement implements ALittleForInCondition {

  public ALittleForInConditionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitForInCondition(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ALittleForPairDec> getForPairDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleForPairDec.class);
  }

  @Override
  @Nullable
  public ALittleValueStat getValueStat() {
    return findChildByClass(ALittleValueStat.class);
  }

}
