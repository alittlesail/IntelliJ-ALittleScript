package plugin.format;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.ALittleLanguage;
import plugin.psi.ALittleTypes;

public class ALittleFormattingModelBuilder implements FormattingModelBuilder {
    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        return FormattingModelProvider.createFormattingModelForPsiFile(
                element.getContainingFile(),
                new ALittleBlock(element.getNode(),
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(),
                        createSpaceBuilder(settings)),
                settings);
    }

    // 这个是处理非行开头部分的代码空格
    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        // 这里用来添加空格
        return new SpacingBuilder(settings, ALittleLanguage.INSTANCE)
                .before(ALittleTypes.RBRACE).spaces(1)
                .before(ALittleTypes.LBRACE).spaces(1);
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}
