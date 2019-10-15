package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleReferenceUtil {
    // 检查迭代函数
    public static boolean IsPairsFunction(@NotNull List<ALittleGuess> guessList) {
        if (guessList.size() != 3) return false;
        if (!(guessList.get(0) instanceof ALittleGuessFunctor)) return false;
        ALittleGuessFunctor guess = (ALittleGuessFunctor)guessList.get(0);
        if (guess.functorAwait) return false;
        if (guess.functorProto != null) return false;
        if (!guess.functorTemplateParamList.isEmpty()) return false;
        if (guess.functorParamList.size() != 2) return false;
        if (guess.functorParamTail != null) return false;
        if (!guess.functorReturnList.isEmpty()) return false;
        if (guess.functorReturnTail != null) return false;
        if (!guess.functorParamList.get(0).value.equals(guessList.get(1).value)) return false;
        if (!guess.functorParamList.get(1).value.equals(guessList.get(2).value)) return false;
        return true;
    }

    // 计算表达式需要使用什么样的变量方式
    @NotNull
    public static String CalcPairsType(ALittleValueStat valueStat) throws ALittleGuessException {
        List<ALittleGuess> guessList = valueStat.guessTypes();
        // 必须是模板容器
        if (guessList.size() == 1 && guessList.get(0) instanceof ALittleGuessList) {
            return "___ipairs";
        } else if (guessList.size() == 1 && guessList.get(0) instanceof ALittleGuessMap) {
            return "___pairs";
        }

        // 检查迭代函数
        if (IsPairsFunction(guessList)) return "";

        throw new ALittleGuessException(valueStat, "该表达式不能遍历");
    }

    // 判断
    public static boolean IsClassSuper(ALittleClassDec child, @NotNull String parent) throws ALittleGuessException {
        ALittleClassExtendsDec extendsDec = child.getClassExtendsDec();
        if (extendsDec == null) return false;

        ALittleClassNameDec nameDec = extendsDec.getClassNameDec();
        if (nameDec == null) return false;

        ALittleGuess guess = nameDec.guessType();
        if (guess.value.equals(parent)) return true;

        if (!(guess instanceof ALittleGuessClass)) return false;
        ALittleGuessClass guessClass = (ALittleGuessClass)guess;

        return IsClassSuper(guessClass.element, parent);
    }

    // 判断 parent是否是child的父类
    public static boolean IsStructSuper(PsiElement child, @NotNull String parent) throws ALittleGuessException {
        if (!(child instanceof ALittleStructDec)) return false;
        ALittleStructDec childStruct = (ALittleStructDec)child;

        ALittleStructExtendsDec extendsDec = childStruct.getStructExtendsDec();
        if (extendsDec == null) return false;

        ALittleStructNameDec nameDec = extendsDec.getStructNameDec();
        if (nameDec == null) return false;

        ALittleGuess guess = nameDec.guessType();
        if (guess.value.equals(parent)) return true;

        if (!(guess instanceof ALittleGuessStruct)) return false;
        ALittleGuessStruct guessStruct = (ALittleGuessStruct)guess;

        return IsStructSuper(guessStruct.element, parent);
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
        if (element instanceof ALittleMethodReturnTailDec) return new ALittleMethodReturnTailDecReference((ALittleMethodReturnTailDec)element, range);

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

        if (element instanceof ALittleThrowExpr) return new ALittleThrowExprReference((ALittleThrowExpr)element, range);

        return null;
    }
}
