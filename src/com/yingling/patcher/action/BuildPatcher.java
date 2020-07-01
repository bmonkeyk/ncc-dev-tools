package com.yingling.patcher.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yingling.patcher.dialog.PatcherDialog;

public class BuildPatcher extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        PatcherDialog dialog = new PatcherDialog(e);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();

    }

}
