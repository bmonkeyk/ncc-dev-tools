package com.yingling.devconfig.action.button.module;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.base.ModuleFileUtil;
import com.yingling.devconfig.util.TableModelUtil;

import javax.swing.JTable;
import java.awt.event.ActionEvent;
import java.util.Set;

/**
 * 应用默认按钮
 */
public class DefaultModuleAction extends AbstractButtonAction {

    private int type;

    public DefaultModuleAction(AbstractDialog dialog, int type) {
        super(dialog);
        this.type = type;
    }

    @Override
    public void doAction(ActionEvent event) {

        if (type == TableModelUtil.MODULE_TYPE_MUST) {
            JTable table = getDialog().getComponent(JTable.class, "mustTable");
            Set<String> set = ModuleFileUtil.getModuleSet();
            int count = getDialog().getComponent(JTable.class, "mustTable").getRowCount();
            for (int i = 0; i < count; i++) {
                Object obj = table.getModel().getValueAt(i, 2);
                if (set.contains(obj)) {
                    table.setValueAt(true, i, 1);
                }
            }
        } else if (type == TableModelUtil.MODULE_TYPE_SEL) {
            JTable table = getDialog().getComponent(JTable.class, "selTable");
            JTable mustTable = getDialog().getComponent(JTable.class, "mustTable");
            int count = getDialog().getComponent(JTable.class, "mustTable").getRowCount();
            for (int i = 0; i < count; i++) {
                Object obj = mustTable.getModel().getValueAt(i, 1);
                boolean checked = obj == null ? false : Boolean.valueOf(obj.toString());
                table.setValueAt(checked, i, 1);
            }
        }
    }
}
