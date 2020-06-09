package plugin.generate;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.FileHelper;
import plugin.alittle.PsiHelper;
import plugin.guess.*;
import plugin.module.ALittleConfig;
import plugin.psi.*;
import plugin.reference.ALittleReferenceInterface;

import java.io.File;
import java.util.*;

public class ALittleTranslation {
    // 当前文件命名域
    protected String m_namespace_name = "";
    // 当前模块所在工程路径
    protected String m_project_path = "";
    // 当前文件路径
    protected String m_file_path = "";

    // 定义依赖
    protected final Set<String> m_define_rely = new HashSet<>();
    // 运行依赖
    protected final Set<String> m_run_rely = new HashSet<>();
    // 当前是否是定义依赖
    protected boolean m_is_define_relay = false;

    @NotNull
    public static ALittleTranslation createTranslation(Project project)
    {
        if (ALittleConfig.getConfig(project).getTargetLanguage().equals("Lua"))
            return new ALittleTranslationLua();
        else if (ALittleConfig.getConfig(project).getTargetLanguage().equals("JavaScript"))
            return new ALittleTranslationJavaScript();
        return new ALittleTranslation();
    }

    protected void addRelay(PsiElement element)
    {
        if (element == null) return;

        String full_path = element.getContainingFile().getOriginalFile().getVirtualFile().getPath();
        if (full_path.equals(m_file_path)) return;

        if (PsiHelper.isRegister(element)) return;

        if (m_is_define_relay)
        {
            m_define_rely.add(full_path);
            return;
        }
        m_run_rely.add(full_path);
    }

    // 检查语法错误
    private void checkErrorElement(PsiElement element, boolean fullCheck) throws ALittleGuessException {
        for(PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof PsiErrorElement) {
                throw new ALittleGuessException(child, ((PsiErrorElement) child).getErrorDescription());
            }

            // 判断语义错误
            if (fullCheck) {
                PsiReference ref = element.getReference();
                if (ref instanceof ALittleReferenceInterface) {
                    ((ALittleReferenceInterface) ref).checkError();
                }
            }

            checkErrorElement(child, fullCheck);
        }
    }

    public void generate(ALittleFile file, boolean full_check) throws ALittleGuessException
    {
        // 获取命名域
        ALittleNamespaceDec namespace_dec = PsiHelper.getNamespaceDec(file);
        if (namespace_dec == null) throw new ALittleGuessException(null, "没有定义命名域 namespace");

        ALittleNamespaceNameDec name_dec = namespace_dec.getNamespaceNameDec();
        if (name_dec == null) throw new ALittleGuessException(null, "命名域没有定义名字");

        // 获取语法错误
        try {
            checkErrorElement(file, full_check);
        } catch (ALittleGuessException e) {
            throw new ALittleGuessException(null, e.getElement().getContainingFile().getName() + "有语法错误:" + e.getError());
        }

        // 如果命名域有register标记，那么就不需要生成
        if (PsiHelper.isRegister(namespace_dec.getModifierList())) return;
        if (PsiHelper.isLanguageEnable(namespace_dec.getModifierList())) return;

        // 保存到文件
        FileIndexFacade facade = FileIndexFacade.getInstance(file.getProject());
        Module module = facade.getModuleForFile(file.getVirtualFile());
        if (module == null) {
            return;
        }

        m_namespace_name = name_dec.getText();

        try {
            m_project_path = FileHelper.getDirectoryName(module.getModuleFilePath(), true);
            m_file_path = file.getVirtualFile().getPath();
            String full_path = FileHelper.calcTargetFullPath(m_project_path, m_file_path, getExt());
            String full_dir = FileHelper.getDirectoryName(full_path, false);
            new File(full_dir).mkdirs();
            // 生成代码
            String content = generateRoot(namespace_dec.getNamespaceElementDecList());
            FileHelper.writeFile(full_path, content);
        } catch (Exception e) {
            throw new ALittleGuessException(null, "模块路径获取失败:" + e.getMessage());
        }
    }

    protected String generateRoot(List<ALittleNamespaceElementDec> element_list) throws ALittleGuessException
    {
        throw  new ALittleGuessException(null, "未实现生成代码");
    }

    protected String getExt()
    {
        return "";
    }
}
