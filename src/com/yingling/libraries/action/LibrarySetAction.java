package com.yingling.libraries.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import org.jetbrains.annotations.NotNull;

/**
 * 为module 设置library
 */
public class LibrarySetAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Module selectModule = event.getData(LangDataKeys.MODULE);
        String message = "success";
        try {
            ProjectManager.getInstance().setModuleLibrary(event.getProject(),selectModule);
            Messages.showInfoMessage(message, "Tips");
        } catch (BusinessException e) {
            e.printStackTrace();
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }
}
