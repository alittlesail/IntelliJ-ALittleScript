package plugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.FileHelper;
import plugin.psi.*;
import plugin.reference.ALittleReference;
import plugin.reference.ALittleReferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ALittleGenerateLua {
    private String mNamespaceName = "";
    private List<String> mEnumList;
    private List<String> mClassList;

    private boolean mOpenRawSet = false;
    private int mRawsetUseCount = 0;

    private void copyStdLibrary(String module_base_path) throws Exception {
        File file = new File(module_base_path + "/std");
        if (file.exists()) return;
        if (!file.mkdirs())
            throw new Exception("文件夹创建失败:" + file.getPath());

        // 适配代码
        String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);
        VirtualFile dir;
        if (jarPath.endsWith(".jar"))
            dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "adapter/Lua"));
        else
            dir = VfsUtil.findFileByIoFile(new File(jarPath +"/adapter/Lua"), true);

        if (dir != null) {
            VirtualFile[] file_list = dir.getChildren();
            if (file_list != null) {
                for (VirtualFile virtualFile : file_list) {
                    FileOutputStream file_out = new FileOutputStream(new File(module_base_path + "/std/" + virtualFile.getName()));
                    file_out.write(virtualFile.contentsToByteArray());
                    file_out.close();
                }
            }
        }
    }

    private void checkErrorElement(PsiElement element, boolean full_check) throws ALittleReferenceUtil.ALittleReferenceException {
        for(PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof PsiErrorElement) {
                throw new ALittleReferenceUtil.ALittleReferenceException(child, ((PsiErrorElement) child).getErrorDescription());
            }

            if (full_check) {
                // 检查错误，给元素上色
                PsiReference ref = element.getReference();
                if (ref instanceof ALittleReference) {
                    ((ALittleReference) ref).checkError();
                }
            }

            checkErrorElement(child, full_check);
        }
    }

    public void GenerateLua(ALittleFile alittleFile, boolean full_check) throws Exception {
        // 获取语法错误
        try {
            checkErrorElement(alittleFile, full_check);
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new Exception(e.getElement().getContainingFile().getName() + "有语法错误:" + e.getError());
        }

        mEnumList = new ArrayList<>();
        mClassList = new ArrayList<>();

        ALittleNamespaceDec namespaceDec = ALittleUtil.getNamespaceDec(alittleFile);
        if (namespaceDec == null) throw new Exception("没有定义命名域 namespace");

        // 如果命名域有register标记，那么就不需要生成
        if (namespaceDec.getRegisterModifier() != null) {
            return;
        }

        // 生成代码
        String content = GenerateNamespace(namespaceDec);

        // 保存到文件
        FileIndexFacade facade = FileIndexFacade.getInstance(alittleFile.getProject());
        Module module = facade.getModuleForFile(alittleFile.getVirtualFile());
        if (module == null) {
            return;
        }

        String alittleRelPath = FileHelper.calcALittleRelPath(module, alittleFile.getVirtualFile());
        FileHelper.writeFile(FileHelper.calcScriptPath(module) + alittleRelPath + "lua", content);

        // 复制标准库
        copyStdLibrary(FileHelper.calcScriptPath(module));
    }

    @NotNull
    private String GenerateBindStat(ALittleBindStat bind_stat) throws Exception {
        List<ALittleValueStat> value_stat_list = bind_stat.getValueStatList();

        String content = "ALittle.Bind(";
        if (ALittleUtil.getNamespaceName((ALittleFile)bind_stat.getContainingFile()).equals("ALittle"))
            content = "Bind(";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            param_list.add(GenerateValueStat(value_stat));
        }
        content += String.join(", ", param_list);
        content += ")";
        return content;
    }

    @NotNull
    private String GenerateOpNewListStat(ALittleOpNewListStat op_new_list) throws Exception {
        List<ALittleValueStat> value_stat_list = op_new_list.getValueStatList();

        String content = "{";
        List<String> param_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            param_list.add(GenerateValueStat(value_stat));
        }
        content += String.join(", ", param_list);
        content += "}";
        return content;
    }

    @NotNull
    private String GenerateOpNewStat(ALittleOpNewStat op_new_stat) throws Exception {
        // 如果是通用类型
        ALittleGenericType genericType = op_new_stat.getGenericType();
        if (genericType != null) {
            // 如果是Map，那么直接返回{}
            ALittleGenericMapType map_type = genericType.getGenericMapType();
            if (map_type != null) return "{}";

            // 如果是List，那么直接返回{}
            ALittleGenericListType list_type = genericType.getGenericListType();
            if (list_type != null) return "{}";

            ALittleGenericFunctorType functor_type = genericType.getGenericFunctorType();
            if (functor_type != null) {
                throw new Exception("Functor不能使用new来创建");
            }
        }

        // 自定义类型
        ALittleCustomType custom_type = op_new_stat.getCustomType();
        if (custom_type != null) {
            ALittleReferenceUtil.GuessTypeInfo guessType = custom_type.guessType();
            // 如果是结构体名，那么就当表来处理
            if (guessType.type == ALittleReferenceUtil.GuessType.GT_STRUCT) {
                return "{}";
            // 如果是类名
            } else if (guessType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                // 如果是类名
                String content = "";
                ALittleNamespaceNameDec namespace_nameDec = custom_type.getNamespaceNameDec();
                if (namespace_nameDec != null)
                    content = namespace_nameDec.getIdContent().getText() + ".";
                content += custom_type.getIdContent().getText() + "(";

                List<String> param_list = new ArrayList<>();
                List<ALittleValueStat> value_stat_list = op_new_stat.getValueStatList();
                for (ALittleValueStat value_stat : value_stat_list) {
                    param_list.add(GenerateValueStat(value_stat));
                }
                content += String.join(", ", param_list);

                content += ")";
                return content;
            }
        }

        throw new Exception("new 未知类型");
    }

    @NotNull
    private String GenerateOp8Suffix(ALittleOp8Suffix suffix) throws Exception {
        String op_string = suffix.getOp8().getText();
        if (op_string.equals("||")) {
            op_string = "or";
        }

        String value_factor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_factor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEe> suffix_ee_list = suffix.getOp8SuffixEeList();
        for (ALittleOp8SuffixEe suffix_ee : suffix_ee_list) {
            String suffix_ee_result = GenerateOp8SuffixEe(suffix_ee);
            suffix_content_list.add(suffix_ee_result);
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp8SuffixEe(ALittleOp8SuffixEe suffix) throws Exception {
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
            throw new Exception("GenerateOp8SuffixEe出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp8SuffixEx(ALittleOp8SuffixEx suffix) throws Exception {
        if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            throw new Exception("GenerateOp8SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp8Stat(ALittleOp8Stat op_8_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_8_stat.getValueFactorStat());

        ALittleOp8Suffix suffix = op_8_stat.getOp8Suffix();
        String suffix_result = GenerateOp8Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp8SuffixEx> suffix_ex_list = op_8_stat.getOp8SuffixExList();
        for (ALittleOp8SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp8SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp7Suffix(ALittleOp7Suffix suffix) throws Exception {
        String op_string = suffix.getOp7().getText();
        if (op_string.equals("&&")) {
            op_string = "and";
        }

        String value_factor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_factor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEe> suffix_ee_list = suffix.getOp7SuffixEeList();
        for (ALittleOp7SuffixEe suffix_ee : suffix_ee_list) {
            suffix_content_list.add(GenerateOp7SuffixEe(suffix_ee));
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp7SuffixEe(ALittleOp7SuffixEe suffix) throws Exception {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else {
            throw new Exception("GenerateOp7SuffixEe出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp7SuffixEx(ALittleOp7SuffixEx suffix) throws Exception {
        if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            throw new Exception("GenerateOp7SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp7Stat(ALittleOp7Stat op_7_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_7_stat.getValueFactorStat());

        ALittleOp7Suffix suffix = op_7_stat.getOp7Suffix();
        String suffix_result = GenerateOp7Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp7SuffixEx> suffix_ex_list = op_7_stat.getOp7SuffixExList();
        for (ALittleOp7SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp7SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp6Suffix(ALittleOp6Suffix suffix) throws Exception {
        String op_string = suffix.getOp6().getText();
        if (op_string.equals("!=")) {
            op_string = "~=";
        }

        String value_factor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_factor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEe> suffix_ee_list = suffix.getOp6SuffixEeList();
        for (ALittleOp6SuffixEe suffix_ee : suffix_ee_list) {
            suffix_content_list.add(GenerateOp6SuffixEe(suffix_ee));
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp6SuffixEe(ALittleOp6SuffixEe suffix) throws Exception {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else {
            throw new Exception("GenerateOp6SuffixEe出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp6SuffixEx(ALittleOp6SuffixEx suffix) throws Exception {
        if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            throw new Exception("GenerateOp6SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp6Stat(ALittleOp6Stat op_6_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_6_stat.getValueFactorStat());

        ALittleOp6Suffix suffix = op_6_stat.getOp6Suffix();
        String suffix_result = GenerateOp6Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp6SuffixEx> suffix_ex_list = op_6_stat.getOp6SuffixExList();
        for (ALittleOp6SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp6SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp5Suffix(ALittleOp5Suffix suffix) throws Exception {
        String op_string = suffix.getOp5().getText();

        String value_factor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_factor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEe> suffix_ee_list = suffix.getOp5SuffixEeList();
        for (ALittleOp5SuffixEe suffix_ee : suffix_ee_list) {
            suffix_content_list.add(GenerateOp5SuffixEe(suffix_ee));
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp5SuffixEe(ALittleOp5SuffixEe suffix) throws Exception {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else if (suffix.getOp4Suffix() != null) {
            return GenerateOp4Suffix(suffix.getOp4Suffix());
        } else {
            throw new Exception("GenerateOp5SuffixEe出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp5SuffixEx(ALittleOp5SuffixEx suffix) throws Exception {
        if (suffix.getOp5Suffix() != null) {
            return GenerateOp5Suffix(suffix.getOp5Suffix());
        } else if (suffix.getOp6Suffix() != null) {
            return GenerateOp6Suffix(suffix.getOp6Suffix());
        } else if (suffix.getOp7Suffix() != null) {
            return GenerateOp7Suffix(suffix.getOp7Suffix());
        } else if (suffix.getOp8Suffix() != null) {
            return GenerateOp8Suffix(suffix.getOp8Suffix());
        } else {
            throw new Exception("GenerateOp5SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp5Stat(ALittleOp5Stat op_5_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_5_stat.getValueFactorStat());

        ALittleOp5Suffix suffix = op_5_stat.getOp5Suffix();
        String suffix_result = GenerateOp5Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp5SuffixEx> suffix_ex_list = op_5_stat.getOp5SuffixExList();
        for (ALittleOp5SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp5SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp4Suffix(ALittleOp4Suffix suffix) throws Exception {
        String op_string = suffix.getOp4().getText();

        String value_factor_result = null;
        if (suffix.getValueFactorStat() != null) {
            value_factor_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_factor_result = GenerateOp2Value(suffix.getOp2Value());
        }

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEe> suffix_ee_list = suffix.getOp4SuffixEeList();
        for (ALittleOp4SuffixEe suffix_ee : suffix_ee_list) {
            suffix_content_list.add(GenerateOp4SuffixEe(suffix_ee));
        }
        String content = op_string + " " + value_factor_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp4SuffixEe(ALittleOp4SuffixEe suffix) throws Exception {
        if (suffix.getOp3Suffix() != null) {
            return GenerateOp3Suffix(suffix.getOp3Suffix());
        } else {
            throw new Exception("GenerateOp4SuffixEe出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp4SuffixEx(ALittleOp4SuffixEx suffix) throws Exception {
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
            throw new Exception("GenerateOp4SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp4Stat(ALittleOp4Stat op_4_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_4_stat.getValueFactorStat());

        ALittleOp4Suffix suffix = op_4_stat.getOp4Suffix();
        String suffix_result = GenerateOp4Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp4SuffixEx> suffix_ex_list = op_4_stat.getOp4SuffixExList();
        for (ALittleOp4SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp4SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp3Suffix(ALittleOp3Suffix suffix) throws Exception {
        String op_string = suffix.getOp3().getText();

        String value_result;
        if (suffix.getValueFactorStat() != null) {
            value_result = GenerateValueFactorStat(suffix.getValueFactorStat());
        } else if (suffix.getOp2Value() != null) {
            value_result = GenerateOp2Value(suffix.getOp2Value());
        } else {
            throw new Exception("GenerateOp3Suffix出现未知的表达式");
        }

        return op_string + " " + value_result;
    }

    @NotNull
    private String GenerateOp3SuffixEx(ALittleOp3SuffixEx suffix) throws Exception {
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
            throw new Exception("GenerateOp3SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp3Stat(ALittleOp3Stat op_3_stat) throws Exception {
        String value_factor_result = GenerateValueFactorStat(op_3_stat.getValueFactorStat());

        ALittleOp3Suffix suffix = op_3_stat.getOp3Suffix();
        String suffix_result = GenerateOp3Suffix(suffix);

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp3SuffixEx> suffix_ex_list = op_3_stat.getOp3SuffixExList();
        for (ALittleOp3SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp3SuffixEx(suffix_ex));
        }
        String content = value_factor_result + " " + suffix_result;
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateOp2SuffixEx(ALittleOp2SuffixEx suffix) throws Exception {
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
            throw new Exception("GenerateOp2SuffixEx出现未知的表达式");
        }
    }

    @NotNull
    private String GenerateOp2Value(ALittleOp2Value op_2_value) throws Exception {
        String content = "";

        ALittleValueFactorStat value_factor = op_2_value.getValueFactorStat();
        if (value_factor == null) {
            throw new Exception("GenerateOp2Stat单目运算没有操作对象");
        }

        String valueStatResult = GenerateValueFactorStat(value_factor);
        String op_string = op_2_value.getOp2().getText();
        if (op_string.equals("!")) {
            content += "not " + valueStatResult;
        } else if (op_string.equals("-")) {
            content += "-" + valueStatResult;
        } else {
            throw new Exception("GenerateOp2Stat出现未知类型");
        }

        return content;
    }

    @NotNull
    private String GenerateOp2Stat(ALittleOp2Stat op_2_stat) throws Exception {
        String content = GenerateOp2Value(op_2_stat.getOp2Value());

        List<String> suffix_content_list = new ArrayList<>();
        List<ALittleOp2SuffixEx> suffix_ex_list = op_2_stat.getOp2SuffixExList();
        for (ALittleOp2SuffixEx suffix_ex : suffix_ex_list) {
            suffix_content_list.add(GenerateOp2SuffixEx(suffix_ex));
        }
        if (!suffix_content_list.isEmpty()) content += " " + String.join(" ", suffix_content_list);
        return content;
    }

    @NotNull
    private String GenerateValueStat(ALittleValueStat root_stat) throws Exception {
        ALittleValueFactorStat value_factor = root_stat.getValueFactorStat();
        if (value_factor != null) return GenerateValueFactorStat(value_factor);

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

        ALittleOpNewListStat op_new_listStat = root_stat.getOpNewListStat();
        if (op_new_listStat != null) return GenerateOpNewListStat(op_new_listStat);

        ALittleBindStat bind_stat = root_stat.getBindStat();
        if (bind_stat != null) return GenerateBindStat(bind_stat);

        return "";
    }

    @NotNull
    private String GenerateValueFactorStat(ALittleValueFactorStat value_factor) throws Exception {
        ALittleConstValue const_value = value_factor.getConstValue();
        if (const_value != null) {
            return GenerateConstValue(const_value);
        }

        ALittleReflectValue reflect_value = value_factor.getReflectValue();
        if (reflect_value != null) {
            return GenerateReflectValue(reflect_value);
        }

        ALittlePropertyValue prop_value = value_factor.getPropertyValue();
        if (prop_value != null) {
            return GeneratePropertyValue(prop_value);
        }

        ALittleWrapValueStat value_stat_paren = value_factor.getWrapValueStat();
        if (value_stat_paren != null) {
            String result = GenerateValueStat(value_stat_paren.getValueStat());
            return "(" + result + ")";
        }

        throw new Exception("GenerateValueFactor出现未知类型");
    }

    @NotNull
    private String GenerateConstValue(ALittleConstValue const_value) throws Exception {
        String content = "";
        String const_value_string = const_value.getText();
        if (const_value_string.equals("null"))
            content += "nil";
        else
            content += const_value_string;
        return content;
    }

    @NotNull
    static public String GenerateReflectValue(ALittleReflectValue reflect_value) throws Exception {
        String content = "";
        ALittleCustomType custom_type = reflect_value.getCustomType();
        if (custom_type == null) return content;
        ALittleReferenceUtil.GuessTypeInfo guessType = custom_type.guessType();

        // 把获取到的对象转为Json

        return "\"" + content.replace("\"", "\\\"") + "\"";
    }

    @NotNull
    private String GeneratePropertyValue(ALittlePropertyValue prop_value) throws Exception {
        try {
            StringBuilder content = new StringBuilder();

            // 用来标记第一个变量是不是lua命名域
            boolean is_lua_namespace = false;

            // 通用类型的类型猜测
            ALittleReferenceUtil.GuessTypeInfo custom_guessType = null;

            // 获取开头的属性信息
            ALittlePropertyValueFirstType firstType = prop_value.getPropertyValueFirstType();
            ALittlePropertyValueCustomType custom_type = firstType.getPropertyValueCustomType();
            ALittlePropertyValueThisType this_type = firstType.getPropertyValueThisType();
            ALittlePropertyValueCastType cast_type = firstType.getPropertyValueCastType();
            if (custom_type != null) {
                String custom_type_content = custom_type.getText();
                custom_guessType = custom_type.guessType();
                if (custom_guessType.type == ALittleReferenceUtil.GuessType.GT_NAMESPACE_NAME && custom_type_content.equals("lua"))
                    is_lua_namespace = true;

                // 如果是lua命名域，那么就忽略
                if (!is_lua_namespace)
                    content.append(custom_type_content);
                // 如果是this，那么就变为self
            } else if (this_type != null) {
                content.append("self");
            } else if (cast_type != null) {
                ALittleValueFactorStat valueFactorStat = cast_type.getValueFactorStat();
                if (valueFactorStat == null) throw new Exception("cast没有填写转换对象");
                content.append(GenerateValueFactorStat(valueFactorStat));
            }

            // 后面跟着后缀属性
            List<ALittlePropertyValueSuffix> suffixList = prop_value.getPropertyValueSuffixList();
            for (int index = 0; index < suffixList.size(); ++index) {
                // 获取当前后缀
                ALittlePropertyValueSuffix suffix = suffixList.get(index);
                // 获取上一个后缀
                ALittlePropertyValueSuffix pre_suffix = null;
                if (index - 1 >= 0) pre_suffix = suffixList.get(index - 1);
                // 获取下一个后缀
                ALittlePropertyValueSuffix next_suffix = null;
                if (index + 1 < suffixList.size()) next_suffix = suffixList.get(index + 1);

                // 如果当前是
                ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
                if (dotId != null) {
                    // 获取类型
                    ALittleReferenceUtil.GuessTypeInfo guess = dotId.guessType();

                    // 如果是lua命名域下的，判断当前后缀的类型。决定使用.还是:
                    if (!is_lua_namespace) {
                        String split = ".";

                        // 如果是函数名
                        if (guess.element instanceof ALittleMethodNameDec) {
                            ALittleMethodNameDec method_nameDec = (ALittleMethodNameDec) guess;
                            // 1. 是成员函数
                            // 2. 使用的是调用
                            // 3. 前一个后缀是类实例对象
                            // 那么就要改成使用语法糖
                            if (method_nameDec.getParent() instanceof ALittleClassMethodDec) {
                                if (next_suffix != null && next_suffix.getPropertyValueMethodCall() != null) {
                                    // 获取前一个后缀的类型
                                    ALittleReferenceUtil.GuessTypeInfo pre_guess = null;
                                    // pre_suffix为空，说明前面是（CustomType或者ThisType）
                                    // 如果是ThisType说明一定是语法糖
                                    // 如果是CustomType那么就判断下类型
                                    if (pre_suffix == null) {
                                        if (custom_type != null) {
                                            pre_guess = custom_guessType;
                                        }
                                    } else if (pre_suffix.getPropertyValueDotId() != null)
                                        pre_guess = pre_suffix.getPropertyValueDotId().guessType();
                                    else if (pre_suffix.getPropertyValueMethodCall() != null)
                                        pre_guess = pre_suffix.getPropertyValueMethodCall().guessType();
                                    else if (pre_suffix.getPropertyValueBracketValue() != null)
                                        pre_guess = pre_suffix.getPropertyValueBracketValue().guessType();

                                    // 只要不是类名，那么肯定就是类实例对象，就是用语法糖
                                    if (pre_guess.type != ALittleReferenceUtil.GuessType.GT_CLASS_NAME)
                                        split = ":";
                                }
                                // setter和getter需要特殊处理
                            } else if (method_nameDec.getParent() instanceof ALittleClassSetterDec
                                    || method_nameDec.getParent() instanceof ALittleClassGetterDec) {
                                if (next_suffix != null && next_suffix.getPropertyValueMethodCall() != null) {
                                    ALittleReferenceUtil.GuessTypeInfo pre_guess = null;
                                    // pre_suffix为空，说明前面是（CustomType或者ThisType）
                                    // 如果是ThisType说明一定是语法糖
                                    // 如果是CustomType那么就判断下类型
                                    if (pre_suffix == null) {
                                        if (custom_type != null) {
                                            pre_guess = custom_guessType;
                                        }
                                    } else if (pre_suffix.getPropertyValueDotId() != null)
                                        pre_guess = pre_suffix.getPropertyValueDotId().guessType();
                                    else if (pre_suffix.getPropertyValueMethodCall() != null)
                                        pre_guess = pre_suffix.getPropertyValueMethodCall().guessType();
                                    else if (pre_suffix.getPropertyValueBracketValue() != null)
                                        pre_guess = pre_suffix.getPropertyValueBracketValue().guessType();

                                    // 如果前一个后缀是类名，那么那么就需要获取setter或者getter来获取
                                    if (pre_guess.type == ALittleReferenceUtil.GuessType.GT_CLASS_NAME) {
                                        // 如果是getter，那么一定是一个参数，比如ClassName.disabled(self)
                                        // 如果是setter，那么一定是两个参数，比如ClassName.width(self, 100)
                                        if (next_suffix.getPropertyValueMethodCall().getValueStatList().size() == 1)
                                            split = ".__getter.";
                                        else
                                            split = ".__setter.";
                                    }
                                }
                            }
                        }

                        content.append(split);
                    }

                    if (dotId.getIdContent() == null) {
                        throw new Exception("点后面没有内容");
                    }

                    String name_content = dotId.getIdContent().getText();
                    // 因为lua中自带的string模块名和关键字string一样，所以把lua自动的改成String（大些开头）
                    // 然后再翻译的时候，把String改成string
                    if (is_lua_namespace && name_content.equals("String"))
                        name_content = "string";
                    content.append(name_content);

                    // 置为false，表示不是命名域
                    is_lua_namespace = false;
                    continue;
                }

                ALittlePropertyValueBracketValue bracketValue = suffix.getPropertyValueBracketValue();
                if (bracketValue != null) {
                    ALittleValueStat value_stat = bracketValue.getValueStat();
                    if (value_stat != null) {
                        content.append("[").append(GenerateValueStat(value_stat)).append("]");
                    }
                    continue;
                }

                ALittlePropertyValueMethodCall methodCall = suffix.getPropertyValueMethodCall();
                if (methodCall != null) {
                    List<ALittleValueStat> value_stat_list = methodCall.getValueStatList();
                    List<String> param_list = new ArrayList<>();
                    for (ALittleValueStat value_stat : value_stat_list) {
                        param_list.add(GenerateValueStat(value_stat));
                    }
                    content.append("(").append(String.join(", ", param_list)).append(")");
                    continue;
                }

                throw new Exception("GeneratePropertyValue出现未知类型");
            }

            return content.toString();
        } catch (ALittleReferenceUtil.ALittleReferenceException e) {
            throw new Exception(e.getError());
        }
    }

    @NotNull
    private String GeneratePropertyValueExpr(ALittlePropertyValueExpr root, String pre_tab) throws Exception {
        return pre_tab + GeneratePropertyValue(root.getPropertyValue()) + "\n";
    }

    @NotNull
    private String GenerateOp1Expr(ALittleOp1Expr root, String pre_tab) throws Exception {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            throw new Exception("GenerateOp1Expr 没有操作值:" + root.getText());
        }
        ALittleOp1 op_1 = root.getOp1();

        String valueStatResult = GenerateValueStat(value_stat);

        String op_1_string = op_1.getText();
        if (op_1_string.equals("++"))
            return pre_tab + valueStatResult + " = " + valueStatResult + " + 1\n";

        if (op_1_string.equals("--"))
            return pre_tab + valueStatResult + " = " + valueStatResult + " - 1\n";

        throw new Exception("GenerateOp1Expr未知类型:" + op_1_string);
    }

    @NotNull
    private String GenerateVarAssignExpr(ALittleVarAssignExpr root, String pre_tab, String preString) throws Exception {
        List<ALittleVarAssignDec> pair_decList = root.getVarAssignDecList();
        if (pair_decList.isEmpty()) {
            throw new Exception("局部变量没有变量名:" + root.getText());
        }

        String content = pre_tab + preString;

        List<String> name_list = new ArrayList<>();
        for (ALittleVarAssignDec pair_dec : pair_decList) {
            name_list.add(pair_dec.getVarAssignNameDec().getIdContent().getText());
        }
        content += String.join(", ", name_list);

        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null)
            return content + "\n";

        return content + " = " + GenerateValueStat(value_stat) + "\n";
    }

    @NotNull
    private String GenerateOpAssignExpr(ALittleOpAssignExpr root, String pre_tab) throws Exception {
        List<ALittlePropertyValue> propValueList = root.getPropertyValueList();
        List<String> content_list = new ArrayList<>();
        for (ALittlePropertyValue prop_value : propValueList) {
            content_list.add(GeneratePropertyValue(prop_value));
        }

        String prop_value_result = String.join(", ", content_list);

        ALittleOpAssign op_assign = root.getOpAssign();
        ALittleValueStat value_stat = root.getValueStat();
        if (op_assign == null || value_stat == null)
            return pre_tab + prop_value_result + "\n";

        String valueStatResult = GenerateValueStat(value_stat);

        if (op_assign.getText().equals("=")) {
            // 这里做优化
            // 把 self._attr = value 优化为  rawset(self, "_attr", value)
            if (mOpenRawSet && propValueList.size() == 1) {
                ALittlePropertyValue prop_value = propValueList.get(0);
                ALittlePropertyValueThisType this_type = prop_value.getPropertyValueFirstType().getPropertyValueThisType();
                if (this_type != null && prop_value.getPropertyValueSuffixList().size() == 1) {
                    ALittlePropertyValueSuffix suffix = prop_value.getPropertyValueSuffixList().get(0);
                    if (suffix.getPropertyValueDotId() != null) {
                        ALittlePropertyValueDotId dotId = suffix.getPropertyValueDotId();
                        if (dotId != null && dotId.getIdContent() != null) {
                            String attrName = dotId.getIdContent().getText();
                            ALittleReferenceUtil.GuessTypeInfo thisGuessType = this_type.guessType();
                            if (thisGuessType.type == ALittleReferenceUtil.GuessType.GT_CLASS) {
                                List<ALittleClassVarDec> varNameList = new ArrayList<>();
                                ALittleUtil.findClassVarNameDecList(thisGuessType.element.getProject()
                                        , ALittleUtil.getNamespaceName((ALittleFile) thisGuessType.element.getContainingFile())
                                        , (ALittleClassDec) thisGuessType.element
                                        , attrName
                                        , varNameList, 100);
                                if (!varNameList.isEmpty()) {
                                    ++mRawsetUseCount;
                                    return pre_tab + "___rawset(self, \"" + attrName + "\", " + valueStatResult + ")\n";
                                }
                            }
                        }
                    }
                }
            }

            return pre_tab + prop_value_result + " = " + valueStatResult + "\n";
        }

        String op_assign_string = op_assign.getText();

        // 如果出现多个前缀赋值，那么只能是=号
        if (content_list.size() > 1)
            throw new Exception("等号左边出现多个值的时候，只能使用=赋值符号:" + root.getText());

        String content = "";
        switch (op_assign_string) {
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "%=":
                String op_string = op_assign_string.substring(0, 1);
                content = pre_tab + prop_value_result + " = " + prop_value_result + " " + op_string + " (" + valueStatResult + ")\n";
                break;
            default:
                throw new Exception("未知的赋值操作类型:" + op_assign_string);
        }
        return content;
    }

    @NotNull
    private String GenerateElseExpr(ALittleElseExpr root, String pre_tab) throws Exception {
        StringBuilder content = new StringBuilder(pre_tab);
        content.append("else\n");
        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
        }
        return content.toString();
    }

    @NotNull
    private String GenerateElseIfExpr(ALittleElseIfExpr root, String pre_tab) throws Exception {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            throw new Exception("elseif (?) elseif没有条件值:" + root.getText());
        }
        String valueStatResult = GenerateValueStat(value_stat);

        StringBuilder content = new StringBuilder(pre_tab);
        content.append("elseif")
                .append(" ")
                .append(valueStatResult)
                .append(" then\n");

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
        }
        return content.toString();
    }

    @NotNull
    private String GenerateIfExpr(ALittleIfExpr root, String pre_tab) throws Exception {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            throw new Exception("if (?) if没有条件值:" + root.getText());
        }
        String valueStatResult = GenerateValueStat(value_stat);

        StringBuilder content = new StringBuilder(pre_tab);
        content.append("if")
                .append(" ")
                .append(valueStatResult)
                .append(" then\n");

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
        }

        List<ALittleElseIfExpr> else_if_expr_list = root.getElseIfExprList();
        for (ALittleElseIfExpr else_if_expr : else_if_expr_list) {
            String result = GenerateElseIfExpr(else_if_expr, pre_tab);
            if (result == null) return null;
            content.append(result);
        }

        ALittleElseExpr else_expr = root.getElseExpr();
        if (else_expr != null) {
            content.append(GenerateElseExpr(else_expr, pre_tab));
        }
        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    @NotNull
    private String GenerateForExpr(ALittleForExpr root, String pre_tab) throws Exception {
        ALittleForStepCondition for_step_condition = root.getForStepCondition();
        ALittleForInCondition for_in_condition = root.getForInCondition();

        StringBuilder content = new StringBuilder(pre_tab);
        if (for_step_condition != null) {
            ALittleForStartStat for_start_stat = for_step_condition.getForStartStat();

            ALittleValueStat start_value_stat = for_start_stat.getValueStat();
            if (start_value_stat == null) {
                throw new Exception("for 没有初始表达式:" + root.getText());
            }
            String start_valueStatResult = GenerateValueStat(start_value_stat);

            ALittleVarAssignNameDec nameDec = for_start_stat.getForPairDec().getVarAssignNameDec();
            if (nameDec == null) {
                throw new Exception("for 初始表达式没有变量名:" + root.getText());
            }
            String start_var_name = nameDec.getText();

            content.append("for ")
                    .append(start_var_name)
                    .append(" = ")
                    .append(start_valueStatResult)
                    .append(", ");

            ALittleForEndStat for_end_stat = for_step_condition.getForEndStat();

            ALittleValueStat end_value_stat = for_end_stat.getValueStat();
            content.append(GenerateValueStat(end_value_stat));

            ALittleForStepStat for_step_stat = for_step_condition.getForStepStat();
            ALittleValueStat step_value_stat = for_step_stat.getValueStat();
            content.append(", ").append(GenerateValueStat(step_value_stat));

            content.append(" do\n");
        } else if (for_in_condition != null) {
            ALittleValueStat value_stat = for_in_condition.getValueStat();
            if (value_stat == null) {
                throw new Exception("for in 没有遍历的对象:" + root.getText());
            }

            String valueStatResult = GenerateValueStat(value_stat);

            List<ALittleForPairDec> pair_list = for_in_condition.getForPairDecList();
            List<String> pair_string_list = new ArrayList<>();
            for (ALittleForPairDec pair : pair_list) {
                ALittleVarAssignNameDec nameDec = pair.getVarAssignNameDec();
                if (nameDec == null)
                    throw new Exception("for in 没有变量名");
                pair_string_list.add(nameDec.getText());
            }

            String pair_type = ALittleReferenceUtil.CalcPairsType(value_stat);
            if (pair_type == null) {
                throw new Exception("for in 的遍历对象表达式错误:" + root.getText());
            }

            // 如果foreach的参数数量不为2，那么就不用pair_type
            if (pair_type.isEmpty()) {
                content.append("for ")
                        .append(String.join(", ", pair_string_list))
                        .append(" in ")
                        .append(valueStatResult)
                        .append(" do\n");
            } else {
                content.append("for ")
                        .append(String.join(", ", pair_string_list))
                        .append(" in ")
                        .append(pair_type)
                        .append("(")
                        .append(valueStatResult)
                        .append(") do\n");
            }
        } else {
            throw new Exception("for(?) 无效的for语句:" + root.getText());
        }

        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
        }

        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    @NotNull
    private String GenerateWhileExpr(ALittleWhileExpr root, String pre_tab) throws Exception {
        ALittleValueStat value_stat = root.getValueStat();
        if (value_stat == null) {
            throw new Exception("while (?) { ... } while中没有条件值");
        }
        String valueStatResult = GenerateValueStat(value_stat);

        StringBuilder content = new StringBuilder(pre_tab + "while " + valueStatResult + " do\n");
        List<ALittleAllExpr> all_expr_list = root.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab).append("end\n");
        return content.toString();
    }

    @NotNull
    private String GenerateDoWhileExpr(ALittleDoWhileExpr root_expr, String pre_tab) throws Exception {
        ALittleValueStat value_stat = root_expr.getValueStat();
        if (value_stat == null) {
            throw new Exception("do { ... } while(?) while中没有条件值");
        }
        String valueStatResult = GenerateValueStat(value_stat);

        StringBuilder content = new StringBuilder(pre_tab + "repeat\n");
        List<ALittleAllExpr> all_expr_list = root_expr.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            String result = GenerateAllExpr(all_expr, pre_tab + "\t");
            if (result == null) return null;
            content.append(result);
        }
        content.append(pre_tab)
                .append("until not(")
                .append(valueStatResult)
                .append(")\n");

        return content.toString();
    }

    @NotNull
    private String GenerateWrapExpr(ALittleWrapExpr root_expr, String pre_tab) throws Exception {
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

    @NotNull
    private String GenerateReturnExpr(ALittleReturnExpr root_expr, String pre_tab) throws Exception {
        if (root_expr.getReturnYield() != null) {
            return pre_tab + "return ___coroutine.yield()\n";
        }

        List<ALittleValueStat> value_stat_list = root_expr.getValueStatList();
        List<String> content_list = new ArrayList<>();
        for (ALittleValueStat value_stat : value_stat_list) {
            content_list.add(GenerateValueStat(value_stat));
        }
        String valueStatResult = "";
        if (!content_list.isEmpty())
            valueStatResult = " " + String.join(", ", content_list);

        return pre_tab + "return" + valueStatResult + "\n";
    }

    @NotNull
    private String GenerateFlowExpr(ALittleFlowExpr root, String pre_tab) throws Exception {
        String content = root.getText();
        if (content.startsWith("break"))
            return pre_tab + "break\n";

        throw new Exception("未知的操作语句:" + content);
    }

    @NotNull
    private String GenerateAllExpr(ALittleAllExpr root, String pre_tab) throws Exception {
        PsiElement[] child_list = root.getChildren();

        List<String> expr_list = new ArrayList<>();
        for (PsiElement child : child_list) {
            if (child instanceof ALittleFlowExpr) {
                expr_list.add(GenerateFlowExpr((ALittleFlowExpr)child, pre_tab));
            } else if (child instanceof ALittleReturnExpr) {
                expr_list.add(GenerateReturnExpr((ALittleReturnExpr)child, pre_tab));
            } else if (child instanceof ALittleDoWhileExpr) {
                expr_list.add(GenerateDoWhileExpr((ALittleDoWhileExpr)child, pre_tab));
            } else if (child instanceof ALittleWhileExpr) {
                expr_list.add(GenerateWhileExpr((ALittleWhileExpr)child, pre_tab));
            } else if (child instanceof ALittleForExpr) {
                expr_list.add(GenerateForExpr((ALittleForExpr)child, pre_tab));
            } else if (child instanceof ALittleIfExpr) {
                expr_list.add(GenerateIfExpr((ALittleIfExpr)child, pre_tab));
            } else if (child instanceof ALittleOpAssignExpr) {
                expr_list.add(GenerateOpAssignExpr((ALittleOpAssignExpr)child, pre_tab));
            } else if (child instanceof ALittleVarAssignExpr) {
                expr_list.add(GenerateVarAssignExpr((ALittleVarAssignExpr)child, pre_tab, "local "));
            } else if (child instanceof ALittleOp1Expr) {
                expr_list.add(GenerateOp1Expr((ALittleOp1Expr)child, pre_tab));
            } else if (child instanceof ALittleWrapExpr) {
                expr_list.add(GenerateWrapExpr((ALittleWrapExpr)child, pre_tab));
            } else if (child instanceof ALittlePropertyValueExpr) {
                expr_list.add(GeneratePropertyValueExpr((ALittlePropertyValueExpr)child, pre_tab));
            }
        }

        return String.join("\n", expr_list);
    }

    @NotNull
    private String GenerateEnum(ALittleEnumDec root, String pre_tab) throws Exception {
        ALittleEnumNameDec nameDec = root.getEnumNameDec();
        if (nameDec == null) throw new Exception(root.getText() + "没有定义枚举名");

        StringBuilder content = new StringBuilder();
        content.append(pre_tab)
                .append(nameDec.getIdContent().getText())
                .append(" = {\n");

        int enumValue = -1;
        String enumString = "-1";

        List<ALittleEnumVarDec> varDecList = root.getEnumVarDecList();
        for (ALittleEnumVarDec varDec : varDecList) {
            if (varDec.getDigitContent() != null) {
                String value = varDec.getDigitContent().getText();
                if (!ALittleUtil.isInt(value)) {
                    throw new Exception(varDec.getIdContent().getText() + "对应的枚举值必须是整数");
                }
                String number_content = varDec.getText();
                if (number_content.startsWith("0x"))
                    enumValue = Integer.parseInt(number_content.substring(2), 16);
                else
                    enumValue = Integer.parseInt(number_content);
                enumString = number_content;
            } else if (varDec.getStringContent() != null) {
                enumString = varDec.getStringContent().getText();
            } else {
                ++ enumValue;
                enumString = "" + enumValue;
            }
            
            content.append(pre_tab)
                    .append("\t")
                    .append(varDec.getIdContent().getText())
                    .append(" = ")
                    .append(enumString)
                    .append(",\n");
        }

        content.append(pre_tab).append("}\n\n");

        return content.toString();
    }

    @NotNull
    private String GenerateClass(ALittleClassDec root, String pre_tab) throws Exception {
        ALittleClassNameDec nameDec = root.getClassNameDec();
        if (nameDec == null) {
            throw new Exception("类没有定义类名");
        }

        //类声明//////////////////////////////////////////////////////////////////////////////////////////
        String class_name = nameDec.getIdContent().getText();
        StringBuilder content = new StringBuilder(pre_tab + class_name);

        String namespace_pre = "ALittle.";
        if (mNamespaceName.equals("ALittle")) namespace_pre = "";

        ALittleClassExtendsDec extendsDec = root.getClassExtendsDec();
        String extends_name = "";
        if (extendsDec != null) {
            if (extendsDec.getNamespaceNameDec() != null) {
                extends_name += extendsDec.getNamespaceNameDec().getText() + ".";
            }
            if (extendsDec.getClassNameDec() != null) {
                extends_name += extendsDec.getClassNameDec().getText();
            }
        }
        if (extends_name.equals("")) {
            extends_name = "nil";
        }

        content.append(" = ").append(namespace_pre).append("Class(").append(extends_name).append(", \"").append(class_name).append("\")\n\n");

        //构建构造函数//////////////////////////////////////////////////////////////////////////////////////////
        String ctorParamList = "";
        List<ALittleClassCtorDec> ctorDecList = root.getClassCtorDecList();
        if (ctorDecList.size() > 1) {
            throw new Exception("class " + class_name + " 最多只能有一个构造函数");
        }
        if (ctorDecList.size() > 0) {
            ALittleClassCtorDec ctorDec = ctorDecList.get(0);
            List<String> param_name_list = new ArrayList<>();

            ALittleMethodParamDec param_dec = ctorDec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                    ALittleMethodParamNameDec param_nameDec = param_one_dec.getMethodParamNameDec();
                    if (param_nameDec == null) {
                        throw new Exception("class " + class_name + " 的构造函数没有参数名");
                    }
                    param_name_list.add(param_nameDec.getIdContent().getText());
                }
            }
            ctorParamList = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(":Ctor(").append(ctorParamList).append(")\n");

            mOpenRawSet = true;

            ALittleMethodBodyDec body_dec = ctorDec.getMethodBodyDec();
            StringBuilder all_expr_content = new StringBuilder();
            if (body_dec != null) {
                List<ALittleAllExpr> all_expr_list = body_dec.getAllExprList();
                for (ALittleAllExpr all_expr : all_expr_list) {
                    all_expr_content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
                }
            }

            mOpenRawSet = false;

            content.append(all_expr_content);
            content.append(pre_tab).append("end\n");

            content.append("\n");
        }
        //构建getter函数///////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassGetterDec> class_getter_decList = root.getClassGetterDecList();
        for (ALittleClassGetterDec class_getter_dec : class_getter_decList) {
            ALittleMethodNameDec class_method_nameDec = class_getter_dec.getMethodNameDec();
            if (class_method_nameDec == null) {
                throw new Exception("class " + class_name + " getter函数没有函数名");
            }
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".__getter:")
                    .append(class_method_nameDec.getIdContent().getText())
                    .append("()\n");

            ALittleMethodBodyDec class_method_body_dec = class_getter_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                throw new Exception("class " + class_name + " getter函数没有函数体");
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
            }
            content.append(pre_tab).append("end\n");

            content.append("\n");
        }
        //构建setter函数///////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassSetterDec> class_setter_decList = root.getClassSetterDecList();
        for (ALittleClassSetterDec class_setter_dec : class_setter_decList) {
            ALittleMethodNameDec class_method_nameDec = class_setter_dec.getMethodNameDec();
            if (class_method_nameDec == null) {
                throw new Exception("class " + class_name + " setter函数没有函数名");
            }
            ALittleMethodParamOneDec param_dec = class_setter_dec.getMethodParamOneDec();
            if (param_dec == null) {
                throw new Exception("class " + class_name + " setter函数必须要有一个参数");
            }
            ALittleMethodParamNameDec param_nameDec = param_dec.getMethodParamNameDec();
            if (param_nameDec == null) {
                throw new Exception("class " + class_name + " 函数没有定义函数名");
            }
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".__setter:")
                    .append(class_method_nameDec.getIdContent().getText())
                    .append("(")
                    .append(param_nameDec.getIdContent().getText())
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_setter_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                throw new Exception("class " + class_name + " setter函数没有函数体");
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
            }
            content.append(pre_tab).append("end\n");

            content.append("\n");
        }
        //构建成员函数//////////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassMethodDec> class_method_decList = root.getClassMethodDecList();
        for (ALittleClassMethodDec class_method_dec : class_method_decList) {
            ALittleMethodNameDec class_method_nameDec = class_method_dec.getMethodNameDec();
            if (class_method_nameDec == null) {
                throw new Exception("class " + class_name + " 成员函数没有函数名");
            }

            List<String> param_name_list = new ArrayList<>();
            ALittleMethodParamDec param_dec = class_method_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                    ALittleMethodParamNameDec param_nameDec = param_one_dec.getMethodParamNameDec();
                    if (param_nameDec == null) {
                        throw new Exception("class " + class_name + " 成员函数没有参数名");
                    }
                    param_name_list.add(param_nameDec.getIdContent().getText());
                }
            }
            String method_param_list = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(":")
                    .append(class_method_nameDec.getText())
                    .append("(")
                    .append(method_param_list)
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_method_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                throw new Exception("class " + class_name + " 成员函数没有函数体");
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
            }
            content.append(pre_tab).append("end\n");

            if (class_method_dec.getCoModifier() != null && class_method_dec.getCoModifier().getText().equals("async")) {
                content.append(pre_tab)
                        .append(class_name).append(".").append(class_method_nameDec.getIdContent().getText())
                        .append(" = ").append(namespace_pre).append("CoWrap(")
                        .append(class_name).append(".").append(class_method_nameDec.getIdContent().getText())
                        .append(")\n");
            }

            content.append("\n");
        }
        //构建静态函数//////////////////////////////////////////////////////////////////////////////////////////
        List<ALittleClassStaticDec> class_static_decList = root.getClassStaticDecList();
        for (ALittleClassStaticDec class_static_dec : class_static_decList) {
            ALittleMethodNameDec class_method_nameDec = class_static_dec.getMethodNameDec();
            if (class_method_nameDec == null) {
                throw new Exception("class " + class_name + " 静态函数没有函数名");
            }
            List<String> param_name_list = new ArrayList<>();
            ALittleMethodParamDec param_dec = class_static_dec.getMethodParamDec();
            if (param_dec != null) {
                List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
                for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                    ALittleMethodParamNameDec param_nameDec = param_one_dec.getMethodParamNameDec();
                    if (param_nameDec == null) {
                        throw new Exception("class " + class_name + " 静态函数没有参数名");
                    }
                    param_name_list.add(param_nameDec.getIdContent().getText());
                }
            }

            String method_param_list = String.join(", ", param_name_list);
            content.append(pre_tab)
                    .append("function ")
                    .append(class_name)
                    .append(".")
                    .append(class_method_nameDec.getText())
                    .append("(")
                    .append(method_param_list)
                    .append(")\n");

            ALittleMethodBodyDec class_method_body_dec = class_static_dec.getMethodBodyDec();
            if (class_method_body_dec == null) {
                throw new Exception("class " + class_name + " 静态函数没有函数体");
            }
            List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
            for (ALittleAllExpr all_expr : all_expr_list) {
                content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
            }
            content.append(pre_tab).append("end\n");

            if (class_static_dec.getCoModifier() != null && class_static_dec.getCoModifier().getText().equals("async")) {
                content.append(pre_tab)
                        .append(class_name).append(".").append(class_method_nameDec.getIdContent().getText())
                        .append(" = ").append(namespace_pre).append("CoWrap(")
                        .append(class_name).append(".").append(class_method_nameDec.getIdContent().getText())
                        .append(")\n");
            }
            content.append("\n");
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        return content.toString();
    }

    @NotNull
    private String GenerateInstance(ALittleInstanceDec root, String pre_tab) throws Exception {
        String globalPreString = "";
        ALittleAccessModifier access_modifier_dec = root.getAccessModifier();
        if (access_modifier_dec != null && access_modifier_dec.getText().equals("public")) {
            globalPreString = "_G.";
        }
        return GenerateVarAssignExpr(root.getVarAssignExpr(), pre_tab, globalPreString);
    }

    @NotNull
    private String GenerateGlobalMethod(ALittleGlobalMethodDec root, String pre_tab) throws Exception {
        ALittleMethodNameDec global_method_nameDec = root.getMethodNameDec();
        if (global_method_nameDec == null) {
            throw new Exception("全局函数没有函数名");
        }

        String namespace_pre = "ALittle.";
        if (mNamespaceName.equals("ALittle")) namespace_pre = "";

        String method_name = global_method_nameDec.getIdContent().getText();

        List<String> param_name_list = new ArrayList<>();
        ALittleMethodParamDec param_dec = root.getMethodParamDec();
        if (param_dec != null) {
            List<ALittleMethodParamOneDec> param_one_decList = param_dec.getMethodParamOneDecList();
            for (ALittleMethodParamOneDec param_one_dec : param_one_decList) {
                ALittleMethodParamNameDec param_nameDec = param_one_dec.getMethodParamNameDec();
                if (param_nameDec == null) {
                    throw new Exception("全局函数" + method_name + "没有参数名");
                }
                param_name_list.add(param_nameDec.getIdContent().getText());
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
            throw new Exception("全局函数 " + method_name + " 没有函数体");
        }
        List<ALittleAllExpr> all_expr_list = class_method_body_dec.getAllExprList();
        for (ALittleAllExpr all_expr : all_expr_list) {
            content.append(GenerateAllExpr(all_expr, pre_tab + "\t"));
        }
        content.append(pre_tab).append("end\n");

        // 协程判定
        if (root.getCoModifier() != null && root.getCoModifier().getText().equals("async")) {
            content.append(pre_tab).append(method_name)
                    .append(" = ").append(namespace_pre).append("CoWrap(")
                    .append(method_name).append(")\n");
        }

        content.append("\n");

        return content.toString();
    }

    @NotNull
    private String GenerateNamespace(ALittleNamespaceDec root) throws Exception {
        ALittleNamespaceNameDec nameDec = root.getNamespaceNameDec();
        if (nameDec == null) {
            throw new Exception("命名域没有定义名字");
        }
        mNamespaceName = nameDec.getIdContent().getText();

        StringBuilder content;
        if (mNamespaceName.equals("lua"))
            content = new StringBuilder("\n");
        else
            content = new StringBuilder("\nmodule(\"" + mNamespaceName + "\", package.seeall)\n\n");

        StringBuilder otherContent = new StringBuilder();
        PsiElement[] child_list = root.getChildren();
        for (PsiElement child : child_list) {
            // 处理结构体
            if (child instanceof ALittleStructDec) {
            // 处理enum
            } else if (child instanceof ALittleEnumDec) {
                otherContent.append(GenerateEnum((ALittleEnumDec) child, ""));
            // 处理class
            } else if (child instanceof ALittleClassDec) {
                otherContent.append(GenerateClass((ALittleClassDec) child, ""));
            // 处理instance
            } else if (child instanceof ALittleInstanceDec) {
                otherContent.append(GenerateInstance((ALittleInstanceDec)child, ""));
            // 处理全局函数
            } else if (child instanceof ALittleGlobalMethodDec) {
                otherContent.append(GenerateGlobalMethod((ALittleGlobalMethodDec)child, ""));
            }
        }

        if (mRawsetUseCount > 0)
            content.append("local ___rawset = rawset\n");
        content.append("local ___pairs = pairs\n");
        content.append("local ___ipairs = ipairs\n");
        content.append("local ___coroutine = coroutine\n");
        content.append("\n");

        content.append(otherContent);

        return content.toString();
    }
}
