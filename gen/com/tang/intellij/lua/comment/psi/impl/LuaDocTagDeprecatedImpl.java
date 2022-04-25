// This is a generated file. Not intended for manual editing.
package com.tang.intellij.lua.comment.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.tang.intellij.lua.comment.psi.LuaDocTagDeprecated;
import com.tang.intellij.lua.comment.psi.LuaDocVisitor;
import org.jetbrains.annotations.NotNull;

public class LuaDocTagDeprecatedImpl extends ASTWrapperPsiElement implements LuaDocTagDeprecated {

  public LuaDocTagDeprecatedImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LuaDocVisitor visitor) {
    visitor.visitTagDeprecated(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LuaDocVisitor) accept((LuaDocVisitor)visitor);
    else super.accept(visitor);
  }

}
