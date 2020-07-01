package com.yingling.extend.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yingling.extend.util.ExtendCopyUtil;
import org.jetbrains.annotations.NotNull;

public class CoryExtendAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        String message = "success";
        ExtendCopyUtil util = new ExtendCopyUtil();
        try {
            util.copyToNCHome(event);
            Messages.showInfoMessage(message, "Tips");
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }
}
