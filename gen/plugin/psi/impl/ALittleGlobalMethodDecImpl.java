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

public class ALittleGlobalMethodDecImpl extends ASTWrapperPsiElement implements ALittleGlobalMethodDec {

  public ALittleGlobalMethodDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitGlobalMethodDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleAccessModifier getAccessModifier() {
    return findChildByClass(ALittleAccessModifier.class);
  }

  @Override
  @Nullable
  public ALittleCoModifier getCoModifier() {
    return findChildByClass(ALittleCoModifier.class);
  }

  @Override
  @Nullable
  public ALittleMethodBodyDec getMethodBodyDec() {
    return findChildByClass(ALittleMethodBodyDec.class);
  }

  @Override
  @Nullable
  public ALittleMethodNameDec getMethodNameDec() {
    return findChildByClass(ALittleMethodNameDec.class);
  }

  @Override
  @Nullable
  public ALittleMethodParamDec getMethodParamDec() {
    return findChildByClass(ALittleMethodParamDec.class);
  }

  @Override
  @Nullable
  public ALittleMethodReturnDec getMethodReturnDec() {
    return findChildByClass(ALittleMethodReturnDec.class);
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
