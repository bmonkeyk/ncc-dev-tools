package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.DataSourceCopyDlg;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CopyDataBaseAction extends AbstractButtonAction {
    public CopyDataBaseAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        DataSourceCopyDlg copyDlg = new DataSourceCopyDlg();
        copyDlg.setParent(dlg);
        copyDlg.setTitle("copy " + dlg.getDataSourceMetaBox().getSelectedItem() + " to next one");
        copyDlg.setSize(new Dimension(600, 170));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        copyDlg.setLocation(Double.valueOf(screenWidth / 2).intValue() - 300, Double.valueOf(screenHeight / 2).intValue() - 85);
        copyDlg.setVisible(true);
    }
}
