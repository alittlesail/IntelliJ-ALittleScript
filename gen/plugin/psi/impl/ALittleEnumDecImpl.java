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

public class ALittleEnumDecImpl extends ASTWrapperPsiElement implements ALittleEnumDec {

  public ALittleEnumDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitEnumDec(this);
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
  public ALittleEnumNameDec getEnumNameDec() {
    return findChildByClass(ALittleEnumNameDec.class);
  }

  @Override
  @NotNull
  public List<ALittleEnumVarDec> getEnumVarDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleEnumVarDec.class);
  }

}
