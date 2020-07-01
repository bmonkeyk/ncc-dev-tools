package com.yingling.extensions.listener.item;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractItemListener;
import com.yingling.extensions.util.DataSourceUtil;

import java.awt.event.ItemEvent;

/**
 * 驱动列表编辑后事件
 */
public class DataSourceMetaBoxListener extends AbstractItemListener {
    public DataSourceMetaBoxListener(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void afterEdit(ItemEvent event, NccDevSettingDlg dlg) {
        String dsname = (String) dlg.getDataSourceMetaBox().getSelectedItem();
        if (!"".equals(dsname)) {
            dlg.setSwitchFlag(true);
            DataSourceUtil.syncCurrDataSourceMeta(dlg);
            dlg.setCurrmeta(dlg.getDataSourceMetaMap().get(dsname));
            DataSourceUtil.fillDataSourceMeta(dlg);
            dlg.setSwitchFlag(false);
        }
    }
}
