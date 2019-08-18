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
        public String GetError() { return mError; }

        @NotNull
        public PsiElement GetElement() { return mElement; }
    }

    public enum GuessType
    {
        GT_CONST,
        GT_PRIMITIVE,
        GT_CLASS,
        GT_CLASS_NAME,
        GT_STRUCT,
        GT_ENUM,
        GT_MAP,
        GT_LIST,
        GT_FUNCTOR,
        GT_NAMESPACE_NAME,
    }

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
        boolean functorAwait;                              // type="Functor"时, 表示是否是await
        // public List<GuessTypeInfo> listVarType;           // type="class" 或者 type="struct"，表示成员的类型列表
        // public List<String> listVarName;                  // type="class" 或者 type="struct"，表示成员的变量列表
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

    // 创建引用对象
    public static ALittleReference create(PsiElement element) {
        if (element instanceof ALittleNamespaceNameDec) return new ALittleNamespaceNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleClassExtendsNameDec) return new ALittleClassExtendsNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleClassExtendsNamespaceNameDec) return new ALittleClassExtendsNamespaceNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleCustomTypeNamespaceNameDec) return new ALittleCustomTypeNamespaceNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleCustomTypeNameDec) return new ALittleCustomTypeNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueCustomType) return new ALittlePropertyValueCustomTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueDotIdName) return new ALittlePropertyValueDotIdNameReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleClassNameDec) return new ALittleClassNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleMethodNameDec) return new ALittleMethodNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleMethodParamNameDec) return new ALittleMethodParamNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleVarAssignNameDec) return new ALittleVarAssignNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleInstanceClassNameDec) return new ALittleInstanceClassNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleInstanceNameDec) return new ALittleInstanceNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleClassVarNameDec) return new ALittleClassVarNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleStructNameDec) return new ALittleStructNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleStructVarNameDec) return new ALittleStructVarNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleStructExtendsNameDec) return new ALittleStructExtendsNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleStructExtendsNamespaceNameDec) return new ALittleStructExtendsNamespaceNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleEnumNameDec) return new ALittleEnumNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleEnumVarNameDec) return new ALittleEnumVarNameDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePrimitiveType) return new ALittlePrimitiveTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleAutoType) return new ALittleAutoTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueThisType) return new ALittlePropertyValueThisTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueCastType) return new ALittlePropertyValueCastTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueMethodCallStat) return new ALittlePropertyValueMethodCallStatReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValueBrackValueStat) return new ALittlePropertyValueBrackValueStatReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleBindStat) return new ALittleBindStatReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleConstValue) return new ALittleConstValueReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleOpNewStat) return new ALittleOpNewStatReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleMethodReturnTypeDec) return new ALittleMethodReturnTypeDecReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleAllType) return new ALittleAllTypeReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittlePropertyValue) return new ALittlePropertyValueReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleValueStat) return new ALittleValueStatReference(element, new TextRange(0, element.getText().length()));
        if (element instanceof ALittleOp2Stat) return new ALittleOp2StatReference(element, new TextRange(0, element.getText().length()));

        return null;
    }
}
