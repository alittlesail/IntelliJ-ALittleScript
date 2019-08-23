package plugin.module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.IdeBorderFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ALittleModuleConfigurationEditor implements ModuleConfigurationEditor {
    private JLabel myOutputLabel;
    private JTextField myOutputTextField;
    private ModuleConfigurationState mState;
    private boolean mModified = false;

    protected ALittleModuleConfigurationEditor(final ModuleConfigurationState state) {
        mState = state;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        final JPanel outputPathsPanel = new JPanel(new GridBagLayout());

        myOutputLabel = new JLabel("脚本生成目录:");
        myOutputTextField = new JTextField();
        outputPathsPanel.add(myOutputLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 0, 0, 4), 0, 0));
        outputPathsPanel.add(myOutputTextField, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 4, 0, 0), 0, 0));

        // fill with data
        updateOutputPathPresentation();

        myOutputTextField.addPropertyChangeListener(new PropertyChangeListener() {
            String mTmp = myOutputTextField.getText();
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (mModified) return;
                if (mTmp.equals(myOutputTextField.getToolTipText())) return;
                mModified = true;
            }
        });

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(IdeBorderFactory.createTitledBorder(ProjectBundle.message("project.roots.output.compiler.title")));
        panel.add(outputPathsPanel, BorderLayout.NORTH);
        return panel;
    }

    private void updateOutputPathPresentation() {
        final VirtualFile compilerOutputPath = getCompilerExtension().getCompilerOutputPath();
        if (compilerOutputPath != null) {
            myOutputTextField.setText(FileUtil.toSystemDependentName(compilerOutputPath.getPath()));
        } else {
            final String compilerOutputUrl = getCompilerExtension().getCompilerOutputUrl();
            if (compilerOutputUrl != null) {
                myOutputTextField.setText(FileUtil.toSystemDependentName(VfsUtilCore.urlToPath(compilerOutputUrl)));
            }
        }
    }

    @Override
    public void saveData() {
        getCompilerExtension().commit();
    }

    @Override
    public String getDisplayName() {
        return ProjectBundle.message("output.tab.title");
    }

    @Override
    @Nullable
    @NonNls
    public String getHelpTopic() {
        return "project.structureModulesPage.outputJavadoc";
    }

    @Override
    public void moduleStateChanged() {
    }

    public CompilerModuleExtension getCompilerExtension() {
        return mState.getRootModel().getModuleExtension(CompilerModuleExtension.class);
    }

    @Override
    public boolean isModified() {
        return mModified;
    }

    @Override
    public void apply() throws ConfigurationException {
        mModified = false;
        getCompilerExtension().setCompilerOutputPath(VfsUtilCore.pathToUrl(myOutputTextField.getText()));
    }
}