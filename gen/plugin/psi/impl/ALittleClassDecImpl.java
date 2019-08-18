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

public class ALittleClassDecImpl extends ASTWrapperPsiElement implements ALittleClassDec {

  public ALittleClassDecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitClassDec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleClassAccessModifier getClassAccessModifier() {
    return findChildByClass(ALittleClassAccessModifier.class);
  }

  @Override
  @NotNull
  public List<ALittleClassCtorDec> getClassCtorDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassCtorDec.class);
  }

  @Override
  @Nullable
  public ALittleClassExtendsAccessModifier getClassExtendsAccessModifier() {
    return findChildByClass(ALittleClassExtendsAccessModifier.class);
  }

  @Override
  @Nullable
  public ALittleClassExtendsNameDec getClassExtendsNameDec() {
    return findChildByClass(ALittleClassExtendsNameDec.class);
  }

  @Override
  @Nullable
  public ALittleClassExtendsNamespaceNameDec getClassExtendsNamespaceNameDec() {
    return findChildByClass(ALittleClassExtendsNamespaceNameDec.class);
  }

  @Override
  @NotNull
  public List<ALittleClassGetterDec> getClassGetterDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassGetterDec.class);
  }

  @Override
  @NotNull
  public List<ALittleClassMethodDec> getClassMethodDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassMethodDec.class);
  }

  @Override
  @Nullable
  public ALittleClassNameDec getClassNameDec() {
    return findChildByClass(ALittleClassNameDec.class);
  }

  @Override
  @NotNull
  public List<ALittleClassSetterDec> getClassSetterDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassSetterDec.class);
  }

  @Override
  @NotNull
  public List<ALittleClassStaticDec> getClassStaticDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassStaticDec.class);
  }

  @Override
  @NotNull
  public List<ALittleClassVarDec> getClassVarDecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ALittleClassVarDec.class);
  }

  @Override
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

}
