package com.yingling.debug.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yingling.debug.util.CreatApplicationConfigurationUtil;
import org.jetbrains.annotations.NotNull;

public class NewClientApplicationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String message = "success";
        try {
            CreatApplicationConfigurationUtil.createApplicationConfiguration(event, false);
            Messages.showInfoMessage(message, "Tips");
        } catch (Exception e) {
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }
}
