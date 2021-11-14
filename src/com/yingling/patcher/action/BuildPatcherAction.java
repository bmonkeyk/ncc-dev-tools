package com.yingling.patcher.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yingling.abs.AbstractAnAction;
import com.yingling.base.ProjectManager;
import com.yingling.patcher.dialog.PatcherDialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;


public class BuildPatcherAction extends AbstractAnAction {

    @Override
    public void doAction(AnActionEvent event) {
        PatcherDialog dialog = new PatcherDialog(event);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile[] selectFileArr = getSelectFileArr(e);
        boolean flag = true;
        if (selectFileArr == null || selectFileArr.length == 0) {
            flag = false;
        } else {
            for (VirtualFile virtualFile : selectFileArr) {
                if (virtualFile instanceof FsRoot) {
                    flag = false;
                    break;
                }
                flag = isModuleChild(virtualFile, e) || isMavenModuleChild(virtualFile, e);
//                if (flag) {
//                    Module module = getSelectModule(e);
//                    if (virtualFile.getParent().equals(module.getModuleFile().getParent())) {
//                        flag = new File(virtualFile.getPath() + File.separator + "component.xml").exists();
//                    }
//                }
            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
