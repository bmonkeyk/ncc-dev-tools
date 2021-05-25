package com.yingling.libraries.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.pub.exception.BusinessException;
import com.pub.util.ProjectManager;
import com.yingling.abs.AbstractAnAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 为module 设置library
 */
public class LibrarySetAction extends AbstractAnAction {

    @Override
    public void doAction(@NotNull AnActionEvent event) {
        Module selectModule = event.getData(LangDataKeys.MODULE);
        String message = "success";
        try {
            ProjectManager.getInstance().setModuleLibrary(event.getProject(), getSelectModule(event));
            Messages.showInfoMessage(message, "Tips");
        } catch (BusinessException e) {
            e.printStackTrace();
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isNCModule(e));
    }
}
