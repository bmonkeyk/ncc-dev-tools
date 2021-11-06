package com.yingling.module;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class NCCModuleType extends ModuleType<NCCModuleBuilder> {

    public NCCModuleType() {
        this("JAVA_MODULE");
    }

    protected NCCModuleType(String id) {
        super(id);
    }


    @Override
    public @NotNull
    NCCModuleBuilder createModuleBuilder() {
        return new NCCModuleBuilder();
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Title)
    String getName() {
        return "ncc-dev-module";
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Sentence)
    String getDescription() {
        return "ncc-dev-module";
    }

    @Override
    public @NotNull
    Icon getNodeIcon(boolean b) {
        return AllIcons.Nodes.Module;
    }
}
