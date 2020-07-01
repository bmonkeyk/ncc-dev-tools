package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.MustModuleDlg;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;

import java.awt.event.ActionEvent;

public class SetMustAction extends AbstractButtonAction {
    public SetMustAction(NccDevSettingDlg nccDevSettingDlg) {
        super(nccDevSettingDlg);
    }


    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        new MustModuleDlg();
    }
}
