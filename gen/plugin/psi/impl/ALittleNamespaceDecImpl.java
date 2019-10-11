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

public class ALittleNamespaceDecImpl extends ASTWrapperPsiElement implements ALittleNamespaceDec {

  public ALittleNamespaceDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitNamespaceDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ALittleClassDec> getClassDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassDec.class);
  }

  @Override
  @NotNull
  public List<ALittleEnumDec> getEnumDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleEnumDec.class);
  }

  @Override
  @NotNull
  public List<ALittleGlobalMethodDec> getGlobalMethodDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleGlobalMethodDec.class);
  }

  @Override
  @NotNull
  public List<ALittleInstanceDec> getInstanceDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleInstanceDec.class);
  }

  @Override
  @Nullable
  public ALittleNamespaceNameDec getNamespaceNameDec() {
    return findChildByClass(ALittleNamespaceNameDec.class);
  }

  @Override
  @NotNull
  public List<ALittleOpAssignExpr> getOpAssignExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleOpAssignExpr.class);
  }

  @Override
  @NotNull
  public List<ALittlePropertyValueExpr> getPropertyValueExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittlePropertyValueExpr.class);
  }

  @Override
  @Nullable
  public ALittleRegisterModifier getRegisterModifier() {
    return findChildByClass(ALittleRegisterModifier.class);
  }

  @Override
  @NotNull
  public List<ALittleStructDec> getStructDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleStructDec.class);
  }

  @Override
  @NotNull
  public List<ALittleUsingDec> getUsingDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleUsingDec.class);
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
