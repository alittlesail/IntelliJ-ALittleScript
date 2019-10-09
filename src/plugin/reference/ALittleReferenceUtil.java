package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleReferenceUtil {
    // 定义异常
    public static class ALittleReferenceException extends Exception {
        private String mError;
        private PsiElement mElement;

        public ALittleReferenceException(@NotNull PsiElement element, @NotNull String error) {
            mElement = element;
            mError = error;
        }

        @NotNull
        public String getError() { return mError; }

        @NotNull
        public PsiElement getElement() { return mElement; }
    }

    // 基本变量类型
    public static GuessTypeInfo sIntGuessTypeInfo;
    public static GuessTypeInfo sDoubleGuessTypeInfo;
    public static GuessTypeInfo sStringGuessTypeInfo;
    public static GuessTypeInfo sBoolGuessTypeInfo;
    public static GuessTypeInfo sI64GuessTypeInfo;
    public static GuessTypeInfo sAnyGuessTypeInfo;
    public static Map<String, List<GuessTypeInfo>> sPrimitiveGuessTypeMap;
    public static List<GuessTypeInfo> sConstNullGuessType;
    static {
        sPrimitiveGuessTypeMap = new HashMap<>();
        List<GuessTypeInfo> tmp;
        tmp = new ArrayList<>(); sIntGuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "int"); tmp.add(sIntGuessTypeInfo); sPrimitiveGuessTypeMap.put("int", tmp);
        tmp = new ArrayList<>(); sDoubleGuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "double"); tmp.add(sDoubleGuessTypeInfo); sPrimitiveGuessTypeMap.put("double", tmp);
        tmp = new ArrayList<>(); sStringGuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "string"); tmp.add(sStringGuessTypeInfo); sPrimitiveGuessTypeMap.put("string", tmp);
        tmp = new ArrayList<>(); sBoolGuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "bool"); tmp.add(sBoolGuessTypeInfo); sPrimitiveGuessTypeMap.put("bool", tmp);
        tmp = new ArrayList<>(); sI64GuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "I64"); tmp.add(sI64GuessTypeInfo); sPrimitiveGuessTypeMap.put("I64", tmp);
        tmp = new ArrayList<>(); sAnyGuessTypeInfo = new GuessTypeInfo(GuessType.GT_PRIMITIVE, "any"); tmp.add(sAnyGuessTypeInfo); sPrimitiveGuessTypeMap.put("any", tmp);
        sConstNullGuessType = new ArrayList<>(); sConstNullGuessType.add(new GuessTypeInfo(GuessType.GT_CONST, "null"));
    }

    // 定义类型
    public enum GuessType
    {
        GT_CONST,
        GT_PRIMITIVE,
        GT_MAP,
        GT_LIST,
        GT_FUNCTOR,
        GT_NAMESPACE,
        GT_NAMESPACE_NAME,
        GT_CLASS,
        GT_CLASS_NAME,
        GT_STRUCT,
        GT_STRUCT_NAME,
        GT_ENUM,
        GT_ENUM_NAME,
        GT_PARAM_TAIL,
        GT_CLASS_TEMPLATE,            // 声明的模板
    }

    // 类型信息
    public static class GuessTypeInfo
    {
        public GuessTypeInfo() {}
        public GuessTypeInfo(GuessType t, String v) { type = t; value = v; }

        public boolean isChanged() {
            if (listSubType != null && listSubType.isChanged()) {
                return true;
            }

            if (mapKeyType != null && mapKeyType.isChanged()) {
                return true;
            }

            if (mapValueType != null && mapValueType.isChanged()) {
                return true;
            }

            if (functorParamList != null) {
                for (ALittleReferenceUtil.GuessTypeInfo paramInfo : functorParamList) {
                    if (paramInfo.isChanged()) return true;
                }
            }
            if (functorReturnList != null) {
                for (ALittleReferenceUtil.GuessTypeInfo returnInfo : functorReturnList) {
                    if (returnInfo.isChanged()) return true;
                }
            }

            if (classTemplateExtends != null && classTemplateExtends.isChanged()) {
                return true;
            }

            if (classTemplateList != null) {
                for (ALittleReferenceUtil.GuessTypeInfo classTemplateInfo : classTemplateList) {
                    if (classTemplateInfo.isChanged()) return true;
                }
            }
            if (classTemplateMap != null) {
                for (ALittleReferenceUtil.GuessTypeInfo classTemplateInfo : classTemplateMap.values()) {
                    if (classTemplateInfo.isChanged()) return true;
                }
            }

            if (element != null && ALittleTreeChangeListener.getGuessTypeList(element) != null) {
                return false;
            }

            return true;
        }

        public GuessType type;
        public String value;                              // 完整类型的字符串
        public PsiElement element;                        // 指向的元素
        public GuessTypeInfo classTemplateExtends;        // type="GT_CLASS_TEMPLATE"时，类模板标识符继承的类
        public List<GuessTypeInfo> classTemplateList;     // type="GT_CLASS"时，定义的模板列表
        public Map<String, GuessTypeInfo> classTemplateMap; // type="GT_CLASS"时，属于实例化模板时的类型映射, KEY是模板定义名，Value是实例对象
        public GuessTypeInfo listSubType;                 // type="List"时，表示List的子类型
        public GuessTypeInfo mapKeyType;                  // type="Map"时, 表示Map的Key
        public GuessTypeInfo mapValueType;                // type="Map"时, 表示Map的Value
        public List<GuessTypeInfo> functorParamList;      // type="Functor"时, 表示参数列表
        public List<String> functorParamNameList;         // type="Functor"时, 表示参数名列表
        public GuessTypeInfo functorParamTail;            // type="Functor"时, 表示参数占位符
        public List<GuessTypeInfo> functorReturnList;     // type="Functor"时, 表示返回值列表
        public boolean functorAwait;                      // type="Functor"时, 表示是否是await
    }

    // 计算表达式需要使用什么样的变量方式
    @NotNull
    public static String CalcPairsType(ALittleValueStat valueStat) throws ALittleReferenceException {
        List<GuessTypeInfo> guessTypeList = valueStat.guessTypes();
        // 必须是模板容器
        if (guessTypeList.size() == 1 && guessTypeList.get(0).type == GuessType.GT_LIST) {
            return "___ipairs";
        } else if (guessTypeList.size() == 1 && guessTypeList.get(0).type == GuessType.GT_MAP) {
            return "___pairs";
        }

        // 检查迭代函数
        do {
            if (guessTypeList.size() != 3) break;
            if (guessTypeList.get(0).type != ALittleReferenceUtil.GuessType.GT_FUNCTOR) break;
            if (guessTypeList.get(0).functorAwait) break;
            if (guessTypeList.get(0).functorParamList.size() != 2) break;
            if (guessTypeList.get(0).functorReturnList.size() != 0) break;
            if (!guessTypeList.get(0).functorParamList.get(0).value.equals(guessTypeList.get(1).value)) break;
            if (!guessTypeList.get(0).functorParamList.get(1).value.equals(guessTypeList.get(2).value)) break;
            return "";
        } while (false);

        throw new ALittleReferenceException(valueStat, "该表达式不能遍历");
    }

    // 判断 parent是否是child的父类
    public static boolean IsClassSuper(PsiElement child, PsiElement parent) throws ALittleReferenceException {
        if (!(child instanceof ALittleClassDec)) return false;
        ALittleClassDec childClass = (ALittleClassDec)child;

        ALittleClassExtendsDec extendsDec = childClass.getClassExtendsDec();
        if (extendsDec == null) return false;

        ALittleClassNameDec nameDec = extendsDec.getClassNameDec();
        if (nameDec == null) return false;

        GuessTypeInfo guessType = nameDec.guessType();
        if (guessType.element.equals(parent)) return true;

        return IsClassSuper(guessType.element, parent);
    }

    // 判断
    public static boolean IsClassSuper(PsiElement child, @NotNull String parent) throws ALittleReferenceException {
        if (!(child instanceof ALittleClassDec)) return false;
        ALittleClassDec childClass = (ALittleClassDec)child;

        ALittleClassExtendsDec extendsDec = childClass.getClassExtendsDec();
        if (extendsDec == null) return false;

        ALittleClassNameDec nameDec = extendsDec.getClassNameDec();
        if (nameDec == null) return false;

        GuessTypeInfo guessType = nameDec.guessType();
        if (guessType.value.equals(parent)) return true;

        return IsClassSuper(guessType.element, parent);
    }

    // 判断 parent是否是child的父类
    public static boolean IsStructSuper(PsiElement child, PsiElement parent) throws ALittleReferenceException {
        if (!(child instanceof ALittleStructDec)) return false;
        ALittleStructDec childStruct = (ALittleStructDec)child;

        ALittleStructExtendsDec extendsDec = childStruct.getStructExtendsDec();
        if (extendsDec == null) return false;

        ALittleStructNameDec nameDec = extendsDec.getStructNameDec();
        if (nameDec == null) return false;

        GuessTypeInfo guessType = nameDec.guessType();
        if (guessType.element.equals(parent)) return true;

        return IsStructSuper(guessType.element, parent);
    }

    // 创建引用对象
    public static ALittleReference create(PsiElement element) {
        TextRange range = new TextRange(0, element.getText().length());

        if (element instanceof ALittleAllType) return new ALittleAllTypeReference((ALittleAllType)element, range);
        if (element instanceof ALittleAutoType) return new ALittleAutoTypeReference((ALittleAutoType)element, range);
        if (element instanceof ALittleBindStat) return new ALittleBindStatReference((ALittleBindStat)element, range);

        if (element instanceof ALittleClassCtorDec) return new ALittleClassCtorDecReference((ALittleClassCtorDec)element, range);
        if (element instanceof ALittleClassDec) return new ALittleClassDecReference((ALittleClassDec)element, range);
        if (element instanceof ALittleClassNameDec) return new ALittleClassNameDecReference((ALittleClassNameDec)element, range);
        if (element instanceof ALittleClassVarDec) return new ALittleClassVarDecReference((ALittleClassVarDec)element, range);

        if (element instanceof ALittleConstValue) return new ALittleConstValueReference((ALittleConstValue)element, range);
        if (element instanceof ALittleCustomType) return new ALittleCustomTypeReference((ALittleCustomType)element, range);
        if (element instanceof ALittleCustomTypeDotIdName) return new ALittleCustomTypeDotIdNameReference((ALittleCustomTypeDotIdName)element, range);

        if (element instanceof ALittleEnumDec) return new ALittleEnumDecReference((ALittleEnumDec)element, range);
        if (element instanceof ALittleEnumNameDec) return new ALittleEnumNameDecReference((ALittleEnumNameDec)element, range);
        if (element instanceof ALittleEnumVarDec) return new ALittleEnumVarDecReference((ALittleEnumVarDec)element, range);

        if (element instanceof ALittleForExpr) return new ALittleForExprReference((ALittleForExpr)element, range);
        if (element instanceof ALittleForPairDec) return new ALittleForPairDecReference((ALittleForPairDec)element, range);
        if (element instanceof ALittleGenericType) return new ALittleGenericTypeReference((ALittleGenericType)element, range);
        if (element instanceof ALittleGlobalMethodDec) return new ALittleGlobalMethodDecReference((ALittleGlobalMethodDec)element, range);
        if (element instanceof ALittleMethodNameDec) return new ALittleMethodNameDecReference((ALittleMethodNameDec)element, range);
        if (element instanceof ALittleMethodParamNameDec) return new ALittleMethodParamNameDecReference((ALittleMethodParamNameDec)element, range);
        if (element instanceof ALittleMethodParamTailDec) return new ALittleMethodParamTailDecReference((ALittleMethodParamTailDec)element, range);

        if (element instanceof ALittleNamespaceDec) return new ALittleNamespaceDecReference((ALittleNamespaceDec)element, range);
        if (element instanceof ALittleNamespaceNameDec) return new ALittleNamespaceNameDecReference((ALittleNamespaceNameDec)element, range);

        if (element instanceof ALittleOpAssignExpr) return new ALittleOpAssignExprReference((ALittleOpAssignExpr)element, range);
        if (element instanceof ALittleOpNewListStat) return new ALittleOpNewListStatReference((ALittleOpNewListStat)element, range);
        if (element instanceof ALittleOpNewStat) return new ALittleOpNewStatReference((ALittleOpNewStat)element, range);
        if (element instanceof ALittleTcallStat) return new ALittleTcallStatReference((ALittleTcallStat)element, range);
        if (element instanceof ALittlePrimitiveType) return new ALittlePrimitiveTypeReference((ALittlePrimitiveType)element, range);

        if (element instanceof ALittlePropertyValueBracketValue) return new ALittlePropertyValueBracketValueReference((ALittlePropertyValueBracketValue)element, range);
        if (element instanceof ALittlePropertyValueCastType) return new ALittlePropertyValueCastTypeReference((ALittlePropertyValueCastType)element, range);
        if (element instanceof ALittlePropertyValueCustomType) return new ALittlePropertyValueCustomTypeReference((ALittlePropertyValueCustomType)element, range);
        if (element instanceof ALittlePropertyValueDotIdName) return new ALittlePropertyValueDotIdNameReference((ALittlePropertyValueDotIdName)element, range);
        if (element instanceof ALittlePropertyValueDotId) return new ALittlePropertyValueDotIdReference((ALittlePropertyValueDotId)element, range);
        if (element instanceof ALittlePropertyValueFirstType) return new ALittlePropertyValueFirstTypeReference((ALittlePropertyValueFirstType)element, range);
        if (element instanceof ALittlePropertyValueMethodCall) return new ALittlePropertyValueMethodCallReference((ALittlePropertyValueMethodCall)element, range);
        if (element instanceof ALittlePropertyValue) return new ALittlePropertyValueReference((ALittlePropertyValue)element, range);
        if (element instanceof ALittlePropertyValueSuffix) return new ALittlePropertyValueSuffixReference((ALittlePropertyValueSuffix)element, range);
        if (element instanceof ALittlePropertyValueThisType) return new ALittlePropertyValueThisTypeReference((ALittlePropertyValueThisType)element, range);

        if (element instanceof ALittleReflectValue) return new ALittleReflectValueReference((ALittleReflectValue)element, range);
        if (element instanceof ALittleReturnExpr) return new ALittleReturnExprReference((ALittleReturnExpr)element, range);

        if (element instanceof ALittleStructDec) return new ALittleStructDecReference((ALittleStructDec)element, range);
        if (element instanceof ALittleStructNameDec) return new ALittleStructNameDecReference((ALittleStructNameDec)element, range);
        if (element instanceof ALittleStructVarDec) return new ALittleStructVarDecReference((ALittleStructVarDec)element, range);

        if (element instanceof ALittleTemplateDec) return new ALittleTemplateDecReference((ALittleTemplateDec)element, range);
        if (element instanceof ALittleTemplatePairDec) return new ALittleTemplatePairDecReference((ALittleTemplatePairDec)element, range);
        if (element instanceof ALittleUsingDec) return new ALittleUsingDecReference((ALittleUsingDec)element, range);
        if (element instanceof ALittleUsingNameDec) return new ALittleUsingNameDecReference((ALittleUsingNameDec)element, range);
        if (element instanceof ALittleValueFactorStat) return new ALittleValueFactorStatReference((ALittleValueFactorStat)element, range);
        if (element instanceof ALittleValueStat) return new ALittleValueStatReference((ALittleValueStat)element, range);

        if (element instanceof ALittleVarAssignDec) return new ALittleVarAssignDecReference((ALittleVarAssignDec)element, range);
        if (element instanceof ALittleVarAssignExpr) return new ALittleVarAssignExprReference((ALittleVarAssignExpr)element, range);
        if (element instanceof ALittleVarAssignNameDec) return new ALittleVarAssignNameDecReference((ALittleVarAssignNameDec)element, range);
        if (element instanceof ALittleWrapValueStat) return new ALittleWrapValueStatReference((ALittleWrapValueStat)element, range);

        if (element instanceof ALittleNcallStat) return new ALittleNcallStatReference((ALittleNcallStat)element, range);
        if (element instanceof ALittleNsendExpr) return new ALittleNsendExprReference((ALittleNsendExpr)element, range);
        if (element instanceof ALittleThrowExpr) return new ALittleThrowExprReference((ALittleThrowExpr)element, range);

        return null;
    }
}
