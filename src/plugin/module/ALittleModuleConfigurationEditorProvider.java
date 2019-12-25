package plugin.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

import java.util.ArrayList;
import java.util.List;

public class ALittleModuleConfigurationEditorProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState moduleConfigurationState) {
        List<ModuleConfigurationEditor> editors = new ArrayList<>();
        Module module = moduleConfigurationState.getRootModel().getModule();
        ModuleType<?> module_type = ModuleType.get(module);
        if (module_type == ALittleModuleType.getInstance()) {
            editors.add(new ALittleModuleConfigurationEditor(moduleConfigurationState));
        }
        ModuleConfigurationEditor[] result = new ModuleConfigurationEditor[editors.size()];
        return editors.toArray(result);
    }
}
