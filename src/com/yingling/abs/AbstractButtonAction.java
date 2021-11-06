package com.yingling.abs;

import com.intellij.openapi.ui.Messages;
import com.yingling.base.BusinessException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * swing 抽象按钮
 */
public abstract class AbstractButtonAction implements ActionListener {

    private AbstractDialog dialog;

    public AbstractButtonAction(AbstractDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            doAction(event);
        } catch (BusinessException e) {
            Messages.showErrorDialog(e.getMessage(), "出错了");
        }
    }

    public abstract void doAction(ActionEvent event) throws BusinessException;

    public AbstractDialog getDialog() {
        return dialog;
    }

    public void setDialog(AbstractDialog dialog) {
        this.dialog = dialog;
    }
}
