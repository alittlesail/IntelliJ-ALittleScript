package plugin.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import plugin.guess.ALittleGuess;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.reference.*;

import java.util.List;

public class ALittlePsiImplUtil {
    public static PsiReference getReference(PsiElement element) {
        return ALittleReferenceUtil.create(element);
    }

    @NotNull
    public static ALittleGuess guessType(PsiElement element) throws ALittleGuessException {
        List<ALittleGuess> guessList = guessTypes(element);
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(element, "无法计算出该元素属于什么类型");
        }
        return guessList.get(0);
    }

    @NotNull
    public static List<ALittleGuess> guessTypes(PsiElement element) throws ALittleGuessException {
        List<ALittleGuess> guessTypeInfoList = ALittleTreeChangeListener.getGuessTypeList(element);
        if (guessTypeInfoList != null && !guessTypeInfoList.isEmpty()) {
            boolean isChanged = false;
            for (ALittleGuess info : guessTypeInfoList) {
                if (info.isChanged()) {
                    isChanged = true;
                    break;
                }
            }
            if (!isChanged) return  guessTypeInfoList;
        }

        ALittleReferenceInterface ref = ALittleReferenceUtil.create(element);
        if (ref == null) {
            throw new ALittleGuessException(element, "ALittleReference对象创建失败 element:" + element);
        }
        guessTypeInfoList = ref.guessTypes();
        ALittleTreeChangeListener.putGuessTypeList(element, guessTypeInfoList);
        return guessTypeInfoList;
    }
}
