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
import plugin.reference.ALittleReferenceUtil.GuessTypeInfo;
import plugin.reference.ALittleReferenceUtil.ALittleReferenceException;

public class ALittleOp7SuffixEeImpl extends ASTWrapperPsiElement implements ALittleOp7SuffixEe {

  public ALittleOp7SuffixEeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp7SuffixEe(this);
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

  @Override
  @Nullable
  public ALittleOp5Suffix getOp5Suffix() {
    return findChildByClass(ALittleOp5Suffix.class);
  }

  @Override
  @Nullable
  public ALittleOp6Suffix getOp6Suffix() {
    return findChildByClass(ALittleOp6Suffix.class);
  }

  @Override
  @NotNull
  public GuessTypeInfo guessType() throws ALittleReferenceException {
    return ALittlePsiImplUtil.guessType(this);
  }

  @Override
  @NotNull
  public List<GuessTypeInfo> guessTypes() throws ALittleReferenceException {
    return ALittlePsiImplUtil.guessTypes(this);
  }

  @Override
  public PsiReference getReference() {
    return ALittlePsiImplUtil.getReference(this);
  }

  @Override
  public void checkError() throws ALittleReferenceException {
    ALittlePsiImplUtil.checkError(this);
  }

}
