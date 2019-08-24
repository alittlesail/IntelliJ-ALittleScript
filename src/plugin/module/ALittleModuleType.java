package plugin.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import plugin.component.ALittleIcons;

import javax.swing.*;

public class ALittleModuleType extends ModuleType<ALittleModuleBuilder> {
    private static final String ID = "ALittle";

    public ALittleModuleType() {
        super(ID);
    }

    public static ALittleModuleType getInstance() {
        return (ALittleModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public ALittleModuleBuilder createModuleBuilder() {
        return new ALittleModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "ALittle";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ALittle";
    }


    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return ALittleIcons.MODULE;
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ALittleModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }
}
