package plugin;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;
import plugin.reference.ALittleReferenceOpUtil;
import plugin.reference.ALittleReferenceUtil;

import java.util.Collection;
import java.util.List;

public class ALittleLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            Collection<? super RelatedItemLineMarkerInfo> result) {
        try {
            if (element instanceof ALittleMethodNameDec) {
                ALittleMethodNameDec myElement = (ALittleMethodNameDec) element;
                String mNamespace = ALittleUtil.getNamespaceName((ALittleFile)myElement.getContainingFile());
                String mKey = myElement.getText();

                PsiElement parent = myElement.getParent();
                Project project = myElement.getProject();
                PsiFile psiFile = myElement.getContainingFile();
                if (!(parent.getParent() instanceof ALittleClassDec)) return;
                ALittleClassDec classDec = (ALittleClassDec) parent.getParent();
                if (classDec.getClassExtendsDec() == null) return;
                ALittleClassExtendsDec extendsDec = classDec.getClassExtendsDec();
                if (extendsDec == null) return;
                ALittleClassNameDec classNameDec = extendsDec.getClassNameDec();
                if (classNameDec == null) return;
                ALittleReferenceUtil.GuessTypeInfo extendsGuess = classNameDec.guessType();

                ALittleMethodNameDec methodNameDec = null;
                if (parent instanceof ALittleClassMethodDec) {
                    methodNameDec = ALittleUtil.findFirstFunDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, mKey, 100);
                } else if (parent instanceof ALittleClassStaticDec) {
                    methodNameDec = ALittleUtil.findFirstStaticDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, mKey, 100);

                } else if (parent instanceof ALittleClassGetterDec) {
                    methodNameDec = ALittleUtil.findFirstGetterDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, mKey, 100);

                } else if (parent instanceof ALittleClassSetterDec) {
                    methodNameDec = ALittleUtil.findFirstSetterDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, mKey, 100);
                }
                if (methodNameDec != null) {
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(ALittleIcons.OVERRIDE).
                                    setTargets(methodNameDec).
                                    setTooltipText("override");
                    result.add(builder.createLineMarkerInfo(element));
                }
            } else if (element instanceof ALittleClassCtorDec) {
                ALittleClassCtorDec myElement = (ALittleClassCtorDec) element;
                String mNamespace = ALittleUtil.getNamespaceName((ALittleFile)myElement.getContainingFile());

                PsiElement parent = myElement.getParent();
                Project project = myElement.getProject();
                PsiFile psiFile = myElement.getContainingFile();
                if (!(parent instanceof ALittleClassDec)) return;
                ALittleClassDec classDec = (ALittleClassDec)parent;
                if (classDec.getClassExtendsDec() == null) return;
                ALittleClassExtendsDec extendsDec = classDec.getClassExtendsDec();
                if (extendsDec == null) return;
                ALittleClassNameDec classNameDec = extendsDec.getClassNameDec();
                if (classNameDec == null) return;
                ALittleReferenceUtil.GuessTypeInfo extendsGuess = classNameDec.guessType();

                ALittleClassCtorDec extendsCtorDec = ALittleUtil.findFirstCtorDecFromExtends(project, psiFile, mNamespace, (ALittleClassDec) extendsGuess.element, 100);
                if (extendsCtorDec != null) {
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(ALittleIcons.OVERRIDE).
                                    setTargets(extendsCtorDec).
                                    setTooltipText("override");
                    result.add(builder.createLineMarkerInfo(element));
                }
            }
        } catch (ALittleReferenceUtil.ALittleReferenceException ignored) {

        }
    }
}
