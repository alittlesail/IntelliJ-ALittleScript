package plugin.module;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ALittleModuleConfigurationEditor implements ModuleConfigurationEditor {
    private final ModuleConfigurationState mState;
    private boolean mModified = false;

    // 输出目录
    private ComboBox<String> myTargetLanguageTextField;

    protected ALittleModuleConfigurationEditor(final ModuleConfigurationState state) {
        mState = state;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        final JPanel outputPathsPanel = new JPanel(new GridBagLayout());

        myTargetLanguageTextField = addConfigureComboBox(outputPathsPanel, "目标语言:");
        myTargetLanguageTextField.addItem("Lua");
        myTargetLanguageTextField.addItem("JavaScript");

        listenChange(myTargetLanguageTextField);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(IdeBorderFactory.createTitledBorder(ProjectBundle.message("project.roots.output.compiler.title")));
        panel.add(outputPathsPanel, BorderLayout.NORTH);
        return panel;
    }

    private void listenChange(JTextField item) {
        item.addPropertyChangeListener(new PropertyChangeListener() {
            final String mTmp = item.getText();
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (mModified) return;
                if (mTmp.equals(item.getToolTipText())) return;
                mModified = true;
            }
        });
    }

    private void listenChange(ComboBox<String> item) {
        item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                mModified = true;
            }
        });
    }

    private JTextField addConfigureTextField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        JTextField item = new JTextField();
        panel.add(label, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, JBUI.insets(6, 0, 0, 4), 0, 0));
        panel.add(item, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, JBUI.insets(6, 4, 0, 0), 0, 0));
        return item;
    }

    private ComboBox<String> addConfigureComboBox(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        ComboBox<String> item = new ComboBox<>();
        panel.add(label, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, JBUI.insets(6, 0, 0, 4), 0, 0));
        panel.add(item, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, JBUI.insets(6, 4, 0, 0), 0, 0));
        return item;
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

        Project project = mState.getProject();
        ALittleConfig config = ALittleConfig.getConfig(project);

        Object item = myTargetLanguageTextField.getSelectedItem();
        if (item != null)
            config.setTargetLanguage(item.toString());
        else
            config.setTargetLanguage("Lua");
        config.save();
    }
}