package com.yingling.module.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.yingling.abs.AbstractAnAction;
import com.yingling.module.dialog.NewComponetDialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 新增业务组件
 */
public class CreateComponentAction extends AbstractAnAction {

    public CreateComponentAction() {
        super("", null, AllIcons.Actions.ModuleDirectory);
    }

    @Override
    public void doAction(AnActionEvent event) {

        //判断选中的是不是nc module
        boolean isNcModule = isNCModule(event);
        if (!isNcModule) {
            Messages.showErrorDialog("Please select nc module root!", "Error");
            return;
        }

        NewComponetDialog dialog = new NewComponetDialog(event);
        dialog.setSize(900, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}
