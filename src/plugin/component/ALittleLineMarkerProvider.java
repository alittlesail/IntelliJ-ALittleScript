package plugin.component;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.psi.*;

import java.util.Collection;

public class ALittleLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof ALittleMethodNameDec) {
            ALittleMethodNameDec myElement = (ALittleMethodNameDec) element;
            // 获取函数名
            String mKey = myElement.getText();

            PsiElement parent = myElement.getParent();
            // 必须是类的函数
            if (!(parent.getParent() instanceof ALittleClassDec)) return;
            ALittleClassDec classDec = (ALittleClassDec) parent.getParent();
            // 计算父类
            ALittleClassDec classExtendsDec = PsiHelper.findClassExtends(classDec);
            if (classExtendsDec == null) return;

            // 判断函数类型
            PsiHelper.ClassAttrType attrType;
            if (parent instanceof ALittleClassMethodDec) {
                attrType = PsiHelper.ClassAttrType.FUN;
            } else if (parent instanceof ALittleClassStaticDec) {
                attrType = PsiHelper.ClassAttrType.STATIC;
            } else if (parent instanceof ALittleClassGetterDec) {
                attrType = PsiHelper.ClassAttrType.GETTER;
            } else if (parent instanceof ALittleClassSetterDec) {
                attrType = PsiHelper.ClassAttrType.SETTER;
            } else {
                return;
            }

            // 标记函数继承
            PsiElement methodNameDec = PsiHelper.findFirstClassAttrFromExtends(classExtendsDec, attrType, mKey, 100);
            if (methodNameDec != null) {
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder.create(ALittleIcons.OVERRIDE).
                                setTargets(methodNameDec).
                                setTooltipText("override");
                result.add(builder.createLineMarkerInfo(myElement.getIdContent()));
            }
        }
    }
}
