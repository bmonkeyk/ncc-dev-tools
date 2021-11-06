package com.yingling.script.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractAnAction;
import com.yingling.script.common.powerdesigner.impl.DbCreateServiceImpl;
import com.yingling.script.common.powerdesigner.itf.IDbCreateService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * pdm文件建库脚本导出
 */
public class ExportDBCreateScriptsAction extends AbstractAnAction {
    @Override
    public void doAction(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile pdmFile = getSelectFile(event);

        VirtualFile script = pdmFile.getParent().getParent().getParent();
        VirtualFile folder = getChildFile(script, "dbcreate");

        if (folder == null || !folder.exists()) {
            try {
                folder = script.createChildDirectory(null, "dbcreate");
            } catch (IOException e1) {
                Messages.showMessageDialog(project, "creat dbcreate{failed}", "tips", Messages.getInformationIcon());
                return;
            }
        }
        try {
            IDbCreateService dbCreateService = new DbCreateServiceImpl();
            dbCreateService.geneSqlFile(pdmFile, false, folder);
        } catch (Exception ex) {
            Messages.showErrorDialog(ex.getMessage(), "出错了");
        }

    }


    private VirtualFile getChildFile(VirtualFile parent, String fileName) {
        if (parent == null) {
            return null;
        }
        VirtualFile[] children = parent.getChildren();
        if (children == null || children.length == 0) {
            return null;
        }

        for (VirtualFile child : children) {
            if (fileName.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    /**
     * 设置按钮状态
     *
     * @param e
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectFile = getSelectFile(e);
        boolean flag = selectFile != null && !selectFile.isDirectory() && selectFile.getPath().contains("script" + File.separator + "conf") && selectFile.getName().endsWith(".pdm");
        e.getPresentation().setEnabledAndVisible(flag);
    }
}
