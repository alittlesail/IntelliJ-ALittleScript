package plugin.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ALittleModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {
    private List<Pair<String, String>> m_source_paths = null;
    @Override
    public void setupRootModel(ModifiableRootModel model) throws ConfigurationException {
        ContentEntry contentEntry = this.doAddContentEntry(model);
        if (contentEntry != null) {
            List<Pair<String, String>> source_paths = this.getSourcePaths();
            if (source_paths != null) {
                for (Pair<String, String> source_path : source_paths) {
                    String path = source_path.first;
                    new File(path).mkdirs();
                    VirtualFile source_root = LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
                    if (source_root != null)
                        contentEntry.addSourceFolder(source_root, false, source_path.second);
                }
            }
        }
    }

    @Override
    public ModuleType getModuleType() {
        return ALittleModuleType.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return null;
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {
        if (m_source_paths == null) {
            m_source_paths = new ArrayList<>();

            String path = this.getContentEntryPath() + File.separator + "src";
            new File(path).mkdirs();
            m_source_paths.add(new Pair<>(path, ""));
        }
        return m_source_paths;
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> list) {
        m_source_paths = list;
    }

    @Override
    public void addSourcePath(Pair<String, String> pair) {
        m_source_paths.add(pair);
    }
}