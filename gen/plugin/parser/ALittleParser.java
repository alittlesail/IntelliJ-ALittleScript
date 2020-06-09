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
  public static boolean AccessModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AccessModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACCESS_MODIFIER, "<access modifier>");
    r = consumeToken(b, PUBLIC);
    if (!r) r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, PROTECTED);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Modifier* (IfExpr |
  //             ForExpr |
  //             WhileExpr |
  //             DoWhileExpr |
  //             ReturnExpr |
  //             FlowExpr |
  //             ThrowExpr |
  //             AssertExpr |
  //             WrapExpr |
  //             Op1Expr |
  //             EmptyExpr |
  //             VarAssignExpr |
  //             OpAssignExpr)
  public static boolean AllExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_EXPR, "<all expr>");
    r = AllExpr_0(b, l + 1);
    r = r && AllExpr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Modifier*
  private static boolean AllExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllExpr_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AllExpr_0", c)) break;
    }
    return true;
  }

  // IfExpr |
  //             ForExpr |
  //             WhileExpr |
  //             DoWhileExpr |
  //             ReturnExpr |
  //             FlowExpr |
  //             ThrowExpr |
  //             AssertExpr |
  //             WrapExpr |
  //             Op1Expr |
  //             EmptyExpr |
  //             VarAssignExpr |
  //             OpAssignExpr
  private static boolean AllExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllExpr_1")) return false;
    boolean r;
    r = IfExpr(b, l + 1);
    if (!r) r = ForExpr(b, l + 1);
    if (!r) r = WhileExpr(b, l + 1);
    if (!r) r = DoWhileExpr(b, l + 1);
    if (!r) r = ReturnExpr(b, l + 1);
    if (!r) r = FlowExpr(b, l + 1);
    if (!r) r = ThrowExpr(b, l + 1);
    if (!r) r = AssertExpr(b, l + 1);
    if (!r) r = WrapExpr(b, l + 1);
    if (!r) r = Op1Expr(b, l + 1);
    if (!r) r = EmptyExpr(b, l + 1);
    if (!r) r = VarAssignExpr(b, l + 1);
    if (!r) r = OpAssignExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // AllTypeConst? (PrimitiveType | GenericType | CustomType)
  public static boolean AllType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_TYPE, "<all type>");
    r = AllType_0(b, l + 1);
    r = r && AllType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AllTypeConst?
  private static boolean AllType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllType_0")) return false;
    AllTypeConst(b, l + 1);
    return true;
  }

  // PrimitiveType | GenericType | CustomType
  private static boolean AllType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllType_1")) return false;
    boolean r;
    r = PrimitiveType(b, l + 1);
    if (!r) r = GenericType(b, l + 1);
    if (!r) r = CustomType(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // const
  public static boolean AllTypeConst(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllTypeConst")) return false;
    if (!nextTokenIs(b, CONST)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST);
    exit_section_(b, m, ALL_TYPE_CONST, r);
    return r;
  }

  /* ********************************************************** */
  // assert '(' (ValueStat AssertExprValueStat_*)? ')' ';'
  public static boolean AssertExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssertExpr")) return false;
    if (!nextTokenIs(b, ASSERT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSERT_EXPR, null);
    r = consumeTokens(b, 1, ASSERT, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, AssertExpr_2(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, RPAREN, SEMI)) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ValueStat AssertExprValueStat_*)?
  private static boolean AssertExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssertExpr_2")) return false;
    AssertExpr_2_0(b, l + 1);
    return true;
  }

  // ValueStat AssertExprValueStat_*
  private static boolean AssertExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssertExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && AssertExpr_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AssertExprValueStat_*
  private static boolean AssertExpr_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssertExpr_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AssertExprValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AssertExpr_2_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean AssertExprValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssertExprValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '[' (NativeModifier | LanguageModifier | ConstModifier | NullableModifier | ProtocolModifier | CommandModifier)? ']'
  public static boolean AttributeModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeModifier")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_MODIFIER, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, AttributeModifier_1(b, l + 1));
    r = p && consumeToken(b, RBRACK) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (NativeModifier | LanguageModifier | ConstModifier | NullableModifier | ProtocolModifier | CommandModifier)?
  private static boolean AttributeModifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeModifier_1")) return false;
    AttributeModifier_1_0(b, l + 1);
    return true;
  }

  // NativeModifier | LanguageModifier | ConstModifier | NullableModifier | ProtocolModifier | CommandModifier
  private static boolean AttributeModifier_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeModifier_1_0")) return false;
    boolean r;
    r = NativeModifier(b, l + 1);
    if (!r) r = LanguageModifier(b, l + 1);
    if (!r) r = ConstModifier(b, l + 1);
    if (!r) r = NullableModifier(b, l + 1);
    if (!r) r = ProtocolModifier(b, l + 1);
    if (!r) r = CommandModifier(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // bind '(' (ValueStat BindStatValueStat_*)? ')'
  public static boolean BindStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BindStat")) return false;
    if (!nextTokenIs(b, BIND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BIND_STAT, null);
    r = consumeTokens(b, 1, BIND, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, BindStat_2(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ValueStat BindStatValueStat_*)?
  private static boolean BindStat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BindStat_2")) return false;
    BindStat_2_0(b, l + 1);
    return true;
  }

  // ValueStat BindStatValueStat_*
  private static boolean BindStat_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BindStat_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && BindStat_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BindStatValueStat_*
  private static boolean BindStat_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BindStat_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!BindStatValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "BindStat_2_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean BindStatValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BindStatValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' ClassElementDec* '}'
  public static boolean ClassBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, ClassBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ClassElementDec*
  private static boolean ClassBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ClassElementDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ClassBodyDec_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ctor (MethodParamDec MethodBodyDec?)?
  public static boolean ClassCtorDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassCtorDec")) return false;
    if (!nextTokenIs(b, CTOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_CTOR_DEC, null);
    r = consumeToken(b, CTOR);
    p = r; // pin = 1
    r = r && ClassCtorDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodParamDec MethodBodyDec?)?
  private static boolean ClassCtorDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassCtorDec_1")) return false;
    ClassCtorDec_1_0(b, l + 1);
    return true;
  }

  // MethodParamDec MethodBodyDec?
  private static boolean ClassCtorDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassCtorDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodParamDec(b, l + 1);
    r = r && ClassCtorDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodBodyDec?
  private static boolean ClassCtorDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassCtorDec_1_0_1")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // class (ClassNameDec TemplateDec? ClassExtendsDec? ClassBodyDec?)?
  public static boolean ClassDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_DEC, null);
    r = consumeToken(b, CLASS);
    p = r; // pin = 1
    r = r && ClassDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ClassNameDec TemplateDec? ClassExtendsDec? ClassBodyDec?)?
  private static boolean ClassDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec_1")) return false;
    ClassDec_1_0(b, l + 1);
    return true;
  }

  // ClassNameDec TemplateDec? ClassExtendsDec? ClassBodyDec?
  private static boolean ClassDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ClassNameDec(b, l + 1);
    r = r && ClassDec_1_0_1(b, l + 1);
    r = r && ClassDec_1_0_2(b, l + 1);
    r = r && ClassDec_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TemplateDec?
  private static boolean ClassDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec_1_0_1")) return false;
    TemplateDec(b, l + 1);
    return true;
  }

  // ClassExtendsDec?
  private static boolean ClassDec_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec_1_0_2")) return false;
    ClassExtendsDec(b, l + 1);
    return true;
  }

  // ClassBodyDec?
  private static boolean ClassDec_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassDec_1_0_3")) return false;
    ClassBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Modifier* (ClassCtorDec | ClassGetterDec | ClassSetterDec | ClassStaticDec | ClassMethodDec | ClassVarDec)
  public static boolean ClassElementDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassElementDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_ELEMENT_DEC, "<class element dec>");
    r = ClassElementDec_0(b, l + 1);
    r = r && ClassElementDec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Modifier*
  private static boolean ClassElementDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassElementDec_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ClassElementDec_0", c)) break;
    }
    return true;
  }

  // ClassCtorDec | ClassGetterDec | ClassSetterDec | ClassStaticDec | ClassMethodDec | ClassVarDec
  private static boolean ClassElementDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassElementDec_1")) return false;
    boolean r;
    r = ClassCtorDec(b, l + 1);
    if (!r) r = ClassGetterDec(b, l + 1);
    if (!r) r = ClassSetterDec(b, l + 1);
    if (!r) r = ClassStaticDec(b, l + 1);
    if (!r) r = ClassMethodDec(b, l + 1);
    if (!r) r = ClassVarDec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ':' (NamespaceNameDec '.')? ClassNameDec
  public static boolean ClassExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_EXTENDS_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, ClassExtendsDec_1(b, l + 1));
    r = p && ClassNameDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (NamespaceNameDec '.')?
  private static boolean ClassExtendsDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassExtendsDec_1")) return false;
    ClassExtendsDec_1_0(b, l + 1);
    return true;
  }

  // NamespaceNameDec '.'
  private static boolean ClassExtendsDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassExtendsDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = NamespaceNameDec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // get (MethodNameDec (MethodGetterParamDec MethodBodyDec_?)?)?
  public static boolean ClassGetterDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec")) return false;
    if (!nextTokenIs(b, GET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_GETTER_DEC, null);
    r = consumeToken(b, GET);
    p = r; // pin = 1
    r = r && ClassGetterDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodNameDec (MethodGetterParamDec MethodBodyDec_?)?)?
  private static boolean ClassGetterDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec_1")) return false;
    ClassGetterDec_1_0(b, l + 1);
    return true;
  }

  // MethodNameDec (MethodGetterParamDec MethodBodyDec_?)?
  private static boolean ClassGetterDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodNameDec(b, l + 1);
    r = r && ClassGetterDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (MethodGetterParamDec MethodBodyDec_?)?
  private static boolean ClassGetterDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec_1_0_1")) return false;
    ClassGetterDec_1_0_1_0(b, l + 1);
    return true;
  }

  // MethodGetterParamDec MethodBodyDec_?
  private static boolean ClassGetterDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodGetterParamDec(b, l + 1);
    r = r && ClassGetterDec_1_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodBodyDec_?
  private static boolean ClassGetterDec_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassGetterDec_1_0_1_0_1")) return false;
    MethodBodyDec_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // fun (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  public static boolean ClassMethodDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec")) return false;
    if (!nextTokenIs(b, FUN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_METHOD_DEC, null);
    r = consumeToken(b, FUN);
    p = r; // pin = 1
    r = r && ClassMethodDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  private static boolean ClassMethodDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1")) return false;
    ClassMethodDec_1_0(b, l + 1);
    return true;
  }

  // MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean ClassMethodDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodNameDec(b, l + 1);
    r = r && ClassMethodDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean ClassMethodDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0_1")) return false;
    ClassMethodDec_1_0_1_0(b, l + 1);
    return true;
  }

  // TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?
  private static boolean ClassMethodDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ClassMethodDec_1_0_1_0_0(b, l + 1);
    r = r && MethodParamDec(b, l + 1);
    r = r && ClassMethodDec_1_0_1_0_2(b, l + 1);
    r = r && ClassMethodDec_1_0_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TemplateDec?
  private static boolean ClassMethodDec_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0_1_0_0")) return false;
    TemplateDec(b, l + 1);
    return true;
  }

  // MethodReturnDec?
  private static boolean ClassMethodDec_1_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0_1_0_2")) return false;
    MethodReturnDec(b, l + 1);
    return true;
  }

  // MethodBodyDec?
  private static boolean ClassMethodDec_1_0_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassMethodDec_1_0_1_0_3")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT | 'Map'
  public static boolean ClassNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassNameDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_NAME_DEC, "<class name dec>");
    r = consumeToken(b, ID_CONTENT);
    if (!r) r = consumeToken(b, "Map");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // set (MethodNameDec (MethodSetterParamDec MethodBodyDec?)?)?
  public static boolean ClassSetterDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec")) return false;
    if (!nextTokenIs(b, SET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_SETTER_DEC, null);
    r = consumeToken(b, SET);
    p = r; // pin = 1
    r = r && ClassSetterDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodNameDec (MethodSetterParamDec MethodBodyDec?)?)?
  private static boolean ClassSetterDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec_1")) return false;
    ClassSetterDec_1_0(b, l + 1);
    return true;
  }

  // MethodNameDec (MethodSetterParamDec MethodBodyDec?)?
  private static boolean ClassSetterDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodNameDec(b, l + 1);
    r = r && ClassSetterDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (MethodSetterParamDec MethodBodyDec?)?
  private static boolean ClassSetterDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec_1_0_1")) return false;
    ClassSetterDec_1_0_1_0(b, l + 1);
    return true;
  }

  // MethodSetterParamDec MethodBodyDec?
  private static boolean ClassSetterDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodSetterParamDec(b, l + 1);
    r = r && ClassSetterDec_1_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodBodyDec?
  private static boolean ClassSetterDec_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassSetterDec_1_0_1_0_1")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // static (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  public static boolean ClassStaticDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec")) return false;
    if (!nextTokenIs(b, STATIC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_STATIC_DEC, null);
    r = consumeToken(b, STATIC);
    p = r; // pin = 1
    r = r && ClassStaticDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  private static boolean ClassStaticDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1")) return false;
    ClassStaticDec_1_0(b, l + 1);
    return true;
  }

  // MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean ClassStaticDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodNameDec(b, l + 1);
    r = r && ClassStaticDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean ClassStaticDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0_1")) return false;
    ClassStaticDec_1_0_1_0(b, l + 1);
    return true;
  }

  // TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?
  private static boolean ClassStaticDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ClassStaticDec_1_0_1_0_0(b, l + 1);
    r = r && MethodParamDec(b, l + 1);
    r = r && ClassStaticDec_1_0_1_0_2(b, l + 1);
    r = r && ClassStaticDec_1_0_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TemplateDec?
  private static boolean ClassStaticDec_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0_1_0_0")) return false;
    TemplateDec(b, l + 1);
    return true;
  }

  // MethodReturnDec?
  private static boolean ClassStaticDec_1_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0_1_0_2")) return false;
    MethodReturnDec(b, l + 1);
    return true;
  }

  // MethodBodyDec?
  private static boolean ClassStaticDec_1_0_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassStaticDec_1_0_1_0_3")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // AllType ClassVarNameDec ClassVarValueDec? ';'
  public static boolean ClassVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassVarDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_VAR_DEC, "<class var dec>");
    r = AllType(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, ClassVarNameDec(b, l + 1));
    r = p && report_error_(b, ClassVarDec_2(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ClassVarValueDec?
  private static boolean ClassVarDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassVarDec_2")) return false;
    ClassVarValueDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean ClassVarNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassVarNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // '=' (ConstValue | OpNewStat)
  public static boolean ClassVarValueDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassVarValueDec")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_VAR_VALUE_DEC, null);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && ClassVarValueDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ConstValue | OpNewStat
  private static boolean ClassVarValueDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ClassVarValueDec_1")) return false;
    boolean r;
    r = ConstValue(b, l + 1);
    if (!r) r = OpNewStat(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '(' TEXT_CONTENT ')'
  public static boolean CommandBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommandBodyDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_BODY_DEC, null);
    r = consumeTokens(b, 1, LPAREN, TEXT_CONTENT, RPAREN);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Cmd CommandBodyDec?
  public static boolean CommandModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommandModifier")) return false;
    if (!nextTokenIs(b, CMD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_MODIFIER, null);
    r = consumeToken(b, CMD);
    p = r; // pin = 1
    r = r && CommandModifier_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // CommandBodyDec?
  private static boolean CommandModifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommandModifier_1")) return false;
    CommandBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Constant
  public static boolean ConstModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstModifier")) return false;
    if (!nextTokenIs(b, CONSTANT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONSTANT);
    exit_section_(b, m, CONST_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // true | false | null | TEXT_CONTENT | NUMBER_CONTENT
  public static boolean ConstValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONST_VALUE, "<const value>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, TEXT_CONTENT);
    if (!r) r = consumeToken(b, NUMBER_CONTENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // async | await
  public static boolean CoroutineModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CoroutineModifier")) return false;
    if (!nextTokenIs(b, "<coroutine modifier>", ASYNC, AWAIT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COROUTINE_MODIFIER, "<coroutine modifier>");
    r = consumeToken(b, ASYNC);
    if (!r) r = consumeToken(b, AWAIT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // co
  public static boolean CoroutineStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CoroutineStat")) return false;
    if (!nextTokenIs(b, CO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CO);
    exit_section_(b, m, COROUTINE_STAT, r);
    return r;
  }

  /* ********************************************************** */
  // CustomTypeName CustomTypeDotId? CustomTypeTemplate?
  public static boolean CustomType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomType")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = CustomTypeName(b, l + 1);
    r = r && CustomType_1(b, l + 1);
    r = r && CustomType_2(b, l + 1);
    exit_section_(b, m, CUSTOM_TYPE, r);
    return r;
  }

  // CustomTypeDotId?
  private static boolean CustomType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomType_1")) return false;
    CustomTypeDotId(b, l + 1);
    return true;
  }

  // CustomTypeTemplate?
  private static boolean CustomType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomType_2")) return false;
    CustomTypeTemplate(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '.' CustomTypeDotIdName
  public static boolean CustomTypeDotId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeDotId")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_TYPE_DOT_ID, null);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && CustomTypeDotIdName(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT | 'Map' | 'async'
  public static boolean CustomTypeDotIdName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeDotIdName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_TYPE_DOT_ID_NAME, "<custom type dot id name>");
    r = consumeToken(b, ID_CONTENT);
    if (!r) r = consumeToken(b, "Map");
    if (!r) r = consumeToken(b, "async");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean CustomTypeName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeName")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CUSTOM_TYPE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // '<' AllType (',' AllType)* '>'
  public static boolean CustomTypeTemplate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeTemplate")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_TYPE_TEMPLATE, null);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, AllType(b, l + 1));
    r = p && report_error_(b, CustomTypeTemplate_2(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' AllType)*
  private static boolean CustomTypeTemplate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeTemplate_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!CustomTypeTemplate_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "CustomTypeTemplate_2", c)) break;
    }
    return true;
  }

  // ',' AllType
  private static boolean CustomTypeTemplate_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomTypeTemplate_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AllType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean DoWhileBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoWhileBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_WHILE_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, DoWhileBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean DoWhileBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoWhileBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "DoWhileBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' ValueStat? ')'
  public static boolean DoWhileCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoWhileCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_WHILE_CONDITION, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, DoWhileCondition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueStat?
  private static boolean DoWhileCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoWhileCondition_1")) return false;
    ValueStat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // do DoWhileBody while DoWhileCondition ';'
  public static boolean DoWhileExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoWhileExpr")) return false;
    if (!nextTokenIs(b, DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_WHILE_EXPR, null);
    r = consumeToken(b, DO);
    p = r; // pin = 1
    r = r && report_error_(b, DoWhileBody(b, l + 1));
    r = p && report_error_(b, consumeToken(b, WHILE)) && r;
    r = p && report_error_(b, DoWhileCondition(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean ElseBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, ElseBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean ElseBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ElseBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // else (ElseBody | AllExpr)
  public static boolean ElseExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseExpr")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_EXPR, null);
    r = consumeToken(b, ELSE);
    p = r; // pin = 1
    r = r && ElseExpr_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ElseBody | AllExpr
  private static boolean ElseExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseExpr_1")) return false;
    boolean r;
    r = ElseBody(b, l + 1);
    if (!r) r = AllExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean ElseIfBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, ElseIfBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean ElseIfBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ElseIfBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' ValueStat? ')'
  public static boolean ElseIfCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF_CONDITION, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ElseIfCondition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueStat?
  private static boolean ElseIfCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfCondition_1")) return false;
    ValueStat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // elseif ElseIfCondition (ElseIfBody | AllExpr)
  public static boolean ElseIfExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfExpr")) return false;
    if (!nextTokenIs(b, ELSEIF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF_EXPR, null);
    r = consumeToken(b, ELSEIF);
    p = r; // pin = 1
    r = r && report_error_(b, ElseIfCondition(b, l + 1));
    r = p && ElseIfExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ElseIfBody | AllExpr
  private static boolean ElseIfExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseIfExpr_2")) return false;
    boolean r;
    r = ElseIfBody(b, l + 1);
    if (!r) r = AllExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean EmptyExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EmptyExpr")) return false;
    if (!nextTokenIs(b, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    exit_section_(b, m, EMPTY_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // '{' EnumVarDec* '}'
  public static boolean EnumBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, EnumBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // EnumVarDec*
  private static boolean EnumBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!EnumVarDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "EnumBodyDec_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // enum (EnumNameDec EnumBodyDec?)?
  public static boolean EnumDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumDec")) return false;
    if (!nextTokenIs(b, ENUM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_DEC, null);
    r = consumeToken(b, ENUM);
    p = r; // pin = 1
    r = r && EnumDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (EnumNameDec EnumBodyDec?)?
  private static boolean EnumDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumDec_1")) return false;
    EnumDec_1_0(b, l + 1);
    return true;
  }

  // EnumNameDec EnumBodyDec?
  private static boolean EnumDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = EnumNameDec(b, l + 1);
    r = r && EnumDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EnumBodyDec?
  private static boolean EnumDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumDec_1_0_1")) return false;
    EnumBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean EnumNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, ENUM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // EnumVarNameDec EnumVarDecValue_? ';'
  public static boolean EnumVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumVarDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_VAR_DEC, null);
    r = EnumVarNameDec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, EnumVarDec_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // EnumVarDecValue_?
  private static boolean EnumVarDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumVarDec_1")) return false;
    EnumVarDecValue_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '=' (NUMBER_CONTENT | TEXT_CONTENT)
  static boolean EnumVarDecValue_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumVarDecValue_")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && EnumVarDecValue__1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // NUMBER_CONTENT | TEXT_CONTENT
  private static boolean EnumVarDecValue__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumVarDecValue__1")) return false;
    boolean r;
    r = consumeToken(b, NUMBER_CONTENT);
    if (!r) r = consumeToken(b, TEXT_CONTENT);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean EnumVarNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnumVarNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, ENUM_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // (break | continue) ';'
  public static boolean FlowExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowExpr")) return false;
    if (!nextTokenIs(b, "<flow expr>", BREAK, CONTINUE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FLOW_EXPR, "<flow expr>");
    r = FlowExpr_0(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // break | continue
  private static boolean FlowExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowExpr_0")) return false;
    boolean r;
    r = consumeToken(b, BREAK);
    if (!r) r = consumeToken(b, CONTINUE);
    return r;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean ForBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, ForBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean ForBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ForBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' var ForPairDec (ForStepCondition | ForInCondition) ')'
  public static boolean ForCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_CONDITION, null);
    r = consumeTokens(b, 1, LPAREN, VAR);
    p = r; // pin = 1
    r = r && report_error_(b, ForPairDec(b, l + 1));
    r = p && report_error_(b, ForCondition_3(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ForStepCondition | ForInCondition
  private static boolean ForCondition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForCondition_3")) return false;
    boolean r;
    r = ForStepCondition(b, l + 1);
    if (!r) r = ForInCondition(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ValueStat
  public static boolean ForEndStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForEndStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_END_STAT, "<for end stat>");
    r = ValueStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // for ForCondition (ForBody | AllExpr)
  public static boolean ForExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForExpr")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_EXPR, null);
    r = consumeToken(b, FOR);
    p = r; // pin = 1
    r = r && report_error_(b, ForCondition(b, l + 1));
    r = p && ForExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ForBody | AllExpr
  private static boolean ForExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForExpr_2")) return false;
    boolean r;
    r = ForBody(b, l + 1);
    if (!r) r = AllExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ForPairDec_* in ValueStat
  public static boolean ForInCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForInCondition")) return false;
    if (!nextTokenIs(b, "<for in condition>", COMMA, IN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_IN_CONDITION, "<for in condition>");
    r = ForInCondition_0(b, l + 1);
    r = r && consumeToken(b, IN);
    p = r; // pin = 2
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ForPairDec_*
  private static boolean ForInCondition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForInCondition_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ForPairDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ForInCondition_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // VarAssignNameDec ForPairDecAllType_?
  public static boolean ForPairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForPairDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarAssignNameDec(b, l + 1);
    r = r && ForPairDec_1(b, l + 1);
    exit_section_(b, m, FOR_PAIR_DEC, r);
    return r;
  }

  // ForPairDecAllType_?
  private static boolean ForPairDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForPairDec_1")) return false;
    ForPairDecAllType_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ':' AllType
  static boolean ForPairDecAllType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForPairDecAllType_")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && AllType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ',' ForPairDec
  static boolean ForPairDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForPairDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ForPairDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '=' ValueStat
  public static boolean ForStartStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStartStat")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_START_STAT, null);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ForStartStat ';' ForEndStat ';' ForStepStat
  public static boolean ForStepCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStepCondition")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_STEP_CONDITION, null);
    r = ForStartStat(b, l + 1);
    r = r && consumeToken(b, SEMI);
    p = r; // pin = 2
    r = r && report_error_(b, ForEndStat(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SEMI)) && r;
    r = p && ForStepStat(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ValueStat
  public static boolean ForStepStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStepStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_STEP_STAT, "<for step stat>");
    r = ValueStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GenericFunctorParamTail | AllType
  public static boolean GenericFunctorParamOneType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorParamOneType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_PARAM_ONE_TYPE, "<generic functor param one type>");
    r = GenericFunctorParamTail(b, l + 1);
    if (!r) r = AllType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ',' GenericFunctorParamOneType
  static boolean GenericFunctorParamOneType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorParamOneType_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && GenericFunctorParamOneType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '...'
  public static boolean GenericFunctorParamTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorParamTail")) return false;
    if (!nextTokenIs(b, TYPE_TAIL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_TAIL);
    exit_section_(b, m, GENERIC_FUNCTOR_PARAM_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // GenericFunctorParamOneType GenericFunctorParamOneType_*
  public static boolean GenericFunctorParamType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorParamType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_PARAM_TYPE, "<generic functor param type>");
    r = GenericFunctorParamOneType(b, l + 1);
    r = r && GenericFunctorParamType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // GenericFunctorParamOneType_*
  private static boolean GenericFunctorParamType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorParamType_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GenericFunctorParamOneType_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "GenericFunctorParamType_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // GenericFunctorReturnTail | AllType
  public static boolean GenericFunctorReturnOneType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorReturnOneType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_RETURN_ONE_TYPE, "<generic functor return one type>");
    r = GenericFunctorReturnTail(b, l + 1);
    if (!r) r = AllType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ',' GenericFunctorReturnOneType
  static boolean GenericFunctorReturnOneType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorReturnOneType_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && GenericFunctorReturnOneType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '...'
  public static boolean GenericFunctorReturnTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorReturnTail")) return false;
    if (!nextTokenIs(b, TYPE_TAIL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_TAIL);
    exit_section_(b, m, GENERIC_FUNCTOR_RETURN_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // ':' GenericFunctorReturnOneType GenericFunctorReturnOneType_*
  public static boolean GenericFunctorReturnType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorReturnType")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_RETURN_TYPE, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, GenericFunctorReturnOneType(b, l + 1));
    r = p && GenericFunctorReturnType_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // GenericFunctorReturnOneType_*
  private static boolean GenericFunctorReturnType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorReturnType_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GenericFunctorReturnOneType_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "GenericFunctorReturnType_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Functor '<' AllTypeConst? CoroutineModifier? '(' GenericFunctorParamType? ')' GenericFunctorReturnType? '>'
  public static boolean GenericFunctorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorType")) return false;
    if (!nextTokenIs(b, FUNCTOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_TYPE, null);
    r = consumeTokens(b, 1, FUNCTOR, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, GenericFunctorType_2(b, l + 1));
    r = p && report_error_(b, GenericFunctorType_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, GenericFunctorType_5(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    r = p && report_error_(b, GenericFunctorType_7(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllTypeConst?
  private static boolean GenericFunctorType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorType_2")) return false;
    AllTypeConst(b, l + 1);
    return true;
  }

  // CoroutineModifier?
  private static boolean GenericFunctorType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorType_3")) return false;
    CoroutineModifier(b, l + 1);
    return true;
  }

  // GenericFunctorParamType?
  private static boolean GenericFunctorType_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorType_5")) return false;
    GenericFunctorParamType(b, l + 1);
    return true;
  }

  // GenericFunctorReturnType?
  private static boolean GenericFunctorType_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericFunctorType_7")) return false;
    GenericFunctorReturnType(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // List '<' AllType '>'
  public static boolean GenericListType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericListType")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_LIST_TYPE, null);
    r = consumeTokens(b, 1, LIST, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, AllType(b, l + 1));
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Map '<' AllType GenericMapTypeAllType_ '>'
  public static boolean GenericMapType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericMapType")) return false;
    if (!nextTokenIs(b, MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_MAP_TYPE, null);
    r = consumeTokens(b, 1, MAP, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, AllType(b, l + 1));
    r = p && report_error_(b, GenericMapTypeAllType_(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ',' AllType
  static boolean GenericMapTypeAllType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericMapTypeAllType_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && AllType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // GenericListType | GenericMapType | GenericFunctorType
  public static boolean GenericType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GenericType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_TYPE, "<generic type>");
    r = GenericListType(b, l + 1);
    if (!r) r = GenericMapType(b, l + 1);
    if (!r) r = GenericFunctorType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // static (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  public static boolean GlobalMethodDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec")) return false;
    if (!nextTokenIs(b, STATIC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_METHOD_DEC, null);
    r = consumeToken(b, STATIC);
    p = r; // pin = 1
    r = r && GlobalMethodDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?)?
  private static boolean GlobalMethodDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1")) return false;
    GlobalMethodDec_1_0(b, l + 1);
    return true;
  }

  // MethodNameDec (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean GlobalMethodDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodNameDec(b, l + 1);
    r = r && GlobalMethodDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?)?
  private static boolean GlobalMethodDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0_1")) return false;
    GlobalMethodDec_1_0_1_0(b, l + 1);
    return true;
  }

  // TemplateDec? MethodParamDec MethodReturnDec? MethodBodyDec?
  private static boolean GlobalMethodDec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GlobalMethodDec_1_0_1_0_0(b, l + 1);
    r = r && MethodParamDec(b, l + 1);
    r = r && GlobalMethodDec_1_0_1_0_2(b, l + 1);
    r = r && GlobalMethodDec_1_0_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TemplateDec?
  private static boolean GlobalMethodDec_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0_1_0_0")) return false;
    TemplateDec(b, l + 1);
    return true;
  }

  // MethodReturnDec?
  private static boolean GlobalMethodDec_1_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0_1_0_2")) return false;
    MethodReturnDec(b, l + 1);
    return true;
  }

  // MethodBodyDec?
  private static boolean GlobalMethodDec_1_0_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GlobalMethodDec_1_0_1_0_3")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean IfBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, IfBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean IfBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "IfBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' ValueStat? ')'
  public static boolean IfCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_CONDITION, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, IfCondition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueStat?
  private static boolean IfCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfCondition_1")) return false;
    ValueStat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // if IfCondition (IfBody | AllExpr) ElseIfExpr* ElseExpr?
  public static boolean IfExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfExpr")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_EXPR, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && report_error_(b, IfCondition(b, l + 1));
    r = p && report_error_(b, IfExpr_2(b, l + 1)) && r;
    r = p && report_error_(b, IfExpr_3(b, l + 1)) && r;
    r = p && IfExpr_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // IfBody | AllExpr
  private static boolean IfExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfExpr_2")) return false;
    boolean r;
    r = IfBody(b, l + 1);
    if (!r) r = AllExpr(b, l + 1);
    return r;
  }

  // ElseIfExpr*
  private static boolean IfExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfExpr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ElseIfExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "IfExpr_3", c)) break;
    }
    return true;
  }

  // ElseExpr?
  private static boolean IfExpr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfExpr_4")) return false;
    ElseExpr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // VarAssignExpr
  public static boolean InstanceDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InstanceDec")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarAssignExpr(b, l + 1);
    exit_section_(b, m, INSTANCE_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (LanguageNameDec LanguageNameDec_*)? ')'
  public static boolean LanguageBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageBodyDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LANGUAGE_BODY_DEC, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, LanguageBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (LanguageNameDec LanguageNameDec_*)?
  private static boolean LanguageBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageBodyDec_1")) return false;
    LanguageBodyDec_1_0(b, l + 1);
    return true;
  }

  // LanguageNameDec LanguageNameDec_*
  private static boolean LanguageBodyDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageBodyDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = LanguageNameDec(b, l + 1);
    r = r && LanguageBodyDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LanguageNameDec_*
  private static boolean LanguageBodyDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageBodyDec_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!LanguageNameDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LanguageBodyDec_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Language LanguageBodyDec?
  public static boolean LanguageModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageModifier")) return false;
    if (!nextTokenIs(b, LANGUAGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LANGUAGE_MODIFIER, null);
    r = consumeToken(b, LANGUAGE);
    p = r; // pin = 1
    r = r && LanguageModifier_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // LanguageBodyDec?
  private static boolean LanguageModifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageModifier_1")) return false;
    LanguageBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean LanguageNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, LANGUAGE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ',' LanguageNameDec
  static boolean LanguageNameDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LanguageNameDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && LanguageNameDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean MethodBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, MethodBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean MethodBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MethodBodyDec_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ':' (AllType MethodBodyDec?)?
  static boolean MethodBodyDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec_")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && MethodBodyDec__1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (AllType MethodBodyDec?)?
  private static boolean MethodBodyDec__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec__1")) return false;
    MethodBodyDec__1_0(b, l + 1);
    return true;
  }

  // AllType MethodBodyDec?
  private static boolean MethodBodyDec__1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec__1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AllType(b, l + 1);
    r = r && MethodBodyDec__1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodBodyDec?
  private static boolean MethodBodyDec__1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodBodyDec__1_0_1")) return false;
    MethodBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' ')'
  public static boolean MethodGetterParamDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodGetterParamDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_GETTER_PARAM_DEC, null);
    r = consumeTokens(b, 1, LPAREN, RPAREN);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT | 'async'
  public static boolean MethodNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodNameDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_NAME_DEC, "<method name dec>");
    r = consumeToken(b, ID_CONTENT);
    if (!r) r = consumeToken(b, "async");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' (MethodParamOneDec MethodParamOneDec_*)? ')'
  public static boolean MethodParamDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_DEC, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, MethodParamDec_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodParamOneDec MethodParamOneDec_*)?
  private static boolean MethodParamDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamDec_1")) return false;
    MethodParamDec_1_0(b, l + 1);
    return true;
  }

  // MethodParamOneDec MethodParamOneDec_*
  private static boolean MethodParamDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodParamOneDec(b, l + 1);
    r = r && MethodParamDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodParamOneDec_*
  private static boolean MethodParamDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamDec_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!MethodParamOneDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MethodParamDec_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean MethodParamNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, METHOD_PARAM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // AllType MethodParamNameDec
  static boolean MethodParamNameDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamNameDec_")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = AllType(b, l + 1);
    p = r; // pin = 1
    r = r && MethodParamNameDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MethodParamTailDec | Modifier* MethodParamNameDec_
  public static boolean MethodParamOneDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamOneDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_ONE_DEC, "<method param one dec>");
    r = MethodParamTailDec(b, l + 1);
    if (!r) r = MethodParamOneDec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Modifier* MethodParamNameDec_
  private static boolean MethodParamOneDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamOneDec_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodParamOneDec_1_0(b, l + 1);
    r = r && MethodParamNameDec_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Modifier*
  private static boolean MethodParamOneDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamOneDec_1_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MethodParamOneDec_1_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' MethodParamOneDec
  static boolean MethodParamOneDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamOneDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && MethodParamOneDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '...'
  public static boolean MethodParamTailDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodParamTailDec")) return false;
    if (!nextTokenIs(b, TYPE_TAIL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_TAIL);
    exit_section_(b, m, METHOD_PARAM_TAIL_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ':' (MethodReturnOneDec MethodReturnOneDec_*)?
  public static boolean MethodReturnDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_RETURN_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && MethodReturnDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (MethodReturnOneDec MethodReturnOneDec_*)?
  private static boolean MethodReturnDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnDec_1")) return false;
    MethodReturnDec_1_0(b, l + 1);
    return true;
  }

  // MethodReturnOneDec MethodReturnOneDec_*
  private static boolean MethodReturnDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodReturnOneDec(b, l + 1);
    r = r && MethodReturnDec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MethodReturnOneDec_*
  private static boolean MethodReturnDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnDec_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!MethodReturnOneDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MethodReturnDec_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // MethodReturnTailDec | AllType
  public static boolean MethodReturnOneDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnOneDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_RETURN_ONE_DEC, "<method return one dec>");
    r = MethodReturnTailDec(b, l + 1);
    if (!r) r = AllType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ',' MethodReturnOneDec
  static boolean MethodReturnOneDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnOneDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && MethodReturnOneDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '...'
  public static boolean MethodReturnTailDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodReturnTailDec")) return false;
    if (!nextTokenIs(b, TYPE_TAIL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_TAIL);
    exit_section_(b, m, METHOD_RETURN_TAIL_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // '(' MethodParamOneDec ')'
  public static boolean MethodSetterParamDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSetterParamDec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_SETTER_PARAM_DEC, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, MethodParamOneDec(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AttributeModifier | AccessModifier | CoroutineModifier | RegisterModifier
  public static boolean Modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODIFIER, "<modifier>");
    r = AttributeModifier(b, l + 1);
    if (!r) r = AccessModifier(b, l + 1);
    if (!r) r = CoroutineModifier(b, l + 1);
    if (!r) r = RegisterModifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Modifier* namespace NamespaceNameDec ';' NamespaceElementDec*
  public static boolean NamespaceDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DEC, "<namespace dec>");
    r = NamespaceDec_0(b, l + 1);
    r = r && consumeToken(b, NAMESPACE);
    p = r; // pin = 2
    r = r && report_error_(b, NamespaceNameDec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SEMI)) && r;
    r = p && NamespaceDec_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Modifier*
  private static boolean NamespaceDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDec_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "NamespaceDec_0", c)) break;
    }
    return true;
  }

  // NamespaceElementDec*
  private static boolean NamespaceDec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDec_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!NamespaceElementDec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "NamespaceDec_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Modifier* (GlobalMethodDec | ClassDec | EnumDec | StructDec | UsingDec | InstanceDec | OpAssignExpr)
  public static boolean NamespaceElementDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceElementDec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_ELEMENT_DEC, "<namespace element dec>");
    r = NamespaceElementDec_0(b, l + 1);
    r = r && NamespaceElementDec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Modifier*
  private static boolean NamespaceElementDec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceElementDec_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "NamespaceElementDec_0", c)) break;
    }
    return true;
  }

  // GlobalMethodDec | ClassDec | EnumDec | StructDec | UsingDec | InstanceDec | OpAssignExpr
  private static boolean NamespaceElementDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceElementDec_1")) return false;
    boolean r;
    r = GlobalMethodDec(b, l + 1);
    if (!r) r = ClassDec(b, l + 1);
    if (!r) r = EnumDec(b, l + 1);
    if (!r) r = StructDec(b, l + 1);
    if (!r) r = UsingDec(b, l + 1);
    if (!r) r = InstanceDec(b, l + 1);
    if (!r) r = OpAssignExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean NamespaceNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // Native
  public static boolean NativeModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NativeModifier")) return false;
    if (!nextTokenIs(b, NATIVE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NATIVE);
    exit_section_(b, m, NATIVE_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // Nullable
  public static boolean NullableModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NullableModifier")) return false;
    if (!nextTokenIs(b, NULLABLE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NULLABLE);
    exit_section_(b, m, NULLABLE_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // '++' | '--'
  public static boolean Op1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op1")) return false;
    if (!nextTokenIs(b, "<op 1>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_1, "<op 1>");
    r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op1 ValueStat ';'
  public static boolean Op1Expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op1Expr")) return false;
    if (!nextTokenIs(b, "<op 1 expr>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_1_EXPR, "<op 1 expr>");
    r = Op1(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, ValueStat(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '!' | '-'
  public static boolean Op2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op2")) return false;
    if (!nextTokenIs(b, "<op 2>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2, "<op 2>");
    r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op2Value Op2SuffixEx*
  public static boolean Op2Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op2Stat")) return false;
    if (!nextTokenIs(b, "<op 2 stat>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_STAT, "<op 2 stat>");
    r = Op2Value(b, l + 1);
    r = r && Op2Stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Op2SuffixEx*
  private static boolean Op2Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op2Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op2SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op2Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix | Op5Suffix | Op6Suffix | Op7Suffix | Op8Suffix
  public static boolean Op2SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op2SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_SUFFIX_EX, "<op 2 suffix ex>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op2 ValueFactorStat
  public static boolean Op2Value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op2Value")) return false;
    if (!nextTokenIs(b, "<op 2 value>", MINUS, NOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_2_VALUE, "<op 2 value>");
    r = Op2(b, l + 1);
    p = r; // pin = 1
    r = r && ValueFactorStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '*' | '/' | '%'
  public static boolean Op3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3, "<op 3>");
    r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, REMAINDER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op3Suffix Op3SuffixEx*
  public static boolean Op3Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_STAT, "<op 3 stat>");
    r = Op3Suffix(b, l + 1);
    r = r && Op3Stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Op3SuffixEx*
  private static boolean Op3Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op3SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op3Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3 (ValueFactorStat | Op2Value)
  public static boolean Op3Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3Suffix")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX, "<op 3 suffix>");
    r = Op3(b, l + 1);
    p = r; // pin = 1
    r = r && Op3Suffix_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op3Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix | Op5Suffix | Op6Suffix | Op7Suffix | Op8Suffix
  public static boolean Op3SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op3SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX_EX, "<op 3 suffix ex>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+' | '-'
  public static boolean Op4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4")) return false;
    if (!nextTokenIs(b, "<op 4>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4, "<op 4>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op4Suffix Op4SuffixEx*
  public static boolean Op4Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4Stat")) return false;
    if (!nextTokenIs(b, "<op 4 stat>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_STAT, "<op 4 stat>");
    r = Op4Suffix(b, l + 1);
    r = r && Op4Stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Op4SuffixEx*
  private static boolean Op4Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op4SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op4Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op4 (ValueFactorStat | Op2Value) Op4SuffixEe*
  public static boolean Op4Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4Suffix")) return false;
    if (!nextTokenIs(b, "<op 4 suffix>", MINUS, PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX, "<op 4 suffix>");
    r = Op4(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Op4Suffix_1(b, l + 1));
    r = p && Op4Suffix_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op4Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  // Op4SuffixEe*
  private static boolean Op4Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op4SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op4Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix
  public static boolean Op4SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EE, "<op 4 suffix ee>");
    r = Op3Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op4Suffix | Op5Suffix | Op6Suffix | Op7Suffix | Op8Suffix
  public static boolean Op4SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op4SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EX, "<op 4 suffix ex>");
    r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '..'
  public static boolean Op5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONCAT);
    exit_section_(b, m, OP_5, r);
    return r;
  }

  /* ********************************************************** */
  // Op5Suffix Op5SuffixEx*
  public static boolean Op5Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5Stat")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Op5Suffix(b, l + 1);
    r = r && Op5Stat_1(b, l + 1);
    exit_section_(b, m, OP_5_STAT, r);
    return r;
  }

  // Op5SuffixEx*
  private static boolean Op5Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op5SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op5Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op5 (ValueFactorStat | Op2Value) Op5SuffixEe*
  public static boolean Op5Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5Suffix")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX, null);
    r = Op5(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Op5Suffix_1(b, l + 1));
    r = p && Op5Suffix_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op5Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  // Op5SuffixEe*
  private static boolean Op5Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op5SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op5Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix
  public static boolean Op5SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EE, "<op 5 suffix ee>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op5Suffix | Op6Suffix | Op7Suffix | Op8Suffix
  public static boolean Op5SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op5SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EX, "<op 5 suffix ex>");
    r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '<=' | '<' | '>=' | '>' | '==' | '!='
  public static boolean Op6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6, "<op 6>");
    r = consumeToken(b, LESS_OR_EQUAL);
    if (!r) r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, GREATER_OR_EQUAL);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, NOT_EQ);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op6Suffix Op6SuffixEx*
  public static boolean Op6Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6Stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_STAT, "<op 6 stat>");
    r = Op6Suffix(b, l + 1);
    r = r && Op6Stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Op6SuffixEx*
  private static boolean Op6Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op6SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op6Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op6 (ValueFactorStat | Op2Value) Op6SuffixEe*
  public static boolean Op6Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6Suffix")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX, "<op 6 suffix>");
    r = Op6(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Op6Suffix_1(b, l + 1));
    r = p && Op6Suffix_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op6Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  // Op6SuffixEe*
  private static boolean Op6Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op6SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op6Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix | Op5Suffix
  public static boolean Op6SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EE, "<op 6 suffix ee>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op6Suffix | Op7Suffix | Op8Suffix
  public static boolean Op6SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op6SuffixEx")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EX, "<op 6 suffix ex>");
    r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '&&'
  public static boolean Op7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_AND);
    exit_section_(b, m, OP_7, r);
    return r;
  }

  /* ********************************************************** */
  // Op7Suffix Op7SuffixEx*
  public static boolean Op7Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7Stat")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Op7Suffix(b, l + 1);
    r = r && Op7Stat_1(b, l + 1);
    exit_section_(b, m, OP_7_STAT, r);
    return r;
  }

  // Op7SuffixEx*
  private static boolean Op7Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op7SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op7Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op7 (ValueFactorStat | Op2Value) Op7SuffixEe*
  public static boolean Op7Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7Suffix")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX, null);
    r = Op7(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Op7Suffix_1(b, l + 1));
    r = p && Op7Suffix_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op7Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  // Op7SuffixEe*
  private static boolean Op7Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op7SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op7Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix | Op5Suffix | Op6Suffix
  public static boolean Op7SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EE, "<op 7 suffix ee>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op7Suffix | Op8Suffix
  public static boolean Op7SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op7SuffixEx")) return false;
    if (!nextTokenIs(b, "<op 7 suffix ex>", COND_AND, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EX, "<op 7 suffix ex>");
    r = Op7Suffix(b, l + 1);
    if (!r) r = Op8Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '||'
  public static boolean Op8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_OR);
    exit_section_(b, m, OP_8, r);
    return r;
  }

  /* ********************************************************** */
  // Op8Suffix Op8SuffixEx*
  public static boolean Op8Stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8Stat")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Op8Suffix(b, l + 1);
    r = r && Op8Stat_1(b, l + 1);
    exit_section_(b, m, OP_8_STAT, r);
    return r;
  }

  // Op8SuffixEx*
  private static boolean Op8Stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8Stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op8SuffixEx(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op8Stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op8 (ValueFactorStat | Op2Value) Op8SuffixEe*
  public static boolean Op8Suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8Suffix")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_8_SUFFIX, null);
    r = Op8(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Op8Suffix_1(b, l + 1));
    r = p && Op8Suffix_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueFactorStat | Op2Value
  private static boolean Op8Suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8Suffix_1")) return false;
    boolean r;
    r = ValueFactorStat(b, l + 1);
    if (!r) r = Op2Value(b, l + 1);
    return r;
  }

  // Op8SuffixEe*
  private static boolean Op8Suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8Suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Op8SuffixEe(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Op8Suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Op3Suffix | Op4Suffix | Op5Suffix | Op6Suffix | Op7Suffix
  public static boolean Op8SuffixEe(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8SuffixEe")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_8_SUFFIX_EE, "<op 8 suffix ee>");
    r = Op3Suffix(b, l + 1);
    if (!r) r = Op4Suffix(b, l + 1);
    if (!r) r = Op5Suffix(b, l + 1);
    if (!r) r = Op6Suffix(b, l + 1);
    if (!r) r = Op7Suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Op8Suffix
  public static boolean Op8SuffixEx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Op8SuffixEx")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Op8Suffix(b, l + 1);
    exit_section_(b, m, OP_8_SUFFIX_EX, r);
    return r;
  }

  /* ********************************************************** */
  // '=' | '+=' | '-=' | '*=' | '/=' | '%='
  public static boolean OpAssign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpAssign")) return false;
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
  // PropertyValue OpAssignExprTail_? ';'
  public static boolean OpAssignExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpAssignExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_ASSIGN_EXPR, "<op assign expr>");
    r = PropertyValue(b, l + 1);
    r = r && OpAssignExpr_1(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // OpAssignExprTail_?
  private static boolean OpAssignExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpAssignExpr_1")) return false;
    OpAssignExprTail_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // PropertyValue_* OpAssign ValueStat
  static boolean OpAssignExprTail_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpAssignExprTail_")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = OpAssignExprTail__0(b, l + 1);
    r = r && OpAssign(b, l + 1);
    p = r; // pin = 2
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // PropertyValue_*
  private static boolean OpAssignExprTail__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpAssignExprTail__0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!PropertyValue_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OpAssignExprTail__0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '[' (ValueStat OpNewListStatValueStat_*)? ']'
  public static boolean OpNewListStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewListStat")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_NEW_LIST_STAT, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, OpNewListStat_1(b, l + 1));
    r = p && consumeToken(b, RBRACK) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ValueStat OpNewListStatValueStat_*)?
  private static boolean OpNewListStat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewListStat_1")) return false;
    OpNewListStat_1_0(b, l + 1);
    return true;
  }

  // ValueStat OpNewListStatValueStat_*
  private static boolean OpNewListStat_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewListStat_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && OpNewListStat_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OpNewListStatValueStat_*
  private static boolean OpNewListStat_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewListStat_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!OpNewListStatValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OpNewListStat_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean OpNewListStatValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewListStatValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // new (CustomType | GenericType) '(' (ValueStat OpNewStatValueStat_*)? ')'
  public static boolean OpNewStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStat")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_NEW_STAT, null);
    r = consumeToken(b, NEW);
    p = r; // pin = 1
    r = r && report_error_(b, OpNewStat_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, OpNewStat_3(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // CustomType | GenericType
  private static boolean OpNewStat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStat_1")) return false;
    boolean r;
    r = CustomType(b, l + 1);
    if (!r) r = GenericType(b, l + 1);
    return r;
  }

  // (ValueStat OpNewStatValueStat_*)?
  private static boolean OpNewStat_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStat_3")) return false;
    OpNewStat_3_0(b, l + 1);
    return true;
  }

  // ValueStat OpNewStatValueStat_*
  private static boolean OpNewStat_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStat_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && OpNewStat_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OpNewStatValueStat_*
  private static boolean OpNewStat_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStat_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!OpNewStatValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OpNewStat_3_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean OpNewStatValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OpNewStatValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // bool | double | int | long | any | string
  public static boolean PrimitiveType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PrimitiveType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMITIVE_TYPE, "<primitive type>");
    r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, LONG);
    if (!r) r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PropertyValueFirstType PropertyValueSuffix*
  public static boolean PropertyValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE, "<property value>");
    r = PropertyValueFirstType(b, l + 1);
    r = r && PropertyValue_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PropertyValueSuffix*
  private static boolean PropertyValue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValue_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!PropertyValueSuffix(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PropertyValue_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '[' ValueStat ']'
  public static boolean PropertyValueBracketValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueBracketValue")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_BRACKET_VALUE, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, ValueStat(b, l + 1));
    r = p && consumeToken(b, RBRACK) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // cast '<' AllType '>' '(' ValueFactorStat ')'
  public static boolean PropertyValueCastType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueCastType")) return false;
    if (!nextTokenIs(b, CAST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_CAST_TYPE, null);
    r = consumeTokens(b, 1, CAST, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, AllType(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, GREATER, LPAREN)) && r;
    r = p && report_error_(b, ValueFactorStat(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean PropertyValueCustomType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueCustomType")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, PROPERTY_VALUE_CUSTOM_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // '.' PropertyValueDotIdName
  public static boolean PropertyValueDotId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueDotId")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_DOT_ID, null);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && PropertyValueDotIdName(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT | 'Map' | 'async' | 'get'
  public static boolean PropertyValueDotIdName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueDotIdName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_DOT_ID_NAME, "<property value dot id name>");
    r = consumeToken(b, ID_CONTENT);
    if (!r) r = consumeToken(b, "Map");
    if (!r) r = consumeToken(b, "async");
    if (!r) r = consumeToken(b, "get");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PropertyValueThisType | PropertyValueCastType | PropertyValueCustomType
  public static boolean PropertyValueFirstType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueFirstType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_FIRST_TYPE, "<property value first type>");
    r = PropertyValueThisType(b, l + 1);
    if (!r) r = PropertyValueCastType(b, l + 1);
    if (!r) r = PropertyValueCustomType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PropertyValueMethodTemplate? '(' (ValueStat PropertyValueMethodCallValueStat_*)? ')'
  public static boolean PropertyValueMethodCall(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCall")) return false;
    if (!nextTokenIs(b, "<property value method call>", LBRACE, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_METHOD_CALL, "<property value method call>");
    r = PropertyValueMethodCall_0(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    p = r; // pin = 2
    r = r && report_error_(b, PropertyValueMethodCall_2(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // PropertyValueMethodTemplate?
  private static boolean PropertyValueMethodCall_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCall_0")) return false;
    PropertyValueMethodTemplate(b, l + 1);
    return true;
  }

  // (ValueStat PropertyValueMethodCallValueStat_*)?
  private static boolean PropertyValueMethodCall_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCall_2")) return false;
    PropertyValueMethodCall_2_0(b, l + 1);
    return true;
  }

  // ValueStat PropertyValueMethodCallValueStat_*
  private static boolean PropertyValueMethodCall_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCall_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && PropertyValueMethodCall_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PropertyValueMethodCallValueStat_*
  private static boolean PropertyValueMethodCall_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCall_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!PropertyValueMethodCallValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PropertyValueMethodCall_2_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean PropertyValueMethodCallValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodCallValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' AllType PropertyValueMethodTemplateAllType_* '}'
  public static boolean PropertyValueMethodTemplate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodTemplate")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_METHOD_TEMPLATE, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, AllType(b, l + 1));
    r = p && report_error_(b, PropertyValueMethodTemplate_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // PropertyValueMethodTemplateAllType_*
  private static boolean PropertyValueMethodTemplate_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodTemplate_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!PropertyValueMethodTemplateAllType_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PropertyValueMethodTemplate_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' AllType
  static boolean PropertyValueMethodTemplateAllType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueMethodTemplateAllType_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && AllType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PropertyValueDotId | PropertyValueBracketValue | PropertyValueMethodCall
  public static boolean PropertyValueSuffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueSuffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_SUFFIX, "<property value suffix>");
    r = PropertyValueDotId(b, l + 1);
    if (!r) r = PropertyValueBracketValue(b, l + 1);
    if (!r) r = PropertyValueMethodCall(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // this
  public static boolean PropertyValueThisType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValueThisType")) return false;
    if (!nextTokenIs(b, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, THIS);
    exit_section_(b, m, PROPERTY_VALUE_THIS_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // ',' PropertyValue
  static boolean PropertyValue_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PropertyValue_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && PropertyValue(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Http | HttpDownload | HttpUpload | Msg
  public static boolean ProtocolModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ProtocolModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROTOCOL_MODIFIER, "<protocol modifier>");
    r = consumeToken(b, HTTP);
    if (!r) r = consumeToken(b, HTTPDOWNLOAD);
    if (!r) r = consumeToken(b, HTTPUPLOAD);
    if (!r) r = consumeToken(b, MSG);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '<' CustomType '>'
  public static boolean ReflectCustomType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReflectCustomType")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REFLECT_CUSTOM_TYPE, null);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, CustomType(b, l + 1));
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // reflect (ReflectCustomType | ReflectValueStat)
  public static boolean ReflectValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReflectValue")) return false;
    if (!nextTokenIs(b, REFLECT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REFLECT_VALUE, null);
    r = consumeToken(b, REFLECT);
    p = r; // pin = 1
    r = r && ReflectValue_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ReflectCustomType | ReflectValueStat
  private static boolean ReflectValue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReflectValue_1")) return false;
    boolean r;
    r = ReflectCustomType(b, l + 1);
    if (!r) r = ReflectValueStat(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '(' ValueStat ')'
  public static boolean ReflectValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReflectValueStat")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REFLECT_VALUE_STAT, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ValueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // register
  public static boolean RegisterModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RegisterModifier")) return false;
    if (!nextTokenIs(b, REGISTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, REGISTER);
    exit_section_(b, m, REGISTER_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // return ((ValueStat ReturnExprValueStat_*) | ReturnYield)? ';'
  public static boolean ReturnExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExpr")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_EXPR, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, ReturnExpr_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((ValueStat ReturnExprValueStat_*) | ReturnYield)?
  private static boolean ReturnExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExpr_1")) return false;
    ReturnExpr_1_0(b, l + 1);
    return true;
  }

  // (ValueStat ReturnExprValueStat_*) | ReturnYield
  private static boolean ReturnExpr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExpr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ReturnExpr_1_0_0(b, l + 1);
    if (!r) r = ReturnYield(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ValueStat ReturnExprValueStat_*
  private static boolean ReturnExpr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExpr_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && ReturnExpr_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ReturnExprValueStat_*
  private static boolean ReturnExpr_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExpr_1_0_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ReturnExprValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ReturnExpr_1_0_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean ReturnExprValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnExprValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ValueStat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // yield
  public static boolean ReturnYield(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnYield")) return false;
    if (!nextTokenIs(b, YIELD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, YIELD);
    exit_section_(b, m, RETURN_YIELD, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (StructOptionDec | StructVarDec)* '}'
  public static boolean StructBodyDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructBodyDec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, StructBodyDec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (StructOptionDec | StructVarDec)*
  private static boolean StructBodyDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructBodyDec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!StructBodyDec_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "StructBodyDec_1", c)) break;
    }
    return true;
  }

  // StructOptionDec | StructVarDec
  private static boolean StructBodyDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructBodyDec_1_0")) return false;
    boolean r;
    r = StructOptionDec(b, l + 1);
    if (!r) r = StructVarDec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // struct (StructNameDec StructExtendsDec? StructBodyDec?)?
  public static boolean StructDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructDec")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_DEC, null);
    r = consumeToken(b, STRUCT);
    p = r; // pin = 1
    r = r && StructDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (StructNameDec StructExtendsDec? StructBodyDec?)?
  private static boolean StructDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructDec_1")) return false;
    StructDec_1_0(b, l + 1);
    return true;
  }

  // StructNameDec StructExtendsDec? StructBodyDec?
  private static boolean StructDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StructNameDec(b, l + 1);
    r = r && StructDec_1_0_1(b, l + 1);
    r = r && StructDec_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // StructExtendsDec?
  private static boolean StructDec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructDec_1_0_1")) return false;
    StructExtendsDec(b, l + 1);
    return true;
  }

  // StructBodyDec?
  private static boolean StructDec_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructDec_1_0_2")) return false;
    StructBodyDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ':' (NamespaceNameDec '.')? StructNameDec
  public static boolean StructExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_EXTENDS_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && report_error_(b, StructExtendsDec_1(b, l + 1));
    r = p && StructNameDec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (NamespaceNameDec '.')?
  private static boolean StructExtendsDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructExtendsDec_1")) return false;
    StructExtendsDec_1_0(b, l + 1);
    return true;
  }

  // NamespaceNameDec '.'
  private static boolean StructExtendsDec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructExtendsDec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = NamespaceNameDec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean StructNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // option StructOptionNameDec '=' TEXT_CONTENT ';'
  public static boolean StructOptionDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructOptionDec")) return false;
    if (!nextTokenIs(b, OPTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_OPTION_DEC, null);
    r = consumeToken(b, OPTION);
    p = r; // pin = 1
    r = r && report_error_(b, StructOptionNameDec(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, ASSIGN, TEXT_CONTENT, SEMI)) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean StructOptionNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructOptionNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_OPTION_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // AllType StructVarNameDec ';'
  public static boolean StructVarDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructVarDec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_VAR_DEC, "<struct var dec>");
    r = AllType(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, StructVarNameDec(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean StructVarNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructVarNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // tcall '(' (ValueStat TcallStatValueStat_*)? ')'
  public static boolean TcallStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TcallStat")) return false;
    if (!nextTokenIs(b, TCALL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TCALL_STAT, null);
    r = consumeTokens(b, 1, TCALL, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, TcallStat_2(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ValueStat TcallStatValueStat_*)?
  private static boolean TcallStat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TcallStat_2")) return false;
    TcallStat_2_0(b, l + 1);
    return true;
  }

  // ValueStat TcallStatValueStat_*
  private static boolean TcallStat_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TcallStat_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && TcallStat_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TcallStatValueStat_*
  private static boolean TcallStat_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TcallStat_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!TcallStatValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TcallStat_2_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean TcallStatValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TcallStatValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '<' TemplatePairDec TemplatePairDec_* '>'
  public static boolean TemplateDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateDec")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_DEC, null);
    r = consumeToken(b, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, TemplatePairDec(b, l + 1));
    r = p && report_error_(b, TemplateDec_2(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TemplatePairDec_*
  private static boolean TemplateDec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateDec_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!TemplatePairDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TemplateDec_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // class
  public static boolean TemplateExtendsClassDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateExtendsClassDec")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CLASS);
    exit_section_(b, m, TEMPLATE_EXTENDS_CLASS_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ':' (AllType | TemplateExtendsClassDec | TemplateExtendsStructDec)
  public static boolean TemplateExtendsDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateExtendsDec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_EXTENDS_DEC, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && TemplateExtendsDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllType | TemplateExtendsClassDec | TemplateExtendsStructDec
  private static boolean TemplateExtendsDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateExtendsDec_1")) return false;
    boolean r;
    r = AllType(b, l + 1);
    if (!r) r = TemplateExtendsClassDec(b, l + 1);
    if (!r) r = TemplateExtendsStructDec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // struct
  public static boolean TemplateExtendsStructDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateExtendsStructDec")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRUCT);
    exit_section_(b, m, TEMPLATE_EXTENDS_STRUCT_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean TemplateNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplateNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, TEMPLATE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // TemplateNameDec TemplateExtendsDec?
  public static boolean TemplatePairDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplatePairDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_PAIR_DEC, null);
    r = TemplateNameDec(b, l + 1);
    p = r; // pin = 1
    r = r && TemplatePairDec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TemplateExtendsDec?
  private static boolean TemplatePairDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplatePairDec_1")) return false;
    TemplateExtendsDec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ',' TemplatePairDec
  static boolean TemplatePairDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TemplatePairDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && TemplatePairDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // throw '(' (ValueStat ThrowExprValueStat_*)? ')' ';'
  public static boolean ThrowExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ThrowExpr")) return false;
    if (!nextTokenIs(b, THROW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, THROW_EXPR, null);
    r = consumeTokens(b, 1, THROW, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ThrowExpr_2(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, RPAREN, SEMI)) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ValueStat ThrowExprValueStat_*)?
  private static boolean ThrowExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ThrowExpr_2")) return false;
    ThrowExpr_2_0(b, l + 1);
    return true;
  }

  // ValueStat ThrowExprValueStat_*
  private static boolean ThrowExpr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ThrowExpr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ValueStat(b, l + 1);
    r = r && ThrowExpr_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ThrowExprValueStat_*
  private static boolean ThrowExpr_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ThrowExpr_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ThrowExprValueStat_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ThrowExpr_2_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ',' ValueStat
  static boolean ThrowExprValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ThrowExprValueStat_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // using UsingNameDec '=' AllType ';'
  public static boolean UsingDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingDec")) return false;
    if (!nextTokenIs(b, USING)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, USING_DEC, null);
    r = consumeToken(b, USING);
    p = r; // pin = 1
    r = r && report_error_(b, UsingNameDec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, ASSIGN)) && r;
    r = p && report_error_(b, AllType(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean UsingNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, USING_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // WrapValueStat | ConstValue | ReflectValue | PropertyValue | MethodParamTailDec | CoroutineStat
  public static boolean ValueFactorStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueFactorStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_FACTOR_STAT, "<value factor stat>");
    r = WrapValueStat(b, l + 1);
    if (!r) r = ConstValue(b, l + 1);
    if (!r) r = ReflectValue(b, l + 1);
    if (!r) r = PropertyValue(b, l + 1);
    if (!r) r = MethodParamTailDec(b, l + 1);
    if (!r) r = CoroutineStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ValueFactorStat (Op3Stat | Op4Stat | Op5Stat | Op6Stat | Op7Stat | Op8Stat)?
  public static boolean ValueOpStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueOpStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_OP_STAT, "<value op stat>");
    r = ValueFactorStat(b, l + 1);
    r = r && ValueOpStat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (Op3Stat | Op4Stat | Op5Stat | Op6Stat | Op7Stat | Op8Stat)?
  private static boolean ValueOpStat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueOpStat_1")) return false;
    ValueOpStat_1_0(b, l + 1);
    return true;
  }

  // Op3Stat | Op4Stat | Op5Stat | Op6Stat | Op7Stat | Op8Stat
  private static boolean ValueOpStat_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueOpStat_1_0")) return false;
    boolean r;
    r = Op3Stat(b, l + 1);
    if (!r) r = Op4Stat(b, l + 1);
    if (!r) r = Op5Stat(b, l + 1);
    if (!r) r = Op6Stat(b, l + 1);
    if (!r) r = Op7Stat(b, l + 1);
    if (!r) r = Op8Stat(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // OpNewStat | OpNewListStat | BindStat | TcallStat | Op2Stat | ValueOpStat
  public static boolean ValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueStat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_STAT, "<value stat>");
    r = OpNewStat(b, l + 1);
    if (!r) r = OpNewListStat(b, l + 1);
    if (!r) r = BindStat(b, l + 1);
    if (!r) r = TcallStat(b, l + 1);
    if (!r) r = Op2Stat(b, l + 1);
    if (!r) r = ValueOpStat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VarAssignNameDec VarAssignNameDecAllType_?
  public static boolean VarAssignDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarAssignNameDec(b, l + 1);
    r = r && VarAssignDec_1(b, l + 1);
    exit_section_(b, m, VAR_ASSIGN_DEC, r);
    return r;
  }

  // VarAssignNameDecAllType_?
  private static boolean VarAssignDec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignDec_1")) return false;
    VarAssignNameDecAllType_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // var VarAssignDec VarAssignExprVarAssignDec_* VarAssignExprValueStat_? ';'
  public static boolean VarAssignExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignExpr")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VAR_ASSIGN_EXPR, null);
    r = consumeToken(b, VAR);
    p = r; // pin = 1
    r = r && report_error_(b, VarAssignDec(b, l + 1));
    r = p && report_error_(b, VarAssignExpr_2(b, l + 1)) && r;
    r = p && report_error_(b, VarAssignExpr_3(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // VarAssignExprVarAssignDec_*
  private static boolean VarAssignExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignExpr_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!VarAssignExprVarAssignDec_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VarAssignExpr_2", c)) break;
    }
    return true;
  }

  // VarAssignExprValueStat_?
  private static boolean VarAssignExpr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignExpr_3")) return false;
    VarAssignExprValueStat_(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '=' ValueStat
  static boolean VarAssignExprValueStat_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignExprValueStat_")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && ValueStat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ',' VarAssignDec
  static boolean VarAssignExprVarAssignDec_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignExprVarAssignDec_")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && VarAssignDec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean VarAssignNameDec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignNameDec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, VAR_ASSIGN_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ':' AllType
  static boolean VarAssignNameDecAllType_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarAssignNameDecAllType_")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && AllType(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean WhileBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, WhileBody_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean WhileBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "WhileBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' ValueStat? ')'
  public static boolean WhileCondition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileCondition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_CONDITION, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, WhileCondition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ValueStat?
  private static boolean WhileCondition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileCondition_1")) return false;
    ValueStat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // while WhileCondition (WhileBody | AllExpr)
  public static boolean WhileExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileExpr")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_EXPR, null);
    r = consumeToken(b, WHILE);
    p = r; // pin = 1
    r = r && report_error_(b, WhileCondition(b, l + 1));
    r = p && WhileExpr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // WhileBody | AllExpr
  private static boolean WhileExpr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileExpr_2")) return false;
    boolean r;
    r = WhileBody(b, l + 1);
    if (!r) r = AllExpr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '{' AllExpr* '}'
  public static boolean WrapExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WrapExpr")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRAP_EXPR, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, WrapExpr_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AllExpr*
  private static boolean WrapExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WrapExpr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AllExpr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "WrapExpr_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' ValueStat ')'
  public static boolean WrapValueStat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WrapValueStat")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRAP_VALUE_STAT, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ValueStat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NamespaceDec?
  static boolean alittleFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alittleFile")) return false;
    NamespaceDec(b, l + 1);
    return true;
  }

}
