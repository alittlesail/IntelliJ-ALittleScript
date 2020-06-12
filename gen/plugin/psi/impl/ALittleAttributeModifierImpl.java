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

public class ALittleAttributeModifierImpl extends ASTWrapperPsiElement implements ALittleAttributeModifier {

  public ALittleAttributeModifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ALittleVisitor visitor) {
    visitor.visitAttributeModifier(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ALittleVisitor) accept((ALittleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ALittleCommandModifier getCommandModifier() {
    return findChildByClass(ALittleCommandModifier.class);
  }

  @Override
  @Nullable
  public ALittleConstModifier getConstModifier() {
    return findChildByClass(ALittleConstModifier.class);
  }

  @Override
  @Nullable
  public ALittleLanguageModifier getLanguageModifier() {
    return findChildByClass(ALittleLanguageModifier.class);
  }

  @Override
  @Nullable
  public ALittleNativeModifier getNativeModifier() {
    return findChildByClass(ALittleNativeModifier.class);
  }

  @Override
  @Nullable
  public ALittleNullableModifier getNullableModifier() {
    return findChildByClass(ALittleNullableModifier.class);
  }

  @Override
  @Nullable
  public ALittleProtocolModifier getProtocolModifier() {
    return findChildByClass(ALittleProtocolModifier.class);
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
