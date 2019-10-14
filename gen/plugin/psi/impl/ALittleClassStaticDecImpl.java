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

public class ALittleClassStaticDecImpl extends ASTWrapperPsiElement implements ALittleClassStaticDec {

  public ALittleClassStaticDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitClassStaticDec(this);
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
  @Nullable
  public ALittleTemplateDec getTemplateDec() {
    return findChildByClass(ALittleTemplateDec.class);
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
