package com.yingling.extensions.listener;

import com.yingling.extensions.component.NccDevSettingDlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 按钮监听基础类
 */
public abstract class AbstractButtonAction implements ActionListener {

    private NccDevSettingDlg dlg;

    public AbstractButtonAction(NccDevSettingDlg dlg) {
        this.dlg = dlg;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        doAction(event, dlg);
    }

    protected abstract void doAction(ActionEvent event, NccDevSettingDlg dlg);
}
