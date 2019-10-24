package plugin.guess;

import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleClassDec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALittleGuessClass extends ALittleGuess {
    private @NotNull String mNamespaceName;
    private @NotNull String mClassName;

    public @NotNull List<ALittleGuess> templateList = new ArrayList<>();            // 类本身定义的模板列表
    public @NotNull Map<String, ALittleGuess> templateMap = new HashMap<>();        // 填充后的模板实例

    public String usingName;        // 如果是using定义出来的，那么就有这个值
    public @NotNull ALittleClassDec element;
    public ALittleGuessClass(@NotNull String namespaceName, @NotNull String className,
                             @NotNull ALittleClassDec e, String un) {
        isRegister = PsiHelper.isRegister(e);
        mNamespaceName = namespaceName;
        mClassName = className;
        element = e;
        usingName = un;
    }

    @Override
    public boolean NeedReplace() {
        if (templateList.isEmpty()) return false;
        for (Map.Entry<String, ALittleGuess> entry : templateMap.entrySet()) {
            if (entry.getValue().NeedReplace()) return true;
        }
        return false;
    }

    @Override
    @NotNull
    public ALittleGuess ReplaceTemplate(@NotNull Map<String, ALittleGuess> fillMap) {
        ALittleGuessClass newGuess = (ALittleGuessClass)Clone();
        for (Map.Entry<String, ALittleGuess> entry : templateMap.entrySet()) {
            ALittleGuess guess = entry.getValue().ReplaceTemplate(fillMap);
            if (!guess.equals(entry.getValue())) {
                newGuess.templateMap.put(entry.getKey(), entry.getValue().ReplaceTemplate(fillMap));
            }
        }
        return newGuess;
    }

    @Override
    @NotNull
    public ALittleGuess Clone() {
        ALittleGuessClass guess = new ALittleGuessClass(mNamespaceName, mClassName, element, usingName);
        guess.templateList.addAll(templateList);
        for (Map.Entry<String, ALittleGuess> entry : templateMap.entrySet()) {
            guess.templateMap.put(entry.getKey(), entry.getValue());
        }
        guess.UpdateValue();
        return guess;
    }

    @NotNull
    public String GetNamespaceName() {
        return mNamespaceName;
    }

    @NotNull
    public String GetClassName() {
        return mClassName;
    }

    @Override
    public void UpdateValue() {
        value = mNamespaceName + "." + mClassName;
        List<String> nameList = new ArrayList<>();
        for (ALittleGuess tem : templateList) {
            ALittleGuess impl = templateMap.get(tem.value);
            if (impl == null) {
                nameList.add(tem.value);
            } else {
                nameList.add(impl.value);
            }
        }
        if (!nameList.isEmpty()) {
            value += "<" + String.join(",", nameList) + ">";
        }
    }

    @Override
    public boolean isChanged() {
        for (ALittleGuess guess : templateList) {
            if (guess.isChanged()) return true;
        }
        for (ALittleGuess guess : templateMap.values()) {
            if (guess.isChanged()) return true;
        }
        if (!element.isValid()) return true;
        return ALittleTreeChangeListener.getGuessTypeList(element) == null;
    }
}
