package plugin.guess;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassDec;
import plugin.reference.ALittleReferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleGuessClass extends ALittleGuess {
    public @NotNull List<ALittleGuess> templateList = new ArrayList<>();            // 类本身定义的模板列表
    public @NotNull Map<String, ALittleGuess> templateMap = new HashMap<>();        // 填充后的模板实例

    public @NotNull ALittleClassDec element;
    public ALittleGuessClass(@NotNull ALittleClassDec e) {
        super("");
        element = e;
    }

    public void UpdateValue(@NotNull String namespaceName, @NotNull String className) {
        value = namespaceName + "." + className;
        List<String> nameList = new ArrayList<>();
        for (ALittleGuess guess : templateList) {
            nameList.add(guess.value);
        }
        value += "<" + String.join(",", nameList) + ">";
    }

    public boolean isChanged() {
        for (ALittleGuess guess : templateList) {
            if (guess.isChanged()) return true;
        }
        for (ALittleGuess guess : templateMap.values()) {
            if (guess.isChanged()) return true;
        }
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
