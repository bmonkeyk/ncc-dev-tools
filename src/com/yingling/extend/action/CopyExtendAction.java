package com.yingling.extend.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yingling.abs.AbstractAnAction;
import com.yingling.extend.util.ExtendCopyUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CopyExtendAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
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

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectFile = getSelectFile(e);
        boolean flag = true;

        if(selectFile == null || (selectFile != null && selectFile instanceof FsRoot)){
            flag = false;
        } else {
            File file = new File(selectFile.getPath());
            if (file.isFile()) {
                flag = file.getName().endsWith(".xml") && file.getPath().contains("yyconfig" + File.separator + "modules") && (file.getParent().endsWith("action") || file.getParent().endsWith("authorize"));
            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
