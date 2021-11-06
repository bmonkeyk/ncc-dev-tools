package com.yingling.abs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class AbstractKeyListener implements KeyListener {

    private AbstractDialog dialog;

    public AbstractKeyListener(AbstractDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressed(e);
    }

    public abstract void pressed(KeyEvent e);

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public AbstractDialog getDialog() {
        return dialog;
    }
}
