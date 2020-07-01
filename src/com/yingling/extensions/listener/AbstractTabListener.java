package com.yingling.extensions.listener;

import com.yingling.extensions.component.NccDevSettingDlg;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AbstractTabListener implements ChangeListener, MouseListener {

    private NccDevSettingDlg dlg;

    public AbstractTabListener(NccDevSettingDlg dlg) {
        this.dlg = dlg;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        afterChange(e, dlg);
    }

    protected abstract void afterChange(ChangeEvent event, NccDevSettingDlg dlg);

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
