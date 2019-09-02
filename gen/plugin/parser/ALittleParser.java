// This is a generated file. Not intended for manual editing.
package plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static plugin.psi.ALittleTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ALittleParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return alittleFile(b, l + 1);
  }

  /* ********************************************************** */
  // public | private | protected
  public static boolean accessModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACCESS_MODIFIER, "<access modifier>");
    r = consumeToken(b, PUBLIC);
    if (!r) r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, PROTECTED);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespaceDec?
  static boolean alittleFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alittleFile")) return false;
    namespaceDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // varAssignExpr |
  //             opAssignExpr |
  //             op1Expr |
  //             ifExpr |
  //             forExpr |
  //             whileExpr |
  //             doWhileExpr |
  //             returnExpr |
  //             flowExpr |
  //             wrapExpr |
  //             propertyValueExpr |
  //             emptyExpr
  public static boolean allExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "allExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_EXPR, "<all expr>");
    r = varAssignExpr(b, l + 1);
    if (!r) r = opAssignExpr(b, l + 1);
    if (!r) r = op1Expr(b, l + 1);
    if (!r) r = ifExpr(b, l + 1);
    if (!r) r = forExpr(b, l + 1);
    if (!r) r = whileExpr(b, l + 1);
    if (!r) r = doWhileExpr(b, l + 1);
    if (!r) r = returnExpr(b, l + 1);
    if (!r) r = flowExpr(b, l + 1);
    if (!r) r = wrapExpr(b, l + 1);
    if (!r) r = propertyValueExpr(b, l + 1);
    if (!r) r = emptyExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // primitiveType | genericType | customType
  public static boolean allType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "allType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_TYPE, "<all type>");
    r = primitiveType(b, l + 1);
    if (!r) r = genericType(b, l + 1);
    if (!r) r = customType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // auto
  public static boolean autoType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "autoType")) return false;
    if (!nextTokenIs(b, AUTO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AUTO);
    exit_section_(b, m, AUTO_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // bind LPAREN (valueStat (COMMA valueStat)*)? RPAREN
  public static boolean bindStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bindStat")) return false;
    if (!nextTokenIs(b, BIND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BIND_STAT, null);
    r = consumeTokens(b, 1, BIND, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, bindStat_2(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (valueStat (COMMA valueStat)*)?
  private static boolean bindStat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bindStat_2")) return false;
    bindStat_2_0(b, l + 1);
    return true;
  }

  // valueStat (COMMA valueStat)*
  private static boolean bindStat_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bindStat_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && bindStat_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean bindStat_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bindStat_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!bindStat_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bindStat_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean bindStat_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bindStat_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE (classVarDec | classCtorDec | classGetterDec | classSetterDec | classStaticDec | classMethodDec)* RBRACE
  static boolean classBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, classBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (classVarDec | classCtorDec | classGetterDec | classSetterDec | classStaticDec | classMethodDec)*
  private static boolean classBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!classBodyDec_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "classBodyDec_1", c)) break;
    }
    return true;
  }

  // classVarDec | classCtorDec | classGetterDec | classSetterDec | classStaticDec | classMethodDec
  private static boolean classBodyDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classBodyDec_1_0")) return false;
    boolean r;
    r = classVarDec(b, l + 1);
    if (!r) r = classCtorDec(b, l + 1);
    if (!r) r = classGetterDec(b, l + 1);
    if (!r) r = classSetterDec(b, l + 1);
    if (!r) r = classStaticDec(b, l + 1);
    if (!r) r = classMethodDec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // accessModifier? Ctor methodParamDec methodBodyDec
  public static boolean classCtorDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classCtorDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_CTOR_DEC, "<class ctor dec>");
    r = classCtorDec_0(b, l + 1);
    r = r && consumeToken(b, CTOR);
    p = r; // pin = 2
    r = r && report_error_(b, methodParamDec(b, l + 1));
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classCtorDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classCtorDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? class classNameDec templateDec? classExtendsDec? classBodyDec
  public static boolean classDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_DEC, "<class dec>");
    r = classDec_0(b, l + 1);
    r = r && consumeToken(b, CLASS);
    p = r; // pin = 2
    r = r && report_error_(b, classNameDec(b, l + 1));
    r = p && report_error_(b, classDec_3(b, l + 1)) && r;
    r = p && report_error_(b, classDec_4(b, l + 1)) && r;
    r = p && classBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // templateDec?
  private static boolean classDec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDec_3")) return false;
    templateDec(b, l + 1);
    return true;
  }

  // classExtendsDec?
  private static boolean classDec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDec_4")) return false;
    classExtendsDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COLON accessModifier? (namespaceNameDec DOT)? classNameDec classTemplate?
  public static boolean classExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_EXTENDS_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, classExtendsDec_1(b, l + 1));
    r = p && report_error_(b, classExtendsDec_2(b, l + 1)) && r;
    r = p && report_error_(b, classNameDec(b, l + 1)) && r;
    r = p && classExtendsDec_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classExtendsDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classExtendsDec_1")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // (namespaceNameDec DOT)?
  private static boolean classExtendsDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classExtendsDec_2")) return false;
    classExtendsDec_2_0(b, l + 1);
    return true;
  }

  // namespaceNameDec DOT
  private static boolean classExtendsDec_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classExtendsDec_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceNameDec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // classTemplate?
  private static boolean classExtendsDec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classExtendsDec_4")) return false;
    classTemplate(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? get methodNameDec LPAREN RPAREN COLON allType methodBodyDec
  public static boolean classGetterDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classGetterDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_GETTER_DEC, "<class getter dec>");
    r = classGetterDec_0(b, l + 1);
    r = r && consumeToken(b, GET);
    p = r; // pin = 2
    r = r && report_error_(b, methodNameDec(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, LPAREN, RPAREN, COLON)) && r;
    r = p && report_error_(b, allType(b, l + 1)) && r;
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classGetterDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classGetterDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? coModifier? fun methodNameDec methodParamDec methodReturnDec? methodBodyDec
  public static boolean classMethodDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classMethodDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_METHOD_DEC, "<class method dec>");
    r = classMethodDec_0(b, l + 1);
    r = r && classMethodDec_1(b, l + 1);
    r = r && consumeToken(b, FUN);
    p = r; // pin = 3
    r = r && report_error_(b, methodNameDec(b, l + 1));
    r = p && report_error_(b, methodParamDec(b, l + 1)) && r;
    r = p && report_error_(b, classMethodDec_5(b, l + 1)) && r;
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classMethodDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classMethodDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // coModifier?
  private static boolean classMethodDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classMethodDec_1")) return false;
    coModifier(b, l + 1);
    return true;
  }

  // methodReturnDec?
  private static boolean classMethodDec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classMethodDec_5")) return false;
    methodReturnDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean classNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // accessModifier? set methodNameDec LPAREN methodParamOneDec RPAREN methodBodyDec
  public static boolean classSetterDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classSetterDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_SETTER_DEC, "<class setter dec>");
    r = classSetterDec_0(b, l + 1);
    r = r && consumeToken(b, SET);
    p = r; // pin = 2
    r = r && report_error_(b, methodNameDec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, methodParamOneDec(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classSetterDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classSetterDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? coModifier? static methodNameDec methodParamDec methodReturnDec? methodBodyDec
  public static boolean classStaticDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStaticDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_STATIC_DEC, "<class static dec>");
    r = classStaticDec_0(b, l + 1);
    r = r && classStaticDec_1(b, l + 1);
    r = r && consumeToken(b, STATIC);
    p = r; // pin = 3
    r = r && report_error_(b, methodNameDec(b, l + 1));
    r = p && report_error_(b, methodParamDec(b, l + 1)) && r;
    r = p && report_error_(b, classStaticDec_5(b, l + 1)) && r;
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classStaticDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStaticDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // coModifier?
  private static boolean classStaticDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStaticDec_1")) return false;
    coModifier(b, l + 1);
    return true;
  }

  // methodReturnDec?
  private static boolean classStaticDec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStaticDec_5")) return false;
    methodReturnDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LESS allType classTemplatePair* GREATER
  static boolean classTemplate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classTemplate")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && report_error_(b, classTemplate_2(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // classTemplatePair*
  private static boolean classTemplate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classTemplate_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!classTemplatePair(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "classTemplate_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMA allType
  static boolean classTemplatePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classTemplatePair")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && allType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // accessModifier? allType ID_CONTENT SEMI
  public static boolean classVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classVarDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_VAR_DEC, "<class var dec>");
    r = classVarDec_0(b, l + 1);
    r = r && allType(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, consumeTokens(b, -1, ID_CONTENT, SEMI));
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean classVarDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classVarDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // async | await
  public static boolean coModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "coModifier")) return false;
    if (!nextTokenIs(b, "<co modifier>", ASYNC, AWAIT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CO_MODIFIER, "<co modifier>");
    r = consumeToken(b, ASYNC);
    if (!r) r = consumeToken(b, AWAIT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // true | false | STRING_CONTENT | DIGIT_CONTENT | null
  public static boolean constValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONST_VALUE, "<const value>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, STRING_CONTENT);
    if (!r) r = consumeToken(b, DIGIT_CONTENT);
    if (!r) r = consumeToken(b, NULL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (namespaceNameDec DOT)? ID_CONTENT customTypeTemplate?
  public static boolean customType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customType")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = customType_0(b, l + 1);
    r = r && consumeToken(b, ID_CONTENT);
    r = r && customType_2(b, l + 1);
    exit_section_(b, m, CUSTOM_TYPE, r);
    return r;
  }

  // (namespaceNameDec DOT)?
  private static boolean customType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customType_0")) return false;
    customType_0_0(b, l + 1);
    return true;
  }

  // namespaceNameDec DOT
  private static boolean customType_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customType_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceNameDec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // customTypeTemplate?
  private static boolean customType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customType_2")) return false;
    customTypeTemplate(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LESS allType customTypeTemplatePair* GREATER
  static boolean customTypeTemplate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customTypeTemplate")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && report_error_(b, customTypeTemplate_2(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // customTypeTemplatePair*
  private static boolean customTypeTemplate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customTypeTemplate_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!customTypeTemplatePair(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "customTypeTemplate_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMA allType
  static boolean customTypeTemplatePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customTypeTemplatePair")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && allType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean doWhileBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doWhileBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, doWhileBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean doWhileBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doWhileBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "doWhileBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN valueStat RPAREN
  static boolean doWhileCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doWhileCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // do doWhileBody while doWhileCondition SEMI
  public static boolean doWhileExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doWhileExpr")) return false;
    if (!nextTokenIs(b, DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_WHILE_EXPR, null);
    r = consumeToken(b, DO);
    p = r; // pin = 1
    r = r && report_error_(b, doWhileBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, WHILE)) && r;
    r = p && report_error_(b, doWhileCondition(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean elseBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, elseBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean elseBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "elseBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // else (elseBody | allExpr)
  public static boolean elseExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseExpr")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_EXPR, null);
    r = consumeToken(b, ELSE);
    p = r; // pin = 1
    r = r && elseExpr_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // elseBody | allExpr
  private static boolean elseExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseExpr_1")) return false;
    boolean r;
    r = elseBody(b, l + 1);
    if (!r) r = allExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean elseIfBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseIfBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, elseIfBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean elseIfBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseIfBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "elseIfBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN valueStat RPAREN
  static boolean elseIfCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseIfCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // elseif elseIfCondition (elseIfBody | allExpr)
  public static boolean elseIfExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseIfExpr")) return false;
    if (!nextTokenIs(b, ELSEIF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF_EXPR, null);
    r = consumeToken(b, ELSEIF);
    p = r; // pin = 1
    r = r && report_error_(b, elseIfCondition(b, l + 1));
    r = p && elseIfExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // elseIfBody | allExpr
  private static boolean elseIfExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseIfExpr_2")) return false;
    boolean r;
    r = elseIfBody(b, l + 1);
    if (!r) r = allExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SEMI
  public static boolean emptyExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyExpr")) return false;
    if (!nextTokenIs(b, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    exit_section_(b, m, EMPTY_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE (enumVarDec(COMMA enumVarDec)* COMMA?)? RBRACE
  static boolean enumBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, enumBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (enumVarDec(COMMA enumVarDec)* COMMA?)?
  private static boolean enumBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec_1")) return false;
    enumBodyDec_1_0(b, l + 1);
    return true;
  }

  // enumVarDec(COMMA enumVarDec)* COMMA?
  private static boolean enumBodyDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enumVarDec(b, l + 1);
    r = r && enumBodyDec_1_0_1(b, l + 1);
    r = r && enumBodyDec_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA enumVarDec)*
  private static boolean enumBodyDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enumBodyDec_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enumBodyDec_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA enumVarDec
  private static boolean enumBodyDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enumVarDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean enumBodyDec_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumBodyDec_1_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? enum enumNameDec enumBodyDec
  public static boolean enumDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_DEC, "<enum dec>");
    r = enumDec_0(b, l + 1);
    r = r && consumeToken(b, ENUM);
    p = r; // pin = 2
    r = r && report_error_(b, enumNameDec(b, l + 1));
    r = p && enumBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean enumDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean enumNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, ENUM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT (ASSIGN (DIGIT_CONTENT | STRING_CONTENT))?
  public static boolean enumVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumVarDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_VAR_DEC, null);
    r = consumeToken(b, ID_CONTENT);
    p = r; // pin = 1
    r = r && enumVarDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ASSIGN (DIGIT_CONTENT | STRING_CONTENT))?
  private static boolean enumVarDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumVarDec_1")) return false;
    enumVarDec_1_0(b, l + 1);
    return true;
  }

  // ASSIGN (DIGIT_CONTENT | STRING_CONTENT)
  private static boolean enumVarDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumVarDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSIGN);
    r = r && enumVarDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DIGIT_CONTENT | STRING_CONTENT
  private static boolean enumVarDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumVarDec_1_0_1")) return false;
    boolean r;
    r = consumeToken(b, DIGIT_CONTENT);
    if (!r) r = consumeToken(b, STRING_CONTENT);
    return r;
  }

  /* ********************************************************** */
  // break SEMI
  public static boolean flowExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flowExpr")) return false;
    if (!nextTokenIs(b, BREAK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FLOW_EXPR, null);
    r = consumeTokens(b, 1, BREAK, SEMI);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean forBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, forBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean forBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "forBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN (forStepCondition | forInCondition) RPAREN
  static boolean forCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, forCondition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // forStepCondition | forInCondition
  private static boolean forCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forCondition_1")) return false;
    boolean r;
    r = forStepCondition(b, l + 1);
    if (!r) r = forInCondition(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // valueStat
  public static boolean forEndStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forEndStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_END_STAT, "<for end stat>");
    r = valueStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // for forCondition (forBody | allExpr)
  public static boolean forExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forExpr")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_EXPR, null);
    r = consumeToken(b, FOR);
    p = r; // pin = 1
    r = r && report_error_(b, forCondition(b, l + 1));
    r = p && forExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // forBody | allExpr
  private static boolean forExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forExpr_2")) return false;
    boolean r;
    r = forBody(b, l + 1);
    if (!r) r = allExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // forPairDec forInConditionPairDec* forInConditionInDec
  public static boolean forInCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forInCondition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_IN_CONDITION, "<for in condition>");
    r = forPairDec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, forInCondition_1(b, l + 1));
    r = p && forInConditionInDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // forInConditionPairDec*
  private static boolean forInCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forInCondition_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!forInConditionPairDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "forInCondition_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // in valueStat
  static boolean forInConditionInDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forInConditionInDec")) return false;
    if (!nextTokenIs(b, IN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, IN);
    p = r; // pin = 1
    r = r && valueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COMMA forPairDec
  static boolean forInConditionPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forInConditionPairDec")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && forPairDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (autoType | allType) varAssignNameDec
  public static boolean forPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forPairDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_PAIR_DEC, "<for pair dec>");
    r = forPairDec_0(b, l + 1);
    p = r; // pin = 1
    r = r && varAssignNameDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // autoType | allType
  private static boolean forPairDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forPairDec_0")) return false;
    boolean r;
    r = autoType(b, l + 1);
    if (!r) r = allType(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // forPairDec ASSIGN valueStat
  public static boolean forStartStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forStartStat")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_START_STAT, "<for start stat>");
    r = forPairDec(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    p = r; // pin = 2
    r = r && valueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // forStartStat forStepConditionEndStep
  public static boolean forStepCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forStepCondition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_STEP_CONDITION, "<for step condition>");
    r = forStartStat(b, l + 1);
    r = r && forStepConditionEndStep(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMMA forEndStat forStepConditionStep
  static boolean forStepConditionEndStep(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forStepConditionEndStep")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && report_error_(b, forEndStat(b, l + 1));
    r = p && forStepConditionStep(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COMMA forStepStat
  static boolean forStepConditionStep(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forStepConditionStep")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && forStepStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // valueStat
  public static boolean forStepStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forStepStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_STEP_STAT, "<for step stat>");
    r = valueStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // allType (COMMA allType)*
  public static boolean genericFunctorParamType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorParamType")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_PARAM_TYPE, "<generic functor param type>");
    r = allType(b, l + 1);
    p = r; // pin = 1
    r = r && genericFunctorParamType_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA allType)*
  private static boolean genericFunctorParamType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorParamType_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!genericFunctorParamType_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "genericFunctorParamType_1", c)) break;
    }
    return true;
  }

  // COMMA allType
  private static boolean genericFunctorParamType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorParamType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && allType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON allType (COMMA allType)*
  public static boolean genericFunctorReturnType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorReturnType")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_RETURN_TYPE, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && genericFunctorReturnType_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA allType)*
  private static boolean genericFunctorReturnType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorReturnType_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!genericFunctorReturnType_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "genericFunctorReturnType_2", c)) break;
    }
    return true;
  }

  // COMMA allType
  private static boolean genericFunctorReturnType_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorReturnType_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && allType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Functor LESS LPAREN genericFunctorParamType? RPAREN genericFunctorReturnType? GREATER
  public static boolean genericFunctorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorType")) return false;
    if (!nextTokenIs(b, FUNCTOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_TYPE, null);
    r = consumeTokens(b, 1, FUNCTOR, LESS, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, genericFunctorType_3(b, l + 1));
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    r = p && report_error_(b, genericFunctorType_5(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // genericFunctorParamType?
  private static boolean genericFunctorType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorType_3")) return false;
    genericFunctorParamType(b, l + 1);
    return true;
  }

  // genericFunctorReturnType?
  private static boolean genericFunctorType_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericFunctorType_5")) return false;
    genericFunctorReturnType(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // List LESS allType GREATER
  public static boolean genericListType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericListType")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_LIST_TYPE, null);
    r = consumeTokens(b, 1, LIST, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Map LESS allType COMMA allType GREATER
  public static boolean genericMapType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericMapType")) return false;
    if (!nextTokenIs(b, MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_MAP_TYPE, null);
    r = consumeTokens(b, 1, MAP, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COMMA)) && r;
    r = p && report_error_(b, allType(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // genericListType | genericMapType | genericFunctorType
  public static boolean genericType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_TYPE, "<generic type>");
    r = genericListType(b, l + 1);
    if (!r) r = genericMapType(b, l + 1);
    if (!r) r = genericFunctorType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // protoModifier? accessModifier? coModifier? static methodNameDec methodParamDec methodReturnDec? methodBodyDec
  public static boolean globalMethodDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalMethodDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_METHOD_DEC, "<global method dec>");
    r = globalMethodDec_0(b, l + 1);
    r = r && globalMethodDec_1(b, l + 1);
    r = r && globalMethodDec_2(b, l + 1);
    r = r && consumeToken(b, STATIC);
    p = r; // pin = 4
    r = r && report_error_(b, methodNameDec(b, l + 1));
    r = p && report_error_(b, methodParamDec(b, l + 1)) && r;
    r = p && report_error_(b, globalMethodDec_6(b, l + 1)) && r;
    r = p && methodBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // protoModifier?
  private static boolean globalMethodDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalMethodDec_0")) return false;
    protoModifier(b, l + 1);
    return true;
  }

  // accessModifier?
  private static boolean globalMethodDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalMethodDec_1")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // coModifier?
  private static boolean globalMethodDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalMethodDec_2")) return false;
    coModifier(b, l + 1);
    return true;
  }

  // methodReturnDec?
  private static boolean globalMethodDec_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalMethodDec_6")) return false;
    methodReturnDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean ifBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, ifBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean ifBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ifBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN valueStat RPAREN
  static boolean ifCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // if ifCondition (ifBody | allExpr) elseIfExpr* elseExpr?
  public static boolean ifExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_EXPR, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && report_error_(b, ifCondition(b, l + 1));
    r = p && report_error_(b, ifExpr_2(b, l + 1)) && r;
    r = p && report_error_(b, ifExpr_3(b, l + 1)) && r;
    r = p && ifExpr_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ifBody | allExpr
  private static boolean ifExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr_2")) return false;
    boolean r;
    r = ifBody(b, l + 1);
    if (!r) r = allExpr(b, l + 1);
    return r;
  }

  // elseIfExpr*
  private static boolean ifExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!elseIfExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ifExpr_3", c)) break;
    }
    return true;
  }

  // elseExpr?
  private static boolean ifExpr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr_4")) return false;
    elseExpr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // accessModifier? varAssignExpr
  public static boolean instanceDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSTANCE_DEC, "<instance dec>");
    r = instanceDec_0(b, l + 1);
    r = r && varAssignExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // accessModifier?
  private static boolean instanceDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  public static boolean methodBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, methodBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean methodBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "methodBodyDec_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean methodNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, METHOD_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN ((methodParamOneDec methodParamPairDec* methodParamTailPairDec?) | methodParamTailDec)? RPAREN
  public static boolean methodParamDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_DEC, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, methodParamDec_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((methodParamOneDec methodParamPairDec* methodParamTailPairDec?) | methodParamTailDec)?
  private static boolean methodParamDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec_1")) return false;
    methodParamDec_1_0(b, l + 1);
    return true;
  }

  // (methodParamOneDec methodParamPairDec* methodParamTailPairDec?) | methodParamTailDec
  private static boolean methodParamDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = methodParamDec_1_0_0(b, l + 1);
    if (!r) r = methodParamTailDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // methodParamOneDec methodParamPairDec* methodParamTailPairDec?
  private static boolean methodParamDec_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = methodParamOneDec(b, l + 1);
    r = r && methodParamDec_1_0_0_1(b, l + 1);
    r = r && methodParamDec_1_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // methodParamPairDec*
  private static boolean methodParamDec_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec_1_0_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!methodParamPairDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "methodParamDec_1_0_0_1", c)) break;
    }
    return true;
  }

  // methodParamTailPairDec?
  private static boolean methodParamDec_1_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamDec_1_0_0_2")) return false;
    methodParamTailPairDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean methodParamNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, METHOD_PARAM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // allType methodParamNameDec
  public static boolean methodParamOneDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamOneDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_ONE_DEC, "<method param one dec>");
    r = allType(b, l + 1);
    p = r; // pin = 1
    r = r && methodParamNameDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COMMA methodParamOneDec
  static boolean methodParamPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamPairDec")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && methodParamOneDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PARAM_TAIL
  public static boolean methodParamTailDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamTailDec")) return false;
    if (!nextTokenIs(b, PARAM_TAIL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PARAM_TAIL);
    exit_section_(b, m, METHOD_PARAM_TAIL_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // COMMA methodParamTailDec
  static boolean methodParamTailPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodParamTailPairDec")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && methodParamTailDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON allType (COMMA allType)*
  public static boolean methodReturnDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodReturnDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_RETURN_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && methodReturnDec_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA allType)*
  private static boolean methodReturnDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodReturnDec_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!methodReturnDec_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "methodReturnDec_2", c)) break;
    }
    return true;
  }

  // COMMA allType
  private static boolean methodReturnDec_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodReturnDec_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && allType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // registerModifier? namespace namespaceNameDec SEMI (globalMethodDec | classDec | enumDec | structDec | instanceDec | opAssignExpr | propertyValueExpr)*
  public static boolean namespaceDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDec")) return false;
    if (!nextTokenIs(b, "<namespace dec>", NAMESPACE, REGISTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DEC, "<namespace dec>");
    r = namespaceDec_0(b, l + 1);
    r = r && consumeToken(b, NAMESPACE);
    p = r; // pin = 2
    r = r && report_error_(b, namespaceNameDec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SEMI)) && r;
    r = p && namespaceDec_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // registerModifier?
  private static boolean namespaceDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDec_0")) return false;
    registerModifier(b, l + 1);
    return true;
  }

  // (globalMethodDec | classDec | enumDec | structDec | instanceDec | opAssignExpr | propertyValueExpr)*
  private static boolean namespaceDec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDec_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!namespaceDec_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespaceDec_4", c)) break;
    }
    return true;
  }

  // globalMethodDec | classDec | enumDec | structDec | instanceDec | opAssignExpr | propertyValueExpr
  private static boolean namespaceDec_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDec_4_0")) return false;
    boolean r;
    r = globalMethodDec(b, l + 1);
    if (!r) r = classDec(b, l + 1);
    if (!r) r = enumDec(b, l + 1);
    if (!r) r = structDec(b, l + 1);
    if (!r) r = instanceDec(b, l + 1);
    if (!r) r = opAssignExpr(b, l + 1);
    if (!r) r = propertyValueExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean namespaceNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // PLUS_PLUS | MINUS_MINUS
  public static boolean op1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op1")) return false;
    if (!nextTokenIs(b, "<op 1>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_1, "<op 1>");
    r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op1 valueStat SEMI
  public static boolean op1Expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op1Expr")) return false;
    if (!nextTokenIs(b, "<op 1 expr>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_1_EXPR, "<op 1 expr>");
    r = op1(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NOT | MINUS
  public static boolean op2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op2")) return false;
    if (!nextTokenIs(b, "<op 2>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2, "<op 2>");
    r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op2Value op2SuffixEx*
  public static boolean op2Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op2Stat")) return false;
    if (!nextTokenIs(b, "<op 2 stat>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_STAT, "<op 2 stat>");
    r = op2Value(b, l + 1);
    r = r && op2Stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op2SuffixEx*
  private static boolean op2Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op2Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op2SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op2Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix | op5Suffix | op6Suffix | op7Suffix | op8Suffix
  public static boolean op2SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op2SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_SUFFIX_EX, "<op 2 suffix ex>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op2 valueFactorStat
  public static boolean op2Value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op2Value")) return false;
    if (!nextTokenIs(b, "<op 2 value>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_VALUE, "<op 2 value>");
    r = op2(b, l + 1);
    r = r && valueFactorStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MUL | QUOTIENT | REMAINDER
  public static boolean op3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3, "<op 3>");
    r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, REMAINDER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op3Suffix op3SuffixEx*
  public static boolean op3Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_STAT, "<op 3 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op3Suffix(b, l + 1);
    r = r && op3Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op3SuffixEx*
  private static boolean op3Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op3SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op3Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3 (valueFactorStat | op2Value)
  public static boolean op3Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3Suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX, "<op 3 suffix>");
    r = op3(b, l + 1);
    r = r && op3Suffix_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op3Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix | op5Suffix | op6Suffix | op7Suffix | op8Suffix
  public static boolean op3SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op3SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX_EX, "<op 3 suffix ex>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PLUS | MINUS
  public static boolean op4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4")) return false;
    if (!nextTokenIs(b, "<op 4>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4, "<op 4>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op4Suffix op4SuffixEx*
  public static boolean op4Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_STAT, "<op 4 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op4Suffix(b, l + 1);
    r = r && op4Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op4SuffixEx*
  private static boolean op4Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op4SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op4Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op4 (valueFactorStat | op2Value) op4SuffixEe*
  public static boolean op4Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4Suffix")) return false;
    if (!nextTokenIs(b, "<op 4 suffix>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX, "<op 4 suffix>");
    r = op4(b, l + 1);
    r = r && op4Suffix_1(b, l + 1);
    r = r && op4Suffix_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op4Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  // op4SuffixEe*
  private static boolean op4Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op4SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op4Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix
  public static boolean op4SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EE, "<op 4 suffix ee>");
    r = op3Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op4Suffix | op5Suffix | op6Suffix | op7Suffix | op8Suffix
  public static boolean op4SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op4SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EX, "<op 4 suffix ex>");
    r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CONCAT
  public static boolean op5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONCAT);
    exit_section_(b, m, OP_5, r);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op5Suffix op5SuffixEx*
  public static boolean op5Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_STAT, "<op 5 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op5Suffix(b, l + 1);
    r = r && op5Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op5SuffixEx*
  private static boolean op5Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op5SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op5Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op5 (valueFactorStat | op2Value) op5SuffixEe*
  public static boolean op5Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5Suffix")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op5(b, l + 1);
    r = r && op5Suffix_1(b, l + 1);
    r = r && op5Suffix_2(b, l + 1);
    exit_section_(b, m, OP_5_SUFFIX, r);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op5Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  // op5SuffixEe*
  private static boolean op5Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op5SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op5Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix
  public static boolean op5SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EE, "<op 5 suffix ee>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op5Suffix | op6Suffix | op7Suffix | op8Suffix
  public static boolean op5SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op5SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EX, "<op 5 suffix ex>");
    r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | EQ | NOT_EQ
  public static boolean op6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6, "<op 6>");
    r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, LESS_OR_EQUAL);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_OR_EQUAL);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, NOT_EQ);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op6Suffix op6SuffixEx*
  public static boolean op6Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_STAT, "<op 6 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op6Suffix(b, l + 1);
    r = r && op6Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op6SuffixEx*
  private static boolean op6Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op6SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op6Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op6 (valueFactorStat | op2Value) op6SuffixEe*
  public static boolean op6Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6Suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX, "<op 6 suffix>");
    r = op6(b, l + 1);
    r = r && op6Suffix_1(b, l + 1);
    r = r && op6Suffix_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op6Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  // op6SuffixEe*
  private static boolean op6Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op6SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op6Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix | op5Suffix
  public static boolean op6SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EE, "<op 6 suffix ee>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op6Suffix | op7Suffix | op8Suffix
  public static boolean op6SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op6SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EX, "<op 6 suffix ex>");
    r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COND_AND
  public static boolean op7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_AND);
    exit_section_(b, m, OP_7, r);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op7Suffix op7SuffixEx*
  public static boolean op7Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_STAT, "<op 7 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op7Suffix(b, l + 1);
    r = r && op7Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op7SuffixEx*
  private static boolean op7Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op7SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op7Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op7 (valueFactorStat | op2Value) op7SuffixEe*
  public static boolean op7Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7Suffix")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op7(b, l + 1);
    r = r && op7Suffix_1(b, l + 1);
    r = r && op7Suffix_2(b, l + 1);
    exit_section_(b, m, OP_7_SUFFIX, r);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op7Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  // op7SuffixEe*
  private static boolean op7Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op7SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op7Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix | op5Suffix | op6Suffix
  public static boolean op7SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EE, "<op 7 suffix ee>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op7Suffix | op8Suffix
  public static boolean op7SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op7SuffixEx")) return false;
    if (!nextTokenIs(b, "<op 7 suffix ex>", COND_AND, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EX, "<op 7 suffix ex>");
    r = op7Suffix(b, l + 1);
    if (!r) r = op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COND_OR
  public static boolean op8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_OR);
    exit_section_(b, m, OP_8, r);
    return r;
  }

  /* ********************************************************** */
  // valueFactorStat op8Suffix op8SuffixEx*
  public static boolean op8Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_8_STAT, "<op 8 stat>");
    r = valueFactorStat(b, l + 1);
    r = r && op8Suffix(b, l + 1);
    r = r && op8Stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op8SuffixEx*
  private static boolean op8Stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8Stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op8SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op8Stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op8 (valueFactorStat | op2Value) op8SuffixEe*
  public static boolean op8Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8Suffix")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op8(b, l + 1);
    r = r && op8Suffix_1(b, l + 1);
    r = r && op8Suffix_2(b, l + 1);
    exit_section_(b, m, OP_8_SUFFIX, r);
    return r;
  }

  // valueFactorStat | op2Value
  private static boolean op8Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8Suffix_1")) return false;
    boolean r;
    r = valueFactorStat(b, l + 1);
    if (!r) r = op2Value(b, l + 1);
    return r;
  }

  // op8SuffixEe*
  private static boolean op8Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op8SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op8Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op3Suffix | op4Suffix | op5Suffix | op6Suffix | op7Suffix
  public static boolean op8SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_8_SUFFIX_EE, "<op 8 suffix ee>");
    r = op3Suffix(b, l + 1);
    if (!r) r = op4Suffix(b, l + 1);
    if (!r) r = op5Suffix(b, l + 1);
    if (!r) r = op6Suffix(b, l + 1);
    if (!r) r = op7Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op8Suffix
  public static boolean op8SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op8SuffixEx")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op8Suffix(b, l + 1);
    exit_section_(b, m, OP_8_SUFFIX_EX, r);
    return r;
  }

  /* ********************************************************** */
  // ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | QUOTIENT_ASSIGN | REMAINDER_ASSIGN
  public static boolean opAssign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssign")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_ASSIGN, "<op assign>");
    r = consumeToken(b, ASSIGN);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, MUL_ASSIGN);
    if (!r) r = consumeToken(b, QUOTIENT_ASSIGN);
    if (!r) r = consumeToken(b, REMAINDER_ASSIGN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyValue ((COMMA propertyValue)* opAssignValueStat)? SEMI
  public static boolean opAssignExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignExpr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_ASSIGN_EXPR, "<op assign expr>");
    r = propertyValue(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, opAssignExpr_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((COMMA propertyValue)* opAssignValueStat)?
  private static boolean opAssignExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignExpr_1")) return false;
    opAssignExpr_1_0(b, l + 1);
    return true;
  }

  // (COMMA propertyValue)* opAssignValueStat
  private static boolean opAssignExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opAssignExpr_1_0_0(b, l + 1);
    r = r && opAssignValueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA propertyValue)*
  private static boolean opAssignExpr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignExpr_1_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!opAssignExpr_1_0_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opAssignExpr_1_0_0", c)) break;
    }
    return true;
  }

  // COMMA propertyValue
  private static boolean opAssignExpr_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignExpr_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && propertyValue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opAssign valueStat
  static boolean opAssignValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opAssignValueStat")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = opAssign(b, l + 1);
    p = r; // pin = 1
    r = r && valueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACK (valueStat (COMMA valueStat)*)? RBRACK
  public static boolean opNewListStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewListStat")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_NEW_LIST_STAT, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, opNewListStat_1(b, l + 1));
    r = p && consumeToken(b, RBRACK) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (valueStat (COMMA valueStat)*)?
  private static boolean opNewListStat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewListStat_1")) return false;
    opNewListStat_1_0(b, l + 1);
    return true;
  }

  // valueStat (COMMA valueStat)*
  private static boolean opNewListStat_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewListStat_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && opNewListStat_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean opNewListStat_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewListStat_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!opNewListStat_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opNewListStat_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean opNewListStat_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewListStat_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // new (customType | genericType) LPAREN (valueStat (COMMA valueStat)*)? RPAREN
  public static boolean opNewStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_NEW_STAT, null);
    r = consumeToken(b, NEW);
    p = r; // pin = 1
    r = r && report_error_(b, opNewStat_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, opNewStat_3(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // customType | genericType
  private static boolean opNewStat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat_1")) return false;
    boolean r;
    r = customType(b, l + 1);
    if (!r) r = genericType(b, l + 1);
    return r;
  }

  // (valueStat (COMMA valueStat)*)?
  private static boolean opNewStat_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat_3")) return false;
    opNewStat_3_0(b, l + 1);
    return true;
  }

  // valueStat (COMMA valueStat)*
  private static boolean opNewStat_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && opNewStat_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean opNewStat_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!opNewStat_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opNewStat_3_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean opNewStat_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNewStat_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // pcall LPAREN (valueStat (COMMA valueStat)*)? RPAREN
  public static boolean pcallStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pcallStat")) return false;
    if (!nextTokenIs(b, PCALL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PCALL_STAT, null);
    r = consumeTokens(b, 1, PCALL, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, pcallStat_2(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (valueStat (COMMA valueStat)*)?
  private static boolean pcallStat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pcallStat_2")) return false;
    pcallStat_2_0(b, l + 1);
    return true;
  }

  // valueStat (COMMA valueStat)*
  private static boolean pcallStat_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pcallStat_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && pcallStat_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean pcallStat_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pcallStat_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!pcallStat_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pcallStat_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean pcallStat_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pcallStat_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // bool | double | int | I64 | any | string
  public static boolean primitiveType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMITIVE_TYPE, "<primitive type>");
    r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, I64);
    if (!r) r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyValueFirstType propertyValueSuffix*
  public static boolean propertyValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE, "<property value>");
    r = propertyValueFirstType(b, l + 1);
    r = r && propertyValue_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // propertyValueSuffix*
  private static boolean propertyValue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValue_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!propertyValueSuffix(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "propertyValue_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACK valueStat RBRACK
  public static boolean propertyValueBracketValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueBracketValue")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_BRACKET_VALUE, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, RBRACK) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // cast LESS allType GREATER LPAREN valueFactorStat RPAREN
  public static boolean propertyValueCastType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueCastType")) return false;
    if (!nextTokenIs(b, CAST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_CAST_TYPE, null);
    r = consumeTokens(b, 1, CAST, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, allType(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, GREATER, LPAREN)) && r;
    r = p && report_error_(b, valueFactorStat(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean propertyValueCustomType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueCustomType")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, PROPERTY_VALUE_CUSTOM_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // DOT propertyValueDotIdName
  public static boolean propertyValueDotId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueDotId")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_DOT_ID, null);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && propertyValueDotIdName(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean propertyValueDotIdName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueDotIdName")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, PROPERTY_VALUE_DOT_ID_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // propertyValue SEMI
  public static boolean propertyValueExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueExpr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_EXPR, "<property value expr>");
    r = propertyValue(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // propertyValueCustomType | propertyValueThisType | propertyValueCastType
  public static boolean propertyValueFirstType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueFirstType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_FIRST_TYPE, "<property value first type>");
    r = propertyValueCustomType(b, l + 1);
    if (!r) r = propertyValueThisType(b, l + 1);
    if (!r) r = propertyValueCastType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN (valueStat (COMMA valueStat)*)? RPAREN
  public static boolean propertyValueMethodCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueMethodCall")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_METHOD_CALL, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, propertyValueMethodCall_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (valueStat (COMMA valueStat)*)?
  private static boolean propertyValueMethodCall_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueMethodCall_1")) return false;
    propertyValueMethodCall_1_0(b, l + 1);
    return true;
  }

  // valueStat (COMMA valueStat)*
  private static boolean propertyValueMethodCall_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueMethodCall_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && propertyValueMethodCall_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean propertyValueMethodCall_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueMethodCall_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!propertyValueMethodCall_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "propertyValueMethodCall_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean propertyValueMethodCall_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueMethodCall_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // propertyValueDotId | propertyValueBracketValue | propertyValueMethodCall
  public static boolean propertyValueSuffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueSuffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_SUFFIX, "<property value suffix>");
    r = propertyValueDotId(b, l + 1);
    if (!r) r = propertyValueBracketValue(b, l + 1);
    if (!r) r = propertyValueMethodCall(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // this
  public static boolean propertyValueThisType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyValueThisType")) return false;
    if (!nextTokenIs(b, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, THIS);
    exit_section_(b, m, PROPERTY_VALUE_THIS_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // PROTO_OPTION protoOption
  public static boolean protoModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "protoModifier")) return false;
    if (!nextTokenIs(b, PROTO_OPTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROTO_MODIFIER, null);
    r = consumeToken(b, PROTO_OPTION);
    p = r; // pin = 1
    r = r && protoOption(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // protomsg | httpget | httppost
  static boolean protoOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "protoOption")) return false;
    boolean r;
    r = consumeToken(b, PROTOMSG);
    if (!r) r = consumeToken(b, HTTPGET);
    if (!r) r = consumeToken(b, HTTPPOST);
    return r;
  }

  /* ********************************************************** */
  // reflect LESS customType GREATER
  public static boolean reflectValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reflectValue")) return false;
    if (!nextTokenIs(b, REFLECT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REFLECT_VALUE, null);
    r = consumeTokens(b, 1, REFLECT, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, customType(b, l + 1));
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // register
  public static boolean registerModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "registerModifier")) return false;
    if (!nextTokenIs(b, REGISTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, REGISTER);
    exit_section_(b, m, REGISTER_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // return ((valueStat (COMMA valueStat)*) | returnYield)? SEMI
  public static boolean returnExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_EXPR, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, returnExpr_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((valueStat (COMMA valueStat)*) | returnYield)?
  private static boolean returnExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_1")) return false;
    returnExpr_1_0(b, l + 1);
    return true;
  }

  // (valueStat (COMMA valueStat)*) | returnYield
  private static boolean returnExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returnExpr_1_0_0(b, l + 1);
    if (!r) r = returnYield(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // valueStat (COMMA valueStat)*
  private static boolean returnExpr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = valueStat(b, l + 1);
    r = r && returnExpr_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA valueStat)*
  private static boolean returnExpr_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_1_0_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!returnExpr_1_0_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "returnExpr_1_0_0_1", c)) break;
    }
    return true;
  }

  // COMMA valueStat
  private static boolean returnExpr_1_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnExpr_1_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && valueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // yield
  public static boolean returnYield(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnYield")) return false;
    if (!nextTokenIs(b, YIELD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, YIELD);
    exit_section_(b, m, RETURN_YIELD, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE (structVarDec)* RBRACE
  static boolean structBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, structBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (structVarDec)*
  private static boolean structBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!structBodyDec_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "structBodyDec_1", c)) break;
    }
    return true;
  }

  // (structVarDec)
  private static boolean structBodyDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structBodyDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = structVarDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // accessModifier? struct structNameDec structExtendsDec? structBodyDec
  public static boolean structDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_DEC, "<struct dec>");
    r = structDec_0(b, l + 1);
    r = r && consumeToken(b, STRUCT);
    p = r; // pin = 2
    r = r && report_error_(b, structNameDec(b, l + 1));
    r = p && report_error_(b, structDec_3(b, l + 1)) && r;
    r = p && structBodyDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // accessModifier?
  private static boolean structDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structDec_0")) return false;
    accessModifier(b, l + 1);
    return true;
  }

  // structExtendsDec?
  private static boolean structDec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structDec_3")) return false;
    structExtendsDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COLON (namespaceNameDec DOT)? structNameDec
  public static boolean structExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_EXTENDS_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, structExtendsDec_1(b, l + 1));
    r = p && structNameDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (namespaceNameDec DOT)?
  private static boolean structExtendsDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structExtendsDec_1")) return false;
    structExtendsDec_1_0(b, l + 1);
    return true;
  }

  // namespaceNameDec DOT
  private static boolean structExtendsDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structExtendsDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceNameDec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean structNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // allType ID_CONTENT SEMI
  public static boolean structVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structVarDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_VAR_DEC, "<struct var dec>");
    r = allType(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeTokens(b, -1, ID_CONTENT, SEMI));
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LESS templatePairDec (COMMA templatePairDec)? GREATER
  public static boolean templateDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateDec")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_DEC, null);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, templatePairDec(b, l + 1));
    r = p && report_error_(b, templateDec_2(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA templatePairDec)?
  private static boolean templateDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateDec_2")) return false;
    templateDec_2_0(b, l + 1);
    return true;
  }

  // COMMA templatePairDec
  private static boolean templateDec_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateDec_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && templatePairDec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON allType
  static boolean templateExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && allType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT templateExtendsDec?
  public static boolean templatePairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templatePairDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_PAIR_DEC, null);
    r = consumeToken(b, ID_CONTENT);
    p = r; // pin = 1
    r = r && templatePairDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // templateExtendsDec?
  private static boolean templatePairDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templatePairDec_1")) return false;
    templateExtendsDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // wrapValueStat | constValue | propertyValue | reflectValue
  public static boolean valueFactorStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueFactorStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_FACTOR_STAT, "<value factor stat>");
    r = wrapValueStat(b, l + 1);
    if (!r) r = constValue(b, l + 1);
    if (!r) r = propertyValue(b, l + 1);
    if (!r) r = reflectValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opNewStat | op3Stat | op2Stat | op4Stat | op5Stat | op6Stat | op7Stat | op8Stat | valueFactorStat | opNewListStat | bindStat | pcallStat | methodParamTailDec
  public static boolean valueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_STAT, "<value stat>");
    r = opNewStat(b, l + 1);
    if (!r) r = op3Stat(b, l + 1);
    if (!r) r = op2Stat(b, l + 1);
    if (!r) r = op4Stat(b, l + 1);
    if (!r) r = op5Stat(b, l + 1);
    if (!r) r = op6Stat(b, l + 1);
    if (!r) r = op7Stat(b, l + 1);
    if (!r) r = op8Stat(b, l + 1);
    if (!r) r = valueFactorStat(b, l + 1);
    if (!r) r = opNewListStat(b, l + 1);
    if (!r) r = bindStat(b, l + 1);
    if (!r) r = pcallStat(b, l + 1);
    if (!r) r = methodParamTailDec(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (autoType | allType) varAssignNameDec
  public static boolean varAssignDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VAR_ASSIGN_DEC, "<var assign dec>");
    r = varAssignDec_0(b, l + 1);
    r = r && varAssignNameDec(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // autoType | allType
  private static boolean varAssignDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignDec_0")) return false;
    boolean r;
    r = autoType(b, l + 1);
    if (!r) r = allType(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // varAssignDec varAssignPairDec* varAssignValueStat? SEMI
  public static boolean varAssignExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignExpr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VAR_ASSIGN_EXPR, "<var assign expr>");
    r = varAssignDec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, varAssignExpr_1(b, l + 1));
    r = p && report_error_(b, varAssignExpr_2(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // varAssignPairDec*
  private static boolean varAssignExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!varAssignPairDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "varAssignExpr_1", c)) break;
    }
    return true;
  }

  // varAssignValueStat?
  private static boolean varAssignExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignExpr_2")) return false;
    varAssignValueStat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean varAssignNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, VAR_ASSIGN_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // COMMA varAssignDec
  static boolean varAssignPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignPairDec")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && varAssignDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ASSIGN valueStat
  static boolean varAssignValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varAssignValueStat")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && valueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  static boolean whileBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, whileBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean whileBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "whileBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN valueStat RPAREN
  static boolean whileCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, valueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // while whileCondition (whileBody | allExpr)
  public static boolean whileExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileExpr")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_EXPR, null);
    r = consumeToken(b, WHILE);
    p = r; // pin = 1
    r = r && report_error_(b, whileCondition(b, l + 1));
    r = p && whileExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // whileBody | allExpr
  private static boolean whileExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileExpr_2")) return false;
    boolean r;
    r = whileBody(b, l + 1);
    if (!r) r = allExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LBRACE allExpr* RBRACE
  public static boolean wrapExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wrapExpr")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRAP_EXPR, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, wrapExpr_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // allExpr*
  private static boolean wrapExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wrapExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!allExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "wrapExpr_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN valueStat RPAREN
  public static boolean wrapValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wrapValueStat")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && valueStat(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, WRAP_VALUE_STAT, r);
    return r;
  }

}
