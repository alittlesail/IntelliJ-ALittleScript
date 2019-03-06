package plugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import plugin.ALittleFileType;

public class ALittleElementFactory {
    public static PsiElement create(PsiElement element, String name) {
        if (element instanceof ALittleNamespaceNameDec) return createNamespaceNameDec(element.getProject(), name);
        if (element instanceof ALittleClassExtendsNamespaceNameDec) return createClassExtendsNamespaceNameDec(element.getProject(), name);
        if (element instanceof ALittleCustomTypeNamespaceNameDec) return createCustomTypeNamespaceNameDec(element.getProject(), name);
        if (element instanceof ALittleCustomTypeNameDec) return createCustomTypeNameDec(element.getProject(), name);
        if (element instanceof ALittlePropertyValueCustomType) return createPropertyValueCustomType(element.getProject(), name);
        if (element instanceof ALittlePropertyValueDotIdName) return createPropertyValueDotIdName(element.getProject(), name);
        if (element instanceof ALittleClassNameDec) return createClassNameDec(element.getProject(), name);
        if (element instanceof ALittleClassExtendsNameDec) return createClassExtendsNameDec(element.getProject(), name);
        if (element instanceof ALittleMethodNameDec) return createMethodNameDec(element.getProject(), name);
        if (element instanceof ALittleMethodParamNameDec) return createMethodParamNameDec(element.getProject(), name);
        if (element instanceof ALittleVarAssignNameDec) return createVarAssignNameDec(element.getProject(), name);
        if (element instanceof ALittleInstanceClassNameDec) return createInstanceClassNameDec(element.getProject(), name);
        if (element instanceof ALittleInstanceNameDec) return createInstanceNameDec(element.getProject(), name);
        if (element instanceof ALittleClassVarNameDec) return createClassVarNameDec(element.getProject(), name);
        if (element instanceof ALittleStructNameDec) return createStructNameDec(element.getProject(), name);
        if (element instanceof ALittleStructVarNameDec) return createStructVarNameDec(element.getProject(), name);
        if (element instanceof ALittleEnumNameDec) return createEnumNameDec(element.getProject(), name);
        if (element instanceof ALittleEnumVarNameDec) return createEnumVarNameDec(element.getProject(), name);
        if (element instanceof ALittlePrimitiveType) return createPrimitiveType(element.getProject(), name);
        if (element instanceof ALittleStructExtendsNamespaceNameDec) return createStructExtendsNamespaceNameDec(element.getProject(), name);
        if (element instanceof ALittleStructExtendsNameDec) return createStructExtendsNameDec(element.getProject(), name);

        return null;
    }

    private static ALittleNamespaceNameDec createNamespaceNameDec(Project project, String name) {
        String content = "namespace " + name;
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getNamespaceNameDec();
    }

    private static ALittleClassExtendsNamespaceNameDec createClassExtendsNamespaceNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA : " + name + ".ClassB {}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassExtendsNamespaceNameDec();
    }

    private static ALittleCustomTypeNamespaceNameDec createCustomTypeNamespaceNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA { fun Method() : " + name + ".ClassB {} }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodReturnDec()
                .getMethodReturnTypeDecList().get(0)
                .getAllType().getCustomType().getCustomTypeNamespaceNameDec();
    }

    private static ALittleCustomTypeNameDec createCustomTypeNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA { fun Method() : " + name + " {} }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodReturnDec()
                .getMethodReturnTypeDecList().get(0)
                .getAllType().getCustomType().getCustomTypeNameDec();
    }

    private static ALittlePropertyValueCustomType createPropertyValueCustomType(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA { fun Method() { var " + name + ":int = 0; " + name + " = 0; } }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodBodyDec()
                .getAllExprList().get(1)
                .getOpAssignExpr()
                .getPropertyValueList().get(0)
                .getPropertyValueCustomType();
    }

    private static ALittlePropertyValueDotIdName createPropertyValueDotIdName(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA { fun Method() { this." + name + " = 0; } }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodBodyDec()
                .getAllExprList().get(0)
                .getOpAssignExpr()
                .getPropertyValueList()
                .get(0)
                .getPropertyValueSuffixList().get(0)
                .getPropertyValueDotId()
                .getPropertyValueDotIdName();
    }

    private static ALittleClassNameDec createClassNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class " + name + "{}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassNameDec();
    }

    private static ALittleClassExtendsNameDec createClassExtendsNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class ClassA : ALittle." + name + " {}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassExtendsNameDec();
    }

    private static ALittleMethodNameDec createMethodNameDec(Project project, String name) {
        String content = "namespace ALittle";
        content += "class Class { void " + name + "() {} }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassMethodDecList().get(0).getMethodNameDec();
    }

    private static ALittleMethodParamNameDec createMethodParamNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "class Class { void Method(" + name + ":int) {} }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodParamDec()
                .getMethodParamOneDecList().get(0)
                .getMethodParamNameDec();
    }

    private static ALittleVarAssignNameDec createVarAssignNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "class Class { void Method() { var " + name + ":int = 0; } }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0)
                .getClassMethodDecList().get(0)
                .getMethodBodyDec()
                .getAllExprList().get(0)
                .getVarAssignExpr()
                .getVarAssignPairDecList().get(0)
                .getVarAssignNameDec();
    }

    private static ALittleInstanceClassNameDec createInstanceClassNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "instance Instance: " + name + " = new " + name + "();";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getInstanceDecList().get(0).getInstanceClassNameDec();
    }

    private static ALittleInstanceNameDec createInstanceNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "instance " + name + ":Class = new Class();";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getInstanceDecList().get(0).getInstanceNameDec();
    }

    private static ALittleClassVarNameDec createClassVarNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "class Class { private " + name + ":int; }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassVarDecList().get(0).getClassVarNameDec();
    }

    private static ALittleStructNameDec createStructNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "struct " + name + "{}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getStructDecList().get(0).getStructNameDec();
    }

    private static ALittleStructVarNameDec createStructVarNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "struct Struct { " + name + ":int; }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getStructDecList().get(0).getStructVarDecList().get(0).getStructVarNameDec();
    }

    private static ALittleEnumNameDec createEnumNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "enum " + name + "{}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getEnumDecList().get(0).getEnumNameDec();
    }

    private static ALittleEnumVarNameDec createEnumVarNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "enum Enum { " + name + "= 0, }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getEnumDecList().get(0).getEnumVarDecList().get(0).getEnumVarNameDec();
    }

    private static ALittlePrimitiveType createPrimitiveType(Project project, String name) {
        String content = "namespace ALittle;";
        content += "class Class { public mem:" + name + "; }";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getClassDecList().get(0).getClassVarDecList().get(0).getAllType().getPrimitiveType();
    }

    private static ALittleStructExtendsNamespaceNameDec createStructExtendsNamespaceNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "struct StructA : " + name + ".StructB {}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getStructDecList().get(0).getStructExtendsNamespaceNameDec();
    }

    private static ALittleStructExtendsNameDec createStructExtendsNameDec(Project project, String name) {
        String content = "namespace ALittle;";
        content += "struct StructA : ALittle." + name + " {}";
        final ALittleFile file = createFile(project, content);
        return ((ALittleNamespaceDec)file.getFirstChild()).getStructDecList().get(0).getStructExtendsNameDec();
    }

    private static ALittleFile createFile(Project project, String text) {
        String name = "dummy.alittle";
        return (ALittleFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ALittleFileType.INSTANCE, text);
    }
}
