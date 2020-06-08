package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleTemplatePairDecReference extends ALittleReference<ALittleTemplatePairDec> {
    public ALittleTemplatePairDecReference(@NotNull ALittleTemplatePairDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        ALittleAllType all_type = null;
        ALittleTemplateExtendsClassDec extends_class_dec = null;
        ALittleTemplateExtendsStructDec extends_struct_dec = null;

        ALittleTemplateExtendsDec extends_dec = myElement.getTemplateExtendsDec();
        if (extends_dec != null) {
            all_type = extends_dec.getAllType();
            extends_class_dec = extends_dec.getTemplateExtendsClassDec();
            extends_struct_dec = extends_dec.getTemplateExtendsStructDec();
        }

        ALittleGuess template_extends = null;
        boolean is_class = false;
        boolean is_struct = false;
        if (all_type != null) {
            ALittleGuess guess = all_type.guessType();
            if (!(guess instanceof ALittleGuessClass) && !(guess instanceof ALittleGuessStruct)) {
                throw new ALittleGuessException(all_type, "继承的对象必须是一个类或者结构体");
            }
            template_extends = guess;
        } else if (extends_class_dec != null) {
            is_class = true;
        } else if (extends_struct_dec != null) {
            is_struct = true;
        }

        if (myElement.getParent() == null) throw new ALittleGuessException(myElement, "没有父节点");
        PsiElement parent = myElement.getParent();
        if (parent.getParent() == null) throw new ALittleGuessException(parent, "没有父节点");
        parent = parent.getParent();

        // 根据定义区分类模板还是函数模板
        if (parent instanceof ALittleClassDec) {
            ALittleGuessClassTemplate info = new ALittleGuessClassTemplate(myElement, template_extends, is_class, is_struct);
            info.updateValue();
            guess_list.add(info);
        } else {
            ALittleGuessMethodTemplate info = new ALittleGuessMethodTemplate(myElement, template_extends, is_class, is_struct);
            info.updateValue();
            guess_list.add(info);
        }
        return guess_list;
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getTemplateNameDec().getText().startsWith("___")) {
            throw new ALittleGuessException(myElement, "局部变量名不能以3个下划线开头");
        }

        List<ALittleGuess> guessList = myElement.guessTypes();
        if (guessList.isEmpty()) {
            throw new ALittleGuessException(myElement, "未知类型");
        } else if (guessList.size() != 1) {
            throw new ALittleGuessException(myElement, "重复定义");
        }
    }
}
