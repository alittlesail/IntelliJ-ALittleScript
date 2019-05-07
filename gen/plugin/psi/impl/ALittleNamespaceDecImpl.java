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
  @Nullable
  public ALittleNamespaceRegisterDec getNamespaceRegisterDec() {
    return findChildByClass(ALittleNamespaceRegisterDec.class);
  }

  @Override
  @NotNull
  public List<ALittleStructDec> getStructDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleStructDec.class);
  }

}
