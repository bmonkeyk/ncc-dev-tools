package com.yingling.extensions.listener.mouse;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractMouseListener;

import java.awt.event.MouseEvent;

public class SearFieldMouseListener extends AbstractMouseListener {
    public SearFieldMouseListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void click(MouseEvent event, NccDevSettingDlg dlg) {
        dlg.getSearchField().setText("");
    }
}
