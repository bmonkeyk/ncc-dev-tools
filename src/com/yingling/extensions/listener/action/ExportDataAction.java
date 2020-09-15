package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;

import java.awt.event.ActionEvent;

public class ExportDataAction extends AbstractButtonAction {
    private NccDevSettingDlg dlg;

    public ExportDataAction(NccDevSettingDlg nccDevSettingDlg) {
        super(nccDevSettingDlg);
        dlg = nccDevSettingDlg;
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {


        //

    }


}
