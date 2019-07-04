package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import plugin.psi.*;

public class ALittleReferenceFactory {
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

        return null;
    }
}
