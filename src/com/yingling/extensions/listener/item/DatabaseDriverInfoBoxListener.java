package com.yingling.extensions.listener.item;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractItemListener;
import com.yingling.extensions.util.DataSourceUtil;
import nc.uap.plugin.studio.ui.preference.dbdriver.DriverInfo;
import org.apache.commons.lang.StringUtils;

import java.awt.event.ItemEvent;

/**
 * 数据库类型下拉监听
 */
public class DatabaseDriverInfoBoxListener extends AbstractItemListener {
    public DatabaseDriverInfoBoxListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void afterEdit(ItemEvent event, NccDevSettingDlg dlg) {
        String selected = (String) dlg.getDatabaseDriverInfoBox().getSelectedItem();
        if (StringUtils.isNotBlank(selected)) {
            DriverInfo[] infos = dlg.getDatabaseDriverInfoMap().get(selected).getDatabase();
            DataSourceUtil.fillCombo(dlg.getDriverInfoBox(), infos, dlg);
        }
    }
}
