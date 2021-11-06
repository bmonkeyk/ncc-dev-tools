package com.yingling.devconfig.action.button.datasource;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;

import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.util.Map;


/**
 * 设为基准库
 */
public class SetBaseDataSourceAction extends AbstractButtonAction {

    public SetBaseDataSourceAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {
        DevConfigDialog dialog = (DevConfigDialog) getDialog();
        Map<String, DataSourceMeta> map = dialog.getDataSourceMetaMap();
        DataSourceMeta currMeta = dialog.getCurrMeta();
        for (String key : map.keySet()) {
            DataSourceMeta meta = map.get(key);
            if (key.equals(currMeta.getDataSourceName())) {
                meta.setBase(true);
                dialog.getComponent(JCheckBox.class, "baseChx").setSelected(true);
                dialog.getComponent(JCheckBox.class, "devChx").setSelected(false);
            } else {
                meta.setBase(false);
                dialog.getComponent(JCheckBox.class, "baseChx").setSelected(false);
            }
        }
        DataSourceUtil.saveDesignDataSourceMeta(dialog);
    }
}
