package plugin.hightlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleLexerAdapter;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class ALittleSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("ALITTLE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SYMBOL =
            createTextAttributesKey("ALITTLE_SYMBOL", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("ALITTLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("ALITTLE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("ALITTLE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("ALITTLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey ANNO =
            createTextAttributesKey("ALITTLE_ANNO", DefaultLanguageHighlighterColors.STATIC_METHOD);

    private static final TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_CONTENT_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_CONTENT_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] SYMBOL_KEYS = new TextAttributesKey[]{SYMBOL};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] ANNO_KEYS = new TextAttributesKey[]{ANNO};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ALittleLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (ALittleParserDefinition.KEYWORD_SET.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (ALittleParserDefinition.SYMBOL_SET.contains(tokenType)) {
            return SYMBOL_KEYS;
        } else if (ALittleParserDefinition.COMMENT_SET.contains(tokenType)) {
            return COMMENT_KEYS;
        } else if (ALittleParserDefinition.STRING_CONTENT_SET.contains(tokenType)) {
            return STRING_CONTENT_KEYS;
        } else if (ALittleParserDefinition.NUMBER_CONTENT_SET.contains(tokenType)) {
            return NUMBER_CONTENT_KEYS;
        } else if (ALittleParserDefinition.BAD_CHARACTER_SET.contains(tokenType)) {
            return BAD_CHARACTER_KEYS;
        } else if (ALittleParserDefinition.ANNO_SET.contains(tokenType)) {
            return ANNO_KEYS;
        }

        return EMPTY_KEYS;
    }
}

