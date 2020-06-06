package plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ALittleGlobalMethodDecReference extends ALittleReference<ALittleGlobalMethodDec> {
    public ALittleGlobalMethodDecReference(@NotNull ALittleGlobalMethodDec element, TextRange textRange) {
        super(element, textRange);
    }

    private void checkMsgStruct(@NotNull PsiElement element, ALittleGuess guess, HashSet<String> map) throws ALittleGuessException {
        if (guess instanceof ALittleGuessList) {
            ALittleGuessList guess_list = (ALittleGuessList) guess;
            checkMsgStruct(element, guess_list.sub_type, map);
        } else if (guess instanceof ALittleGuessMap) {
            ALittleGuessMap guess_map = (ALittleGuessMap) guess;
            if (!(guess_map.key_type instanceof ALittleGuessString)
                    && !(guess_map.key_type instanceof ALittleGuessInt)
                    && !(guess_map.key_type instanceof ALittleGuessLong))
                throw new ALittleGuessException(element, "Msg协议接口的参数使用二进制序列化，内部使用的Map的key必须是string,int,long类型");
            checkMsgStruct(element, guess_map.value_type, map);
        } else if (guess instanceof ALittleGuessStruct) {
            ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;
            // 如果有继承，那么就检查一下继承
            if (guess_struct.struct_dec.getStructExtendsDec() != null) {
                ALittleStructNameDec extends_name = guess_struct.struct_dec.getStructExtendsDec().getStructNameDec();
                if (extends_name != null) {
                    ALittleGuess extends_guess = extends_name.guessType();
                    ALittleGuessStruct extends_struct_guess = (ALittleGuessStruct) extends_guess;
                    checkMsgStruct(element, extends_struct_guess, map);
                }
            }

            if (guess_struct.getValueWithoutConst().equals("ALittle.ProtocolAnyStruct")) return;

            // 如果已经识别了，那么就直接返回
            if (map.contains(guess_struct.getValueWithoutConst())) return;
            map.add(guess_struct.getValueWithoutConst());

            ALittleStructBodyDec body_dec = guess_struct.struct_dec.getStructBodyDec();
            if (body_dec == null)
                throw new ALittleGuessException(element, "struct不完整");

            List<ALittleStructVarDec> var_dec_list = body_dec.getStructVarDecList();
            for (ALittleStructVarDec var_dec : var_dec_list) {
                guess = var_dec.guessType();
                checkMsgStruct(element, guess, map);
            }
        } else if (guess instanceof ALittleGuessClass) {
            throw new ALittleGuessException(element, "Msg协议接口的参数使用二进制序列化，内部不能使用类");
        } else if (guess instanceof ALittleGuessFunctor) {
            throw new ALittleGuessException(element, "Msg协议接口的参数使用二进制序列化，内部不能使用函数");
        } else if (guess.hasAny()) {
            throw new ALittleGuessException(element, "Msg协议接口的参数使用二进制序列化，内部不能使用any");
        }
    }

    private void checkJsonStruct(PsiElement element, ALittleGuess guess, HashSet<String> map) throws ALittleGuessException {
        if (guess instanceof ALittleGuessList) {
            ALittleGuessList guess_list = (ALittleGuessList) guess;
            checkJsonStruct(element, guess_list.sub_type, map);
        } else if (guess instanceof ALittleGuessMap) {
            ALittleGuessMap guess_map = (ALittleGuessMap) guess;
            if (!(guess_map.key_type instanceof ALittleGuessString))
                throw new ALittleGuessException(element, "http协议接口的参数使用json序列化，内部使用的Map的key必须是string类型");
            checkJsonStruct(element, guess_map.value_type, map);
        } else if (guess instanceof ALittleGuessStruct) {
            ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;
            // 如果有继承，那么就检查一下继承
            if (guess_struct.struct_dec.getStructExtendsDec() != null) {
                ALittleStructNameDec extends_name = guess_struct.struct_dec.getStructExtendsDec().getStructNameDec();
                if (extends_name != null) {
                    ALittleGuess extends_guess = extends_name.guessType();
                    ALittleGuessStruct extends_struct_guess = (ALittleGuessStruct) extends_guess;
                    checkJsonStruct(element, extends_struct_guess, map);
                }
            }

            // 如果已经识别了，那么就直接返回
            if (map.contains(guess_struct.getValueWithoutConst())) return;
            map.add(guess_struct.getValueWithoutConst());

            ALittleStructBodyDec body_dec = guess_struct.struct_dec.getStructBodyDec();
            if (body_dec == null)
                throw new ALittleGuessException(element, "struct不完整");

            List<ALittleStructVarDec> var_dec_list = body_dec.getStructVarDecList();
            for (ALittleStructVarDec var_dec : var_dec_list) {
                guess = var_dec.guessType();
                checkJsonStruct(element, guess, map);
            }
        } else if (guess instanceof ALittleGuessClass) {
            throw new ALittleGuessException(element, "http协议接口的参数使用json序列化，内部不能使用类");
        } else if (guess instanceof ALittleGuessFunctor) {
            throw new ALittleGuessException(element, "http协议接口的参数使用json序列化，内部不能使用函数");
        } else if (guess.hasAny()) {
            throw new ALittleGuessException(element, "http协议接口的参数使用json序列化，内部不能使用any");
        }
    }


    private void checkCmdError() throws ALittleGuessException {
        ALittleNamespaceElementDec parent = (ALittleNamespaceElementDec) myElement.getParent();
        String command_type = PsiHelper.getCommandDetail(parent.getModifierList());
        if (command_type == null) return;

        if (myElement.getTemplateDec() != null)
            throw new ALittleGuessException(myElement, "带Cmd的全局函数，不能使用模板");
    }

    private void checkProtoError() throws ALittleGuessException {
        ALittleNamespaceElementDec parent = (ALittleNamespaceElementDec) myElement.getParent();
        String proto_type = PsiHelper.getProtocolType(parent.getModifierList());
        if (proto_type == null) return;

        String co_text = PsiHelper.getCoroutineType(parent.getModifierList());

        ALittleMethodParamDec param_dec = myElement.getMethodParamDec();
        ALittleMethodReturnDec return_dec = myElement.getMethodReturnDec();

        String text = proto_type;

        if (myElement.getTemplateDec() != null)
            throw new ALittleGuessException(myElement, "带" + text + "的全局函数，不能使用模板");

        if (param_dec == null) throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有参数");
        List<ALittleAllType> return_list = new ArrayList<>();
        if (return_dec != null) {
            List<ALittleMethodReturnOneDec> return_one_list = return_dec.getMethodReturnOneDecList();
            for (ALittleMethodReturnOneDec return_one : return_one_list) {
                if (return_one.getMethodReturnTailDec() != null)
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，不能使用返回值占位符");
                ALittleAllType all_type = return_one.getAllType();
                if (all_type == null)
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值没有定义类型");
                return_list.add(all_type);
            }
        }

        List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
        List<ALittleGuess> param_guess_list = new ArrayList<>();
        for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
            if (param_one_dec.getMethodParamTailDec() != null)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，不能使用参数占位符");
            ALittleAllType all_type = param_one_dec.getAllType();
            if (all_type == null)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，参数没有定义类型");
            ALittleGuess all_type_guess = all_type.guessType();
            param_guess_list.add(all_type_guess);
        }

        List<ALittleGuess> return_guess_list = new ArrayList<>();
        for (ALittleAllType all_type : return_list) {
            ALittleGuess all_type_guess = all_type.guessType();
            return_guess_list.add(all_type_guess);
        }

        if (co_text.equals("async") && return_guess_list.size() > 0)
            throw new ALittleGuessException(return_dec, "带async修饰的函数，不能有返回值");

        // 检查参数个数
        if (param_guess_list.size() != 2) throw new ALittleGuessException(param_dec, "带" + text + "的全局函数，必须有两个参数");

        // 第二个参数
        if (!(param_guess_list.get(1) instanceof ALittleGuessStruct))
            throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第二个参数必须是struct");
        if (text.equals("Http")) {
            if (!co_text.equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (return_guess_list.size() != 1)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有一个返回值");
            // 第一个参数
            if (!param_guess_list.get(0).getValue().equals("ALittle.IHttpReceiver"))
                throw new ALittleGuessException(param_one_dec_list.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpReceiver");

            // 返回值
            if (!(return_guess_list.get(0) instanceof ALittleGuessStruct))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值必须是struct");

            checkJsonStruct(param_one_dec_list.get(1), param_guess_list.get(1), new HashSet<>());
            checkJsonStruct(return_list.get(0), return_guess_list.get(0), new HashSet<>());
        } else if (text.equals("HttpDownload")) {
            if (!co_text.equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            if (return_guess_list.size() != 2)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须有两个返回值，一个是string，一个是int");
            if (!return_guess_list.get(0).getValue().equals("string"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第一个参数必须是string");
            if (!return_guess_list.get(1).getValue().equals("int"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，第二个参数必须是int");
            // 第一个参数
            if (!param_guess_list.get(0).getValue().equals("ALittle.IHttpReceiver"))
                throw new ALittleGuessException(param_one_dec_list.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpReceiver");

            checkJsonStruct(param_one_dec_list.get(1), param_guess_list.get(1), new HashSet<>());
        } else if (text.equals("HttpUpload")) {
            if (!co_text.equals("await"))
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，必须使用await修饰");

            // 第一个参数
            if (!param_guess_list.get(0).getValue().equals("ALittle.IHttpFileReceiver"))
                throw new ALittleGuessException(param_one_dec_list.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IHttpFileReceiver");

            checkJsonStruct(param_one_dec_list.get(1), param_guess_list.get(1), new HashSet<>());

            // 返回值
            if (return_guess_list.size() != 0)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，不能有返回值");
        } else if (text.equals("Msg")) {
            if (return_guess_list.size() > 1)
                throw new ALittleGuessException(myElement, "带" + text + "的全局函数，最多只能有一个返回值");
            // 第一个参数
            if (!param_guess_list.get(0).getValue().equals("ALittle.IMsgCommon"))
                throw new ALittleGuessException(param_one_dec_list.get(0), "带" + text + "的全局函数，第一个参数必须是ALittle.IMsgCommon");

            checkMsgStruct(param_one_dec_list.get(1), param_guess_list.get(1), new HashSet<>());

            // 返回值
            if (return_guess_list.size() > 0) {
                if (!co_text.equals("await"))
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，并且有返回值，必须使用await修饰");

                if (!(return_guess_list.get(0) instanceof ALittleGuessStruct))
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，返回值必须是struct");
                checkMsgStruct(return_list.get(0), return_guess_list.get(0), new HashSet<>());
            } else {
                // 如果没有返回值，那么不能使用await，只能使用async，或者不使用
                if (co_text.equals("await"))
                    throw new ALittleGuessException(myElement, "带" + text + "的全局函数，当没有返回值时，不能使用await，可以使用async");
            }
        }
    }

    public void checkError() throws ALittleGuessException {
        if (myElement.getMethodNameDec() == null)
            throw new ALittleGuessException(myElement, "没有函数名");
        if (myElement.getMethodBodyDec() == null)
            throw new ALittleGuessException(myElement, "没有函数体");

        checkCmdError();
        checkProtoError();
    }
}
