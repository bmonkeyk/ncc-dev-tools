package com.yingling.module.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.abs.AbstractAnAction;
import com.yingling.module.NewModuleDialog;

/**
 * 新建nc模块
 */
public class CreateNewNCModuleAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        NewModuleDialog dialog = new NewModuleDialog(event);
        dialog.setSize(900, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}
