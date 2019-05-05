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

public class ALittleStructDecImpl extends ASTWrapperPsiElement implements ALittleStructDec {

  public ALittleStructDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitStructDec(this);
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
  public ALittleStructExtendsNameDec getStructExtendsNameDec() {
    return findChildByClass(ALittleStructExtendsNameDec.class);
  }

  @Override
  @Nullable
  public ALittleStructExtendsNamespaceNameDec getStructExtendsNamespaceNameDec() {
    return findChildByClass(ALittleStructExtendsNamespaceNameDec.class);
  }

  @Override
  @NotNull
  public ALittleStructNameDec getStructNameDec() {
    return findNotNullChildByClass(ALittleStructNameDec.class);
  }

  @Override
  @Nullable
  public ALittleStructProtocolDec getStructProtocolDec() {
    return findChildByClass(ALittleStructProtocolDec.class);
  }

  @Override
  @NotNull
  public List<ALittleStructVarDec> getStructVarDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleStructVarDec.class);
  }

}
