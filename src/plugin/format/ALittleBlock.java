package plugin.format;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleLanguage;
import plugin.ALittleUtil;
import plugin.psi.ALittleTypes;

import java.util.ArrayList;
import java.util.List;

public class ALittleBlock extends AbstractBlock implements BlockWithParent {
    private final CodeStyleSettings mySettings;
    private BlockWithParent myParent;

    protected ALittleBlock(ASTNode node, Wrap wrap, Alignment alignment, CodeStyleSettings settings) {
        super(node, wrap, alignment);
        mySettings = settings;
    }

    @Override
    public Indent getIndent() {
        final IElementType elementType = myNode.getElementType();
        final ASTNode prevSibling = ALittleUtil.getPrevSiblingSkipWhiteSpacesAndComments(myNode);
        final IElementType prevSiblingType = prevSibling == null ? null : prevSibling.getElementType();
        final ASTNode parent = myNode.getTreeParent();
        final IElementType parentType = parent != null ? parent.getElementType() : null;
        final ASTNode superParent = parent == null ? null : parent.getTreeParent();
        final IElementType superParentType = superParent == null ? null : superParent.getElementType();

        if (elementType == ALittleTypes.LBRACE) {
            return Indent.getNoneIndent();
        }

        if (parentType == ALittleTypes.IF_EXPR
            || parentType == ALittleTypes.ELSE_IF_EXPR
            || parentType == ALittleTypes.ELSE_EXPR
            || parentType == ALittleTypes.METHOD_BODY_DEC
            || parentType == ALittleTypes.CLASS_DEC
            || parentType == ALittleTypes.STRUCT_DEC
            || parentType == ALittleTypes.ENUM_DEC) {
            return Indent.getNormalIndent();
        }

        return Indent.getNoneIndent();
    }

    @Nullable
    public Wrap createChildWrap(ASTNode child) {
        return null;
    }

    @Nullable
    protected Alignment createChildAlignment(ASTNode child) {
        return null;
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        return null;
    }

    @Override
    protected List<Block> buildChildren() {
        final ArrayList<Block> tlChildren = new ArrayList<>();
        for (ASTNode childNode = getNode().getFirstChildNode(); childNode != null; childNode = childNode.getTreeNext()) {
            if (FormatterUtil.containsWhiteSpacesOnly(childNode)) continue;
            final ALittleBlock childBlock = new ALittleBlock(childNode, createChildWrap(childNode), createChildAlignment(childNode), mySettings);
            childBlock.setParent(this);
            tlChildren.add(childBlock);
        }
        return tlChildren;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newIndex) {
        int index = newIndex;
        ASTBlock prev = null;
        do {
            if (index == 0) {
                break;
            }
            prev = (ASTBlock)getSubBlocks().get(index - 1);
            index--;
        }
        while (prev.getNode().getElementType() == ALittleTypes.SEMI || prev.getNode() instanceof PsiWhiteSpace);

        final IElementType elementType = myNode.getElementType();
        final IElementType prevType = prev == null ? null : prev.getNode().getElementType();

        // System.out.print(prevType);
        // System.out.print("->");
        // System.out.println(elementType);

        if (elementType == ALittleTypes.IF_EXPR
            || elementType == ALittleTypes.ELSE_IF_EXPR
            || elementType == ALittleTypes.ELSE_EXPR) {
            if (prevType == ALittleTypes.ELSE || prevType == ALittleTypes.RPAREN) {
                return new ChildAttributes(Indent.getNoneIndent(), null);
            }
            return new ChildAttributes(Indent.getNormalIndent(), null);
        } else if (elementType == ALittleTypes.METHOD_BODY_DEC
            || elementType == ALittleTypes.CLASS_DEC
            || elementType == ALittleTypes.STRUCT_DEC
            || elementType == ALittleTypes.ENUM_DEC) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public BlockWithParent getParent() {
        return myParent;
    }

    @Override
    public void setParent(BlockWithParent newParent) {
        myParent = newParent;
    }
}
