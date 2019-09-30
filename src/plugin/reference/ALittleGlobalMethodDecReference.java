package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleGlobalMethodDecReference extends ALittleReference<ALittleGlobalMethodDec> {
    public ALittleGlobalMethodDecReference(@NotNull ALittleGlobalMethodDec element, TextRange textRange) {
        super(element, textRange);
    }

    private void checkStructExtends(@NotNull PsiElement element, @NotNull ALittleStructDec dec) throws ALittleReferenceUtil.ALittleReferenceException {
        if (dec.getStructExtendsDec() != null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(element, "协议接口的参数不能使用具有继承的struct");
        }

        List<ALittleStructVarDec> varDecList = dec.getStructVarDecList();
        for (ALittleStructVarDec varDec : varDecList) {
            ALittleReferenceUtil.GuessTypeInfo guessInfo = varDec.guessType();
            if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                checkStructExtends(element, (ALittleStructDec)guessInfo.element);
            } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "协议接口的参数内部不能使用类");
            } else if (guessInfo.type == ALittleReferenceUtil.GuessType.GT_FUNCTOR) {
                throw new ALittleReferenceUtil.ALittleReferenceException(element, "协议接口的参数内部不能使用函数");
            }
        }
    }

    public void checkError() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleProtoModifier modifier = myElement.getProtoModifier();
        if (modifier == null) return;

        ALittleMethodParamDec paramDec = myElement.getMethodParamDec();
        ALittleMethodReturnDec returnDec = myElement.getMethodReturnDec();

        String text = modifier.getText();

        if (paramDec == null) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须有参数");
        List<ALittleAllType> returnList = new ArrayList<>();
        if (returnDec != null) {
            returnList = returnDec.getAllTypeList();
        }

        List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
        List<ALittleReferenceUtil.GuessTypeInfo> paramGuessList = new ArrayList<>();
        for (ALittleMethodParamOneDec oneDec : oneDecList) {
            paramGuessList.add(oneDec.getAllType().guessType());
        }

        List<ALittleReferenceUtil.GuessTypeInfo> returnGuessList = new ArrayList<>();
        for (ALittleAllType allType : returnList) {
            returnGuessList.add(allType.guessType());
        }

        // 检查参数个数
        if (paramGuessList.size() != 2) throw new ALittleReferenceUtil.ALittleReferenceException(paramDec, "带" + text + "的全局函数，必须有两个参数");

        // 第二个参数
        if (paramGuessList.get(1).type != ALittleReferenceUtil.GuessType.GT_STRUCT)
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第二个参数必须是struct");
        checkStructExtends(oneDecList.get(1), (ALittleStructDec) paramGuessList.get(1).element);

        if (text.equals("@Http")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 1) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpClient"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpClient");

            // 返回值
            if (returnGuessList.get(0).type != ALittleReferenceUtil.GuessType.GT_STRUCT)
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，返回值必须是struct");
            checkStructExtends(returnList.get(0), (ALittleStructDec) returnGuessList.get(0).element);
        } else if (text.equals("@HttpDownload")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 2) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须有两个返回值，一个是string，一个是int");
            if (!returnGuessList.get(0).value.equals("string")) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第一个参数必须是string");
            if (!returnGuessList.get(1).value.equals("int")) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第二个参数必须是int");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpFileClient"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpFileClient");
        } else if (text.equals("@HttpUpload")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 1) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，必须有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpFileClient"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpFileClient");

            // 返回值
            if (returnGuessList.get(0).type != ALittleReferenceUtil.GuessType.GT_STRUCT)
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，返回值必须是struct");
            checkStructExtends(returnList.get(0), (ALittleStructDec) returnGuessList.get(0).element);
        } else if (text.equals("@Msg")) {
            if (returnGuessList.size() > 0) {
                if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，并且有返回值，必须使用await修饰");
            }

            if (returnGuessList.size() > 1) throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，最多只能有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IMsgClient"))
                throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，第一个参数必须是ALittle.IMsgClient");

            // 返回值
            if (!returnGuessList.isEmpty()) {
                if (returnGuessList.get(0).type != ALittleReferenceUtil.GuessType.GT_STRUCT)
                    throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "带" + text + "的全局函数，返回值必须是struct");
                checkStructExtends(returnList.get(0), (ALittleStructDec) returnGuessList.get(0).element);
            }
        }
    }
}
