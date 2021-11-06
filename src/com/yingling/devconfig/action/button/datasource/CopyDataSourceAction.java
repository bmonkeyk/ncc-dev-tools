package com.yingling.devconfig.action.button.datasource;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.DataSourceCopyDlg;
import com.yingling.devconfig.DevConfigDialog;

import java.awt.event.ActionEvent;

/**
 * 复制数据源
 */
public class CopyDataSourceAction extends AbstractButtonAction {
    public CopyDataSourceAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        DevConfigDialog dialog = (DevConfigDialog) getDialog();
        DataSourceCopyDlg dlg = new DataSourceCopyDlg();
        dlg.setParentDlg(dialog);
        dlg.setVisible(true);
    }
}
