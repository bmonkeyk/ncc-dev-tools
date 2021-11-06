package com.yingling.abs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AbstractMouseListener implements MouseListener {

    private AbstractDialog dlg;

    public AbstractMouseListener(AbstractDialog dlg) {
        this.dlg = dlg;
    }

    protected abstract void click(MouseEvent event, AbstractDialog dlg);

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

