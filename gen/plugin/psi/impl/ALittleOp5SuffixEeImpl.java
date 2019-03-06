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

public class ALittleOp5SuffixEeImpl extends ASTWrapperPsiElement implements ALittleOp5SuffixEe {

  public ALittleOp5SuffixEeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp5SuffixEe(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleOp3Suffix getOp3Suffix() {
    return findChildByClass(ALittleOp3Suffix.class);
  }

  @Override
  @Nullable
  public ALittleOp4Suffix getOp4Suffix() {
    return findChildByClass(ALittleOp4Suffix.class);
  }

}
