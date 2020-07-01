package com.yingling.extensions.listener;

import com.yingling.extensions.component.NccDevSettingDlg;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public abstract class AbstractItemListener implements ItemListener {

    private NccDevSettingDlg dlg;

    public AbstractItemListener(NccDevSettingDlg dlg) {
        this.dlg = dlg;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }
        afterEdit(e, dlg);
    }

    protected abstract void afterEdit(ItemEvent event, NccDevSettingDlg dlg);
}
