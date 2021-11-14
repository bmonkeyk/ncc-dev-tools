package com.yingling.upm.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yingling.abs.AbstractAnAction;
import com.yingling.upm.util.EjbConfCopyUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 复制upm、res文件到nchome
 */
public class CopyUpmAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
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

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectFile = getSelectFile(e);
        boolean flag = true;
        if (selectFile == null || (selectFile != null && selectFile instanceof FsRoot)) {
            flag = false;
        } else {
            File file = new File(selectFile.getPath());
            if (file.isFile()) {
                flag = file.getPath().contains("META-INF") && (file.getName().endsWith(".upm") || file.getName().endsWith(".rest"));
            } else {
                flag = isModuleChild(selectFile, e);
                if (flag) {
                    Module module = getSelectModule(e);
                    if (module != null && module.getModuleFile() != null) {
                        if (selectFile.getParent().equals(module.getModuleFile().getParent())) {
                            flag = new File(selectFile.getPath() + File.separator + "component.xml").exists();
                        }
                    }
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
