package com.yingling.devconfig.action.item;

import com.yingling.abs.AbstractDialog;
import com.yingling.abs.AbstractItemListener;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.script.studio.ui.preference.dbdriver.DriverInfo;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComboBox;
import java.awt.event.ItemEvent;

/**
 * 驱动列表下拉监听
 */
public class DBTypeBoxListener extends AbstractItemListener {
    public DBTypeBoxListener(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void afterSelect(ItemEvent e) {

        DevConfigDialog dialog = (DevConfigDialog) getDialog();
        String selected = (String) dialog.getComponent(JComboBox.class, "dbTypeBox").getSelectedItem();

        if (StringUtils.isNotBlank(selected)) {
            DriverInfo[] infos = dialog.getDatabaseDriverInfoMap().get(selected).getDatabase();
            DataSourceUtil.fillCombo(dialog.getComponent(JComboBox.class, "driverBox"), infos, dialog);
        }
    }
}
