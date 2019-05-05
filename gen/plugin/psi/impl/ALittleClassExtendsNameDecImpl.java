// This is a generated file. Not intended for manual editing.
package plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static plugin.psi.ALittleTypes.*;
import plugin.psi.*;
import com.intellij.psi.PsiReference;

public class ALittleClassExtendsNameDecImpl extends ALittleClassExtendsNameDecElementImpl implements ALittleClassExtendsNameDec {

  public ALittleClassExtendsNameDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitClassExtendsNameDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdContent() {
    return findNotNullChildByType(ID_CONTENT);
  }

  public PsiElement guessType() {
    return ALittlePsiImplUtil.guessType(this);
  }

  @NotNull
  public List<PsiElement> guessTypes() {
    return ALittlePsiImplUtil.guessTypes(this);
  }

  public PsiReference[] getReferences() {
    return ALittlePsiImplUtil.getReferences(this);
  }

  public String getName() {
    return ALittlePsiImplUtil.getName(this);
  }

  public PsiElement setName(String new_name) {
    return ALittlePsiImplUtil.setName(this, new_name);
  }

  public PsiElement getNameIdentifier() {
    return ALittlePsiImplUtil.getNameIdentifier(this);
  }

}
