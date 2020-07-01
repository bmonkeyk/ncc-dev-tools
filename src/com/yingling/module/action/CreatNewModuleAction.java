package com.yingling.module.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.module.dialog.NewModuleDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 创建nc module
 */
public class CreatNewModuleAction extends AnAction {
    public CreatNewModuleAction() {
        super("", null, AllIcons.Actions.ModuleDirectory);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
//        RunConfigurable configurable = RunConfigurableKt.createRunConfigurationConfigurable(event.getProject()).selectConfigurableOnShow(true);
        NewModuleDialog dialog = new NewModuleDialog(event);
        dialog.setSize(900, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}
