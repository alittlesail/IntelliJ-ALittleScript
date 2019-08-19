package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import plugin.ALittleUtil;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittlePropertyValueBracketValueReference extends ALittleReference<ALittlePropertyValueBracketValue> {
    public ALittlePropertyValueBracketValueReference(@NotNull ALittlePropertyValueBracketValue element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();

        // 获取父节点
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
        ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffixList.indexOf(propertyValueSuffix);
        if (index == -1) return guessList;

        // 获取前一个类型
        ALittleReferenceUtil.GuessTypeInfo preType;
        if (index == 0) {
            preType = propertyValueFirstType.guessType();
        } else {
            preType = suffixList.get(index - 1).guessType();
        }

        // 获取类型
        if (preType.type == ALittleReferenceUtil.GuessType.GT_LIST) {
            guessList.add(preType.listSubType);
        } else if (preType.type == ALittleReferenceUtil.GuessType.GT_MAP) {
            guessList.add(preType.mapValueType);
        }

        return guessList;
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleValueStat value_stat = myElement.getValueStat();
        if (value_stat == null) return;

        // 获取父节点
        ALittlePropertyValueSuffix propertyValueSuffix = (ALittlePropertyValueSuffix)myElement.getParent();
        ALittlePropertyValue propertyValue = (ALittlePropertyValue)propertyValueSuffix.getParent();
        ALittlePropertyValueFirstType propertyValueFirstType = propertyValue.getPropertyValueFirstType();
        List<ALittlePropertyValueSuffix> suffixList = propertyValue.getPropertyValueSuffixList();

        // 获取所在位置
        int index = suffixList.indexOf(propertyValueSuffix);
        if (index == -1) return;

        // 获取前一个类型
        ALittleReferenceUtil.GuessTypeInfo preType;
        if (index == 0) {
            preType = propertyValueFirstType.guessType();
        } else {
            preType = suffixList.get(index - 1).guessType();
        }

        ALittleReferenceUtil.GuessTypeInfo keyGuessType = value_stat.guessType();
        // 获取类型
        if (preType.type == ALittleReferenceUtil.GuessType.GT_LIST) {
            if (!keyGuessType.value.equals("int") && !keyGuessType.value.equals("I64")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "索引值的类型必须是int或者是I64，不能是:" + keyGuessType.value);
            }
        } else if (preType.type == ALittleReferenceUtil.GuessType.GT_MAP) {
            try {
                boolean result = ALittleReferenceOpUtil.guessTypeEqual(myElement, preType.mapKeyType, value_stat, keyGuessType);
                if (!result) {
                    throw new ALittleReferenceUtil.ALittleReferenceException(value_stat, "索引值的类型不能是:" + keyGuessType.value);
                }
            } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "索引值的类型不能是:" + keyGuessType.value + " :" + e.getError());
            }
        }
    }
}
