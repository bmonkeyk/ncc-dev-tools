package com.yingling.devconfig.action.button.module;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.util.TableModelUtil;

import javax.swing.JTable;
import java.awt.event.ActionEvent;

/**
 * 全选按钮
 */
public class SelAllAction extends AbstractButtonAction {

    private int type;

    public SelAllAction(AbstractDialog dialog, int type) {
        super(dialog);
        this.type = type;
    }


    @Override
    public void doAction(ActionEvent event) {
        JTable table = null;
        if (type == TableModelUtil.MODULE_TYPE_MUST) {
            table = getDialog().getComponent(JTable.class, "mustTable");
        } else if (type == TableModelUtil.MODULE_TYPE_SEL) {
            table = getDialog().getComponent(JTable.class, "selTable");
        }

        if (table != null) {
            TableModelUtil.setAllCheckState(table, true);
        }
    }
}
