package plugin.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.reference.*;

import java.util.List;

public class ALittlePsiImplUtil {
    public static PsiReference getReference(PsiElement element) {
        return ALittleReferenceUtil.create(element);
    }

    @NotNull
    public static ALittleReferenceUtil.GuessTypeInfo guessType(PsiElement element) throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes(element);
        if (guessList.isEmpty()) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "无法计算出该元素属于什么类型");
        }
        return guessList.get(0);
    }

    @NotNull
    public static List<ALittleReferenceUtil.GuessTypeInfo> guessTypes(PsiElement element) throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleReference ref = ALittleReferenceUtil.create(element);
        if (ref == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "ALittleReference对象创建失败 element:" + element);
        }
        return ref.guessTypes();
    }
}
