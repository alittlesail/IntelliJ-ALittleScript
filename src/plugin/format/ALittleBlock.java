package plugin.format;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import groovyjarjarantlr.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleBlock extends AbstractBlock {
    private final SpacingBuilder m_spacingBuilder;

    protected ALittleBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                           SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        m_spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();

        if (myNode.getElementType() == ALittleTypes.NAMESPACE_ELEMENT_DEC) {
            ASTNode child = myNode.getFirstChildNode();
            while (child != null) {
                if (child.getElementType() != TokenType.WHITE_SPACE) {
                    ALittleBlock block = new ALittleBlock(child,
                            Wrap.createWrap(WrapType.NONE, false),
                            Alignment.createAlignment(),
                            m_spacingBuilder);

                    if (child.getElementType() == ALittleTypes.GLOBAL_METHOD_DEC
                        || child.getElementType() == ALittleTypes.CLASS_DEC
                        || child.getElementType() == ALittleTypes.ENUM_DEC
                        || child.getElementType() == ALittleTypes.STRUCT_DEC
                        || child.getElementType() == ALittleTypes.USING_DEC
                        || child.getElementType() == ALittleTypes.INSTANCE_DEC) {
                        blocks.addAll(block.buildChildren());
                    }
                    else
                    {
                        blocks.add(block);
                    }
                }
                child = child.getTreeNext();
            }
            return blocks;
        }

        if (myNode.getElementType() == ALittleTypes.CLASS_ELEMENT_DEC) {
            ASTNode child = myNode.getFirstChildNode();
            while (child != null) {
                if (child.getElementType() != TokenType.WHITE_SPACE) {
                    ALittleBlock block = new ALittleBlock(child,
                            Wrap.createWrap(WrapType.NONE, false),
                            Alignment.createAlignment(),
                            m_spacingBuilder);

                    if (child.getElementType() == ALittleTypes.CLASS_CTOR_DEC
                            || child.getElementType() == ALittleTypes.CLASS_METHOD_DEC
                            || child.getElementType() == ALittleTypes.CLASS_GETTER_DEC
                            || child.getElementType() == ALittleTypes.CLASS_SETTER_DEC
                            || child.getElementType() == ALittleTypes.CLASS_STATIC_DEC
                            || child.getElementType() == ALittleTypes.CLASS_VAR_DEC) {
                        blocks.addAll(block.buildChildren());
                    }
                    else
                    {
                        blocks.add(block);
                    }
                }
                child = child.getTreeNext();
            }
            return blocks;
        }

        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                Block block = new ALittleBlock(child,
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(),
                        m_spacingBuilder);
                blocks.add(block);
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public Indent getIndent() {
        PsiElement element = myNode.getPsi();
        PsiElement parent = element.getParent();
        IElementType type = myNode.getElementType();

        if (element instanceof ALittleClassElementDec)
            return Indent.getNormalIndent();

        if (element instanceof ALittleStructVarDec
                || element instanceof ALittleEnumVarDec)
            return Indent.getNormalIndent();

        if (element instanceof ALittleAllExpr)
            return Indent.getNormalIndent();

        if (type == ALittleTypes.LINE_COMMENT || type == ALittleTypes.BLOCK_COMMENT) {
            if (parent instanceof ALittleFile || parent instanceof ALittleNamespaceDec)
                return Indent.getNoneIndent();
            return Indent.getNormalIndent();
        }

        return Indent.getNoneIndent();
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        PsiElement element = myNode.getPsi();

        if (element instanceof ALittleClassBodyDec
                || element instanceof ALittleStructBodyDec
                || element instanceof ALittleEnumBodyDec
                || element instanceof ALittleMethodBodyDec
                || element instanceof ALittleIfBody
                || element instanceof ALittleElseIfBody
                || element instanceof ALittleElseBody
                || element instanceof ALittleForBody
                || element instanceof ALittleWhileBody
                || element instanceof ALittleWrapExpr)
            return new ChildAttributes(Indent.getNormalIndent(), null);

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return m_spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
