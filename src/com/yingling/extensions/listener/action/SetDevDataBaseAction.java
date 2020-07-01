package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;

import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * 按钮事件
 * 设为开发库
 */
public class SetDevDataBaseAction extends AbstractButtonAction {
    public SetDevDataBaseAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        String dsname = (String) dlg.getDatabaseDriverInfoBox().getSelectedItem();
        if (!"".equals(dsname) && !"design".equals(dsname)) {
            try {
                dlg.setDirty(true);
                Map<String, DataSourceMeta> dataSourceMetaMap = dlg.getDataSourceMetaMap();
                DataSourceMeta meta = (DataSourceMeta) dataSourceMetaMap.get(dsname).clone();
                meta.setDataSourceName("design");
                dataSourceMetaMap.put(meta.getDataSourceName(), meta);
                dlg.getDatabaseDriverInfoBox().setSelectedIndex(0);
            } catch (CloneNotSupportedException ex) {
            }
        }
    }
}
