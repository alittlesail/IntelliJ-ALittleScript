package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleGlobalMethodDecReference extends ALittleReference<ALittleGlobalMethodDec> {
    public ALittleGlobalMethodDecReference(@NotNull ALittleGlobalMethodDec element, TextRange textRange) {
        super(element, textRange);
    }

    private void checkStructExtends(@NotNull PsiElement element, @NotNull ALittleStructDec dec) throws ALittleGuessException {
        if (dec.getStructExtendsDec() != null) {
            throw new ALittleGuessException(element, "协议接口的参数不能使用具有继承的struct");
        }

        List<ALittleStructVarDec> varDecList = dec.getStructVarDecList();
        for (ALittleStructVarDec varDec : varDecList) {
            ALittleGuess guess = varDec.guessType();
            if (guess instanceof ALittleGuessStruct) {
                checkStructExtends(element, ((ALittleGuessStruct)guess).element);
            } else if (guess instanceof ALittleGuessClass) {
                throw new ALittleGuessException(element, "协议接口的参数内部不能使用类");
            } else if (guess instanceof ALittleGuessFunctor) {
                throw new ALittleGuessException(element, "协议接口的参数内部不能使用函数");
            }
        }
    }

    private void checkCmdError() throws ALittleGuessException {
        ALittleCmdModifier modifier = myElement.getCmdModifier();
        if (modifier == null) return;

        if (myElement.getTemplateDec() != null)
            throw new ALittleGuessException(myElement, "带@Cmd的全局函数，不能使用模板");

        PsiElement desc = modifier.getStringContent();
        if (desc == null) throw new ALittleGuessException(myElement, "带@Cmd的全局函数格式错误，请修改为 @Cmd \"指令描述\"");
    }

    private void checkProtoError() throws ALittleGuessException {
        ALittleProtoModifier modifier = myElement.getProtoModifier();
        if (modifier == null) return;

        ALittleMethodParamDec paramDec = myElement.getMethodParamDec();
        ALittleMethodReturnDec returnDec = myElement.getMethodReturnDec();

        String text = modifier.getText();

        if (myElement.getTemplateDec() != null)
            throw new ALittleGuessException(myElement, "带" + text + "的全局函数，不能使用模板");

        if (paramDec == null) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有参数");
        List<ALittleAllType> returnList = new ArrayList<>();
        if (returnDec != null) {
            returnList = returnDec.getAllTypeList();
        }

        List<ALittleMethodParamOneDec> oneDecList = paramDec.getMethodParamOneDecList();
        List<ALittleGuess> paramGuessList = new ArrayList<>();
        for (ALittleMethodParamOneDec oneDec : oneDecList) {
            paramGuessList.add(oneDec.getAllType().guessType());
        }

        List<ALittleGuess> returnGuessList = new ArrayList<>();
        for (ALittleAllType allType : returnList) {
            returnGuessList.add(allType.guessType());
        }

        // 检查参数个数
        if (paramGuessList.size() != 2) throw new ALittleGuessException(paramDec, "带" + text + "的全局函数，必须有两个参数");

        // 第二个参数
        if (!(paramGuessList.get(1) instanceof ALittleGuessStruct))
            throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第二个参数必须是struct");
        checkStructExtends(oneDecList.get(1), ((ALittleGuessStruct)paramGuessList.get(1)).element);

        if (text.equals("@Http")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 1) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpReceiver"))
                throw new ALittleGuessException(oneDecList.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpReceiver");

            // 返回值
            if (!(returnGuessList.get(0) instanceof ALittleGuessStruct))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值必须是struct");
            checkStructExtends(returnList.get(0), ((ALittleGuessStruct)returnGuessList.get(0)).element);
        } else if (text.equals("@HttpDownload")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 2) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有两个返回值，一个是string，一个是int");
            if (!returnGuessList.get(0).value.equals("string")) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第一个参数必须是string");
            if (!returnGuessList.get(1).value.equals("int")) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第二个参数必须是int");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpReceiver"))
                throw new ALittleGuessException(oneDecList.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpReceiver");
        } else if (text.equals("@HttpUpload")) {
            if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (returnGuessList.size() != 1) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IHttpFileReceiver"))
                throw new ALittleGuessException(oneDecList.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpFileReceiver");

            // 返回值
            if (!(returnGuessList.get(0) instanceof ALittleGuessStruct))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值必须是struct");
            checkStructExtends(returnList.get(0), ((ALittleGuessStruct)returnGuessList.get(0)).element);
        } else if (text.equals("@Msg")) {
            if (returnGuessList.size() > 0) {
                if (myElement.getCoModifier() == null || !myElement.getCoModifier().getText().equals("await"))
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，并且有返回值，必须使用await修饰");
            }

            if (returnGuessList.size() > 1) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，最多只能有一个返回值");
            // 第一个参数
            if (!paramGuessList.get(0).value.equals("ALittle.IMsgCommon"))
                throw new ALittleGuessException(oneDecList.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IMsgCommon");

            // 返回值
            if (!returnGuessList.isEmpty()) {
                if (!(returnGuessList.get(0) instanceof ALittleGuessStruct)) {
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值必须是struct");
                }
                checkStructExtends(returnList.get(0), ((ALittleGuessStruct)returnGuessList.get(0)).element);
            }
        }
    }

    public void checkError() throws ALittleGuessException {
        checkCmdError();
        checkProtoError();
    }
}
