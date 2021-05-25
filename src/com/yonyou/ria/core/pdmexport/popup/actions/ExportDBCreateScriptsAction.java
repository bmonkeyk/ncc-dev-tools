package com.yonyou.ria.core.pdmexport.popup.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractAnAction;
import com.yonyou.common.database.powerdesigner.impl.DbCreateServiceImpl;
import com.yonyou.common.database.powerdesigner.itf.IDbCreateService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExportDBCreateScriptsAction extends AbstractAnAction {

    private static final IDbCreateService dbCreateService = new DbCreateServiceImpl();

    @Override
    public void doAction(@NotNull AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile pdmFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        //向上找三层到script目录
        assert pdmFile != null;
        VirtualFile script = pdmFile.getParent().getParent().getParent();
        VirtualFile folder = getChildFile(script, "dbcreate");
        if (folder == null || !folder.exists()) {
            try {
                folder = script.createChildDirectory(null, "dbcreate");
            } catch (IOException e1) {
                Messages.showMessageDialog(project, "creat dbcreate{failed", "tips", Messages.getInformationIcon());
                return;
            }
        }
        dbCreateService.geneSqlFile(pdmFile, false, folder);

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

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectFile = getSelectFile(e);
        if (selectFile.isDirectory() || !selectFile.getName().endsWith(".pdm")) {
            e.getPresentation().setEnabled(false);
        }
    }
}
