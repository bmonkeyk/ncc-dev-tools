package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * table 路径选择按钮
 */
public class TableSelAction extends AbstractButtonAction {

    public TableSelAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        String tablePath = System.getenv("NCC_ENV_TABLES");
        JFileChooser chooser = null;
        if (StringUtils.isBlank(tablePath)) {
            tablePath = dlg.getTableText().getText();
        }
        if (StringUtils.isNotBlank(tablePath)) {
            tablePath = new File(tablePath).getParent();
        }
        chooser = new JFileChooser(tablePath);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showOpenDialog(dlg);
        if (JFileChooser.APPROVE_OPTION != ret) {
            return;
        }
        dlg.getTableText().setText(chooser.getSelectedFile().getAbsolutePath());
    }
}
