package com.yingling.abs;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 下拉监听
 */
public abstract class AbstractItemListener implements ItemListener {

    private AbstractDialog dialog;

    public AbstractItemListener(AbstractDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        afterSelect(e);
    }

    public abstract void afterSelect(ItemEvent e);

    public AbstractDialog getDialog() {
        return dialog;
    }
}
