package com.yingling.extensions.listener.item;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractItemListener;
import com.yingling.extensions.util.DataSourceUtil;
import nc.uap.plugin.studio.ui.preference.dbdriver.DriverInfo;
import nc.uap.plugin.studio.ui.preference.prop.ToolUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.event.ItemEvent;

/**
 * 驱动类型下拉监听
 */
public class DriverInfoBoxListener extends AbstractItemListener {
    public DriverInfoBoxListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void afterEdit(ItemEvent event, NccDevSettingDlg dlg) {
        String selected = (String) dlg.getDriverInfoBox().getSelectedItem();
        if (StringUtils.isNotBlank(selected)) {
            DriverInfo info = dlg.getDriverInfoMap().get(selected);
            dlg.getHostText().setEnabled(ToolUtils.isJDBCUrl(info
                    .getDriverUrl()));
            dlg.getPortText().setEnabled(ToolUtils.isJDBCUrl(info
                    .getDriverUrl()));
            DataSourceUtil.fillDBConnByInfo(dlg, info.getDriverUrl());
        }
    }
}
