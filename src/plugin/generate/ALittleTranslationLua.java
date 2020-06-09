package plugin.generate;

import com.intellij.psi.*;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.internal.StringUtil;
import plugin.alittle.PsiHelper;
import plugin.alittle.FileHelper;
import plugin.guess.*;
import plugin.psi.*;
import plugin.reference.ALittlePropertyValueMethodCallReference;

import java.util.*;

public class ALittleTranslationLua extends ALittleTranslation {
    // 命名域生成前缀
    private String m_alittle_gen_namespace_pre;

    // 标记是否使用rawset
    private boolean m_open_rawset = false;
    // 使用rawset的数量
    private int m_rawset_usecount = 0;

    // 当前文件需要处理的反射信息
    private Map<String, StructReflectInfo> m_reflect_map;
    // 是否需要all_struct
    private boolean m_need_all_struct = false;

    @Override
    protected String getExt() {
        return "lua";
    }

    // 生成bind命令
    @NotNull
    private String GenerateBindStat(ALittleBindStat bind_stat) throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = bind_stat.getValueStatList();

        String content = "Lua.Bind(";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String sub_content = GenerateValueStat(value_stat);
            param_list.add(sub_content);
        }
        content += StringUtil.join(param_list, ", ");
        content += ")";
        return content;
    }

    // 生成tcall命令
    @NotNull
    private String GenerateTcallStat(ALittleTcallStat tcall_stat) throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = tcall_stat.getValueStatList();

        String content = "Lua.TCall(";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String sub_content = GenerateValueStat(value_stat);
            param_list.add(sub_content);
        }
        content += StringUtil.join(param_list, ", ");
        content += ")";
        return content;
    }

    // 生成new ArrayList
    @NotNull
    private String GenerateOpNewListStat(ALittleOpNewListStat op_new_list) throws ALittleGuessException {
        List<ALittleValueStat> value_stat_list = op_new_list.getValueStatList();

        String content = "{";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String sub_content = GenerateValueStat(value_stat);
            param_list.add(sub_content);
        }
        content += StringUtil.join(param_list, ", ");
        content += "}";
        return content;
    }

    // 生成new
    @NotNull
    private String GenerateOpNewStat(ALittleOpNewStat op_new_stat) throws ALittleGuessException {
        String content = "";
        // 如果是通用类型
        ALittleGenericType generic_type = op_new_stat.getGenericType();
        if (generic_type != null) {
            // 如果是Map，那么直接返回{}
            ALittleGenericMapType map_type = generic_type.getGenericMapType();
            if (map_type != null) {
                content = "{}";
                return content;
            }

            // 如果是List，那么直接返回{}
            ALittleGenericListType list_type = generic_type.getGenericListType();
            if (list_type != null) {
                content = "{}";
                return content;
            }

            ALittleGenericFunctorType functor_type = generic_type.getGenericFunctorType();
            if (functor_type != null)
                throw new ALittleGuessException(generic_type, "Functor不能使用new来创建");
        }

        // 自定义类型
        ALittleCustomType custom_type = op_new_stat.getCustomType();
        if (custom_type != null) {
            ALittleGuess guess = custom_type.guessType();

            // 如果是Map，那么直接返回{}
            if (guess instanceof ALittleGuessMap) {
                content = "{}";
                return content;
            }

            // 如果是List，那么直接返回{}
            if (guess instanceof ALittleGuessList) {
                content = "{}";
                return content;
            }

            // 如果是结构体
            if (guess instanceof ALittleGuessStruct) {
                content = "{}";
                return content;
            }

            // 如果是类
            if (guess instanceof ALittleGuessClass) {
                // 生成custom_type名
                content = GenerateCustomType(custom_type);
                content += "(";
                List<String> param_list = new ArrayList<>();
                List<ALittleValueStat> value_stat_list = op_new_stat.getValueStatList();
                for (ALittleValueStat value_stat : value_stat_list) {
                    String sub_content = GenerateValueStat(value_stat);
                    param_list.add(sub_content);
                }
                content += StringUtil.join(param_list, ", ");
                content += ")";
                return content;
            }

            // 如果是函数模板参数
            if (guess instanceof ALittleGuessMethodTemplate) {
                ALittleGuessMethodTemplate guess_template = (ALittleGuessMethodTemplate) guess;
                if (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct) {
                    content = "{}";
                    return content;
                }

                if (guess_template.template_extends instanceof ALittleGuessClass || guess_template.is_class) {
                    content = guess_template.getValue() + "()";
                    return content;
                }
            }

            // 如果是类模板参数
            if (guess instanceof ALittleGuessClassTemplate) {
                ALittleGuessClassTemplate guess_template = (ALittleGuessClassTemplate) guess;
                if (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct) {
                    content = "{}";
                    return content;
                }

                if (guess_template.template_extends instanceof ALittleGuessClass) {
                    // 生成custom_type名
                    content = GenerateCustomType(custom_type);
                    content += "(";
                    List<String> param_list = new ArrayList<>();
                    List<ALittleValueStat> value_stat_list = op_new_stat.getValueStatList();
                    for (ALittleValueStat value_stat : value_stat_list) {
                        String sub_content = GenerateValueStat(value_stat);
                        param_list.add(sub_content);
                    }
                    content += StringUtil.join(param_list, ", ");
                    content += ")";
                    return content;
                }

                if (guess_template.is_class) {
                    throw new ALittleGuessException(null, "该模板只是class，不能确定它的构造参数参数");
                }
            }
        }

        throw new ALittleGuessException(null, "new 未知类型");
    }

    // 生成custom_type定义中的模板参数列表
    private void GenerateCustomTypeTemplateList(List<ALittleGuess> guess_list,
                                                List<String> template_param_list,
                                                List<String> template_param_name_list) throws ALittleGuessException {
        for (int index = 0; index < guess_list.size(); ++index) {
            ALittleGuess guess = guess_list.get(index);
            if (guess instanceof ALittleGuessClass) {
                ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                // 添加依赖
                addRelay(guess_class.class_dec);
                // 如果没有模板参数
                if (guess_class.template_list.size() == 0) {
                    // 获取类名
                    String name = guess_class.getValue();
                    // 如果是有using定义而来，就使用using_name
                    if (guess_class.using_name != null) name = guess_class.using_name;
                    // 拆分名称，检查命名域，如果与当前相同，或者是lua，那么就去掉
                    String[] split = name.split("\\.");
                    if (split.length == 2 && (split[0].equals(m_namespace_name) || split[0].equals("lua")))
                        template_param_list.add(split[1]);
                    else
                        template_param_list.add(name);
                    template_param_name_list.add(name);
                }
                // 有模板参数
                else {
                    // 检查模板参数
                    List<ALittleGuess> sub_guess_list = new ArrayList<>();
                    for (ALittleGuess sub_guess : guess_class.template_list) {
                        ALittleGuess value_guess = guess_class.template_map.get(sub_guess.getValueWithoutConst());
                        if (value_guess == null)
                            throw new ALittleGuessException(null, "参数模板没有填充完毕");
                        if (sub_guess.is_const && !value_guess.is_const) {
                            value_guess = value_guess.clone();
                            value_guess.is_const = true;
                            value_guess.updateValue();
                        }
                        sub_guess_list.add(value_guess);
                    }
                    // 获取子模板参数
                    List<String> sub_template_param_list = new ArrayList<>();
                    List<String> sub_template_param_name_list = new ArrayList<>();
                    GenerateCustomTypeTemplateList(sub_guess_list, sub_template_param_list, sub_template_param_name_list);

                    // 带命名域的类名
                    String full_class_name = guess_class.namespace_name + "." + guess_class.class_name;

                    // 计算实际类名
                    String class_name = full_class_name;
                    if (guess_class.namespace_name.equals(m_namespace_name) || guess_class.namespace_name.equals("lua"))
                        class_name = guess_class.class_name;

                    // 计算模板名
                    String template_name = full_class_name + "<" + StringUtil.join(sub_template_param_name_list, ", ") + ">";

                    String content = "Lua.Template(" + class_name;
                    content += ", \"" + template_name + "\"";
                    if (sub_template_param_list.size() > 0)
                        content += ", " + StringUtil.join(sub_template_param_list, ", ");

                    content += ")";
                    template_param_name_list.add(template_name);
                    template_param_list.add(content);
                }
            }
            // 如果是结构体
            else if (guess instanceof ALittleGuessStruct) {
                template_param_name_list.add(guess.getValue());
                m_need_all_struct = true;
                template_param_list.add("___all_struct[" + PsiHelper.structHash((ALittleGuessStruct) guess) + "]");
                GenerateReflectStructInfo((ALittleGuessStruct) guess);
            }
            // 如果是函数模板参数
            else if (guess instanceof ALittleGuessMethodTemplate) {
                ALittleGuessMethodTemplate guess_template = (ALittleGuessMethodTemplate) guess;
                template_param_list.add(guess_template.getValue());
                // 如果不是结构体就是类
                if (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct)
                    template_param_name_list.add("\".." + guess_template.getValue() + ".name..\"");
                else
                    template_param_name_list.add("\".." + guess_template.getValue() + ".__name..\"");
            }
            // 如果是类模板参数
            else if (guess instanceof ALittleGuessClassTemplate) {
                ALittleGuessClassTemplate guess_template = (ALittleGuessClassTemplate) guess;
                template_param_list.add("self.__class.__element[" + (index + 1) + "]");
                // 如果不是结构体就是类
                if (guess_template.template_extends instanceof ALittleGuessStruct || guess_template.is_struct)
                    template_param_name_list.add("\"..self.__class.__element[" + (index + 1) + "].name..\"");
                else
                    template_param_name_list.add("\"..self.__class.__element[" + (index + 1) + "].__name..\"");
            }
            // 其他类型，直接填nil
            else {
                template_param_name_list.add(guess.getValue());
                template_param_list.add("nil");
            }
        }
    }

    // 生成custom_type
    @NotNull
    private String GenerateCustomType(ALittleCustomType custom_type) throws ALittleGuessException {
        String content = "";

        ALittleGuess guess = custom_type.guessType();

        // 如果是结构体名，那么就当表来处理
        if (guess instanceof ALittleGuessStruct) {
            content = "{}";
            return content;
        }
        // 如果是类
        else if (guess instanceof ALittleGuessClass) {
            ALittleGuessClass guess_class = (ALittleGuessClass) guess;
            // 添加依赖
            addRelay(guess_class.class_dec);

            // 计算custom_type的类名，如果和当前文件命名与一致，或者是在lua命名域下，取消命名域前缀
            ALittleCustomTypeName name_dec = custom_type.getCustomTypeName();
            if (name_dec == null) throw new ALittleGuessException(custom_type, "表达式不完整");
            String class_name = name_dec.getText();

            ALittleCustomTypeDotId dot_id = custom_type.getCustomTypeDotId();
            if (dot_id != null) {
                ALittleCustomTypeDotIdName dot_id_name = dot_id.getCustomTypeDotIdName();
                if (dot_id_name != null) {
                    if (class_name.equals(m_namespace_name) || class_name.equals("lua"))
                        class_name = dot_id_name.getText();
                    else
                        class_name += "." + dot_id_name.getText();
                }
            }

            // 如果有填充模板参数，那么就模板模板
            List<ALittleAllType> all_type_list = null;
            if (custom_type.getCustomTypeTemplate() != null)
                all_type_list = custom_type.getCustomTypeTemplate().getAllTypeList();

            if (all_type_list != null && all_type_list.size() > 0) {
                // 获取所有模板参数
                List<ALittleGuess> guess_list = new ArrayList<ALittleGuess>();
                for (ALittleAllType all_type : all_type_list) {
                    ALittleGuess sub_guess = all_type.guessType();
                    guess_list.add(sub_guess);
                }
                // 生成模板信息
                List<String> template_param_list = new ArrayList<>();
                List<String> template_param_name_list = new ArrayList<>();
                GenerateCustomTypeTemplateList(guess_list, template_param_list, template_param_name_list);

                String template_name = guess_class.namespace_name + "." + guess_class.class_name;
                template_name += "<" + StringUtil.join(template_param_name_list, ", ") + ">";

                content = "Lua.Template(" + class_name;
                content += ", \"" + template_name + "\"";
                if (template_param_list.size() > 0)
                    content += ", " + StringUtil.join(template_param_list, ", ");
                content += ")";

                return content;
            } else {
                content = class_name;
                return content;
            }
        }
        // 如果是函数模板参数
        else if (guess instanceof ALittleGuessMethodTemplate) {
            content = guess.getValue();
            return content;
        }
        // 如果是类模板元素
        else if (guess instanceof ALittleGuessClassTemplate) {
            ALittleGuessClassTemplate guess_template = (ALittleGuessClassTemplate) guess;
            // 检查下标
            ALittleTemplatePairDec template_pair_dec = guess_template.template_pair_dec;
            ALittleTemplateDec template_dec = (ALittleTemplateDec) template_pair_dec.getParent();
            int index = template_dec.getTemplatePairDecList().indexOf(template_pair_dec);
            // 模板元素
            content = "self.__class.__element[" + (index + 1) + "]";
            return content;
        }

        throw new ALittleGuessException(null, "未知的表达式类型");
    }

    // 生成8级运算符
    @NotNull
    private String GenerateOp8Suffix(ALittleOp8Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp8().getText();
        if (op_string.equals("||")) op_string = "or";

        String value_functor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_functor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_functor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEe> suffix_ee_list = suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp8SuffixEe(suffix_ee);
            suffix_content_list.add(suffix_ee_result);
        }
        content = op_string + " " + value_functor_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }


    @NotNull
    private String GenerateOp8SuffixEe(ALittleOp8SuffixEe suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp8SuffixEe出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp8SuffixEx(ALittleOp8SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp8SuffixEx出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp8Stat(ALittleValueFactorStat value_factor_stat, ALittleOp8Stat op_8_stat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);

        ALittleOp8Suffix suffix = op_8_stat.getOp8Suffix();
        String suffix_result = GenerateOp8Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEx> suffix_ex_list = op_8_stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp8SuffixEx(suffix_ex);
            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成7级运算符
    @NotNull
    private String GenerateOp7Suffix(ALittleOp7Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp7().getText();
        if (op_string.equals("&&"))
            op_string = "and";

        String value_functor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_functor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_functor_result = GenerateOp2Value(suffix.getOp2Value());

        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEe> suffix_ee_list = suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffix_ee : suffix_ee_list) {
            String sub_content = GenerateOp7SuffixEe(suffix_ee);

            suffix_content_list.add(sub_content);
        }
        content = op_string + " " + value_functor_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }


    @NotNull
    private String GenerateOp7SuffixEe(ALittleOp7SuffixEe suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp7SuffixEe出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp7SuffixEx(ALittleOp7SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp7SuffixEx出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp7Stat(ALittleValueFactorStat value_factor_stat, ALittleOp7Stat op_7_stat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);


        ALittleOp7Suffix suffix = op_7_stat.getOp7Suffix();
        String suffix_result = GenerateOp7Suffix(suffix);


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEx> suffix_ex_list = op_7_stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp7SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成6级运算符
    @NotNull
    private String GenerateOp6Suffix(ALittleOp6Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp6().getText();
        if (op_string.equals("!="))
            op_string = "~=";

        String value_functor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_functor_result = GenerateValueFactorStat(suffix.getValueFactorStat());

        } else if (suffix.getOp2Value() != null) {
            value_functor_result = GenerateOp2Value(suffix.getOp2Value());

        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEe> suffix_ee_list = suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffix_ee : suffix_ee_list) {
            String sub_content = GenerateOp6SuffixEe(suffix_ee);

            suffix_content_list.add(sub_content);
        }
        content = op_string + " " + value_functor_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }


    @NotNull
    private String GenerateOp6SuffixEe(ALittleOp6SuffixEe suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp6SuffixEe出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp6SuffixEx(ALittleOp6SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp6SuffixEx出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp6Stat(ALittleValueFactorStat value_factor_stat, ALittleOp6Stat op_6_tat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);


        ALittleOp6Suffix suffix = op_6_tat.getOp6Suffix();
        String suffix_result = GenerateOp6Suffix(suffix);


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEx> suffix_ex_list = op_6_tat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp6SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成5级运算符

    @NotNull
    private String GenerateOp5Suffix(ALittleOp5Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp5().getText();

        String value_functor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_functor_result = GenerateValueFactorStat(suffix.getValueFactorStat());

        } else if (suffix.getOp2Value() != null) {
            value_functor_result = GenerateOp2Value(suffix.getOp2Value());

        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEe> suffix_ee_list = suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffix_ee : suffix_ee_list) {
            String sub_content = GenerateOp5SuffixEe(suffix_ee);

            suffix_content_list.add(sub_content);
        }
        content = op_string + " " + value_functor_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }


    @NotNull
    private String GenerateOp5SuffixEe(ALittleOp5SuffixEe suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else {
            String content = "";
            throw new ALittleGuessException(null, "GenerateOp5SuffixEe出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp5SuffixEx(ALittleOp5SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp5SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp5Stat(ALittleValueFactorStat value_factor_stat, ALittleOp5Stat op_5_stat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);


        ALittleOp5Suffix suffix = op_5_stat.getOp5Suffix();
        String suffix_result = GenerateOp5Suffix(suffix);


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEx> suffix_ex_list = op_5_stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp5SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成4级运算符
    @NotNull
    private String GenerateOp4Suffix(ALittleOp4Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp4().getText();

        String value_functor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_functor_result = GenerateValueFactorStat(suffix.getValueFactorStat());

        } else if (suffix.getOp2Value() != null) {
            value_functor_result = GenerateOp2Value(suffix.getOp2Value());

        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEe> suffix_ee_list = suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffix_ee : suffix_ee_list) {
            String sub_content = GenerateOp4SuffixEe(suffix_ee);

            suffix_content_list.add(sub_content);
        }
        content = op_string + " " + value_functor_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    @NotNull
    private String GenerateOp4SuffixEe(ALittleOp4SuffixEe suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp4SuffixEe出现未知的表达式");
        }
    }


    @NotNull
    private String GenerateOp4SuffixEx(ALittleOp4SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            throw new ALittleGuessException(null, "GenerateOp4SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp4Stat(ALittleValueFactorStat value_factor_stat, ALittleOp4Stat op_4_stat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);


        ALittleOp4Suffix suffix = op_4_stat.getOp4Suffix();
        String suffix_result = GenerateOp4Suffix(suffix);


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEx> suffix_ex_list = op_4_stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp4SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成3级运算符
    @NotNull
    private String GenerateOp3Suffix(ALittleOp3Suffix suffix) throws ALittleGuessException {
        String content = "";
        String op_string = suffix.getOp3().getText();

        String value_result;
        if (suffix.getValueFactorStat() != null) {
            value_result = GenerateValueFactorStat(suffix.getValueFactorStat());

        } else if (suffix.getOp2Value() != null) {
            value_result = GenerateOp2Value(suffix.getOp2Value());

        } else {
            throw new ALittleGuessException(null, "GenerateOp3Suffix出现未知的表达式");
        }

        content = op_string + " " + value_result;
        return content;
    }

    @NotNull
    private String GenerateOp3SuffixEx(ALittleOp3SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            String content = "";
            throw new ALittleGuessException(null, "GenerateOp3SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateValueOpStat(ALittleValueOpStat value_op_stat) throws ALittleGuessException {
        String content = "";

        ALittleValueFactorStat value_factor_stat = value_op_stat.getValueFactorStat();
        if (value_factor_stat == null) throw new ALittleGuessException(null, "表达式不完整");

        if (value_op_stat.getOp3Stat() != null)
            return GenerateOp3Stat(value_factor_stat, value_op_stat.getOp3Stat());

        if (value_op_stat.getOp4Stat() != null)
            return GenerateOp4Stat(value_factor_stat, value_op_stat.getOp4Stat());

        if (value_op_stat.getOp5Stat() != null)
            return GenerateOp5Stat(value_factor_stat, value_op_stat.getOp5Stat());

        if (value_op_stat.getOp6Stat() != null)
            return GenerateOp6Stat(value_factor_stat, value_op_stat.getOp6Stat());

        if (value_op_stat.getOp7Stat() != null)
            return GenerateOp7Stat(value_factor_stat, value_op_stat.getOp7Stat());

        if (value_op_stat.getOp8Stat() != null)
            return GenerateOp8Stat(value_factor_stat, value_op_stat.getOp8Stat());

        return GenerateValueFactorStat(value_factor_stat);
    }

    @NotNull
    private String GenerateOp3Stat(ALittleValueFactorStat value_factor_stat, ALittleOp3Stat op_3_stat) throws ALittleGuessException {
        String content = "";
        String value_functor_result = GenerateValueFactorStat(value_factor_stat);


        ALittleOp3Suffix suffix = op_3_stat.getOp3Suffix();
        String suffix_result = GenerateOp3Suffix(suffix);


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp3SuffixEx> suffix_ex_list = op_3_stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp3SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        content = value_functor_result + " " + suffix_result;
        if (suffix_content_list.size() > 0) content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成2级运算符
    @NotNull
    private String GenerateOp2SuffixEx(ALittleOp2SuffixEx suffix) throws ALittleGuessException {
        if (suffix.getOp3Suffix() != null)
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        else if (suffix.getOp4Suffix() != null)
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        else if (suffix.getOp5Suffix() != null)
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        else if (suffix.getOp6Suffix() != null)
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        else if (suffix.getOp7Suffix() != null)
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        else if (suffix.getOp8Suffix() != null)
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        else {
            String content = "";
            throw new ALittleGuessException(null, "GenerateOp2SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp2Value(ALittleOp2Value op_2_value) throws ALittleGuessException {
        String content = "";

        ALittleValueFactorStat value_factor = op_2_value.getValueFactorStat();
        if (value_factor == null)
            throw new ALittleGuessException(null, "GenerateOp2Stat单目运算没有操作对象");

        String value_stat_result = GenerateValueFactorStat(value_factor);

        String op_string = op_2_value.getOp2().getText();
        if (op_string.equals("!"))
            content += "not " + value_stat_result;
        else if (op_string.equals("-"))
            content += "-" + value_stat_result;
        else
            throw new ALittleGuessException(null, "GenerateOp2Stat出现未知类型");

        return content;
    }

    @NotNull
    private String GenerateOp2Stat(ALittleOp2Stat op_2_stat) throws ALittleGuessException {
        String content = GenerateOp2Value(op_2_stat.getOp2Value());


        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp2SuffixEx> suffix_ex_list = op_2_stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_ex_list) {
            String sub_content = GenerateOp2SuffixEx(suffix_ex);

            suffix_content_list.add(sub_content);
        }
        if (suffix_content_list.size() > 0)
            content += " " + StringUtil.join(suffix_content_list, " ");
        return content;
    }

    // 生成值表达式
    @NotNull
    private String GenerateValueStat(ALittleValueStat root) throws ALittleGuessException {
        if (root.getValueOpStat() != null)
            return GenerateValueOpStat(root.getValueOpStat());

        if (root.getOp2Stat() != null)
            return GenerateOp2Stat(root.getOp2Stat());

        if (root.getOpNewStat() != null)
            return GenerateOpNewStat(root.getOpNewStat());

        if (root.getOpNewListStat() != null)
            return GenerateOpNewListStat(root.getOpNewListStat());

        if (root.getBindStat() != null)
            return GenerateBindStat(root.getBindStat());

        if (root.getTcallStat() != null)
            return GenerateTcallStat(root.getTcallStat());

        String content = "";
        return content;
    }

    // 生成ValueFactorStat
    @NotNull
    private String GenerateValueFactorStat(ALittleValueFactorStat value_factor) throws ALittleGuessException {
        if (value_factor.getConstValue() != null)
            return GenerateConstValue(value_factor.getConstValue());

        if (value_factor.getReflectValue() != null)
            return GenerateReflectValue(value_factor.getReflectValue());

        if (value_factor.getPropertyValue() != null)
            return GeneratePropertyValue(value_factor.getPropertyValue());

        if (value_factor.getCoroutineStat() != null)
            return GenerateCoroutineStat(value_factor.getCoroutineStat());

        if (value_factor.getWrapValueStat() != null) {
            String content = "";
            String sub_content = GenerateValueStat(value_factor.getWrapValueStat().getValueStat());

            content = "(" + sub_content + ")";
            return content;
        }

        if (value_factor.getMethodParamTailDec() != null) {
            String content = value_factor.getMethodParamTailDec().getText();
            return content;
        }

        throw new ALittleGuessException(null, "GenerateValueFactor出现未知类型");
    }

    // 生成常量

    @NotNull
    private String GenerateConstValue(ALittleConstValue const_value) throws ALittleGuessException {
        String content = "";
        String const_value_string = const_value.getText();
        if (const_value_string.equals("null"))
            content += "nil";
        else
            content += const_value_string;
        return content;
    }

    // 生成反射
    @NotNull
    private String GenerateReflectValue(ALittleReflectValue reflect_value) throws ALittleGuessException {
        String content = "";
        if (reflect_value.getReflectCustomType() != null) {
            ALittleCustomType custom_type = reflect_value.getReflectCustomType().getCustomType();
            if (custom_type == null) throw new ALittleGuessException(null, "表达式不完整");

            ALittleGuess guess = custom_type.guessType();

            if (guess instanceof ALittleGuessStruct) {
                ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;
                m_need_all_struct = true;
                content = "___all_struct[" + PsiHelper.structHash(guess_struct) + "]";
                GenerateReflectStructInfo(guess_struct);
                return content;
            } else if (guess instanceof ALittleGuessClass) {
                ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                String name = guess_class.getValue();
                // 如果是using定义而来，那么就使用using_name
                if (guess_class.using_name != null) name = guess_class.using_name;
                String[] split = name.split("\\.");
                if (split.length == 2 && (split[0].equals(m_namespace_name) || split[0].equals("lua")))
                    content = split[1];
                else
                    content = name;
                return content;
            } else if (guess instanceof ALittleGuessMethodTemplate) {
                content = guess.getValue();
                return content;
            } else if (guess instanceof ALittleGuessClassTemplate) {
                ALittleGuessClassTemplate guess_template = (ALittleGuessClassTemplate) guess;
                ALittleTemplateDec template_dec = (ALittleTemplateDec) guess_template.template_pair_dec.getParent();
                int index = template_dec.getTemplatePairDecList().indexOf(guess_template.template_pair_dec);
                content = "self.__class.__element[" + (index + 1) + "]";
                return content;
            }
        } else if (reflect_value.getReflectValueStat() != null) {
            ALittleValueStat value_stat = reflect_value.getReflectValueStat().getValueStat();
            if (value_stat == null) throw new ALittleGuessException(null, "表达式不完整");

            ALittleGuess guess = value_stat.guessType();

            if (guess instanceof ALittleGuessStruct) {
                ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;
                m_need_all_struct = true;
                content = "___all_struct[" + PsiHelper.structHash(guess_struct) + "]";
                GenerateReflectStructInfo(guess_struct);
                return content;
            } else if (guess instanceof ALittleGuessClass) {
                String sub_content = GenerateValueStat(value_stat);

                content = "(" + sub_content + ").__class";
                return content;
            } else if (guess instanceof ALittleGuessTemplate) {
                ALittleGuessTemplate guess_template = (ALittleGuessTemplate) guess;
                if (guess_template.template_extends instanceof ALittleGuessClass || guess_template.is_class) {
                    String sub_content = GenerateValueStat(value_stat);

                    content = "(" + sub_content + ").__class";
                    return content;
                }

                throw new ALittleGuessException(null, "reflect不能反射struct类型模板对象");
            }
        }

        throw new ALittleGuessException(null, "reflect只能反射struct或者class以及class对象");
    }

    // 生成struct的反射信息
    @NotNull
    private void GenerateReflectStructInfo(ALittleGuess guess) throws ALittleGuessException {
        if (guess instanceof ALittleGuessList) {
            ALittleGuessList guess_list = (ALittleGuessList) guess;
            GenerateReflectStructInfo(guess_list.sub_type);

        } else if (guess instanceof ALittleGuessMap) {
            ALittleGuessMap guess_map = (ALittleGuessMap) guess;
            GenerateReflectStructInfo(guess_map.key_type);
            GenerateReflectStructInfo(guess_map.value_type);

        } else if (guess instanceof ALittleGuessStruct) {
            ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;

            if (m_reflect_map.containsKey(guess_struct.getValue())) return;

            boolean generate = false;
            // 如果是本文件的，那么就生成
            if (guess_struct.struct_dec.getContainingFile().getOriginalFile().getVirtualFile().getPath().equals(m_file_path))
                generate = true;
            // 如果不在同一个工程，那么就生成
            if (!FileHelper.calcModulePath(guess_struct.struct_dec, true).equals(m_project_path))
                generate = true;
                //  如果是同一个工程，并且是register，那么也要生成
            else if (guess_struct.is_register)
                generate = true;

            ALittleStructDec struct_dec = guess_struct.struct_dec;
            ALittleStructBodyDec body_dec = struct_dec.getStructBodyDec();
            if (body_dec == null) return;

            StructReflectInfo info = new StructReflectInfo();
            info.generate = generate;

            // 如果有继承，那么就获取继承
            ALittleStructExtendsDec extends_dec = struct_dec.getStructExtendsDec();
            if (extends_dec != null && extends_dec.getStructNameDec() != null) {
                ALittleGuess extends_guess = extends_dec.getStructNameDec().guessType();

                if (!(extends_guess instanceof ALittleGuessStruct))
                    throw new ALittleGuessException(null, extends_guess.getValue() + "不是结构体");
                GenerateReflectStructInfo(extends_guess);


                StructReflectInfo extends_info = m_reflect_map.get(extends_guess.getValue());
                if (extends_info == null)
                    throw new ALittleGuessException(null, extends_guess.getValue() + "反射信息生成失败");
                info.name_list.addAll(extends_info.name_list);
                info.type_list.addAll(extends_info.type_list);
                for (Map.Entry<String, String> pair : extends_info.option_map.entrySet()) {
                    info.option_map.put(pair.getKey(), pair.getValue());
                }
            }

            List<ALittleGuess> next_list = new ArrayList<ALittleGuess>();
            List<ALittleStructVarDec> var_dec_list = body_dec.getStructVarDecList();
            for (ALittleStructVarDec var_dec : var_dec_list) {
                ALittleGuess var_guess = var_dec.guessType();

                ALittleStructVarNameDec name_dec = var_dec.getStructVarNameDec();
                if (name_dec == null) throw new ALittleGuessException(null, guess_struct.getValue() + "没有定义变量名");
                info.name_list.add("\"" + name_dec.getText() + "\"");
                info.type_list.add("\"" + var_guess.getValue() + "\"");

                next_list.add(var_guess);
            }
            List<ALittleStructOptionDec> option_dec_list = body_dec.getStructOptionDecList();
            for (ALittleStructOptionDec option_dec : option_dec_list) {
                ALittleStructOptionNameDec name = option_dec.getStructOptionNameDec();
                if (name == null) throw new ALittleGuessException(null, guess_struct.getValue() + "option定义不完整");
                PsiElement value = option_dec.getTextContent();
                if (value == null) throw new ALittleGuessException(null, guess_struct.getValue() + "option定义不完整");
                info.option_map.put(name.getText(), value.getText());
            }

            String[] split_list = guess_struct.getValue().split("\\.");
            if (split_list.length != 2) return;

            info.name = guess_struct.getValue();
            info.ns_name = split_list[0];
            info.rl_name = split_list[1];
            info.hash_code = PsiHelper.structHash(guess_struct);

            info.content = "{\n";
            info.content += "name = \"" + info.name + "\", ";         // 全称
            info.content += "ns_name = \"" + info.ns_name + "\", ";          // 命名域名
            info.content += "rl_name = \"" + info.rl_name + "\", ";          // struct名
            info.content += "hash_code = " + info.hash_code + ",\n";        // 哈希值
            info.content += "name_list = {" + StringUtil.join(info.name_list, ",") + "},\n";      // 成员名列表
            info.content += "type_list = {" + StringUtil.join(info.type_list, ",") + "},\n";      // 类型名列表
            info.content += "option_map = {";
            int cur_count = 0;
            for (Map.Entry<String, String> pair : info.option_map.entrySet()) {
                info.content += pair.getKey() + "=" + pair.getValue();
                ++cur_count;
                if (info.option_map.size() != cur_count)
                    info.content += ",";
            }
            info.content += "}\n";      // 类型名列表
            info.content += "}";
            m_reflect_map.put(guess_struct.getValue(), info);

            for (ALittleGuess guess_info : next_list) {
                GenerateReflectStructInfo(guess_info);
            }
        }
    }

    // 对其他工程的枚举值进行优化处理，直接生成对应的值
    @NotNull
    private String GenerateEnumValue(ALittlePropertyValue prop_value) throws ALittleGuessException {
        String content = "";

        ALittlePropertyValueFirstType first_type = prop_value.getPropertyValueFirstType();
        if (first_type == null) return content;

        ALittleGuess custom_guess = first_type.guessType();


        ALittleGuessEnumName enum_name_guess = null;
        ALittlePropertyValueSuffix suffix = null;

        List<ALittlePropertyValueSuffix> suffix_list = prop_value.getPropertyValueSuffixList();
        if (custom_guess instanceof ALittleGuessNamespaceName) {
            if (suffix_list.size() != 2) return content;
            suffix = suffix_list.get(1);

            ALittleGuess guess = suffix_list.get(0).guessType();

            if (guess instanceof ALittleGuessEnumName)
                enum_name_guess = (ALittleGuessEnumName) guess;
            else
                enum_name_guess = null;
        } else if (custom_guess instanceof ALittleGuessEnumName) {
            if (suffix_list.size() != 1) return content;
            suffix = suffix_list.get(0);

            enum_name_guess = (ALittleGuessEnumName) custom_guess;
        }

        if (enum_name_guess == null) return content;
        if (suffix == null) return content;

        if (FileHelper.calcModulePath(enum_name_guess.enum_name_dec, true).equals(m_project_path)) return content;

        ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
        if (dot_id == null) return content;
        ALittlePropertyValueDotIdName dot_id_name = dot_id.getPropertyValueDotIdName();
        if (dot_id_name == null) return content;

        if (!(enum_name_guess.enum_name_dec.getParent() instanceof ALittleEnumDec))
            return content;
        ALittleEnumDec enum_dec = (ALittleEnumDec) enum_name_guess.enum_name_dec.getParent();

        ALittleEnumBodyDec body_dec = enum_dec.getEnumBodyDec();
        if (body_dec == null) return content;

        List<ALittleEnumVarDec> var_dec_list = body_dec.getEnumVarDecList();
        for (ALittleEnumVarDec var_dec : var_dec_list) {
            ALittleEnumVarNameDec name_dec = var_dec.getEnumVarNameDec();
            if (name_dec == null) continue;
            if (!name_dec.getText().equals(dot_id_name.getText()))
                continue;

            if (var_dec.getTextContent() != null)
                content = var_dec.getTextContent().getText();
            else if (var_dec.getNumberContent() != null)
                content = var_dec.getNumberContent().getText();

            return content;
        }

        return content;
    }

    // 生成属性值表达式
    @NotNull
    private String GeneratePropertyValue(ALittlePropertyValue prop_value) throws ALittleGuessException {
        // 对于枚举值进行特殊处理
        String content = GenerateEnumValue(prop_value);
        if (content.length() > 0) return content;

        // 用来标记第一个变量是不是lua命名域
        boolean is_lua_namespace = false;
        boolean is_alittle_namespace = false;

        // 获取开头的属性信息
        ALittlePropertyValueFirstType first_type = prop_value.getPropertyValueFirstType();
        ALittlePropertyValueCustomType custom_type = first_type.getPropertyValueCustomType();
        ALittlePropertyValueThisType this_type = first_type.getPropertyValueThisType();
        ALittlePropertyValueCastType cast_type = first_type.getPropertyValueCastType();

        ALittleGuess custom_guess = first_type.guessType();

        if (custom_type != null) {
            if ((custom_guess instanceof ALittleGuessFunctor && ((ALittleGuessFunctor) custom_guess).element instanceof ALittleGlobalMethodDec)
                    || custom_guess instanceof ALittleGuessClassName
                    || custom_guess instanceof ALittleGuessEnumName)
                addRelay(((ALittleGuess) custom_guess).getElement());

            if (custom_guess instanceof ALittleGuessNamespaceName) {
                is_lua_namespace = custom_guess.getValue().equals("lua");
                is_alittle_namespace = custom_guess.getValue().equals("alittle");
            }

            // 如果是lua命名域，那么就忽略
            if (!is_lua_namespace && !is_alittle_namespace)
                content += custom_type.getText();
        }
        // 如果是this，那么就变为self
        else if (this_type != null) {
            content += "self";
        } else if (cast_type != null) {
            ALittleValueFactorStat value_factor_stat = cast_type.getValueFactorStat();
            if (value_factor_stat == null) throw new ALittleGuessException(null, "cast没有填写转换对象");
            String sub_content = GenerateValueFactorStat(value_factor_stat);

            content += sub_content;
        }

        String split = ".";
        // 后面跟着后缀属性
        List<ALittlePropertyValueSuffix> suffix_list = prop_value.getPropertyValueSuffixList();
        for (int index = 0; index < suffix_list.size(); ++index) {
            // 获取当前后缀
            ALittlePropertyValueSuffix suffix = suffix_list.get(index);
            // 获取上一个后缀
            ALittlePropertyValueSuffix pre_suffix = null;
            if (index - 1 >= 0) pre_suffix = suffix_list.get(index - 1);
            // 获取下一个后缀
            ALittlePropertyValueSuffix next_suffix = null;
            if (index + 1 < suffix_list.size()) next_suffix = suffix_list.get(index + 1);

            // 如果当前是点
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null) {
                ALittlePropertyValueDotIdName dot_id_name = dot_id.getPropertyValueDotIdName();
                if (dot_id_name == null) throw new ALittleGuessException(null, "点后面没有定义属性对象");
                // 获取类型
                ALittleGuess guess = dot_id_name.guessType();

                split = ".";
                // 如果是函数名
                if (guess instanceof ALittleGuessFunctor) {
                    ALittleGuessFunctor guess_functor = (ALittleGuessFunctor) guess;
                    // 1. 是成员函数
                    // 2. 使用的是调用
                    // 3. 前一个后缀是类实例对象
                    // 那么就要改成使用语法糖
                    if (guess_functor.element instanceof ALittleClassMethodDec) {
                        if (next_suffix != null && next_suffix.getPropertyValueMethodCall() != null) {
                            // 获取前一个后缀的类型
                            ALittleGuess pre_guess = custom_guess;
                            if (pre_suffix != null) {
                                pre_guess = pre_suffix.guessType();
                            }

                            // 只要不是类名，那么肯定就是类实例对象，就是用语法糖
                            if (!(pre_guess instanceof ALittleGuessClassName))
                                split = ":";
                        }
                    }
                    // setter和getter需要特殊处理
                    else if (guess_functor.element instanceof ALittleClassSetterDec
                            || guess_functor.element instanceof ALittleClassGetterDec) {
                        if (next_suffix != null && next_suffix.getPropertyValueMethodCall() != null) {
                            ALittleGuess pre_guess = custom_guess;
                            if (pre_suffix != null) {
                                pre_guess = pre_suffix.guessType();

                            }

                            // 如果前一个后缀是类名，那么那么就需要获取setter或者getter来获取
                            if (pre_guess instanceof ALittleGuessClassName) {
                                // 如果是getter，那么一定是一个参数，比如ClassName.disabled(self)
                                // 如果是setter，那么一定是两个参数，比如ClassName.width(self, 100)
                                if (next_suffix.getPropertyValueMethodCall().getValueStatList().size() == 1)
                                    split = ".__getter.";
                                else
                                    split = ".__setter.";
                            }
                        }
                    }
                    // 全局函数
                    else if (guess_functor.element instanceof ALittleGlobalMethodDec) {
                        addRelay(guess_functor.element);
                    }
                } else if (guess instanceof ALittleGuessClassName || guess instanceof ALittleGuessEnumName) {
                    addRelay(((ALittleGuess) guess).getElement());
                }

                if (!is_lua_namespace && !is_alittle_namespace)
                    content += split;

                if (dot_id.getPropertyValueDotIdName() == null)
                    throw new ALittleGuessException(null, "点后面没有内容");

                String name_content = dot_id.getPropertyValueDotIdName().getText();
                // 因为lua中自带的string模块名和关键字string一样，所以把lua自动的改成string（大些开头）
                // 然后再翻译的时候，把string改成string
                if (is_lua_namespace && name_content.equals("String"))
                    name_content = "string";
                content += name_content;

                // 置为false，表示不是命名域
                is_lua_namespace = false;
                is_alittle_namespace = false;
                continue;
            }

            ALittlePropertyValueBracketValue bracket_value = suffix.getPropertyValueBracketValue();
            if (bracket_value != null) {
                ALittleValueStat value_stat = bracket_value.getValueStat();
                if (value_stat != null) {
                    String sub_content = GenerateValueStat(value_stat);

                    content += "[" + sub_content + "]";
                }
                continue;
            }

            ALittlePropertyValueMethodCall method_call = suffix.getPropertyValueMethodCall();
            if (method_call != null) {
                // 是否是调用了带注解函数，要进行特殊处理
                PsiReference refe = method_call.getReference();
                if (!(refe instanceof ALittlePropertyValueMethodCallReference))
                    throw new ALittleGuessException(null, "ALittlePropertyValueMethodCall.getReference()得到的不是ALittlePropertyValueMethodCallReference");

                ALittlePropertyValueMethodCallReference reference = (ALittlePropertyValueMethodCallReference) refe;
                ALittleGuess pre_type = reference.guessPreType();

                if (!(pre_type instanceof ALittleGuessFunctor))
                    throw new ALittleGuessException(null, "ALittlePropertyValueMethodCallReference.guessPreType()得到的不是ALittleGuessFunctor");
                ALittleGuessFunctor pre_type_functor = (ALittleGuessFunctor) pre_type;

                if (pre_type_functor.proto != null) {
                    if (pre_type_functor.proto.equals("Http"))
                        content = "ALittle.IHttpSender.Invoke";
                    else if (pre_type_functor.proto.equals("HttpDownload"))
                        content = "ALittle.IHttpFileSender.InvokeDownload";
                    else if (pre_type_functor.proto.equals("HttpUpload"))
                        content = "ALittle.IHttpFileSender.InvokeUpload";
                    else if (pre_type_functor.proto.equals("Msg")) {
                        if (pre_type_functor.return_list.size() == 0)
                            content = "ALittle.IMsgCommon.Invoke";
                        else
                            content = "ALittle.IMsgCommon.InvokeRPC";
                    }

                    if (pre_type_functor.param_list.size() != 2)
                        throw new ALittleGuessException(null, "GeneratePropertyValue:处理到MethodCall时发现带注解的函数参数数量不是2");
                    if (!(pre_type_functor.param_list.get(1) instanceof ALittleGuessStruct))
                        throw new ALittleGuessException(null, "GeneratePropertyValue:处理到MethodCall时发现带注解的函数第二个参数不是struct");
                    ALittleGuessStruct param_struct = (ALittleGuessStruct) pre_type_functor.param_list.get(1);
                    int msg_id = PsiHelper.structHash(param_struct);

                    List<String> param_list = new ArrayList<>();
                    if (pre_type_functor.proto.equals("Msg")) {
                        param_list.add("" + msg_id);
                        // 注册协议
                        GenerateReflectStructInfo(param_struct);


                        // 如果有返回值，那么也要注册返回值
                        if (pre_type_functor.return_list.size() == 2) {
                            if (!(pre_type_functor.return_list.get(1) instanceof ALittleGuessStruct))
                                throw new ALittleGuessException(null, "GeneratePropertyValue:处理到MethodCall时发现带注解的函数返回值不是struct");
                            GenerateReflectStructInfo((ALittleGuessStruct) pre_type_functor.return_list.get(1));

                        }
                    } else {
                        param_list.add("\"" + param_struct.getValue() + "\"");
                    }

                    List<ALittleValueStat> value_stat_list = method_call.getValueStatList();
                    for (ALittleValueStat value_stat : value_stat_list) {
                        String sub_content = GenerateValueStat(value_stat);

                        param_list.add(sub_content);
                    }

                    content += "(" + StringUtil.join(param_list, ", ") + ")";
                } else {
                    List<String> param_list = new ArrayList<>();

                    // 生成模板参数
                    List<ALittleGuess> template_list = reference.generateTemplateParamList();

                    for (ALittleGuess guess : template_list) {
                        if (guess instanceof ALittleGuessClass) {
                            ALittleGuessClass guess_class = (ALittleGuessClass) guess;
                            if (guess_class.namespace_name.equals(m_namespace_name) || guess_class.namespace_name.equals("lua"))
                                param_list.add(guess_class.class_name);
                            else
                                param_list.add(guess_class.getValue());
                        } else if (guess instanceof ALittleGuessStruct) {
                            m_need_all_struct = true;
                            param_list.add("___all_struct[" + PsiHelper.structHash((ALittleGuessStruct) guess) + "]");
                            GenerateReflectStructInfo((ALittleGuessStruct) guess);

                        } else if (guess instanceof ALittleGuessMethodTemplate) {
                            param_list.add(guess.getValue());
                        } else if (guess instanceof ALittleGuessClassTemplate) {
                            ALittleGuessClassTemplate guess_template = (ALittleGuessClassTemplate) guess;
                            ALittleTemplateDec template_dec = (ALittleTemplateDec) guess_template.template_pair_dec.getParent();
                            int template_index = template_dec.getTemplatePairDecList().indexOf(guess_template.template_pair_dec);
                            param_list.add("self.__class.__element[" + (template_index + 1) + "]");
                        } else {
                            throw new ALittleGuessException(null, "ALittlePropertyValueMethodCallReference.generateTemplateParamList()的返回列表中出现其他类型的ALittleGuess:" + guess.getValue());
                        }
                    }

                    // 生成实际参数
                    List<ALittleValueStat> value_stat_list = method_call.getValueStatList();
                    for (int i = 0; i < value_stat_list.size(); ++i) {
                        ALittleValueStat value_stat = value_stat_list.get(i);
                        // 如果是成员、setter、getter函数，第一个参数要放在最前面
                        if (i == 0 && !split.equals(":") && (pre_type_functor.element instanceof ALittleClassMethodDec
                                || pre_type_functor.element instanceof ALittleClassGetterDec
                                || pre_type_functor.element instanceof ALittleClassSetterDec)) {
                            String sub_content = GenerateValueStat(value_stat);

                            param_list.add(0, sub_content);
                        } else {
                            String sub_content = GenerateValueStat(value_stat);

                            param_list.add(sub_content);
                        }
                    }
                    content += "(" + StringUtil.join(param_list, ", ") + ")";
                }
                continue;
            }

            throw new ALittleGuessException(null, "GeneratePropertyValue出现未知类型");
        }

        return content;
    }

    // 生成co
    @NotNull
    private String GenerateCoroutineStat(ALittleCoroutineStat root) throws ALittleGuessException {
        String content = "___COROUTINE";
        return content;
    }

    // 生成using
    @NotNull
    private String GenerateUsingDec(List<ALittleModifier> modifier, ALittleUsingDec root, String pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleUsingNameDec name_dec = root.getUsingNameDec();
        if (name_dec == null) throw new ALittleGuessException(null, "using 没有定义名称");

        ALittleAllType all_type = root.getAllType();
        if (all_type == null) return content;

        ALittleCustomType custom_type = all_type.getCustomType();
        if (custom_type == null) return content;

        ALittleGuess guess = custom_type.guessType();

        if (!(guess instanceof ALittleGuessClass)) return content;

        content = pre_tab;

        if (PsiHelper.calcAccessType(modifier) == PsiHelper.ClassAccessType.PRIVATE)
            content += "local ";

        String sub_content = GenerateCustomType(custom_type);

        content += name_dec.getText() + " = " + sub_content + ";\n";
        return content;
    }

    // 生成异常表达式
    @NotNull
    private String GenerateThrowExpr(ALittleThrowExpr return_expr, String pre_tab) throws ALittleGuessException {
        String content = "";

        List<ALittleValueStat> value_stat_list = return_expr.getValueStatList();
        if (value_stat_list.size() == 0) throw new ALittleGuessException(null, "throw第一个参数必须是string类型");

        ALittleGuess guess_info = value_stat_list.get(0).guessType();


        if (!(guess_info instanceof ALittleGuessString))
            throw new ALittleGuessException(null, "throw第一个参数必须是string类型");
        if (value_stat_list.size() != 1)
            throw new ALittleGuessException(null, "throw只有一个参数");

        content = pre_tab + "Lua.Throw(";
        List<String> param_list = new ArrayList<>();
        for (int i = 0; i < value_stat_list.size(); ++i) {
            String sub_content = GenerateValueStat(value_stat_list.get(i));

            param_list.add(sub_content);
        }
        content += StringUtil.join(param_list, ", ");
        content += ")\n";
        return content;
    }

    // 生成断言表达式
    @NotNull
    private String GenerateAssertExpr(ALittleAssertExpr assert_expr, String pre_tab) throws ALittleGuessException {
        String content = "";

        List<ALittleValueStat> value_stat_list = assert_expr.getValueStatList();
        if (value_stat_list.size() != 2) throw new ALittleGuessException(null, "assert有且仅有两个参数，第一个是任意类型，第二个是string类型");

        ALittleGuess guess_info = value_stat_list.get(1).guessType();

        if (!(guess_info instanceof ALittleGuessString))
            throw new ALittleGuessException(null, "assert第二个参数必须是string类型");

        content = pre_tab + "Lua.Assert(";
        List<String> param_list = new ArrayList<>();
        for (int i = 0; i < value_stat_list.size(); ++i) {
            String sub_content = GenerateValueStat(value_stat_list.get(i));

            param_list.add(sub_content);
        }
        content += StringUtil.join(param_list, ", ");
        content += ")\n";
        return content;
    }

    // 生成1级运算符
    @NotNull
    private String GenerateOp1Expr(ALittleOp1Expr root, String pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null)
            throw new ALittleGuessException(null, "GenerateOp1Expr 没有操作值:" + root.getText());

        ALittleOp1 op_1 = root.getOp1();

        String value_stat_result = GenerateValueStat(value_stat);


        String op_1_string = op_1.getText();
        if (op_1_string.equals("++")) {
            content = pre_tab + value_stat_result + " = " + value_stat_result + " + 1\n";
            return content;
        }

        if (op_1_string.equals("--")) {
            content = pre_tab + value_stat_result + " = " + value_stat_result + " - 1\n";
            return content;
        }

        throw new ALittleGuessException(null, "GenerateOp1Expr未知类型:" + op_1_string);
    }

    // 生成变量定义以及赋值表达式
    @NotNull
    private String GenerateVarAssignExpr(ALittleVarAssignExpr root, String pre_tab, String pre_string) throws ALittleGuessException {
        String content = "";

        List<ALittleVarAssignDec> pair_dec_list = root.getVarAssignDecList();
        if (pair_dec_list.size() == 0)
            throw new ALittleGuessException(null, "局部变量没有变量名:" + root.getText());

        content = pre_tab + pre_string;

        List<String> name_list = new ArrayList<>();
        for (ALittleVarAssignDec pair_dec : pair_dec_list)
            name_list.add(pair_dec.getVarAssignNameDec().getText());
        content += StringUtil.join(name_list, ", ");

        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            content += "\n";
            return content;
        }

        String sub_content = GenerateValueStat(value_stat);

        content += " = " + sub_content + "\n";
        return content;
    }

    // 生成赋值表达式
    @NotNull
    private String GenerateOpAssignExpr(ALittleOpAssignExpr root, String pre_tab) throws ALittleGuessException {
        String content = "";
        List<ALittlePropertyValue> prop_value_list = root.getPropertyValueList();

        // 变量列表
        List<String> content_list = new ArrayList<>();
        for (ALittlePropertyValue prop_value : prop_value_list) {
            String sub_content = GeneratePropertyValue(prop_value);
            content_list.add(sub_content);
        }
        String prop_value_result = StringUtil.join(content_list, ", ");

        // 如果没有赋值，可以直接返回定义
        ALittleOpAssign op_assign = root.getOpAssign();
        ALittleValueStat value_stat = root.getValueStat();
        if (op_assign == null || value_stat == null) {
            content = pre_tab + prop_value_result + "\n";
            return content;
        }

        // 获取赋值表达式
        String value_stat_result = GenerateValueStat(value_stat);


        // 处理等号
        if (op_assign.getText().equals("=")) {
            // 这里做优化
            // 把 self._attr = value 优化为  rawset(self, "_attr", value)
            if (m_open_rawset && prop_value_list.size() == 1) {
                ALittlePropertyValue prop_value = prop_value_list.get(0);
                ALittlePropertyValueThisType this_type = prop_value.getPropertyValueFirstType().getPropertyValueThisType();
                if (this_type != null && prop_value.getPropertyValueSuffixList().size() == 1) {
                    ALittlePropertyValueSuffix suffix = prop_value.getPropertyValueSuffixList().get(0);
                    if (suffix.getPropertyValueDotId() != null) {
                        ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
                        if (dot_id != null && dot_id.getPropertyValueDotIdName() != null) {
                            String attr_name = dot_id.getPropertyValueDotIdName().getText();
                            ALittleGuess this_guess = this_type.guessType();


                            if (this_guess instanceof ALittleGuessClass) {
                                ALittleGuessClass this_guess_class = (ALittleGuessClass) this_guess;
                                List<PsiElement> var_name_list = new ArrayList<>();
                                PsiHelper.findClassAttrList(this_guess_class.class_dec
                                        , PsiHelper.sAccessPrivateAndProtectedAndPublic
                                        , PsiHelper.ClassAttrType.VAR
                                        , attr_name
                                        , var_name_list, 100);
                                if (var_name_list.size() > 0) {
                                    ++m_rawset_usecount;
                                    content = pre_tab + "___rawset(self, \"" + attr_name + "\", " + value_stat_result + ")\n";
                                    return content;
                                }
                            }
                        }
                    }
                }
            }

            content = pre_tab + prop_value_result + " = " + value_stat_result + "\n";
            return content;
        }

        String op_assign_string = op_assign.getText();

        // 如果出现多个前缀赋值，那么只能是=号
        if (content_list.size() > 1)
            throw new ALittleGuessException(null, "等号左边出现多个值的时候，只能使用=赋值符号:" + root.getText());

        content = "";
        switch (op_assign_string) {
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "%=":
                String op_string = op_assign_string.substring(0, 1);
                content = pre_tab + prop_value_result + " = " + prop_value_result + " " + op_string + " (" + value_stat_result + ")\n";
                break;
            default:
                throw new ALittleGuessException(null, "未知的赋值操作类型:" + op_assign_string);
        }
        return content;
    }

    // 生成else表达式
    @NotNull
    private Tuple2<String, Boolean> GenerateElseExpr(ALittleElseExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        boolean has_continue = false;

        String content = pre_tab;
        content += "else\n";
        List<ALittleAllExpr> all_expr_list;
        if (root.getAllExpr() != null) {
            all_expr_list = new ArrayList<>();
            all_expr_list.add(root.getAllExpr());
        } else if (root.getElseBody() != null) {
            all_expr_list = root.getElseBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");
            content += result.getFirst();
            has_continue = result.getSecond();
        }
        return new Tuple2<>(content, has_continue);
    }

    // 生成elseif表达式
    @NotNull
    private Tuple2<String, Boolean> GenerateElseIfExpr(ALittleElseIfExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        boolean has_continue = false;
        String content = "";
        ALittleElseIfCondition condition = root.getElseIfCondition();
        if (condition == null || condition.getValueStat() == null)
            throw new ALittleGuessException(null, "elseif (?) elseif没有条件值:" + root.getText());

        String value_stat_result = GenerateValueStat(condition.getValueStat());


        content = pre_tab;
        content += "elseif " + value_stat_result + " then\n";

        List<ALittleAllExpr> all_expr_list;
        if (root.getAllExpr() != null) {
            all_expr_list = new ArrayList<ALittleAllExpr>();
            all_expr_list.add(root.getAllExpr());
        } else if (root.getElseIfBody() != null) {
            all_expr_list = root.getElseIfBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            has_continue = result.getSecond();
        }
        return new Tuple2<>(content, has_continue);
    }

    // 生成if表达式
    @NotNull
    private Tuple2<String, Boolean> GenerateIfExpr(ALittleIfExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        boolean has_continue = false;
        String content = "";

        ALittleIfCondition condition = root.getIfCondition();
        if (condition == null || condition.getValueStat() == null)
            throw new ALittleGuessException(null, "if (?) if没有条件值:" + root.getText());

        String value_stat_result = GenerateValueStat(condition.getValueStat());


        content = pre_tab;
        content += "if " + value_stat_result + " then\n";

        List<ALittleAllExpr> all_expr_list;
        if (root.getAllExpr() != null) {
            all_expr_list = new ArrayList<ALittleAllExpr>();
            all_expr_list.add(root.getAllExpr());
        } else if (root.getIfBody() != null) {
            all_expr_list = root.getIfBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            has_continue = result.getSecond();
        }

        List<ALittleElseIfExpr> elseIfExprList = root.getElseIfExprList();
        for (ALittleElseIfExpr elseIfExpr : elseIfExprList) {
            Tuple2<String, Boolean> result = GenerateElseIfExpr(elseIfExpr, continue_num, pre_tab);

            content += result.getFirst();
            has_continue = result.getSecond();
        }

        ALittleElseExpr elseExpr = root.getElseExpr();
        if (elseExpr != null) {
            Tuple2<String, Boolean> result = GenerateElseExpr(elseExpr, continue_num, pre_tab);

            content += result.getFirst();
            has_continue = result.getSecond();
        }
        content += pre_tab + "end\n";
        return new Tuple2<>(content, has_continue);
    }

    // 生成for表达式
    @NotNull
    private String GenerateForExpr(ALittleForExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        String content = "";

        ALittleForCondition for_condition = root.getForCondition();
        if (for_condition == null) throw new ALittleGuessException(null, "表达式不完整");

        ALittleForPairDec for_pair_dec = for_condition.getForPairDec();
        if (for_pair_dec == null) throw new ALittleGuessException(null, "表达式不完整");

        content = pre_tab;

        String for_step_content = null;

        ALittleForStepCondition for_step_condition = for_condition.getForStepCondition();
        ALittleForInCondition for_in_condition = for_condition.getForInCondition();
        if (for_step_condition != null) {
            ALittleForStartStat for_start_stat = for_step_condition.getForStartStat();

            ALittleValueStat start_value_stat = for_start_stat.getValueStat();
            if (start_value_stat == null)
                throw new ALittleGuessException(null, "for 没有初始表达式:" + root.getText());

            String start_value_stat_result = GenerateValueStat(start_value_stat);


            ALittleVarAssignNameDec name_dec = for_pair_dec.getVarAssignNameDec();
            if (name_dec == null)
                throw new ALittleGuessException(null, "for 初始表达式没有变量名:" + root.getText());

            String start_var_name = name_dec.getText();

            content += "local " + start_var_name + " = " + start_value_stat_result + "\n";
            content += pre_tab + "while true do\n";

            ALittleForEndStat for_end_stat = for_step_condition.getForEndStat();
            if (for_end_stat == null)
                throw new ALittleGuessException(null, "for 没有结束表达式:" + root.getText());

            ALittleValueStat end_value_stat = for_end_stat.getValueStat();
            String sub_content = GenerateValueStat(end_value_stat);

            content += pre_tab + "\tif not(" + sub_content + ") then break end\n";

            ALittleForStepStat for_step_stat = for_step_condition.getForStepStat();
            if (for_step_stat == null)
                throw new ALittleGuessException(null, "for 没有步长表达式");

            ALittleValueStat step_value_stat = for_step_stat.getValueStat();
            sub_content = GenerateValueStat(step_value_stat);

            for_step_content = pre_tab + "\t" + start_var_name + " = " + start_var_name + "+(" + sub_content + ")\n";
        } else if (for_in_condition != null) {
            ALittleValueStat value_stat = for_in_condition.getValueStat();
            if (value_stat == null)
                throw new ALittleGuessException(null, "for : 没有遍历的对象:" + root.getText());

            String value_stat_result = GenerateValueStat(value_stat);

            List<ALittleForPairDec> src_pair_list = for_in_condition.getForPairDecList();
            List<ALittleForPairDec> pair_list = new ArrayList<>(src_pair_list);
            pair_list.add(0, for_pair_dec);
            List<String> pair_string_list = new ArrayList<>();
            for (ALittleForPairDec pair : pair_list) {
                ALittleVarAssignNameDec name_dec = pair.getVarAssignNameDec();
                if (name_dec == null)
                    throw new ALittleGuessException(null, "for in 没有变量名");
                pair_string_list.add(name_dec.getText());
            }

            String pair_type = PsiHelper.calcPairsTypeForLua(value_stat);


            // 如果for : 遇到迭代函数，那么就不用pair_type
            if (pair_type.length() == 0)
                content += "for " + StringUtil.join(pair_string_list, ", ") + " in " + value_stat_result + " do\n";
            else
                content += "for " + StringUtil.join(pair_string_list, ", ") + " in " + pair_type + "(" + value_stat_result + ") do\n";
        } else {
            throw new ALittleGuessException(null, "for(?) 无效的for语句:" + root.getText());
        }

        List<ALittleAllExpr> all_expr_list;
        if (root.getAllExpr() != null) {
            all_expr_list = new ArrayList<ALittleAllExpr>();
            all_expr_list.add(root.getAllExpr());
        } else if (root.getForBody() != null) {
            all_expr_list = root.getForBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }

        boolean has_continue_expr = false;
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            if (result.getSecond()) has_continue_expr = true;
        }

        if (has_continue_expr)
            content += pre_tab + "\t::continue_" + continue_num + "::\n";

        if (for_step_content != null)
            content += for_step_content;
        content += pre_tab + "end\n";
        return content;
    }

    // 生成while表达式
    @NotNull
    private String GenerateWhileExpr(ALittleWhileExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleWhileCondition condition = root.getWhileCondition();
        if (condition == null || condition.getValueStat() == null)
            throw new ALittleGuessException(null, "while (?) { ... } while中没有条件值");

        String value_stat_result = GenerateValueStat(condition.getValueStat());


        content = pre_tab + "while " + value_stat_result + " do\n";

        List<ALittleAllExpr> all_expr_list;
        if (root.getAllExpr() != null) {
            all_expr_list = new ArrayList<ALittleAllExpr>();
            all_expr_list.add(root.getAllExpr());
        } else if (root.getWhileBody() != null) {
            all_expr_list = root.getWhileBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }

        boolean has_continue_expr = false;
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            if (result.getSecond()) has_continue_expr = true;
        }

        if (has_continue_expr)
            content += pre_tab + "::continue_" + continue_num + "::\n";
        content += pre_tab + "end\n";
        return content;
    }

    // 生成do while表达式
    @NotNull
    private String GenerateDoWhileExpr(ALittleDoWhileExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleDoWhileCondition condition = root.getDoWhileCondition();
        if (condition == null || condition.getValueStat() == null)
            throw new ALittleGuessException(null, "do { ... } while(?) while中没有条件值");

        String value_stat_result = GenerateValueStat(condition.getValueStat());


        content = pre_tab + "repeat\n";

        List<ALittleAllExpr> all_expr_list;
        if (root.getDoWhileBody() != null) {
            all_expr_list = root.getDoWhileBody().getAllExprList();
        } else {
            throw new ALittleGuessException(null, "表达式不完整");
        }

        boolean has_continue_expr = false;
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            if (result.getSecond()) has_continue_expr = true;
        }

        if (has_continue_expr)
            content += pre_tab + "::continue_" + continue_num + "::\n";
        content += pre_tab + "until not(" + value_stat_result + ")\n";
        return content;
    }

    // 生成子表达式组
    @NotNull
    private Tuple2<String, Boolean> GenerateWrapExpr(ALittleWrapExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        String content = pre_tab + "do\n";

        boolean has_continue_expr = false;
        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, continue_num, pre_tab + "\t");

            content += result.getFirst();
            if (result.getSecond()) has_continue_expr = true;
        }

        content += pre_tab + "end\n";
        return new Tuple2<>(content, has_continue_expr);
    }

    // 生成return表达式
    @NotNull
    private String GenerateReturnExpr(ALittleReturnExpr root, String pre_tab) throws ALittleGuessException {
        String content = "";

        if (root.getReturnYield() != null) {
            content = pre_tab + "return coroutine.yield()\n";
            return content;
        }

        List<ALittleValueStat> value_stat_list = root.getValueStatList();
        List<String> content_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String sub_content = GenerateValueStat(value_stat);

            content_list.add(sub_content);
        }

        String value_stat_result = "";
        if (content_list.size() > 0)
            value_stat_result = " " + StringUtil.join(content_list, ", ");

        content = pre_tab + "return" + value_stat_result + "\n";
        return content;
    }

    // 生成break表达式
    @NotNull
    private Tuple2<String, Boolean> GenerateFlowExpr(ALittleFlowExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        boolean has_continue = false;

        String content = root.getText();
        if (content.startsWith("break")) {
            content = pre_tab + "break\n";
            return new Tuple2<>(content, has_continue);
        } else if (content.startsWith("continue")) {
            content = pre_tab + "goto continue_" + continue_num + "\n";
            has_continue = true;
            return new Tuple2<>(content, has_continue);
        }

        throw new ALittleGuessException(null, "未知的操作语句:" + content);
    }

    // 生成任意表达式
    @NotNull
    private Tuple2<String, Boolean> GenerateAllExpr(ALittleAllExpr root, int continue_num, String pre_tab) throws ALittleGuessException {
        boolean has_continue = false;

        String content = "";
        List<String> expr_list = new ArrayList<>();
        for (PsiElement child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleFlowExpr) {
                Tuple2<String, Boolean> result = GenerateFlowExpr((ALittleFlowExpr) child, continue_num, pre_tab);

                expr_list.add(result.getFirst());
                has_continue = result.getSecond();
            } else if (child instanceof ALittleReturnExpr) {
                String sub_content = GenerateReturnExpr((ALittleReturnExpr) child, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleDoWhileExpr) {
                String sub_content = GenerateDoWhileExpr((ALittleDoWhileExpr) child, continue_num + 1, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleWhileExpr) {
                String sub_content = GenerateWhileExpr((ALittleWhileExpr) child, continue_num + 1, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleForExpr) {
                String sub_content = GenerateForExpr((ALittleForExpr) child, continue_num + 1, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleIfExpr) {
                Tuple2<String, Boolean> result = GenerateIfExpr((ALittleIfExpr) child, continue_num, pre_tab);

                expr_list.add(result.getFirst());
                has_continue = result.getSecond();
            } else if (child instanceof ALittleOpAssignExpr) {
                String sub_content = GenerateOpAssignExpr((ALittleOpAssignExpr) child, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleVarAssignExpr) {
                String sub_content = GenerateVarAssignExpr((ALittleVarAssignExpr) child, pre_tab, "local ");

                expr_list.add(sub_content);
            } else if (child instanceof ALittleOp1Expr) {
                String sub_content = GenerateOp1Expr((ALittleOp1Expr) child, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleWrapExpr) {
                Tuple2<String, Boolean> result = GenerateWrapExpr((ALittleWrapExpr) child, continue_num, pre_tab);

                expr_list.add(result.getFirst());
                has_continue = result.getSecond();
            } else if (child instanceof ALittleThrowExpr) {
                String sub_content = GenerateThrowExpr((ALittleThrowExpr) child, pre_tab);

                expr_list.add(sub_content);
            } else if (child instanceof ALittleAssertExpr) {
                String sub_content = GenerateAssertExpr((ALittleAssertExpr) child, pre_tab);

                expr_list.add(sub_content);
            }
        }

        content = StringUtil.join(expr_list, "\n");
        return new Tuple2<>(content, has_continue);
    }

    // 生成枚举
    @NotNull
    private String GenerateEnum(ALittleEnumDec root, String pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleEnumNameDec name_dec = root.getEnumNameDec();
        if (name_dec == null) throw new ALittleGuessException(null, root.getText() + "没有定义枚举名");

        content += pre_tab + name_dec.getText() + " = {\n";

        int enum_value = -1;
        String enum_string;

        ALittleEnumBodyDec body_dec = root.getEnumBodyDec();
        if (body_dec == null) throw new ALittleGuessException(null, "表达式不完整");

        List<ALittleEnumVarDec> var_dec_list = body_dec.getEnumVarDecList();
        for (ALittleEnumVarDec var_dec : var_dec_list) {
            if (var_dec.getNumberContent() != null) {
                String value = var_dec.getNumberContent().getText();
                if (!PsiHelper.isInt(value))
                    throw new ALittleGuessException(null, var_dec.getNumberContent().getText() + "对应的枚举值必须是整数");

                if (value.startsWith("0x")) {
                    try {
                        enum_value = Integer.parseInt(value.substring(2), 16);
                    } catch (Exception e) {
                        throw new ALittleGuessException(null, "枚举值的十六进制数解析失败");
                    }
                } else {
                    try {
                        enum_value = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new ALittleGuessException(null, "枚举值的十进制数解析失败");
                    }
                }
                enum_string = value;
            } else if (var_dec.getTextContent() != null) {
                enum_string = var_dec.getTextContent().getText();
            } else {
                ++enum_value;
                enum_string = "" + enum_value;
            }

            content += pre_tab + "\t" + var_dec.getEnumVarNameDec().getText()
                    + " = " + enum_string + ",\n";
        }

        content += pre_tab + "}\n\n";

        return content;
    }

    // 生成类
    @NotNull
    private String GenerateClass(ALittleClassDec root, String pre_tab) throws ALittleGuessException {
        String content = "";

        ALittleClassNameDec name_dec = root.getClassNameDec();
        if (name_dec == null) throw new ALittleGuessException(null, "类没有定义类名");

        //类声明//////////////////////////////////////////////////////////////////////////////////////////
        String class_name = name_dec.getText();

        ALittleClassExtendsDec extends_dec = root.getClassExtendsDec();
        String extends_name = "";
        if (extends_dec != null && extends_dec.getClassNameDec() != null) {
            ALittleGuess guess = extends_dec.getClassNameDec().guessType();


            ALittleGuessClass guess_class = (ALittleGuessClass)guess;
            if (guess_class == null)
                throw new ALittleGuessException(extends_dec, "extends_dec.getClassNameDec().guessType 得到的不是ALittleGuessClass");
            extends_name = guess_class.namespace_name + "." + guess_class.class_name;

            // 继承属于定义依赖
            m_is_define_relay = true;
            addRelay(guess_class.class_dec);
            m_is_define_relay = false;
        }
        if (extends_name.equals(""))
            extends_name = "nil";
        else
            content += pre_tab + "assert(" + extends_name + ", \" extends class:" + extends_name + " is nil\")\n";

        content += pre_tab + class_name + " = "
                + "Lua.Class(" + extends_name + ", \""
                + PsiHelper.getNamespaceName(root) + "." + class_name + "\")\n\n";

        ALittleClassBodyDec class_body_dec = root.getClassBodyDec();
        if (class_body_dec == null) throw new ALittleGuessException(null, "表达式不完整");

        List<ALittleClassElementDec> class_element_list = class_body_dec.getClassElementDecList();

        // 获取所有成员变量初始化
        String var_init = "";
        boolean has_ctor = false;
        for (ALittleClassElementDec class_element_dec : class_element_list) {
            if (class_element_dec.getClassCtorDec() != null) {
                has_ctor = true;
                continue;
            }

            ALittleClassVarDec var_dec = class_element_dec.getClassVarDec();
            if (var_dec == null) continue;

            ALittleClassVarNameDec var_name_dec = var_dec.getClassVarNameDec();
            if (var_name_dec == null) continue;
            String var_name = var_name_dec.getText();

            ALittleClassVarValueDec var_value_dec = var_dec.getClassVarValueDec();
            if (var_value_dec == null) continue;
            {
                if (var_value_dec.getConstValue() != null) {
                    String var_value_content = GenerateConstValue(var_value_dec.getConstValue());
                    var_init += pre_tab + "\t" + "___rawset(self, \"" + var_name + "\", " + var_value_content + ")\n";
                } else if (var_value_dec.getOpNewStat() != null) {
                    String op_new_stat_content = GenerateOpNewStat(var_value_dec.getOpNewStat());
                    var_init += pre_tab + "\t" + "___rawset(self, \"" + var_name + "\", " + op_new_stat_content + ")\n";
                }
            }
        }

        // 如果没有ctor，并且有初始化函数
        if (!has_ctor && var_init.length() > 0) {
            content += pre_tab + "function " + class_name + ":Ctor()\n";
            content += var_init;
            content += pre_tab + "end\n";
            content += "\n";
        }

        int ctor_count = 0;
        for (ALittleClassElementDec class_element_dec : class_element_list) {
            if (PsiHelper.isLanguageEnable(class_element_dec.getModifierList()))
                continue;

            if (class_element_dec.getClassCtorDec() != null) {
                ++ctor_count;
                if (ctor_count > 1)
                    throw new ALittleGuessException(null, "class " + class_name + " 最多只能有一个构造函数");
                //构建构造函数//////////////////////////////////////////////////////////////////////////////////////////
                String ctor_param_list;

                ALittleClassCtorDec ctor_dec = class_element_dec.getClassCtorDec();
                List<String> param_name_list = new ArrayList<>();

                ALittleMethodParamDec param_dec = ctor_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                        ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                        if (param_name_dec == null)
                            throw new ALittleGuessException(null, "class " + class_name + " 的构造函数没有参数名");
                        param_name_list.add(param_name_dec.getText());
                    }
                }
                ctor_param_list = StringUtil.join(param_name_list, ", ");
                content += pre_tab + "function " + class_name + ":Ctor(" + ctor_param_list + ")\n";

                m_open_rawset = true;

                ALittleMethodBodyDec body_dec = ctor_dec.getMethodBodyDec();
                String all_expr_content = "";

                // 初始化成员变量
                if (var_init.length() > 0) {
                    all_expr_content += var_init;
                    var_init = "";
                }

                if (body_dec != null) {
                    List<ALittleAllExpr> all_expr_list = body_dec.getAllExprList();
                    for (ALittleAllExpr all_expr : all_expr_list) {
                        if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                            continue;
                        Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t");

                        all_expr_content += result.getFirst();
                    }
                }

                m_open_rawset = false;

                content += all_expr_content;
                content += pre_tab + "end\n";

                content += "\n";
            } else if (class_element_dec.getClassGetterDec() != null) {
                //构建getter函数///////////////////////////////////////////////////////////////////////////////////////
                ALittleClassGetterDec class_getter_dec = class_element_dec.getClassGetterDec();
                ALittleMethodNameDec class_method_name_dec = class_getter_dec.getMethodNameDec();
                if (class_method_name_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " getter函数没有函数名");

                content += pre_tab + "function " + class_name + ".__getter:" + class_method_name_dec.getText() + "()\n";

                ALittleMethodBodyDec class_method_body_dec = class_getter_dec.getMethodBodyDec();
                if (class_method_body_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " getter函数没有函数体");
                List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                        continue;
                    Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t");

                    content += result.getFirst();
                }
                content += pre_tab + "end\n";

                content += "\n";
            } else if (class_element_dec.getClassSetterDec() != null) {
                //构建setter函数///////////////////////////////////////////////////////////////////////////////////////
                ALittleClassSetterDec class_setter_dec = class_element_dec.getClassSetterDec();
                ALittleMethodNameDec class_method_name_dec = class_setter_dec.getMethodNameDec();
                if (class_method_name_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " setter函数没有函数名");
                ALittleMethodSetterParamDec param_dec = class_setter_dec.getMethodSetterParamDec();
                if (param_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " setter函数必须要有一个参数");

                ALittleMethodParamOneDec param_one_dec = param_dec.getMethodParamOneDec();
                if (param_one_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " setter函数必须要有一个参数");

                ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                if (param_name_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " 函数没有定义函数名");

                content += pre_tab + "function " + class_name + ".__setter:"
                        + class_method_name_dec.getText() + "("
                        + param_name_dec.getText() + ")\n";

                ALittleMethodBodyDec class_method_body_dec = class_setter_dec.getMethodBodyDec();
                if (class_method_body_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " setter函数没有函数体");

                List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                        continue;
                    Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t");

                    content += result.getFirst();
                }
                content += pre_tab + "end\n";

                content += "\n";
            } else if (class_element_dec.getClassMethodDec() != null) {
                //构建成员函数//////////////////////////////////////////////////////////////////////////////////////////
                ALittleClassMethodDec class_method_dec = class_element_dec.getClassMethodDec();
                ALittleMethodNameDec class_method_name_dec = class_method_dec.getMethodNameDec();
                if (class_method_name_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " 成员函数没有函数名");

                List<String> param_name_list = new ArrayList<>();

                ALittleTemplateDec template_dec = class_method_dec.getTemplateDec();
                if (template_dec != null) {
                    List<ALittleTemplatePairDec> pair_dec_list = template_dec.getTemplatePairDecList();
                    for (ALittleTemplatePairDec pair_dec : pair_dec_list) {
                        ALittleGuess guess = pair_dec.guessType();

                        if (guess instanceof ALittleGuessTemplate) {
                            ALittleGuessTemplate guess_template = (ALittleGuessTemplate)guess;
                            if (guess_template.template_extends != null || guess_template.is_class || guess_template.is_struct)
                                param_name_list.add(guess_template.getValue());
                        }
                    }
                }

                ALittleMethodParamDec param_dec = class_method_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                        if (param_one_dec.getMethodParamTailDec() != null) {
                            param_name_list.add(param_one_dec.getMethodParamTailDec().getText());
                            continue;
                        }
                        ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                        if (param_name_dec == null)
                            throw new ALittleGuessException(null, "class " + class_name + " 成员函数没有参数名");
                        param_name_list.add(param_name_dec.getText());
                    }
                }
                String method_param_list = StringUtil.join(param_name_list, ", ");
                content += pre_tab + "function " + class_name + ":"
                        + class_method_name_dec.getText()
                        + "(" + method_param_list + ")\n";

                String coroutine_type = PsiHelper.getCoroutineType(class_element_dec.getModifierList());
                if (coroutine_type.equals("await"))
                    content += pre_tab + "\tlocal ___COROUTINE = coroutine.running()\n";

                ALittleMethodBodyDec class_method_body_dec = class_method_dec.getMethodBodyDec();
                if (class_method_body_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " 成员函数没有函数体");
                List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                        continue;
                    Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t");

                    content += result.getFirst();
                }
                content += pre_tab + "end\n";

                if (coroutine_type.equals("async")) {
                    content += pre_tab
                            + class_name + "." + class_method_name_dec.getText()
                            + " = " + "Lua.CoWrap("
                            + class_name + "." + class_method_name_dec.getText()
                            + ")\n";
                }

                content += "\n";
            } else if (class_element_dec.getClassStaticDec() != null) {
                //构建静态函数//////////////////////////////////////////////////////////////////////////////////////////
                ALittleClassStaticDec class_static_dec = class_element_dec.getClassStaticDec();
                ALittleMethodNameDec class_method_name_dec = class_static_dec.getMethodNameDec();
                if (class_method_name_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " 静态函数没有函数名");
                List<String> param_name_list = new ArrayList<>();

                ALittleTemplateDec template_dec = class_static_dec.getTemplateDec();
                if (template_dec != null) {
                    List<ALittleTemplatePairDec> pair_dec_list = template_dec.getTemplatePairDecList();
                    for (ALittleTemplatePairDec pair_dec : pair_dec_list) {
                        ALittleGuess guess = pair_dec.guessType();

                        if (guess instanceof ALittleGuessTemplate) {
                            ALittleGuessTemplate guess_template = (ALittleGuessTemplate)guess;
                            if (guess_template.template_extends != null || guess_template.is_class || guess_template.is_struct)
                                param_name_list.add(guess_template.getValue());
                        }
                    }
                }

                ALittleMethodParamDec param_dec = class_static_dec.getMethodParamDec();
                if (param_dec != null) {
                    List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                    for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                        if (param_one_dec.getMethodParamTailDec() != null) {
                            param_name_list.add(param_one_dec.getMethodParamTailDec().getText());
                            continue;
                        }
                        ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                        if (param_name_dec == null)
                            throw new ALittleGuessException(null, "class " + class_name + " 静态函数没有参数名");
                        param_name_list.add(param_name_dec.getText());
                    }
                }

                String method_param_list = StringUtil.join(param_name_list, ", ");
                content += pre_tab + "function " + class_name + "."
                        + class_method_name_dec.getText()
                        + "(" + method_param_list + ")\n";

                String coroutine_type = PsiHelper.getCoroutineType(class_element_dec.getModifierList());
                if (coroutine_type.equals("await"))
                    content += pre_tab + "\tlocal ___COROUTINE = coroutine.running()\n";

                ALittleMethodBodyDec class_method_body_dec = class_static_dec.getMethodBodyDec();
                if (class_method_body_dec == null)
                    throw new ALittleGuessException(null, "class " + class_name + " 静态函数没有函数体");

                List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                        continue;
                    Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t");

                    content += result.getFirst();
                }
                content += pre_tab + "end\n";

                if (coroutine_type.equals("async")) {
                    content += pre_tab
                            + class_name + "." + class_method_name_dec.getText()
                            + " = " + "Lua.CoWrap("
                            + class_name + "." + class_method_name_dec.getText()
                            + ")\n";
                }
                content += "\n";
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        return content;
    }

    // 生成单例
    @NotNull
    private String GenerateInstance(List<ALittleModifier> modifier, ALittleInstanceDec root, String
            pre_tab) throws ALittleGuessException {
        String content = "";
        ALittleVarAssignExpr var_assign_expr = root.getVarAssignExpr();
        List<ALittleVarAssignDec> pair_dec_list = var_assign_expr.getVarAssignDecList();
        if (pair_dec_list.size() == 0)
            throw new ALittleGuessException(null, "局部变量没有变量名:" + root.getText());

        List<String> name_list = new ArrayList<>();
        for (ALittleVarAssignDec pair_dec : pair_dec_list)
            name_list.add(pair_dec.getVarAssignNameDec().getText());

        content = pre_tab;

        PsiHelper.ClassAccessType access_type = PsiHelper.calcAccessType(modifier);
        if (access_type == PsiHelper.ClassAccessType.PRIVATE) {
            content += "local ";
            content += StringUtil.join(name_list,", ");
        } else if (access_type == PsiHelper.ClassAccessType.PROTECTED) {
            content += StringUtil.join(name_list, ", ");
        } else if (access_type == PsiHelper.ClassAccessType.PUBLIC) {
            content += "_G.";
            content += StringUtil.join(name_list, ", _G.");
        }

        ALittleValueStat value_stat = var_assign_expr.getValueStat();
        if (value_stat == null) {
            content += " = nil\n";
            return content;
        }

        String sub_content = GenerateValueStat(value_stat);

        content += " = " + sub_content + "\n";
        return content;
    }

    // 生成全局函数
    @NotNull
    private String GenerateGlobalMethod(List<ALittleModifier> modifier, ALittleGlobalMethodDec
            root, String pre_tab) throws ALittleGuessException {
        String content = "";

        ALittleMethodNameDec global_method_name_dec = root.getMethodNameDec();
        if (global_method_name_dec == null)
            throw new ALittleGuessException(null, "全局函数没有函数名");

        // 函数名
        String method_name = global_method_name_dec.getText();

        // 参数名列表
        List<String> param_name_list = new ArrayList<>();

        // 模板列表
        ALittleTemplateDec template_dec = root.getTemplateDec();
        if (template_dec != null) {
            List<ALittleTemplatePairDec> pair_dec_list = template_dec.getTemplatePairDecList();
            for (ALittleTemplatePairDec pair_dec : pair_dec_list) {
                ALittleGuess guess = pair_dec.guessType();


                // 把模板名作为参数名
                if (guess instanceof ALittleGuessTemplate) {
                    ALittleGuessTemplate guess_template = (ALittleGuessTemplate)guess;
                    if (guess_template.template_extends != null || guess_template.is_class || guess_template.is_struct)
                        param_name_list.add(guess_template.getValue());
                }
            }
        }

        // 遍历参数列表
        ALittleMethodParamDec param_dec = root.getMethodParamDec();
        if (param_dec != null) {
            List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
            for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                if (param_one_dec.getMethodParamTailDec() != null) {
                    param_name_list.add(param_one_dec.getMethodParamTailDec().getText());
                    continue;
                }
                ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                if (param_name_dec == null)
                    throw new ALittleGuessException(null, "全局函数" + method_name + "没有参数名");
                param_name_list.add(param_name_dec.getText());
            }
        }

        // 私有判定
        boolean isPrivate = PsiHelper.calcAccessType(modifier) == PsiHelper.ClassAccessType.PRIVATE;

        String method_param_list = StringUtil.join(param_name_list, ", ");
        if (isPrivate) {
            content += pre_tab + "local " + method_name + "\n"
                    + pre_tab + method_name + " = " + "function(" + method_param_list + ")\n";
        } else {
            content += pre_tab + "function " + method_name + "(" + method_param_list + ")\n";
        }

        String coroutine_type = PsiHelper.getCoroutineType(modifier);
        if (coroutine_type.equals("await"))
            content += pre_tab + "\tlocal ___COROUTINE = coroutine.running()\n";

        ALittleMethodBodyDec class_method_body_dec = root.getMethodBodyDec();
        if (class_method_body_dec == null)
            throw new ALittleGuessException(null, "全局函数 " + method_name + " 没有函数体");

        List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            if (PsiHelper.isLanguageEnable(all_expr.getModifierList()))
                continue;
            Tuple2<String, Boolean> result = GenerateAllExpr(all_expr, 0, pre_tab +"\t")
            ;

            content += result.getFirst();
        }
        content += pre_tab + "end\n";

        // 协程判定
        if (coroutine_type.equals("async")) {
            content += pre_tab + method_name
                    + " = " + "Lua.CoWrap("
                    + method_name + ")\n";
        }

        content += "\n";

        // 注解判定
        String proto_type = PsiHelper.getProtocolType(modifier);

        PsiHelper.CommandInfo command_info = PsiHelper.getCommandDetail(modifier);
        if (proto_type != null) {
            if (param_dec == null) throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，必须有两个参数");
            List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
            if (one_dec_list.size() != 2 || one_dec_list.get(1).getAllType() == null) throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，必须有两个参数");

            ALittleGuess guess_param = one_dec_list.get(1).getAllType().guessType();


            if (!(guess_param instanceof ALittleGuessStruct))
                throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，第二个参数必须是struct");
            ALittleGuessStruct guess_param_struct = (ALittleGuessStruct)guess_param;

            List<ALittleAllType> return_list = new ArrayList<>();
            ALittleMethodReturnDec return_dec = root.getMethodReturnDec();
            if (return_dec != null) {
                List<ALittleMethodReturnOneDec> return_one_dec_list = return_dec.getMethodReturnOneDecList();
                for (ALittleMethodReturnOneDec return_one_dec : return_one_dec_list) {
                    if (return_one_dec.getAllType() != null)
                        return_list.add(return_one_dec.getAllType());
                }
            }

            ALittleGuess guess_return = null;
            if (return_list.size() == 1) {
                guess_return = return_list.get(0).guessType();

            }

            if (proto_type.equals("Http")) {
                if (return_list.size() != 1)
                    throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，有且仅有一个返回值");
                content += pre_tab + m_alittle_gen_namespace_pre + "RegHttpCallback(\"" + guess_param_struct.getValue() + "\", " + method_name + ")\n";
            } else if (proto_type.equals("HttpDownload")) {
                if (return_list.size() != 2)
                    throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，有且仅有两个返回值");
                content += pre_tab + m_alittle_gen_namespace_pre
                        + "RegHttpDownloadCallback(\""
                        + guess_param_struct.getValue() + "\", " + method_name + ")\n";
            } else if (proto_type.equals("HttpUpload")) {
                if (return_list.size() != 0)
                    throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，不能有返回值");
                content += pre_tab + m_alittle_gen_namespace_pre
                        + "RegHttpFileCallback(\""
                        + guess_param_struct.getValue() + "\", " + method_name + ")\n";
            } else if (proto_type.equals("Msg")) {
                if (return_list.size() > 1)
                    throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，最多只有一个返回值");
                GenerateReflectStructInfo(guess_param_struct);


                if (guess_return == null) {
                    content += pre_tab + m_alittle_gen_namespace_pre
                            + "RegMsgCallback(" + PsiHelper.structHash(guess_param_struct)
                            + ", " + method_name + ")\n";
                } else {
                    if (!(guess_return instanceof ALittleGuessStruct))
                        throw new ALittleGuessException(null, "带" + proto_type + "的全局函数，返回值必须是struct");
                    ALittleGuessStruct guess_return_struct = (ALittleGuessStruct)guess_return;

                    content += pre_tab + m_alittle_gen_namespace_pre
                            + "RegMsgRpcCallback(" + PsiHelper.structHash(guess_param_struct)
                            + ", " + method_name + ", " + PsiHelper.structHash(guess_return_struct)
                            + ")\n";

                    GenerateReflectStructInfo(guess_return_struct);

                }
            }
        } else if (command_info.type != null) {
            if (command_info.desc == null) command_info.desc = "";

            List<String> var_list = new ArrayList<>();
            List<String> name_list = new ArrayList<>();
            for (String param_name : param_name_list)
                name_list.add("\"" + param_name + "\"");

            if (param_dec != null) {
                List<ALittleMethodParamOneDec> one_dec_list = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec one_dec : one_dec_list) {
                    ALittleGuess all_type_guess = one_dec.getAllType().guessType();
                    var_list.add("\"" + all_type_guess.getValue() + "\"");
                }
            }

            content += pre_tab + m_alittle_gen_namespace_pre
                    + "RegCmdCallback(\"" + method_name + "\", " + method_name
                    + ", {" + StringUtil.join(var_list, ",") + "}, {" + StringUtil.join(name_list, ",")
                    + "}, \"" + command_info.desc + "\")\n";
        }

        return content;
    }

    // 生成根节点
    @NotNull
    @Override
    protected String generateRoot(List<ALittleNamespaceElementDec> element_dec_list) throws ALittleGuessException {
        String content = "-- ALittle Generate Lua And Do Not Edit This Line!";

        m_alittle_gen_namespace_pre = "ALittle.";
        if (m_namespace_name.equals("ALittle")) m_alittle_gen_namespace_pre = "";

        m_reflect_map = new HashMap<String, StructReflectInfo>();

        // 如果是lua命名域，那么就不要使用module;
        if (m_namespace_name.equals("lua") || m_namespace_name.equals("alittle"))
            content += "\n";
        else
            content += "\nmodule(\"" + m_namespace_name + "\", package.seeall)\n\n";

        String other_content = "";
        for (ALittleNamespaceElementDec child : element_dec_list) {
            if (PsiHelper.isLanguageEnable(child.getModifierList()))
                continue;

            // 处理结构体
            if (child.getStructDec() != null) {
                ALittleGuess guess = child.getStructDec().guessType();

                GenerateReflectStructInfo(guess);

            }
            // 处理enum
            else if (child.getEnumDec() != null) {
                String sub_content = GenerateEnum(child.getEnumDec(), "");

                other_content += sub_content;
            }
            // 处理class
            else if (child.getClassDec() != null) {
                String sub_content = GenerateClass(child.getClassDec(), "");

                other_content += sub_content;
            }
            // 处理instance
            else if (child.getInstanceDec() != null) {
                m_is_define_relay = true;
                String sub_content = GenerateInstance(child.getModifierList(), child.getInstanceDec(), "");
                m_is_define_relay = false;

                other_content += sub_content;
            }
            // 处理全局函数
            else if (child.getGlobalMethodDec() != null) {
                m_is_define_relay = false;
                String sub_content = GenerateGlobalMethod(child.getModifierList(), child.getGlobalMethodDec(), "");

                other_content += sub_content;
            }
            // 处理全局操作表达式
            else if (child.getOpAssignExpr() != null) {
                m_is_define_relay = true;
                String sub_content = GenerateOpAssignExpr(child.getOpAssignExpr(), "");
                m_is_define_relay = false;

                other_content += sub_content;
            }
            // 处理using
            else if (child.getUsingDec() != null) {
                m_is_define_relay = true;
                String sub_content = GenerateUsingDec(child.getModifierList(), child.getUsingDec(), "");
                m_is_define_relay = false;

                other_content += sub_content;
            }
        }

        if (m_rawset_usecount > 0) content += "local ___rawset = rawset\n";
        content += "local ___pairs = pairs\n";
        content += "local ___ipairs = ipairs\n";
        if (m_need_all_struct)
            content += "local ___all_struct = " + m_alittle_gen_namespace_pre + "GetAllStruct()\n";
        content += "\n";

        List<StructReflectInfo> info_list = new ArrayList<>();
        for (Map.Entry<String, StructReflectInfo> pair : m_reflect_map.entrySet()) {
            info_list.add(pair.getValue());
        }
        info_list.sort((a, b) -> a.hash_code - b.hash_code);
        for (StructReflectInfo info : info_list) {
            if (!info.generate) continue;
            content += m_alittle_gen_namespace_pre
                    + "RegStruct(" + info.hash_code + ", \"" + info.name + "\", " + info.content + ")\n";
        }
        content += "\n";

        content += other_content;

        return content;
    }
}
