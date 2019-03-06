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

public class ALittleValueStatImpl extends ASTWrapperPsiElement implements ALittleValueStat {

  public ALittleValueStatImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitValueStat(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleOp2Stat getOp2Stat() {
    return findChildByClass(ALittleOp2Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp3Stat getOp3Stat() {
    return findChildByClass(ALittleOp3Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp4Stat getOp4Stat() {
    return findChildByClass(ALittleOp4Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp5Stat getOp5Stat() {
    return findChildByClass(ALittleOp5Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp6Stat getOp6Stat() {
    return findChildByClass(ALittleOp6Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp7Stat getOp7Stat() {
    return findChildByClass(ALittleOp7Stat.class);
  }

  @Override
  @Nullable
  public ALittleOp8Stat getOp8Stat() {
    return findChildByClass(ALittleOp8Stat.class);
  }

  @Override
  @Nullable
  public ALittleOpNewStat getOpNewStat() {
    return findChildByClass(ALittleOpNewStat.class);
  }

  @Override
  @Nullable
  public ALittleValueFactor getValueFactor() {
    return findChildByClass(ALittleValueFactor.class);
  }

}
