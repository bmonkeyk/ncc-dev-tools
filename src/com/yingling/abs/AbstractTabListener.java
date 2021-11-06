package com.yingling.abs;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AbstractTabListener implements ChangeListener, MouseListener {

    private AbstractDialog dlg;

    public AbstractTabListener(AbstractDialog dlg) {
        this.dlg = dlg;
    }

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

    @Override
    public void stateChanged(ChangeEvent e) {
        afterChange(e, dlg);
    }

    protected abstract void afterChange(ChangeEvent event, AbstractDialog dlg);

    protected abstract void click(MouseEvent event, AbstractDialog dlg);

    public AbstractDialog getDlg() {
        return dlg;
    }
}
