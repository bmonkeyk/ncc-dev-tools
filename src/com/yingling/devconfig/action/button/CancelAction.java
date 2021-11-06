package com.yingling.devconfig.action.button;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;

import java.awt.event.ActionEvent;

/**
 * 设置面板取消按钮
 */
public class CancelAction extends AbstractButtonAction {
    public CancelAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        getDialog().dispose();
    }
}
