// This is a generated file. Not intended for manual editing.
package plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static plugin.psi.ALittleTypes.*;
import static plugin.parser.ALittleParserUtil.*;
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
    if (t == ACCESS_MODIFIER) {
      r = access_modifier(b, 0);
    }
    else if (t == ALL_EXPR) {
      r = all_expr(b, 0);
    }
    else if (t == ALL_TYPE) {
      r = all_type(b, 0);
    }
    else if (t == CLASS_ACCESS_MODIFIER) {
      r = class_access_modifier(b, 0);
    }
    else if (t == CLASS_CTOR_DEC) {
      r = class_ctor_dec(b, 0);
    }
    else if (t == CLASS_DEC) {
      r = class_dec(b, 0);
    }
    else if (t == CLASS_EXTENDS_ACCESS_MODIFIER) {
      r = class_extends_access_modifier(b, 0);
    }
    else if (t == CLASS_EXTENDS_NAME_DEC) {
      r = class_extends_name_dec(b, 0);
    }
    else if (t == CLASS_EXTENDS_NAMESPACE_NAME_DEC) {
      r = class_extends_namespace_name_dec(b, 0);
    }
    else if (t == CLASS_GETTER_DEC) {
      r = class_getter_dec(b, 0);
    }
    else if (t == CLASS_METHOD_DEC) {
      r = class_method_dec(b, 0);
    }
    else if (t == CLASS_NAME_DEC) {
      r = class_name_dec(b, 0);
    }
    else if (t == CLASS_SETTER_DEC) {
      r = class_setter_dec(b, 0);
    }
    else if (t == CLASS_STATIC_DEC) {
      r = class_static_dec(b, 0);
    }
    else if (t == CLASS_VAR_DEC) {
      r = class_var_dec(b, 0);
    }
    else if (t == CLASS_VAR_NAME_DEC) {
      r = class_var_name_dec(b, 0);
    }
    else if (t == CONST_VALUE) {
      r = const_value(b, 0);
    }
    else if (t == CUSTOM_TYPE) {
      r = custom_type(b, 0);
    }
    else if (t == CUSTOM_TYPE_NAME_DEC) {
      r = custom_type_name_dec(b, 0);
    }
    else if (t == CUSTOM_TYPE_NAMESPACE_NAME_DEC) {
      r = custom_type_namespace_name_dec(b, 0);
    }
    else if (t == DO_WHILE_EXPR) {
      r = do_while_expr(b, 0);
    }
    else if (t == ELSE_EXPR) {
      r = else_expr(b, 0);
    }
    else if (t == ELSE_IF_EXPR) {
      r = else_if_expr(b, 0);
    }
    else if (t == ENUM_DEC) {
      r = enum_dec(b, 0);
    }
    else if (t == ENUM_NAME_DEC) {
      r = enum_name_dec(b, 0);
    }
    else if (t == ENUM_VAR_DEC) {
      r = enum_var_dec(b, 0);
    }
    else if (t == ENUM_VAR_NAME_DEC) {
      r = enum_var_name_dec(b, 0);
    }
    else if (t == ENUM_VAR_VALUE_DEC) {
      r = enum_var_value_dec(b, 0);
    }
    else if (t == FLOW_EXPR) {
      r = flow_expr(b, 0);
    }
    else if (t == FOR_END_STAT) {
      r = for_end_stat(b, 0);
    }
    else if (t == FOR_EXPR) {
      r = for_expr(b, 0);
    }
    else if (t == FOR_IN_CONDITION) {
      r = for_in_condition(b, 0);
    }
    else if (t == FOR_PAIR_DEC) {
      r = for_pair_dec(b, 0);
    }
    else if (t == FOR_START_STAT) {
      r = for_start_stat(b, 0);
    }
    else if (t == FOR_STEP_CONDITION) {
      r = for_step_condition(b, 0);
    }
    else if (t == FOR_STEP_STAT) {
      r = for_step_stat(b, 0);
    }
    else if (t == GENERIC_FUNCTOR_PARAM_TYPE) {
      r = generic_functor_param_type(b, 0);
    }
    else if (t == GENERIC_FUNCTOR_RETURN_TYPE) {
      r = generic_functor_return_type(b, 0);
    }
    else if (t == GENERIC_FUNCTOR_TYPE) {
      r = generic_functor_type(b, 0);
    }
    else if (t == GENERIC_LIST_TYPE) {
      r = generic_list_type(b, 0);
    }
    else if (t == GENERIC_MAP_TYPE) {
      r = generic_map_type(b, 0);
    }
    else if (t == GENERIC_TYPE) {
      r = generic_type(b, 0);
    }
    else if (t == GLOBAL_METHOD_DEC) {
      r = global_method_dec(b, 0);
    }
    else if (t == IF_EXPR) {
      r = if_expr(b, 0);
    }
    else if (t == INSTANCE_CLASS_NAME_DEC) {
      r = instance_class_name_dec(b, 0);
    }
    else if (t == INSTANCE_DEC) {
      r = instance_dec(b, 0);
    }
    else if (t == INSTANCE_NAME_DEC) {
      r = instance_name_dec(b, 0);
    }
    else if (t == METHOD_BODY_DEC) {
      r = method_body_dec(b, 0);
    }
    else if (t == METHOD_NAME_DEC) {
      r = method_name_dec(b, 0);
    }
    else if (t == METHOD_PARAM_DEC) {
      r = method_param_dec(b, 0);
    }
    else if (t == METHOD_PARAM_NAME_DEC) {
      r = method_param_name_dec(b, 0);
    }
    else if (t == METHOD_PARAM_ONE_DEC) {
      r = method_param_one_dec(b, 0);
    }
    else if (t == METHOD_PARAM_TYPE_DEC) {
      r = method_param_type_dec(b, 0);
    }
    else if (t == METHOD_RETURN_DEC) {
      r = method_return_dec(b, 0);
    }
    else if (t == METHOD_RETURN_TYPE_DEC) {
      r = method_return_type_dec(b, 0);
    }
    else if (t == NAMESPACE_DEC) {
      r = namespace_dec(b, 0);
    }
    else if (t == NAMESPACE_NAME_DEC) {
      r = namespace_name_dec(b, 0);
    }
    else if (t == OP_1) {
      r = op_1(b, 0);
    }
    else if (t == OP_1_EXPR) {
      r = op_1_expr(b, 0);
    }
    else if (t == OP_2) {
      r = op_2(b, 0);
    }
    else if (t == OP_2_STAT) {
      r = op_2_stat(b, 0);
    }
    else if (t == OP_2_SUFFIX_EX) {
      r = op_2_suffix_ex(b, 0);
    }
    else if (t == OP_2_VALUE) {
      r = op_2_value(b, 0);
    }
    else if (t == OP_3) {
      r = op_3(b, 0);
    }
    else if (t == OP_3_STAT) {
      r = op_3_stat(b, 0);
    }
    else if (t == OP_3_SUFFIX) {
      r = op_3_suffix(b, 0);
    }
    else if (t == OP_3_SUFFIX_EX) {
      r = op_3_suffix_ex(b, 0);
    }
    else if (t == OP_4) {
      r = op_4(b, 0);
    }
    else if (t == OP_4_STAT) {
      r = op_4_stat(b, 0);
    }
    else if (t == OP_4_SUFFIX) {
      r = op_4_suffix(b, 0);
    }
    else if (t == OP_4_SUFFIX_EE) {
      r = op_4_suffix_ee(b, 0);
    }
    else if (t == OP_4_SUFFIX_EX) {
      r = op_4_suffix_ex(b, 0);
    }
    else if (t == OP_5) {
      r = op_5(b, 0);
    }
    else if (t == OP_5_STAT) {
      r = op_5_stat(b, 0);
    }
    else if (t == OP_5_SUFFIX) {
      r = op_5_suffix(b, 0);
    }
    else if (t == OP_5_SUFFIX_EE) {
      r = op_5_suffix_ee(b, 0);
    }
    else if (t == OP_5_SUFFIX_EX) {
      r = op_5_suffix_ex(b, 0);
    }
    else if (t == OP_6) {
      r = op_6(b, 0);
    }
    else if (t == OP_6_STAT) {
      r = op_6_stat(b, 0);
    }
    else if (t == OP_6_SUFFIX) {
      r = op_6_suffix(b, 0);
    }
    else if (t == OP_6_SUFFIX_EE) {
      r = op_6_suffix_ee(b, 0);
    }
    else if (t == OP_6_SUFFIX_EX) {
      r = op_6_suffix_ex(b, 0);
    }
    else if (t == OP_7) {
      r = op_7(b, 0);
    }
    else if (t == OP_7_STAT) {
      r = op_7_stat(b, 0);
    }
    else if (t == OP_7_SUFFIX) {
      r = op_7_suffix(b, 0);
    }
    else if (t == OP_7_SUFFIX_EE) {
      r = op_7_suffix_ee(b, 0);
    }
    else if (t == OP_7_SUFFIX_EX) {
      r = op_7_suffix_ex(b, 0);
    }
    else if (t == OP_8) {
      r = op_8(b, 0);
    }
    else if (t == OP_8_STAT) {
      r = op_8_stat(b, 0);
    }
    else if (t == OP_8_SUFFIX) {
      r = op_8_suffix(b, 0);
    }
    else if (t == OP_8_SUFFIX_EE) {
      r = op_8_suffix_ee(b, 0);
    }
    else if (t == OP_8_SUFFIX_EX) {
      r = op_8_suffix_ex(b, 0);
    }
    else if (t == OP_ASSIGN) {
      r = op_assign(b, 0);
    }
    else if (t == OP_ASSIGN_EXPR) {
      r = op_assign_expr(b, 0);
    }
    else if (t == OP_NEW_STAT) {
      r = op_new_stat(b, 0);
    }
    else if (t == PRIMITIVE_TYPE) {
      r = primitive_type(b, 0);
    }
    else if (t == PROPERTY_VALUE) {
      r = property_value(b, 0);
    }
    else if (t == PROPERTY_VALUE_BRACK_VALUE_STAT) {
      r = property_value_brack_value_stat(b, 0);
    }
    else if (t == PROPERTY_VALUE_CUSTOM_TYPE) {
      r = property_value_custom_type(b, 0);
    }
    else if (t == PROPERTY_VALUE_DOT_ID) {
      r = property_value_dot_id(b, 0);
    }
    else if (t == PROPERTY_VALUE_DOT_ID_NAME) {
      r = property_value_dot_id_name(b, 0);
    }
    else if (t == PROPERTY_VALUE_EXPR) {
      r = property_value_expr(b, 0);
    }
    else if (t == PROPERTY_VALUE_METHOD_CALL_STAT) {
      r = property_value_method_call_stat(b, 0);
    }
    else if (t == PROPERTY_VALUE_SUFFIX) {
      r = property_value_suffix(b, 0);
    }
    else if (t == PROPERTY_VALUE_THIS_TYPE) {
      r = property_value_this_type(b, 0);
    }
    else if (t == RETURN_EXPR) {
      r = return_expr(b, 0);
    }
    else if (t == STRUCT_DEC) {
      r = struct_dec(b, 0);
    }
    else if (t == STRUCT_EXTENDS_NAME_DEC) {
      r = struct_extends_name_dec(b, 0);
    }
    else if (t == STRUCT_EXTENDS_NAMESPACE_NAME_DEC) {
      r = struct_extends_namespace_name_dec(b, 0);
    }
    else if (t == STRUCT_NAME_DEC) {
      r = struct_name_dec(b, 0);
    }
    else if (t == STRUCT_VAR_DEC) {
      r = struct_var_dec(b, 0);
    }
    else if (t == STRUCT_VAR_NAME_DEC) {
      r = struct_var_name_dec(b, 0);
    }
    else if (t == VALUE_FACTOR) {
      r = value_factor(b, 0);
    }
    else if (t == VALUE_STAT) {
      r = value_stat(b, 0);
    }
    else if (t == VALUE_STAT_PAREN) {
      r = value_stat_paren(b, 0);
    }
    else if (t == VAR_ASSIGN_EXPR) {
      r = var_assign_expr(b, 0);
    }
    else if (t == VAR_ASSIGN_NAME_DEC) {
      r = var_assign_name_dec(b, 0);
    }
    else if (t == VAR_ASSIGN_PAIR_DEC) {
      r = var_assign_pair_dec(b, 0);
    }
    else if (t == WHILE_EXPR) {
      r = while_expr(b, 0);
    }
    else if (t == WRAP_EXPR) {
      r = wrap_expr(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return alittleFile(b, l + 1);
  }

  /* ********************************************************** */
  // public | private | protected
  public static boolean access_modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "access_modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACCESS_MODIFIER, "<access modifier>");
    r = consumeToken(b, PUBLIC);
    if (!r) r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, PROTECTED);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespace_dec?
  static boolean alittleFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alittleFile")) return false;
    namespace_dec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // var_assign_expr |
  //             op_assign_expr |
  //             op_1_expr |
  //             if_expr |
  //             for_expr |
  //             while_expr |
  //             do_while_expr |
  //             return_expr |
  //             flow_expr |
  //             wrap_expr |
  //             property_value_expr
  public static boolean all_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "all_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_EXPR, "<all expr>");
    r = var_assign_expr(b, l + 1);
    if (!r) r = op_assign_expr(b, l + 1);
    if (!r) r = op_1_expr(b, l + 1);
    if (!r) r = if_expr(b, l + 1);
    if (!r) r = for_expr(b, l + 1);
    if (!r) r = while_expr(b, l + 1);
    if (!r) r = do_while_expr(b, l + 1);
    if (!r) r = return_expr(b, l + 1);
    if (!r) r = flow_expr(b, l + 1);
    if (!r) r = wrap_expr(b, l + 1);
    if (!r) r = property_value_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // primitive_type | generic_type | custom_type
  public static boolean all_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "all_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALL_TYPE, "<all type>");
    r = primitive_type(b, l + 1);
    if (!r) r = generic_type(b, l + 1);
    if (!r) r = custom_type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // access_modifier
  public static boolean class_access_modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_access_modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_ACCESS_MODIFIER, "<class access modifier>");
    r = access_modifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? Ctor method_param_dec method_body_dec
  public static boolean class_ctor_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_ctor_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_CTOR_DEC, "<class ctor dec>");
    r = class_ctor_dec_0(b, l + 1);
    r = r && consumeToken(b, CTOR);
    p = r; // pin = 2
    r = r && report_error_(b, method_param_dec(b, l + 1));
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_ctor_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_ctor_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // class_access_modifier? class class_name_dec (COLON class_extends_access_modifier? (class_extends_namespace_name_dec DOT)? class_extends_name_dec)? LBRACE (class_var_dec | class_ctor_dec | class_getter_dec | class_setter_dec | class_static_dec | class_method_dec)* RBRACE
  public static boolean class_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_DEC, "<class dec>");
    r = class_dec_0(b, l + 1);
    r = r && consumeToken(b, CLASS);
    p = r; // pin = 2
    r = r && report_error_(b, class_name_dec(b, l + 1));
    r = p && report_error_(b, class_dec_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, class_dec_5(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // class_access_modifier?
  private static boolean class_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_0")) return false;
    class_access_modifier(b, l + 1);
    return true;
  }

  // (COLON class_extends_access_modifier? (class_extends_namespace_name_dec DOT)? class_extends_name_dec)?
  private static boolean class_dec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_3")) return false;
    class_dec_3_0(b, l + 1);
    return true;
  }

  // COLON class_extends_access_modifier? (class_extends_namespace_name_dec DOT)? class_extends_name_dec
  private static boolean class_dec_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && class_dec_3_0_1(b, l + 1);
    r = r && class_dec_3_0_2(b, l + 1);
    r = r && class_extends_name_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // class_extends_access_modifier?
  private static boolean class_dec_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_3_0_1")) return false;
    class_extends_access_modifier(b, l + 1);
    return true;
  }

  // (class_extends_namespace_name_dec DOT)?
  private static boolean class_dec_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_3_0_2")) return false;
    class_dec_3_0_2_0(b, l + 1);
    return true;
  }

  // class_extends_namespace_name_dec DOT
  private static boolean class_dec_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = class_extends_namespace_name_dec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (class_var_dec | class_ctor_dec | class_getter_dec | class_setter_dec | class_static_dec | class_method_dec)*
  private static boolean class_dec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!class_dec_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "class_dec_5", c)) break;
    }
    return true;
  }

  // class_var_dec | class_ctor_dec | class_getter_dec | class_setter_dec | class_static_dec | class_method_dec
  private static boolean class_dec_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_dec_5_0")) return false;
    boolean r;
    r = class_var_dec(b, l + 1);
    if (!r) r = class_ctor_dec(b, l + 1);
    if (!r) r = class_getter_dec(b, l + 1);
    if (!r) r = class_setter_dec(b, l + 1);
    if (!r) r = class_static_dec(b, l + 1);
    if (!r) r = class_method_dec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // access_modifier
  public static boolean class_extends_access_modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_extends_access_modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_EXTENDS_ACCESS_MODIFIER, "<class extends access modifier>");
    r = access_modifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean class_extends_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_extends_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_EXTENDS_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean class_extends_namespace_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_extends_namespace_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_EXTENDS_NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? get method_name_dec LPAREN RPAREN COLON method_return_type_dec method_body_dec
  public static boolean class_getter_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_getter_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_GETTER_DEC, "<class getter dec>");
    r = class_getter_dec_0(b, l + 1);
    r = r && consumeToken(b, GET);
    p = r; // pin = 2
    r = r && report_error_(b, method_name_dec(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, LPAREN, RPAREN, COLON)) && r;
    r = p && report_error_(b, method_return_type_dec(b, l + 1)) && r;
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_getter_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_getter_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // access_modifier? fun method_name_dec method_param_dec method_return_dec? method_body_dec
  public static boolean class_method_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_method_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_METHOD_DEC, "<class method dec>");
    r = class_method_dec_0(b, l + 1);
    r = r && consumeToken(b, FUN);
    p = r; // pin = 2
    r = r && report_error_(b, method_name_dec(b, l + 1));
    r = p && report_error_(b, method_param_dec(b, l + 1)) && r;
    r = p && report_error_(b, class_method_dec_4(b, l + 1)) && r;
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_method_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_method_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // method_return_dec?
  private static boolean class_method_dec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_method_dec_4")) return false;
    method_return_dec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean class_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? set method_name_dec LPAREN method_param_one_dec RPAREN method_body_dec
  public static boolean class_setter_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_setter_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_SETTER_DEC, "<class setter dec>");
    r = class_setter_dec_0(b, l + 1);
    r = r && consumeToken(b, SET);
    p = r; // pin = 2
    r = r && report_error_(b, method_name_dec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, method_param_one_dec(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_setter_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_setter_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // access_modifier? static method_name_dec method_param_dec method_return_dec? method_body_dec
  public static boolean class_static_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_static_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_STATIC_DEC, "<class static dec>");
    r = class_static_dec_0(b, l + 1);
    r = r && consumeToken(b, STATIC);
    p = r; // pin = 2
    r = r && report_error_(b, method_name_dec(b, l + 1));
    r = p && report_error_(b, method_param_dec(b, l + 1)) && r;
    r = p && report_error_(b, class_static_dec_4(b, l + 1)) && r;
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_static_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_static_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // method_return_dec?
  private static boolean class_static_dec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_static_dec_4")) return false;
    method_return_dec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // access_modifier? class_var_name_dec COLON all_type SEMI
  public static boolean class_var_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_var_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_VAR_DEC, "<class var dec>");
    r = class_var_dec_0(b, l + 1);
    r = r && class_var_name_dec(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && report_error_(b, all_type(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean class_var_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_var_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean class_var_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_var_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CLASS_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // true | false | STRING_CONTENT | DIGIT_CONTENT | null
  public static boolean const_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_value")) return false;
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
  // (custom_type_namespace_name_dec DOT)? custom_type_name_dec
  public static boolean custom_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_type")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = custom_type_0(b, l + 1);
    r = r && custom_type_name_dec(b, l + 1);
    exit_section_(b, m, CUSTOM_TYPE, r);
    return r;
  }

  // (custom_type_namespace_name_dec DOT)?
  private static boolean custom_type_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_type_0")) return false;
    custom_type_0_0(b, l + 1);
    return true;
  }

  // custom_type_namespace_name_dec DOT
  private static boolean custom_type_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_type_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = custom_type_namespace_name_dec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean custom_type_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_type_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CUSTOM_TYPE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean custom_type_namespace_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_type_namespace_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, CUSTOM_TYPE_NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // do private_do_while_body while private_do_while_condition SEMI
  public static boolean do_while_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_while_expr")) return false;
    if (!nextTokenIs(b, DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_WHILE_EXPR, null);
    r = consumeToken(b, DO);
    p = r; // pin = 1
    r = r && report_error_(b, private_do_while_body(b, l + 1));
    r = p && report_error_(b, consumeToken(b, WHILE)) && r;
    r = p && report_error_(b, private_do_while_condition(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // else (private_else_body | all_expr)
  public static boolean else_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_expr")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_EXPR, null);
    r = consumeToken(b, ELSE);
    p = r; // pin = 1
    r = r && else_expr_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_else_body | all_expr
  private static boolean else_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_expr_1")) return false;
    boolean r;
    r = private_else_body(b, l + 1);
    if (!r) r = all_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // elseif private_else_if_condition (private_else_if_body | all_expr)
  public static boolean else_if_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_if_expr")) return false;
    if (!nextTokenIs(b, ELSEIF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF_EXPR, null);
    r = consumeToken(b, ELSEIF);
    p = r; // pin = 1
    r = r && report_error_(b, private_else_if_condition(b, l + 1));
    r = p && else_if_expr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_else_if_body | all_expr
  private static boolean else_if_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_if_expr_2")) return false;
    boolean r;
    r = private_else_if_body(b, l + 1);
    if (!r) r = all_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? enum enum_name_dec LBRACE (enum_var_dec (COMMA enum_var_dec)* COMMA?)? RBRACE
  public static boolean enum_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_DEC, "<enum dec>");
    r = enum_dec_0(b, l + 1);
    r = r && consumeToken(b, ENUM);
    p = r; // pin = 2
    r = r && report_error_(b, enum_name_dec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, enum_dec_4(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean enum_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // (enum_var_dec (COMMA enum_var_dec)* COMMA?)?
  private static boolean enum_dec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_4")) return false;
    enum_dec_4_0(b, l + 1);
    return true;
  }

  // enum_var_dec (COMMA enum_var_dec)* COMMA?
  private static boolean enum_dec_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enum_var_dec(b, l + 1);
    r = r && enum_dec_4_0_1(b, l + 1);
    r = r && enum_dec_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA enum_var_dec)*
  private static boolean enum_dec_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_4_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enum_dec_4_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enum_dec_4_0_1", c)) break;
    }
    return true;
  }

  // COMMA enum_var_dec
  private static boolean enum_dec_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enum_var_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean enum_dec_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_dec_4_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean enum_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, ENUM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // enum_var_name_dec (ASSIGN enum_var_value_dec)?
  public static boolean enum_var_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_var_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_VAR_DEC, null);
    r = enum_var_name_dec(b, l + 1);
    p = r; // pin = 1
    r = r && enum_var_dec_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ASSIGN enum_var_value_dec)?
  private static boolean enum_var_dec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_var_dec_1")) return false;
    enum_var_dec_1_0(b, l + 1);
    return true;
  }

  // ASSIGN enum_var_value_dec
  private static boolean enum_var_dec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_var_dec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSIGN);
    r = r && enum_var_value_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean enum_var_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_var_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, ENUM_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // DIGIT_CONTENT | STRING_CONTENT
  public static boolean enum_var_value_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_var_value_dec")) return false;
    if (!nextTokenIs(b, "<enum var value dec>", DIGIT_CONTENT, STRING_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_VAR_VALUE_DEC, "<enum var value dec>");
    r = consumeToken(b, DIGIT_CONTENT);
    if (!r) r = consumeToken(b, STRING_CONTENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // break SEMI
  public static boolean flow_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flow_expr")) return false;
    if (!nextTokenIs(b, BREAK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FLOW_EXPR, null);
    r = consumeTokens(b, 1, BREAK, SEMI);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // value_stat
  public static boolean for_end_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_end_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_END_STAT, "<for end stat>");
    r = value_stat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // for private_for_condition (private_for_body | all_expr)
  public static boolean for_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_expr")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_EXPR, null);
    r = consumeToken(b, FOR);
    p = r; // pin = 1
    r = r && report_error_(b, private_for_condition(b, l + 1));
    r = p && for_expr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_for_body | all_expr
  private static boolean for_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_expr_2")) return false;
    boolean r;
    r = private_for_body(b, l + 1);
    if (!r) r = all_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // for_pair_dec (COMMA for_pair_dec)* in value_stat
  public static boolean for_in_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_in_condition")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_IN_CONDITION, null);
    r = for_pair_dec(b, l + 1);
    r = r && for_in_condition_1(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, consumeToken(b, IN));
    r = p && value_stat(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA for_pair_dec)*
  private static boolean for_in_condition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_in_condition_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!for_in_condition_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_in_condition_1", c)) break;
    }
    return true;
  }

  // COMMA for_pair_dec
  private static boolean for_in_condition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_in_condition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && for_pair_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // var_assign_name_dec COLON all_type
  public static boolean for_pair_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_pair_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = var_assign_name_dec(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && all_type(b, l + 1);
    exit_section_(b, m, FOR_PAIR_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // for_pair_dec ASSIGN value_stat
  public static boolean for_start_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_start_stat")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_START_STAT, null);
    r = for_pair_dec(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    p = r; // pin = 2
    r = r && value_stat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // for_start_stat COMMA for_end_stat COMMA for_step_stat
  public static boolean for_step_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_step_condition")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = for_start_stat(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && for_end_stat(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && for_step_stat(b, l + 1);
    exit_section_(b, m, FOR_STEP_CONDITION, r);
    return r;
  }

  /* ********************************************************** */
  // value_stat
  public static boolean for_step_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_step_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_STEP_STAT, "<for step stat>");
    r = value_stat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // all_type (COMMA all_type)*
  public static boolean generic_functor_param_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_param_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_PARAM_TYPE, "<generic functor param type>");
    r = all_type(b, l + 1);
    r = r && generic_functor_param_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA all_type)*
  private static boolean generic_functor_param_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_param_type_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!generic_functor_param_type_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "generic_functor_param_type_1", c)) break;
    }
    return true;
  }

  // COMMA all_type
  private static boolean generic_functor_param_type_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_param_type_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && all_type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON all_type (COMMA all_type)*
  public static boolean generic_functor_return_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_return_type")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && all_type(b, l + 1);
    r = r && generic_functor_return_type_2(b, l + 1);
    exit_section_(b, m, GENERIC_FUNCTOR_RETURN_TYPE, r);
    return r;
  }

  // (COMMA all_type)*
  private static boolean generic_functor_return_type_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_return_type_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!generic_functor_return_type_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "generic_functor_return_type_2", c)) break;
    }
    return true;
  }

  // COMMA all_type
  private static boolean generic_functor_return_type_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_return_type_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && all_type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Functor LESS LPAREN generic_functor_param_type? RPAREN generic_functor_return_type? GREATER
  public static boolean generic_functor_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_type")) return false;
    if (!nextTokenIs(b, FUNCTOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_FUNCTOR_TYPE, null);
    r = consumeTokens(b, 1, FUNCTOR, LESS, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, generic_functor_type_3(b, l + 1));
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    r = p && report_error_(b, generic_functor_type_5(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // generic_functor_param_type?
  private static boolean generic_functor_type_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_type_3")) return false;
    generic_functor_param_type(b, l + 1);
    return true;
  }

  // generic_functor_return_type?
  private static boolean generic_functor_type_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_functor_type_5")) return false;
    generic_functor_return_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // List LESS all_type GREATER
  public static boolean generic_list_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_list_type")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_LIST_TYPE, null);
    r = consumeTokens(b, 1, LIST, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, all_type(b, l + 1));
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Map LESS all_type COMMA all_type GREATER
  public static boolean generic_map_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_map_type")) return false;
    if (!nextTokenIs(b, MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_MAP_TYPE, null);
    r = consumeTokens(b, 1, MAP, LESS);
    p = r; // pin = 1
    r = r && report_error_(b, all_type(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COMMA)) && r;
    r = p && report_error_(b, all_type(b, l + 1)) && r;
    r = p && consumeToken(b, GREATER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // generic_list_type | generic_map_type | generic_functor_type
  public static boolean generic_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_TYPE, "<generic type>");
    r = generic_list_type(b, l + 1);
    if (!r) r = generic_map_type(b, l + 1);
    if (!r) r = generic_functor_type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? static method_name_dec method_param_dec method_return_dec? method_body_dec
  public static boolean global_method_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_method_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_METHOD_DEC, "<global method dec>");
    r = global_method_dec_0(b, l + 1);
    r = r && consumeToken(b, STATIC);
    p = r; // pin = 2
    r = r && report_error_(b, method_name_dec(b, l + 1));
    r = p && report_error_(b, method_param_dec(b, l + 1)) && r;
    r = p && report_error_(b, global_method_dec_4(b, l + 1)) && r;
    r = p && method_body_dec(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean global_method_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_method_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // method_return_dec?
  private static boolean global_method_dec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_method_dec_4")) return false;
    method_return_dec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // if private_if_condition (private_if_body | all_expr) else_if_expr* else_expr?
  public static boolean if_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expr")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_EXPR, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && report_error_(b, private_if_condition(b, l + 1));
    r = p && report_error_(b, if_expr_2(b, l + 1)) && r;
    r = p && report_error_(b, if_expr_3(b, l + 1)) && r;
    r = p && if_expr_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_if_body | all_expr
  private static boolean if_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expr_2")) return false;
    boolean r;
    r = private_if_body(b, l + 1);
    if (!r) r = all_expr(b, l + 1);
    return r;
  }

  // else_if_expr*
  private static boolean if_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!else_if_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_expr_3", c)) break;
    }
    return true;
  }

  // else_expr?
  private static boolean if_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expr_4")) return false;
    else_expr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean instance_class_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_class_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, INSTANCE_CLASS_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? instance instance_name_dec COLON all_type (ASSIGN new instance_class_name_dec LPAREN (value_stat (COMMA value_stat)*)? RPAREN)? SEMI
  public static boolean instance_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INSTANCE_DEC, "<instance dec>");
    r = instance_dec_0(b, l + 1);
    r = r && consumeToken(b, INSTANCE);
    p = r; // pin = 2
    r = r && report_error_(b, instance_name_dec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, all_type(b, l + 1)) && r;
    r = p && report_error_(b, instance_dec_5(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // access_modifier?
  private static boolean instance_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // (ASSIGN new instance_class_name_dec LPAREN (value_stat (COMMA value_stat)*)? RPAREN)?
  private static boolean instance_dec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5")) return false;
    instance_dec_5_0(b, l + 1);
    return true;
  }

  // ASSIGN new instance_class_name_dec LPAREN (value_stat (COMMA value_stat)*)? RPAREN
  private static boolean instance_dec_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ASSIGN, NEW);
    r = r && instance_class_name_dec(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && instance_dec_5_0_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (value_stat (COMMA value_stat)*)?
  private static boolean instance_dec_5_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5_0_4")) return false;
    instance_dec_5_0_4_0(b, l + 1);
    return true;
  }

  // value_stat (COMMA value_stat)*
  private static boolean instance_dec_5_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_stat(b, l + 1);
    r = r && instance_dec_5_0_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA value_stat)*
  private static boolean instance_dec_5_0_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5_0_4_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!instance_dec_5_0_4_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "instance_dec_5_0_4_0_1", c)) break;
    }
    return true;
  }

  // COMMA value_stat
  private static boolean instance_dec_5_0_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_dec_5_0_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && value_stat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean instance_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instance_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, INSTANCE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  public static boolean method_body_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_body_dec")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_BODY_DEC, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, method_body_dec_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean method_body_dec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_body_dec_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "method_body_dec_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean method_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, METHOD_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN (method_param_one_dec (COMMA method_param_one_dec)*)? RPAREN
  public static boolean method_param_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_dec")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_DEC, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, method_param_dec_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (method_param_one_dec (COMMA method_param_one_dec)*)?
  private static boolean method_param_dec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_dec_1")) return false;
    method_param_dec_1_0(b, l + 1);
    return true;
  }

  // method_param_one_dec (COMMA method_param_one_dec)*
  private static boolean method_param_dec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_dec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = method_param_one_dec(b, l + 1);
    r = r && method_param_dec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA method_param_one_dec)*
  private static boolean method_param_dec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_dec_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!method_param_dec_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "method_param_dec_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA method_param_one_dec
  private static boolean method_param_dec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_dec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && method_param_one_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean method_param_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, METHOD_PARAM_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // method_param_name_dec COLON method_param_type_dec
  public static boolean method_param_one_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_one_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = method_param_name_dec(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && method_param_type_dec(b, l + 1);
    exit_section_(b, m, METHOD_PARAM_ONE_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // all_type
  public static boolean method_param_type_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param_type_dec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM_TYPE_DEC, "<method param type dec>");
    r = all_type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COLON method_return_type_dec (COMMA method_return_type_dec)*
  public static boolean method_return_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_return_dec")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && method_return_type_dec(b, l + 1);
    r = r && method_return_dec_2(b, l + 1);
    exit_section_(b, m, METHOD_RETURN_DEC, r);
    return r;
  }

  // (COMMA method_return_type_dec)*
  private static boolean method_return_dec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_return_dec_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!method_return_dec_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "method_return_dec_2", c)) break;
    }
    return true;
  }

  // COMMA method_return_type_dec
  private static boolean method_return_dec_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_return_dec_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && method_return_type_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // all_type
  public static boolean method_return_type_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_return_type_dec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_RETURN_TYPE_DEC, "<method return type dec>");
    r = all_type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespace namespace_name_dec SEMI (global_method_dec | class_dec | enum_dec | struct_dec | instance_dec)*
  public static boolean namespace_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_dec")) return false;
    if (!nextTokenIs(b, NAMESPACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DEC, null);
    r = consumeToken(b, NAMESPACE);
    p = r; // pin = 1
    r = r && report_error_(b, namespace_name_dec(b, l + 1));
    r = p && report_error_(b, consumeToken(b, SEMI)) && r;
    r = p && namespace_dec_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (global_method_dec | class_dec | enum_dec | struct_dec | instance_dec)*
  private static boolean namespace_dec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_dec_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!namespace_dec_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespace_dec_3", c)) break;
    }
    return true;
  }

  // global_method_dec | class_dec | enum_dec | struct_dec | instance_dec
  private static boolean namespace_dec_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_dec_3_0")) return false;
    boolean r;
    r = global_method_dec(b, l + 1);
    if (!r) r = class_dec(b, l + 1);
    if (!r) r = enum_dec(b, l + 1);
    if (!r) r = struct_dec(b, l + 1);
    if (!r) r = instance_dec(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean namespace_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // PLUS_PLUS | MINUS_MINUS
  public static boolean op_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_1")) return false;
    if (!nextTokenIs(b, "<op 1>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_1, "<op 1>");
    r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_1 value_stat SEMI
  public static boolean op_1_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_1_expr")) return false;
    if (!nextTokenIs(b, "<op 1 expr>", MINUS_MINUS, PLUS_PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_1_EXPR, "<op 1 expr>");
    r = op_1(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, value_stat(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NOT | MINUS
  public static boolean op_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_2")) return false;
    if (!nextTokenIs(b, "<op 2>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2, "<op 2>");
    r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_2_value op_2_suffix_ex*
  public static boolean op_2_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_2_stat")) return false;
    if (!nextTokenIs(b, "<op 2 stat>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_STAT, "<op 2 stat>");
    r = op_2_value(b, l + 1);
    r = r && op_2_stat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_2_suffix_ex*
  private static boolean op_2_stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_2_stat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_2_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_2_stat_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix | op_5_suffix | op_6_suffix | op_7_suffix | op_8_suffix
  public static boolean op_2_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_2_suffix_ex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_SUFFIX_EX, "<op 2 suffix ex>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_2 value_factor
  public static boolean op_2_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_2_value")) return false;
    if (!nextTokenIs(b, "<op 2 value>", MINUS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_2_VALUE, "<op 2 value>");
    r = op_2(b, l + 1);
    r = r && value_factor(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MUL | QUOTIENT | REMAINDER
  public static boolean op_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3, "<op 3>");
    r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, REMAINDER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value_factor op_3_suffix op_3_suffix_ex*
  public static boolean op_3_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_STAT, "<op 3 stat>");
    r = value_factor(b, l + 1);
    r = r && op_3_suffix(b, l + 1);
    r = r && op_3_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_3_suffix_ex*
  private static boolean op_3_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_3_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_3_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3 (value_factor | op_2_value)
  public static boolean op_3_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3_suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX, "<op 3 suffix>");
    r = op_3(b, l + 1);
    r = r && op_3_suffix_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_3_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix | op_5_suffix | op_6_suffix | op_7_suffix | op_8_suffix
  public static boolean op_3_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_3_suffix_ex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_3_SUFFIX_EX, "<op 3 suffix ex>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PLUS | MINUS
  public static boolean op_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4")) return false;
    if (!nextTokenIs(b, "<op 4>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4, "<op 4>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value_factor op_4_suffix op_4_suffix_ex*
  public static boolean op_4_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_STAT, "<op 4 stat>");
    r = value_factor(b, l + 1);
    r = r && op_4_suffix(b, l + 1);
    r = r && op_4_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_4_suffix_ex*
  private static boolean op_4_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_4_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_4_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_4 (value_factor | op_2_value) op_4_suffix_ee*
  public static boolean op_4_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_suffix")) return false;
    if (!nextTokenIs(b, "<op 4 suffix>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX, "<op 4 suffix>");
    r = op_4(b, l + 1);
    r = r && op_4_suffix_1(b, l + 1);
    r = r && op_4_suffix_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_4_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  // op_4_suffix_ee*
  private static boolean op_4_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_4_suffix_ee(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_4_suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix
  public static boolean op_4_suffix_ee(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_suffix_ee")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EE, "<op 4 suffix ee>");
    r = op_3_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_4_suffix | op_5_suffix | op_6_suffix | op_7_suffix | op_8_suffix
  public static boolean op_4_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_4_suffix_ex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_4_SUFFIX_EX, "<op 4 suffix ex>");
    r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CONCAT
  public static boolean op_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONCAT);
    exit_section_(b, m, OP_5, r);
    return r;
  }

  /* ********************************************************** */
  // value_factor op_5_suffix op_5_suffix_ex*
  public static boolean op_5_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_STAT, "<op 5 stat>");
    r = value_factor(b, l + 1);
    r = r && op_5_suffix(b, l + 1);
    r = r && op_5_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_5_suffix_ex*
  private static boolean op_5_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_5_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_5_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_5 (value_factor | op_2_value) op_5_suffix_ee*
  public static boolean op_5_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_suffix")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_5(b, l + 1);
    r = r && op_5_suffix_1(b, l + 1);
    r = r && op_5_suffix_2(b, l + 1);
    exit_section_(b, m, OP_5_SUFFIX, r);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_5_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  // op_5_suffix_ee*
  private static boolean op_5_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_5_suffix_ee(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_5_suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix
  public static boolean op_5_suffix_ee(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_suffix_ee")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EE, "<op 5 suffix ee>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_5_suffix | op_6_suffix | op_7_suffix | op_8_suffix
  public static boolean op_5_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_5_suffix_ex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_5_SUFFIX_EX, "<op 5 suffix ex>");
    r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LESS | LESS_OR_EQUAL | GREATER | GREATER_OR_EQUAL | EQ | NOT_EQ
  public static boolean op_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6")) return false;
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
  // value_factor op_6_suffix op_6_suffix_ex*
  public static boolean op_6_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_STAT, "<op 6 stat>");
    r = value_factor(b, l + 1);
    r = r && op_6_suffix(b, l + 1);
    r = r && op_6_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_6_suffix_ex*
  private static boolean op_6_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_6_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_6_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_6 (value_factor | op_2_value) op_6_suffix_ee*
  public static boolean op_6_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX, "<op 6 suffix>");
    r = op_6(b, l + 1);
    r = r && op_6_suffix_1(b, l + 1);
    r = r && op_6_suffix_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_6_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  // op_6_suffix_ee*
  private static boolean op_6_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_6_suffix_ee(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_6_suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix | op_5_suffix
  public static boolean op_6_suffix_ee(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_suffix_ee")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EE, "<op 6 suffix ee>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_6_suffix | op_7_suffix | op_8_suffix
  public static boolean op_6_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_6_suffix_ex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_6_SUFFIX_EX, "<op 6 suffix ex>");
    r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COND_AND
  public static boolean op_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_AND);
    exit_section_(b, m, OP_7, r);
    return r;
  }

  /* ********************************************************** */
  // value_factor op_7_suffix op_7_suffix_ex*
  public static boolean op_7_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_STAT, "<op 7 stat>");
    r = value_factor(b, l + 1);
    r = r && op_7_suffix(b, l + 1);
    r = r && op_7_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_7_suffix_ex*
  private static boolean op_7_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_7_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_7_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_7 (value_factor | op_2_value) op_7_suffix_ee*
  public static boolean op_7_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_suffix")) return false;
    if (!nextTokenIs(b, COND_AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_7(b, l + 1);
    r = r && op_7_suffix_1(b, l + 1);
    r = r && op_7_suffix_2(b, l + 1);
    exit_section_(b, m, OP_7_SUFFIX, r);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_7_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  // op_7_suffix_ee*
  private static boolean op_7_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_7_suffix_ee(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_7_suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix | op_5_suffix | op_6_suffix
  public static boolean op_7_suffix_ee(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_suffix_ee")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EE, "<op 7 suffix ee>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_7_suffix | op_8_suffix
  public static boolean op_7_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_7_suffix_ex")) return false;
    if (!nextTokenIs(b, "<op 7 suffix ex>", COND_AND, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_7_SUFFIX_EX, "<op 7 suffix ex>");
    r = op_7_suffix(b, l + 1);
    if (!r) r = op_8_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COND_OR
  public static boolean op_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COND_OR);
    exit_section_(b, m, OP_8, r);
    return r;
  }

  /* ********************************************************** */
  // value_factor op_8_suffix op_8_suffix_ex*
  public static boolean op_8_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_8_STAT, "<op 8 stat>");
    r = value_factor(b, l + 1);
    r = r && op_8_suffix(b, l + 1);
    r = r && op_8_stat_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op_8_suffix_ex*
  private static boolean op_8_stat_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_stat_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_8_suffix_ex(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_8_stat_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_8 (value_factor | op_2_value) op_8_suffix_ee*
  public static boolean op_8_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_suffix")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_8(b, l + 1);
    r = r && op_8_suffix_1(b, l + 1);
    r = r && op_8_suffix_2(b, l + 1);
    exit_section_(b, m, OP_8_SUFFIX, r);
    return r;
  }

  // value_factor | op_2_value
  private static boolean op_8_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_suffix_1")) return false;
    boolean r;
    r = value_factor(b, l + 1);
    if (!r) r = op_2_value(b, l + 1);
    return r;
  }

  // op_8_suffix_ee*
  private static boolean op_8_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_suffix_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_8_suffix_ee(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_8_suffix_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // op_3_suffix | op_4_suffix | op_5_suffix | op_6_suffix | op_7_suffix
  public static boolean op_8_suffix_ee(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_suffix_ee")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_8_SUFFIX_EE, "<op 8 suffix ee>");
    r = op_3_suffix(b, l + 1);
    if (!r) r = op_4_suffix(b, l + 1);
    if (!r) r = op_5_suffix(b, l + 1);
    if (!r) r = op_6_suffix(b, l + 1);
    if (!r) r = op_7_suffix(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_8_suffix
  public static boolean op_8_suffix_ex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_8_suffix_ex")) return false;
    if (!nextTokenIs(b, COND_OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_8_suffix(b, l + 1);
    exit_section_(b, m, OP_8_SUFFIX_EX, r);
    return r;
  }

  /* ********************************************************** */
  // ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | QUOTIENT_ASSIGN | REMAINDER_ASSIGN
  public static boolean op_assign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign")) return false;
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
  // property_value ((COMMA property_value)* op_assign value_stat)? SEMI
  public static boolean op_assign_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign_expr")) return false;
    if (!nextTokenIs(b, "<op assign expr>", ID_CONTENT, THIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_ASSIGN_EXPR, "<op assign expr>");
    r = property_value(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, op_assign_expr_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((COMMA property_value)* op_assign value_stat)?
  private static boolean op_assign_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign_expr_1")) return false;
    op_assign_expr_1_0(b, l + 1);
    return true;
  }

  // (COMMA property_value)* op_assign value_stat
  private static boolean op_assign_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_assign_expr_1_0_0(b, l + 1);
    r = r && op_assign(b, l + 1);
    r = r && value_stat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA property_value)*
  private static boolean op_assign_expr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign_expr_1_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_assign_expr_1_0_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_assign_expr_1_0_0", c)) break;
    }
    return true;
  }

  // COMMA property_value
  private static boolean op_assign_expr_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_assign_expr_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && property_value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // new (custom_type | generic_type) LPAREN (value_stat (COMMA value_stat)*)? RPAREN
  public static boolean op_new_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OP_NEW_STAT, null);
    r = consumeToken(b, NEW);
    p = r; // pin = 1
    r = r && report_error_(b, op_new_stat_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LPAREN)) && r;
    r = p && report_error_(b, op_new_stat_3(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // custom_type | generic_type
  private static boolean op_new_stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat_1")) return false;
    boolean r;
    r = custom_type(b, l + 1);
    if (!r) r = generic_type(b, l + 1);
    return r;
  }

  // (value_stat (COMMA value_stat)*)?
  private static boolean op_new_stat_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat_3")) return false;
    op_new_stat_3_0(b, l + 1);
    return true;
  }

  // value_stat (COMMA value_stat)*
  private static boolean op_new_stat_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_stat(b, l + 1);
    r = r && op_new_stat_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA value_stat)*
  private static boolean op_new_stat_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!op_new_stat_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "op_new_stat_3_0_1", c)) break;
    }
    return true;
  }

  // COMMA value_stat
  private static boolean op_new_stat_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_new_stat_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && value_stat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // bool | double | int | any | string
  public static boolean primitive_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitive_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMITIVE_TYPE, "<primitive type>");
    r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, DOUBLE);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ASSIGN value_stat
  static boolean private_ASSIGN_value_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_ASSIGN_value_stat")) return false;
    if (!nextTokenIs(b, ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && value_stat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COLON all_type
  static boolean private_COLON_all_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_COLON_all_type")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && all_type(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COMMA var_assign_pair_dec
  static boolean private_COMMA_var_assign_pair_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_COMMA_var_assign_pair_dec")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && var_assign_pair_dec(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_do_while_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_do_while_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_do_while_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_do_while_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_do_while_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_do_while_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN value_stat RPAREN
  static boolean private_do_while_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_do_while_condition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, value_stat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_else_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_else_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_else_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_else_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_else_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_else_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_else_if_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_else_if_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_else_if_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_else_if_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_else_if_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_else_if_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN value_stat RPAREN
  static boolean private_else_if_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_else_if_condition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, value_stat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_for_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_for_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_for_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_for_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_for_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_for_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN (for_step_condition | for_in_condition) RPAREN
  static boolean private_for_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_for_condition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, private_for_condition_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // for_step_condition | for_in_condition
  private static boolean private_for_condition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_for_condition_1")) return false;
    boolean r;
    r = for_step_condition(b, l + 1);
    if (!r) r = for_in_condition(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_if_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_if_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_if_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_if_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_if_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_if_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN value_stat RPAREN
  static boolean private_if_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_if_condition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, value_stat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  static boolean private_while_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_while_body")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, private_while_body_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean private_while_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_while_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "private_while_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPAREN value_stat RPAREN
  static boolean private_while_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_while_condition")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, value_stat(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (property_value_custom_type | property_value_this_type) property_value_suffix*
  public static boolean property_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value")) return false;
    if (!nextTokenIs(b, "<property value>", ID_CONTENT, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE, "<property value>");
    r = property_value_0(b, l + 1);
    r = r && property_value_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // property_value_custom_type | property_value_this_type
  private static boolean property_value_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_0")) return false;
    boolean r;
    r = property_value_custom_type(b, l + 1);
    if (!r) r = property_value_this_type(b, l + 1);
    return r;
  }

  // property_value_suffix*
  private static boolean property_value_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_value_suffix(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_value_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACK value_stat RBRACK
  public static boolean property_value_brack_value_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_brack_value_stat")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACK);
    r = r && value_stat(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, PROPERTY_VALUE_BRACK_VALUE_STAT, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean property_value_custom_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_custom_type")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, PROPERTY_VALUE_CUSTOM_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // DOT property_value_dot_id_name
  public static boolean property_value_dot_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_dot_id")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && property_value_dot_id_name(b, l + 1);
    exit_section_(b, m, PROPERTY_VALUE_DOT_ID, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean property_value_dot_id_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_dot_id_name")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, PROPERTY_VALUE_DOT_ID_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // property_value SEMI
  public static boolean property_value_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_expr")) return false;
    if (!nextTokenIs(b, "<property value expr>", ID_CONTENT, THIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_EXPR, "<property value expr>");
    r = property_value(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LPAREN (value_stat (COMMA value_stat)*)? RPAREN
  public static boolean property_value_method_call_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_method_call_stat")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_METHOD_CALL_STAT, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, property_value_method_call_stat_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (value_stat (COMMA value_stat)*)?
  private static boolean property_value_method_call_stat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_method_call_stat_1")) return false;
    property_value_method_call_stat_1_0(b, l + 1);
    return true;
  }

  // value_stat (COMMA value_stat)*
  private static boolean property_value_method_call_stat_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_method_call_stat_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = value_stat(b, l + 1);
    p = r; // pin = 1
    r = r && property_value_method_call_stat_1_0_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA value_stat)*
  private static boolean property_value_method_call_stat_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_method_call_stat_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_value_method_call_stat_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_value_method_call_stat_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA value_stat
  private static boolean property_value_method_call_stat_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_method_call_stat_1_0_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && value_stat(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // property_value_dot_id | property_value_brack_value_stat | property_value_method_call_stat
  public static boolean property_value_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_VALUE_SUFFIX, "<property value suffix>");
    r = property_value_dot_id(b, l + 1);
    if (!r) r = property_value_brack_value_stat(b, l + 1);
    if (!r) r = property_value_method_call_stat(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // this
  public static boolean property_value_this_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_value_this_type")) return false;
    if (!nextTokenIs(b, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, THIS);
    exit_section_(b, m, PROPERTY_VALUE_THIS_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // return (value_stat (COMMA value_stat)*)? SEMI
  public static boolean return_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_expr")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_EXPR, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, return_expr_1(b, l + 1));
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (value_stat (COMMA value_stat)*)?
  private static boolean return_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_expr_1")) return false;
    return_expr_1_0(b, l + 1);
    return true;
  }

  // value_stat (COMMA value_stat)*
  private static boolean return_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_stat(b, l + 1);
    r = r && return_expr_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA value_stat)*
  private static boolean return_expr_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_expr_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!return_expr_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_expr_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA value_stat
  private static boolean return_expr_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_expr_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && value_stat(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // access_modifier? struct struct_name_dec (COLON (struct_extends_namespace_name_dec DOT)? struct_extends_name_dec)? LBRACE (struct_var_dec)* RBRACE
  public static boolean struct_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_DEC, "<struct dec>");
    r = struct_dec_0(b, l + 1);
    r = r && consumeToken(b, STRUCT);
    r = r && struct_name_dec(b, l + 1);
    r = r && struct_dec_3(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && struct_dec_5(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // access_modifier?
  private static boolean struct_dec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_0")) return false;
    access_modifier(b, l + 1);
    return true;
  }

  // (COLON (struct_extends_namespace_name_dec DOT)? struct_extends_name_dec)?
  private static boolean struct_dec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_3")) return false;
    struct_dec_3_0(b, l + 1);
    return true;
  }

  // COLON (struct_extends_namespace_name_dec DOT)? struct_extends_name_dec
  private static boolean struct_dec_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && struct_dec_3_0_1(b, l + 1);
    r = r && struct_extends_name_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (struct_extends_namespace_name_dec DOT)?
  private static boolean struct_dec_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_3_0_1")) return false;
    struct_dec_3_0_1_0(b, l + 1);
    return true;
  }

  // struct_extends_namespace_name_dec DOT
  private static boolean struct_dec_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_extends_namespace_name_dec(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (struct_var_dec)*
  private static boolean struct_dec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!struct_dec_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "struct_dec_5", c)) break;
    }
    return true;
  }

  // (struct_var_dec)
  private static boolean struct_dec_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_dec_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_var_dec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean struct_extends_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_extends_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_EXTENDS_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean struct_extends_namespace_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_extends_namespace_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_EXTENDS_NAMESPACE_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean struct_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // struct_var_name_dec COLON all_type SEMI
  public static boolean struct_var_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_var_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_VAR_DEC, null);
    r = struct_var_name_dec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && report_error_(b, all_type(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean struct_var_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_var_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, STRUCT_VAR_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // value_stat_paren | const_value | property_value
  public static boolean value_factor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_factor")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_FACTOR, "<value factor>");
    r = value_stat_paren(b, l + 1);
    if (!r) r = const_value(b, l + 1);
    if (!r) r = property_value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // op_new_stat | op_3_stat | op_2_stat | op_4_stat | op_5_stat | op_6_stat | op_7_stat | op_8_stat | value_factor
  public static boolean value_stat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_stat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_STAT, "<value stat>");
    r = op_new_stat(b, l + 1);
    if (!r) r = op_3_stat(b, l + 1);
    if (!r) r = op_2_stat(b, l + 1);
    if (!r) r = op_4_stat(b, l + 1);
    if (!r) r = op_5_stat(b, l + 1);
    if (!r) r = op_6_stat(b, l + 1);
    if (!r) r = op_7_stat(b, l + 1);
    if (!r) r = op_8_stat(b, l + 1);
    if (!r) r = value_factor(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN value_stat RPAREN
  public static boolean value_stat_paren(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_stat_paren")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && value_stat(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, VALUE_STAT_PAREN, r);
    return r;
  }

  /* ********************************************************** */
  // var var_assign_pair_dec private_COMMA_var_assign_pair_dec* private_ASSIGN_value_stat? SEMI
  public static boolean var_assign_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_assign_expr")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VAR_ASSIGN_EXPR, null);
    r = consumeToken(b, VAR);
    p = r; // pin = 1
    r = r && report_error_(b, var_assign_pair_dec(b, l + 1));
    r = p && report_error_(b, var_assign_expr_2(b, l + 1)) && r;
    r = p && report_error_(b, var_assign_expr_3(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_COMMA_var_assign_pair_dec*
  private static boolean var_assign_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_assign_expr_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!private_COMMA_var_assign_pair_dec(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "var_assign_expr_2", c)) break;
    }
    return true;
  }

  // private_ASSIGN_value_stat?
  private static boolean var_assign_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_assign_expr_3")) return false;
    private_ASSIGN_value_stat(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID_CONTENT
  public static boolean var_assign_name_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_assign_name_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID_CONTENT);
    exit_section_(b, m, VAR_ASSIGN_NAME_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // var_assign_name_dec private_COLON_all_type
  public static boolean var_assign_pair_dec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_assign_pair_dec")) return false;
    if (!nextTokenIs(b, ID_CONTENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = var_assign_name_dec(b, l + 1);
    r = r && private_COLON_all_type(b, l + 1);
    exit_section_(b, m, VAR_ASSIGN_PAIR_DEC, r);
    return r;
  }

  /* ********************************************************** */
  // while private_while_condition (private_while_body | all_expr)
  public static boolean while_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_expr")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_EXPR, null);
    r = consumeToken(b, WHILE);
    p = r; // pin = 1
    r = r && report_error_(b, private_while_condition(b, l + 1));
    r = p && while_expr_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_while_body | all_expr
  private static boolean while_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_expr_2")) return false;
    boolean r;
    r = private_while_body(b, l + 1);
    if (!r) r = all_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LBRACE all_expr* RBRACE
  public static boolean wrap_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wrap_expr")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRAP_EXPR, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, wrap_expr_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // all_expr*
  private static boolean wrap_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wrap_expr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!all_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "wrap_expr_1", c)) break;
    }
    return true;
  }

}
