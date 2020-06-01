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

public class ALittleClassElementDecImpl extends ASTWrapperPsiElement implements ALittleClassElementDec {

  public ALittleClassElementDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitClassElementDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleClassCtorDec getClassCtorDec() {
    return findChildByClass(ALittleClassCtorDec.class);
  }

  @Override
  @Nullable
  public ALittleClassGetterDec getClassGetterDec() {
    return findChildByClass(ALittleClassGetterDec.class);
  }

  @Override
  @Nullable
  public ALittleClassMethodDec getClassMethodDec() {
    return findChildByClass(ALittleClassMethodDec.class);
  }

  @Override
  @Nullable
  public ALittleClassSetterDec getClassSetterDec() {
    return findChildByClass(ALittleClassSetterDec.class);
  }

  @Override
  @Nullable
  public ALittleClassStaticDec getClassStaticDec() {
    return findChildByClass(ALittleClassStaticDec.class);
  }

  @Override
  @Nullable
  public ALittleClassVarDec getClassVarDec() {
    return findChildByClass(ALittleClassVarDec.class);
  }

  @Override
  @NotNull
  public List<ALittleModifier> getModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleModifier.class);
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
