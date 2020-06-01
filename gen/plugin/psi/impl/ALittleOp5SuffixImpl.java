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

public class ALittleOp5SuffixImpl extends ASTWrapperPsiElement implements ALittleOp5Suffix {

  public ALittleOp5SuffixImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitOp5Suffix(this);
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
  public ALittleOp5 getOp5() {
    return findNotNullChildByClass(ALittleOp5.class);
  }

  @Override
  @NotNull
  public List<ALittleOp5SuffixEe> getOp5SuffixEeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleOp5SuffixEe.class);
  }

  @Override
  @Nullable
  public ALittleValueFactorStat getValueFactorStat() {
    return findChildByClass(ALittleValueFactorStat.class);
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
