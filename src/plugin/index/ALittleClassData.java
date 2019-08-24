package plugin.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.alittle.PsiHelper;
import plugin.component.StdLibraryProvider;
import plugin.psi.*;
import plugin.reference.ALittleReferenceUtil;

import java.io.File;
import java.util.*;

public class ALittleClassData {
    private ALittleClassMemberData privateData;
    private ALittleClassMemberData protectedData;
    private ALittleClassMemberData publicData;

    public ALittleClassData() {
        privateData = new ALittleClassMemberData();
        protectedData = new ALittleClassMemberData();
        publicData = new ALittleClassMemberData();
    }

    public void addClassVarDec(@NotNull ALittleClassVarDec dec) {
        PsiElement nameDec = dec.getIdContent();
        if (nameDec == null) return;
        PsiHelper.ClassAccessType accessType = PsiHelper.calcAccess(dec.getAccessModifier());
        if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
            privateData.addClassVarDec(nameDec.getText(), dec);
        } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
            protectedData.addClassVarDec(nameDec.getText(), dec);
        } else {
            publicData.addClassVarDec(nameDec.getText(), dec);
        }
    }

    public void addClassMethodNameDec(@NotNull ALittleMethodNameDec dec) {
        PsiElement parent = dec.getParent();

        PsiHelper.ClassAccessType accessType;
        PsiHelper.ClassMethodType methodType;
        if (parent instanceof ALittleClassMethodDec) {
            ALittleClassMethodDec methodDec = (ALittleClassMethodDec)parent;
            accessType = PsiHelper.calcAccess(methodDec.getAccessModifier());
            methodType = PsiHelper.ClassMethodType.FUN;
        } else if (parent instanceof ALittleClassGetterDec) {
            ALittleClassGetterDec methodDec = (ALittleClassGetterDec)parent;
            accessType = PsiHelper.calcAccess(methodDec.getAccessModifier());
            methodType = PsiHelper.ClassMethodType.GETTER;
        } else if (parent instanceof ALittleClassSetterDec) {
            ALittleClassSetterDec methodDec = (ALittleClassSetterDec)parent;
            accessType = PsiHelper.calcAccess(methodDec.getAccessModifier());
            methodType = PsiHelper.ClassMethodType.SETTER;
        } else if (parent instanceof ALittleClassStaticDec) {
            ALittleClassStaticDec methodDec = (ALittleClassStaticDec)parent;
            accessType = PsiHelper.calcAccess(methodDec.getAccessModifier());
            methodType = PsiHelper.ClassMethodType.STATIC;
        } else {
            return;
        }

        if (accessType == PsiHelper.ClassAccessType.PRIVATE) {
            privateData.addMethodNameDec(methodType, dec.getText(), dec);
        } else if (accessType == PsiHelper.ClassAccessType.PROTECTED) {
            protectedData.addMethodNameDec(methodType, dec.getText(), dec);
        } else {
            publicData.addMethodNameDec(methodType, dec.getText(), dec);
        }
    }
}
