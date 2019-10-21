package plugin.module;
import com.intellij.openapi.module.Module;
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
import plugin.link.ALittleLinkConfig;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ALittleModuleConfigurationEditor implements ModuleConfigurationEditor {
    private ModuleConfigurationState mState;
    private boolean mModified = false;

    // 输出目录
    private JTextField myOutputPathTextField;

    // Csv目录
    private JTextField myCsvPathTextField;

    // Mysql配置
    private JTextField myMysqlIpTextField;
    private JTextField myMysqlPortTextField;
    private JTextField myMysqlUserTextField;
    private JTextField myMysqlPasswordTextField;

    protected ALittleModuleConfigurationEditor(final ModuleConfigurationState state) {
        mState = state;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        final JPanel outputPathsPanel = new JPanel(new GridBagLayout());

        myOutputPathTextField = addConfigureUI(outputPathsPanel, "脚本生成目录:");
        myCsvPathTextField = addConfigureUI(outputPathsPanel, "Csv目录:");
        myMysqlIpTextField = addConfigureUI(outputPathsPanel, "Mysql IP:");
        myMysqlPortTextField = addConfigureUI(outputPathsPanel, "Mysql Port:");
        myMysqlUserTextField = addConfigureUI(outputPathsPanel, "Mysql User:");
        myMysqlPasswordTextField = addConfigureUI(outputPathsPanel, "Mysql Password:");

        Module module = mState.getRootModel().getModule();
        ALittleLinkConfig config = ALittleLinkConfig.getConfig(module);
        myOutputPathTextField.setText(config.getOutputPath());
        myCsvPathTextField.setText(config.getCsvPath());
        myMysqlIpTextField.setText(config.getMysqlIp());
        myMysqlPortTextField.setText(config.getMysqlPort());
        myMysqlUserTextField.setText(config.getMysqlUser());
        myMysqlPasswordTextField.setText(config.getMysqlPassword());

        listenChange(myOutputPathTextField);
        listenChange(myCsvPathTextField);
        listenChange(myMysqlIpTextField);
        listenChange(myMysqlPortTextField);
        listenChange(myMysqlUserTextField);
        listenChange(myMysqlPasswordTextField);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(IdeBorderFactory.createTitledBorder(ProjectBundle.message("project.roots.output.compiler.title")));
        panel.add(outputPathsPanel, BorderLayout.NORTH);
        return panel;
    }

    private void listenChange(JTextField textField) {
        textField.addPropertyChangeListener(new PropertyChangeListener() {
            String mTmp = textField.getText();
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (mModified) return;
                if (mTmp.equals(textField.getToolTipText())) return;
                mModified = true;
            }
        });
    }

    private JTextField addConfigureUI(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField();
        panel.add(label, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 0, 0, 4), 0, 0));
        panel.add(textField, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 4, 0, 0), 0, 0));
        return textField;
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

        Module module = mState.getRootModel().getModule();
        ALittleLinkConfig config = ALittleLinkConfig.getConfig(module);
        config.setOutputPath(myOutputPathTextField.getText());
        config.setCsvPath(myCsvPathTextField.getText());
        config.setMysql(myMysqlIpTextField.getText(),
                myMysqlPortTextField.getText(),
                myMysqlUserTextField.getText(),
                myMysqlPasswordTextField.getText());
        config.save();
    }
}