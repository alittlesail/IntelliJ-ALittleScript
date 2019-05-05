package plugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static plugin.psi.ALittleTypes.*;

%%

%{
  public _ALittleLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ALittleLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

WHITE_SPACE=[ \t\n\x0B\f\r]+
COMMENT="//".*
DIGIT_CONTENT=0x[0-9a-fA-F]+|[0-9]+(\.[0-9]*)?
STRING_CONTENT=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
ID_CONTENT=[_a-zA-Z][_a-zA-Z0-9]*

%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

  "{"                   { return LBRACE; }
  "}"                   { return RBRACE; }
  "["                   { return LBRACK; }
  "]"                   { return RBRACK; }
  "("                   { return LPAREN; }
  ")"                   { return RPAREN; }
  ":"                   { return COLON; }
  ";"                   { return SEMI; }
  ","                   { return COMMA; }
  "=="                  { return EQ; }
  "="                   { return ASSIGN; }
  "!="                  { return NOT_EQ; }
  "!"                   { return NOT; }
  "++"                  { return PLUS_PLUS; }
  "+="                  { return PLUS_ASSIGN; }
  "+"                   { return PLUS; }
  ".."                  { return CONCAT; }
  "--"                  { return MINUS_MINUS; }
  "-="                  { return MINUS_ASSIGN; }
  "-"                   { return MINUS; }
  "||"                  { return COND_OR; }
  "|="                  { return BIT_OR_ASSIGN; }
  "&&"                  { return COND_AND; }
  "&="                  { return BIT_AND_ASSIGN; }
  "&"                   { return BIT_AND; }
  "|"                   { return BIT_OR; }
  "~"                   { return BIT_NOT; }
  "<<="                 { return SHIFT_LEFT_ASSIGN; }
  "<<"                  { return SHIFT_LEFT; }
  "<="                  { return LESS_OR_EQUAL; }
  "<"                   { return LESS; }
  "^="                  { return BIT_XOR_ASSIGN; }
  "^"                   { return BIT_XOR; }
  "*="                  { return MUL_ASSIGN; }
  "*"                   { return MUL; }
  "/="                  { return QUOTIENT_ASSIGN; }
  "/"                   { return QUOTIENT; }
  "%="                  { return REMAINDER_ASSIGN; }
  "%"                   { return REMAINDER; }
  ">>="                 { return SHIFT_RIGHT_ASSIGN; }
  ">>"                  { return SHIFT_RIGHT; }
  ">="                  { return GREATER_OR_EQUAL; }
  ">"                   { return GREATER; }
  "."                   { return DOT; }
  "'"                   { return APOS; }
  "\""                  { return QUOTE; }
  "\\"                  { return BACK; }
  "namespace"           { return NAMESPACE; }
  "instance"            { return INSTANCE; }
  "new"                 { return NEW; }
  "class"               { return CLASS; }
  "Ctor"                { return CTOR; }
  "get"                 { return GET; }
  "set"                 { return SET; }
  "fun"                 { return FUN; }
  "static"              { return STATIC; }
  "struct"              { return STRUCT; }
  "enum"                { return ENUM; }
  "protocol"            { return PROTOCOL; }
  "public"              { return PUBLIC; }
  "private"             { return PRIVATE; }
  "protected"           { return PROTECTED; }
  "for"                 { return FOR; }
  "in"                  { return IN; }
  "while"               { return WHILE; }
  "do"                  { return DO; }
  "if"                  { return IF; }
  "else"                { return ELSE; }
  "elseif"              { return ELSEIF; }
  "return"              { return RETURN; }
  "break"               { return BREAK; }
  "var"                 { return VAR; }
  "Map"                 { return MAP; }
  "List"                { return LIST; }
  "Functor"             { return FUNCTOR; }
  "bool"                { return BOOL; }
  "double"              { return DOUBLE; }
  "int"                 { return INT; }
  "any"                 { return ANY; }
  "string"              { return STRING; }
  "true"                { return TRUE; }
  "false"               { return FALSE; }
  "null"                { return NULL; }
  "this"                { return THIS; }

  {WHITE_SPACE}         { return WHITE_SPACE; }
  {COMMENT}             { return COMMENT; }
  {DIGIT_CONTENT}       { return DIGIT_CONTENT; }
  {STRING_CONTENT}      { return STRING_CONTENT; }
  {ID_CONTENT}          { return ID_CONTENT; }

}

[^] { return BAD_CHARACTER; }
