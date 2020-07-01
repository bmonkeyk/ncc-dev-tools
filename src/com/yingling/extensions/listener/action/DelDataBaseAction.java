package com.yingling.extensions.listener.action;

import com.intellij.openapi.ui.Messages;
import com.yingling.extensions.component.NccDevSettingDlg;
import com.yingling.extensions.listener.AbstractButtonAction;

import java.awt.event.ActionEvent;

/**
 * 删除数据源
 */
public class DelDataBaseAction extends AbstractButtonAction {
    public DelDataBaseAction(NccDevSettingDlg dlg) {
        super(dlg);
    }

    @Override
    protected void doAction(ActionEvent event, NccDevSettingDlg dlg) {
        String dsName = (String) dlg.getDataSourceMetaBox().getSelectedItem();
        if (!"".equals(dsName) && !"design".equals(dsName)) {
            dlg.setDirty(true);
            dlg.getDataSourceMetaMap().remove(dsName);
            dlg.getDataSourceMetaBox().removeItem(dsName);
            dlg.getDataSourceMetaBox().setSelectedIndex(0);
        } else {
            Messages.showMessageDialog("Design datasource can not be delete.", "tips", Messages.getInformationIcon());
        }
    }
}
