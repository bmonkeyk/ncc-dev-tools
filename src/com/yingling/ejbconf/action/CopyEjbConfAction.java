package com.yingling.ejbconf.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yingling.ejbconf.util.EjbConfCopyUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 复制upm到nchome
 */
public class CopyEjbConfAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        String message = "success";
        EjbConfCopyUtil util = new EjbConfCopyUtil();
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
