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

public class ALittleOp3SuffixImpl extends ASTWrapperPsiElement implements ALittleOp3Suffix {

  public ALittleOp3SuffixImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp3Suffix(this);
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
  public ALittleOp3 getOp3() {
    return findNotNullChildByClass(ALittleOp3.class);
  }

  @Override
  @Nullable
  public ALittleValueFactor getValueFactor() {
    return findChildByClass(ALittleValueFactor.class);
  }

}
