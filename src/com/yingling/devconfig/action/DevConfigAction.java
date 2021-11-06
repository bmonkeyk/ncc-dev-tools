package com.yingling.devconfig.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.abs.AbstractAnAction;
import com.yingling.devconfig.DevConfigDialog;

/**
 * 集成环境配置按钮
 */
public class DevConfigAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        DevConfigDialog dialog = new DevConfigDialog();
        dialog.setVisible(true);
    }
}
