// This is a generated file. Not intended for manual editing.
package plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import plugin.psi.impl.*;

public interface ALittleTypes {

  IElementType ACCESS_MODIFIER = new ALittleElementType("ACCESS_MODIFIER");
  IElementType ALL_EXPR = new ALittleElementType("ALL_EXPR");
  IElementType ALL_TYPE = new ALittleElementType("ALL_TYPE");
  IElementType AUTO_TYPE = new ALittleElementType("AUTO_TYPE");
  IElementType BIND_STAT = new ALittleElementType("BIND_STAT");
  IElementType CLASS_CTOR_DEC = new ALittleElementType("CLASS_CTOR_DEC");
  IElementType CLASS_DEC = new ALittleElementType("CLASS_DEC");
  IElementType CLASS_EXTENDS_DEC = new ALittleElementType("CLASS_EXTENDS_DEC");
  IElementType CLASS_GETTER_DEC = new ALittleElementType("CLASS_GETTER_DEC");
  IElementType CLASS_METHOD_DEC = new ALittleElementType("CLASS_METHOD_DEC");
  IElementType CLASS_NAME_DEC = new ALittleElementType("CLASS_NAME_DEC");
  IElementType CLASS_SETTER_DEC = new ALittleElementType("CLASS_SETTER_DEC");
  IElementType CLASS_STATIC_DEC = new ALittleElementType("CLASS_STATIC_DEC");
  IElementType CLASS_VAR_DEC = new ALittleElementType("CLASS_VAR_DEC");
  IElementType CONST_VALUE = new ALittleElementType("CONST_VALUE");
  IElementType CO_MODIFIER = new ALittleElementType("CO_MODIFIER");
  IElementType CUSTOM_TYPE = new ALittleElementType("CUSTOM_TYPE");
  IElementType CUSTOM_TYPE_DOT_ID = new ALittleElementType("CUSTOM_TYPE_DOT_ID");
  IElementType CUSTOM_TYPE_DOT_ID_NAME = new ALittleElementType("CUSTOM_TYPE_DOT_ID_NAME");
  IElementType DO_WHILE_EXPR = new ALittleElementType("DO_WHILE_EXPR");
  IElementType ELSE_EXPR = new ALittleElementType("ELSE_EXPR");
  IElementType ELSE_IF_EXPR = new ALittleElementType("ELSE_IF_EXPR");
  IElementType EMPTY_EXPR = new ALittleElementType("EMPTY_EXPR");
  IElementType ENUM_DEC = new ALittleElementType("ENUM_DEC");
  IElementType ENUM_NAME_DEC = new ALittleElementType("ENUM_NAME_DEC");
  IElementType ENUM_VAR_DEC = new ALittleElementType("ENUM_VAR_DEC");
  IElementType FLOW_EXPR = new ALittleElementType("FLOW_EXPR");
  IElementType FOR_END_STAT = new ALittleElementType("FOR_END_STAT");
  IElementType FOR_EXPR = new ALittleElementType("FOR_EXPR");
  IElementType FOR_IN_CONDITION = new ALittleElementType("FOR_IN_CONDITION");
  IElementType FOR_PAIR_DEC = new ALittleElementType("FOR_PAIR_DEC");
  IElementType FOR_START_STAT = new ALittleElementType("FOR_START_STAT");
  IElementType FOR_STEP_CONDITION = new ALittleElementType("FOR_STEP_CONDITION");
  IElementType FOR_STEP_STAT = new ALittleElementType("FOR_STEP_STAT");
  IElementType GENERIC_FUNCTOR_PARAM_TYPE = new ALittleElementType("GENERIC_FUNCTOR_PARAM_TYPE");
  IElementType GENERIC_FUNCTOR_RETURN_TYPE = new ALittleElementType("GENERIC_FUNCTOR_RETURN_TYPE");
  IElementType GENERIC_FUNCTOR_TYPE = new ALittleElementType("GENERIC_FUNCTOR_TYPE");
  IElementType GENERIC_LIST_TYPE = new ALittleElementType("GENERIC_LIST_TYPE");
  IElementType GENERIC_MAP_TYPE = new ALittleElementType("GENERIC_MAP_TYPE");
  IElementType GENERIC_TYPE = new ALittleElementType("GENERIC_TYPE");
  IElementType GLOBAL_METHOD_DEC = new ALittleElementType("GLOBAL_METHOD_DEC");
  IElementType IF_EXPR = new ALittleElementType("IF_EXPR");
  IElementType INSTANCE_DEC = new ALittleElementType("INSTANCE_DEC");
  IElementType METHOD_BODY_DEC = new ALittleElementType("METHOD_BODY_DEC");
  IElementType METHOD_NAME_DEC = new ALittleElementType("METHOD_NAME_DEC");
  IElementType METHOD_PARAM_DEC = new ALittleElementType("METHOD_PARAM_DEC");
  IElementType METHOD_PARAM_NAME_DEC = new ALittleElementType("METHOD_PARAM_NAME_DEC");
  IElementType METHOD_PARAM_ONE_DEC = new ALittleElementType("METHOD_PARAM_ONE_DEC");
  IElementType METHOD_PARAM_TAIL_DEC = new ALittleElementType("METHOD_PARAM_TAIL_DEC");
  IElementType METHOD_RETURN_DEC = new ALittleElementType("METHOD_RETURN_DEC");
  IElementType NAMESPACE_DEC = new ALittleElementType("NAMESPACE_DEC");
  IElementType NAMESPACE_NAME_DEC = new ALittleElementType("NAMESPACE_NAME_DEC");
  IElementType NCALL_STAT = new ALittleElementType("NCALL_STAT");
  IElementType OP_1 = new ALittleElementType("OP_1");
  IElementType OP_1_EXPR = new ALittleElementType("OP_1_EXPR");
  IElementType OP_2 = new ALittleElementType("OP_2");
  IElementType OP_2_STAT = new ALittleElementType("OP_2_STAT");
  IElementType OP_2_SUFFIX_EX = new ALittleElementType("OP_2_SUFFIX_EX");
  IElementType OP_2_VALUE = new ALittleElementType("OP_2_VALUE");
  IElementType OP_3 = new ALittleElementType("OP_3");
  IElementType OP_3_STAT = new ALittleElementType("OP_3_STAT");
  IElementType OP_3_SUFFIX = new ALittleElementType("OP_3_SUFFIX");
  IElementType OP_3_SUFFIX_EX = new ALittleElementType("OP_3_SUFFIX_EX");
  IElementType OP_4 = new ALittleElementType("OP_4");
  IElementType OP_4_STAT = new ALittleElementType("OP_4_STAT");
  IElementType OP_4_SUFFIX = new ALittleElementType("OP_4_SUFFIX");
  IElementType OP_4_SUFFIX_EE = new ALittleElementType("OP_4_SUFFIX_EE");
  IElementType OP_4_SUFFIX_EX = new ALittleElementType("OP_4_SUFFIX_EX");
  IElementType OP_5 = new ALittleElementType("OP_5");
  IElementType OP_5_STAT = new ALittleElementType("OP_5_STAT");
  IElementType OP_5_SUFFIX = new ALittleElementType("OP_5_SUFFIX");
  IElementType OP_5_SUFFIX_EE = new ALittleElementType("OP_5_SUFFIX_EE");
  IElementType OP_5_SUFFIX_EX = new ALittleElementType("OP_5_SUFFIX_EX");
  IElementType OP_6 = new ALittleElementType("OP_6");
  IElementType OP_6_STAT = new ALittleElementType("OP_6_STAT");
  IElementType OP_6_SUFFIX = new ALittleElementType("OP_6_SUFFIX");
  IElementType OP_6_SUFFIX_EE = new ALittleElementType("OP_6_SUFFIX_EE");
  IElementType OP_6_SUFFIX_EX = new ALittleElementType("OP_6_SUFFIX_EX");
  IElementType OP_7 = new ALittleElementType("OP_7");
  IElementType OP_7_STAT = new ALittleElementType("OP_7_STAT");
  IElementType OP_7_SUFFIX = new ALittleElementType("OP_7_SUFFIX");
  IElementType OP_7_SUFFIX_EE = new ALittleElementType("OP_7_SUFFIX_EE");
  IElementType OP_7_SUFFIX_EX = new ALittleElementType("OP_7_SUFFIX_EX");
  IElementType OP_8 = new ALittleElementType("OP_8");
  IElementType OP_8_STAT = new ALittleElementType("OP_8_STAT");
  IElementType OP_8_SUFFIX = new ALittleElementType("OP_8_SUFFIX");
  IElementType OP_8_SUFFIX_EE = new ALittleElementType("OP_8_SUFFIX_EE");
  IElementType OP_8_SUFFIX_EX = new ALittleElementType("OP_8_SUFFIX_EX");
  IElementType OP_ASSIGN = new ALittleElementType("OP_ASSIGN");
  IElementType OP_ASSIGN_EXPR = new ALittleElementType("OP_ASSIGN_EXPR");
  IElementType OP_NEW_LIST_STAT = new ALittleElementType("OP_NEW_LIST_STAT");
  IElementType OP_NEW_STAT = new ALittleElementType("OP_NEW_STAT");
  IElementType PCALL_STAT = new ALittleElementType("PCALL_STAT");
  IElementType PRIMITIVE_TYPE = new ALittleElementType("PRIMITIVE_TYPE");
  IElementType PROPERTY_VALUE = new ALittleElementType("PROPERTY_VALUE");
  IElementType PROPERTY_VALUE_BRACKET_VALUE = new ALittleElementType("PROPERTY_VALUE_BRACKET_VALUE");
  IElementType PROPERTY_VALUE_CAST_TYPE = new ALittleElementType("PROPERTY_VALUE_CAST_TYPE");
  IElementType PROPERTY_VALUE_CUSTOM_TYPE = new ALittleElementType("PROPERTY_VALUE_CUSTOM_TYPE");
  IElementType PROPERTY_VALUE_DOT_ID = new ALittleElementType("PROPERTY_VALUE_DOT_ID");
  IElementType PROPERTY_VALUE_DOT_ID_NAME = new ALittleElementType("PROPERTY_VALUE_DOT_ID_NAME");
  IElementType PROPERTY_VALUE_EXPR = new ALittleElementType("PROPERTY_VALUE_EXPR");
  IElementType PROPERTY_VALUE_FIRST_TYPE = new ALittleElementType("PROPERTY_VALUE_FIRST_TYPE");
  IElementType PROPERTY_VALUE_METHOD_CALL = new ALittleElementType("PROPERTY_VALUE_METHOD_CALL");
  IElementType PROPERTY_VALUE_SUFFIX = new ALittleElementType("PROPERTY_VALUE_SUFFIX");
  IElementType PROPERTY_VALUE_THIS_TYPE = new ALittleElementType("PROPERTY_VALUE_THIS_TYPE");
  IElementType PROTO_MODIFIER = new ALittleElementType("PROTO_MODIFIER");
  IElementType REFLECT_VALUE = new ALittleElementType("REFLECT_VALUE");
  IElementType REGISTER_MODIFIER = new ALittleElementType("REGISTER_MODIFIER");
  IElementType RETURN_EXPR = new ALittleElementType("RETURN_EXPR");
  IElementType RETURN_YIELD = new ALittleElementType("RETURN_YIELD");
  IElementType STRUCT_DEC = new ALittleElementType("STRUCT_DEC");
  IElementType STRUCT_EXTENDS_DEC = new ALittleElementType("STRUCT_EXTENDS_DEC");
  IElementType STRUCT_NAME_DEC = new ALittleElementType("STRUCT_NAME_DEC");
  IElementType STRUCT_VAR_DEC = new ALittleElementType("STRUCT_VAR_DEC");
  IElementType TEMPLATE_DEC = new ALittleElementType("TEMPLATE_DEC");
  IElementType TEMPLATE_PAIR_DEC = new ALittleElementType("TEMPLATE_PAIR_DEC");
  IElementType USING_DEC = new ALittleElementType("USING_DEC");
  IElementType USING_NAME_DEC = new ALittleElementType("USING_NAME_DEC");
  IElementType VALUE_FACTOR_STAT = new ALittleElementType("VALUE_FACTOR_STAT");
  IElementType VALUE_STAT = new ALittleElementType("VALUE_STAT");
  IElementType VAR_ASSIGN_DEC = new ALittleElementType("VAR_ASSIGN_DEC");
  IElementType VAR_ASSIGN_EXPR = new ALittleElementType("VAR_ASSIGN_EXPR");
  IElementType VAR_ASSIGN_NAME_DEC = new ALittleElementType("VAR_ASSIGN_NAME_DEC");
  IElementType WHILE_EXPR = new ALittleElementType("WHILE_EXPR");
  IElementType WRAP_EXPR = new ALittleElementType("WRAP_EXPR");
  IElementType WRAP_VALUE_STAT = new ALittleElementType("WRAP_VALUE_STAT");

  IElementType ANY = new ALittleTokenType("any");
  IElementType APOS = new ALittleTokenType("'");
  IElementType ASSIGN = new ALittleTokenType("=");
  IElementType ASYNC = new ALittleTokenType("async");
  IElementType AUTO = new ALittleTokenType("auto");
  IElementType AWAIT = new ALittleTokenType("await");
  IElementType BACK = new ALittleTokenType("\\");
  IElementType BIND = new ALittleTokenType("bind");
  IElementType BOOL = new ALittleTokenType("bool");
  IElementType BREAK = new ALittleTokenType("break");
  IElementType CAST = new ALittleTokenType("cast");
  IElementType CLASS = new ALittleTokenType("class");
  IElementType COLON = new ALittleTokenType(":");
  IElementType COMMA = new ALittleTokenType(",");
  IElementType COMMENT = new ALittleTokenType("COMMENT");
  IElementType CONCAT = new ALittleTokenType("..");
  IElementType COND_AND = new ALittleTokenType("&&");
  IElementType COND_OR = new ALittleTokenType("||");
  IElementType CTOR = new ALittleTokenType("Ctor");
  IElementType DIGIT_CONTENT = new ALittleTokenType("DIGIT_CONTENT");
  IElementType DO = new ALittleTokenType("do");
  IElementType DOT = new ALittleTokenType(".");
  IElementType DOUBLE = new ALittleTokenType("double");
  IElementType ELSE = new ALittleTokenType("else");
  IElementType ELSEIF = new ALittleTokenType("elseif");
  IElementType ENUM = new ALittleTokenType("enum");
  IElementType EQ = new ALittleTokenType("==");
  IElementType FALSE = new ALittleTokenType("false");
  IElementType FOR = new ALittleTokenType("for");
  IElementType FUN = new ALittleTokenType("fun");
  IElementType FUNCTOR = new ALittleTokenType("Functor");
  IElementType GET = new ALittleTokenType("get");
  IElementType GREATER = new ALittleTokenType(">");
  IElementType GREATER_OR_EQUAL = new ALittleTokenType(">=");
  IElementType HTTP = new ALittleTokenType("@Http");
  IElementType HTTP_DOWNLOAD = new ALittleTokenType("@HttpDownload");
  IElementType HTTP_UPLOAD = new ALittleTokenType("@HttpUpload");
  IElementType I64 = new ALittleTokenType("I64");
  IElementType ID_CONTENT = new ALittleTokenType("ID_CONTENT");
  IElementType IF = new ALittleTokenType("if");
  IElementType IN = new ALittleTokenType("in");
  IElementType INT = new ALittleTokenType("int");
  IElementType LBRACE = new ALittleTokenType("{");
  IElementType LBRACK = new ALittleTokenType("[");
  IElementType LESS = new ALittleTokenType("<");
  IElementType LESS_OR_EQUAL = new ALittleTokenType("<=");
  IElementType LIST = new ALittleTokenType("List");
  IElementType LPAREN = new ALittleTokenType("(");
  IElementType MAP = new ALittleTokenType("Map");
  IElementType MINUS = new ALittleTokenType("-");
  IElementType MINUS_ASSIGN = new ALittleTokenType("-=");
  IElementType MINUS_MINUS = new ALittleTokenType("--");
  IElementType MSG = new ALittleTokenType("@Msg");
  IElementType MUL = new ALittleTokenType("*");
  IElementType MUL_ASSIGN = new ALittleTokenType("*=");
  IElementType NAMESPACE = new ALittleTokenType("namespace");
  IElementType NCALL = new ALittleTokenType("ncall");
  IElementType NEW = new ALittleTokenType("new");
  IElementType NOT = new ALittleTokenType("!");
  IElementType NOT_EQ = new ALittleTokenType("!=");
  IElementType NULL = new ALittleTokenType("null");
  IElementType PARAM_TAIL = new ALittleTokenType("...");
  IElementType PCALL = new ALittleTokenType("pcall");
  IElementType PLUS = new ALittleTokenType("+");
  IElementType PLUS_ASSIGN = new ALittleTokenType("+=");
  IElementType PLUS_PLUS = new ALittleTokenType("++");
  IElementType PRIVATE = new ALittleTokenType("private");
  IElementType PROTECTED = new ALittleTokenType("protected");
  IElementType PUBLIC = new ALittleTokenType("public");
  IElementType QUOTE = new ALittleTokenType("\"");
  IElementType QUOTIENT = new ALittleTokenType("/");
  IElementType QUOTIENT_ASSIGN = new ALittleTokenType("/=");
  IElementType RBRACE = new ALittleTokenType("}");
  IElementType RBRACK = new ALittleTokenType("]");
  IElementType REFLECT = new ALittleTokenType("reflect");
  IElementType REGISTER = new ALittleTokenType("register");
  IElementType REMAINDER = new ALittleTokenType("%");
  IElementType REMAINDER_ASSIGN = new ALittleTokenType("%=");
  IElementType RETURN = new ALittleTokenType("return");
  IElementType RPAREN = new ALittleTokenType(")");
  IElementType SEMI = new ALittleTokenType(";");
  IElementType SET = new ALittleTokenType("set");
  IElementType STATIC = new ALittleTokenType("static");
  IElementType STRING = new ALittleTokenType("string");
  IElementType STRING_CONTENT = new ALittleTokenType("STRING_CONTENT");
  IElementType STRUCT = new ALittleTokenType("struct");
  IElementType THIS = new ALittleTokenType("this");
  IElementType TRUE = new ALittleTokenType("true");
  IElementType USING = new ALittleTokenType("using");
  IElementType WHILE = new ALittleTokenType("while");
  IElementType YIELD = new ALittleTokenType("yield");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ACCESS_MODIFIER) {
        return new ALittleAccessModifierImpl(node);
      }
      else if (type == ALL_EXPR) {
        return new ALittleAllExprImpl(node);
      }
      else if (type == ALL_TYPE) {
        return new ALittleAllTypeImpl(node);
      }
      else if (type == AUTO_TYPE) {
        return new ALittleAutoTypeImpl(node);
      }
      else if (type == BIND_STAT) {
        return new ALittleBindStatImpl(node);
      }
      else if (type == CLASS_CTOR_DEC) {
        return new ALittleClassCtorDecImpl(node);
      }
      else if (type == CLASS_DEC) {
        return new ALittleClassDecImpl(node);
      }
      else if (type == CLASS_EXTENDS_DEC) {
        return new ALittleClassExtendsDecImpl(node);
      }
      else if (type == CLASS_GETTER_DEC) {
        return new ALittleClassGetterDecImpl(node);
      }
      else if (type == CLASS_METHOD_DEC) {
        return new ALittleClassMethodDecImpl(node);
      }
      else if (type == CLASS_NAME_DEC) {
        return new ALittleClassNameDecImpl(node);
      }
      else if (type == CLASS_SETTER_DEC) {
        return new ALittleClassSetterDecImpl(node);
      }
      else if (type == CLASS_STATIC_DEC) {
        return new ALittleClassStaticDecImpl(node);
      }
      else if (type == CLASS_VAR_DEC) {
        return new ALittleClassVarDecImpl(node);
      }
      else if (type == CONST_VALUE) {
        return new ALittleConstValueImpl(node);
      }
      else if (type == CO_MODIFIER) {
        return new ALittleCoModifierImpl(node);
      }
      else if (type == CUSTOM_TYPE) {
        return new ALittleCustomTypeImpl(node);
      }
      else if (type == CUSTOM_TYPE_DOT_ID) {
        return new ALittleCustomTypeDotIdImpl(node);
      }
      else if (type == CUSTOM_TYPE_DOT_ID_NAME) {
        return new ALittleCustomTypeDotIdNameImpl(node);
      }
      else if (type == DO_WHILE_EXPR) {
        return new ALittleDoWhileExprImpl(node);
      }
      else if (type == ELSE_EXPR) {
        return new ALittleElseExprImpl(node);
      }
      else if (type == ELSE_IF_EXPR) {
        return new ALittleElseIfExprImpl(node);
      }
      else if (type == EMPTY_EXPR) {
        return new ALittleEmptyExprImpl(node);
      }
      else if (type == ENUM_DEC) {
        return new ALittleEnumDecImpl(node);
      }
      else if (type == ENUM_NAME_DEC) {
        return new ALittleEnumNameDecImpl(node);
      }
      else if (type == ENUM_VAR_DEC) {
        return new ALittleEnumVarDecImpl(node);
      }
      else if (type == FLOW_EXPR) {
        return new ALittleFlowExprImpl(node);
      }
      else if (type == FOR_END_STAT) {
        return new ALittleForEndStatImpl(node);
      }
      else if (type == FOR_EXPR) {
        return new ALittleForExprImpl(node);
      }
      else if (type == FOR_IN_CONDITION) {
        return new ALittleForInConditionImpl(node);
      }
      else if (type == FOR_PAIR_DEC) {
        return new ALittleForPairDecImpl(node);
      }
      else if (type == FOR_START_STAT) {
        return new ALittleForStartStatImpl(node);
      }
      else if (type == FOR_STEP_CONDITION) {
        return new ALittleForStepConditionImpl(node);
      }
      else if (type == FOR_STEP_STAT) {
        return new ALittleForStepStatImpl(node);
      }
      else if (type == GENERIC_FUNCTOR_PARAM_TYPE) {
        return new ALittleGenericFunctorParamTypeImpl(node);
      }
      else if (type == GENERIC_FUNCTOR_RETURN_TYPE) {
        return new ALittleGenericFunctorReturnTypeImpl(node);
      }
      else if (type == GENERIC_FUNCTOR_TYPE) {
        return new ALittleGenericFunctorTypeImpl(node);
      }
      else if (type == GENERIC_LIST_TYPE) {
        return new ALittleGenericListTypeImpl(node);
      }
      else if (type == GENERIC_MAP_TYPE) {
        return new ALittleGenericMapTypeImpl(node);
      }
      else if (type == GENERIC_TYPE) {
        return new ALittleGenericTypeImpl(node);
      }
      else if (type == GLOBAL_METHOD_DEC) {
        return new ALittleGlobalMethodDecImpl(node);
      }
      else if (type == IF_EXPR) {
        return new ALittleIfExprImpl(node);
      }
      else if (type == INSTANCE_DEC) {
        return new ALittleInstanceDecImpl(node);
      }
      else if (type == METHOD_BODY_DEC) {
        return new ALittleMethodBodyDecImpl(node);
      }
      else if (type == METHOD_NAME_DEC) {
        return new ALittleMethodNameDecImpl(node);
      }
      else if (type == METHOD_PARAM_DEC) {
        return new ALittleMethodParamDecImpl(node);
      }
      else if (type == METHOD_PARAM_NAME_DEC) {
        return new ALittleMethodParamNameDecImpl(node);
      }
      else if (type == METHOD_PARAM_ONE_DEC) {
        return new ALittleMethodParamOneDecImpl(node);
      }
      else if (type == METHOD_PARAM_TAIL_DEC) {
        return new ALittleMethodParamTailDecImpl(node);
      }
      else if (type == METHOD_RETURN_DEC) {
        return new ALittleMethodReturnDecImpl(node);
      }
      else if (type == NAMESPACE_DEC) {
        return new ALittleNamespaceDecImpl(node);
      }
      else if (type == NAMESPACE_NAME_DEC) {
        return new ALittleNamespaceNameDecImpl(node);
      }
      else if (type == NCALL_STAT) {
        return new ALittleNcallStatImpl(node);
      }
      else if (type == OP_1) {
        return new ALittleOp1Impl(node);
      }
      else if (type == OP_1_EXPR) {
        return new ALittleOp1ExprImpl(node);
      }
      else if (type == OP_2) {
        return new ALittleOp2Impl(node);
      }
      else if (type == OP_2_STAT) {
        return new ALittleOp2StatImpl(node);
      }
      else if (type == OP_2_SUFFIX_EX) {
        return new ALittleOp2SuffixExImpl(node);
      }
      else if (type == OP_2_VALUE) {
        return new ALittleOp2ValueImpl(node);
      }
      else if (type == OP_3) {
        return new ALittleOp3Impl(node);
      }
      else if (type == OP_3_STAT) {
        return new ALittleOp3StatImpl(node);
      }
      else if (type == OP_3_SUFFIX) {
        return new ALittleOp3SuffixImpl(node);
      }
      else if (type == OP_3_SUFFIX_EX) {
        return new ALittleOp3SuffixExImpl(node);
      }
      else if (type == OP_4) {
        return new ALittleOp4Impl(node);
      }
      else if (type == OP_4_STAT) {
        return new ALittleOp4StatImpl(node);
      }
      else if (type == OP_4_SUFFIX) {
        return new ALittleOp4SuffixImpl(node);
      }
      else if (type == OP_4_SUFFIX_EE) {
        return new ALittleOp4SuffixEeImpl(node);
      }
      else if (type == OP_4_SUFFIX_EX) {
        return new ALittleOp4SuffixExImpl(node);
      }
      else if (type == OP_5) {
        return new ALittleOp5Impl(node);
      }
      else if (type == OP_5_STAT) {
        return new ALittleOp5StatImpl(node);
      }
      else if (type == OP_5_SUFFIX) {
        return new ALittleOp5SuffixImpl(node);
      }
      else if (type == OP_5_SUFFIX_EE) {
        return new ALittleOp5SuffixEeImpl(node);
      }
      else if (type == OP_5_SUFFIX_EX) {
        return new ALittleOp5SuffixExImpl(node);
      }
      else if (type == OP_6) {
        return new ALittleOp6Impl(node);
      }
      else if (type == OP_6_STAT) {
        return new ALittleOp6StatImpl(node);
      }
      else if (type == OP_6_SUFFIX) {
        return new ALittleOp6SuffixImpl(node);
      }
      else if (type == OP_6_SUFFIX_EE) {
        return new ALittleOp6SuffixEeImpl(node);
      }
      else if (type == OP_6_SUFFIX_EX) {
        return new ALittleOp6SuffixExImpl(node);
      }
      else if (type == OP_7) {
        return new ALittleOp7Impl(node);
      }
      else if (type == OP_7_STAT) {
        return new ALittleOp7StatImpl(node);
      }
      else if (type == OP_7_SUFFIX) {
        return new ALittleOp7SuffixImpl(node);
      }
      else if (type == OP_7_SUFFIX_EE) {
        return new ALittleOp7SuffixEeImpl(node);
      }
      else if (type == OP_7_SUFFIX_EX) {
        return new ALittleOp7SuffixExImpl(node);
      }
      else if (type == OP_8) {
        return new ALittleOp8Impl(node);
      }
      else if (type == OP_8_STAT) {
        return new ALittleOp8StatImpl(node);
      }
      else if (type == OP_8_SUFFIX) {
        return new ALittleOp8SuffixImpl(node);
      }
      else if (type == OP_8_SUFFIX_EE) {
        return new ALittleOp8SuffixEeImpl(node);
      }
      else if (type == OP_8_SUFFIX_EX) {
        return new ALittleOp8SuffixExImpl(node);
      }
      else if (type == OP_ASSIGN) {
        return new ALittleOpAssignImpl(node);
      }
      else if (type == OP_ASSIGN_EXPR) {
        return new ALittleOpAssignExprImpl(node);
      }
      else if (type == OP_NEW_LIST_STAT) {
        return new ALittleOpNewListStatImpl(node);
      }
      else if (type == OP_NEW_STAT) {
        return new ALittleOpNewStatImpl(node);
      }
      else if (type == PCALL_STAT) {
        return new ALittlePcallStatImpl(node);
      }
      else if (type == PRIMITIVE_TYPE) {
        return new ALittlePrimitiveTypeImpl(node);
      }
      else if (type == PROPERTY_VALUE) {
        return new ALittlePropertyValueImpl(node);
      }
      else if (type == PROPERTY_VALUE_BRACKET_VALUE) {
        return new ALittlePropertyValueBracketValueImpl(node);
      }
      else if (type == PROPERTY_VALUE_CAST_TYPE) {
        return new ALittlePropertyValueCastTypeImpl(node);
      }
      else if (type == PROPERTY_VALUE_CUSTOM_TYPE) {
        return new ALittlePropertyValueCustomTypeImpl(node);
      }
      else if (type == PROPERTY_VALUE_DOT_ID) {
        return new ALittlePropertyValueDotIdImpl(node);
      }
      else if (type == PROPERTY_VALUE_DOT_ID_NAME) {
        return new ALittlePropertyValueDotIdNameImpl(node);
      }
      else if (type == PROPERTY_VALUE_EXPR) {
        return new ALittlePropertyValueExprImpl(node);
      }
      else if (type == PROPERTY_VALUE_FIRST_TYPE) {
        return new ALittlePropertyValueFirstTypeImpl(node);
      }
      else if (type == PROPERTY_VALUE_METHOD_CALL) {
        return new ALittlePropertyValueMethodCallImpl(node);
      }
      else if (type == PROPERTY_VALUE_SUFFIX) {
        return new ALittlePropertyValueSuffixImpl(node);
      }
      else if (type == PROPERTY_VALUE_THIS_TYPE) {
        return new ALittlePropertyValueThisTypeImpl(node);
      }
      else if (type == PROTO_MODIFIER) {
        return new ALittleProtoModifierImpl(node);
      }
      else if (type == REFLECT_VALUE) {
        return new ALittleReflectValueImpl(node);
      }
      else if (type == REGISTER_MODIFIER) {
        return new ALittleRegisterModifierImpl(node);
      }
      else if (type == RETURN_EXPR) {
        return new ALittleReturnExprImpl(node);
      }
      else if (type == RETURN_YIELD) {
        return new ALittleReturnYieldImpl(node);
      }
      else if (type == STRUCT_DEC) {
        return new ALittleStructDecImpl(node);
      }
      else if (type == STRUCT_EXTENDS_DEC) {
        return new ALittleStructExtendsDecImpl(node);
      }
      else if (type == STRUCT_NAME_DEC) {
        return new ALittleStructNameDecImpl(node);
      }
      else if (type == STRUCT_VAR_DEC) {
        return new ALittleStructVarDecImpl(node);
      }
      else if (type == TEMPLATE_DEC) {
        return new ALittleTemplateDecImpl(node);
      }
      else if (type == TEMPLATE_PAIR_DEC) {
        return new ALittleTemplatePairDecImpl(node);
      }
      else if (type == USING_DEC) {
        return new ALittleUsingDecImpl(node);
      }
      else if (type == USING_NAME_DEC) {
        return new ALittleUsingNameDecImpl(node);
      }
      else if (type == VALUE_FACTOR_STAT) {
        return new ALittleValueFactorStatImpl(node);
      }
      else if (type == VALUE_STAT) {
        return new ALittleValueStatImpl(node);
      }
      else if (type == VAR_ASSIGN_DEC) {
        return new ALittleVarAssignDecImpl(node);
      }
      else if (type == VAR_ASSIGN_EXPR) {
        return new ALittleVarAssignExprImpl(node);
      }
      else if (type == VAR_ASSIGN_NAME_DEC) {
        return new ALittleVarAssignNameDecImpl(node);
      }
      else if (type == WHILE_EXPR) {
        return new ALittleWhileExprImpl(node);
      }
      else if (type == WRAP_EXPR) {
        return new ALittleWrapExprImpl(node);
      }
      else if (type == WRAP_VALUE_STAT) {
        return new ALittleWrapValueStatImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
