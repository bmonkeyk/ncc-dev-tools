package com.yingling.devconfig.action.button.datasource;

import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.script.studio.ui.preference.prop.DataSourceMeta;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * 设为开发库
 */
public class SetDevDataSourceAction extends AbstractButtonAction {
    public SetDevDataSourceAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) {

        DevConfigDialog dialog = (DevConfigDialog) getDialog();
        String dsname = (String) dialog.getComponent(JComboBox.class, "dbBox").getSelectedItem();
        int index = dialog.getComponent(JComboBox.class, "dbBox").getSelectedIndex();
        int count = dialog.getComponent(JComboBox.class, "dbBox").getItemCount();
        for (int i = 0; i < count; i++) {
            if ("design".equals(dialog.getComponent(JComboBox.class, "dbBox").getItemAt(i))) {
                return;
            }
        }
        if (StringUtils.isNotBlank(dsname)) {
            try {
                Map<String, DataSourceMeta> dataSourceMetaMap = dialog.getDataSourceMetaMap();
                DataSourceMeta meta = (DataSourceMeta) dataSourceMetaMap.get(dsname).clone();
                meta.setDataSourceName("design");
                meta.setBase(false);
                dataSourceMetaMap.put(meta.getDataSourceName(), meta);
                dialog.getComponent(JComboBox.class, "dbBox").insertItemAt("design", index + 1);
                dialog.getComponent(JComboBox.class, "dbBox").setSelectedIndex(index + 1);
                DataSourceUtil.saveDesignDataSourceMeta(dialog);
            } catch (Exception ex) {

            }
        }
    }
}
