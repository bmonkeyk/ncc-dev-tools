package com.yingling.extensions.listener.action;

import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;
import com.yingling.extensions.util.DataSourceUtil;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;

import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * 设为基准库监听
 */
public class SetBaseDataBaseAction extends AbstractButtonAction {
    public SetBaseDataBaseAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        Map<String, DataSourceMeta> map = dlg.getDataSourceMetaMap();
        for (String key : map.keySet()) {
            DataSourceMeta meta = map.get(key);
            if (key.equals(dlg.getDataSourceMetaBox().getSelectedItem())) {
                meta.setBase(true);
                dlg.getBaseCheck().setSelected(true);
                DataSourceUtil.saveDesignDataSourceMeta(dlg);
            } else {
                meta.setBase(false);
            }
        }
    }
}
