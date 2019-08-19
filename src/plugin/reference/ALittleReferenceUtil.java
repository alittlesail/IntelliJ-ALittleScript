package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.List;

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

    // 定义类型
    public enum GuessType
    {
        GT_CONST,
        GT_PRIMITIVE,
        GT_CLASS,
        GT_STRUCT,
        GT_ENUM,
        GT_MAP,
        GT_LIST,
        GT_FUNCTOR,
        GT_NAMESPACE,
    }

    // 类型信息
    public static class GuessTypeInfo
    {
        public GuessType type;
        public String value;                              // 完整类型的字符串
        public PsiElement element;                        // 指向的元素
        public GuessTypeInfo listSubType;                 // type="List"时，表示List的子类型
        public GuessTypeInfo mapKeyType;                  // type="Map"时, 表示Map的Key
        public GuessTypeInfo mapValueType;                // type="Map"时, 表示Map的Value
        public List<GuessTypeInfo> functorParamList;      // type="Functor"时, 表示参数列表
        public List<GuessTypeInfo> functorReturnList;     // type="Functor"时, 表示返回值列表
        boolean functorAwait;                             // type="Functor"时, 表示是否是await
    }

    // 计算表达式需要使用什么样的变量方式
    @NotNull
    public static String CalcPairsType(ALittleValueStat valueStat) throws ALittleReferenceException {
        GuessTypeInfo guessType = valueStat.guessType();
        // 必须是模板容器
        if (guessType.type == GuessType.GT_LIST) {
            return "___ipairs";
        } else if (guessType.type == GuessType.GT_MAP) {
            return "___pairs";
        }

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

        if (element instanceof ALittleClassDec) return new ALittleClassDecReference((ALittleClassDec)element, range);
        if (element instanceof ALittleClassNameDec) return new ALittleClassNameDecReference((ALittleClassNameDec)element, range);
        if (element instanceof ALittleClassVarDec) return new ALittleClassVarDecReference((ALittleClassVarDec)element, range);

        if (element instanceof ALittleConstValue) return new ALittleConstValueReference((ALittleConstValue)element, range);
        if (element instanceof ALittleCustomType) return new ALittleCustomTypeReference((ALittleCustomType)element, range);

        if (element instanceof ALittleEnumDec) return new ALittleEnumDecReference((ALittleEnumDec)element, range);
        if (element instanceof ALittleEnumNameDec) return new ALittleEnumNameDecReference((ALittleEnumNameDec)element, range);
        if (element instanceof ALittleEnumVarDec) return new ALittleEnumVarDecReference((ALittleEnumVarDec)element, range);

        if (element instanceof ALittleForExpr) return new ALittleForExprReference((ALittleForExpr)element, range);
        if (element instanceof ALittleGenericType) return new ALittleGenericTypeReference((ALittleGenericType)element, range);
        if (element instanceof ALittleMethodNameDec) return new ALittleMethodNameDecReference((ALittleMethodNameDec)element, range);
        if (element instanceof ALittleMethodParamNameDec) return new ALittleMethodParamNameDecReference((ALittleMethodParamNameDec)element, range);

        if (element instanceof ALittleNamespaceDec) return new ALittleNamespaceDecReference((ALittleNamespaceDec)element, range);
        if (element instanceof ALittleNamespaceNameDec) return new ALittleNamespaceNameDecReference((ALittleNamespaceNameDec)element, range);

        if (element instanceof ALittleOpNewListStat) return new ALittleOpNewListStatReference((ALittleOpNewListStat)element, range);
        if (element instanceof ALittleOpNewStat) return new ALittleOpNewStatReference((ALittleOpNewStat)element, range);
        if (element instanceof ALittlePrimitiveType) return new ALittlePrimitiveTypeReference((ALittlePrimitiveType)element, range);

        if (element instanceof ALittlePropertyValueBracketValue) return new ALittlePropertyValueBracketValueReference((ALittlePropertyValueBracketValue)element, range);
        if (element instanceof ALittlePropertyValueCastType) return new ALittlePropertyValueCastTypeReference((ALittlePropertyValueCastType)element, range);
        if (element instanceof ALittlePropertyValueCustomType) return new ALittlePropertyValueCustomTypeReference((ALittlePropertyValueCustomType)element, range);
        if (element instanceof ALittlePropertyValueDotIdName) return new ALittlePropertyValueDotIdNameReference((ALittlePropertyValueDotIdName)element, range);
        if (element instanceof ALittlePropertyValueFirstType) return new ALittlePropertyValueFirstTypeReference((ALittlePropertyValueFirstType)element, range);
        if (element instanceof ALittlePropertyValueMethodCall) return new ALittlePropertyValueMethodCallReference((ALittlePropertyValueMethodCall)element, range);
        if (element instanceof ALittlePropertyValue) return new ALittlePropertyValueReference((ALittlePropertyValue)element, range);
        if (element instanceof ALittlePropertyValueThisType) return new ALittlePropertyValueThisTypeReference((ALittlePropertyValueThisType)element, range);

        if (element instanceof ALittleReflectValue) return new ALittleReflectValueReference((ALittleReflectValue)element, range);
        if (element instanceof ALittleReturnExpr) return new ALittleReturnExprReference((ALittleReturnExpr)element, range);

        if (element instanceof ALittleStructDec) return new ALittleStructDecReference((ALittleStructDec)element, range);
        if (element instanceof ALittleStructNameDec) return new ALittleStructNameDecReference((ALittleStructNameDec)element, range);
        if (element instanceof ALittleStructVarDec) return new ALittleStructVarDecReference((ALittleStructVarDec)element, range);

        if (element instanceof ALittleValueFactorStat) return new ALittleValueFactorStatReference((ALittleValueFactorStat)element, range);
        if (element instanceof ALittleValueStat) return new ALittleValueStatReference((ALittleValueStat)element, range);

        if (element instanceof ALittleVarAssignDec) return new ALittleVarAssignDecReference((ALittleVarAssignDec)element, range);
        if (element instanceof ALittleVarAssignExpr) return new ALittleVarAssignExprReference((ALittleVarAssignExpr)element, range);
        if (element instanceof ALittleVarAssignNameDec) return new ALittleVarAssignNameDecReference((ALittleVarAssignNameDec)element, range);
        if (element instanceof ALittleWrapValueStat) return new ALittleWrapValueStatReference((ALittleWrapValueStat)element, range);

        return null;
    }
}
