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

public class ALittleOp4SuffixImpl extends ASTWrapperPsiElement implements ALittleOp4Suffix {

  public ALittleOp4SuffixImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp4Suffix(this);
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
  public ALittleOp4 getOp4() {
    return findNotNullChildByClass(ALittleOp4.class);
  }

  @Override
  @NotNull
  public List<ALittleOp4SuffixEe> getOp4SuffixEeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleOp4SuffixEe.class);
  }

  @Override
  @Nullable
  public ALittleValueFactor getValueFactor() {
    return findChildByClass(ALittleValueFactor.class);
  }

}
