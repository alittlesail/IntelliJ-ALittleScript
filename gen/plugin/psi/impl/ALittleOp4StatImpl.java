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

public class ALittleOp4StatImpl extends ASTWrapperPsiElement implements ALittleOp4Stat {

  public ALittleOp4StatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp4Stat(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ALittleOp4Suffix getOp4Suffix() {
    return findNotNullChildByClass(ALittleOp4Suffix.class);
  }

  @Override
  @NotNull
  public List<ALittleOp4SuffixEx> getOp4SuffixExList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleOp4SuffixEx.class);
  }

  @Override
  @NotNull
  public ALittleValueFactor getValueFactor() {
    return findNotNullChildByClass(ALittleValueFactor.class);
  }

}
