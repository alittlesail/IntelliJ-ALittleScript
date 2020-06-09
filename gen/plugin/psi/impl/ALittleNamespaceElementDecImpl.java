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
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;

public class ALittleNamespaceElementDecImpl extends ASTWrapperPsiElement implements ALittleNamespaceElementDec {

  public ALittleNamespaceElementDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitNamespaceElementDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleClassDec getClassDec() {
    return findChildByClass(ALittleClassDec.class);
  }

  @Override
  @Nullable
  public ALittleEnumDec getEnumDec() {
    return findChildByClass(ALittleEnumDec.class);
  }

  @Override
  @Nullable
  public ALittleGlobalMethodDec getGlobalMethodDec() {
    return findChildByClass(ALittleGlobalMethodDec.class);
  }

  @Override
  @Nullable
  public ALittleInstanceDec getInstanceDec() {
    return findChildByClass(ALittleInstanceDec.class);
  }

  @Override
  @NotNull
  public List<ALittleModifier> getModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleModifier.class);
  }

  @Override
  @Nullable
  public ALittleOpAssignExpr getOpAssignExpr() {
    return findChildByClass(ALittleOpAssignExpr.class);
  }

  @Override
  @Nullable
  public ALittleStructDec getStructDec() {
    return findChildByClass(ALittleStructDec.class);
  }

  @Override
  @Nullable
  public ALittleUsingDec getUsingDec() {
    return findChildByClass(ALittleUsingDec.class);
  }

  @Override
  @NotNull
  public ALittleGuess guessType() throws ALittleGuessException {
    return ALittlePsiImplUtil.guessType(this);
  }

  @Override
  @NotNull
  public List<ALittleGuess> guessTypes() throws ALittleGuessException {
    return ALittlePsiImplUtil.guessTypes(this);
  }

  @Override
  public PsiReference getReference() {
    return ALittlePsiImplUtil.getReference(this);
  }

}
