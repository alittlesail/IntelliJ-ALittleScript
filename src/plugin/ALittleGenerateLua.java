package plugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import plugin.psi.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ALittleGenerateLua {
    String m_error = "";
    String m_namespace_name = "";
    List<String> m_protocol_list;

    private void copyStdLibrary(String module_base_path) {
        try {
            File file = new File(module_base_path + "/std");
            if (file.exists()) return;
            boolean result = file.mkdirs();

            // 适配代码
            String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);
            VirtualFile dir = null;

            if (jarPath.endsWith(".jar"))
                dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "adapter/Lua"));
            else
                dir = VfsUtil.findFileByIoFile(new File(jarPath +"/adapter/Lua"), true);

            if (dir != null) {
                VirtualFile[] file_list = dir.getChildren();
                if (file_list != null)
                {
                    for (VirtualFile virtualFile : file_list)
                    {
                        FileOutputStream file_out = new FileOutputStream(new File(module_base_path + "/std/" + virtualFile.getName()));
                        file_out.write(virtualFile.contentsToByteArray());
                        file_out.close();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PsiErrorElement checkErrorElement(PsiElement element, boolean full_check) {
        for(PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof PsiErrorElement) {
                return (PsiErrorElement)child;
            }

            if (full_check) {
                // 获取对应的定义
                List<PsiElement> guess_list = ALittleAnnotator.GetGuessList(element);

                // 检查未定义或者重复定义
                ALittleAnnotator.CheckErrorForGuessList(element, null, guess_list);

                // 枚举类型错误检查
                ALittleAnnotator.CheckErrorForEnum(element, null, guess_list);

                // 结构体类型错误检查
                ALittleAnnotator.CheckErrorForStruct(element, null, guess_list);

                // return语句返回的内容和函数定义的返回值相符
                ALittleAnnotator.CheckErrorForReturn(element, null, guess_list);

                // 赋值语句左右两方那个的类型检查
                ALittleAnnotator.CheckErrorForVarAssign(element, null, guess_list);

                // 赋值语句左右两方那个的类型检查
                ALittleAnnotator.CheckErrorForOpAssign(element, null, guess_list);

                // if elseif while dowhile 条件表达式检查
                ALittleAnnotator.CheckErrorForIfAndElseIfAndWhileAndDoWhile(element, null, guess_list);

                // for语句内部局部变量的类型
                ALittleAnnotator.CheckErrorForFor(element, null, guess_list);

                // 检查函数调用时参数个数，和参数类型
                ALittleAnnotator.CheckErrorForMethodCall(element, null, guess_list);

                // 检查中括号内部值的类型检查
                ALittleAnnotator.CheckErrorForBrackValue(element, null, guess_list);

                // 检查new表达式的参数
                ALittleAnnotator.CheckErrorForOpNewStat(element, null, guess_list);

                // 检查便捷List表达式
                ALittleAnnotator.CheckErrorForOpNewList(element, null, guess_list);
            }

            PsiErrorElement error = checkErrorElement(child, full_check);
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    public String GenerateLua(ALittleFile alittleFile, boolean full_check) {
        // 获取语法错误
        PsiErrorElement error = checkErrorElement(alittleFile, full_check);
        if (error != null) {
            return "有语法错误:" + error.getErrorDescription();
        }
        m_protocol_list = new ArrayList<>();

        List<ALittleNamespaceDec> namespace_list = PsiTreeUtil.getChildrenOfTypeAsList(alittleFile, ALittleNamespaceDec.class);
        if (namespace_list.isEmpty()) {
            return "没有定义命名域 namespace";
        }
        if (namespace_list.size() > 1) {
            return "代码生成失败 每个文件只有有一个命名域";
        }
        // 如果命名域有register标记，那么就不需要生成
        ALittleNamespaceDec namespace_dec = namespace_list.get(0);
        if (namespace_dec.getNamespaceRegisterDec() != null) {
            return null;
        }

        String content = GenerateNamespace(namespace_dec);
        if (content == null) return m_error;

        String protocol_content = null;
        if (!m_protocol_list.isEmpty())
            protocol_content = "[" + String.join(",", m_protocol_list) + "]";

        // 保存到文件
        try {
            FileIndexFacade facade = FileIndexFacade.getInstance(alittleFile.getProject());
            Module module = facade.getModuleForFile(alittleFile.getVirtualFile());
            if (module == null) {
                // 不是模块目录下的，不能生成文件
                return null;
            }
            String module_name = module.getName();
            String module_file_path = module.getModuleFilePath();
            String module_file_name = module_name + ".iml";
            if (!module_file_path.endsWith(module_file_name)) {
                return "模块文件路径:" + module_file_path + "没有以:" + module_file_name + "结尾";
            }
            String module_base_path = module_file_path.substring(0, module_file_path.length() - module_file_name.length());
            String file_path = alittleFile.getVirtualFile().getPath();
            if (!file_path.startsWith(module_base_path)) {
                return "当前文件不在模块路径下:" + file_path;
            }
            String lua_rel_path = file_path.substring(module_base_path.length());
            String protocol_rel_path = file_path.substring(module_base_path.length());

            String std_path = "Script";

            if (!lua_rel_path.startsWith("src")) {
                return "不支持该目录下的文件生成:" + file_path;
            }

            // 如果模块名是引擎库，那么需要做特殊处理
            if (module_name.equals("AEngine")) {
                // AEngine的工程文件在：集成开发环境安装目录/Module/ALittleIDE/Other/AEngine/AEngine.iml
                // 目标的目录是是在：集成开发环境安装目录/Engine
                lua_rel_path = "../../../../Engine" + lua_rel_path.substring("src".length());
                protocol_rel_path = null;
                std_path = "../../../../Engine";
            } else {
                lua_rel_path = "Script" + lua_rel_path.substring("src".length());
                protocol_rel_path = "Protocol" + protocol_rel_path.substring("src".length());
            }

            String ext = "alittle";
            if (!lua_rel_path.endsWith(ext)) {
                return "要生成的代码文件后缀名必须是alittle:" + file_path;
            }
            lua_rel_path = lua_rel_path.substring(0, lua_rel_path.length() - ext.length()) + "lua";
            if (protocol_rel_path != null)
                protocol_rel_path = protocol_rel_path.substring(0, protocol_rel_path.length() - ext.length()) + "json";
            String lua_full_path = module_base_path + lua_rel_path;

            File file = new File(lua_full_path);
            boolean result = file.getParentFile().mkdirs();
            FileOutputStream file_out = new FileOutputStream(new File(lua_full_path));
            file_out.write(content.getBytes(StandardCharsets.UTF_8));
            file_out.close();

            if (protocol_content != null && protocol_rel_path != null)
            {
                String protocol_full_path = module_base_path + protocol_rel_path;
                file = new File(protocol_full_path);
                result = file.getParentFile().mkdirs();
                file_out = new FileOutputStream(new File(protocol_full_path));
                file_out.write(protocol_content.getBytes(StandardCharsets.UTF_8));
                file_out.close();
            }

            // 复制标准库
            copyStdLibrary(module_base_path + std_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "代码写入文件时失败";
        } catch (IOException e) {
            e.printStackTrace();
            return "代码写入文件时失败";
        }

        return null;
    }

    private String GenerateOpNewList(ALittleOpNewList op_new_list) {
        List<ALittleValueStat> value_stat_list = op_new_list.getValueStatList();

        String content = "{";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String result = GenerateValueStat(value_stat);
            if (result == null) return null;
            param_list.add(result);
        }
        content += String.join(", ", param_list);
        content += "}";
        return content;
    }

    private String GenerateOpNewStat(ALittleOpNewStat op_new_stat) {
        // 如果是通用类型
        ALittleGenericType generic_type = op_new_stat.getGenericType();
        if (generic_type != null) {
            // 如果是Map，那么直接返回{}
            ALittleGenericMapType map_type = generic_type.getGenericMapType();
            if (map_type != null) return "{}";

            // 如果是List，那么直接返回{}
            ALittleGenericListType list_type = generic_type.getGenericListType();
            if (list_type != null) return "{}";

            ALittleGenericFunctorType functor_type = generic_type.getGenericFunctorType();
            if (functor_type != null) {
                m_error = "Functor不能使用new来创建";
                return null;
            }
        }

        // 自定义类型
        ALittleCustomType custom_type = op_new_stat.getCustomType();
        if (custom_type != null) {
            PsiElement guess_type = custom_type.getCustomTypeNameDec().guessType();
            // 如果是结构体名，那么就当表来处理
            if (guess_type instanceof ALittleStructDec) {
                return "{}";
            // 如果是类名
            } else if (guess_type instanceof ALittleClassDec) {
                // 如果是类名
                String content = "";
                ALittleCustomTypeNamespaceNameDec namespace_name_dec = custom_type.getCustomTypeNamespaceNameDec();
                if (namespace_name_dec != null)
                    content = namespace_name_dec.getIdContent().getText() + ".";
                content += custom_type.getCustomTypeNameDec().getIdContent().getText() + "(";

                List<String> param_list = new ArrayList<>();
                List<ALittleValueStat> value_stat_list = op_new_stat.getValueStatList();
                for (ALittleValueStat value_stat : value_stat_list) {
                    String result = GenerateValueStat(value_stat);
                    if (result == null) return null;
                    param_list.add(result);
                }
                content += String.join(", ", param_list);

                content += ")";
                return content;
            }
        }

        m_error = "new 未知类型";
        return null;
    }

    private String GenerateOp8Suffix(ALittleOp8Suffix suffix) {
        String op_string = suffix.getOp8().getText();
        if (op_string.equals("||")) {
            op_string = "or";
        }

        String value_factor_result = null;
        if (suffix.getValueFactor() != null) {
            value_factor_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_factor_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_factor_result == null) return null;
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEe> suffix_ee_list = suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp8SuffixEe(suffix_ee);
            if (suffix_ee_result == null) return null;
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp8SuffixEe(ALittleOp8SuffixEe suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else {
            m_error = "GenerateOp8SuffixEe出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp8SuffixEx(ALittleOp8SuffixEx suffix) {
        if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp8SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp8Stat(ALittleOp8Stat op_8_stat) {
        String value_factor_result = GenerateValueFactor(op_8_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp8Suffix suffix = op_8_stat.getOp8Suffix();
        String suffix_result = GenerateOp8Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEx> suffix_ex_list = op_8_stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp8SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp7Suffix(ALittleOp7Suffix suffix) {
        String op_string = suffix.getOp7().getText();
        if (op_string.equals("&&")) {
            op_string = "and";
        }

        String value_factor_result = null;
        if (suffix.getValueFactor() != null) {
            value_factor_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_factor_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_factor_result == null) return null;
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEe> suffix_ee_list = suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp7SuffixEe(suffix_ee);
            if (suffix_ee_result == null) return null;
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp7SuffixEe(ALittleOp7SuffixEe suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else {
            m_error = "GenerateOp7SuffixEe出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp7SuffixEx(ALittleOp7SuffixEx suffix) {
        if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp7SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp7Stat(ALittleOp7Stat op_7_stat) {
        String value_factor_result = GenerateValueFactor(op_7_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp7Suffix suffix = op_7_stat.getOp7Suffix();
        String suffix_result = GenerateOp7Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEx> suffix_ex_list = op_7_stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp7SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp6Suffix(ALittleOp6Suffix suffix) {
        String op_string = suffix.getOp6().getText();
        if (op_string.equals("!=")) {
            op_string = "~=";
        }

        String value_factor_result = null;
        if (suffix.getValueFactor() != null) {
            value_factor_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_factor_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_factor_result == null) return null;
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEe> suffix_ee_list = suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp6SuffixEe(suffix_ee);
            if (suffix_ee_result == null) return null;
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp6SuffixEe(ALittleOp6SuffixEe suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else {
            m_error = "GenerateOp6SuffixEe出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp6SuffixEx(ALittleOp6SuffixEx suffix) {
        if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp6SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp6Stat(ALittleOp6Stat op_6_stat) {
        String value_factor_result = GenerateValueFactor(op_6_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp6Suffix suffix = op_6_stat.getOp6Suffix();
        String suffix_result = GenerateOp6Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEx> suffix_ex_list = op_6_stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp6SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp5Suffix(ALittleOp5Suffix suffix) {
        String op_string = suffix.getOp5().getText();

        String value_factor_result = null;
        if (suffix.getValueFactor() != null) {
            value_factor_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_factor_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_factor_result == null) return null;
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEe> suffix_ee_list = suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp5SuffixEe(suffix_ee);
            if (suffix_ee_result == null) return null;
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp5SuffixEe(ALittleOp5SuffixEe suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else {
            m_error = "GenerateOp5SuffixEe出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp5SuffixEx(ALittleOp5SuffixEx suffix) {
        if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp5SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp5Stat(ALittleOp5Stat op_5_stat) {
        String value_factor_result = GenerateValueFactor(op_5_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp5Suffix suffix = op_5_stat.getOp5Suffix();
        String suffix_result = GenerateOp5Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEx> suffix_ex_list = op_5_stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp5SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp4Suffix(ALittleOp4Suffix suffix) {
        String op_string = suffix.getOp4().getText();

        String value_factor_result = null;
        if (suffix.getValueFactor() != null) {
            value_factor_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_factor_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_factor_result == null) return null;
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEe> suffix_ee_list = suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp4SuffixEe(suffix_ee);
            if (suffix_ee_result == null) return null;
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp4SuffixEe(ALittleOp4SuffixEe suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else {
            m_error = "GenerateOp4SuffixEe出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp4SuffixEx(ALittleOp4SuffixEx suffix) {
        if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp4SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp4Stat(ALittleOp4Stat op_4_stat) {
        String value_factor_result = GenerateValueFactor(op_4_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp4Suffix suffix = op_4_stat.getOp4Suffix();
        String suffix_result = GenerateOp4Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEx> suffix_ex_list = op_4_stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp4SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp3Suffix(ALittleOp3Suffix suffix) {
        String op_string = suffix.getOp3().getText();

        String value_result = null;
        if (suffix.getValueFactor() != null) {
            value_result = GenerateValueFactor(suffix.getValueFactor());
            if (value_result == null) return null;
        } else if (suffix.getOp2Value() != null) {
            value_result = GenerateOp2Value(suffix.getOp2Value());
            if (value_result == null) return null;
        } else {
            m_error = "GenerateOp3Suffix出现未知的表达式";
            return null;
        }

        return op_string + " " + value_result;
    }

    private String GenerateOp3SuffixEx(ALittleOp3SuffixEx suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp3SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp3Stat(ALittleOp3Stat op_3_stat) {
        String value_factor_result = GenerateValueFactor(op_3_stat.getValueFactor());
        if (value_factor_result == null) return null;

        ALittleOp3Suffix suffix = op_3_stat.getOp3Suffix();
        String suffix_result = GenerateOp3Suffix(suffix);
        if (suffix_result == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp3SuffixEx> suffix_ex_list = op_3_stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp3SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateOp2SuffixEx(ALittleOp2SuffixEx suffix) {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            m_error = "GenerateOp2SuffixEx出现未知的表达式";
            return null;
        }
    }

    private String GenerateOp2Value(ALittleOp2Value op_2_value) {
        String content = "";

        ALittleValueFactor value_factor = op_2_value.getValueFactor();
        if (value_factor == null) {
            m_error = "GenerateOp2Stat单目运算没有操作对象";
            return null;
        }

        String value_stat_result = GenerateValueFactor(value_factor);
        if (value_stat_result == null) return null;
        String op_string = op_2_value.getOp2().getText();
        if (op_string.equals("!")) {
            content += "not " + value_stat_result;
        } else if (op_string.equals("-")) {
            content += "-" + value_stat_result;
        } else {
            m_error = "GenerateOp2Stat出现未知类型";
            return null;
        }

        return content;
    }

    private String GenerateOp2Stat(ALittleOp2Stat op_2_stat) {
        String content = GenerateOp2Value(op_2_stat.getOp2Value());
        if (content == null) return null;

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp2SuffixEx> suffix_ex_list = op_2_stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_ex_list) {
            String suffix_ex_result = GenerateOp2SuffixEx(suffix_ex);
            if (suffix_ex_result == null) return null;
            suffix_content_list.add(suffix_ex_result);
        }
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    private String GenerateValueStat(ALittleValueStat root_stat) {
        ALittleValueFactor value_factor = root_stat.getValueFactor();
        if (value_factor != null) return GenerateValueFactor(value_factor);

        ALittleOp2Stat op_2_stat = root_stat.getOp2Stat();
        if (op_2_stat != null) return GenerateOp2Stat(op_2_stat);

        ALittleOp3Stat op_3_stat = root_stat.getOp3Stat();
        if (op_3_stat != null) return GenerateOp3Stat(op_3_stat);

        ALittleOp4Stat op_4_stat = root_stat.getOp4Stat();
        if (op_4_stat != null) return GenerateOp4Stat(op_4_stat);

        ALittleOp5Stat op_5_stat = root_stat.getOp5Stat();
        if (op_5_stat != null) return GenerateOp5Stat(op_5_stat);

        ALittleOp6Stat op_6_stat = root_stat.getOp6Stat();
        if (op_6_stat != null) return GenerateOp6Stat(op_6_stat);

        ALittleOp7Stat op_7_stat = root_stat.getOp7Stat();
        if (op_7_stat != null) return GenerateOp7Stat(op_7_stat);

        ALittleOp8Stat op_8_stat = root_stat.getOp8Stat();
        if (op_8_stat != null) return GenerateOp8Stat(op_8_stat);

        ALittleOpNewStat op_new_stat = root_stat.getOpNewStat();
        if (op_new_stat != null) return GenerateOpNewStat(op_new_stat);

        ALittleOpNewList op_new_list = root_stat.getOpNewList();
        if (op_new_list != null) return GenerateOpNewList(op_new_list);

        return "";
    }

    private String GenerateValueFactor(ALittleValueFactor value_factor) {
        ALittleConstValue const_value = value_factor.getConstValue();
        if (const_value != null) {
            return GenerateConstValue(const_value);
        }

        ALittlePropertyValue prop_value = value_factor.getPropertyValue();
        if (prop_value != null) {
            return GeneratePropertyValue(prop_value);
        }

        ALittleValueStatParen value_stat_paren = value_factor.getValueStatParen();
        if (value_stat_paren != null) {
            String result = GenerateValueStat(value_stat_paren.getValueStat());
            if (result == null) return null;
            return "(" + result + ")";
        }

        m_error = "GenerateValueFactor出现未知类型";
        return null;
    }

    private String GenerateConstValue(ALittleConstValue const_value) {
        String content = "";
        String const_value_string = const_value.getText();
        if (const_value_string.equals("null"))
            content += "nil";
        else
            content += const_value_string;
        return content;
    }

    private String GeneratePropertyValue(ALittlePropertyValue prop_value) {
        StringBuilder content = new StringBuilder();

        // 用来标记第一个变量是不是lua命名域
        boolean is_lua_namespace = false;

        // 通用类型的类型猜测
        PsiElement custom_guess_type = null;

        // 获取开头的属性信息
        ALittlePropertyValueCustomType custom_type = prop_value.getPropertyValueCustomType();
        ALittlePropertyValueThisType this_type = prop_value.getPropertyValueThisType();
        ALittlePropertyValueCastType cast_type = prop_value.getPropertyValueCastType();
        if (custom_type != null) {
            String custom_type_content = custom_type.getText();
            custom_guess_type = custom_type.guessType();
            if (custom_guess_type instanceof ALittleNamespaceNameDec && custom_type_content.equals("lua"))
                is_lua_namespace = true;

            // 如果是lua命名域，那么就忽略
            if (!is_lua_namespace)
                content.append(custom_type_content);
        // 如果是this，那么就变为self
        } else if (this_type != null) {
            content.append("self");
        } else if (cast_type != null) {
            String value = GenerateValueFactor(cast_type.getValueFactor());
            if (value == null) return null;
            content.append(value);
        }

        // 后面跟着后缀属性
        List<ALittlePropertyValueSuffix> suffix_list = prop_value.getPropertyValueSuffixList();
        for (int index = 0; index < suffix_list.size(); ++index)
        {
            // 获取当前后缀
            ALittlePropertyValueSuffix suffix = suffix_list.get(index);
            // 获取上一个后缀
            ALittlePropertyValueSuffix pre_suffix = null;
            if (index - 1 >= 0) pre_suffix = suffix_list.get(index - 1);
            // 获取下一个后缀
            ALittlePropertyValueSuffix next_suffix = null;
            if (index + 1 < suffix_list.size()) next_suffix = suffix_list.get(index + 1);

            // 如果当前是
            ALittlePropertyValueDotId dot_id = suffix.getPropertyValueDotId();
            if (dot_id != null) {
                // 获取类型
                PsiElement guess = dot_id.getPropertyValueDotIdName().guessType();
                if (guess == null) {
                    m_error = "未知的属性类型";
                    return null;
                }

                // 如果是lua命名域下的，判断当前后缀的类型。决定使用.还是:
                if (!is_lua_namespace)
                {
                    String split = ".";

                    // 如果是函数名
                    if (guess instanceof ALittleMethodNameDec) {
                        ALittleMethodNameDec method_name_dec = (ALittleMethodNameDec)guess;
                        // 1. 是成员函数
                        // 2. 使用的是调用
                        // 3. 前一个后缀是类实例对象
                        // 那么就要改成使用语法糖
                        if (method_name_dec.getParent() instanceof ALittleClassMethodDec) {
                            if (next_suffix != null && next_suffix.getPropertyValueMethodCallStat() != null) {
                                // 获取前一个后缀的类型
                                PsiElement pre_guess = null;
                                // pre_suffix为空，说明前面是（CustomType或者ThisType）
                                // 如果是ThisType说明一定是语法糖
                                // 如果是CustomType那么就判断下类型
                                if (pre_suffix == null) {
                                    if (custom_type != null) {
                                        if (custom_guess_type == null) {
                                            custom_guess_type = custom_type.guessType();
                                        }
                                        pre_guess = custom_guess_type;
                                    }
                                } else if (pre_suffix.getPropertyValueDotId() != null)
                                    pre_guess = pre_suffix.getPropertyValueDotId().getPropertyValueDotIdName().guessType();
                                else if (pre_suffix.getPropertyValueMethodCallStat() != null)
                                    pre_guess = pre_suffix.getPropertyValueMethodCallStat().guessType();
                                else if (pre_suffix.getPropertyValueBrackValueStat() != null)
                                    pre_guess = pre_suffix.getPropertyValueBrackValueStat().guessType();

                                // 只要不是类名，那么肯定就是类实例对象，就是用语法糖
                                if (!(pre_guess instanceof ALittleClassNameDec))
                                    split = ":";
                            }
                        // setter和getter需要特殊处理
                        } else if (method_name_dec.getParent() instanceof ALittleClassSetterDec
                                    || method_name_dec.getParent() instanceof ALittleClassGetterDec) {
                            if (next_suffix != null && next_suffix.getPropertyValueMethodCallStat() != null) {
                                PsiElement pre_guess = null;
                                // pre_suffix为空，说明前面是（CustomType或者ThisType）
                                // 如果是ThisType说明一定是语法糖
                                // 如果是CustomType那么就判断下类型
                                if (pre_suffix == null) {
                                    if (custom_type != null) {
                                        if (custom_guess_type == null) {
                                            custom_guess_type = custom_type.guessType();
                                        }
                                        pre_guess = custom_guess_type;
                                    }
                                } else if (pre_suffix.getPropertyValueDotId() != null)
                                    pre_guess = pre_suffix.getPropertyValueDotId().getPropertyValueDotIdName().guessType();
                                else if (pre_suffix.getPropertyValueMethodCallStat() != null)
                                    pre_guess = pre_suffix.getPropertyValueMethodCallStat().guessType();
                                else if (pre_suffix.getPropertyValueBrackValueStat() != null)
                                    pre_guess = pre_suffix.getPropertyValueBrackValueStat().guessType();

                                // 如果前一个后缀是类名，那么那么就需要获取setter或者getter来获取
                                if (pre_guess instanceof ALittleClassNameDec) {
                                    // 如果是getter，那么一定是一个参数，比如ClassName.disabled(self)
                                    // 如果是setter，那么一定是两个参数，比如ClassName.width(self, 100)
                                    if (next_suffix.getPropertyValueMethodCallStat().getValueStatList().size() == 1)
                                        split = ".__getter.";
                                    else
                                        split = ".__setter.";
                                }
                            }
                        }
                    }

                    content.append(split);
                }

                String name_content = dot_id.getPropertyValueDotIdName().getIdContent().getText();
                // 因为lua中自带的string模块名和关键字string一样，所以把lua自动的改成String（大些开头）
                // 然后再翻译的时候，把String改成string
                if (is_lua_namespace && name_content.equals("String"))
                    name_content = "string";
                content.append(name_content);

                // 置为false，表示不是命名域
                is_lua_namespace = false;
                continue;
            }

            ALittlePropertyValueBrackValueStat brack_value_stat = suffix.getPropertyValueBrackValueStat();
            if (brack_value_stat != null) {
                ALittleValueStat value_stat = brack_value_stat.getValueStat();
                String value_stat_result = GenerateValueStat(value_stat);
                if (value_stat_result == null) return null;
                content.append("[").append(value_stat_result).append("]");
                continue;
            }

            ALittlePropertyValueMethodCallStat method_call_stat = suffix.getPropertyValueMethodCallStat();
            if (method_call_stat != null) {
                List<ALittleValueStat> value_stat_list = method_call_stat.getValueStatList();
                List<String> param_list = new ArrayList<>();
                for (ALittleValueStat value_stat : value_stat_list) {
                    String result = GenerateValueStat(value_stat);
                    if (result == null) return null;
                    param_list.add(result);
                }
                content.append("(").append(String.join(", ", param_list)).append(")");
                continue;
            }

            m_error = "GeneratePropertyValue出现未知类型";
            return null;
        }

        return content.toString();
    }

    private String GeneratePropertyValueExpr(ALittlePropertyValueExpr root, String pre_tab) {
        String prop_value_result = GeneratePropertyValue(root.getPropertyValue());
        if (prop_value_result == null) return null;
        return pre_tab + prop_value_result + "\n";
    }

    private String GenerateOp1Expr(ALittleOp1Expr root, String pre_tab) {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            m_error = "GenerateOp1Expr 没有操作值:" + root.getText();
            return null;
        }
        ALittleOp1 op_1 = root.getOp1();

        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        String op_1_string = op_1.getText();
        if (op_1_string.equals("++"))
            return pre_tab + value_stat_result + " = " + value_stat_result + " + 1\n";

        if (op_1_string.equals("--"))
            return pre_tab + value_stat_result + " = " + value_stat_result + " - 1\n";

        m_error = "GenerateOp1Expr未知类型:" + op_1_string;
        return null;
    }

    private String GenerateVarAssignExpr(ALittleVarAssignExpr root, String pre_tab) {
        List<ALittleVarAssignPairDec> pair_dec_list = root.getVarAssignPairDecList();
        if (pair_dec_list.isEmpty()) {
            m_error = "局部变量没有变量名:" + root.getText();
            return null;
        }

        String content = pre_tab + "local ";

        List<String> name_list = new ArrayList<>();
        for (ALittleVarAssignPairDec pair_dec : pair_dec_list) {
            name_list.add(pair_dec.getVarAssignNameDec().getIdContent().getText());
        }
        content += String.join(", ", name_list);

        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null)
            return content + "\n";

        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        return content + " = " + value_stat_result + "\n";
    }

    private String GenerateOpAssignExpr(ALittleOpAssignExpr root, String pre_tab) {
        List<ALittlePropertyValue> prop_value_list = root.getPropertyValueList();
        List<String> content_list = new ArrayList<>();
        for (ALittlePropertyValue prop_value : prop_value_list) {
            String prop_value_result = GeneratePropertyValue(prop_value);
            if (prop_value_result == null) return null;
            content_list.add(prop_value_result);
        }

        String prop_value_result = String.join(", ", content_list);

        ALittleOpAssign op_assign = root.getOpAssign();
        ALittleValueStat value_stat = root.getValueStat();
        if (op_assign == null || value_stat == null) return pre_tab + prop_value_result + "\n";

        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        if (op_assign.getText().equals("="))
            return pre_tab + prop_value_result + " = " + value_stat_result + "\n";

        String op_assign_string = op_assign.getText();

        // 如果出现多个前缀赋值，那么只能是=号
        if (content_list.size() > 1)
        {
            m_error = "等号左边出现多个值的时候，只能使用=赋值符号:" + root.getText();
            return null;
        }

        String content = "";
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
                m_error = "未知的赋值操作类型:" + op_assign_string;
                return null;
        }
        return content;
    }

    private String GenerateElseExpr(ALittleElseExpr root, String pre_tab) {
        StringBuilder content = new StringBuilder(pre_tab);
        content.append("else\n");
        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        return content.toString();
    }

    private String GenerateElseIfExpr(ALittleElseIfExpr root, String pre_tab) {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            m_error = "elseif (?) elseif没有条件值:" + root.getText();
            return null;
        }
        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        StringBuilder content = new StringBuilder(pre_tab);
        content.append("elseif")
                .append(" ")
                .append(value_stat_result)
                .append(" then\n");

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        return content.toString();
    }

    private String GenerateIfExpr(ALittleIfExpr root, String pre_tab) {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            m_error = "if (?) if没有条件值:" + root.getText();
            return null;
        }
        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        StringBuilder content = new StringBuilder(pre_tab);
        content.append("if")
                .append(" ")
                .append(value_stat_result)
                .append(" then\n");

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }

        List<ALittleElseIfExpr> else_if_expr_list = root.getElseIfExprList();
        for (ALittleElseIfExpr else_if_expr : else_if_expr_list) {
            String result = GenerateElseIfExpr(else_if_expr, pre_tab);
            if (result == null) return null;
            content.append(result);
        }

        ALittleElseExpr else_expr = root.getElseExpr();
        if (else_expr != null) {
            String result = GenerateElseExpr(else_expr, pre_tab);
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    private String GenerateForExpr(ALittleForExpr root, String pre_tab) {
        ALittleForStepCondition for_step_condition = root.getForStepCondition();
        ALittleForInCondition for_in_condition = root.getForInCondition();

        StringBuilder content = new StringBuilder(pre_tab);
        if (for_step_condition != null) {
            ALittleForStartStat for_start_stat = for_step_condition.getForStartStat();

            ALittleValueStat start_value_stat = for_start_stat.getValueStat();
            if (start_value_stat == null) {
                m_error = "for 没有初始表达式:" + root.getText();
                return null;
            }
            String start_value_stat_result = GenerateValueStat(start_value_stat);
            if (start_value_stat_result == null) return null;

            String start_var_name = for_start_stat.getForPairDec().getVarAssignNameDec().getIdContent().getText();

            content.append("for ")
                    .append(start_var_name)
                    .append(" = ")
                    .append(start_value_stat_result)
                    .append(", ");

            ALittleForEndStat for_end_stat = for_step_condition.getForEndStat();

            ALittleValueStat end_value_stat = for_end_stat.getValueStat();
            String end_value_stat_result = GenerateValueStat(end_value_stat);
            if (end_value_stat_result == null) return null;
            content.append(end_value_stat_result);

            ALittleForStepStat for_step_stat = for_step_condition.getForStepStat();
            ALittleValueStat step_value_stat = for_step_stat.getValueStat();
            String step_value_stat_result = GenerateValueStat(step_value_stat);
            if (step_value_stat_result == null) return null;
            content.append(", ").append(step_value_stat_result);

            content.append(" do\n");
        } else if (for_in_condition != null) {
            ALittleValueStat value_stat = for_in_condition.getValueStat();
            if (value_stat == null) {
                m_error = "for in 没有遍历的对象:" + root.getText();
                return null;
            }

            String value_stat_result = GenerateValueStat(value_stat);
            if (value_stat_result == null) return null;

            List<ALittleForPairDec> pair_list = for_in_condition.getForPairDecList();
            List<String> pair_string_list = new ArrayList<>();
            for (ALittleForPairDec pair : pair_list) {
                pair_string_list.add(pair.getVarAssignNameDec().getIdContent().getText());
            }

            String pair_type = ALittleUtil.CalcPairsType(value_stat);
            if (pair_type == null) {
                m_error = "for in 的遍历对象表达式错误:" + root.getText();;
                return null;
            }

            // 如果foreach的参数数量不为2，那么就不用pair_type
            if (pair_type.isEmpty()) {
                content.append("for ")
                        .append(String.join(", ", pair_string_list))
                        .append(" in ")
                        .append(value_stat_result)
                        .append(" do\n");
            } else {
                content.append("for ")
                        .append(String.join(", ", pair_string_list))
                        .append(" in ")
                        .append(pair_type)
                        .append("(")
                        .append(value_stat_result)
                        .append(") do\n");
            }
        } else {
            m_error = "for(?) 无效的for语句:" + root.getText();
            return null;
        }

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }

        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    private String GenerateWhileExpr(ALittleWhileExpr root, String pre_tab) {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            m_error = "while (?) { ... } while中没有条件值";
            return null;
        }
        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        StringBuilder content = new StringBuilder(pre_tab + "while " + value_stat_result + " do\n");
        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    private String GenerateDoWhileExpr(ALittleDoWhileExpr root_expr, String pre_tab) {
        ALittleValueStat value_stat = root_expr.getValueStat();
        if (value_stat == null) {
            m_error = "do { ... } while(?) while中没有条件值";
            return null;
        }
        String value_stat_result = GenerateValueStat(value_stat);
        if (value_stat_result == null) return null;

        StringBuilder content = new StringBuilder(pre_tab + "repeat\n");
        List<ALittleAllExpr> all_expr_list = root_expr.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab)
                .append("until not(")
                .append(value_stat_result)
                .append(")\n");

        return content.toString();
    }

    private String GenerateWrapExpr(ALittleWrapExpr root_expr, String pre_tab) {
        StringBuilder content = new StringBuilder(pre_tab + "do\n");
        List<ALittleAllExpr> all_expr_list = root_expr.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab + "end\n");

        return content.toString();
    }

    private String GenerateReturnExpr(ALittleReturnExpr root_expr, String pre_tab) {
        List<ALittleValueStat> value_stat_list = root_expr.getValueStatList();
        List<String> content_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            String value_stat_result = GenerateValueStat(value_stat);
            if (value_stat_result == null) return null;
            content_list.add(value_stat_result);
        }
        String value_stat_result = "";
        if (!content_list.isEmpty())
            value_stat_result = " " + String.join(", ", content_list);

        return pre_tab + "return" + value_stat_result + "\n";
    }

    private String GenerateFlowExpr(ALittleFlowExpr root, String pre_tab) {
        String content = root.getText();
        if (content.startsWith("break"))
            return pre_tab + "break\n";

        m_error = "未知的操作语句:" + content;
        return null;
    }

    private String GenerateAllExpr(ALittleAllExpr root, String pre_tab) {
        PsiElement[] child_list = root.getChildren();

        List<String> expr_list = new ArrayList<>();
        for (PsiElement child : child_list) {
            if (child instanceof ALittleFlowExpr) {
                String result = GenerateFlowExpr((ALittleFlowExpr)child, pre_tab);
                expr_list.add(result);
            } else if (child instanceof ALittleReturnExpr) {
                String result = GenerateReturnExpr((ALittleReturnExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleDoWhileExpr) {
                String result = GenerateDoWhileExpr((ALittleDoWhileExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleWhileExpr) {
                String result = GenerateWhileExpr((ALittleWhileExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleForExpr) {
                String result = GenerateForExpr((ALittleForExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleIfExpr) {
                String result = GenerateIfExpr((ALittleIfExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleOpAssignExpr) {
                String result = GenerateOpAssignExpr((ALittleOpAssignExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleVarAssignExpr) {
                String result = GenerateVarAssignExpr((ALittleVarAssignExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleOp1Expr) {
                String result = GenerateOp1Expr((ALittleOp1Expr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittleWrapExpr) {
                String result = GenerateWrapExpr((ALittleWrapExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            } else if (child instanceof ALittlePropertyValueExpr) {
                String result = GeneratePropertyValueExpr((ALittlePropertyValueExpr)child, pre_tab);
                if (result == null) return null;
                expr_list.add(result);
            }
        }

        return String.join("\n", expr_list);
    }

    private String GenerateEnum(ALittleEnumDec root, String pre_tab) {
        // 如果带有协议标志的，那么就不生成
        if (root.getEnumProtocolDec() != null) return "";

        ALittleEnumNameDec name_dec = root.getEnumNameDec();
        if (name_dec == null)
        {
            m_error = root.getText() + "没有定义枚举名";
            return null;
        }

        StringBuilder content = new StringBuilder();
        content.append(pre_tab)
                .append(name_dec.getIdContent().getText())
                .append(" = {\n");

        List<ALittleEnumVarDec> var_dec_list = root.getEnumVarDecList();
        int enum_value = -1;
        String enum_string = "-1";
        for (ALittleEnumVarDec var_dec : var_dec_list) {
            ALittleEnumVarNameDec var_name_dec = var_dec.getEnumVarNameDec();
            ALittleEnumVarValueDec var_value_dec = var_dec.getEnumVarValueDec();
            if (var_value_dec == null) {
                ++ enum_value;
                enum_string = "" + enum_value;
            } else {
                if (var_value_dec.getDigitContent() != null) {
                    String value = var_value_dec.getDigitContent().getText();
                    if (!ALittleUtil.isInt(value)) {
                        m_error = var_name_dec.getIdContent().getText() + "对应的枚举值必须是整数";
                        return null;
                    }
                    String number_content = var_value_dec.getText();
                    if (number_content.startsWith("0x"))
                        enum_value = Integer.parseInt(number_content.substring(2), 16);
                    else
                        enum_value = Integer.parseInt(number_content);
                    enum_string = number_content;
                } else if (var_value_dec.getStringContent() != null) {
                    enum_string = var_value_dec.getStringContent().getText();
                }
            }
            content.append(pre_tab)
                    .append("\t")
                    .append(var_name_dec.getIdContent().getText())
                    .append(" = ")
                    .append(enum_string)
                    .append(",\n");
        }

        content.append(pre_tab).append("}\n\n");

        return content.toString();
    }

    private String GenerateClass(ALittleClassDec root, String pre_tab) {
        ALittleClassNameDec name_dec = root.getClassNameDec();
        if (name_dec == null) {
            m_error = "类没有定义类名";
            return null;
        }

        //类声明//////////////////////////////////////////////////////////////////////////////////////////
        String class_name = name_dec.getIdContent().getText();
        StringBuilder content = new StringBuilder(pre_tab + class_name);

        String namespace_pre = "ALittle.";
        if (m_namespace_name.equals("ALittle")) namespace_pre = "";

        ALittleClassExtendsNameDec extends_name_dec = root.getClassExtendsNameDec();
        ALittleClassExtendsNamespaceNameDec extends_namespace_name_dec = root.getClassExtendsNamespaceNameDec();
        String extends_name = "";
        if (extends_namespace_name_dec != null) {
            extends_name += extends_namespace_name_dec.getText() + ".";
        }
        if (extends_name_dec != null) {
            extends_name += extends_name_dec.getText();
        }
        if (extends_name.equals("")) {
            extends_name = "nil";
        }

        content.append(" = ").append(namespace_pre).append("Class(").append(extends_name).append(", \"").append(class_name).append("\")\n\n");

        //构建构造函数//////////////////////////////////////////////////////////////////////////////////////////
        String ctor_param_list = "";
        List<ALittleClassCtorDec> ctor_dec_list = root.getClassCtorDecList();
        if (ctor_dec_list.size() > 1) {
            m_error = "class " + class_name + " 最多只能有一个构造函数";
            return null;
        }
        if (ctor_dec_list.size() > 0) {
            ALittleClassCtorDec ctor_dec = ctor_dec_list.get(0);
            List<String> param_name_list = new ArrayList<>();

            ALittleMethodParamDec param_dec = ctor_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                    ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                    param_name_list.add(param_name_dec.getIdContent().getText());
                }
            }
            ctor_param_list = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(":Ctor(").append(ctor_param_list).append(")\n");

            ALittleMethodBodyDec body_dec = ctor_dec.getMethodBodyDec();
            if (body_dec != null) {
                List<ALittleAllExpr> all_expr_list = body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    String result = GenerateAllExpr(all_expr, pre_tab + "\t");
                    if (result == null) return null;
                    content.append(result);
                }
            }
            content.append(pre_tab).append("end\n\n");
        }
        //构建getter函数///////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassGetterDec> class_getter_dec_list = root.getClassGetterDecList();
        for (ALittleClassGetterDec class_getter_dec : class_getter_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_getter_dec.getMethodNameDec();
            if (class_method_name_dec == null) {
                m_error = "class " + class_name + " getter函数没有函数名";
                return null;
            }
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".__getter:")
                    .append(class_method_name_dec.getIdContent().getText())
                    .append("()\n");

            ALittleMethodBodyDec class_method_body_dec = class_getter_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                m_error = "class " + class_name + " getter函数没有函数体";
                return null;
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                String result = GenerateAllExpr(all_expr, pre_tab + "\t");
                if (result == null) return null;
                content.append(result);
            }
            content.append(pre_tab).append("end\n\n");
        }
        //构建setter函数///////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassSetterDec> class_setter_dec_list = root.getClassSetterDecList();
        for (ALittleClassSetterDec class_setter_dec : class_setter_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_setter_dec.getMethodNameDec();
            if (class_method_name_dec == null) {
                m_error = "class " + class_name + " setter函数没有函数名";
                return null;
            }
            ALittleMethodParamOneDec param_dec = class_setter_dec.getMethodParamOneDec();
            if (param_dec == null) {
                m_error = "class " + class_name + " setter函数必须要有一个参数";
                return null;
            }
            ALittleMethodParamNameDec param_name_dec = param_dec.getMethodParamNameDec();
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".__setter:")
                    .append(class_method_name_dec.getIdContent().getText())
                    .append("(")
                    .append(param_name_dec.getIdContent().getText())
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_setter_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                m_error = "class " + class_name + " setter函数没有函数体";
                return null;
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                String result = GenerateAllExpr(all_expr, pre_tab + "\t");
                if (result == null) return null;
                content.append(result);
            }
            content.append(pre_tab).append("end\n\n");
        }
        //构建成员函数//////////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassMethodDec> class_method_dec_list = root.getClassMethodDecList();
        for (ALittleClassMethodDec class_method_dec : class_method_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_method_dec.getMethodNameDec();
            if (class_method_name_dec == null) {
                m_error = "class " + class_name + " 成员函数没有函数名";
                return null;
            }

            List<String> param_name_list = new ArrayList<>();
            ALittleMethodParamDec param_dec = class_method_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                    ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                    param_name_list.add(param_name_dec.getIdContent().getText());
                }
            }
            String method_param_list = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(":")
                    .append(class_method_name_dec.getText())
                    .append("(")
                    .append(method_param_list)
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_method_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                m_error = "class " + class_name + " 成员函数没有函数体";
                return null;
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                String result = GenerateAllExpr(all_expr, pre_tab + "\t");
                if (result == null) return null;
                content.append(result);
            }
            content.append(pre_tab).append("end\n\n");
        }
        //构建静态函数//////////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassStaticDec> class_static_dec_list = root.getClassStaticDecList();
        for (ALittleClassStaticDec class_static_dec : class_static_dec_list) {
            ALittleMethodNameDec class_method_name_dec = class_static_dec.getMethodNameDec();
            if (class_method_name_dec == null) {
                m_error = "class " + class_name + " 静态函数没有函数名";
                return null;
            }
            List<String> param_name_list = new ArrayList<>();
            ALittleMethodParamDec param_dec = class_static_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                    ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                    param_name_list.add(param_name_dec.getIdContent().getText());
                }
            }

            String method_param_list = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".")
                    .append(class_method_name_dec.getText())
                    .append("(")
                    .append(method_param_list)
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_static_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                m_error = "class " + class_name + " 静态函数没有函数体";
                return null;
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                String result = GenerateAllExpr(all_expr, pre_tab + "\t");
                if (result == null) return null;
                content.append(result);
            }
            content.append(pre_tab).append("end\n\n");
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        return content.toString();
    }

    private String GenerateInstance(ALittleInstanceDec root, String pre_tab) {
        ALittleInstanceNameDec name_dec = root.getInstanceNameDec();
        if (name_dec == null) {
            m_error = "单例没有定义名称";
            return null;
        }

        StringBuilder content = new StringBuilder();

        ALittleInstanceClassNameDec class_name_dec = root.getInstanceClassNameDec();
        if (class_name_dec != null) {
            content.append(pre_tab);

            ALittleAccessModifier access_modifier_dec = root.getAccessModifier();
            if (access_modifier_dec != null && access_modifier_dec.getText().equals("public")) {
                content.append("_G.");
            }

            content.append(name_dec.getIdContent().getText()).append(" = ");
            content.append(class_name_dec.getIdContent().getText()).append("(");
            List<String> param_list = new ArrayList<>();
            List<ALittleValueStat> value_stat_list = root.getValueStatList();
            for (ALittleValueStat value_stat_dec : value_stat_list) {
                String result = GenerateValueStat(value_stat_dec);
                if (result == null) return null;
                param_list.add(result);
            }
            content.append(String.join(", ", param_list));
            content.append(")\n");
        }
        return content.toString();
    }

    private String GenerateGlobalMethod(ALittleGlobalMethodDec root, String pre_tab) {
        ALittleMethodNameDec global_method_name_dec = root.getMethodNameDec();
        if (global_method_name_dec == null) {
            m_error = "全局函数没有函数名";
            return null;
        }

        String method_name = global_method_name_dec.getIdContent().getText();

        List<String> param_name_list = new ArrayList<>();
        ALittleMethodParamDec param_dec = root.getMethodParamDec();
        if (param_dec != null) {
            List<ALittleMethodParamOneDec> param_one_dec_list = param_dec.getMethodParamOneDecList();
            for (ALittleMethodParamOneDec param_one_dec : param_one_dec_list) {
                ALittleMethodParamNameDec param_name_dec = param_one_dec.getMethodParamNameDec();
                param_name_list.add(param_name_dec.getIdContent().getText());
            }
        }

        StringBuilder content = new StringBuilder();
        String method_param_list = String.join(", ", param_name_list);
        content.append(pre_tab)
                .append("function ")
                .append(method_name)
                .append("(")
                .append(method_param_list)
                .append(")\n");

        ALittleMethodBodyDec class_method_body_dec = root.getMethodBodyDec();
        if (class_method_body_dec == null) {
            m_error = "全局函数 " + method_name + " 没有函数体";
            return null;
        }
        List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab).append("end\n\n");

        return content.toString();
    }

    private String GenerateNamespace(ALittleNamespaceDec root) {
        ALittleNamespaceNameDec name_dec = root.getNamespaceNameDec();
        if (name_dec == null) {
            m_error = "命名域没有定义名字";
            return null;
        }
        m_namespace_name = name_dec.getIdContent().getText();

        StringBuilder content = null;
        if (m_namespace_name.equals("lua"))
            content = new StringBuilder("\n");
        else
            content = new StringBuilder("\nmodule(\"" + m_namespace_name + "\", package.seeall)\n\n");

        PsiElement[] child_list = root.getChildren();
        for (PsiElement child : child_list) {
            // 处理结构体
            if (child instanceof ALittleStructDec) {
                List<String> error = new ArrayList<>();
                String result = ALittleUtil.GenerateStruct((ALittleStructDec) child, "", error);
                if (result == null)
                {
                    if (!error.isEmpty()) m_error = error.get(0);
                    return null;
                }
                if (!result.isEmpty())
                    m_protocol_list.add(result);
            // 处理enum
            } else if (child instanceof ALittleEnumDec) {
                String result = GenerateEnum((ALittleEnumDec) child, "");
                if (result == null) return null;
                content.append(result);
            // 处理class
            } else if (child instanceof ALittleClassDec) {
                String result = GenerateClass((ALittleClassDec) child, "");
                if (result == null) return null;
                content.append(result);
            // 处理instance
            } else if (child instanceof ALittleInstanceDec) {
                String result = GenerateInstance((ALittleInstanceDec)child, "");
                if (result == null) return null;
                content.append(result);
            // 处理全局函数
            } else if (child instanceof ALittleGlobalMethodDec) {
                String result = GenerateGlobalMethod((ALittleGlobalMethodDec)child, "");
                if (result == null) return null;
                content.append(result);
            }
        }

        return content.toString();
    }
}
