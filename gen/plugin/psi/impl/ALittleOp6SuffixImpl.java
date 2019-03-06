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

public class ALittleOp6SuffixImpl extends ASTWrapperPsiElement implements ALittleOp6Suffix {

  public ALittleOp6SuffixImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp6Suffix(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleOp2Value getOp2Value() {
    return findChildByClass(ALittleOp2Value.class);
  }

  @Override
  @NotNull
  public ALittleOp6 getOp6() {
    return findNotNullChildByClass(ALittleOp6.class);
  }

  @Override
  @NotNull
  public List<ALittleOp6SuffixEe> getOp6SuffixEeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleOp6SuffixEe.class);
  }

  @Override
  @Nullable
  public ALittleValueFactor getValueFactor() {
    return findChildByClass(ALittleValueFactor.class);
  }

}
