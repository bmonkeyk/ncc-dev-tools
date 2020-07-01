package com.yingling.extensions.listener;


import com.yingling.extensions.component.NccDevSettingDlg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AbstractMouseListener implements MouseListener {

    private NccDevSettingDlg dlg;

    public AbstractMouseListener(NccDevSettingDlg dlg) {
        this.dlg = dlg;
    }

    protected abstract void click(MouseEvent event, NccDevSettingDlg dlg);

    @Override
    public void mouseClicked(MouseEvent e) {
        click(e, dlg);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
