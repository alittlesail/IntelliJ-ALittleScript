package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.FileHelper;
import plugin.guess.*;
import plugin.psi.ALittlePathsValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ALittlePathsValueReference extends ALittleReference<ALittlePathsValue> {
    public ALittlePathsValueReference(@NotNull ALittlePathsValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();
        ALittleGuess guess = new ALittleGuessList(ALittleGuessPrimitive.sStringGuess, false, false);
        guess_list.add(guess);
        return guess_list;
    }

    @Override
    public void checkError() throws ALittleGuessException {
        PsiElement text = myElement.getTextContent();
        if (text == null) throw new ALittleGuessException(myElement, "请填写路径来获取子文件夹以及文件的路径");
        String value = text.getText();

        // 检查路径是否存在
        String path = FileHelper.calcModulePath(myElement, true) + value.substring(1, value.length() - 1).trim();
        File info = new File(path);
        if (!info.isDirectory())
            throw new ALittleGuessException(myElement, "填写的路径不存在:" + path);
    }
}
