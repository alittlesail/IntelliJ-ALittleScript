package plugin;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.NotNull;
import plugin.parser.ALittleParser;
import plugin.psi.ALittleFile;
import plugin.psi.ALittleTypes;

public class ALittleParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(ALittleLanguage.INSTANCE);

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENT_SET = TokenSet.create(ALittleTypes.COMMENT);
    public static final TokenSet STRING_CONTENT_SET = TokenSet.create(ALittleTypes.STRING_CONTENT);
    public static final TokenSet NUMBER_CONTENT_SET = TokenSet.create(ALittleTypes.DIGIT_CONTENT);
    public static final TokenSet BAD_CHARACTER_SET = TokenSet.create(TokenType.BAD_CHARACTER);

    public static final TokenSet KEYWORD_SET = TokenSet.create(ALittleTypes.ANY, ALittleTypes.INT, ALittleTypes.I64, ALittleTypes.DOUBLE, ALittleTypes.STRING, ALittleTypes.BOOL,
            ALittleTypes.CLASS, ALittleTypes.ENUM, ALittleTypes.STRUCT, ALittleTypes.INSTANCE,
            ALittleTypes.PROTO, ALittleTypes.PUBLIC, ALittleTypes.PRIVATE, ALittleTypes.PROTECTED, ALittleTypes.STATIC, ALittleTypes.INSTANCE,
            ALittleTypes.BREAK, ALittleTypes.CTOR,
            ALittleTypes.IF, ALittleTypes.ELSE, ALittleTypes.ELSEIF, ALittleTypes.DO, ALittleTypes.WHILE, ALittleTypes.FOR,
            ALittleTypes.IN, ALittleTypes.LIST, ALittleTypes.MAP,
            ALittleTypes.NEW, ALittleTypes.RETURN, ALittleTypes.NAMESPACE, ALittleTypes.THIS, ALittleTypes.FUNCTOR,
            ALittleTypes.GET, ALittleTypes.SET, ALittleTypes.FUN, ALittleTypes.VAR, ALittleTypes.COMMA);

    public static final TokenSet SYMBOL_SET = TokenSet.create(ALittleTypes.APOS, ALittleTypes.ASSIGN, ALittleTypes.BACK, ALittleTypes.BIT_AND, ALittleTypes.BIT_AND_ASSIGN,
            ALittleTypes.BIT_NOT, ALittleTypes.BIT_OR, ALittleTypes.BIT_OR_ASSIGN, ALittleTypes.BIT_XOR,
            ALittleTypes.BIT_XOR_ASSIGN, ALittleTypes.COLON, ALittleTypes.COND_AND, ALittleTypes.COND_OR,
            ALittleTypes.DOT, ALittleTypes.EQ, ALittleTypes.GREATER,
            ALittleTypes.GREATER_OR_EQUAL, ALittleTypes.LBRACE, ALittleTypes.LBRACK, ALittleTypes.LESS, ALittleTypes.LESS_OR_EQUAL, ALittleTypes.LPAREN,
            ALittleTypes.MINUS, ALittleTypes.MINUS_ASSIGN, ALittleTypes.MINUS_MINUS, ALittleTypes.MUL, ALittleTypes.MUL_ASSIGN, ALittleTypes.NOT,
            ALittleTypes.NOT_EQ, ALittleTypes.PLUS, ALittleTypes.PLUS_ASSIGN, ALittleTypes.PLUS_PLUS, ALittleTypes.QUOTE, ALittleTypes.QUOTIENT,
            ALittleTypes.QUOTIENT_ASSIGN, ALittleTypes.RBRACE, ALittleTypes.RBRACK, ALittleTypes.REMAINDER, ALittleTypes.REMAINDER_ASSIGN, ALittleTypes.RPAREN,
            ALittleTypes.SEMI, ALittleTypes.SHIFT_LEFT, ALittleTypes.SHIFT_LEFT_ASSIGN, ALittleTypes.SHIFT_RIGHT, ALittleTypes.SHIFT_RIGHT_ASSIGN);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ALittleLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENT_SET;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new ALittleParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ALittleFile(viewProvider);
    }

    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ALittleTypes.Factory.createElement(node);
    }
}
