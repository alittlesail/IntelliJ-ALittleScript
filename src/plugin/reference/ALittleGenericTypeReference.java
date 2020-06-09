package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.psi.*;

import java.util.ArrayList;
import java.util.List;

public class ALittleGenericTypeReference extends ALittleReference<ALittleGenericType> {
    public ALittleGenericTypeReference(@NotNull ALittleGenericType element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public List<ALittleGuess> guessTypes() throws ALittleGuessException {
        List<ALittleGuess> guess_list = new ArrayList<>();

        // 处理List
        if (myElement.getGenericListType() != null) {
            ALittleGenericListType dec = myElement.getGenericListType();
            ALittleAllType all_type = dec.getAllType();
            if (all_type == null) return guess_list;

            ALittleGuess guess = all_type.guessType();

            ALittleGuessList info = new ALittleGuessList(guess, false, false);
            info.updateValue();
            guess_list.add(info);
        }
        // 处理Map
        else if (myElement.getGenericMapType() != null) {
            ALittleGenericMapType dec = myElement.getGenericMapType();
            List<ALittleAllType> all_type_list = dec.getAllTypeList();
            if (all_type_list.size() != 2) return guess_list;

            ALittleGuess key_guess = all_type_list.get(0).guessType();
            ALittleGuess value_guess = all_type_list.get(1).guessType();

            ALittleGuessMap info = new ALittleGuessMap(key_guess, value_guess, false);
            info.updateValue();
            guess_list.add(info);
        }
        // 处理函数
        else if (myElement.getGenericFunctorType() != null) {
            ALittleGenericFunctorType dec = myElement.getGenericFunctorType();
            if (dec != null) {
                ALittleGuessFunctor info = new ALittleGuessFunctor(myElement);
                // 处理是不是const
                info.const_modifier = dec.getAllTypeConst() != null;
                // 处理是不是await
                info.await_modifier = (dec.getCoroutineModifier() != null && dec.getCoroutineModifier().getText().equals("await"));

                // 处理参数
                ALittleGenericFunctorParamType param_type = dec.getGenericFunctorParamType();
                if (param_type != null) {
                    List<ALittleGenericFunctorParamOneType> param_one_list = param_type.getGenericFunctorParamOneTypeList();
                    for (int i = 0; i < param_one_list.size(); ++i) {
                        ALittleGenericFunctorParamOneType param_one = param_one_list.get(i);
                        ALittleAllType all_type = param_one.getAllType();
                        if (all_type != null) {
                            ALittleGuess guess = all_type.guessType();
                            info.param_list.add(guess);
                            info.param_nullable_list.add(false);
                            info.param_name_list.add(guess.getValue());
                        } else {
                            ALittleGenericFunctorParamTail param_tail = param_one.getGenericFunctorParamTail();
                            if (param_tail == null)
                                throw new ALittleGuessException(param_one, "未知类型");
                            if (i + 1 != param_one_list.size())
                                throw new ALittleGuessException(param_one, "参数占位符必须定义在最后");
                            info.param_tail = new ALittleGuessParamTail(param_tail.getText());
                        }
                    }
                }

                // 处理返回值
                ALittleGenericFunctorReturnType return_type = dec.getGenericFunctorReturnType();
                if (return_type != null) {
                    List<ALittleGenericFunctorReturnOneType> return_one_list = return_type.getGenericFunctorReturnOneTypeList();
                    for (int i = 0; i < return_one_list.size(); ++i) {
                        ALittleGenericFunctorReturnOneType return_one = return_one_list.get(i);
                        ALittleAllType all_type = return_one.getAllType();
                        if (all_type != null) {
                            ALittleGuess guess = all_type.guessType();
                            info.return_list.add(guess);
                        } else {
                            ALittleGenericFunctorReturnTail return_tail = return_one.getGenericFunctorReturnTail();
                            if (return_tail == null)
                                throw new ALittleGuessException(return_one, "未知类型");
                            if (i + 1 != return_one_list.size())
                                throw new ALittleGuessException(return_one, "返回值占位符必须定义在最后");
                            info.param_tail = new ALittleGuessParamTail(return_tail.getText());
                        }
                    }
                }
                info.updateValue();
                guess_list.add(info);
            }
        }

        return guess_list;
    }
}
