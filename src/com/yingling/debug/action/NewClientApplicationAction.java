package com.yingling.debug.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yingling.abs.AbstractAnAction;
import com.yingling.debug.util.CreatApplicationConfigurationUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

//        Module module = e.getData(LangDataKeys.MODULE);
//        boolean flag = file != null
//                && module != null
//                && !(file instanceof FsRoot)
//                && new File(file.getPath()).isDirectory()
//                && module.getName().equals(file.getName());

        boolean flag = isModuleChild(file, e);
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
