package plugin.format;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleLanguage;
import plugin.ALittleUtil;
import plugin.psi.*;
import plugin.psi.impl.ALittleClassStaticDecImpl;

import java.util.ArrayList;
import java.util.List;

public class ALittleBlock extends AbstractBlock {
    private SpacingBuilder m_spacing_builder;

    protected ALittleBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                           SpacingBuilder spacing_builder) {
        super(node, wrap, alignment);
        m_spacing_builder = spacing_builder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                Block block = new ALittleBlock(child,
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(),
                        m_spacing_builder);
                blocks.add(block);
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public Indent getIndent()
    {
        PsiElement element = myNode.getPsi();
        PsiElement parent = element.getParent();
        IElementType type = myNode.getElementType();

        if (element instanceof ALittleClassCtorDec
            || element instanceof ALittleClassMethodDec
            || element instanceof ALittleClassStaticDec
            || element instanceof ALittleClassSetterDec
            || element instanceof ALittleClassGetterDec
            || element instanceof ALittleClassVarDec)
            return Indent.getNormalIndent();

        if (element instanceof ALittleStructVarNameDec
            || element instanceof ALittleEnumVarNameDec)
            return Indent.getNormalIndent();

        if (element instanceof ALittleAllExpr)
            return Indent.getNormalIndent();

        if (type == ALittleTypes.COMMENT)
            return Indent.getContinuationIndent();

        return Indent.getNoneIndent();
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        PsiElement element = myNode.getPsi();
        PsiElement parent = element.getParent();
        IElementType type = myNode.getElementType();

        if (element instanceof ALittleStructDec
            || element instanceof ALittleEnumDec
            || element instanceof ALittleClassDec
            || element instanceof ALittleMethodBodyDec
            || element instanceof ALittleIfExpr
            || element instanceof ALittleElseIfExpr
            || element instanceof ALittleElseExpr
            || element instanceof ALittleForExpr
            || element instanceof ALittleWhileExpr
            || element instanceof ALittleWrapExpr)
            return new ChildAttributes(Indent.getNormalIndent(), null);

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return m_spacing_builder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
