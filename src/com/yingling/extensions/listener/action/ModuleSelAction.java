package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.ModuleSelDlg;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;

import java.awt.event.ActionEvent;

/**
 * 启动模块选择
 */
public class ModuleSelAction extends AbstractButtonAction {

    public ModuleSelAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        new ModuleSelDlg();
    }
}
