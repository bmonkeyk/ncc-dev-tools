package com.yingling.debug.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractAnAction;
import com.yingling.debug.util.CreatApplicationConfigurationUtil;
import org.jetbrains.annotations.NotNull;

public class NewClientApplicationAction extends AbstractAnAction {
    @Override
    public void doAction(@NotNull AnActionEvent event) {
        String message = "success";
        try {
            CreatApplicationConfigurationUtil.createApplicationConfiguration(event, false);
            Messages.showInfoMessage(message, "Tips");
        } catch (Exception e) {
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isNCModule(e));
    }
}
