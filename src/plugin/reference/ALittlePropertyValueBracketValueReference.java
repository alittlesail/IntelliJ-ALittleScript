package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
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
        ALittleValueStat valueStat = myElement.getValueStat();
        if (valueStat == null) return;

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

        ALittleReferenceUtil.GuessTypeInfo keyGuessType = valueStat.guessType();
        // 获取类型
        if (preType.type == ALittleReferenceUtil.GuessType.GT_LIST) {
            if (!keyGuessType.value.equals("int") && !keyGuessType.value.equals("I64")) {
                throw new ALittleReferenceUtil.ALittleReferenceException(valueStat, "索引值的类型必须是int或者是I64，不能是:" + keyGuessType.value);
            }
        } else if (preType.type == ALittleReferenceUtil.GuessType.GT_MAP) {
            try {
                ALittleReferenceOpUtil.guessTypeEqual(myElement, preType.mapKeyType, valueStat, keyGuessType);
            } catch (ALittleReferenceUtil.ALittleReferenceException e) {
                throw new ALittleReferenceUtil.ALittleReferenceException(e.getElement(), "索引值的类型不能是:" + keyGuessType.value + " :" + e.getError());
            }
        }

        {
            List<ALittleReferenceUtil.GuessTypeInfo> guessList = guessTypes();
            if (guessList.isEmpty()) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "该元素不能直接使用[]取值，请先cast");
            } else if (guessList.size() != 1) {
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "重复定义");
            }
        }
    }
}
