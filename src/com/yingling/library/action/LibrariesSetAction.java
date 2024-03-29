package com.yingling.library.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractAnAction;
import com.yingling.base.BusinessException;
import com.yingling.base.ProjectManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 设置模块累路径
 */
public class LibrariesSetAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        String message = "success";
        try {
            boolean flag = isNCModule(event);
            if (flag) {
                ProjectManager.getInstance().setModuleLibrary(event.getProject(), event.getData(LangDataKeys.MODULE));
                Messages.showInfoMessage(message, "Tips");
            }
        } catch (BusinessException e) {
            message = e.getMessage();
            Messages.showInfoMessage(message, "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = getSelectFile(e);
        boolean flag = isModuleChild(file, e);
        if(flag){
            flag = new File(file.getPath() + File.separator + "META-INF" + File.separator + "module.xml").exists();
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
