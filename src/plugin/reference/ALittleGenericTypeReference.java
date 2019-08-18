package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleGenericTypeReference extends ALittleReference {
    public ALittleGenericTypeReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        ALittleGenericType genericType = (ALittleGenericType)myElement;

        if (genericType.getGenericListType() != null) {
            ALittleGenericListType dec = genericType.getGenericListType();
            ALittleAllType allType = dec.getAllType();
            if (allType == null) return guessList;
            ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();

            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_LIST;
            info.value = "List<" + GuessInfo.value + ">";
            info.element = myElement;
            info.listSubType = GuessInfo;
            guessList.add(info);
        } else if (genericType.getGenericMapType() != null) {
            ALittleGenericMapType dec = genericType.getGenericMapType();
            List<ALittleAllType> allTypeList = dec.getAllTypeList();
            if (allTypeList.size() != 2) return guessList;
            ALittleReferenceUtil.GuessTypeInfo keyGuessInfo = allTypeList.get(0).guessType();
            ALittleReferenceUtil.GuessTypeInfo valueGuessInfo = allTypeList.get(1).guessType();

            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_MAP;
            info.value = "Map<" + keyGuessInfo.value + "," + valueGuessInfo.value + ">";
            info.element = myElement;
            info.mapKeyType = keyGuessInfo;
            info.mapValueType = valueGuessInfo;
            guessList.add(info);
        } else if (genericType.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = genericType.getGenericFunctorType();
            ALittleGenericFunctorParamType paramType = dec.getGenericFunctorParamType();

            ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
            info.type = ALittleReferenceUtil.GuessType.GT_FUNCTOR;
            info.value = "Functor<(";
            info.element = myElement;
            info.functorParamList = new ArrayList<>();
            info.functorReturnList = new ArrayList<>();

            if (paramType != null) {
                List<String> nameList = new ArrayList<>();
                List<ALittleAllType> allTypeList = paramType.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    nameList.add(GuessInfo.value);
                    info.functorParamList.add(GuessInfo);
                }
                info.value += String.join(",", nameList);
            }
            info.value += ")";
            ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
            if (return_type != null) {
                List<String> nameList = new ArrayList<>();
                List<ALittleAllType> allTypeList = return_type.getAllTypeList();
                for (ALittleAllType allType : allTypeList) {
                    ALittleReferenceUtil.GuessTypeInfo GuessInfo = allType.guessType();
                    nameList.add(GuessInfo.value);
                    info.functorReturnList.add(GuessInfo);
                }
                if (!nameList.isEmpty()) info.value += ":";
                info.value += String.join(",", nameList);
            }
            info.value += ">";
            guessList.add(info);
        }
    }
}
